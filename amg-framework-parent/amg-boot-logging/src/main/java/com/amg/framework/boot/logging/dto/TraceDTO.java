package com.amg.framework.boot.logging.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.io.Serializable;


@Data
@AllArgsConstructor
public class TraceDTO implements Serializable {

    private static final long serialVersionUID = 2059676348309478565L;

    // 请求id
    private String requestId;

    // 所属模块
    private String belong;

    // stage id
    private String stageId;

    // 父级 stage id
    private String parentStageId;

    // 请求uri
    private String requestUri;

    // 执行时间
    private Integer executTime;

    // 日志信息
    private String log;

    // 创建时间
    private Long createTime;

}
