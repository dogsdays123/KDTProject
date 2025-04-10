document.getElementById('selectAll').addEventListener('change', function () {
    document.querySelectorAll('.selectPlan').forEach(cb => {
        cb.checked = this.checked;
    });
});



document.getElementById('openPurchaseModal').addEventListener('click', function () {
    const selected = document.querySelector('.selectPlan:checked');
    if (!selected) {
        alert('발주서를 작성할 조달계획을 선택해주세요.');
        return;
    }
    const modal = new bootstrap.Modal(document.getElementById('purchaseOrderModal'));
    modal.show();
});



function collectSelectedPlans() {
    const selected = document.querySelectorAll('.selectPlan:checked');
    const plans = [];

    selected.forEach(checkbox => {
        const row = checkbox.closest('tr');
        const cells = row.querySelectorAll('td');
        console.log("추출된 셀 수:", cells.length);
        cells.forEach((c, idx) => console.log(`셀 ${idx}:`, c.textContent.trim()));
        plans.push({
            procureCode: cells[2].textContent.trim(),
            materialCode: cells[3].textContent.trim(),
            materialName: cells[4].textContent.trim(),
            quantity: cells[5].textContent.trim() || "0",
            dueDate: cells[6].textContent.trim(),
            supplier: cells[7].textContent.trim(),

            // 규격은 현재 임시값 → 실제 입력값으로 바꾸려면 따로 getElementById 또는 class로 추출
            width: "30",
            height: "50",
            depth: "40"
        });
    });

    return plans;
}

// ✅ 모달 입력값 수집 (이메일, 단가, 비고 등)
function collectFormData() {
    return {
        supplierEmail: document.getElementById('supplierEmail').value,
        deliveryDate: document.getElementById('dueDate').value || "", // 공급 납기일
        unitPrice: document.getElementById('unitPrice').value || "0",
        significant: document.getElementById('orderMemo').value || ""
    };
}

// ✅ 미리보기 전송
function previewPurchaseOrderPDF() {
    const selectedPlans = collectSelectedPlans();
    if (selectedPlans.length === 0) {
        alert('조달계획을 최소 1개 이상 선택해주세요.');
        return;
    }

    const formData = collectFormData();
    formData.items = selectedPlans; // 👉 선택된 품목 리스트 추가

    fetch('/purchase/order/pdf/preview', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(formData)
    })
        .then(res => res.blob())
        .then(blob => {
            const url = window.URL.createObjectURL(blob);
            window.open(url, '_blank'); // 새 창으로 PDF 미리보기
        })
        .catch(() => alert('미리보기에 실패했습니다.'));
}



function generatePurchaseOrderPDF() {
    const formData = collectFormData();
    if (!formData.planCode) {
        alert('선택된 조달계획이 없습니다.');
        return;
    }

    fetch('/purchase/order/pdf/download', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(formData)
    })
        .then(res => res.blob())
        .then(blob => {
            const url = window.URL.createObjectURL(blob);
            const a = document.createElement('a');
            a.href = url;
            a.download = `발주서_${formData.planCode}.pdf`;
            a.click();
            alert('발주서가 PDF로 생성되어 메일로 전송되었습니다.');
            const modal = bootstrap.Modal.getInstance(document.getElementById('purchaseOrderModal'));
            if (modal) modal.hide();
        })
        .catch(() => alert('발주서 생성에 실패했습니다.'));
}
