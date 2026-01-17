package com.example.ai_kanban.common.exception;

import com.example.ai_kanban.common.ResultCode;
import lombok.Getter;

@Getter
public class SystemException extends RuntimeException{
    private int code;

    public SystemException(String message,int code){
        super(message);
        this.code=code;
    }

    public SystemException(ResultCode result){
        super(result.getMessage());
        this.code=result.getCode();
    }

    public SystemException(String message){
        super(message);
    }
}
