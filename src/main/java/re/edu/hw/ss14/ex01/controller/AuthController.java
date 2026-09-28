package re.edu.hw.ss14.ex01.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import re.edu.hw.ss14.ex01.dto.request.FormLogin;
import re.edu.hw.ss14.ex01.dto.request.FormRegister;
import re.edu.hw.ss14.ex01.dto.response.JwtResponse;
import re.edu.hw.ss14.ex01.service.IAuthService;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final IAuthService authService;

    @PostMapping("/login")
    public JwtResponse login(@Valid @RequestBody FormLogin request) {
        return authService.login(request);
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public String register(@Valid @RequestBody FormRegister request) {
        return authService.register(request);
    }
}
