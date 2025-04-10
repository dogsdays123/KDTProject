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

    const planRows = tableBody.querySelectorAll('tr:not(#registerRow)');
    if (planRows.length === 0) {
        const existingRegisterRow = document.getElementById('registerRow');
        if (existingRegisterRow) {
            existingRegisterRow.remove();
        }
        return; // 항목 없으면 함수 종료
    }

// 등록/삭제 버튼 추가 또는 위치 재정렬
    if (!document.getElementById('registerRow')) {
        const registerRow = document.createElement('tr');
        registerRow.id = 'registerRow';
        registerRow.innerHTML = `
        <td colspan="11" class="text-center" style="padding: 10px">
            <div class="d-flex justify-content-center gap-2">
                <button type="submit" class="btn btn-dark btn-sm">전체 등록</button>
                <button type="button" class="btn btn-outline-dark btn-sm" onclick="clearPlanTable()">전체 삭제</button>
            </div>
        </td>
    `;
        tableBody.appendChild(registerRow);
    } else {
        // 항상 버튼을 마지막으로 이동
        const registerRow = document.getElementById('registerRow');
        tableBody.appendChild(registerRow);
    }``
    // 입력값 초기화
    document.getElementById('pCode').value = '';
    document.getElementById('pName').value = '';
}

// 전체 등록 버튼 활성화/비활성화 함수
// function toggleSubmitButton() {
//     const tableBody = document.querySelector("#planTable tbody");
//     const submitBtn = document.getElementById('submitBtn');
//
//     // 테이블에 항목이 있으면 버튼 활성화, 없으면 비활성화
//     if (tableBody.querySelectorAll('tr').length > 0) {
//         submitBtn.disabled = false;
//     } else {
//         submitBtn.disabled = true;
//     }
// }

// 삭제 버튼 클릭 시 해당 행 삭제
function removeRow(button) {
    const row = button.closest('tr');
    row.remove();

    // #registerRow를 제외한 나머지 데이터 행이 0개인지 확인
    const tableBody = document.getElementById('goodsTbody'); // 실제 tbody id로 변경
    const remainingRows = tableBody.querySelectorAll('tr:not(#registerRow)');

    if (remainingRows.length === 0) {
        const registerRow = document.getElementById('registerRow');
        if (registerRow) {
            registerRow.remove();
        }
    }
}

function clearPlanTable() {
    const tableBody = document.querySelector("#planTable tbody");
    tableBody.innerHTML = ""; // 모든 row 삭제
}