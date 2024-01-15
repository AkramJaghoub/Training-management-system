package ju.example.training_management_system.util;

import static java.util.Objects.*;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import java.io.IOException;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.multipart.MultipartFile;

public class GCPFileUploader {

  private static final String BUCKET_NAME = "tero_system";
  private static final String CREDENTIALS_FILE_PATH = "tero-system-cb9716b15b42.json";

  public static String uploadFile(MultipartFile file, String mapper) throws IOException {
    ClassPathResource resource = new ClassPathResource(CREDENTIALS_FILE_PATH);

    Storage storage =
        StorageOptions.newBuilder()
            .setCredentials(GoogleCredentials.fromStream(resource.getInputStream()))
            .build()
            .getService();

    String originalFileName = requireNonNull(file.getOriginalFilename());

    String fileName =
        (mapper != null && !mapper.isEmpty()) ? mapper + "_" + originalFileName : originalFileName;

    BlobId blobId = BlobId.of(BUCKET_NAME, fileName);
    BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType(file.getContentType()).build();
    storage.create(blobInfo, file.getBytes());

    return String.format("https://storage.googleapis.com/%s/%s", BUCKET_NAME, fileName);
  }
}
