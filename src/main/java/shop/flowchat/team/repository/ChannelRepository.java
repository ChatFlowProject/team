package shop.flowchat.team.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import shop.flowchat.team.entity.category.Category;
import shop.flowchat.team.entity.channel.Channel;

import java.util.List;

public interface ChannelRepository extends JpaRepository<Channel, Long> {
    @Query("SELECT c FROM Channel c JOIN FETCH c.category WHERE c.category.id IN :categoryIds")
    List<Channel> findByCategoryIdIn(@Param("categoryIds") List<Long> categoryIds);

    @Query("SELECT COALESCE(MAX(c.position), 0) FROM Channel c WHERE c.category.id = :categoryId")
    Double findMaxPositionByCategoryId(@Param("categoryId") Long categoryId);

    void deleteByCategory(Category category);

}
