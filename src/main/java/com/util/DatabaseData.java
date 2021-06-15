package com.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

public class DatabaseData {
    private static String username = "";
    private static String password = "";
    private static String connectionURL = "";

    public static void init() {
        // https://pclevinblog.pixnet.net/blog/post/314563210-%5Bjava%5Dstring-%E5%AD%97%E4%B8%B2%E6%90%9C%E5%B0%8B%E7%9A%84%E6%96%B9%E6%B3%95%EF%BC%9A-indexof-%E3%80%81-lastindexo
        File realPath = new File(".\\src\\main\\resources");
        File settingFile = null;
        FileInputStream fileInputStream = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader bufferedReader = null;
        String inputText = "";
        String resultText = "";

        try {
            settingFile = new File(realPath.getCanonicalPath() + "\\DatabaseData.txt");
            fileInputStream = new FileInputStream(settingFile);
            inputStreamReader = new InputStreamReader(fileInputStream, "ASCII");
            bufferedReader = new BufferedReader(inputStreamReader);
            while ((inputText = bufferedReader.readLine()) != null) {
                int index = inputText.indexOf("=");
                resultText = inputText.substring(index + 1, inputText.length());
                resultText = resultText.trim();

                String[] splitArray = inputText.split("=");

                if ("username".equals(splitArray[0])) {
                    username = resultText;
                } else if ("password".equals(splitArray[0])) {
                    password = resultText;
                } else if ("connectionURL".equals(splitArray[0])) {
                    connectionURL = resultText;
                }
            }
            // System.out.println("username " + username);
            // System.out.println("password " + password);
            // System.out.println("connectionURL " + connectionURL);
            System.out.println("init DatabaseData config successful");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                    bufferedReader = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (inputStreamReader != null) {
                try {
                    inputStreamReader.close();
                    inputStreamReader = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                    fileInputStream = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static String getUsername() {
        return username;
    }

    public static String getPassword() {
        return password;
    }

    public static String getConnectionURL() {
        return connectionURL;
    }
}