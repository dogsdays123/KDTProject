package org.zerock.b01.domain;

public enum CurrentStatus {
    ON_HOLD, //대기
    APPROVAL, //승인
    IN_PROGRESS,    //진행 중
    UNDER_INSPECTION, //검수중
    RETURNED, //반품
    FINISHED, //종료
    REJECT, //거절
    DELIVERED, //배달 완료
    ARRIVED, //도착
    NOT_REMAINING, // 남은 게 없음
    SUCCESS_INSPECTION, //"검수 완료"
    SUCCESS //"전체 완료"
}
