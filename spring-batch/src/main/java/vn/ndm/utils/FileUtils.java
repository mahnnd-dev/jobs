package vn.ndm.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.file.Files;

@Slf4j
public class FileUtils {
    public FileUtils() {
        // TODO document why this constructor is empty
    }

    public static String readFile(String file) {
        File f = new File(file);
        if (f.isFile()) {
            return readFile(f);
        }
        return null;
    }

    public static String readFile(File f) {
        try{
            byte[] bytes = Files.readAllBytes(f.toPath());
            return new String(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String readFile2(String f) {
        try (BufferedReader reader = new BufferedReader(new FileReader(f))){
            String line = reader.readLine();
            while (line != null) {
                System.out.println(line);
                line = reader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
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
