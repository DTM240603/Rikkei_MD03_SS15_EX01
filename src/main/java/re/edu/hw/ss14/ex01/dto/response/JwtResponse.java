package re.edu.hw.ss14.ex01.dto.response;

public record JwtResponse(
        String accessToken,
        String refreshToken,
        String tokenType,
        long expiresIn
) {
}
