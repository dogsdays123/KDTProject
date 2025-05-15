document.getElementById('selectAll').addEventListener('change', function () {
    document.querySelectorAll('.selectPlan').forEach(cb => {
        cb.checked = this.checked;
    });
});

document.getElementById('openPurchaseModal').addEventListener('click', function () {
    const selectedRows = Array.from(document.querySelectorAll('.selectPlan:checked'))
        .map(cb => cb.closest('tr')); // 체크된 체크박스의 행 가져오기

    if (selectedRows.length === 0) {
        alert('하나 이상의 항목을 선택해주세요.');
        return;
    }

    const drCodes = new Set();  // 중복을 방지하기 위해 Set 사용

    const tbody = document.getElementById('inspectionTableBody');
    tbody.innerHTML = ''; // 기존 내용 비우기

    selectedRows.forEach(checkbox => {
        const row = checkbox.closest('tr');
        const cells = row.querySelectorAll('td');
        const newRow = document.createElement('tr');

        const drCode = cells[2].textContent.trim();
        drCodes.add(drCode);

        newRow.innerHTML = `
            <td>${cells[1].textContent.trim()}</td>
            <td>${cells[2].textContent.trim()}</td>
            <td>${cells[3].textContent.trim()}</td>
            <td>${cells[4].textContent.trim()}</td>
            <td>${cells[5].textContent.trim()}</td>
            <td>${cells[7].textContent.trim()}</td>
        `;

        tbody.appendChild(newRow);
    });

    const form = document.getElementById('purchaseOrderForm');
    form.querySelectorAll('input[name="drCodes"]').forEach(input => input.remove());
    console.log(drCodes);
    drCodes.forEach(drCode => {
        const hiddenInput = document.createElement('input');
        hiddenInput.type = 'hidden';
        hiddenInput.name = 'drCodes';
        hiddenInput.value = drCode;
        form.appendChild(hiddenInput);
    });

    const modal = new bootstrap.Modal(document.getElementById('purchaseOrderModal'));
    modal.show();
});

document.getElementById('openPurchaseDelModal').addEventListener('click', function () {
    const selectedRows = Array.from(document.querySelectorAll('.selectPlan:checked'))
        .map(cb => cb.closest('tr')); // 체크된 체크박스의 행 가져오기

    if (selectedRows.length === 0) {
        alert('하나 이상의 항목을 선택해주세요.');
        return;
    }
    const firstRow = selectedRows[0].children;
    const currentState = firstRow[7].innerText.trim();

    if (currentState !== '납품 완료') {
        alert('선택한 항목은 아직 납품 준비가 완료되지 않았습니다.');
        return;
    }

    const drCodes = new Set();  // 중복을 방지하기 위해 Set 사용

    const tbody = document.getElementById('deleteTableBody');
    tbody.innerHTML = ''; // 기존 내용 비우기

    selectedRows.forEach(checkbox => {
        const row = checkbox.closest('tr');
        const cells = row.querySelectorAll('td');
        const newRow = document.createElement('tr');


        const drCode = cells[2].textContent.trim();
        drCodes.add(drCode);

        newRow.innerHTML = `
            <td>${cells[1].textContent.trim()}</td>
            <td>${cells[2].textContent.trim()}</td>
            <td>${cells[3].textContent.trim()}</td>
            <td>${cells[4].textContent.trim()}</td>
            <td>${cells[5].textContent.trim()}</td>
            <td>${cells[7].textContent.trim()}</td>
        `;

        tbody.appendChild(newRow);
    });

    const form = document.getElementById('purchaseOrderDelForm');
    form.querySelectorAll('input[name="drCodes"]').forEach(input => input.remove());
    console.log(drCodes);
    drCodes.forEach(drCode => {
        const hiddenInput = document.createElement('input');
        hiddenInput.type = 'hidden';
        hiddenInput.name = 'drCodes';
        hiddenInput.value = drCode;
        form.appendChild(hiddenInput);
    });



    const modal = new bootstrap.Modal(document.getElementById('purchaseOrderModalDel'));
    modal.show();
});

document.querySelector(".clearBtn").addEventListener("click", function (e) {
    e.preventDefault()
    e.stopPropagation()

    self.location = '/supplier/requestDelivery'
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
        DELIVERY_DELIVERED: "납품 완료"
    };

    // 드롭다운 option에 텍스트 설정
    document.querySelectorAll(".drState option").forEach(option => {
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