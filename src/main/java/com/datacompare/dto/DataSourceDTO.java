package com.datacompare.dto;

import com.datacompare.entity.DataSource.SourceType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DataSourceDTO {
    @NotBlank(message = "数据源名称不能为空")
    private String name;
    
    @NotNull(message = "数据源类型不能为空")
    private SourceType type;
    
    @NotBlank(message = "数据源配置不能为空")
    private String config;
} 