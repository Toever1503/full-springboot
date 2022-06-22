package com.dtos;

import com.entities.ReviewEntity;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.dtos.QuestionDto.parseJson;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ReviewDto {
    private Long id;
    private String optionName;
    private String content;
    private Float rating;
    private List<Object> attachFiles;
    private Date createdDate;
    private Date updatedDate;
    private boolean isEdit;
    private Long parentId;
    private ProductDto product;
    private OrderDetailDto orderDetail;
    private UserDto createdBy;

    public static ReviewDto toDto(ReviewEntity entity) {
        if (entity == null) return null;
        return ReviewDto.builder()
                .id(entity.getId())
                .optionName(entity.getOptionName())
                .content(entity.getContent())
                .rating(entity.getRating() == null ? null : entity.getRating())
                .attachFiles(entity.getAttachFiles() == null ? null : parseJson(entity.getAttachFiles()).getJSONArray("files").toList())
                .createdDate(entity.getCreatedDate())
                .updatedDate(entity.getUpdatedDate())
                .isEdit(entity.getIsEdit())
                .parentId(entity.getParentReview() == null ? null : entity.getParentReview().getId())
                .product(ProductDto.toDto(entity.getProduct()))
                .orderDetail(OrderDetailDto.toDto(entity.getOrderDetail()))
                .createdBy(UserDto.toDto(entity.getCreatedBy()))
                .build();
    }
}
