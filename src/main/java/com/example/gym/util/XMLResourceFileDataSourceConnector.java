package com.example.gym.util;

import org.springframework.core.io.ClassPathResource;

import java.io.File;

public class XMLResourceFileDataSourceConnector extends XMLFileDataSourceConnector {
    //private static String BASE_PATH = "";

    public XMLResourceFileDataSourceConnector(String fileName) {
        super(fileName);
    }

    public File getXMLFile() throws Exception {
        ClassPathResource resource = new ClassPathResource(XMLDocPath);
        if (inputFile == null) {
            //String resourceJSONFilePath = BASE_PATH + XMLDocPath;
            //System.out.println(">>>>>>> " + resourceJSONFilePath);
            //inputFile = new File(resourceJSONFilePath);
            inputFile = resource.getFile();
        }
        return inputFile;
    }

}
