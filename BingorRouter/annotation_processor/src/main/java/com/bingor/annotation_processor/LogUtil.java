package com.bingor.annotation_processor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by HXB on 2018/7/17.
 */
public class LogUtil {
    public static FileOutputStream fos;

    public static void log(String content) {
        if (fos == null) {
            File file = makeFile(0);
            try {
                fos = new FileOutputStream(file, true);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        if (fos == null) {
            return;
        }
        try {
            fos.write(content.getBytes());
            fos.write("\r\n".getBytes());
            fos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void close() {
        if (fos != null) {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void clear() {
        if (fos == null) {
            int r = (int) (Math.random() * 100);
            File file = makeFile(r);
            try {
                fos = new FileOutputStream(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        if (fos == null) {
            return;
        }
        try {
            fos.write("".getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void clear(String name) {
        if (fos == null) {
            File file = makeFile(name);
            try {
                fos = new FileOutputStream(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        if (fos == null) {
            return;
        }
        try {
            fos.write("".getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static File makeFile(int r) {
        return makeFile("log" + r + ".txt");
    }

    public static File makeFile(String name) {
        File file = new File("D:/abc");
        if (!file.exists()) {
            file.mkdirs();
        }
        file = new File(file, name + ".txt");
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }
}
