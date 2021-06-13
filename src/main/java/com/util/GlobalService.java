package com.util;

import javax.servlet.http.Part;

//這個類別提供公共公開靜態的服務方法
public class GlobalService {

    public static boolean stringIncludes(String originalString, String targetString) {
        // 判斷 originalString字串 裡面是否有包含 targetString字串。
        if (originalString.indexOf(targetString) == -1) {
            return false;
        } else {
            return true;
        }
    }

    public static String revertFileName(String fileName) {
        // 把檔案名稱前面的時間戳記去除掉。
        // 還原回原始檔案名稱。
        int pointIndex = fileName.indexOf("_");
        int maxLength = fileName.length();
        fileName = fileName.substring(pointIndex + 1, maxLength);
        System.out.println("revertFileName pointIndex = " + pointIndex);
        return fileName;
    }

    public static String adjustFileName(String fileName, int maxLength) {
        // 修改含有副檔名的檔案名稱長度
        int length = fileName.length();
        if (length <= maxLength) {
            return fileName;
        }
        int pointIndex = fileName.lastIndexOf(".");
        int endSubLength = fileName.length() - pointIndex - 1;
        fileName = fileName.substring(0, maxLength - 1 - endSubLength) + "." + fileName.substring(pointIndex + 1);
        return fileName;
    }// end of adjustFileName() method

    public static String adjustTitleName(String titleName, int maxLength) {
        // 修改標題長度
        int length = titleName.length();
        if (length <= maxLength) {
            return titleName;
        }
        titleName = titleName.substring(0, maxLength);
        return titleName;
    }// end of adjustTitleName() method

    public static synchronized String getTimeStampStr() {
        // 回傳當前的時間戳記
        return new java.sql.Timestamp(System.currentTimeMillis()).toString();
    }

    public static String getFileName(Part part) {
        // 取得使用者上傳檔案名稱
        System.out.println("part.getHeader(\"content-disposition\") -> " + part.getHeader("content-disposition"));
        String[] content = part.getHeader("content-disposition").split(";");
        int length = content.length;
        String result = "";
        for (int i = 0; i < length; i++) {
            result = String.format("content[%d] -> [%s]", i, content[i]);
            System.out.println(result);
        }
        for (int i = 0; i < length; i++) {
            if (content[i].trim().startsWith("filename")) {
                return content[i].substring(content[i].indexOf('=') + 1).trim().replace("\"", "");
            }
        }
        return null;
    }
}
