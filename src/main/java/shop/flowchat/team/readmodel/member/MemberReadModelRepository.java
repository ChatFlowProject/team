package shop.flowchat.team.readmodel.member;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface MemberReadModelRepository extends JpaRepository<MemberReadModel, UUID> {

}
