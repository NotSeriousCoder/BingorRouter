package com.bingor.java_test;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by Bingor on 2019/8/16.
 */
public class FileTest {
    public static void main(String[] args) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        FileReader fr = new FileReader(new File("D:\\abc\\Keys.java"));
        System.out.println("file exist == " + new File("D:\\abc\\Keys.java").exists());
        BufferedReader br = new BufferedReader(fr);
        String line = "";
        br.mark(100);
        do {
            // Key.java 里面已经有的key，不要重复写入，需要从keys里面去掉
            String delKey = null;
            line = br.readLine();
        } while (!line.contains("----------------- File end -----------------"));
        br.reset();
        do {
            line = br.readLine();
            baos.write(line.getBytes());
            baos.write("\r\n".getBytes());
            if (line.contains("----------------- Keys is under here -----------------")) {
                baos.write(("   public static final String " + "AAA" + " = \"" + "BBB" + "\";").getBytes());
                baos.write("\r\n".getBytes());
            }
        } while (!line.contains("----------------- File end -----------------"));

        if (br != null) {
            br.close();
            br = null;
        }
        if (fr != null) {
            fr.close();
            fr = null;
        }
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        FileOutputStream fos = new FileOutputStream(new File("D:\\abc\\Keys.java"));
        byte[] data = new byte[1024];
        int len = 0;
        while ((len = bais.read(data)) != -1) {
            fos.write(data, 0, len);
            fos.flush();
        }
        if (fos != null) {
            fos.flush();
            fos.close();
        }
        if (bais != null) {
            bais.close();
        }
        if (baos != null) {
            baos.flush();
            baos.close();
        }
    }
}
