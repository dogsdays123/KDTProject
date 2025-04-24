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
    const materialName = firstRow[2].innerText;
    const materialQuantity = firstRow[4].innerText;

    document.getElementById('requestQuantity').addEventListener('input', function () {
        const availableQuantity = parseInt(firstRow[4].innerText); // 납입 가능 수량
        const requestQuantity = parseInt(this.value); // 입력한 납입 요청 수량

        if (requestQuantity > availableQuantity) {
            alert('출고 수량은 출고 가능 수량을 초과할 수 없습니다.');
            this.value = ''; // 잘못 입력한 값 비우기
            this.focus();
        }
    });

    document.getElementById('planCodeInput').innerText = planCodeInput;
    document.getElementById('materialName').innerText = materialName;
    document.getElementById('materialQuantity').innerText = materialQuantity;

    const modal = new bootstrap.Modal(document.getElementById('purchaseOrderModal'));
    modal.show();

});