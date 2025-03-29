package com.adithya.aaafexpensemanager.util;

import android.content.Context;
import android.net.Uri;
import android.provider.DocumentsContract;
import android.util.Log;

import androidx.documentfile.provider.DocumentFile;

public class UriUtils {
    private static final String TAG = "UriUtils";

    /**
     * Adds a file name to a directory URI.
     *
     * @param directoryUri The URI of the directory.
     * @param fileName     The name of the file to add.
     * @return A new URI representing the file within the directory.
     * @throws IllegalArgumentException if directoryUri is not a directory or if fileName is invalid.
     */
    public static Uri addFileNameToUri(Uri directoryUri, String fileName) {
        if (directoryUri == null) {
            throw new IllegalArgumentException("Directory URI cannot be null.");
        }
        if (fileName == null || fileName.trim().isEmpty()) {
            throw new IllegalArgumentException("File name cannot be null or empty.");
        }

        String directoryPath = directoryUri.toString();

        // Check if the URI represents a directory (ends with '/')
        if (!directoryPath.endsWith("/")) {
            directoryPath += "/"; // Ensure it ends with a slash
        }

        // Add the file name to the directory path
        String filePath = directoryPath + fileName;

        // Create and return the new URI
        return Uri.parse(filePath);
    }

    /**
     * Adds a file name to a directory URI, checking if the uri is a file or a directory.
     * if it is a file, the filename will be appended to the directory the file is in.
     *
     * @param fileUri  The URI of the file or directory.
     * @param fileName The name of the file to add.
     * @return A new URI representing the file within the directory.
     * @throws IllegalArgumentException if fileUri is null or if fileName is invalid.
     */
    public static Uri addFileNameToUriSmart(Uri fileUri, String fileName) {
        if (fileUri == null) {
            throw new IllegalArgumentException("File URI cannot be null.");
        }
        if (fileName == null || fileName.trim().isEmpty()) {
            throw new IllegalArgumentException("File name cannot be null or empty.");
        }

        String filePath = fileUri.toString();

        if (filePath.endsWith("/")) {
            // It's a directory
            return addFileNameToUri(fileUri, fileName);
        } else {
            // It's a file
            int lastSlashIndex = filePath.lastIndexOf('/');
            if (lastSlashIndex != -1) {
                String directoryPath = filePath.substring(0, lastSlashIndex + 1);
                Uri directoryUri = Uri.parse(directoryPath);
                return addFileNameToUri(directoryUri, fileName);
            } else {
                // No directory part, assume it's in the root
                return Uri.parse("/" + fileName);
            }
        }
    }

    /**
     * Converts a tree URI to a document URI.
     *
     * @param treeUri The tree URI to convert.
     * @return The document URI representing the root of the tree, or null if the input is invalid.
     * @throws IllegalArgumentException if treeUri is null.
     */
    public static Uri treeUriToDocumentUri(Uri treeUri) {
        if (treeUri == null) {
            throw new IllegalArgumentException("Tree URI cannot be null.");
        }

        if (DocumentsContract.isTreeUri(treeUri)) {
            return DocumentsContract.buildDocumentUriUsingTree(
                    treeUri,
                    DocumentsContract.getTreeDocumentId(treeUri)
            );
        } else {
            return null; // Not a tree URI
        }
    }

    public static DocumentFile getValidDirectory(Context context, Uri uriDirectory) {
        Uri documentUri = UriUtils.treeUriToDocumentUri(uriDirectory);
        if (documentUri == null) {
            Log.e(TAG, "Invalid tree uri: " + uriDirectory);
            return null;
        }

        DocumentFile dirDocFile = DocumentFile.fromTreeUri(context, documentUri);
        if (dirDocFile == null || !dirDocFile.exists() || !dirDocFile.isDirectory()) {
            Log.e(TAG, "Directory not found or invalid: " + uriDirectory);
            return null;
        }
        return dirDocFile;
    }
}