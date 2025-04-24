document.getElementById('selectAll').addEventListener('change', function () {
    document.querySelectorAll('.selectPlan').forEach(cb => {
        cb.checked = this.checked;
    });
});

document.querySelectorAll(".totalPrice").forEach(el => {
    const value = parseInt(el.textContent, 10);
    el.textContent = value.toLocaleString(); // → "1,271,600"
});

document.querySelectorAll(".unitPrice").forEach(el => {
    const value = parseInt(el.textContent, 10);
    el.textContent = value.toLocaleString(); // → "1,271,600"
});

document.getElementById('openPurchaseModal').addEventListener('click', function () {
    const selectedRows = Array.from(document.querySelectorAll('.selectPlan:checked'))
        .map(cb => cb.closest('tr')); // 체크된 체크박스의 행 가져오기

    if (selectedRows.length === 0) {
        alert('하나 이상의 항목을 선택해주세요.');
        return;
    }

    if (selectedRows.length > 1) {
        alert('납입 처리는 검수 요청은 1개 항목만 선택 가능합니다.');
        return;
    }

    const firstRow = selectedRows[0].children;

    const planCodeInput = firstRow[1].innerText;
    const productSupplier = firstRow[2].innerText;
    const productNameInput = firstRow[6].innerText;
    const productQuantityInput = firstRow[3].innerText;


    document.getElementById('planCodeInput').innerText = planCodeInput;
    document.getElementById('productSupplier').innerText = productSupplier;
    document.getElementById('productNameInput').innerText = productNameInput;
    document.getElementById('productQuantityInput').innerText = productQuantityInput;

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
            materialName: cells[2].textContent.trim(),
            quantity: cells[3].textContent.trim(),
            unitPrice: cells[4].textContent.trim(),
            dueDate: cells[6].textContent.trim(),
            width: cells[10].textContent.trim(), // 가로
            depth: cells[11].textContent.trim(), // 깊이
            height: cells[12].textContent.trim() // 높이세로 규격 가로x깊이x높이
        });
    });

    return plans;
}

function previewPurchaseOrderPDF() {
    const selectedPlans = collectSelectedPlans();
    if (selectedPlans.length === 0) {
        alert('조달계획을 최소 1개 이상 선택해주세요.');
        return;
    }

    // const formData = collectFormData();
    const formData = {
        plans: selectedPlans
    };
    // formData.items = selectedPlans; // 👉 선택된 품목 리스트 추가

    fetch('/supplier/purchase/order/pdf/preview', {
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
    const formData = {
        plans: selectedPlans
    };

    fetch('/supplier/purchase/order/pdf/download', {
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

document.getElementById('openPurchaseDelModal').addEventListener('click', function () {

    const selectedRows = document.querySelectorAll('.selectPlan:checked');
    if (selectedRows.length === 0) {
        alert('삭제할 항목을 선택해 주세요.');
        return;
    }

    const tbody = document.getElementById('deleteTableBody');
    tbody.innerHTML = ''; // 기존 내용 비우기

    selectedRows.forEach(checkbox => {
        const row = checkbox.closest('tr');
        const cells = row.querySelectorAll('td');
        const newRow = document.createElement('tr');

        newRow.innerHTML = `
            <td>${cells[1].textContent.trim()}</td>
            <td>${cells[2].textContent.trim()}</td>
            <td>${cells[3].textContent.trim()}</td>
            <td>${cells[4].textContent.trim()}</td>
            <td>${cells[5].textContent.trim()}</td>
            <td>${cells[6].textContent.trim()}</td>
        `;

        tbody.appendChild(newRow);
    });

    const modal = new bootstrap.Modal(document.getElementById('purchaseOrderModalDel'));
    modal.show();
});