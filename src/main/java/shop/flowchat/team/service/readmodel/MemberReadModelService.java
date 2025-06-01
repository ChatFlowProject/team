package shop.flowchat.team.service.readmodel;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.flowchat.team.infrastructure.messaging.member.MemberEventPayload;
import shop.flowchat.team.infrastructure.outbox.model.readmodel.member.MemberReadModel;
import shop.flowchat.team.infrastructure.repository.readmodel.MemberReadModelRepository;

@Service
@RequiredArgsConstructor
public class MemberReadModelService {

    private final MemberReadModelRepository repository;

    @Transactional
    public void upsert(MemberEventPayload payload) {
        repository.findById(payload.id())
                .ifPresentOrElse(
                        existingMember -> { // 유효한 업데이트 이벤트만 적용
                            if (payload.timestamp().isAfter(existingMember.getTimestamp())) {
                                existingMember.update(payload);
                            }
                        },
                        () -> repository.save(MemberReadModel.create(payload))
                );
    }

    @Transactional
    public void delete(MemberEventPayload payload) {
        repository.deleteById(payload.id());
    }

}
