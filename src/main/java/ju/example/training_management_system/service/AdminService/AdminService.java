package ju.example.training_management_system.service.AdminService;

import jakarta.transaction.Transactional;
import ju.example.training_management_system.exception.UserNotFoundException;
import ju.example.training_management_system.model.ApiResponse;
import ju.example.training_management_system.model.company.advertisement.Advertisement;
import ju.example.training_management_system.model.users.Company;
import ju.example.training_management_system.model.users.Role;
import ju.example.training_management_system.model.users.Student;
import ju.example.training_management_system.model.users.User;
import ju.example.training_management_system.repository.AdvertisementRepository;
import ju.example.training_management_system.repository.users.CompanyRepository;
import ju.example.training_management_system.repository.users.StudentRepository;
import ju.example.training_management_system.repository.users.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import static ju.example.training_management_system.util.Utils.decompressImage;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final AdvertisementRepository advertisementRepository;
    private final StudentRepository studentRepository;
    private final CompanyRepository companyRepository;

    public void setUpAdminDashboardPage(Model model) {
        setUpFields(model);
    }

    public void setUpUserListPage(Model model) {
        setUpFields(model);
    }

    private void setUpFields(Model model) {
        LocalDateTime now = LocalDateTime.now();

        List<User> users = userRepository.findAll();
        List<Advertisement> advertisements = advertisementRepository.findAll();

        Map<Long, String> userImages = new HashMap<>();
        List<User> newUsers = users.stream()
                .filter(user -> user.getJoinDate() != null &&
                        ChronoUnit.HOURS.between(user.getJoinDate(), now) < 24)
                .peek(user -> {
                    String base64Image = null;
                    if (user instanceof Company && ((Company) user).getImage() != null) {
                        byte[] decompressedImage = decompressImage(((Company) user).getImage());
                        base64Image = Base64.getEncoder().encodeToString(decompressedImage);
                    } else if (user instanceof Student && ((Student) user).getImage() != null) {
                        byte[] decompressedImage = decompressImage(((Student) user).getImage());
                        base64Image = Base64.getEncoder().encodeToString(decompressedImage);
                    }
                    userImages.put(user.getId(), base64Image);
                })
                .toList();

        long numOfCompanies = users.stream().
                filter(user -> user.getRole() == Role.COMPANY).
                count();

        long numOfStudents = users.stream().
                filter(user -> user.getRole() == Role.STUDENT).
                count();

        long newAdsCount = advertisements.stream()
                .filter(ad -> ad.getPostDate() != null &&
                        ChronoUnit.HOURS.between(ad.getPostDate(), now) < 24)
                .count();

        model.addAttribute("usersCount", users.size());
        model.addAttribute("companiesCount", numOfCompanies);
        model.addAttribute("studentsCount", numOfStudents);
        model.addAttribute("advertisementsCount", advertisements.size());
        model.addAttribute("users", users);
        model.addAttribute("userImages", userImages);
        model.addAttribute("newUsersCount", newUsers.size());
        model.addAttribute("newAdvertisementsCount", newAdsCount);
    }

    public void setUpAdsListPage(Model model) {
        List<Advertisement> advertisements = advertisementRepository.findAll()
                .stream()
                .sorted(Comparator.comparing(Advertisement::getPostDate).reversed())
                .toList();
        model.addAttribute("advertisements", advertisements);
    }

    @Transactional
    public ApiResponse deleteUser(long userId) {
        try {
            Optional<User> usersOpt = userRepository.findById(userId);
            if (usersOpt.isEmpty()) {
                throw new UserNotFoundException("User with id [" + userId + "] was not found");
            }

            User user = usersOpt.get();

            if (user.getRole() == Role.STUDENT) {
                studentRepository.deleteById(userId);
            } else if (user.getRole() == Role.COMPANY) {
                advertisementRepository.deleteByCompanyId(userId);
                companyRepository.deleteById(userId);
            }

            userRepository.deleteById(userId);

            return new ApiResponse("User with id [" + userId + "] was deleted successfully", HttpStatus.OK);

        } catch (UserNotFoundException ex) {
            return new ApiResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}