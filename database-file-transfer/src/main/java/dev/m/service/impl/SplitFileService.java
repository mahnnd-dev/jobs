package dev.m.service.impl;

import com.opencsv.CSVReader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import dev.m.obj.FileInfo;

import java.io.*;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentMap;


@Slf4j
@Service
public class SplitFileService {

    @Value("${app.jobs.read-data.path}")
    private String path;
    @Value("${app.jobs.import-data.path}")
    private String wait;
    @Qualifier("map-file")
    private final ConcurrentMap<String, FileInfo> map;
    private static final int ROWS_PER_THREAD = 100_000;
    private static final int partSize = 10 * 1024 * 1024; // 10MB

    @Autowired
    public SplitFileService(ConcurrentMap<String, FileInfo> map) {
        this.map = map;
    }


    @Scheduled(fixedDelayString = "10000")
    public void run() {
        try {
            log.info("-------------------------------------------");
            readFolder();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void readFolder() {
        try {
            log.info("#Scan folder split file");
            Instant st = Instant.now();
            File directory = new File(path);
            File[] fileList = directory.listFiles();
            if (fileList != null) {
                for (File f : fileList) {
                    //kiểm tra file đã xử lý chưa
                    if (map.get(f.getName()) == null) {
                        FileInfo fileInfo = new FileInfo();
                        if (f.isFile()) {
                            fileInfo.setFileParent(f.getName());
                            List<File> splitFiles = splitCsvFile(f.getAbsolutePath(), wait);
                            log.info("Số file con: {}", splitFiles.size());
                            for (File file : splitFiles) {
                                log.info("File con: {}", file.getName());
                            }
                            fileInfo.setFileChildren(splitFiles);
                            fileInfo.setState(true);
                            fileInfo.setNumChildren(splitFiles.size());
                        }
                        map.put(f.getName(), fileInfo);
                    } else {
                        log.info("File đã xử lý: {}", f.getName());
                    }
                }
            }
            long bw = Duration.between(st, Instant.now()).toMillis();
            log.info("#Split success file to {} millis", bw);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Chia file CSV thành nhiều file nhỏ
    public List<File> splitCsvFile(String inputFile, String outputDir) {
        List<File> splitFiles = new ArrayList<>();
        try {
            try (CSVReader reader = new CSVReader(new FileReader(inputFile))) {
                String[] header = reader.readNext();
                List<String[]> rows = new ArrayList<>();
                String[] row;
                while ((row = reader.readNext()) != null) {
                    rows.add(row);
                    if (rows.size() == SplitFileService.ROWS_PER_THREAD) {
                        File splitFile = new File(outputDir, "split_" + dateString() + ".csv");
                        writeCsvFile(splitFile, header, rows);
                        splitFiles.add(splitFile);
                        rows.clear();
                    }
                }

                // Xử lý dữ liệu còn lại
                if (!rows.isEmpty()) {
                    File splitFile = new File(outputDir, "split_" + dateString() + ".csv");
                    writeCsvFile(splitFile, header, rows);
                    splitFiles.add(splitFile);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Error split file: {}", e.getMessage());
        }
        return splitFiles;
    }

    public String dateString() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSSS");
        Date today = new Date();
        return dateFormat.format(today);
    }

    // Ghi dữ liệu vào file CSV
    private void writeCsvFile(File file, String[] header, List<String[]> rows) throws IOException {
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(String.join(",", header) + "\n");
            for (String[] row : rows) {
                writer.write(String.join(",", row) + "\n");
            }
        }
    }

    public void splitFileSize(String sourceFile, String destinationFilePrefix) {
        try (FileInputStream fis = new FileInputStream(sourceFile)) {
            byte[] buffer = new byte[partSize];
            int bytesRead;
            int partNumber = 1;

            while ((bytesRead = fis.read(buffer)) > 0) {
                String partFileName = destinationFilePrefix + partNumber + ".csv";
                try (FileOutputStream fos = new FileOutputStream(partFileName)) {
                    fos.write(buffer, 0, bytesRead);
                }
                partNumber++;
            }
            System.out.println("File has been split into " + (partNumber - 1) + " csv.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
