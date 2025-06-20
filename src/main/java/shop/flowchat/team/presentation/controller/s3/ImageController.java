package shop.flowchat.team.presentation.controller.s3;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import shop.flowchat.team.presentation.dto.ApiResponse;
import shop.flowchat.team.infrastructure.s3.S3ImageService;

@Tag(name = "Image Service API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/images")
@Profile("!test")
public class ImageController {
    private final S3ImageService s3ImageService;

    @Operation(summary = "파일 업로드")
    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ApiResponse<String> uploadImage(@RequestPart MultipartFile file) {
        String imgPath = s3ImageService.upload(file);
        return ApiResponse.success(imgPath);
    }

    @Operation(summary = "url로 파일 삭제")
    @DeleteMapping
    public ApiResponse<String> deleteImage(@RequestParam String url) {
        s3ImageService.deleteByUrl(url);
        return ApiResponse.success();
    }
}
