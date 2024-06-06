package com.hw.core;

public class CommonFile {
    public String fileName;
    public String mimeType;
    public String base64;

    public CommonFile(String fileName, String mimeType, String base64) {
        this.fileName = fileName;
        this.mimeType = mimeType;
        this.base64 = base64;
    }
}
