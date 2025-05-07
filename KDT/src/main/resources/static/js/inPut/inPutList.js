document.getElementById('selectAll').addEventListener('change', function () {
    document.querySelectorAll('.selectPlan').forEach(cb => {
        cb.checked = this.checked;
    });
});

document.querySelectorAll('.openPurchaseModal').forEach(button => {
    button.addEventListener('click', function () {
        const row = this.closest('tr'); // 클릭한 버튼이 속한 tr

        // 각 td 값을 가져오기
        // const ppCode = row.querySelector('td:nth-child(2)').innerText;
        // const pCode = row.querySelector('td:nth-child(3)').innerText;
        // const pName = row.querySelector('td:nth-child(4)').innerText;
        // const pStart = row.querySelector('td:nth-child(5)').innerText;
        // const pEnd = row.querySelector('td:nth-child(6)').innerText;
        //
        // document.getElementById('planCodeInput').value = ppCode;
        // document.getElementById('ppProductCode').value = pCode;
        // document.getElementById('ppProductName').value = pName;
        // document.getElementById('ppStartDay').value = pStart;
        // document.getElementById('ppEndDay').value = pEnd;

        // 모달 띄우기
        const modal = new bootstrap.Modal(document.getElementById('purchaseOrderModal'));
        modal.show();
    });
});

document.getElementById('openPurchaseDelModal').addEventListener('click', function () {

    const selectedRows = document.querySelectorAll('.selectPlan:checked');
    if (selectedRows.length === 0) {
        alert('삭제할 항목을 선택해 주세요.');
        return;
    }

    const tbody = document.getElementById('deleteTableBody');
    tbody.innerHTML = ''; // 기존 내용 비우기

    const ipIds = [];

    let hasPending = false;

    for (let checkbox of selectedRows) {
        const row = checkbox.closest('tr');
        const cells = row.querySelectorAll('td');
        const status = cells[10].textContent.trim();

        if (status === '진행 중') {
            hasPending = true;
            break;
        }
    }

    if (hasPending) {
        const proceed = confirm('선택한 항목 중 입고 처리가 완료되지 않은 항목이 포함되어 있습니다. 그래도 진행하시겠습니까?');
        if (!proceed) {
            return;
        }
    }


    selectedRows.forEach(checkbox => {
        const row = checkbox.closest('tr');
        const cells = row.querySelectorAll('td');
        const newRow = document.createElement('tr');

        const ipId = cells[1].textContent.trim();
        ipIds.push(ipId);

        newRow.innerHTML = `
            <td>${cells[1].textContent.trim()}</td>
            <td>${cells[2].textContent.trim()}</td>
            <td>${cells[3].textContent.trim()}</td>
            <td>${cells[4].textContent.trim()}</td>
            <td>${cells[5].textContent.trim()}</td>
            <td>${cells[6].textContent.trim()}</td>
            <td>${cells[7].textContent.trim()}</td>
            <td>${cells[8].textContent.trim()}</td>
        `;

        tbody.appendChild(newRow);
    });

    const form = document.getElementById('purchaseOrderDelForm');
    form.querySelectorAll('input[name="ipIds"]').forEach(input => input.remove());

    ipIds.forEach(ipId => {
        const hiddenInput = document.createElement('input');
        hiddenInput.type = 'hidden';
        hiddenInput.name = 'ipIds';
        hiddenInput.value = ipId;
        form.appendChild(hiddenInput);
    });

    const modal = new bootstrap.Modal(document.getElementById('purchaseOrderModalDel'));
    modal.show();
});

document.querySelector(".clearBtn").addEventListener("click", function (e) {
    e.preventDefault()
    e.stopPropagation()

    self.location = '/inPut/inPutList'
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

    document.querySelectorAll(".ipState option").forEach(option => {
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