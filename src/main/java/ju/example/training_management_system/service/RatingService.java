package ju.example.training_management_system.service;

import ju.example.training_management_system.exception.UnauthorizedCompanyAccessException;
import ju.example.training_management_system.exception.UserNotFoundException;
import ju.example.training_management_system.model.ApiResponse;
import ju.example.training_management_system.model.users.Company;
import ju.example.training_management_system.repository.users.CompanyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;

@Service
@RequiredArgsConstructor
public class RatingService {

    private final CompanyRepository companyRepository;

    public ApiResponse rateCompany(long companyId, double rating) {
        try {
            Company company = companyRepository.findById(companyId)
                    .orElseThrow(() -> new UserNotFoundException("Company was not found"));

            company.setRating(rating);
            companyRepository.save(company);
            return new ApiResponse("Company rating was set successfully", OK);
        } catch (UserNotFoundException | UnauthorizedCompanyAccessException ex) {
            return new ApiResponse(ex.getMessage(), BAD_REQUEST);
        }
    }
}