package com.sqlexecute.sqlexu.controller;

import com.sqlexecute.sqlexu.Executor.ExecuteLinuxCommand;
import com.sqlexecute.sqlexu.domain.CommandRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author fanfada@cmss.chinamobile.com
 * @date 2024/12/10 18:59
 */
@Slf4j
@RestController
@RequestMapping("/api")
public class CommandController {

    @Autowired
    private ExecuteLinuxCommand executeLinuxCommand;

    private final Map<String, Process> processMap = new ConcurrentHashMap<>();
    private int taskIdCounter = 0;

    @PostMapping(value = "/executeCommand")
    public String Execute(@RequestBody CommandRequest request) {
        String command = request.getCommand();

        // 执行命令
        String result = this.executeLinuxCommand.executeCommand(command);

        return "Command executed: " + result;
    }

    //补充一个查询当前执行命令进程的接口

    @PostMapping("/start")
    public String startCommand(@RequestParam String command) throws IOException {
        // 创建并启动进程
        ProcessBuilder processBuilder = new ProcessBuilder("bash", "-c", command);
        Process process = processBuilder.start();

        // 分配唯一任务ID并记录到映射表中
        String taskId = "task-" + (++taskIdCounter);
        processMap.put(taskId, process);

        return "Started task with ID: " + taskId;
    }

    @PostMapping("/stop/{taskId}")
    public String stopCommand(@PathVariable String taskId) {
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
