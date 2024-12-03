package vn.ndm.data.transfer.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vn.ndm.data.transfer.utils.IMSIGenerator;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class PhoneNumberGenerator {


    public static void main(String[] args) {
        System.out.println(genarateData());
    }

    public static boolean genarateData() {
        String path = "data";
        String wait = "data/read";
        List<String> phoneNumbers = new ArrayList<>();
        File directory = new File(path);
        File[] fileList = directory.listFiles();
        if (fileList != null) {
            for (File f : fileList) {
                if (f.isFile()) {
                    try (BufferedReader reader = new BufferedReader(new FileReader(f))) {
                        String line;
                        int lineNumber = 0;
                        while ((line = reader.readLine()) != null) {
                            lineNumber++;
                            if (lineNumber > 1) {
                                String[] arr = line.split(",");
                                String newIMSI = IMSIGenerator.generateIMSI(arr[0]);
                                line = line.replace(arr[1], newIMSI);
                            }
                            phoneNumbers.add(line);
                        }
                        writeToFile(wait + "/" + f.getName(), phoneNumbers);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
//                f.delete();
            }
        }
        return false;
    }

    public static void writeToFile(String filePath, List<String> data) {
        File packageFile = new File(filePath);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(packageFile))) {
            // Ghi danh sách vào tập tin
            for (String phoneNumber : data) {
                writer.write(phoneNumber + System.lineSeparator()); // Thêm dòng mới sau mỗi số điện thoại
            }
            System.out.println("OK");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

