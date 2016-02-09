package tasks;

import org.apache.commons.io.FileUtils;
import play.Logger;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

public class WebjarsFilesCopier {
    private static final Logger.ALogger LOGGER = Logger.of(WebjarsFilesCopier.class);
    private static final String WEBJARS_PATH = "META-INF/resources/webjars/";
    private static final FileSystem FILE_SYSTEM = FileSystems.getDefault();

    public static void main(String[] args) {
        if (args.length > 1) {
            final String destBasePath = args[0];
            final List<String> filesToCopy = Arrays.asList(args).subList(1, args.length);
            copyTemplateFiles(destBasePath, filesToCopy);
        } else {
            throw new RuntimeException("Missing arguments, required at least one origin path");
        }
    }

    static void copyTemplateFiles(final String destBasePath, final List<String> filesToCopy) {
        filesToCopy.forEach(fileToCopy -> {
            final String origFilePath = WEBJARS_PATH + fileToCopy;
            final URL origResource = Thread.currentThread().getContextClassLoader().getResource(origFilePath);
            if (origResource != null) {
                final File origFile = new File(origResource.getPath());
                final Path destPath = FILE_SYSTEM.getPath(destBasePath, fileToCopy);
                copyFile(origFile, destPath.getParent());
            } else {
                LOGGER.error("Could not find file \"{}\" in classpath", origFilePath);
            }
        });
    }

    private static void copyFile(final File origFile, final Path destPath) {
        LOGGER.debug(origFile + " => " + destPath);
        try {
            FileUtils.copyFileToDirectory(origFile, destPath.toFile());
            LOGGER.info("Successfully created \"{}\"", destPath);
        } catch (FileAlreadyExistsException e) {
            LOGGER.error("File \"{}\" already exists, please delete it first if you want to replace it", destPath);
        } catch (IOException e) {
            LOGGER.error("Could not copy file to \"{}\"", destPath, e);
        }
    }
}
