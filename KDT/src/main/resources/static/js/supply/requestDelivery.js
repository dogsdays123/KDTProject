document.querySelectorAll('.selectPlan').forEach((checkbox) => {
    checkbox.addEventListener('change', function () {
        if (this.checked) {
            document.querySelectorAll('.selectPlan').forEach((cb) => {
                if (cb !== this) cb.checked = false;
            });
        }
    });
});

document.getElementById('openPurchaseModal').addEventListener('click', function () {
    const selectedRows = Array.from(document.querySelectorAll('.selectPlan:checked'))
        .map(cb => cb.closest('tr')); // 체크된 체크박스의 행 가져오기

    if (selectedRows.length === 0) {
        alert('하나 이상의 항목을 선택해주세요.');
        return;
    }

    const firstRow = selectedRows[0].children;

    const planCodeInput = firstRow[1].innerText;
    const sId = firstRow[2].innerText;
    const productSupplier = firstRow[3].innerText;
    const mCode = firstRow[4].innerText;
    const productNameInput = firstRow[5].innerText;
    const availableQuantity = firstRow[6].innerText;
    const currentState = firstRow[9].innerText.trim();

    if (currentState === '납품 요청됨') {
        alert('선택한 항목은 이미 납품 요청이 완료되었습니다.');
        return;
    }


    if (currentState !== '준비 완료') {
        alert('선택한 항목은 아직 납품 준비가 완료되지 않았습니다.');
        return;
    }

    document.getElementById('requestQuantity').addEventListener('input', function () {
        const availableQuantity = parseInt(firstRow[6].innerText);
        const requestQuantity = parseInt(this.value);

        if (requestQuantity > availableQuantity) {
            alert('납품 요청 수량은 납품 가능 수량을 초과할 수 없습니다.');
            this.value = ''; // 잘못 입력한 값 비우기
            this.focus();
        }
    });

    document.getElementById('planCodeInput').innerText = planCodeInput;
    document.getElementById('oCodeHidden').value = planCodeInput;
    document.getElementById('sIdHidden').value = sId;
    document.getElementById('mCodeHidden').value = mCode;
    document.getElementById('productSupplier').innerText = productSupplier;
    document.getElementById('productNameInput').innerText = productNameInput;
    document.getElementById('availableQuantity').value = availableQuantity;

    const modal = new bootstrap.Modal(document.getElementById('purchaseOrderModal'));
    modal.show();
});

document.querySelector(".clearBtn").addEventListener("click", function (e) {
    e.preventDefault()
    e.stopPropagation()

    self.location = '/supply/requestDelivery'
}, false)

document.querySelector(".pagination").addEventListener("click", function (e) {
    e.preventDefault()
    e.stopPropagation()

    const target = e.target

    if (target.tagName !== 'A') {
        return
    }

    const num = target.getAttribute("data-num")

    const formObj = document.querySelector("form")

    formObj.innerHTML += `<input type='hidden' name='page' value='${num}'>`

    formObj.submit()
}, false)

document.addEventListener("DOMContentLoaded", () => {
    const states = {
        ON_HOLD: "대기",
        APPROVAL: "승인",
        IN_PROGRESS: "진행 중",
        UNDER_INSPECTION: "검수 중",
        RETURNED: "반품",
        FINISHED: "종료",
        REJECT: "거절",
        ARRIVED: "도착",
        NOT_REMAINING: "재고 없음",
        DELIVERED: "배달 완료",
        SUCCESS_INSPECTION: "검수 완료",
        SUCCESS: "전체 완료",
        READY_SUCCESS: "준비 완료",
        DELIVERY_REQUESTED: "납품 요청됨",
        DELIVERY_DELIVERED: "납품 완료",
        HOLD_DELIVERY: "납품 대기"
    };

    // 드롭다운 option에 텍스트 설정
    document.querySelectorAll(".oState option").forEach(option => {
        const state = option.dataset.state;
        if (!state) {
            option.textContent = "전체";
        } else {
            option.textContent = states[state] || "알 수 없음";
        }
    });

    // 테이블 내 표시용
    document.querySelectorAll('[data-state]').forEach(function (td) {
        const stateKey = td.getAttribute('data-state');
        const text = states[stateKey] || "알 수 없음";
        td.innerHTML = `<span class="state-${stateKey}">${text}</span>`;
    });
});