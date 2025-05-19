package shop.flowchat.team.infrastructure.repository.team;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.flowchat.team.domain.team.Team;

import java.util.UUID;

public interface TeamRepository extends JpaRepository<Team, UUID> {

}
