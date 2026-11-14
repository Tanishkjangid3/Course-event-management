package com.campusevents.util;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

/**
 * High-performance image loading, smooth scaling, thumbnail caching,
 * gradient fallback generation, and admin file upload management.
 */
public class ImageUtil {

    private static final Map<String, ImageIcon> IMAGE_CACHE = new ConcurrentHashMap<>();

    private static final Color[][] GRADIENTS = {
        {new Color(0x1A, 0x73, 0xE8), new Color(0x0B, 0x57, 0xD0)}, // Blue
        {new Color(0x34, 0xA8, 0x53), new Color(0x18, 0x80, 0x38)}, // Green
        {new Color(0xEA, 0x43, 0x35), new Color(0xC5, 0x22, 0x1F)}, // Red
        {new Color(0xFB, 0xBC, 0x04), new Color(0xE3, 0x74, 0x00)}, // Amber
        {new Color(0x67, 0x3A, 0xB7), new Color(0x45, 0x27, 0xA0)}, // Purple
        {new Color(0x00, 0x96, 0x88), new Color(0x00, 0x69, 0x5C)}  // Teal
    };

    /**
     * Loads an image, scales it smoothly keeping aspect ratio (center-cropped to fit),
     * caches the result, or generates an attractive gradient fallback if missing.
     */
    public static ImageIcon getImageIcon(String path, int width, int height, String fallbackInitials) {
        String cacheKey = (path != null ? path : "null") + "_" + width + "x" + height;
        if (IMAGE_CACHE.containsKey(cacheKey)) {
            return IMAGE_CACHE.get(cacheKey);
        }

        BufferedImage original = loadImage(path);
        if (original == null) {
            ImageIcon fallback = createFallbackIcon(width, height, fallbackInitials);
            IMAGE_CACHE.put(cacheKey, fallback);
            return fallback;
        }

        BufferedImage scaled = scaleCenterCrop(original, width, height);
        ImageIcon icon = new ImageIcon(scaled);
        IMAGE_CACHE.put(cacheKey, icon);
        return icon;
    }

    /**
     * Loads a raw image from classpath or file system.
     */
    public static BufferedImage loadImage(String path) {
        if (path == null || path.trim().isEmpty()) {
            return null;
        }

        String cleanPath = path.trim().replace("\\", "/");
        if (cleanPath.startsWith("/")) {
            cleanPath = cleanPath.substring(1);
        }

        // 1. Try classpath resource
        try (InputStream is = ImageUtil.class.getClassLoader().getResourceAsStream(cleanPath)) {
            if (is != null) {
                return ImageIO.read(is);
            }
        } catch (Exception ignored) {}

        // 2. Try file system (uploads or project files)
        try {
            Path filePath = Paths.get(cleanPath);
            if (Files.exists(filePath)) {
                try (InputStream fis = new FileInputStream(filePath.toFile())) {
                    return ImageIO.read(fis);
                }
            }
        } catch (Exception ignored) {}

        return null;
    }

    /**
     * Scales an image to exactly fill width x height by center-cropping,
     * maintaining crisp aspect ratio without distortion.
     */
    public static BufferedImage scaleCenterCrop(BufferedImage source, int targetWidth, int targetHeight) {
        int srcWidth = source.getWidth();
        int srcHeight = source.getHeight();

        double srcAspect = (double) srcWidth / srcHeight;
        double targetAspect = (double) targetWidth / targetHeight;

        int cropX = 0;
        int cropY = 0;
        int cropWidth = srcWidth;
        int cropHeight = srcHeight;

        if (srcAspect > targetAspect) {
            // Source is wider than target: crop horizontally
            cropWidth = (int) (srcHeight * targetAspect);
            cropX = (srcWidth - cropWidth) / 2;
        } else {
            // Source is taller than target: crop vertically
            cropHeight = (int) (srcWidth / targetAspect);
            cropY = (srcHeight - cropHeight) / 2;
        }

        BufferedImage cropped = source.getSubimage(cropX, cropY, cropWidth, cropHeight);
        BufferedImage output = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2 = output.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.drawImage(cropped, 0, 0, targetWidth, targetHeight, null);
        g2.dispose();

        return output;
    }

    /**
     * Generates a gradient artwork fallback with initials.
     */
    public static ImageIcon createFallbackIcon(int width, int height, String initials) {
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int hash = Math.abs((initials != null ? initials.hashCode() : 42));
        Color[] grad = GRADIENTS[hash % GRADIENTS.length];

        GradientPaint gp = new GradientPaint(0, 0, grad[0], width, height, grad[1]);
        g2.setPaint(gp);
        g2.fillRect(0, 0, width, height);

        // Subtle geometric circle overlay
        g2.setColor(new Color(255, 255, 255, 30));
        int circleRadius = Math.min(width, height) / 2;
        g2.fillOval(width / 2 - circleRadius, height / 2 - circleRadius, circleRadius * 2, circleRadius * 2);

        // Draw Initials
        String text = (initials != null && !initials.trim().isEmpty()) ? initials.trim().toUpperCase() : "CE";
        if (text.length() > 3) text = text.substring(0, 3);

        int fontSize = Math.max(16, Math.min(width, height) / 4);
        Font font = new Font("Segoe UI", Font.BOLD, fontSize);
        g2.setFont(font);
        g2.setColor(Color.WHITE);

        FontMetrics fm = g2.getFontMetrics();
        int tx = (width - fm.stringWidth(text)) / 2;
        int ty = ((height - fm.getHeight()) / 2) + fm.getAscent();
        g2.drawString(text, tx, ty);

        g2.dispose();
        return new ImageIcon(img);
    }

    /**
     * Copies an admin-uploaded thumbnail file into the uploads directory
     * and returns the relative path.
     */
    public static String uploadThumbnail(File sourceFile) throws IOException {
        if (sourceFile == null || !sourceFile.exists()) {
            throw new IOException("Source file does not exist.");
        }

        Path uploadsDir = Paths.get("uploads");
        if (!Files.exists(uploadsDir)) {
            Files.createDirectories(uploadsDir);
        }

        String fileName = sourceFile.getName();
        String extension = "";
        int dot = fileName.lastIndexOf('.');
        if (dot >= 0) {
            extension = fileName.substring(dot);
        }

        String uniqueName = "event_" + System.currentTimeMillis() + extension;
        Path targetPath = uploadsDir.resolve(uniqueName);
        Files.copy(sourceFile.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);

        return "uploads/" + uniqueName;
    }
}

