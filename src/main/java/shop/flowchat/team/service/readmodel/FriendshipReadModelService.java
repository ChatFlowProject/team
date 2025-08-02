package shop.flowchat.team.service.readmodel;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.flowchat.team.infrastructure.messaging.friendship.FriendshipEventPayload;
import shop.flowchat.team.infrastructure.outbox.model.readmodel.friendship.FriendshipReadModel;
import shop.flowchat.team.infrastructure.repository.readmodel.FriendshipReadModelRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FriendshipReadModelService {
    private final FriendshipReadModelRepository repository;

    @Transactional(readOnly = true)
    public List<FriendshipReadModel> getAllFriendshipsByMemberId(UUID memberId) {
        return repository.findByFromMemberIdOrToMemberId(memberId, memberId);
    }

    @Transactional
    public void create(FriendshipEventPayload payload) {
        repository.findById(payload.id())
                .ifPresentOrElse(
                        existingFriendship -> {}, // friendship은 update 이벤트가 없음
                        () -> repository.save(FriendshipReadModel.create(payload))
                );
    }

    @Transactional
    public void delete(FriendshipEventPayload payload) {
        // friendship은 update 이벤트가 없으므로 soft-delete 처리가 필요 없음
        repository.deleteById(payload.id());
    }

}
