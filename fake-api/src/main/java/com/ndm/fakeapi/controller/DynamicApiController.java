package com.ndm.fakeapi.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.stream.Stream;

@Log4j2
@RestController
public class DynamicApiController {

    @Value("${job.path}")
    private String path;

    @GetMapping(value = "/api/{fileName}")
    public ResponseEntity<String> getDynamicContent(@PathVariable String fileName, HttpServletRequest servletRequest) throws IOException {
        Path directoryPath = Paths.get(path);
        Path filePath = findFileWithExtension(directoryPath, fileName);
        Map<String, String[]> parameters = servletRequest.getParameterMap(); // Lấy tất cả các tham số
        for (Map.Entry<String, String[]> entry : parameters.entrySet()) {
            log.info("Parameter: {} = {}", entry.getKey(), entry.getValue());
        }
        if (filePath != null) {
            byte[] bytes = Files.readAllBytes(filePath);
            String content = new String(bytes, StandardCharsets.UTF_8);
            return ResponseEntity.ok().contentType(MediaType.valueOf(getMediaType(getExtension(filePath)))).body(content);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping(value = "/api/{fileName}")
    public ResponseEntity<String> postDynamicContent(@PathVariable String fileName,  @RequestBody String request) throws IOException {
        Path directoryPath = Paths.get(path);
        Path filePath = findFileWithExtension(directoryPath, fileName);
        log.info("Parameter: {}",request);
        if (filePath != null) {
            byte[] bytes = Files.readAllBytes(filePath);
            String content = new String(bytes, StandardCharsets.UTF_8);
            return ResponseEntity.ok().contentType(MediaType.valueOf(getMediaType(getExtension(filePath)))).body(content);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    private String getExtension(Path filePath) {
        String fileName = filePath.getFileName().toString();
        if (fileName.contains(".")) {
            return fileName.substring(fileName.lastIndexOf(".") + 1);
        } else {
            return ""; // Hoặc xử lý trường hợp không có phần mở rộng
        }
    }

    // Hàm tìm file với phần mở rộng trong thư mục
    private Path findFileWithExtension(Path directoryPath, String fileName) throws IOException {
        try (Stream<Path> paths = Files.walk(directoryPath)) {
            return paths.filter(path -> path.getFileName().toString().startsWith(fileName) && path.getFileName().toString().contains("."))
                    .findFirst().orElse(null);
        }
    }

    private String getMediaType(String extension) {
        switch (extension) {
            case "json":
                return "application/json";
            case "xml":
                return "application/xml";
            default:
                return "text/plain";
        }
    }
}