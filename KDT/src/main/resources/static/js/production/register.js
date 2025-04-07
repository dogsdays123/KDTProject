function addPlan() {
    const startDate = document.getElementById('startDate').value;
    const endDate = document.getElementById('endDate').value;
    const productName = document.getElementById('productName').value;
    const productCode = document.getElementById('productCode').value;
    const productQuantity = document.getElementById('productQuantity').value;

    if (!startDate || !endDate || !productName || !productCode || !productQuantity) {
        alert('모든 항목을 입력해 주세요!');
        return;
    }

    // 📌 테이블 헤더 보이기
    document.getElementById('tableHeader').style.display = 'table-header-group';

    const tableBody = document.querySelector("#planTable tbody");

    const newRow = document.createElement('tr');

    newRow.innerHTML = `
        <td><input type="hidden" name="startDates[]" value="${startDate}">${startDate}</td>
        <td><input type="hidden" name="endDates[]" value="${endDate}">${endDate}</td>
        <td><input type="hidden" name="productNames[]" value="${productName}">${productName}</td>
        <td><input type="hidden" name="productCodes[]" value="${productCode}">${productCode}</td>
        <td><input type="hidden" name="productQuantities[]" value="${productQuantity}">${productQuantity}</td>
        <td>
          <button type="button" style="border: none; background: transparent;" onclick="removeRow(this)">
            <i class="bi bi-x" style="font-size: 1.2rem; color: #888;"></i>
          </button>
        </td>
    `;

    tableBody.appendChild(newRow);

    // 입력값 초기화
    document.getElementById('productName').value = '';
    document.getElementById('productCode').value = '';
    document.getElementById('productQuantity').value = '';
}

function removeRow(btn) {
    const row = btn.closest('tr');
    row.remove();

    const tableBody = document.querySelector("#planTable tbody");
    if (tableBody.children.length === 0) {
        // 항목이 하나도 없으면 헤더 숨기기
        document.getElementById('tableHeader').style.display = 'none';
    }
}

function triggerExcelUpload() {
    const input = document.getElementById('excelFile');
    input.value = ''; // ✅ 값을 비워서 항상 change 이벤트 발생
    input.click();
}

document.getElementById('excelFile').addEventListener('change', function () {
    const file = this.files[0];

    if (file) {
        const fileName = file.name;
        const fileList = document.getElementById('fileList');

        // 항목 생성
        const li = document.createElement('li');
        li.className = 'list-group-item d-flex justify-content-between align-items-center';
        li.textContent = fileName;

        const deleteBtn = document.createElement('button');
        deleteBtn.className = 'btn btn-sm btn-danger';
        deleteBtn.textContent = 'x';
        deleteBtn.onclick = () => {
            li.remove();

            // 리스트가 비었으면 숨김 처리
            if (fileList.children.length === 0) {
                document.getElementById('uploadedFileList').style.display = 'none';
            }
        };

        li.appendChild(deleteBtn);
        fileList.appendChild(li);

        document.getElementById('uploadedFileList').style.display = 'block';
    }
});