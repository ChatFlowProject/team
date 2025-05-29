package shop.flowchat.team.readmodel.friendship;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface FriendshipReadModelRepository extends JpaRepository<FriendshipReadModel, Long> {
    Optional<FriendshipReadModel> findByFromMemberIdAndToMemberId(UUID fromMemberId, UUID toMemberId);

    void deleteByFromMemberIdAndToMemberId(UUID fromMemberId, UUID toMemberId);

}
