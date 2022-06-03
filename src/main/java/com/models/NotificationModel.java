package com.models;

import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;

public class NotificationModel {
    private Long id;
    private String title;
    private String content;
    private String contentExcert;
    private List<MultipartFile> attachFiles;
    private List<String> attachFilesOrigin;
    private Integer viewed;
    private boolean isEdit;
    private Integer limitEditCount;
    private String status;
    private Date futureDate;

    private List<Long> notificationUsers;
}
