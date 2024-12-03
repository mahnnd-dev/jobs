package dev.m.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.telnet.TelnetClient;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class TelnetService {
    private final ThreadPoolTaskExecutor taskExecutor;
    @Value("${job.path.read-file}")
    String pathRead;
    @Value("${job.path.export-file}")
    String pathExp;
    private final TelnetClient telnetClient;
    private final List<String[]> telnetResults = new ArrayList<>();

    @Autowired
    public TelnetService(ThreadPoolTaskExecutor taskExecutor, TelnetClient telnetClient) {
        this.taskExecutor = taskExecutor;
        this.telnetClient = telnetClient;
    }

    @PostConstruct
    public void init() {
        runTelnetTasksAndExport();
    }

    public void runTelnetTasksAndExport() {
        // Thực hiện telnet trên các dữ liệu từ file
        readFileCsv();  // Hoặc readFileTxt() tùy vào file
        // Chờ tất cả các tác vụ telnet hoàn tất
        taskExecutor.getThreadPoolExecutor().shutdown();
        try {
            boolean tasksCompleted = taskExecutor.getThreadPoolExecutor().awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
            if (tasksCompleted) {
                log.info("Tất cả các tác vụ telnet đã hoàn thành.");
                // Xuất kết quả ra file Excel
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
                Date today = new Date();
                String d = dateFormat.format(today);
                String path = pathExp + "resultTelnet_" + d + ".xlsx";
                exportToExcel(path);
            } else {
                log.error("Quá thời gian chờ nhưng các tác vụ telnet vẫn chưa hoàn thành.");
                // Bạn có thể đưa ra hành động dự phòng như ghi log hoặc dừng chương trình
            }
        } catch (InterruptedException e) {
            log.error("Lỗi khi chờ tác vụ telnet hoàn thành: {}", e.getMessage());
            Thread.currentThread().interrupt();
        }
    }

    public void readFileCsv() {
        try (BufferedReader br = new BufferedReader(new FileReader(this.pathRead))) {
            String line;
            InetAddress localhost = InetAddress.getLocalHost();
            String ipLocal = localhost.getHostAddress();
//            String ipLocal = "10.0.9.210";
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                String ipSrc = parts[2];
                String name = parts[3];
                String[] ips = expandIpRanges(parts[4].trim());  // Xử lý dải IP
                String[] ports = expandPortRanges(parts[5].trim());  // Xử lý dải Port
                if (ipSrc.contains(ipLocal)) {
                    for (String value : ips) {
                        for (String s : ports) {
                            telnet(ipLocal, name, value, Integer.parseInt(s));
                        }
                    }
                }
            }
        } catch (IOException e) {
            log.info("Error reading file: {}", e.getMessage());
        }
    }

    private static String[] expandIpRanges(String ipRanges) {
        String[] ranges = ipRanges.split(" ");
        List<String> allIps = new ArrayList<>();

        for (String range : ranges) {
            allIps.addAll(Arrays.asList(expandIpRange(range)));  // Mở rộng từng dải IP
        }

        return allIps.toArray(new String[0]);
    }

    private static String[] expandIpRange(String ipRange) {
        if (!ipRange.contains("-")) {
            return new String[]{ipRange};  // Không có dải, trả về chính IP
        }

        String[] ipParts = ipRange.split("\\.");  // Tách theo dấu "."
        String lastPart = ipParts[3];  // Phần cuối chứa dải, ví dụ: "58-60" hoặc "210-217"

        String[] rangeParts = lastPart.split("-");  // Tách dải ra, ví dụ: "58", "60"
        int start = Integer.parseInt(rangeParts[0]);
        int end = Integer.parseInt(rangeParts[1]);

        List<String> ipList = new ArrayList<>();

        for (int i = start; i <= end; i++) {
            ipList.add(ipParts[0] + "." + ipParts[1] + "." + ipParts[2] + "." + i);
        }

        return ipList.toArray(new String[0]);
    }

    private String[] expandPortRanges(String ipRanges) {
        String[] ranges = ipRanges.split(" ");
        List<String> allIps = new ArrayList<>();

        for (String range : ranges) {
            allIps.addAll(Arrays.asList(expandPortRange(range)));  // Mở rộng từng dải IP
        }

        return allIps.toArray(new String[0]);
    }

    /**
     * Xử lý dải port, ví dụ: "8081-8900"
     * sẽ trả về một mảng ["8081", "8082", ..., "8900"]
     */
    private String[] expandPortRange(String portRange) {
        if (!portRange.contains("-")) {
            return new String[]{portRange};  // Không có dải, trả về chính port
        }

        String[] portParts = portRange.split("-");
        int startPort = Integer.parseInt(portParts[0]);
        int endPort = Integer.parseInt(portParts[1]);

        List<String> portList = new ArrayList<>();

        for (int port = startPort; port <= endPort; port++) {
            portList.add(String.valueOf(port));
        }

        return portList.toArray(new String[0]);
    }

    // Cập nhật hàm telnet để lưu kết quả
    public void telnet(String ipSource, String name, String ip, int port) {
        taskExecutor.execute(() -> {
            String result;
            try {
                telnetClient.connect(ip, port);
                result = "OK";
                log.info("#Telnet name {} ip src: {} --> ip dest: {}, port {} OK", name, ipSource, ip, port);
            } catch (IOException e) {
                result = "ERROR: " + e.getMessage();
                log.info("#Telnet name {} ip src: {} --> ip dest: {}, port {}, ERROR {}", name, ipSource, ip, port, e.getMessage());
            }
            // Lưu kết quả vào danh sách
            telnetResults.add(new String[]{name, ipSource, ip, String.valueOf(port), result});
        });
    }

    // Hàm để xuất kết quả ra file Excel
    public void exportToExcel(String filePath) {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Telnet Results");

        // Tiêu đề
        String[] headers = {"Tên server", "IP nguồn", "IP đích", "Port đích", "Kết quả"};
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            headerRow.createCell(i).setCellValue(headers[i]);
        }

        // Ghi dữ liệu vào các dòng tiếp theo
        int rowNum = 1;
        for (String[] result : telnetResults) {
            Row row = sheet.createRow(rowNum++);
            for (int i = 0; i < result.length; i++) {
                row.createCell(i).setCellValue(result[i]);
            }
        }
        // Xuất file Excel ra đường dẫn chỉ định
        try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
            workbook.write(fileOut);
            log.info("Kết quả telnet đã được ghi vào file Excel tại: {}", filePath);
        } catch (IOException e) {
            log.error("Lỗi khi xuất file Excel: {}", e.getMessage());
        } finally {
            try {
                workbook.close();
            } catch (IOException e) {
                log.error("Lỗi khi đóng workbook: {}", e.getMessage());
            }
        }
    }
}
