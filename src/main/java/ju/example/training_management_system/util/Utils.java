package ju.example.training_management_system.util;

import ju.example.training_management_system.exception.StorageException;
import ju.example.training_management_system.model.users.Role;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Base64;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

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
        return !str1.equals(str2);
    }

    public static byte[] saveImage(MultipartFile file) {
        try {
            // Compress the byte array
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            try (DeflaterOutputStream deflaterOutputStream = new DeflaterOutputStream(byteArrayOutputStream)) {
                deflaterOutputStream.write(file.getBytes());
            }

            return byteArrayOutputStream.toByteArray();

        } catch (IOException e) {
            throw new StorageException("Failed to store and compress file.", e);
        }
    }

    public static byte[] decompressImage(byte[] compressedImage) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try (InflaterInputStream inflaterInputStream = new InflaterInputStream(new ByteArrayInputStream(compressedImage))) {
            byte[] buffer = new byte[1024];
            int len;
            while ((len = inflaterInputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, len);
            }
        } catch (IOException e) {
            throw new StorageException("Failed to decompress image", e);
        }
        return outputStream.toByteArray();
    }

    public static String convertToBase64(byte[] imageBytes) {
        return Base64.getEncoder().encodeToString(imageBytes);
    }
}