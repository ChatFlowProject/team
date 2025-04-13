package shop.flowchat.team.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import shop.flowchat.team.dto.ApiResponse;
import shop.flowchat.team.dto.category.request.CategoryCreateRequest;
import shop.flowchat.team.dto.category.response.CategoryCreateResponse;
import shop.flowchat.team.service.facade.TeamFacadeService;

import java.util.UUID;

@Tag(name = "Category Service API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/teams/{teamId}/categories")
public class CategoryController {
    private final TeamFacadeService teamFacadeService;
    // todo: 토큰 헤더 추가 및 권한 체크 로직 추가

    @Operation(summary = "카테고리 추가")
    @PostMapping
    public ApiResponse<CategoryCreateResponse> createCategory(
            @PathVariable("teamId") UUID teamId,
            @Valid @RequestBody CategoryCreateRequest request) { // todo: kafka 추가시 position 가장 아래 배치
        return ApiResponse.success(teamFacadeService.addCategory(teamId, request));
    }

    @Operation(summary = "카테고리 삭제 (미구현)")
    @DeleteMapping("/{categoryId}")
    public ApiResponse deleteCategory(
            @PathVariable("teamId") UUID teamId,
            @PathVariable("categoryId") Long categoryId) {
//        teamFacadeService.deleteCategory(teamId, categoryId);
        return ApiResponse.success();
    }

}
