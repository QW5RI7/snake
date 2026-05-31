package com.test.swing.snake;

import java.util.ArrayList;
import java.util.Random;

/**
 * 生成随机验证码工具类
 * 负责生成随机的验证码，验证码包含4个随机字母和1个随机数字
 * 排除字母O、I、L，数字0,1
 * @author 王鑫
 * @version 1.0
 * @since 2026-5-23
 */
public class CodeUtil{
    /**
     * 生成随机验证码
     * @return 随机验证码
     */
    public static String getCode(){
        //1.创建一个集合
        ArrayList<Character> list = new ArrayList<>();//52  索引的范围：0 ~ 51
        //2.添加字母 a - z  A - Z
        for (int i = 0; i < 26; i++) {
            if (i == 'O' - 'A' || i == 'I' - 'A' || i == 'L' - 'A'){
                continue;
            }
            list.add((char)('a' + i));//a - z
            list.add((char)('A' + i));//A - Z
        }
        //3.打印集合
        //System.out.println(list);
        //4.生成4个随机字母
        StringBuilder result = new StringBuilder();
        Random r = new Random();
        int len = r.nextInt(1) + 4;
        for (int i = 0; i < len; i++) {
            //获取随机索引
            int randomIndex = r.nextInt(list.size());
            char c = list.get(randomIndex);
            result.append(c);
        }
        //System.out.println(result);//长度为4的随机字符串

        //5.在后面拼接数字 0~9
        int[] numbers = {2,3,4,5,6,7,8,9};
        int number = numbers[r.nextInt(numbers.length)];
        //6.把随机数字拼接到result的后面
        result.append(number);
        //System.out.println(result);//ABCD5
        //7.把字符串变成字符数组
        char[] chars = result.toString().toCharArray();//[A,B,C,D,5]
        //8.在字符数组中生成一个随机索引
        int index = r.nextInt(chars.length);
        //9.拿着最后一个索引上的数字，跟随机索引上的数字进行交换
        char temp = chars[chars.length - 1];
        chars[chars.length - 1] = chars[index];
        chars[index] = temp;
        //10.把字符数组再变回字符串
        //System.out.println(code);
        return new String(chars);
    }
}
