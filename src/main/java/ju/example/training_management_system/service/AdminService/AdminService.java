package ju.example.training_management_system.service.AdminService;

import ju.example.training_management_system.model.users.Role;
import ju.example.training_management_system.model.users.User;
import ju.example.training_management_system.repository.AdvertisementRepository;
import ju.example.training_management_system.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final AdvertisementRepository advertisementRepository;

    public void setUpAdminDashboard(Model model) {

        List<User> users = userRepository.findAll();

        long numOfCompanies = users.stream().
                filter(user -> user.getRole() == Role.COMPANY).
                count();

        long numOfStudents = users.stream().
                filter(user -> user.getRole() == Role.STUDENT).
                count();

        long numOfAdvertisements = advertisementRepository.findAll().size();

        LocalDateTime now = LocalDateTime.now();
        long newUsersCount = users.stream() // users who joined in the last 24 hours
                .filter(user -> user.getJoinDate() != null &&
                        ChronoUnit.HOURS.between(user.getJoinDate(), now) < 24)
                .count();

        model.addAttribute("usersCount", users.size());
        model.addAttribute("companiesCount", numOfCompanies);
        model.addAttribute("studentsCount", numOfStudents);
        model.addAttribute("advertisementsCount", numOfAdvertisements);
        model.addAttribute("newUsersCount", newUsersCount);
    }
}
