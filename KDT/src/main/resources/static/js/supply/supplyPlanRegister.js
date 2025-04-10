function openProcurementModal(planCode) {
    // 선택된 생산계획코드 세팅
    document.getElementById('selectedPlanCodeLabel').innerText = planCode;
    document.getElementById('selectedPlanCode').value = planCode;

    // 모달 열기
    const modal = new bootstrap.Modal(document.getElementById('procurementModal'));
    modal.show();
}

function resetView() {
    const modal = bootstrap.Modal.getInstance(document.getElementById('procurementModal'));
    if (modal) modal.hide();
}

document.addEventListener('DOMContentLoaded', function () {
    const form = document.getElementById('procurementFormElement');

    form.addEventListener('submit', function (event) {
        event.preventDefault();

        alert(`조달 계획이 등록되었습니다`);

        const modal = bootstrap.Modal.getInstance(document.getElementById('procurementModal'));
        if (modal) modal.hide();

        // 초기화 필요 시 여기에 추가
        const tbody = document.getElementById('procurementListBody');
        if (tbody) tbody.innerHTML = '';

        // 데이터 배열도 초기화 (예: JS에서 사용 중이라면)
        procurementList.length = 0;
    });
});



let procurementList = [];

function addProcurement(button) {
    const container = button.closest('.tab-pane'); // 현재 탭 안
    const category = container.id; // 예: wheel, seat 등

    // 입력값 가져오기 (name 속성 사용 권장)
    const code = container.querySelector('input[type="text"]').value.trim(); // 조달계획코드
    const supplier = container.querySelector('select').value;
    const materialName = container.querySelectorAll('input[type="text"]')[1].value.trim();
    const materialCode = container.querySelectorAll('input[type="text"]')[2].value.trim();
    const needQty = container.querySelectorAll('input[type="number"]')[0].value;
    const supplyQty = container.querySelectorAll('input[type="number"]')[2].value;
    const dueDate = container.querySelector('input[type="date"]').value;

    // 유효성 체크 (선택)
    if (!code || !supplier|| !materialName || !materialCode || !supplyQty || !dueDate) {
        alert('필수 정보를 입력해주세요');
        return;
    }

    // 데이터 객체 생성
    const item = {
        code,
        supplier,
        materialName,
        materialCode,
        needQty,
        supplyQty,
        dueDate,
        category
    };

    procurementList.push(item);

    const inputs = container.querySelectorAll('input, select');
    inputs.forEach(el => {
        if (el.tagName === 'INPUT') {
            if (el.type === 'text' || el.type === 'number' || el.type === 'date') {
                el.value = '';
            }
        } else if (el.tagName === 'SELECT') {
            el.selectedIndex = 0;
        }
    });

    renderProcurementTable();


}

function renderProcurementTable() {
    const tbody = document.getElementById('procurementListBody');
    tbody.innerHTML = ''; // 초기화

    procurementList.forEach((item, index) => {
        const row = document.createElement('tr');
        row.innerHTML = `
      <td>${item.code}</td>
      <td>${item.supplier}</td>
      <td>${item.materialName}</td>
      <td>${item.materialCode}</td>
      <td class="text-end">${item.needQty}</td>
      <td class="text-end">${item.supplyQty}</td>
      <td class="text-center">${item.dueDate}</td>
      <td class="text-center">
        <button class="btn btn-sm btn-outline-danger" onclick="removeProcurement(${index})">삭제</button>
      </td>
    `;
        tbody.appendChild(row);
    });
}

function removeProcurement(index) {
    procurementList.splice(index, 1);
    renderProcurementTable();
}