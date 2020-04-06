package tss.t5.service;

import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;

/**
 * Class contains methods for get necessary properties from file.
 */
public class Property {
    private static Logger logger = Logger.getLogger(Property.class);
    private static String fileName = "properties.txt";
    private static String path;

    static {
        getFilepath();
    }

    /**
     * Get port from property file.
     *
     * @return Port
     */
    public static int getPort() {
        return Integer.parseInt(getValuesFromFile("port:"));
    }

    /**
     * Get dataBase name from property file.
     *
     * @return Database name
     */
    public static String getDataBaseName() {
        return getValuesFromFile("database:");
    }

    /**
     * Get path for currently directory from property file.
     *
     * @return Path
     */
    public static String getPath() {
        return path;
    }

    /**
     * Get username for email from property file.
     *
     * @return username
     */
    public static String getEmailUsername() {
        return getValuesFromFile("emailUsername:");
    }

    /**
     * Get password for email from property file.
     *
     * @return password
     */
    public static String getEmailPassword() {
        return getValuesFromFile("emailPassword:");
    }

    /**
     * Get email subject for email from property file.
     *
     * @return email subject
     */
    public static String getEmailSubject() {
        return getValuesFromFile("emailSubject:");
    }

    /**
     * Get restore text for email from property file.
     *
     * @return restore text
     */
    public static String getEmailRestoreText() {
        return getValuesFromFile("emailRestoreText:");
    }

    /**
     * Get registration text for email from property file.
     *
     * @return registration text
     */
    public static String getEmailTextRegistration() {
        return getValuesFromFile("emailTextRegistration:");
    }

    private static void getFilepath() {
        if (getPathForLinux()) {
        } else {
            getPathForWindows();
        }
    }

    private static String getValuesFromFile(String value) {
        logger.info(path + fileName);
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(
                    path + fileName));
            String line = reader.readLine();
            String tempValue = null;
            while (line != null) {
                tempValue =parseLine(line, value);
                if(tempValue != null){
                    reader.close();
                    return tempValue;
                }
                line = reader.readLine();
            }
            reader.close();
            logger.error("file does not contains value " + value);
        } catch (IOException e) {
            logger.info(e);
        }
        return null;
    }

    private static String parseLine(String line, String value) {
        if (line.contains(value)) {
            String parsedValue = line.replace(value, " ").trim();
            logger.info(line + " " + value + " " + parsedValue);
            return parsedValue;
        }
        return null;
    }

    private static boolean getPathForLinux() {
        path = Paths.get(".").toAbsolutePath().normalize().toString() + "/";
        File f = new File(path + fileName);
        if (f.exists() && !f.isDirectory()) {
            logger.info("getPathForLinux exists");
            System.out.println("getPathForLinux exists");
            return true;
        }
        return false;
    }

    private static void getPathForWindows() {
        path = Paths.get(".").toAbsolutePath().normalize().toString() + "\\";
        System.out.println(path);
        File f = new File(path + fileName);
        if (f.exists() && !f.isDirectory()) {
            System.out.println("getPathForWindows exists");
            logger.info("getPathForWindows exists");
        } else {
            System.out.println("getPathForWindows not exists");
            logger.info("getPathForWindows not exists");
        }
    }
}
