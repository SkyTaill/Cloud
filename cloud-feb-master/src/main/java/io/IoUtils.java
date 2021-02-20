package io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;

public class IoUtils {

    public static void main(String[] args) throws URISyntaxException, IOException {
        File f1 = new File("logo.gif");
        File copy = new File("data/logo.gif");
        System.out.println(f1.exists());
        System.out.println(copy.exists());
        InputStream is = new FileInputStream(f1);
        OutputStream os = new FileOutputStream(copy, true);
        int ptr = 0;
        byte[] buffer = new byte[8192];
        while ((ptr = is.read(buffer)) != -1) {
            os.write(buffer, 0, ptr);
        }
        os.close();
        is.close();
    }
}
