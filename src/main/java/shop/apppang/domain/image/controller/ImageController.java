package shop.apppang.domain.image.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import shop.apppang.domain.image.dto.ImageUploadResponse;
import shop.apppang.global.s3.S3Uploader;

@RestController
@RequestMapping("/api/images")
@RequiredArgsConstructor
public class ImageController {

    private final S3Uploader s3Uploader;

    // 상품 이미지 업로드 → S3 URL 반환
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ImageUploadResponse> uploadProductImage(
            @RequestParam("file") MultipartFile file) {
        String url = s3Uploader.upload(file, "products");
        return ResponseEntity.ok(new ImageUploadResponse(url));
    }
}