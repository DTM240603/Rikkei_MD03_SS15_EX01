package re.edu.hw.ss14.ex01.service;

import re.edu.hw.ss14.ex01.dto.request.FormLogin;
import re.edu.hw.ss14.ex01.dto.request.FormRegister;
import re.edu.hw.ss14.ex01.dto.response.JwtResponse;

public interface IAuthService {

    JwtResponse login(FormLogin request);

    String register(FormRegister request);
}
