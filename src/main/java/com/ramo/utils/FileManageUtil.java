package com.ramo.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by ramo on 2016/7/22.
 */
public class FileManageUtil {

    public void delete(String filePath) {
        if (isPathValid(filePath)) {
            File f = new File(filePath);
            if (isFileValid(f)) {
                if (f.isDirectory())
                    deleteDir(f);
                else if (f.isFile())
                    deleteFile(f);

            }
        }
    }

    private void deleteFile(File f) {
        f.delete();
    }

    private void deleteDir(File f) {
        File[] files = f.listFiles();
        for (File file : files) {
            delete(file.getPath());
        }
        deleteFile(f);
    }

    private boolean isFileValid(File f) {
        return null != f && f.exists();
    }


    public void move(String filePath, String toPath) {
        copyFile(filePath, toPath);
        delete(filePath);
    }

    public void rename(String filePath, String newName) {
        if (isPathValid(filePath)) {
            File f = new File(filePath);
            String newFilePath = filePath.substring(0, filePath.lastIndexOf("/") + 1) + newName + filePath.substring(filePath.lastIndexOf("."), filePath.length());
            File newFile = new File(newFilePath);
            if (isFileValid(f)) {
                f.renameTo(newFile);
            }
        }
    }

    public void copyFile(String filePath, String toPath) {
        FileInputStream in = null;
        FileOutputStream out = null;
        try {
            if (isPathValid(filePath) && isPathValid(toPath)) {
                File f1 = new File(filePath);
                File toFile = new File(toPath);
                create(toFile);
                if (isFileValid(f1)) {
                    in = new FileInputStream(f1);
                    out = new FileOutputStream(toFile);
                    byte bytes[] = new byte[1024];
                    for (int len = 0;
                         (len = in.read(bytes, 0, bytes.length)) != -1; ) {
                        out.write(bytes, 0, len);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null)
                    in.close();
                if (out != null)
                    out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean create(File toFile) {
        if (!toFile.exists()) {
            try {
                return toFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private boolean isPathValid(String filePath) {
        return null != filePath && filePath.length() > 0;
    }
}
