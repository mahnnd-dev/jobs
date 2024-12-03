package vn.ndm.jobdatabase.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileWriter;

@Slf4j
public class FileUtils {
    private FileUtils() {
    }

    public static void writeToFile(String filePath, String data) {
        File packageFile = new File(filePath);
        try (FileWriter writer = new FileWriter(packageFile)){
            // Tạo file và ghi DDL vào file
            writer.write(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void folderIsExist(String pathFolder) {
        File file = new File(pathFolder);
        boolean c = file.mkdirs();
        if (!c) log.info("Folder {} exist!", file.getName());
        log.info("Create folder {} success!", file.getName());
    }
}
