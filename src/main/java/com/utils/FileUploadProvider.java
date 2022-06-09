package com.utils;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.entities.QuestionEntity;
import com.entities.UserEntity;
import lombok.Data;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

@Component
public class FileUploadProvider {

    private final String bucket = "team-2";
    private final String bucketEndpoint = "https://team-2.s3.ap-northeast-2.amazonaws.com/";
    private final AmazonS3 s3Client;
    private final String accessKey = "AKIA2GSEWDCLRXXMMCMG";
    private final String accessSecret = "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";
    private final String region = "ap-northeast-2";

    public FileUploadProvider() {
        this.s3Client = amazonS3ClientBuilder().build();
    }

    public String uploadFile(String folder, MultipartFile file) throws IOException {
        StringBuilder checkFileName = new StringBuilder(folder);
        checkFileName.append(file.getOriginalFilename());
        if (isFileExist(checkFileName.toString())) {
            int i = 1;
            while (true) {
                checkFileName.setLength(0);
                checkFileName.append(folder).append(i++).append(file.getOriginalFilename());
                if (!isFileExist(file.toString()))
                    break;
            }
        }
        String filePath = checkFileName.toString();
        s3Client.putObject(this.bucket, filePath, file.getInputStream(), null);
        return bucketEndpoint + filePath;
    }

    public boolean isFileExist(String key) {
        try {
            s3Client.getObject(bucket, key);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public void deleteFile(String key){
        System.out.println("Delete File: "+ key);
        this.s3Client.deleteObject(this.bucket,key.replace(this.bucketEndpoint, ""));
    }

    public AmazonS3ClientBuilder amazonS3ClientBuilder() {
        return AmazonS3ClientBuilder.standard()
                .withCredentials(credentialsProvider())
                .withRegion(this.region);
    }

    private AWSCredentialsProvider credentialsProvider() {
        AWSCredentials awsCredentials = new BasicAWSCredentials(this.accessKey, this.accessSecret);
        return new AWSStaticCredentialsProvider(awsCredentials);
    }

    public static void main(String[] args) {
        FileUploadProvider fileUploadProvider = new FileUploadProvider();
        System.out.println(fileUploadProvider.isFileExist("user/vudt/product/19112016.jpeg"));
    }
}
