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

    const firstRow = selectedRows[0].children;

    const supplier = firstRow[3].innerText;   // 공급업체
    const planCode = firstRow[2].innerText;   // 조달 계획 코드
    const productName = firstRow[5].innerText; // 자재명
    const quantity = firstRow[9].innerText;   // 조달 수량
    const date = firstRow[10].innerText;   // 조달 납기일


    // 모달에 값 주입
    document.getElementById('productSupplier').innerText = supplier;
    document.getElementById('planCodeInput').innerText = planCode;
    document.getElementById('productNameInput').innerText = productName;
    document.getElementById('productQtyInput').innerText = quantity;
    document.getElementById('productDate').innerText = date;

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

            materialName: cells[5].textContent.trim(),
            width: cells[6].textContent.trim(),
            depth: cells[7].textContent.trim(),
            height: cells[8].textContent.trim(),
            quantity: cells[9].textContent.trim() || "0",
            dueDate: cells[10].textContent.trim(),

            supplier: cells[3].textContent.trim(),
        });
    });

    return plans;
}

// ✅ 모달 입력값 수집 (이메일, 단가, 비고 등)
function collectFormData() {
    return {
        unitPrice: document.getElementById('unitPrice').value || "0", // 단가
        deliveryLocation: document.getElementById('deliveryLocation').value || "", // 납품 장소
        deliveryDate: document.getElementById('deliveryDate').value || "", // 희망 입고일
        significant: document.getElementById('orderMemo').value || "",
        payMethodCash: document.getElementById('payMethodCash').checked ? "Y" : "N",
        payMethodCard: document.getElementById('payMethodCard').checked ? "Y" : "N",
        payDate: document.getElementById('payDate').value || "",
        payDocumentsTex: document.getElementById('payDocumentsTex').checked ? "Y" : "N",
        payDocumentsCash: document.getElementById('payDocumentsCash').checked ? "Y" : "N",
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



function generatePurchaseOrderPDF(){
    const selectedPlans = collectSelectedPlans();
    if (selectedPlans.length === 0) {
        alert('조달계획을 최소 1개 이상 선택해주세요.');
        return;
    }
    const formData = collectFormData();
    formData.items = selectedPlans;

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
