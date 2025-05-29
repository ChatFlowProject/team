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
        upsertOneDirection(payload);
        upsertOneDirection(FriendshipEventPayload.from(payload.id(), payload.toMemberId(), payload.fromMemberId()));
    }

    private void upsertOneDirection(FriendshipEventPayload payload) {
        repository.findByFromMemberIdAndToMemberId(payload.fromMemberId(), payload.toMemberId())
                .ifPresentOrElse(
                        existingFriendship -> existingFriendship.update(payload),
                        () -> repository.save(FriendshipReadModel.create(payload))
                );
    }

    @Transactional
    public void delete(FriendshipEventPayload payload) {
        repository.deleteByFromMemberIdAndToMemberId(payload.fromMemberId(), payload.toMemberId());
        repository.deleteByFromMemberIdAndToMemberId(payload.toMemberId(), payload.fromMemberId());
    }

}
