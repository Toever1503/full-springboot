package com.models;

import com.dtos.EStatusReview;
import com.entities.ProductEntity;
import com.entities.ReviewEntity;
import com.entities.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ReviewModel {
    private Long id;
    private Long orderDetailId;
    private String content;
    private String rating;
    private List<MultipartFile> attachFiles;
        private List<String> attachFilesOrigin = new ArrayList<>();
    private Long parentId;

    public static ReviewEntity toEntity(ReviewModel model) {
        if(model == null) return null;
        return ReviewEntity.builder()
                .id(model.getId())
                .content(model.getContent())
                .rating(model.getRating())
                .isEdit(false)
                .status(EStatusReview.PENDING.name())
                .build();
    }
}
