package shop.flowchat.team.infrastructure.repository.channel;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import shop.flowchat.team.domain.category.Category;
import shop.flowchat.team.domain.channel.Channel;
import shop.flowchat.team.domain.channel.ChannelAccessType;

import java.util.List;
import java.util.UUID;

public interface ChannelRepository extends JpaRepository<Channel, Long> {
    @Query("SELECT c FROM Channel c JOIN FETCH c.category WHERE c.id IN :channelIds")
    List<Channel> findByIdIn(@Param("channelIds") List<Long> channelIds);

    @Query("SELECT c FROM Channel c JOIN FETCH c.category WHERE c.category.id IN :categoryIds")
    List<Channel> findByCategoryIdIn(@Param("categoryIds") List<Long> categoryIds);

    @Query("SELECT COALESCE(MAX(c.position), 0) FROM Channel c WHERE c.category.id = :categoryId")
    Double findMaxPositionByCategoryId(@Param("categoryId") Long categoryId);

    @Query("SELECT DISTINCT c FROM Channel c " +
            "JOIN FETCH c.channelMembers cmAll " +
            "JOIN FETCH cmAll.member " +
            "WHERE c.accessType = :accessType AND c.name = :name")
    List<Channel> findByName(@Param("name") String name,
                             @Param("accessType") ChannelAccessType accessType);

    void deleteByCategory(Category category); // 벌크로 동작하는 쿼리 메소드

    @Modifying
    @Query("DELETE FROM Channel c WHERE c.category.id IN :categoryIds")
    void deleteByCategoryIdsIn(@Param("categoryIds") List<Long> categoryIds);

    @Query("SELECT DISTINCT c FROM Channel c " +
            "JOIN c.channelMembers cm " +
            "JOIN cm.member cmm " +
            "JOIN FETCH c.channelMembers cmAll " +
            "JOIN FETCH cmAll.member " +
            "WHERE c.accessType = :accessType AND cmm.id = :memberId")
    List<Channel> findAllPrivateChannelsWithMember(@Param("memberId") UUID memberId,
                                                   @Param("accessType") ChannelAccessType accessType);
}
