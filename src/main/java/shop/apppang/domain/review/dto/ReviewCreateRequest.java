package shop.apppang.domain.review.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter                     // @ModelAttribute 바인딩에 필요
@NoArgsConstructor
public class ReviewCreateRequest {

    private Integer rating;
    private String title;
    private String content;
    private List<MultipartFile> images;   // URL(String) → 실제 파일로 변경
}