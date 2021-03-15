package com.leyou.common.utils;

import java.util.Random;

/**
 * @author 26747
 * @description NumberUtils
 * @date 2020/6/3 19:32
 */
public class NumberUtils {

    /**
     * 生成指定位数的随机数字
     *
     * @param len 随机数的位数
     * @return 生成的随机数
     */
    public static String generateCode(int len) {
        //随机数的位数不大于8位
        len = Math.min(len, 8);
        //设定最小值为10的len-1次方，如len=6，min=100000，即最小的len位数字
        int min = Double.valueOf(Math.pow(10, len - 1)).intValue();
        //生成一个随机数为[0,9999999)之间+min即[100000-10099999)之间,不能有以0开头的随机数，会不足len位
        int num = new Random().nextInt(Double.valueOf(Math.pow(10, len + 1)).intValue() - 1) + min;
        //从字符串下标0开始截取截取到下标len之前，保证都是len位有效数字
        return String.valueOf(num).substring(0, len);
    }
}
