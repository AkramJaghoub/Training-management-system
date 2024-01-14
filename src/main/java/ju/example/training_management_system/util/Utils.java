package ju.example.training_management_system.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;
import ju.example.training_management_system.exception.StorageException;
import ju.example.training_management_system.model.users.Role;
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

  public static byte[] saveImage(MultipartFile file) {
    try {
      // Compress the byte array
      ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
      try (DeflaterOutputStream deflaterOutputStream =
          new DeflaterOutputStream(byteArrayOutputStream)) {
        deflaterOutputStream.write(file.getBytes());
      }

      return byteArrayOutputStream.toByteArray();

    } catch (IOException e) {
      throw new StorageException("Failed to store and compress file");
    }
  }

  public static byte[] decompressImage(byte[] compressedImage) {
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    try (InflaterInputStream inflaterInputStream =
        new InflaterInputStream(new ByteArrayInputStream(compressedImage))) {
      byte[] buffer = new byte[1024];
      int len;
      while ((len = inflaterInputStream.read(buffer)) > 0) {
        outputStream.write(buffer, 0, len);
      }
    } catch (IOException e) {
      throw new StorageException("Failed to decompress image");
    }
    return outputStream.toByteArray();
  }

  public static String convertToBase64(byte[] imageBytes) {
    return Base64.getEncoder().encodeToString(imageBytes);
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
