package ju.example.training_management_system.service;

import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import ju.example.training_management_system.exception.UserNotFoundException;
import ju.example.training_management_system.model.PasswordResetToken;
import ju.example.training_management_system.model.users.Company;
import ju.example.training_management_system.model.users.Student;
import ju.example.training_management_system.model.users.User;
import ju.example.training_management_system.repository.TokenRepository;
import ju.example.training_management_system.repository.users.UserRepository;
import ju.example.training_management_system.util.PasswordHashingUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ResetPasswordService {

    private final JavaMailSender emailSender;
    private final TokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final SpringTemplateEngine templateEngine;

    public void sendEmail(String email) {
        User user = userRepository.findByEmail(email);
        String resetLink = generateResetToken(user);

        try {
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name());

            helper.setFrom("akram.jaghoub51@gmail.com");
            helper.setTo(user.getEmail());
            helper.setSubject("Password Reset Link");

            Context context = new Context();

            String name = null;
            User existingUser = userRepository.findByEmail(email);

            if (existingUser == null) {
                throw new UserNotFoundException("User with email " + email + " wasn't found");
            }

            if ((existingUser instanceof Company company)) {
                name = company.getCompanyName();
            }

            if ((existingUser instanceof Student student)) {
                name = student.getFirstName() + " " + student.getLastName();
            }

            context.setVariable("name", name);
            context.setVariable("resetLink", resetLink);

            String content = templateEngine.process("reset-password-template", context);
            helper.setText(content, true);

            emailSender.send(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private String generateResetToken(User user) {
        UUID uuid = UUID.randomUUID();
        LocalDateTime currentDateTime = LocalDateTime.now();
        LocalDateTime expiryDateTime = currentDateTime.plusMinutes(30);
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setUser(user);
        resetToken.setToken(uuid.toString());
        resetToken.setExpiryDateTime(expiryDateTime);
        PasswordResetToken token = tokenRepository.save(resetToken);
        return "http://localhost:8080/reset-password?token=" + token.getToken();
    }

    public boolean isTokenExpired(String token) {
        PasswordResetToken resetToken = tokenRepository.findByToken(token);
        if (this.hasExpired(resetToken.getExpiryDateTime()) && token != null) {
            tokenRepository.delete(resetToken);
            return true;
        }
        return false;
    }

    private boolean hasExpired(LocalDateTime expiryDateTime) {
        LocalDateTime currentDateTime = LocalDateTime.now();
        return expiryDateTime.isAfter(currentDateTime);
    }

    @Transactional
    public void resetPassword(String email, String newPassword) {
        User user = userRepository.findByEmail(email);
        if (user != null) {
            String hashedPassword = PasswordHashingUtil.hashPassword(newPassword);
            user.setPassword(hashedPassword);
            userRepository.save(user);
        }
        tokenRepository.deleteAllByUserId(user.getId());
    }

    public String getEmailFromToken(String token) {
        PasswordResetToken resetToken = tokenRepository.findByToken(token);
        if (resetToken != null && resetToken.getUser() != null) {
            return resetToken.getUser().getEmail();
        }
        return "no token found";
    }
}
