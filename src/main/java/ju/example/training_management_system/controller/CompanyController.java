package ju.example.training_management_system.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import ju.example.training_management_system.dto.AdvertisementDto;
import ju.example.training_management_system.model.company.advertisement.Advertisement;
import ju.example.training_management_system.model.users.Role;
import ju.example.training_management_system.service.company.CompanyService;
import ju.example.training_management_system.service.company.post.AdsPostService;
import ju.example.training_management_system.util.Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/company")
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyService companyService;
    private final AdsPostService adsPostService;
    private final HttpServletResponse response;

    @GetMapping("/dashboard")
    public String setUpCompanyDashboard(HttpSession session, Model model) {

        String email = (String) session.getAttribute("email");

        if (email != null) {
            String companyName = companyService.getCompanyName(email);
            Cookie companyNameCookie = new Cookie("companyName", companyName);
            companyNameCookie.setPath("/");
            companyNameCookie.setMaxAge(24 * 60 * 60 * 30);
            response.addCookie(companyNameCookie);
        }

        session.setAttribute("email", email);

        return Utils.getRequiredDashboard(Role.COMPANY);
    }

    @GetMapping("/manage-profile")
    public String getManageProfilePage() {
        return "manage-profile";
    }

    @PutMapping("/manage-profile")
    public String manageProfile(@RequestBody Map<String, Object> userData) {
        System.out.println(userData.entrySet());
        companyService.updateCompanyDetails(userData);
        return null;
    }

    @GetMapping("/job/post")
    public String getPostAdsForm(HttpSession session, Model model) {
        String email = (String) session.getAttribute("email");
        if (email != null) {
            model.addAttribute("email", email);
            String companyName = companyService.getCompanyName(email);
            model.addAttribute("companyName", companyName);
        }
        return "job-post";
    }

    @PostMapping("/job/post/{email}")
    public String postAds(@ModelAttribute AdvertisementDto postDto, @PathVariable("email") String email) throws IOException {
        adsPostService.postAd(postDto, email);
        return "job-post";
    }

    @GetMapping("/get/ads")
    public ResponseEntity<List<Advertisement>> getAdvertisementsForCompany(HttpSession session) {
        String email = (String) session.getAttribute("email");
        if (email != null) {
            String companyName = companyService.getCompanyName(email);
            List<Advertisement> advertisements = adsPostService.getAllAdvertisementsForCompany(companyName);
            return ResponseEntity.ok(advertisements);
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
    }

    @PostMapping("/delete/ad/{position}")
    public void deleteAd(@PathVariable("position") String position, HttpSession session) {
        String email = (String) session.getAttribute("email");
        if (email != null) {
            String companyName = companyService.getCompanyName(email);
            adsPostService.deleteAd(companyName, position);
        }
    }
}
