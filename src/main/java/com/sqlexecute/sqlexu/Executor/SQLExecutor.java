package com.sqlexecute.sqlexu.Executor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

@Slf4j
@Service
public class SQLExecutor {

    // 数据库连接的URL、用户名和密码（根据你的实际情况修改）
    private static final String MYSQL_URL = "jdbc:mysql://127.0.0.1:3306/yatdb?characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai&allowMultiQueries=true&allowPublicKeyRetrieval=true";
    private static final String MYSQL_USER = "root";
    private static final String MYSQL_PASSWORD = "Saas@9883";

    private static final String POSTGRES_URL = "jdbc:postgresql://localhost:5432/your_database";
    private static final String POSTGRES_USER = "postgres";
    private static final String POSTGRES_PASSWORD = "password";

    public void start(String dbType, String sqlFilePath) {

        try {
            // 执行 SQL 文件并计算时间
            long startTime = System.nanoTime();
            executeSQLFile(dbType, sqlFilePath);
            long endTime = System.nanoTime();

            long duration = endTime - startTime;  // 执行时间（纳秒）
            log.info("SQL执行时间: " + duration / 1_000_000 + " 毫秒");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 根据数据库类型执行SQL文件
    public void executeSQLFile(String dbType, String sqlFilePath) throws SQLException, IOException {
        Connection connection = null;
        Statement stmt = null;
        ResultSet rs = null;
//        List<String> sqlStatements = readSQLFile(sqlFilePath);
        String sql = new String(Files.readAllBytes(Paths.get(sqlFilePath)));

        // 分割 SQL 文件为单独的语句
        String[] sqlStatements = sql.split("(?<=;)(?=[ \\t\\n\\x0B\\f\\r]*)");

        try {
            // 选择数据库并建立连接
            if (dbType.equalsIgnoreCase("mysql")) {
                connection = DriverManager.getConnection(MYSQL_URL, MYSQL_USER, MYSQL_PASSWORD);
            } else if (dbType.equalsIgnoreCase("postgres")) {
                connection = DriverManager.getConnection(POSTGRES_URL, POSTGRES_USER, POSTGRES_PASSWORD);
            } else {
                throw new IllegalArgumentException("Unsupported database type: " + dbType);
            }

            stmt = connection.createStatement();

            // 执行SQL语句
            for (String statement : sqlStatements) {
                statement = statement.trim();
                if (!statement.isEmpty()) {
                    log.info("Executing SQL: " + statement);
                    executeStatement(stmt, statement, dbType);
                }
            }


            log.info("SQL文件执行完毕！");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 执行单条 SQL 语句并打印结果
     *
     * @param stmt   Statement 对象
     * @param sql    SQL 语句
     * @param dbType 数据库类型（MySQL 或 PostgreSQL）
     */
    public void executeStatement(Statement stmt, String sql, String dbType) throws SQLException {
        if (sql.toUpperCase().startsWith("SELECT")) {
            // 执行查询语句
            ResultSet rs = stmt.executeQuery(sql);
            String resultString = formatQueryResult(rs, dbType);
            System.out.println("Query Results from " + dbType + ":");
            System.out.println(resultString);
        } else {
            // 执行更新、插入、删除等语句
            int rowsAffected = stmt.executeUpdate(sql);
            log.info("Rows affected: " + rowsAffected);
        }
    }

    /**
     * 格式化查询结果为字符串
     *
     * @param rs     ResultSet 对象
     * @param dbType 数据库类型（MySQL 或 PostgreSQL）
     * @return 格式化后的查询结果字符串
     * @throws SQLException
     */
    public String formatQueryResult(ResultSet rs, String dbType) throws SQLException {
        StringBuilder result = new StringBuilder();

        // 获取列数
        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();

        // 添加表头
        for (int i = 1; i <= columnCount; i++) {
            result.append(metaData.getColumnLabel(i)).append("\t");
        }
        result.append("\n");

        // 遍历查询结果
        while (rs.next()) {
            for (int i = 1; i <= columnCount; i++) {
                result.append(rs.getString(i)).append("\t");
            }
            result.append("\n");
        }

        return result.toString();
    }

    // 读取SQL文件中的SQL语句
    public List<String> readSQLFile(String filePath) throws IOException {
        List<String> sqlStatements = new ArrayList<>();
        StringBuilder sqlBuffer = new StringBuilder();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("--") || line.isEmpty()) {
                    continue; // 跳过注释和空行
                }

                sqlBuffer.append(line).append(" "); // 拼接SQL语句

                if (line.endsWith(";")) { // 如果语句以分号结束，认为是完整的SQL
                    sqlStatements.add(sqlBuffer.toString().trim());
                    sqlBuffer.setLength(0); // 重置缓冲区
                }
            }
        }
        return sqlStatements;
    }
}
