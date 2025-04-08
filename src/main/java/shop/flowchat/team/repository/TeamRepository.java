package shop.flowchat.team.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.flowchat.team.entity.Team;

public interface TeamRepository extends JpaRepository<Team, Long> {

}
