package com.luo.usercenter.script;

import com.alibaba.excel.EasyExcel;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author 木南
 * @version 1.0
 * @Description 导入Excel的数据
 * @since 2023/10/1 17:01
 */
public class ImportExcel {

    public static void main(String[] args) {
        String excelName = "F:\\code\\user-center\\src\\main\\resources\\testExcel.xlsx";
        ListenerRead(excelName);
        synchronousRead(excelName);
    }

    /**
     * 监听器读取，先创建监听器，再读取文件时绑定监听器。一条一条处理数据，适用于数据量大的场景
     *
     * @param excelName
     */
    public static void ListenerRead(String excelName) {
        EasyExcel.read(excelName, PlanetUser.class, new DataListener()).sheet().doRead();
    }

    /**
     * 同步读 无需创建监听器，一次性获取完整数据。数据量大时会有等待时间，有内存溢出风险
     *
     * @param excelName
     */
    public static void synchronousRead(String excelName) {
        // 这里 需要指定读用哪个class去读，然后读取第一个sheet 文件流会自动关闭
        List<PlanetUser> userInfoList =
                EasyExcel.read(excelName).head(PlanetUser.class).sheet().doReadSync();
        System.out.println("总数 = " + userInfoList.size());
        Map<String, List<PlanetUser>> userMap = userInfoList.stream().filter(user ->
                StringUtils.isNotEmpty(user.getUsername())
        ).collect(Collectors.groupingBy(PlanetUser::getUsername));

    }
}
