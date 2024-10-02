package com.example.gym.util;

import org.springframework.core.io.ClassPathResource;

import java.io.File;

public class JSONFileDSConnector {

    protected String JSONDocPath;
    protected File inputFile;

    public JSONFileDSConnector(String docName) {
        this.JSONDocPath = docName;
    }

    public File getJSONDoc() throws Exception {
        if (inputFile == null) {
            inputFile = new ClassPathResource(JSONDocPath).getFile();
        }
        return inputFile;
    }

}
