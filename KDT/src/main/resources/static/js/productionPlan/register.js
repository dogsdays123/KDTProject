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

    const tableBody = document.querySelector("#planTable tbody");

    const newRow = document.createElement('tr');

    newRow.innerHTML = `
        <td><input type="hidden" name="startDates[]" value="${startDate}">${startDate}</td>
        <td><input type="hidden" name="endDates[]" value="${endDate}">${endDate}</td>
        <td><input type="hidden" name="productNames[]" value="${productName}">${productName}</td>
        <td><input type="hidden" name="productCodes[]" value="${productCode}">${productCode}</td>
        <td><input type="hidden" name="productQuantities[]" value="${productQuantity}">${productQuantity}</td>
        <td><button type="button" class="btn btn-danger btn-sm" onclick="removeRow(this)">삭제</button></td>
    `;

    tableBody.appendChild(newRow);

    // 입력값 초기화
    document.getElementById('productName').value = '';
    document.getElementById('productCode').value = '';
    document.getElementById('productQuantity').value = '';
}

// 삭제 버튼 클릭 시 해당 행 삭제
function removeRow(btn) {
    btn.closest('tr').remove();
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