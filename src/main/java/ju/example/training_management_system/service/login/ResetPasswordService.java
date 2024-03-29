package ju.example.training_management_system.service.login;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.UUID;
import ju.example.training_management_system.entity.PasswordResetTokenEntity;
import ju.example.training_management_system.entity.users.CompanyEntity;
import ju.example.training_management_system.entity.users.StudentEntity;
import ju.example.training_management_system.entity.users.UserEntity;
import ju.example.training_management_system.exception.PasswordReusedException;
import ju.example.training_management_system.exception.UserNotFoundException;
import ju.example.training_management_system.model.ApiResponse;
import ju.example.training_management_system.repository.TokenRepository;
import ju.example.training_management_system.repository.users.UserRepository;
import ju.example.training_management_system.util.PasswordHashingUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

@Service
@RequiredArgsConstructor
public class ResetPasswordService {

  private final JavaMailSender emailSender;
  private final TokenRepository tokenRepository;
  private final UserRepository userRepository;
  private final SpringTemplateEngine templateEngine;

  public ApiResponse sendEmail(String email) {
    try {
      UserEntity user = userRepository.findByEmail(email);
      if (isNull(user)) {
        throw new UserNotFoundException("Email doesn't exist");
      }

      PasswordResetTokenEntity existingToken = tokenRepository.findByUserId(user.getId());

      String resetLink = determineResetLink(user, existingToken);

      String name = null;

      if ((user instanceof CompanyEntity company)) {
        name = company.getCompanyName();
      }

      if ((user instanceof StudentEntity student)) {
        name = student.getFirstName() + " " + student.getLastName();
      }

      MimeMessage message = emailSender.createMimeMessage();
      MimeMessageHelper helper =
          new MimeMessageHelper(
              message,
              MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
              StandardCharsets.UTF_8.name());

      helper.setFrom("teamterospprt@gmail.com");
      helper.setTo(user.getEmail());
      helper.setSubject("Password Reset Link");

      Context context = new Context();
      context.setVariable("name", name);
      context.setVariable("resetLink", resetLink);

      String content = templateEngine.process("reset-password-template", context);
      helper.setText(content, true);

      emailSender.send(message);
      return new ApiResponse(
          "An email was successfully sent to [" + user.getEmail() + "]", HttpStatus.OK);
    } catch (UserNotFoundException | MessagingException ex) {
      return new ApiResponse(ex.getMessage(), BAD_REQUEST);
    }
  }

  private String determineResetLink(UserEntity user, PasswordResetTokenEntity existingToken) {
    if (nonNull(existingToken) && !this.hasExpired(existingToken.getExpiryDateTime())) {
      return "http://localhost:8080/reset-password?token=" + existingToken.getToken();
    } else {
      if (nonNull(existingToken)) {
        tokenRepository.delete(existingToken);
      }
      return generateResetToken(user);
    }
  }

  private String generateResetToken(UserEntity user) {
    UUID uuid = UUID.randomUUID();
    LocalDateTime currentDateTime = LocalDateTime.now();
    LocalDateTime expiryDateTime = currentDateTime.plusMinutes(30);
    PasswordResetTokenEntity resetToken = new PasswordResetTokenEntity();
    resetToken.setUser(user);
    resetToken.setToken(uuid.toString());
    resetToken.setExpiryDateTime(expiryDateTime);
    tokenRepository.save(resetToken);
    return "http://localhost:8080/reset-password?token=" + resetToken.getToken();
  }

  public boolean isTokenExpired(String token) {
    try {
      PasswordResetTokenEntity resetToken = tokenRepository.findByToken(token);
      if (this.hasExpired(resetToken.getExpiryDateTime()) && nonNull(token)) {
        tokenRepository.delete(resetToken);
        return true;
      }
    } catch (NullPointerException ex) {
      return true;
    }
    return false;
  }

  private boolean hasExpired(LocalDateTime expiryDateTime) {
    LocalDateTime currentDateTime = LocalDateTime.now();
    return expiryDateTime.isBefore(currentDateTime);
  }

  @Transactional
  public ApiResponse resetPassword(String email, String newPassword) {
    try {
      UserEntity user = userRepository.findByEmail(email);
      if (isNull(user)) {
        throw new UserNotFoundException("UserEntity with email [" + email + "] was not found");
      }
      String hashedPassword = PasswordHashingUtil.hashPassword(newPassword);
      if (hashedPassword.equals(user.getPassword())) {
        throw new PasswordReusedException("Your new password matches the current password!");
      }
      user.setPassword(hashedPassword);
      userRepository.save(user);
      tokenRepository.deleteByUserId(user.getId());
      return new ApiResponse(
          "Password for user with email [" + email + "] was changed successfully ", OK);
    } catch (UserNotFoundException | PasswordReusedException ex) {
      return new ApiResponse(ex.getMessage(), BAD_REQUEST);
    }
  }

  public String getEmailFromToken(String token) {
    PasswordResetTokenEntity resetToken = tokenRepository.findByToken(token);
    if (nonNull(resetToken) && nonNull(resetToken.getUser())) {
      return resetToken.getUser().getEmail();
    }
    return "no token found";
  }
}
