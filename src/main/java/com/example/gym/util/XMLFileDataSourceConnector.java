package com.example.gym.util;

import org.springframework.core.io.ClassPathResource;

import java.io.File;

public class XMLFileDataSourceConnector {
    protected String XMLDocPath;
    protected File inputFile;

    public XMLFileDataSourceConnector(String xMLFilePath) {
        XMLDocPath = xMLFilePath;
    }

    public File getXMLFile() throws Exception {
        ClassPathResource resource = new ClassPathResource(XMLDocPath);
        if (inputFile == null)
            inputFile = resource.getFile();
        return inputFile;
    }
}
