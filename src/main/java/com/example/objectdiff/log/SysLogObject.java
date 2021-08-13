package com.example.objectdiff.log;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author hao.wang
 * @date Created in 2021/6/2
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class SysLogObject {


    private String phone;
    private int type;
    private String dbName;
    private long time;
    private String mes;

}
