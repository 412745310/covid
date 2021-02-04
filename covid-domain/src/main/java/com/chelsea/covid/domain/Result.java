package com.chelsea.covid.domain;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 响应实体类
 * 
 * @author shevchenko
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result implements Serializable{
    
    private static final long serialVersionUID = 1L;
    private Object data;
    private Integer code;
    private String message;
    
    public static Result success(Object data) {
        Result result = new Result();
        result.setCode(200);
        result.setMessage("success");
        result.setData(data);
        return result;
    }
    
    public static Result fail() {
        Result result = new Result();
        result.setCode(500);
        result.setMessage("fail");
        result.setData(null);
        return result;
    }

}
