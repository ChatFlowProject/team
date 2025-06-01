package shop.flowchat.team.infrastructure.repository.readmodel;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.flowchat.team.infrastructure.outbox.model.readmodel.friendship.FriendshipReadModel;

public interface FriendshipReadModelRepository extends JpaRepository<FriendshipReadModel, String> {

}
