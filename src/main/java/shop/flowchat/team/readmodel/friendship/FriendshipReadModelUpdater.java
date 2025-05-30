package shop.flowchat.team.readmodel.friendship;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.flowchat.team.infrastructure.messaging.friendship.FriendshipEventPayload;

@Service
@RequiredArgsConstructor
public class FriendshipReadModelUpdater {
    private final FriendshipReadModelRepository repository;

    @Transactional
    public void upsert(FriendshipEventPayload payload) {
        repository.findByFromMemberIdAndToMemberId(payload.fromMemberId(), payload.toMemberId())
                .ifPresentOrElse(
                        existingFriendship -> {},
                        () -> repository.save(FriendshipReadModel.create(payload))
                );
    }

    @Transactional
    public void delete(FriendshipEventPayload payload) {
        repository.deleteByFromMemberIdAndToMemberId(payload.fromMemberId(), payload.toMemberId());
    }

}
