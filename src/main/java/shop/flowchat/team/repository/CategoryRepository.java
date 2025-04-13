package shop.flowchat.team.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import shop.flowchat.team.entity.category.Category;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    @Query("SELECT c FROM Category c JOIN FETCH c.team WHERE c.id = :categoryId")
    Optional<Category> findById(@Param("categoryId") Long categoryId);

    @Query("SELECT c FROM Category c JOIN FETCH c.team WHERE c.team.id = :teamId")
    List<Category> findByTeamId(@Param("teamId") UUID teamId);

    @Query("SELECT COALESCE(MAX(c.position), 0) FROM Category c WHERE c.team.id = :teamId")
    Double findMaxPositionByTeamId(@Param("teamId") UUID teamId);

}
