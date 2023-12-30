package ju.example.training_management_system.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import ju.example.training_management_system.dto.AdvertisementDto;
import ju.example.training_management_system.model.ApiResponse;
import ju.example.training_management_system.model.company.advertisement.Advertisement;
import ju.example.training_management_system.service.AdvertisementService;
import ju.example.training_management_system.service.company.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/advertisement")
public class AdvertisementController {

    private final CompanyService companyService;
    private final AdvertisementService adsPostService;
    private final HttpServletResponse response;
    private final HttpServletRequest request;

    @GetMapping("/ads-form")
    public String getPostAdsForm(Model model) {
        HttpSession session = request.getSession();

        String email = (String) session.getAttribute("email");
        if (email != null && !email.equals("root")) {
            model.addAttribute("email", email);
            companyService.setUpCompanyDashboard(model, email, response);
            session.setAttribute("email", email);
            return "/company/job-post";
        }

        return "redirect:/login";
    }


    @PostMapping("/post")
    public ResponseEntity<?> postAds(@ModelAttribute AdvertisementDto postDto) {
        HttpSession session = request.getSession();

        String email = (String) session.getAttribute("email");
        if (email != null && !email.equals("root")) {
            ApiResponse apiResponse = adsPostService.postAd(postDto, email);
            return ResponseEntity.status(apiResponse.getStatus()).body(apiResponse);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", "/login");
        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }

    @GetMapping("/get")
    public ResponseEntity<?> getAdvertisementsForCompany() {
        HttpSession session = request.getSession();

        String email = (String) session.getAttribute("email");
        if (email != null && !email.equals("root")) {
            String companyName = companyService.getCompanyName(email);
            List<Advertisement> advertisements = adsPostService.getAllAdvertisementsForCompany(companyName);
            session.setAttribute("email", email);
            return ResponseEntity.ok(advertisements);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", "/login");
        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }

    @DeleteMapping("/delete/{adId}")
    public ResponseEntity<?> deleteAd(@PathVariable("adId") long adId,
                           HttpSession session) {
        String email = (String) session.getAttribute("email");
        if (email != null && !email.equals("root")) {
            ApiResponse apiResponse = adsPostService.deleteAd(adId, email);
            return ResponseEntity.status(apiResponse.getStatus()).body(apiResponse);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", "/login");
        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateAd(@ModelAttribute AdvertisementDto adDto) {
        HttpSession session = request.getSession();

        String email = (String) session.getAttribute("email");
        if (email != null && !email.equals("root")) {
            ApiResponse apiResponse = adsPostService.updateAd(adDto, email);
            return ResponseEntity.status(apiResponse.getStatus()).body(apiResponse);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", "/login");
        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }
}
