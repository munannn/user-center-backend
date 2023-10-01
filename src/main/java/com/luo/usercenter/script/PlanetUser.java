package com.luo.usercenter.script;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * @author 木南
 * @version 1.0
 * @Description TODO
 * @since 2023/10/1 16:37
 */
@Data
public class PlanetUser {

    @ExcelProperty("成员编号")
    private String planetCode;

    @ExcelProperty("成员昵称")
    private String username;

    @ExcelProperty("本月积分")
    private String monthPoint;
}
