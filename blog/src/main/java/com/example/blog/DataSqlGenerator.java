package com.example.blog;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class DataSqlGenerator {

    public static void main(String[] args) {
        int totalRecords = 100000;  // 100만 건
        String fileName = "data.sql";

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            writer.write("BEGIN;\n");

            Random rand = new Random();

            // 색상 옵션 배열
            String[] colors = {"Green", "Purple"};

            // 데이터 삽입
            for (int i = 1; i <= totalRecords; i++) {
                String color = colors[rand.nextInt(colors.length)];
                writer.write(String.format("INSERT INTO broccoli (color) VALUES ('%s');\n", color));

                // 1000건마다 커밋
                if (i % 1000 == 0) {
                    writer.write("COMMIT;\nBEGIN;\n");
                }
            }

            writer.write("COMMIT;\n");
            System.out.println("data.sql 파일이 생성되었습니다.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

