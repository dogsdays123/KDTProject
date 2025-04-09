// 제품을 추가하는 함수
function addPlan() {
    const productCode = document.getElementById('pCode').value;
    const productName = document.getElementById('pName').value;

    if (!productName || !productCode) {
        alert('모든 항목을 입력해 주세요!');
        return;
    }

    const tableBody = document.querySelector("#planTable tbody");

    const newRow = document.createElement('tr');

    newRow.innerHTML = `
            <td><input type="hidden" name="pCodes[]" value="${productCode}">${productCode}</td> 
            <td><input type="hidden" name="pNames[]" value="${productName}">${productName}</td>
            <td>
              <button type="button" class="btn btn-outline-dark btn-sm" onclick="removeRow(this)" aria-label="삭제">
                <i class="bi bi-x-lg"></i>
              </button>
            </td>
        `;

    tableBody.appendChild(newRow);

    // 전체 등록 버튼 활성화
    toggleSubmitButton();

    // 입력값 초기화
    document.getElementById('pCode').value = '';
    document.getElementById('pName').value = '';
}

// 삭제 버튼 클릭 시 해당 행 삭제
function removeRow(btn) {
    btn.closest('tr').remove();
    // 삭제 후 테이블에 항목이 남아 있으면 전체 등록 버튼 활성화, 없으면 비활성화
    toggleSubmitButton();
}

// 전체 등록 버튼 활성화/비활성화 함수
function toggleSubmitButton() {
    const tableBody = document.querySelector("#planTable tbody");
    const submitBtn = document.getElementById('submitBtn');

    // 테이블에 항목이 있으면 버튼 활성화, 없으면 비활성화
    if (tableBody.querySelectorAll('tr').length > 0) {
        submitBtn.disabled = false;
    } else {
        submitBtn.disabled = true;
    }
}

// 삭제 버튼 클릭 시 해당 행 삭제
function removeRow(btn) {
    btn.closest('tr').remove();
    // 삭제 후 테이블에 항목이 남아 있으면 전체 등록 버튼 활성화, 없으면 비활성화
    toggleSubmitButton();
}

// 전체 등록 버튼 활성화/비활성화 함수
function toggleSubmitButton() {
    const tableBody = document.querySelector("#planTable tbody");
    const submitBtn = document.getElementById('submitBtn');

    // 테이블에 항목이 있으면 버튼 활성화, 없으면 비활성화
    if (tableBody.querySelectorAll('tr').length > 0) {
        submitBtn.disabled = false;
    } else {
        submitBtn.disabled = true;
    }
}

function removeRow(btn) {
    btn.closest('tr').remove();
}

// 삭제 버튼 클릭 시 해당 행 삭제
function removeRow(btn) {
    btn.closest('tr').remove();
}

function clearPlanTable() {
    const tableBody = document.querySelector("#planTable tbody");
    tableBody.innerHTML = ""; // 모든 row 삭제
}