package org.studyeasy.SpringStarter.Utils;

import java.nio.file.Paths;

public class AppUtil {
    private static final String BASE_PATH = "C:\\Users\\SANCHIT\\OneDrive\\Desktop\\HostelManagementSystem\\SpringStarter\\src\\main\\resources\\static\\uploads";

    public static String get_upload_path(String fileName) {
        return Paths.get(BASE_PATH, fileName).toString();
    }
}

