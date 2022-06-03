package com.dtos;

import java.util.Date;
import java.util.List;

public class NotificationDetailDto {
    private Long id;
    private String title;
    private String content;
    private List<String> attachFiles;
    private Date createdDate;
    private Date updatedDate;
    private Integer viewed;
    private boolean isEdit;
    private Integer limitEditCount;
    private String status;
    private Date futureDate;
    private String createdBy;

}
