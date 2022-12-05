package de.onevision.Platform;

import java.lang.System;
import org.apache.logging.log4j.*;

public class Platform {
    static public void log() {
        logger.debug("Java Class Path: " + javaClassPath);
        logger.debug("Java Home: " + javaHome);
        logger.debug("Java Vendor: " + javaVendor);
        logger.debug("Java Vendor URL: " + javaVendorUrl);
        logger.debug("Java Version: " + javaVersion);

        logger.debug("OS Architecture: " + osArch);
        logger.debug("OS Name: " + osName);
        logger.debug("OS Version: " + osVersion);

        logger.debug("User Directory: " + userDir);
        logger.debug("User Home: " + userHome);
        logger.debug("User Name: " + userName);
    }

    static public final String fileSeparator = System.getProperty("file.separator");
    static public final String pathSeparator = System.getProperty("path.separator");
    static public final String lineSeparator = System.getProperty("line.separator");

    static public final String javaClassPath = System.getProperty("java.class.path");
    static public final String javaHome = System.getProperty("java.home");
    static public final String javaVendor = System.getProperty("java.vendor");
    static public final String javaVendorUrl = System.getProperty("java.vendor.url");
    static public final String javaVersion = System.getProperty("java.version");

    static public final String osArch = System.getProperty("os.arch");
    static public final String osName = System.getProperty("os.name");
    static public final String osVersion = System.getProperty("os.version");

    static public final String userDir = System.getProperty("user.dir");
    static public final String userHome = System.getProperty("user.home");
    static public final String userName = System.getProperty("user.name");

    static public final boolean inWorkspace = System.getenv("OV_STORAGE_BASEPATH") != null;
    static public final boolean isWindows = osName.contains("Windows");
    static public final boolean isLinux = !isWindows;

    static public final String appExt = isWindows ? ".exe" : "";

    private static Logger logger = LogManager.getLogger(Platform.class.getName());
}