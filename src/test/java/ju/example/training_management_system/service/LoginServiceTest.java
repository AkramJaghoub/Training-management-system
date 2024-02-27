package ju.example.training_management_system.service;

import static ju.example.training_management_system.model.Role.STUDENT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import jakarta.servlet.http.HttpSession;
import ju.example.training_management_system.dto.LoginDto;
import ju.example.training_management_system.model.ApiResponse;
import ju.example.training_management_system.service.login.AuthenticationService;
// import ju.example.training_management_system.service.login.FactorAuthenticationService;
import ju.example.training_management_system.service.login.LoginService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class LoginServiceTest {

  private LoginService loginService;
  private UserService userService;
  private AuthenticationService authenticationService;
  //  private FactorAuthenticationService factorAuthenticationService;
  private HttpSession httpSession;

  @BeforeEach
  void setUp() {
    userService = mock(UserService.class);
    authenticationService = mock(AuthenticationService.class);
    //    factorAuthenticationService = mock(FactorAuthenticationService.class);
    httpSession = mock(HttpSession.class);
    loginService = new LoginService(authenticationService, userService);
  }

  @Test
  void should_authenticate_user_as_admin() {
    LoginDto loginDto = getAdminCredentials();
    String email = loginDto.getEmail();
    String password = loginDto.getPassword();

    when(authenticationService.isAdmin(email, password)).thenReturn(true);

    ApiResponse response = loginService.loginUser(loginDto, httpSession);

    verify(authenticationService).isAdmin(email, password);
    verify(authenticationService, never()).isValidUser(email, password);

    assertEquals(HttpStatus.OK, response.getStatus());
  }

  @Test
  void should_authenticate_user_as_student_or_company() {
    LoginDto loginDto = getUserCredentials();
    String email = loginDto.getEmail();
    String password = loginDto.getPassword();

    when(authenticationService.isValidUser(email, password)).thenReturn(true);
    when(userService.getUserRole(email)).thenReturn(STUDENT);
    //    when(factorAuthenticationService.isTokenPersisted(email)).thenReturn(true);

    ApiResponse response = loginService.loginUser(loginDto, httpSession);

    verify(authenticationService).isValidUser(email, password);

    assertEquals(HttpStatus.OK, response.getStatus());
  }

  @Test
  void should_reject_invalid_user() {
    LoginDto loginDto = getUserCredentials();
    String email = loginDto.getEmail();
    String password = loginDto.getPassword();

    when(authenticationService.isValidUser(email, password)).thenReturn(false);
    when(authenticationService.isAdmin(email, password)).thenReturn(false);

    ApiResponse response = loginService.loginUser(loginDto, httpSession);

    assertEquals(HttpStatus.UNAUTHORIZED, response.getStatus());
  }

  private LoginDto getAdminCredentials() {
    LoginDto loginDto = new LoginDto();
    loginDto.setEmail("root");
    loginDto.setPassword("21232619boom");
    return loginDto;
  }

  private LoginDto getUserCredentials() {
    LoginDto loginDto = new LoginDto();
    loginDto.setEmail("test@gmail.com");
    loginDto.setPassword("test123");
    return loginDto;
  }
}
