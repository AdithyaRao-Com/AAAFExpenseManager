package com.adithya.aaafexpensemanager.settings.importExportDatabase;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/** @noinspection CallToPrintStackTrace*/
public class LockedFileReplace {
    public static void replaceLockedFile(Path sourceFilePath, Path destinationFilePath) throws IOException {

        try (FileOutputStream destinationFileOutputStream = new FileOutputStream(destinationFilePath.toFile());
             FileChannel destinationChannel = destinationFileOutputStream.getChannel();
             FileLock lock = destinationChannel.lock(); // Lock the destination file
             FileInputStream sourceFileInputStream = new FileInputStream(sourceFilePath.toFile());
             FileChannel sourceChannel = sourceFileInputStream.getChannel()) {

            // Clear the destination file (truncate to zero length)
            destinationChannel.truncate(0);

            // Transfer data from the source file to the destination file
            sourceChannel.transferTo(0, sourceChannel.size(), destinationChannel);

            System.out.println("Locked file replaced successfully.");

        } catch (IOException e) {
            System.err.println("Error replacing locked file: " + e.getMessage());
            throw e; // Rethrow to allow calling code to handle
        }
    }

    public static void main(String[] args) {
        Path sourceFile = Paths.get("newFile.txt"); // Replace with your source file
        Path destinationFile = Paths.get("lockedFile.txt"); // Replace with your destination file

        try {
            //Create dummy files for testing
            Files.write(sourceFile, "This is the new content".getBytes());
            Files.write(destinationFile, "This is the old content".getBytes());

            replaceLockedFile(sourceFile, destinationFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}