package shop.apppang.domain.image.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import shop.apppang.domain.image.dto.ImageUploadResponse;
import shop.apppang.global.s3.S3Uploader;

import java.util.List;

@RestController
@RequestMapping("/api/images")
@RequiredArgsConstructor
public class ImageController {

    private final S3Uploader s3Uploader;
    private static final List<String> ALLOWED_TYPES = List.of("reviews", "products");

    // 이미지 여러 장 업로드 → URL 리스트 반환
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<List<ImageUploadResponse>> uploadImages(
            @RequestParam("files") List<MultipartFile> files,
            @RequestParam(value = "type", defaultValue = "reviews") String type) {

        if (!ALLOWED_TYPES.contains(type)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "허용되지 않은 타입입니다.");
        }
        List<ImageUploadResponse> result = files.stream()
                .map(f -> new ImageUploadResponse(s3Uploader.upload(f, type)))
                .toList();
        return ResponseEntity.ok(result);
    }
}