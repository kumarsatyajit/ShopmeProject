package com.shopme.admin.utility;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.web.multipart.MultipartFile;

public class FileUploadUtility {

	public static void saveFile(String uploadDir, String fileName, MultipartFile multipartFile) throws IOException {

		if (!(fileName == null)) {
			Path uploadPath = Paths.get(uploadDir);
			if (!Files.exists(uploadPath)) {
				Files.createDirectories(uploadPath);
			}

			try (InputStream inputStream = multipartFile.getInputStream()) {
				Path filePath = uploadPath.resolve(fileName);
				Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException ex) {
				throw new IOException("Could not save file: " + fileName, ex);
			}
		}
	}

	// Clean directory method
	public static void cleanDir(String dir) throws IOException {
		Path dirPath = Paths.get(dir);

		if (!Files.exists(dirPath) || !Files.isDirectory(dirPath)) {
			System.out.println("Directory does not exist or is not a directory: " + dir);
			return;
		}

		try (DirectoryStream<Path> stream = Files.newDirectoryStream(dirPath)) {
			for (Path file : stream) {
				Files.delete(file);
			}
		} catch (IOException ex) {
			throw new IOException("Could not clean directory: " + dirPath, ex);
		}
	}

	// Remove directory method
	public static void removeDir(String dir) throws IOException {
		Path dirPath = Paths.get(dir);

		// Delete all files and subdirectories recursively
		try {
			Files.walk(dirPath).sorted((path1, path2) -> path2.compareTo(path1)) // Sort in reverse order to delete
																					// files before directories
					.forEach(path -> {
						try {
							Files.delete(path);
						} catch (IOException ex) {
							throw new RuntimeException("Could not delete file: " + path, ex);
						}
					});
		} catch (IOException ex) {
			throw new IOException("Could not remove directory: " + dirPath, ex);
		}
	}

	public static void deleteFile(String directory, String fileName) throws IOException {
		Path filePath = Paths.get(directory, fileName);
		System.out.println(filePath);
		System.out.println(filePath.toFile().getAbsolutePath());
		
		if (Files.exists(filePath)) {
			try {
				Files.delete(filePath); // Delete the file
				System.out.println("File deleted successfully: " + fileName);
			} catch (IOException ex) {
				throw new IOException("Could not delete file: " + filePath, ex);
			}
		} else {
			System.out.println("File not found: " + fileName);
		}
	}

}
