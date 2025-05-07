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
        .map(cb => cb.closest('tr'));

    if (selectedRows.length === 0) {
        alert('하나 이상의 항목을 선택해주세요.');
        return;
    }

    const firstRow = selectedRows[0].children;

    const drCode = firstRow[1].innerText;
    const oCode = firstRow[2].innerText;
    const mName = firstRow[4].innerText;
    const sName = firstRow[5].innerText;
    const drNum = parseInt(firstRow[6].textContent.trim()) || 0;

    const regDateInput = document.getElementById("regDate");
    const currentDate = new Date().toISOString().split('T')[0];

    console.log("납품 수량 (drNum):", drNum);
    document.getElementById('drCode').innerText = drCode;
    document.getElementById('hiddenDrCode').value = drCode;
    document.getElementById('hiddenOCode').value = oCode;
    document.getElementById('mName').innerText = mName;
    document.getElementById('hiddenMName').value = mName;
    document.getElementById('sName').innerText = sName;
    document.getElementById('hiddenSName').value = sName;
    document.getElementById('drNum').innerText = drNum;
    document.getElementById('hiddenDrNum').value = drNum;
    regDateInput.value = currentDate;

    const modal = new bootstrap.Modal(document.getElementById('purchaseOrderModal'));
    modal.show();

    const ipNumInput = document.getElementById("ipNum");
    const ipTrueNumInput = document.getElementById("ipTrueNum");
    const ipFalseNumInput = document.getElementById("ipFalseNum");

    function validateInputs() {
        const ipNum = parseInt(ipNumInput.value) || 0;
        const ipTrueNum = parseInt(ipTrueNumInput.value) || 0;
        const ipFalseNum = parseInt(ipFalseNumInput.value) || 0;

        if (ipNum > drNum) {
            alert("입고 수량은 납품 수량을 초과할 수 없습니다.");
            ipNumInput.value = '';
            return false;
        }


        if (ipTrueNum + ipFalseNum > ipNum) {
            alert("합격 수량과 불량 수량의 합은 입고 수량을 초과할 수 없습니다.");
            ipTrueNumInput.value = '';
            ipFalseNumInput.value = '';
            return false;
        }

        return true;
    }

    ipNumInput.addEventListener("input", validateInputs);
    ipTrueNumInput.addEventListener("input", validateInputs);
    ipFalseNumInput.addEventListener("input", validateInputs);

    ipTrueNumInput.addEventListener("blur", () => {
        const ipNum = parseInt(ipNumInput.value) || 0;
        const ipTrueNum = parseInt(ipTrueNumInput.value) || 0;

        if (ipTrueNum > ipNum) {
            alert("합격 수량은 입고 수량보다 많을 수 없습니다.");
            ipTrueNumInput.value = '';
            ipFalseNumInput.value = '';
            return;
        }

        ipFalseNumInput.value = ipNum - ipTrueNum;
    });
});

document.addEventListener("DOMContentLoaded", function () {
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

    document.querySelectorAll('[data-state]').forEach(function (td) {
        const stateKey = td.getAttribute('data-state');
        const state = states[stateKey] || states["UNKNOWN"];

        td.innerHTML = `<span class="${state.class}">${state.text}</span>`;
    });
});

document.querySelector(".clearBtn").addEventListener("click", function (e) {
    e.preventDefault()
    e.stopPropagation()

    self.location = '/inPut/inPutManage'
}, false)

// document.querySelector(".pagination").addEventListener("click", function (e) {
//     e.preventDefault()
//     e.stopPropagation()
//
//     const target = e.target
//
//     if (target.tagName !== 'A') {
//         return
//     }
//
//     const num = target.getAttribute("data-num")
//
//     const formObj = document.querySelector("form")
//
//     formObj.innerHTML += `<input type='hidden' name='page' value='${num}'>`
//
//     formObj.submit()
// }, false)
