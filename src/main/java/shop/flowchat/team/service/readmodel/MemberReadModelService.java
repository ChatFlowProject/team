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
    public void create(MemberEventPayload payload) {
        if (repository.existsById(payload.id())) return;
        repository.save(MemberReadModel.create(payload));
    }

    @Transactional
    public void updateProfile(MemberEventPayload payload) { // upsert
        repository.findById(payload.id())
                .ifPresentOrElse(
                        existingMember -> {
                            if (existingMember.isNewProfileUpdateEvent(payload.timestamp())) {
                                existingMember.updateProfile(payload);
                            }
                        },
                        () -> {
                            MemberReadModel memberReadModel = MemberReadModel.create(payload);
                            memberReadModel.updateProfile(payload);
                            repository.save(memberReadModel);
                        }
                );
    }

    @Transactional
    public void updateStatus(MemberEventPayload payload) { // upsert
        repository.findById(payload.id())
                .ifPresentOrElse(
                        existingMember -> {
                            if (existingMember.isNewStatusUpdateEvent(payload.timestamp())) {
                                existingMember.updateStatus(payload);
                            }
                        },
                        () -> {
                            MemberReadModel memberReadModel = MemberReadModel.create(payload);
                            memberReadModel.updateStatus(payload);
                            repository.save(memberReadModel);
                        }
                );
    }

    @Transactional
    public void delete(MemberEventPayload payload) {
        // 업데이트와 삭제 이벤트 역전으로 데이터가 재생성이 가능함 - 필요시 soft-delete 처리
        repository.deleteById(payload.id());
    }

}
