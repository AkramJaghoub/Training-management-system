package ju.example.training_management_system.service;

import ju.example.training_management_system.exception.UnauthorizedStudentAccessException;
import ju.example.training_management_system.exception.UserNotFoundException;
import ju.example.training_management_system.model.ApiResponse;
import ju.example.training_management_system.model.Feedback;
import ju.example.training_management_system.model.users.Student;
import ju.example.training_management_system.model.users.User;
import ju.example.training_management_system.repository.FeedbackRepository;
import ju.example.training_management_system.repository.users.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.*;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static ju.example.training_management_system.model.PostStatus.APPROVED;
import static ju.example.training_management_system.util.Utils.convertToBase64;
import static ju.example.training_management_system.util.Utils.decompressImage;
import static org.springframework.http.HttpStatus.*;

@Service
@RequiredArgsConstructor
public class CommunityService {

    private final UserRepository userRepository;
    private final FeedbackRepository feedbackRepository;

    public ApiResponse setUpCommunityPage(Model model, String email) {
        try {
            User user = userRepository.findByEmail(email);

            if (isNull(user)) {
                throw new UserNotFoundException("User with email " + email + " wasn't found");
            }

            if (!(user instanceof Student student)) {
                throw new UnauthorizedStudentAccessException("User with email " + email + " wasn't recognized as a student");
            }

            List<Feedback> feedbackList = getFeedbacksByPostDateAndStatus();
            List<Feedback> studentFeedback = getFeedbacksByStudentIdAndPostDate(user.getId());

            String studentName = student.getFirstName() + " " + student.getLastName();
            Map<Long, String> allUserImages = getUserImages(feedbackList);

            model.addAttribute("studentName", studentName);
            model.addAttribute("studentImage", getUserImageBase64(user).orElse(null));
            model.addAttribute("feedbackList", feedbackList);
            model.addAttribute("studentFeedback", studentFeedback);
            model.addAttribute("allUserImages", allUserImages);

            return new ApiResponse("Set up was correctly done", OK);
        } catch (UserNotFoundException | UnauthorizedStudentAccessException ex) {
            return new ApiResponse(ex.getMessage(), ex instanceof UserNotFoundException ? BAD_REQUEST : UNAUTHORIZED);
        }
    }

    private Map<Long, String> getUserImages(List<Feedback> feedbackList) {
        Map<Long, String> allUserImages = new HashMap<>();

        for (Feedback feedback : feedbackList) {
            User feedbackUser = feedback.getStudent();
            allUserImages.put(feedbackUser.getId(), getUserImageBase64(feedbackUser).orElse(null));
        }

        return allUserImages;
    }

    private Optional<String> getUserImageBase64(User user) {
        if (nonNull(user.getImage())) {
            byte[] decompressedImage = decompressImage(user.getImage());
            String base64Image = convertToBase64(decompressedImage);
            return Optional.of(base64Image);
        }
        return Optional.empty();
    }

    private List<Feedback> getFeedbacksByStudentIdAndPostDate(Long studentId) {
        return feedbackRepository.findByStudent_Id(studentId)
                .stream()
                .sorted(Comparator.comparing(Feedback::getPostDate).reversed())
                .toList();
    }

    private List<Feedback> getFeedbacksByPostDateAndStatus() {
        return feedbackRepository.findAll()
                .stream()
                .sorted(Comparator.comparing(Feedback::getPostDate).reversed())
                .filter((feedback) -> feedback.getStatus().equals(APPROVED))
                .toList();
    }
}