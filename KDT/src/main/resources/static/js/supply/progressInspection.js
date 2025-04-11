document.getElementById('openPurchaseModal').addEventListener('click', function () {
    const selected = document.querySelector('.selectPlan:checked');
    if (!selected) {
        alert('진척 검수를 요청할 발주를 선택해주세요.');
        return;
    }
    const modal = new bootstrap.Modal(document.getElementById('purchaseOrderModal'));
    modal.show();
});