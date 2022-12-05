package de.onevision.Platform;

import de.onevision.Platform.Exceptions.Error;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class Application {
    public static Application init(Path pathToApp) throws Error {
        return init(pathToApp, Locations.currentWorkingDir());
    }

    public static Application init(Path pathToApp, Path workingDir) throws Error {
        Application app = new Application();
        app.pathToApp = pathToApp;
        app.workingDir = workingDir;

        Path pathToCheck;
        if (!app.pathToApp.isAbsolute()) {
            pathToCheck = workingDir.resolve(pathToApp.toString());
        } else {
            pathToCheck = pathToApp;
        }

        boolean exe = true;
        if (!Files.isExecutable(pathToCheck) || Files.isDirectory(pathToCheck)) {
            exe = false;
        }

        if (!exe) {
            pathToCheck = Path.of(pathToCheck.toString() + ".exe");
        }

        if (!Files.isExecutable(pathToCheck) || Files.isDirectory(pathToCheck)) {
            throw new Error("internal error", "", pathToCheck.toString() + " is not an executable");
        }

        return app;
    }

    public String execute(ArrayList<String> commands, boolean toStdOut) throws Error {
        ArrayList<String> fullCommand = new ArrayList<>();
        fullCommand.add(pathToApp.toString());
        fullCommand.addAll(commands);

        ProcessBuilder builder = new ProcessBuilder(fullCommand);
        builder.directory(workingDir.toFile());

        Process process = null;
        try {
            process = builder.start();
        } catch (IOException e) {
            throw new Error("internal error", "", "builder: IOException");
        }
        InputStream is = process.getInputStream();
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);

        String output = new String();
        String line;
        try {
            while ((line = br.readLine()) != null) {
                output += line;
            }
        } catch (IOException e) {
            throw new Error("internal error", "", pathToApp.toString() + ": IOException");
        }

        if (toStdOut) {
            System.out.print(output);
        }

        return output;
    }

    public void executeAsync(ArrayList<String> commands) {
        asyncHandle = CompletableFuture.supplyAsync(() -> {
            try {
                return this.execute(commands, false);
            } catch (Error e) {
                return "";
            }
        });
    }

    public boolean isDone() {
        if (asyncHandle == null) {
            return true;
        }

        return asyncHandle.isDone();
    }

    public String getAsyncResult() throws Error {
        if (asyncHandle == null) {
            return "";
        }

        while (!isDone()) {
        }

        try {
            return asyncHandle.get();
        } catch (InterruptedException e) {
            return "";
        } catch (ExecutionException e) {
            throw new Error("internal error", "", pathToApp.toString() + ": ExecutionException");
        }
    }

    private Path pathToApp = Locations.currentWorkingDir();
    private Path workingDir = Locations.currentWorkingDir();

    private CompletableFuture<String> asyncHandle = null;
}
