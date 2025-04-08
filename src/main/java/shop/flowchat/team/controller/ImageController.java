package shop.flowchat.team.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import shop.flowchat.team.dto.ApiResponse;
import shop.flowchat.team.service.S3ImageService;

@Slf4j
@Tag(name = "이미지 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/images")
public class ImageController {
    private final S3ImageService s3ImageService;

    @Operation(summary = "이미지 업로드")
    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ApiResponse<String> uploadImage(@RequestPart MultipartFile file) {
        String imgPath = s3ImageService.upload(file);
        log.info("imagePath = {}", imgPath);
        return ApiResponse.success(imgPath);
    }

    @Operation(summary = "url로 이미지 삭제")
    @DeleteMapping
    public ApiResponse<String> deleteImage(@RequestParam String url) {
        s3ImageService.deleteByUrl(url);
        return ApiResponse.success();
    }
}
