package com.example.gym.util;

public class JSONResourceFileDataSourceConnector extends JSONFileDSConnector {
    private static final String BASE_PATH = "";

    public JSONResourceFileDataSourceConnector(String docName) {
        super(BASE_PATH + docName);
    }

}