package shop.flowchat.team.service.readmodel;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.flowchat.team.infrastructure.messaging.friendship.FriendshipEventPayload;
import shop.flowchat.team.infrastructure.outbox.model.readmodel.friendship.FriendshipReadModel;
import shop.flowchat.team.infrastructure.repository.readmodel.FriendshipReadModelRepository;

@Service
@RequiredArgsConstructor
public class FriendshipReadModelService {
    private final FriendshipReadModelRepository repository;

    @Transactional
    public void upsert(FriendshipEventPayload payload) {
        repository.findById(payload.id())
                .ifPresentOrElse(
                        existingFriendship -> {}, // friendship은 update 이벤트가 없음
                        () -> repository.save(FriendshipReadModel.create(payload))
                );
    }

    @Transactional
    public void delete(FriendshipEventPayload payload) {
        repository.deleteById(payload.id());
    }

}
