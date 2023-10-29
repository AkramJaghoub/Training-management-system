package ju.example.training_management_system.service;

import ju.example.training_management_system.model.PasswordResetToken;
import ju.example.training_management_system.model.User;
import ju.example.training_management_system.repository.TokenRepository;
import ju.example.training_management_system.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.cglib.core.Local;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@AllArgsConstructor
public class ResetPasswordService {

    private final JavaMailSender emailSender;
    private final TokenRepository tokenRepository;
    private final UserRepository userRepository;

    public void sendEmail(String email) {
        User user = userRepository.findByEmail(email);
        System.out.println(user.getEmail());
        String resetLink = generateResetToken(user);
        System.out.println(resetLink);
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("akram.jaghoub51@gmail.com");
        message.setTo(user.getEmail()); // Send the email to the user's email address
        message.setSubject("Password Reset Link");
        message.setText(resetLink);
        emailSender.send(message);
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

    public boolean isTokenExpired(String token){
        PasswordResetToken resetToken = tokenRepository.findByToken(token);
        return resetToken != null && this.hasExpired(resetToken.getExpiryDateTime());
    }

    private boolean hasExpired(LocalDateTime expiryDateTime) {
        LocalDateTime currentDateTime = LocalDateTime.now();
        return expiryDateTime.isAfter(currentDateTime); // if the expiry is before the current then it is expired (ex: 11:10 - 11:15) then it is expired
    }

    public void resetPassword(String email, String newPassword, String token) {
        User user = userRepository.findByEmail(email);
        if (user != null){
            user.setPassword(newPassword);
            userRepository.save(user);
        }
        PasswordResetToken resetToken = tokenRepository.findByToken(token);
        tokenRepository.delete(resetToken);
    }

    public String getEmailFromToken(String token) {
        PasswordResetToken resetToken = tokenRepository.findByToken(token);
        if (resetToken != null && resetToken.getUser() != null) {
            return resetToken.getUser().getEmail();
        }
        return "no token found";
    }
}
