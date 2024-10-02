package com.example.gym.controller.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JSONResponse {

    private boolean success;
    private Object body;
    private Integer code;
    private String[] messages;

    public JSONResponse(boolean pSuccess, Object pBody, Integer pCode, String... pMessages) {
        super();
        success = pSuccess;
        body = pBody;
        messages = pMessages;
        code = pCode;
    }

    public JSONResponse(boolean pSuccess, Object pBody, String... pMessages) {
        super();
        success = pSuccess;
        body = pBody;
        messages = pMessages;
        code = null;
    }

    public JSONResponse(boolean pSuccess, String... pMessages) {
        super();
        success = pSuccess;
        messages = pMessages;
        code = null;
    }

    public JSONResponse(boolean pSuccess, Integer pCode, String... pMessages) {
        super();
        success = pSuccess;
        code = pCode;
        messages = pMessages;
    }

    public JSONResponse(boolean pSuccess, Object pBody, Integer pCode) {
        super();
        success = pSuccess;
        body = pBody;
        code = pCode;
    }
}
