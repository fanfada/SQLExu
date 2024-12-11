package com.sqlexecute.sqlexu.controller;

import com.sqlexecute.sqlexu.Executor.ExecuteLinuxCommand;
import com.sqlexecute.sqlexu.domain.CommandRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping(value = "/executeCommand")
    public String Execute(@RequestBody CommandRequest request) {
        String command = request.getCommand();

        // 执行命令
        String result = this.executeLinuxCommand.executeCommand(command);

        return "Command executed: " + result;
    }
}
