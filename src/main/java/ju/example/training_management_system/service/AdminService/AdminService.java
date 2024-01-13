package ju.example.training_management_system.service.AdminService;

import jakarta.transaction.Transactional;
import ju.example.training_management_system.exception.AdDoesNotExistException;
import ju.example.training_management_system.exception.FeedbackDoesNotExistException;
import ju.example.training_management_system.exception.UserNotFoundException;
import ju.example.training_management_system.model.ApiResponse;
import ju.example.training_management_system.model.Feedback;
import ju.example.training_management_system.model.PostStatus;
import ju.example.training_management_system.model.company.advertisement.Advertisement;
import ju.example.training_management_system.model.users.Company;
import ju.example.training_management_system.model.users.Role;
import ju.example.training_management_system.model.users.Student;
import ju.example.training_management_system.model.users.User;
import ju.example.training_management_system.repository.AdvertisementRepository;
import ju.example.training_management_system.repository.FeedbackRepository;
import ju.example.training_management_system.repository.users.CompanyRepository;
import ju.example.training_management_system.repository.users.StudentRepository;
import ju.example.training_management_system.repository.users.UserRepository;
import ju.example.training_management_system.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;
import static ju.example.training_management_system.model.PostStatus.APPROVED;
import static ju.example.training_management_system.model.PostStatus.PENDING;
import static ju.example.training_management_system.util.Utils.*;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final AdvertisementRepository advertisementRepository;
    private final StudentRepository studentRepository;
    private final CompanyRepository companyRepository;
    private final NotificationService notificationService;
    private final FeedbackRepository feedbackRepository;

    public void setUpAdminDashboardPage(Model model) {
        setUpFields(model);
    }

    public void setUpUserListPage(Model model) {
        setUpFields(model);
    }

    private void setUpFields(Model model) {
        List<User> users = getUsersByJoinDate();

        List<Advertisement> advertisements = advertisementRepository.findAll();

        Map<Long, String> userImages = new HashMap<>();
        List<User> newUsers = getNewUsers(users, userImages);

        long numOfCompanies = users.stream().
                filter(user -> user.getRole() == Role.COMPANY).
                count();

        long numOfStudents = users.stream().
                filter(user -> user.getRole() == Role.STUDENT).
                count();

        long newAdsCount = advertisements.stream()
                .filter(ad -> ad.getPostDate() != null &&
                        ChronoUnit.HOURS.between(ad.getPostDate(), LocalDateTime.now()) < 24)
                .count();

        model.addAttribute("usersCount", users.size());
        model.addAttribute("companiesCount", numOfCompanies);
        model.addAttribute("studentsCount", numOfStudents);
        model.addAttribute("advertisementsCount", advertisements.size());
        model.addAttribute("newUsers", newUsers);
        model.addAttribute("users", users);
        model.addAttribute("userImages", userImages);
        model.addAttribute("newUsersCount", newUsers.size());
        model.addAttribute("newAdvertisementsCount", newAdsCount);
    }

    private List<User> getUsersByJoinDate() {
        return userRepository.findAll().stream()
                .sorted(Comparator.comparing(User::getJoinDate).reversed())
                .collect(Collectors.toList());
    }

    private Optional<String> getUserImageBase64(User user) {
        if (nonNull(user.getImage())) {
            byte[] decompressedImage = decompressImage(user.getImage());
            String base64Image = Base64.getEncoder().encodeToString(decompressedImage);
            return Optional.of(base64Image);
        }
        return Optional.empty();
    }

    private List<User> getNewUsers(List<User> users, Map<Long, String> userImages) {
        return users.stream()
                .filter(user -> user.getJoinDate() != null &&
                        ChronoUnit.HOURS.between(user.getJoinDate(), LocalDateTime.now()) < 24)
                .peek(user -> getUserImageBase64(user)
                        .ifPresent(base64Image -> userImages.put(user.getId(), base64Image)))
                .toList();
    }

    public void setUpAdsListPage(Model model) {
        List<Advertisement> advertisements = getAdvertisementsByPostDate();
        model.addAttribute("advertisements", advertisements);
    }

    public List<Advertisement> getAdvertisementsByPostDate(){
        return advertisementRepository.findAll()
                .stream()
                .sorted(Comparator.comparing(Advertisement::getPostDate).reversed())
                .toList();
    }

    public void setUpStudentsFeedbackPage(Model model) {
        List<Feedback> feedbackList = getFeedbackListByPostDate();

        Map<Long, String> studentImages = new HashMap<>();
        for (Feedback feedback : feedbackList) {
            Student student = feedback.getStudent();
            if (nonNull(student)) {
                getUserImageBase64(student).ifPresent(base64Image -> studentImages.put(student.getId(), base64Image));
            }
        }

        model.addAttribute("feedbackList", feedbackList);
        model.addAttribute("studentImages", studentImages);
    }

    private List<Feedback> getFeedbackListByPostDate() {
        return feedbackRepository.findAll()
                .stream()
                .sorted(Comparator.comparing(Feedback::getPostDate).reversed())
                .toList();
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

    public ApiResponse updateAdStatus(long adId, String newStatus) {
        try {
            Advertisement ad = advertisementRepository.findById(adId)
                    .orElseThrow(() -> new AdDoesNotExistException("Advertisement with [" + adId + "] was not found"));

            if (isEmpty(newStatus)) {
                ad.setPostStatus(PENDING);
            } else {
                ad.setPostStatus(PostStatus.valueOf(newStatus));
                //notify the company
                notificationService.notifyUser(newStatus, ad.getJobTitle(), ad.getCompany());
            }

            advertisementRepository.save(ad);
            return new ApiResponse("Advertisement with [" + adId + "] and name [" + ad.getJobTitle() + "] successfully got " +
                    (newStatus.equals(APPROVED.name()) ? "approved" : "rejected"), HttpStatus.OK);
        } catch (AdDoesNotExistException ex) {
            return new ApiResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    public ApiResponse updateFeedbackStatus(long feedbackId, String newStatus) {
        try {
            Feedback feedback = feedbackRepository.findById(feedbackId)
                    .orElseThrow(() -> new FeedbackDoesNotExistException("Advertisement with [" + feedbackId + "] was not found"));

            if (!isEmpty(newStatus)) {
                feedback.setStatus(PostStatus.valueOf(newStatus));
                //notify the student
                String criteria  = "company: " + feedback.getCompany().getCompanyName()
                        + " and rating of [" + feedback.getRating() + "]";
                notificationService.notifyUser(newStatus, criteria, feedback.getStudent());
            } else {
                feedback.setStatus(PENDING);
            }

            feedbackRepository.save(feedback);
            return new ApiResponse("Feedback with [" + feedbackId + "] successfully got " +
                    (newStatus.equals(APPROVED.name()) ? "approved" : "rejected"), HttpStatus.OK);
        } catch (FeedbackDoesNotExistException ex) {
            return new ApiResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}