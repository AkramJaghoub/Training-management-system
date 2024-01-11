package ju.example.training_management_system.controller;

import ju.example.training_management_system.model.ApiResponse;
import ju.example.training_management_system.service.CommunityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/community")
@RequiredArgsConstructor
public class CommunityController {

    private final CommunityService communityService;

    @PostMapping("/{companyId}")
    public ResponseEntity<?> rateCompany(@PathVariable("companyId") long companyId,
                                         double rating){
        ApiResponse apiResponse = communityService.provideFeedback(companyId, rating);
        return ResponseEntity.status(apiResponse.getStatus()).body(apiResponse.getMessage());
    }
}