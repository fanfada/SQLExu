package com.sqlexecute.sqlexu.controller;

import com.sqlexecute.sqlexu.Executor.SQLExecutor;
import com.sqlexecute.sqlexu.domain.SQLDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author fanfada@cmss.chinamobile.com
 * @date 2024/12/10 16:51
 */
@Slf4j
@RestController
@RequestMapping("/api")
public class SQLManageController {

    @Autowired
    private SQLExecutor sqlExecutor;

    @PostMapping(value = "/execute")
    public String Execute(@RequestBody SQLDto sqlDto) {
        this.sqlExecutor.start(sqlDto.getDbType(), sqlDto.getSqlFilePath());
        return "SUCCESS";
    }
}
