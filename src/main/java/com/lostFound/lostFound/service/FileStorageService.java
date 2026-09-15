package com.lostFound.lostFound.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.*;

@Service
public class FileStorageService {
    private final Path uploadPath;
    public FileStorageService(@Value("${file.upload-dir}") String uploadDir) { this.uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize(); }
    public String store(MultipartFile file) {
        if (file == null || file.isEmpty()) return null;
        try {
            Files.createDirectories(uploadPath);
            String original = StringUtils.cleanPath(file.getOriginalFilename() == null ? "item-image" : file.getOriginalFilename());
            String fileName = System.currentTimeMillis() + "_" + original.replaceAll("[^a-zA-Z0-9._-]", "_");
            Path target = uploadPath.resolve(fileName).normalize();
            if (!target.startsWith(uploadPath)) throw new IllegalArgumentException("Invalid upload path");
            file.transferTo(target);
            return fileName;
        } catch (IOException ex) { throw new IllegalArgumentException("Could not store uploaded file", ex); }
    }
    public String fingerprint(MultipartFile file) {
        if (file == null || file.isEmpty()) return null;
        try (var input = file.getInputStream()) {
            BufferedImage image = ImageIO.read(input);
            if (image == null) return null;
            long sum = 0; int[] values = new int[64]; int index = 0;
            for (int y = 0; y < 8; y++) for (int x = 0; x < 8; x++) {
                int rgb = image.getRGB(Math.min(image.getWidth() - 1, x * image.getWidth() / 8), Math.min(image.getHeight() - 1, y * image.getHeight() / 8));
                int value = ((rgb >> 16 & 255) * 299 + (rgb >> 8 & 255) * 587 + (rgb & 255) * 114) / 1000;
                values[index++] = value; sum += value;
            }
            long average = sum / 64; StringBuilder hash = new StringBuilder(64);
            for (int value : values) hash.append(value >= average ? '1' : '0');
            return hash.toString();
        } catch (IOException ex) { return null; }
    }
    public Path uploadPath() { return uploadPath; }
}