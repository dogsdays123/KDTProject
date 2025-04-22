document.getElementById('selectAll').addEventListener('change', function () {
    document.querySelectorAll('.selectPlan').forEach(cb => {
        cb.checked = this.checked;
    });
});

document.getElementById('openPurchaseDelModal').addEventListener('click', function () {

    const selectedListEl = document.getElementById('selectedPlanList');
    selectedListEl.innerHTML = ''; // 기존 내용 초기화

    const selectedRows = document.querySelectorAll('.selectPlan:checked');

    selectedRows.forEach(checkbox => {
        const row = checkbox.closest('tr');
        const planCode = row.querySelector('td:nth-child(3)').innerText; // 조달계획코드 열
        const li = document.createElement('li');
        li.textContent = planCode;
        selectedListEl.appendChild(li);
    });

    const modal = new bootstrap.Modal(document.getElementById('purchaseOrderModalDel'));
    modal.show();
});
