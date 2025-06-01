package shop.flowchat.team.infrastructure.repository.readmodel;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.flowchat.team.infrastructure.outbox.model.readmodel.member.MemberReadModel;

import java.util.UUID;

public interface MemberReadModelRepository extends JpaRepository<MemberReadModel, UUID> {

}
