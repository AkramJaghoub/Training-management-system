package ju.example.training_management_system.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import ju.example.training_management_system.dto.AdvertisementDto;
import ju.example.training_management_system.model.ApiResponse;
import ju.example.training_management_system.service.AdvertisementService;
import ju.example.training_management_system.service.company.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import static java.util.Objects.isNull;
import static ju.example.training_management_system.util.Utils.redirectToPage;

@Controller
@RequiredArgsConstructor
@RequestMapping("/advertisement")
public class AdvertisementController {

    private final AdvertisementService adsPostService;
    private final HttpServletRequest request;

    @GetMapping("/ads-form")
    public String getPostAdsForm(Model model) {
        HttpSession session = request.getSession();
        String email = (String) session.getAttribute("email");
        if (isNull(email)) {
            return "redirect:/login";
        }
        model.addAttribute("email", email);

        ApiResponse response = adsPostService.setUpPostAdsPage(model, email);
        return response.getStatus() == HttpStatus.OK ? "/company/job-post"
                : "redirect:/login";
    }

    @PostMapping("/post")
    public ResponseEntity<?> postAds(@ModelAttribute AdvertisementDto postDto) {
        HttpSession session = request.getSession();
        String email = (String) session.getAttribute("email");
        if (isNull(email)) {
            return redirectToPage("/login");
        }

        ApiResponse response = adsPostService.postAd(postDto, email);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @DeleteMapping("/delete/{adId}")
    public ResponseEntity<?> deleteAd(@PathVariable("adId") long adId) {
        HttpSession session = request.getSession();
        String email = (String) session.getAttribute("email");
        if (isNull(email)) {
            return redirectToPage("/login");
        }

        ApiResponse response = adsPostService.deleteAd(adId, email);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateAd(@ModelAttribute AdvertisementDto adDto) {
        HttpSession session = request.getSession();
        String email = (String) session.getAttribute("email");
        if (isNull(email)) {
            return redirectToPage("/login");
        }

        ApiResponse response = adsPostService.updateAd(adDto, email);
        return ResponseEntity.status(response.getStatus()).body(response);
    }
}
