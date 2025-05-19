package shop.flowchat.team.readmodel.member;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.flowchat.team.infrastructure.messaging.member.MemberEventPayload;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MemberReadModelUpdater {

    private final MemberReadModelRepository repository;

    @Transactional
    public void upsert(MemberEventPayload payload) {
        repository.findById(payload.id())
                .ifPresentOrElse(
                        existingMember -> existingMember.update(payload),
                        () -> repository.save(MemberReadModel.create(payload))
                );
    }

    @Transactional
    public void delete(UUID memberId) {
        repository.deleteById(memberId);
    }

    @Transactional
    public void updateStatus(UUID memberId, MemberState state) {
        repository.findById(memberId)
                .ifPresent(member -> member.changeState(state));
    }
}
