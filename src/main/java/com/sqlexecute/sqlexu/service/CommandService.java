package com.sqlexecute.sqlexu.service;

import com.sqlexecute.sqlexu.domain.CommandExecution;
import com.sqlexecute.sqlexu.utils.UuidUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@Slf4j
public class CommandService {

    private ExecutorService executorService = Executors.newCachedThreadPool();

    @Getter
    private CommandExecution currentExecution;

    private final Map<String, Process> processMap = new ConcurrentHashMap<>();


    public void stop() {
        // 停止线程池
        executorService.shutdown();
    }

    public CommandExecution startCommandExecution(String command) {
        currentExecution = new CommandExecution(command);
        String taskId = UuidUtil.uuid().substring(0, 7);
        log.info("taskId : {}", taskId);
        currentExecution.setTaskId(taskId);
        executorService.submit(() -> {
            log.info(Thread.currentThread().getName() + " - Task started");
            executeCommand(command, taskId);
            log.info(Thread.currentThread().getName() + " - Task completed");
        });
        return currentExecution;
    }

    private void executeCommand(String command, String taskId) {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder();
            processBuilder.command("bash", "-c", command); // 使用 bash -c 来执行命令

            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();

            processMap.put(taskId, process);

            // Capture the output and update progress
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            StringBuilder outputBuilder = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                // Append the output to our command execution object
//                currentExecution.setOutput(line);
                // Simulate progress (you can update this logic based on your specific needs)
                outputBuilder.append(line).append(System.lineSeparator()); // 保持原来的换行格式
                currentExecution.setOutput(outputBuilder.toString());
            }
            // Wait for the process to complete
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                currentExecution.setStatus("Completed");
            } else {
                currentExecution.setStatus("Failed");
            }
        } catch (IOException | InterruptedException e) {
            currentExecution.setStatus("Failed");
            e.printStackTrace();
        }
    }


    public String stopCommand(String taskId) {
        // 尝试从映射表中找到对应的进程并停止它
        Process process = processMap.remove(taskId);
        if (process != null) {
            process.destroy(); // 或者使用 destroyForcibly() 强制终止
            try {
                process.waitFor();
                return "Stopped task with ID: " + taskId;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return "Interrupted while stopping task with ID: " + taskId;
            }
        } else {
            return "Task not found or already stopped.";
        }
    }
}
