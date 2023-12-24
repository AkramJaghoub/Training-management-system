package ju.example.training_management_system.service;

import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import ju.example.training_management_system.exception.UserNotFoundException;
import ju.example.training_management_system.model.TwoFactorAuthentication;
import ju.example.training_management_system.model.users.Company;
import ju.example.training_management_system.model.users.Student;
import ju.example.training_management_system.model.users.User;
import ju.example.training_management_system.repository.FactorAuthenticationRepository;
import ju.example.training_management_system.repository.users.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import static ju.example.training_management_system.model.TokenStatus.ACTIVE;
import static ju.example.training_management_system.model.TokenStatus.EXPIRED;

@Service
@RequiredArgsConstructor
public class FactorAuthenticationService {

    private final UserRepository userRepository;
    private final JavaMailSender emailSender;
    private final FactorAuthenticationRepository factorAuthenticationRepository;
    private final SpringTemplateEngine templateEngine;

    public boolean isTokenPersisted(String email) {
        User user = userRepository.findByEmail(email);
        List<TwoFactorAuthentication> authenticationTokens = factorAuthenticationRepository.findAllByUserId(user.getId());

        TwoFactorAuthentication latestToken = authenticationTokens.stream()
                .max(Comparator.comparing(TwoFactorAuthentication::getExpiryTime))
                .orElse(null);

        return latestToken != null && !this.hasExpired(latestToken.getPersistenceTime());
    }

    @Transactional
    public void process2FA(String email) {
        User user = userRepository.findByEmail(email);
        List<TwoFactorAuthentication> authenticationTokens = factorAuthenticationRepository.findAllByUserId(user.getId());

        TwoFactorAuthentication latestToken = authenticationTokens.stream()
                .max(Comparator.comparing(TwoFactorAuthentication::getExpiryTime))
                .orElse(null);

        if (latestToken != null) {
            latestToken.setTokenExpiryStatus(EXPIRED);
            factorAuthenticationRepository.save(latestToken);
        }

        authenticationTokens.stream()
                .filter(token -> token.getId() != latestToken.getId())
                .forEach(factorAuthenticationRepository::delete);
    }

    private String generate2FAToken(User user) {
        int number = new Random().nextInt(90000) + 10000;
        String authenticationToken = String.valueOf(number);

        LocalDateTime currentDateTime = LocalDateTime.now();
        LocalDateTime expiryDateTime = currentDateTime.plusMinutes(3);
        LocalDateTime persistenceTime = currentDateTime.plusDays(15);

        TwoFactorAuthentication factorAuthentication = new TwoFactorAuthentication();
        factorAuthentication.setUser(user);
        factorAuthentication.setToken(authenticationToken);
        factorAuthentication.setPersistenceTime(persistenceTime);
        factorAuthentication.setExpiryTime(expiryDateTime);
        factorAuthentication.setTokenExpiryStatus(ACTIVE);
        factorAuthenticationRepository.save(factorAuthentication);
        return authenticationToken;
    }

    public boolean isTokenExpired(String email) {
        User user = userRepository.findByEmail(email);
        List<TwoFactorAuthentication> authenticationTokens = factorAuthenticationRepository.findAllByUserId(user.getId());

        TwoFactorAuthentication latestToken = authenticationTokens.stream()
                .max(Comparator.comparing(TwoFactorAuthentication::getExpiryTime))
                .orElse(null);

        if (latestToken != null && this.hasExpired(latestToken.getExpiryTime())) {
            latestToken.setTokenExpiryStatus(EXPIRED);
            factorAuthenticationRepository.save(latestToken);
            return false;
        }
        return true;
    }

    private boolean hasExpired(LocalDateTime localDateTime) {
        LocalDateTime currentDateTime = LocalDateTime.now();
        return !localDateTime.isAfter(currentDateTime);
    }

    public TwoFactorAuthentication getTokenByEmail(String email) {
        User user = userRepository.findByEmail(email);
        List<TwoFactorAuthentication> authenticationTokens = factorAuthenticationRepository.findAllByUserId(user.getId());

        return authenticationTokens.stream()
                .max(Comparator.comparing(TwoFactorAuthentication::getPersistenceTime))
                .orElse(null);
    }

    public void sendEmail(String email) {
        User user = userRepository.findByEmail(email);
        String token = generate2FAToken(user);

        try {
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name());

            helper.setFrom("akram.jaghoub51@gmail.com");
            helper.setTo(user.getEmail());
            helper.setSubject("Authentication Code");

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
            context.setVariable("token", token);

            String content = templateEngine.process("2FA-template", context);
            helper.setText(content, true);

            emailSender.send(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}