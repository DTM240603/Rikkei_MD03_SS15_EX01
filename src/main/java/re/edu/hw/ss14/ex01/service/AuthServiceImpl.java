package re.edu.hw.ss14.ex01.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import re.edu.hw.ss14.ex01.config.jwt.JwtProvider;
import re.edu.hw.ss14.ex01.dto.request.FormLogin;
import re.edu.hw.ss14.ex01.dto.request.FormRegister;
import re.edu.hw.ss14.ex01.dto.response.JwtResponse;
import re.edu.hw.ss14.ex01.entity.User;
import re.edu.hw.ss14.ex01.repository.UserRepository;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements IAuthService {

    private final JwtProvider jwtProvider;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public JwtResponse login(FormLogin request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String accessToken = jwtProvider.generateAccessToken(userDetails);
        String refreshToken = jwtProvider.generateRefreshToken(userDetails);

        return new JwtResponse(
                accessToken,
                refreshToken,
                "Bearer",
                jwtProvider.getAccessTokenExpiration() / 1000
        );
    }

    @Override
    @Transactional
    public String register(FormRegister request) {
        String email = request.getEmail().trim().toLowerCase(Locale.ROOT);
        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email đã tồn tại");
        }

        User user = new User();
        user.setPhone(request.getPhone().trim());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(email);
        user.setRole("ROLE_USER");
        userRepository.save(user);

        return "Đăng ký thành công";
    }
}
