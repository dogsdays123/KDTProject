document.getElementById('selectAll').addEventListener('change', function () {
    document.querySelectorAll('.selectPlan').forEach(cb => {
        cb.checked = this.checked;
    });
});

document.querySelectorAll('.icon-button').forEach(button => {
    button.addEventListener('click', function () {
        const row = this.closest('tr'); // 클릭한 버튼이 속한 tr

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
    const selectedRows = Array.from(document.querySelectorAll('.selectPlan:checked'))
        .map(cb => cb.closest('tr')); // 체크된 체크박스의 행 가져오기

    if (selectedRows.length === 0) {
        alert('하나 이상의 항목을 선택해주세요.');
        return;
    }


    // const selectedListEl = document.getElementById('selectedPlanList');
    // selectedListEl.innerHTML = ''; // 기존 내용 초기화
    //
    // const selectedRows = document.querySelectorAll('.selectPlan:checked');
    //
    // selectedRows.forEach(checkbox => {
    //     const row = checkbox.closest('tr');
    //     const planCode = row.querySelector('td:nth-child(1)').innerText; // 조달계획코드 열
    //     const li = document.createElement('li');
    //     li.textContent = planCode;
    //     selectedListEl.appendChild(li);
    // });

    const modal = new bootstrap.Modal(document.getElementById('purchaseOrderModalDel'));
    modal.show();
});
