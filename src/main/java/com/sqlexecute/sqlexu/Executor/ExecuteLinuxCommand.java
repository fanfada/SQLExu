package com.sqlexecute.sqlexu.Executor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

@Slf4j
@Service
public class ExecuteLinuxCommand {

    public String executeCommand(String command) {
        if (command == null || command.trim().isEmpty()) {
            return "Invalid command: Command cannot be null or empty.";
        }

        StringBuilder output = new StringBuilder();

        try {
            // 使用 ProcessBuilder 执行命令
            ProcessBuilder processBuilder = new ProcessBuilder();
            processBuilder.command("bash", "-c", command); // 使用 bash -c 来执行命令

            // 启动进程
            Process process = processBuilder.start();

            // 获取命令输出
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }

            // 等待进程结束
            int exitCode = process.waitFor();
            output.append("Command executed with exit code: ").append(exitCode);

        } catch (IOException | InterruptedException e) {
            output.append("Error executing command: ").append(e.getMessage());
        }

        return output.toString();
    }
}
