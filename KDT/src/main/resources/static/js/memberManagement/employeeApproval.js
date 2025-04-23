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
    // const firstRow = selectedRows[0].children;
    //
    // const supplier = firstRow[3].innerText;   // 공급업체
    // const planCode = firstRow[2].innerText;   // 조달 계획 코드
    // const productName = firstRow[5].innerText; // 자재명
    // const quantity = firstRow[9].innerText;   // 조달 수량
    // const date = firstRow[10].innerText;   // 조달 납기일
    //
    //
    // // 모달에 값 주입
    // document.getElementById('productSupplier').innerText = supplier;
    // document.getElementById('planCodeInput').innerText = planCode;
    // document.getElementById('productNameInput').innerText = productName;
    // document.getElementById('productQtyInput').innerText = quantity;
    // document.getElementById('productDate').innerText = date;

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
