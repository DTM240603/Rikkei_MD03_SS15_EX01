package re.edu.hw.ss14.ex01.config.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import re.edu.hw.ss14.ex01.config.JwtAuthenticationEntryPoint;
import re.edu.hw.ss14.ex01.config.UserDetailsServiceCustom;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final UserDetailsServiceCustom userDetailsService;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String token = getTokenFromRequest(request);

        try {
            if (token != null
                    && SecurityContextHolder.getContext().getAuthentication() == null) {
                String email = jwtProvider.extractEmailFromAccessToken(token);
                UserDetails userDetails = userDetailsService.loadUserByUsername(email);
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );
                authentication.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (ExpiredJwtException exception) {
            handleAuthenticationFailure(request, response, exception);
            return;
        } catch (MalformedJwtException exception) {
            handleAuthenticationFailure(request, response, exception);
            return;
        } catch (JwtException | IllegalArgumentException exception) {
            handleAuthenticationFailure(request, response, exception);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private void handleAuthenticationFailure(
            HttpServletRequest request,
            HttpServletResponse response,
            RuntimeException exception
    ) throws IOException {
        SecurityContextHolder.clearContext();
        jwtAuthenticationEntryPoint.commence(
                request,
                response,
                new BadCredentialsException("Invalid JWT", exception)
        );
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        if (authorization != null && authorization.startsWith("Bearer ")) {
            String token = authorization.substring(7).trim();
            return token.isEmpty() ? null : token;
        }
        return null;
    }
}
