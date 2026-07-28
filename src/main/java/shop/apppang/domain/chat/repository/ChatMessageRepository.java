package shop.apppang.domain.chat.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import shop.apppang.domain.chat.entity.ChatMessageEntity;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessageEntity, Long> {

    // 특정 세션의 대화 (오래된 순) — 대화 맥락 이어가기 & 히스토리 조회용
    List<ChatMessageEntity> findBySessionIdOrderByCreatedAtAsc(String sessionId);

    // 히스토리 첫 로드 — 유저의 가장 최신 메시지부터 size개 (id DESC)
    Slice<ChatMessageEntity> findByUser_IdOrderByIdDesc(Long userId, Pageable pageable);

    // 히스토리 위로 스크롤 — cursor(id)보다 오래된 메시지 size개 (id DESC)
    Slice<ChatMessageEntity> findByUser_IdAndIdLessThanOrderByIdDesc(Long userId, Long cursor, Pageable pageable);
}