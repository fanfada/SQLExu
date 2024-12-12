package com.sqlexecute.sqlexu.utils;

import java.util.UUID;

public class UuidUtil {

    public static String uuid() {
        return UUID.randomUUID().toString();
    }

    // 生成以"user_"开头，总长度为8位的随机字符串
    public static String generateRandomString() {
        // 生成一个UUID（通常是32个字符的16进制数字）
        String uuid = UUID.randomUUID().toString().replace("-", "");

        // 只取UUID的前3个字符，拼接到"user_"后
        String randomString = "user_" + uuid.substring(0, 5);

        // 返回生成的随机字符串
        return randomString;
    }

    public static void main(String[] args) {
        // 生成以"user_"开头的8位随机字符串
        String randomString = generateRandomString();

        // 输出随机字符串
        System.out.println("随机生成的字符串: " + uuid());
    }
}
