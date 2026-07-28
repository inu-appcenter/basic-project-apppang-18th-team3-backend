package shop.apppang.domain.chat.dto;

import java.util.List;

// 챗봇 대화 기록 커서 페이징 응답
// messages: 시간순(오래된 → 최신)으로 정렬되어 화면 표시 순서 그대로
// nextCursor: 다음(위로 스크롤) 요청 때 cursor로 넣을 값. 더 없으면 null
// hasNext: 위로 더 불러올 이전 대화가 있는지
public record ChatHistoryPageResponse(
        List<ChatHistoryResponse> messages,
        Long nextCursor,
        boolean hasNext
) {}
