// document.getElementById('selectAll').addEventListener('change', function () {
//     document.querySelectorAll('.selectPlan').forEach(cb => {
//         cb.checked = this.checked;
//     });
// });

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
    //
    const planCodeInput = firstRow[2].innerText;
    const materialName = firstRow[3].innerText;

    document.getElementById('planCodeInput').innerText = planCodeInput;
    document.getElementById('materialName').innerText = materialName;

    const modal = new bootstrap.Modal(document.getElementById('purchaseOrderModal'));
    modal.show();
});