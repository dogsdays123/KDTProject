const stateMap = {
    ON_HOLD: "대기",
    HOLD_PROGRESS: "검수 대기",
    HOLD_DELIVERY: "납품 대기",
    APPROVAL: "승인",
    IN_PROGRESS: "진행 중",
    UNDER_INSPECTION: "검수 중",
    RETURNED: "반품",
    FINISHED: "종료",
    REJECT: "거절",
    DELIVERED: "배달 완료",
    ARRIVED: "도착",
    NOT_REMAINING: "재고 없음",
    SUCCESS_INSPECTION: "검수 완료",
    SUCCESS: "전체 완료",
    READY_SUCCESS: "준비 완료",
    DELIVERY_REQUESTED: "납품 요청됨",
    DELIVERY_DELIVERED: "납품 완료"
};

    document.querySelectorAll(".CheckState").forEach(td => {
        const state = td.dataset.state;
        td.textContent = stateMap[state] || "알 수 없음";
    });