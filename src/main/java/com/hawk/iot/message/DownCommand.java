package com.hawk.iot.message;

import lombok.Data;

import java.io.Serializable;

/**
 * @program: springboot3-tk-data-auth
 * @description:
 * @author: zhb
 * @create: 2025-06-28 07:23
 */
@Data
public class DownCommand implements Serializable {
    
    private String tid;
    private String cmd;
}
