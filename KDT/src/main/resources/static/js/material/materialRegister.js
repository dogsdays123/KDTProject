function addPlan() {
    const pName = document.getElementById('pName').value;
    const mComponentType = document.getElementById('mComponentType').value;
    const mType = document.getElementById('mType').value;
    const mName = document.getElementById('mName').value;
    const mCode = document.getElementById('mCode').value;
    const supplier = document.getElementById('supplier').value;
    const leadTime = document.getElementById('leadTime').value;
    const depth = document.getElementById('depth').value;
    const height = document.getElementById('height').value;
    const width = document.getElementById('width').value;
    const weight = document.getElementById('weight').value;
    const unitPrice = document.getElementById('unitPrice').value;
    const mMinNum = document.getElementById('mMinNum').value;
    const uId = document.getElementById("uId").value;
    let rowIndex = 0;
    //uId는 따로 받아온다.

    if (!pName || !mComponentType || !mType || !mName || !mCode || !supplier || !leadTime
        || !depth || !height || !width || !weight || !unitPrice || !mMinNum) {
        alert('모든 항목을 입력해 주세요!');
        return;
    }

    const tableBody = document.querySelector("#planTable tbody");
    const newRow = document.createElement('tr');

    //<td><input type="hidden" name="supplier[${rowIndex}].supplier" value="${supplier}">${supplier}</td>

    newRow.innerHTML = `
        <td><input type="hidden" name="materials[${rowIndex}].mName" value="${mName}">${mName}</td>
        <td><input type="hidden" name="materials[${rowIndex}].mCode" value="${mCode}">${mCode}</td>
        <td><input type="hidden" name="materials[${rowIndex}].mComponentType" value="${mComponentType}">${mComponentType}</td>
        <td><input type="hidden" name="materials[${rowIndex}].mType" value="${mType}">${mType}</td>
        <td><input type="hidden" name="materials[${rowIndex}].pName" value="${pName}">${pName}</td>
        <td><input type="hidden" name="materials[${rowIndex}].mMinNum" value="${mMinNum}">${mMinNum}</td>
        <td><input type="hidden" name="materials[${rowIndex}].mDepth" value="${depth}">${depth}</td>
        <td><input type="hidden" name="materials[${rowIndex}].mHeight" value="${height}">${height}</td>
        <td><input type="hidden" name="materials[${rowIndex}].mWidth" value="${width}">${width}</td>
        <td><input type="hidden" name="materials[${rowIndex}].mWeight" value="${weight}">${weight}</td>
        <td><input type="hidden" name="materials[${rowIndex}].unitPrice" value="${unitPrice}">${unitPrice}</td>
        <td><input type="hidden" name="materials[${rowIndex}].mLeadTime" value="${leadTime}">${leadTime}</td>
        <td><input type="hidden" name="materials[${rowIndex}].uId" value="${uId}">${uId}</td> 
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
    }
    ``

    // 입력값 초기화
    document.getElementById('pName').selectedIndex = 0;
    document.getElementById('mComponentType').selectedIndex = 0;
    document.getElementById('mType').selectedIndex = 0;
    document.getElementById('mName').value = '';
    document.getElementById('mCode').value = '';
    document.getElementById('supplier').value = '';
    document.getElementById('leadTime').value = '';
    document.getElementById('depth').value = '';
    document.getElementById('height').value = '';
    document.getElementById('width').value = '';
    document.getElementById('weight').value = '';
    document.getElementById('unitPrice').value = '';
    document.getElementById('mMinNum').value = '';


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

$(document).ready(function () {
    $('#pName').select2({
        placeholder: "검색",
        allowClear: true
    });
});