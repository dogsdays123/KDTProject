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
});

document.addEventListener("DOMContentLoaded", function() {
    // 모든 `drState` 값을 처리할 수 있는 코드
    const states = {
        ON_HOLD: "대기",
        APPROVAL: "승인",
        IN_PROGRESS: "진행 중",
        UNDER_INSPECTION: "검수중",
        RETURNED: "반품",
        FINISHED: "종료",
        REJECT: "거절",
        DELIVERED: "배달 됨",
        ARRIVED: "도착함",
        NOT_REMAINING: "남은 게 없음"
    };

    // 모든 drState 항목을 가져와서 처리
    document.querySelectorAll('[data-state]').forEach(function(td) {
        const state = td.getAttribute('data-state');
        td.textContent = states[state] || "알 수 없음"; // 상태를 한글로 변경
    });
});

document.querySelector(".clearBtn").addEventListener("click", function (e) {
    e.preventDefault()
    e.stopPropagation()

    self.location = '/inPut/inPutManage'
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
// document.getElementById('openPurchaseModal').addEventListener('click', function () {
//     const selectedRows = Array.from(document.querySelectorAll('.selectPlan:checked'))
//         .map(cb => cb.closest('tr')); // 체크된 체크박스의 행 가져오기
//
//     if (selectedRows.length === 0) {
//         alert('하나 이상의 항목을 선택해주세요.');
//         return;
//     }
//
//     const firstRow = selectedRows[0].children;
//
//     const planCodeInput = firstRow[1].innerText;
//     const materialName = firstRow[2].innerText;
//     const materialQuantity = firstRow[4].innerText;
//
//     document.getElementById('requestQuantity').addEventListener('input', function () {
//         const availableQuantity = parseInt(firstRow[4].innerText); // 납입 가능 수량
//         const requestQuantity = parseInt(this.value); // 입력한 납입 요청 수량
//
//         if (requestQuantity > availableQuantity) {
//             alert('출고 수량은 출고 가능 수량을 초과할 수 없습니다.');
//             this.value = ''; // 잘못 입력한 값 비우기
//             this.focus();
//         }
//     });
//
//     document.getElementById('planCodeInput').innerText = planCodeInput;
//     document.getElementById('materialName').innerText = materialName;
//     document.getElementById('materialQuantity').innerText = materialQuantity;
//
//     const modal = new bootstrap.Modal(document.getElementById('purchaseOrderModal'));
//     modal.show();
//
// });