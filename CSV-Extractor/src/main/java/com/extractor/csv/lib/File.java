package com.extractor.csv.lib;

import java.io.FileOutputStream;

import org.springframework.core.io.FileSystemResource;

public class File {
    FileOutputStream fo;

    public FileSystemResource CreateTmp(String name, String data) {
        String filePath = "/tmp/" + name;

        try {
            fo = new FileOutputStream(filePath);
            fo.write(data.getBytes());
            fo.close();
        } catch (Exception e) {
            return new FileSystemResource("fail to create the file");
        }
        return new FileSystemResource(filePath);
    }
}