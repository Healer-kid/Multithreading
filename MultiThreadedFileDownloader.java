import java.io.*;
import java.net.*;
import java.util.concurrent.*;

class FileDownloadTask implements Runnable {
    private String fileURL;
    private String savePath;
    private long startByte;
    private long endByte;
    private int partNumber;

    public FileDownloadTask(String fileURL, String savePath, long startByte, long endByte, int partNumber) {
        this.fileURL = fileURL;
        this.savePath = savePath + ".part" + partNumber;
        this.startByte = startByte;
        this.endByte = endByte;
        this.partNumber = partNumber;
    }

    @Override
    public void run() {
        try {
            // Convert URL safely
            URL url = new URI(fileURL).toURL();
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Range", "bytes=" + startByte + "-" + endByte);
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");

            try (InputStream inputStream = connection.getInputStream();
                 FileOutputStream outputStream = new FileOutputStream(savePath)) {

                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                System.out.println("Part " + partNumber + " downloaded.");
            }
        } catch (IOException | URISyntaxException e) {
            System.out.println("Error downloading part " + partNumber + ": " + e.getMessage());
        }
    }
}

public class MultiThreadedFileDownloader {
    private static final int NUM_THREADS = 4; // Number of parallel threads

    public static void downloadFile(String fileURL, String savePath) {
        try {
            // Convert URL safely
            URL url = new URI(fileURL).toURL();
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");
            long fileSize = connection.getContentLengthLong();
            connection.disconnect();

            if (fileSize <= 0) {
                System.out.println("Unable to determine file size. Exiting.");
                return;
            }

            System.out.println("File size: " + fileSize + " bytes");

            // Split file into parts
            long partSize = fileSize / NUM_THREADS;
            ExecutorService executor = Executors.newFixedThreadPool(NUM_THREADS);

            for (int i = 0; i < NUM_THREADS; i++) {
                long startByte = i * partSize;
                long endByte = (i == NUM_THREADS - 1) ? fileSize - 1 : (startByte + partSize - 1);
                executor.execute(new FileDownloadTask(fileURL, savePath, startByte, endByte, i));
            }

            executor.shutdown();
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);

            // Merge parts
            mergeParts(savePath, NUM_THREADS);

        } catch (IOException | InterruptedException | URISyntaxException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void mergeParts(String savePath, int numParts) {
        try (FileOutputStream fos = new FileOutputStream(savePath);
             BufferedOutputStream mergingStream = new BufferedOutputStream(fos)) {
            for (int i = 0; i < numParts; i++) {
                File partFile = new File(savePath + ".part" + i);
                try (FileInputStream fis = new FileInputStream(partFile);
                     BufferedInputStream bis = new BufferedInputStream(fis)) {
                    byte[] buffer = new byte[8192];
                    int bytesRead;
                    while ((bytesRead = bis.read(buffer)) != -1) {
                        mergingStream.write(buffer, 0, bytesRead);
                    }
                }
                partFile.delete(); // Delete part files after merging
            }
            System.out.println("Download complete! File saved as: " + savePath);
        } catch (IOException e) {
            System.out.println("Error merging file parts: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        String fileURL = "https://nbg1-speed.hetzner.com/100MB.bin"; // Large test file
        String savePath = "large_test_file.bin";
        downloadFile(fileURL, savePath);
    }
}
