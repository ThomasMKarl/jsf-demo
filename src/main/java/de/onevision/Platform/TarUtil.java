package de.onevision.Platform;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import de.onevision.Platform.Exceptions.Error;

import org.apache.commons.io.IOUtils;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;

public class TarUtil {
  public static void extractTar(Path pathFrom, Path pathTo) throws Error {
    TarArchiveInputStream tarFile;
    try {
        tarFile = new TarArchiveInputStream(new FileInputStream(new File(pathFrom.toString())));
    }
    catch(FileNotFoundException e) {
        throw new Error("cannot read " + pathFrom, "", "FileNotFoundException");
    }

    try {
        Files.createDirectories(pathTo);
    }
    catch(IOException e) {
        throw new Error("cannot create temporary directory", "", "IOException");
    }

    TarArchiveEntry entry;
    try {
        while ((entry = tarFile.getNextTarEntry()) != null) {
            var filename = entry.getName();
            var content = new byte[(int) entry.getSize()];
            var offset = 0;
            tarFile.read(content, offset, content.length - offset);
            if(entry.isDirectory()) {
                Files.createDirectory(pathTo.resolve(filename));
            }
            else {
                var outputFile = new FileOutputStream(new File(pathTo.toString() + "/" + filename));
                IOUtils.write(content, outputFile);              
                outputFile.close();
            }
        }
    
        tarFile.close();
    }
    catch(IOException e) {
        throw new Error("cannot read " + pathFrom, "", "IOException");
    }
  }

  public static void packTar(ArrayList<Path> pathsFrom, Path pathTo) throws Error {
    OutputStream out;
    try {
      out = Files.newOutputStream(pathTo);
    } catch (IOException e) {
      throw new Error("cannot write to " + pathTo, "", "IOException");
    }

    var buffOut = new BufferedOutputStream(out);
    var tarOut = new TarArchiveOutputStream(buffOut);
    for (Path path : pathsFrom) {
      if (!Files.exists(path)) {
        continue;
      }

      var tarEntry = new TarArchiveEntry(path.toFile(), path.getFileName().toString());
      try {
        tarOut.putArchiveEntry(tarEntry);
        Files.copy(path, tarOut);
        tarOut.closeArchiveEntry();
      } catch (IOException e) {
        throw new Error("cannot write to " + pathTo, "", "IOException");
      }
    }

    try {
      tarOut.finish();
    } catch (IOException e) {
      throw new Error("cannot write to " + pathTo, "", "IOException");
    }
  }
}
