package ru.vsu.cs.platon.docs.service.file;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;

@Service
@Primary
public class LocalFileService implements FileService {

    private static final String UPLOAD_DIR = "uploads";
    private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyz";
    private static final int FOLDER_NAME_LENGTH = 3;
    private static final int FILE_NAME_LENGTH = 10;
    private final SecureRandom random = new SecureRandom();

    @Override
    public String saveFile(byte[] content) {
        String folderPath = generateRandomFolderPath();
        File directory = new File(UPLOAD_DIR, folderPath);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        String fileName = generateRandomString(FILE_NAME_LENGTH);
        Path filePath = Paths.get(directory.getPath(), fileName);

        try {
            Files.write(filePath, content);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save file", e);
        }

        return Paths.get(folderPath, fileName).toString();
    }

    @Override
    public byte[] getFile(String path) {
        Path filePath = Paths.get(UPLOAD_DIR, path);

        try {
            return Files.readAllBytes(filePath);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read file", e);
        }
    }

    private String generateRandomFolderPath() {
        return String.join(File.separator,
                generateRandomString(FOLDER_NAME_LENGTH),
                generateRandomString(FOLDER_NAME_LENGTH),
                generateRandomString(FOLDER_NAME_LENGTH));
    }

    private String generateRandomString(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(ALPHABET.charAt(random.nextInt(ALPHABET.length())));
        }
        return sb.toString();
    }
}
