package shop.flowchat.team.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import shop.flowchat.team.service.TeamService;

@Tag(name = "Group API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/group")
public class TeamController {
    private final TeamService teamService;

}
