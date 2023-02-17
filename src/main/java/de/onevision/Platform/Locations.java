package de.onevision.Platform;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.lang.System;

public class Locations {
    public static String removeFileExtension(Path path, boolean removeAllExtensions) {
        String filename = path.getFileName().toString();
        if (filename == null || filename.isEmpty()) {
            return filename;
        }
    
        String extPattern = "(?<!^)[.]" + (removeAllExtensions ? ".*" : "[^.]*$");
        return filename.replaceAll(extPattern, "");
    }
    public static Path userTempDir() {
        return Paths.get(Platform.systemTempDir).resolve(Platform.userName);
    }
    public static Path userUniqueTempDir() {
        Integer index = 0;
        Path dir = Paths.get(Platform.systemTempDir).resolve(Platform.userName).resolve("Impose.app").resolve(index.toString());
        while (Files.exists(dir)) {
            ++index;
            dir = Paths.get(Platform.systemTempDir).resolve(Platform.userName).resolve("Impose.app").resolve(index.toString());
        }
        return dir;
    }
    public static Path resources() {
        return Paths.get(resources);
    }
    public static Path currentWorkingDir() {
        return Paths.get(currentWorkingDir).toAbsolutePath();
    }
    public static Path storageBasePath() {
        if(Platform.inWorkspace) {
            return Paths.get(storageBasePath);
        }
        else {
            return resources();
        }
    }
    public static Path ovpdfInfoPath() {
        if(Platform.inWorkspace) {
            return Paths.get(storageBasePath + "\\devices\\AutoImpose.app\\OVPDFInfo.app\\OVPDFInfo.exe");
        }
        else {
            return Paths.get(ovpdfInfoAppLocal);
        }
    }
    public static Path ovpdfImposePath() {
        if(Platform.inWorkspace) {
            return Paths.get(storageBasePath + "\\devices\\AutoImpose.app\\OVPDFImpose.app\\OVPDFImpose.exe");
        }
        else {
            return Paths.get(ovpdfImposeAppLocal);
        }
    }

    static private final String currentWorkingDir = ".";
    static private final String storageBasePath = System.getenv("OV_STORAGE_BASEPATH");
    static private final String resources = "D:\\projects\\jsf-demo\\src\\main\\resources";
    static private final String ovpdfInfoAppLocal = "D:\\projects\\jsf-demo\\src\\main\\resources\\OVPDFInfo.app\\OVPDFInfo.exe";
    static private final String ovpdfImposeAppLocal = "D:\\projects\\jsf-demo\\src\\main\\resources\\OVPDFImpose.app\\OVPDFImpose.exe";
}
