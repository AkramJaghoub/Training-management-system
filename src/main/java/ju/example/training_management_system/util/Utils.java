package ju.example.training_management_system.util;

import java.io.IOException;
import ju.example.training_management_system.exception.StorageException;
import ju.example.training_management_system.model.Role;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public final class Utils {

  private Utils() {}

  public static ResponseEntity<?> redirectToPage(String location) {
    HttpHeaders headers = new HttpHeaders();
    headers.add("Location", location);
    return new ResponseEntity<>(headers, HttpStatus.FOUND);
  }

  public static String getRequiredDashboard(Role role) {
    return switch (role) {
      case ADMIN -> "/admin/dashboard";
      case COMPANY -> "/company/dashboard";
      case STUDENT -> "/student/dashboard";
    };
  }

  public static boolean isEmpty(String str) {
    return str == null || str.isEmpty();
  }

  public static String saveImage(MultipartFile file, String mapper) {
    try {
      return GCPFileUploader.uploadFile(file, mapper);
    } catch (IOException e) {
      throw new StorageException("Failed to upload file to GCP");
    }
  }

  public static String capitalizeFirstLetter(String input) {
    if (input == null || input.isEmpty()) {
      return input;
    }

    StringBuilder capitalizedString = new StringBuilder();
    String[] words = input.split("\\s+");

    for (String word : words) {
      if (!word.isEmpty()) {
        capitalizedString.append(Character.toUpperCase(word.charAt(0)));
        capitalizedString.append(word.substring(1)).append(" ");
      }
    }
    return capitalizedString.toString().trim();
  }
}
