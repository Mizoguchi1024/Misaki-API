package org.mizoguchi.misaki.pojo.dto.admin;

import lombok.Data;

@Data
public class SearchEmailLogAdminRequest {
    private Long id;

    private String sender;

    private String receiver;

    private String subject;

    private String createTime;
}
