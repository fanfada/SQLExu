package com.sqlexecute.sqlexu.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author fanfada@cmss.chinamobile.com
 * @date 2024/12/10 16:57
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SQLDto {

    public String dbType;

    public String sqlFilePath;
}
