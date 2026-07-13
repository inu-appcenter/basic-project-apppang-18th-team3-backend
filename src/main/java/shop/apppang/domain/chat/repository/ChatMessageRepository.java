package shop.apppang.domain.chat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.apppang.domain.chat.entity.ChatMessageEntity;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessageEntity, Long> {

    // 특정 세션의 대화 (오래된 순) — 대화 맥락 이어가기 & 히스토리 조회용
    List<ChatMessageEntity> findBySessionIdOrderByCreatedAtAsc(String sessionId);

    // 사용자의 전체 대화 (오래된 순)
    List<ChatMessageEntity> findByUser_IdOrderByCreatedAtAsc(Long userId);
}