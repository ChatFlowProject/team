package shop.flowchat.team.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.flowchat.team.entity.team.Team;

import java.util.List;
import java.util.UUID;

public interface TeamRepository extends JpaRepository<Team, UUID> {
    List<Team> findByOwnerId(UUID ownerId);

}
