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
    const supplier = firstRow[2].innerText;
    const productNameInput = firstRow[4].innerText;
    const productQuantity = firstRow[7].innerText;

    document.getElementById('planCodeInput').innerText = planCodeInput;
    document.getElementById('supplier').innerText = supplier;
    document.getElementById('productNameInput').innerText = productNameInput;
    document.getElementById('productQuantity').innerText = productQuantity;

    const modal = new bootstrap.Modal(document.getElementById('purchaseOrderModal'));
    modal.show();
});