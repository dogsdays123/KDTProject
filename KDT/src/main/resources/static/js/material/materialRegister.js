function addPlan() {
    const productName = document.getElementById('productsName').value;
    const materialTypes = document.getElementById('materialTypes').value;
    const materialNames = document.getElementById('materialNames').value;
    const materialCodes = document.getElementById('materialCodes').value;
    const supplier = document.getElementById('supplier').value;
    const leadTime = document.getElementById('leadTime').value;
    const depth = document.getElementById('depth').value;
    const height = document.getElementById('height').value;
    const width = document.getElementById('width').value;
    const weight = document.getElementById('weight').value;
    const unitPrice = document.getElementById('unitPrice').value;
    const minSupplyNum = document.getElementById('minSupplyNum').value;

    if (!productName || !materialTypes || !materialNames || !materialCodes || !supplier || !leadTime
        || !depth || !height || !width || !weight || !unitPrice || !minSupplyNum) {
        alert('모든 항목을 입력해 주세요!');
        return;
    }

    const tableBody = document.querySelector("#planTable tbody");

    const newRow = document.createElement('tr');

    newRow.innerHTML = `
        <td><input type="hidden" name="materialNames[]" value="${materialNames}">${materialNames}</td>
        <td><input type="hidden" name="materialCodes[]" value="${materialCodes}">${materialCodes}</td>
        <td><input type="hidden" name="materialTypes[]" value="${materialTypes}">${materialTypes}</td>
        <td><input type="hidden" name="productNames[]" value="${productName}">${productName}</td>
        <td><input type="hidden" name="minSupplyNumes[]" value="${minSupplyNum}">${minSupplyNum}</td>
        <td><input type="hidden" name="depth[]" value="${depth}">${depth}</td>
        <td><input type="hidden" name="height[]" value="${height}">${height}</td>
        <td><input type="hidden" name="width[]" value="${width}">${width}</td>
        <td><input type="hidden" name="weight[]" value="${weight}">${weight}</td>
        <td><input type="hidden" name="unitPrice[]" value="${unitPrice}">${unitPrice}</td>
        <td><input type="hidden" name="supplier[]" value="${supplier}">${supplier}</td>
        <td><input type="hidden" name="leadTime[]" value="${leadTime}">${leadTime}</td>
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
    document.getElementById('productsName').selectedIndex = 0;
    document.getElementById('materialTypes').selectedIndex = 0;
    document.getElementById('materialNames').value = '';
    document.getElementById('materialCodes').value = '';
    document.getElementById('supplier').value = '';
    document.getElementById('leadTime').value = '';
    document.getElementById('depth').value = '';
    document.getElementById('height').value = '';
    document.getElementById('width').value = '';
    document.getElementById('weight').value = '';
    document.getElementById('unitPrice').value = '';
    document.getElementById('minSupplyNum').value = '';


}

// 삭제 버튼 클릭 시 해당 행 삭제
function removeRow(button) {
    const row = button.closest('tr');
    row.remove();

    // #registerRow를 제외한 나머지 데이터 행이 0개인지 확인
    const tableBody = document.getElementById('materialTableBody'); // 실제 tbody id로 변경
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