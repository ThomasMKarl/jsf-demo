package de.onevision.Platform;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.lang.System;

public class Locations {
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

    static private final String currentWorkingDir = ".";
    static private final String storageBasePath = System.getenv("OV_STORAGE_BASEPATH");
    static private final String resources = "D:\\jsf-demo\\src\\main\\resources";
}
