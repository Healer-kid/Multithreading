# Multi-Threaded File Downloader  

A Java program that downloads a file in parallel using multiple threads, improving download speed.  

## Features  
- Uses **4 threads** to download different parts of a file concurrently.  
- Merges the downloaded parts into a single file.  
- Supports large file downloads with efficient buffering.  

## Usage  
1. Update `fileURL` in `main()` with the desired file link.  
2. Compile and run:  
   ```sh
   javac MultiThreadedFileDownloader.java  
   java MultiThreadedFileDownloader  
   ```  
3. The file will be saved in the current directory.  

## Dependencies  
- Java 8+  
- Internet connection  

## How It Works  
- The file is **split** into equal parts.  
- Each part is **downloaded in parallel** using `ExecutorService`.  
- The parts are **merged** after all threads finish execution.  

