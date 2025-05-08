document.getElementById('selectAll').addEventListener('change', function () {
    document.querySelectorAll('.selectPlan').forEach(cb => {
        cb.checked = this.checked;
    });
});

document.getElementById('openPurchaseModal').addEventListener('click', function () {

    const selectedRows = document.querySelectorAll('.selectPlan:checked');
    if (selectedRows.length === 0) {
        alert('출고 확정할 항목을 선택해 주세요.');
        return;
    }

    const tbody = document.getElementById('confirmTableBody');
    tbody.innerHTML = ''; // 기존 내용 비우기

    const opIds = [];

    for (let checkbox of selectedRows) {
        const row = checkbox.closest('tr');
        const cells = row.querySelectorAll('td');
        const status = cells[5].textContent.trim();

        if (status === '승인') {
            alert('선택한 항목 중 이미 출고 처리 완료된 항목이 포함되어 있습니다.');
            return;
        }
    }

    selectedRows.forEach(checkbox => {
        const row = checkbox.closest('tr');
        const cells = row.querySelectorAll('td');
        const newRow = document.createElement('tr');

        const opId = cells[1].textContent.trim();
        opIds.push(opId);

        newRow.innerHTML = `
            <td>${cells[1].textContent.trim()}</td>
            <td>${cells[2].textContent.trim()}</td>
            <td>${cells[3].textContent.trim()}</td>
            <td>${cells[4].textContent.trim()}</td>
            <td>${cells[5].textContent.trim()}</td>
            <td>${cells[6].textContent.trim()}</td>
        `;

        tbody.appendChild(newRow);
    });

    const form = document.getElementById('purchaseOrderForm');
    form.querySelectorAll('input[name="opIds"]').forEach(input => input.remove());

    opIds.forEach(opId => {
        const hiddenInput = document.createElement('input');
        hiddenInput.type = 'hidden';
        hiddenInput.name = 'opIds';
        hiddenInput.value = opId;
        form.appendChild(hiddenInput);
    });

    const modal = new bootstrap.Modal(document.getElementById('purchaseOrderModal'));
    modal.show();
});

document.getElementById('openPurchaseDelModal').addEventListener('click', function () {

    const selectedRows = document.querySelectorAll('.selectPlan:checked');
    if (selectedRows.length === 0) {
        alert('삭제할 항목을 선택해 주세요.');
        return;
    }

    const tbody = document.getElementById('deleteTableBody');
    tbody.innerHTML = ''; // 기존 내용 비우기

    const opIds = [];

    let hasPending = false;

    for (let checkbox of selectedRows) {
        const row = checkbox.closest('tr');
        const cells = row.querySelectorAll('td');
        const status = cells[5].textContent.trim();

        if (status === '대기') {
            hasPending = true;
            break;
        }
    }

    if (hasPending) {
        const proceed = confirm('선택한 항목 중 출고처리가 확정되지 않은 항목이 포함되어 있습니다. 그래도 진행하시겠습니까?');
        if (!proceed) {
            return;
        }
    }

    for (let checkbox of selectedRows) {
        const row = checkbox.closest('tr');
        const cells = row.querySelectorAll('td');
        const status = cells[5].textContent.trim();

        if (status !== '대기') {
            opIds.push(cells[1].textContent.trim());
        }
    }

    selectedRows.forEach(checkbox => {
        const row = checkbox.closest('tr');
        const cells = row.querySelectorAll('td');
        const newRow = document.createElement('tr');

        const opId = cells[1].textContent.trim();
        opIds.push(opId);

        newRow.innerHTML = `
            <td>${cells[1].textContent.trim()}</td>
            <td>${cells[2].textContent.trim()}</td>
            <td>${cells[3].textContent.trim()}</td>
            <td>${cells[4].textContent.trim()}</td>
            <td>${cells[5].textContent.trim()}</td>
            <td>${cells[6].textContent.trim()}</td>
        `;

        tbody.appendChild(newRow);
    });

    const form = document.getElementById('purchaseOrderDelForm');
    form.querySelectorAll('input[name="opIds"]').forEach(input => input.remove());

    opIds.forEach(opId => {
        const hiddenInput = document.createElement('input');
        hiddenInput.type = 'hidden';
        hiddenInput.name = 'opIds';
        hiddenInput.value = opId;
        form.appendChild(hiddenInput);
    });

    const modal = new bootstrap.Modal(document.getElementById('purchaseOrderModalDel'));
    modal.show();
});

document.querySelector(".clearBtn").addEventListener("click", function (e) {
    e.preventDefault()
    e.stopPropagation()

    self.location = '/outPut/outPutList'
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
        ON_HOLD:      { text: "대기", class: "badge bg-secondary" },
        APPROVAL:     { text: "승인", class: "badge bg-success" },
        IN_PROGRESS:  { text: "진행 중", class: "badge bg-info" },
        UNDER_INSPECTION: { text: "검수중", class: "badge bg-warning text-dark" },
        RETURNED:     { text: "반품", class: "badge bg-danger" },
        FINISHED:     { text: "종료", class: "badge bg-dark" },
        REJECT:       { text: "거절", class: "badge bg-danger" },
        DELIVERED:    { text: "배달 됨", class: "badge bg-primary" },
        ARRIVED:      { text: "도착함", class: "badge bg-success" },
        NOT_REMAINING: { text: "남은 게 없음", class: "badge bg-light text-dark" },
        UNKNOWN:      { text: "알 수 없음", class: "badge bg-secondary" }
    };

    document.querySelectorAll(".opState option").forEach(option => {
        const state = option.dataset.state;
        if (!state) {
            option.textContent = "전체";
        } else {
            option.textContent = stateMap[state] || "알 수 없음";
        }
    });

    document.querySelectorAll('[data-state]').forEach(function (td) {
        const stateKey = td.getAttribute('data-state');
        const state = states[stateKey] || states["UNKNOWN"];

        td.innerHTML = `<span class="${state.class}">${state.text}</span>`;
    });
});