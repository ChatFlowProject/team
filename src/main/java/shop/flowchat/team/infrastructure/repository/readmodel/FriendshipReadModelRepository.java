package shop.flowchat.team.infrastructure.repository.readmodel;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.flowchat.team.infrastructure.outbox.model.readmodel.friendship.FriendshipReadModel;

import java.util.List;
import java.util.UUID;

public interface FriendshipReadModelRepository extends JpaRepository<FriendshipReadModel, String> {

    List<FriendshipReadModel> findByFromMemberIdOrToMemberId(UUID fromMemberId, UUID toMemberId);

}
