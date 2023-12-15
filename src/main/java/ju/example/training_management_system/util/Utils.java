package ju.example.training_management_system.util;

import ju.example.training_management_system.exception.StorageException;
import ju.example.training_management_system.model.users.Role;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public final class Utils {

    private Utils() {
    }

    public static String getRequiredDashboard(Role role) {
        return switch (role) {
            case ADMIN -> "/admin/dashboard";
            case COMPANY -> "/company/dashboard";
            case STUDENT -> "/student/dashboard";
        };
    }

    public static boolean isNotEqual(String str1, String str2) {
        System.out.println(str1);
        System.out.println(str2);
        System.out.println("result: " + !str1.equals(str2));
        return !str1.equals(str2);
    }

    public static String saveImage(MultipartFile file) {
        Path rootLocation = Paths.get("src/main/resources/static/job-post/images-uploaded");

        if (file.isEmpty()) {
            return null;
        }
        try {
            String filename = System.currentTimeMillis() + "-" + file.getOriginalFilename();
            Path destinationFile = rootLocation.resolve(Paths.get(filename))
                    .normalize().toAbsolutePath();

            if (!destinationFile.getParent().equals(rootLocation.toAbsolutePath())) {
                throw new StorageException("Cannot store file outside current directory.");
            }

            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
            }

            return "/job-post/images-uploaded/" + filename;
        } catch (IOException e) {
            throw new StorageException("Failed to store file.", e);
        }
    }
}
