import java.io.*;
import java.net.*;

public class FileDownloader {
    public void downloadFile(String fileURL, String savePath) {
        try {
            // Convert URL safely
            URL url = new URI(fileURL).toURL();
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            //connection.setRequestProperty("User-Agent", "Mozilla/5.0");

            long fileSize = connection.getContentLengthLong(); // Get file size safely
            if (fileSize <= 0) {
                System.out.println("Warning: Could not determine file size. Progress may be inaccurate.");
            }

            try (InputStream inputStream = connection.getInputStream();
                 FileOutputStream outputStream = new FileOutputStream(savePath)) {

                byte[] buffer = new byte[8192]; // Use a larger buffer for efficiency
                int bytesRead;
                long totalRead = 0;

                while ((bytesRead = inputStream.read(buffer)) != -1) {
                 outputStream.write(buffer, 0, bytesRead);
                    //System.out.write(buffer, 0, bytesRead);
                    totalRead += bytesRead;

                    if (fileSize > 0) { // Only display progress if file size is known
                        int progress = (int) ((totalRead * 100) / fileSize);
                        System.out.printf("\rDownloading... %d%%", progress); 
                    }
                }
                System.out.println("\nDownload complete!");
            }

        } catch (IOException | URISyntaxException e) {
            System.out.println("Error downloading file: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        String fileURL = "https://nbg1-speed.hetzner.com/100MB.bin";  // Large test file
        String savePath = "large_test_file.bin"; 
        FileDownloader a = new FileDownloader(); 
        a.downloadFile(fileURL, savePath);
    }
}

