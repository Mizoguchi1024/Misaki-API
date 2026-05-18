package org.mizoguchi.misaki.pojo.dto.admin;

import lombok.Data;

@Data
public class SearchExceptionLogAdminRequest {
    private Long id;

    private String exception;

    private String message;

    private String ip;

    private String uri;

    private String method;

    private String createTime;
}
