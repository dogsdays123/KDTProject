let pCodeChecks = null;
let pNameChecks = null;

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

    if (!pName || !mType || !mName || !mCode || !supplier || !leadTime
        || !depth || !height || !width || !weight || !unitPrice || !mMinNum) {
        alert('모든 항목을 입력해 주세요!');
        return;
    }

    const tableBody = document.querySelector("#planTable tbody");
    const newRow = document.createElement('tr');

    //<td><input type="hidden" name="supplier[${rowIndex}].supplier" value="${supplier}">${supplier}</td>

    newRow.innerHTML = `
        <td><input type="hidden" name="pNames[]" value="${pName}">${pName}</td>
        <td><input type="hidden" name="mComponentTypes[]" value="${mComponentType}">${mComponentType}</td>
        <td><input type="hidden" name="mTypes[]" value="${mType}">${mType}</td>
        <td><input type="hidden" name="mNames[]" value="${mName}">${mName}</td>
        <td><input type="hidden" name="mCodes[]" value="${mCode}">${mCode}</td>
        <td><input type="hidden" name="mMinNums[]" value="${mMinNum}">${mMinNum}</td>
        <td><input type="hidden" name="mDepths[]" value="${depth}">${depth}</td>
        <td><input type="hidden" name="mHeights[]" value="${height}">${height}</td>
        <td><input type="hidden" name="mWidths[]" value="${width}">${width}</td>
        <td><input type="hidden" name="mWeights[]" value="${weight}">${weight}</td>
        <td><input type="hidden" name="mUnitPrices[]" value="${unitPrice}">${unitPrice}</td>
        <td><input type="hidden" name="supplier[]" value="${supplier}">${supplier}</td>
        <td><input type="hidden" name="mLeadTime[]" value="${leadTime}">${leadTime}</td>
        <td><input type="hidden" name="uId[]" value="${uId}">${uId}</td> 
        <td>
          <button type="button" class="icon-button" onclick="removeRow(this)" aria-label="삭제" title="해당 행 삭제">
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
        <td colspan="15" class="text-center" style="padding: 10px">
            <div class="d-flex justify-content-center gap-2">
                <button type="button" class="btn btn-outline-dark btn-sm" onclick="clearPlanTable()">전체 삭제</button>
                <button type="submit" class="btn btn-dark btn-sm">전체 등록</button>
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
    document.getElementById('mComponentType').value = '';
    document.getElementById('mType').value = '';
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
        placeholder: "검색 또는 직접 입력",
        tags: true, // ← 이게 핵심! 직접 입력 허용
        allowClear: true,
        createTag: function (params) {
            const term = $.trim(params.term);
            if (term === '') {
                return null;
            }
            return {
                id: term,
                text: term,
                newTag: true // 사용자 입력값 구분
            };
        }
    });
});

let selectedFiles = []; // 전역 변수로 따로 관리


document.getElementById('excelFile').addEventListener('change', function(event) {
    const files = Array.from(event.target.files);
    selectedFiles = files;

    updateFileListUI();
});

function updateFileListUI() {
    const fileList = document.getElementById('fileListName');
    fileList.innerHTML = '';
    document.getElementById('fileListContainer').style.display = 'block';

    if (selectedFiles.length === 0) {
        document.getElementById('fileListContainer').style.display = 'none';
        document.getElementById('excelFile').value = '';
        return;
    }

    selectedFiles.forEach((file, index) => {
        const li = document.createElement('li');
        li.className = 'list-group-item d-flex justify-content-between align-items-center';
        li.setAttribute('data-index', index);

        const nameSpan = document.createElement('span');
        nameSpan.className = 'file-name';
        nameSpan.textContent = file.name;
        nameSpan.style.cursor = 'pointer';

        nameSpan.addEventListener('click', () => {
            loadFileContent(file, index);
        });

        const deleteBtnWrap = document.createElement('div');
        deleteBtnWrap.classList.add('tooltip-wrap-mtop');

        const tooltipText = document.createElement('span');
        tooltipText.classList.add('tooltip-text-mtop');
        tooltipText.textContent = '해당 파일 삭제';

        const deleteIcon = document.createElement('i');
        deleteIcon.className = 'bi bi-x-lg deleteIcon text-danger';
        deleteIcon.style.cursor = 'pointer';

        deleteBtnWrap.appendChild(deleteIcon);
        deleteBtnWrap.appendChild(tooltipText);

        deleteIcon.addEventListener('click', () => {
            selectedFiles.splice(index, 1);
            updateFileListUI();

            const currentTableFile = document.getElementById('fileTable').getAttribute('data-file-name');
            if (currentTableFile === file.name) {
                document.getElementById('fileTable').style.display = 'none';
            }
        });

        li.appendChild(nameSpan);
        li.appendChild(deleteBtnWrap);
        fileList.appendChild(li);
    });
}

function loadFileContent(file, index) {
    const reader = new FileReader();
    reader.onload = function(e) {
        const data = e.target.result;
        const workbook = XLSX.read(data, { type: 'binary' });
        const sheet = workbook.Sheets[workbook.SheetNames[0]];
        const rows = XLSX.utils.sheet_to_json(sheet, { header: 1 });

        const tableHeader = document.getElementById('tableHeader');
        const tableBody = document.getElementById('tableBody');

        tableHeader.innerHTML = '';
        tableBody.innerHTML = '';

        let pCodes = [];
        let pNames = [];

        rows[0]?.forEach(header => {
            const th = document.createElement('th');
            th.textContent = header;
            tableHeader.appendChild(th);
        });

        rows.slice(1).forEach(row => {
            const tr = document.createElement('tr');
            tr.setAttribute('data-file-name', file.name);

            const productCode = row[0];
            const productName = row[1];

            pCodes.push(productCode);
            pNames.push(productName);

            row.forEach(cell => {
                const td = document.createElement('td');
                td.textContent = cell;
                tr.appendChild(td);
            });
            tableBody.appendChild(tr);
        });

        console.log("pCodes: ", pCodes);
        console.log("pNames: ", pNames);

        const fileTable = document.getElementById('fileTable');
        fileTable.setAttribute('data-file-name', file.name);
        fileTable.style.display = 'block';
    };
    reader.readAsBinaryString(file);
}

$(document).on('click', '.deleteIcon', function () {
    const fileItem = $(this).closest('li');
    const fileName = fileItem.find('.file-name').text().trim();

    selectedFiles = selectedFiles.filter(file => file.name !== fileName);

    updateFileListUI();
});

$('#excelUpload').on('click', function (e) {
    e.preventDefault();

    if (selectedFiles.length === 0) {
        alert('업로드할 파일이 없습니다.');
        return;
    }

    const formData = new FormData();
    const uId = $('#uId').val();
    const whereValue = $('input[name="where"]').val();

    selectedFiles.forEach(file => {
        formData.append('file', file);
    });

    formData.append('uId', uId);
    formData.append('where', whereValue);
    formData.append('whereToGo', 'register');

    // AJAX 요청 보내기
    $.ajax({
        url: '/material/addMaterial',
        method: 'POST',
        data: formData,
        processData: false,
        contentType: false,
        success: function(response) {
            pCodeChecks = response.pCodes;
            pNameChecks = response.pNames;
            console.log(pCodeChecks);
            console.log(pNameChecks);
            if (response.isAvailable) {
                alert("파일 업로드에 성공했습니다.(특정)");
            } else {
                alert("파일 업로드에 성공했습니다.");
            }
            document.getElementById('fileList').innerHTML = '';
            document.getElementById('uploadedFileList').style.display = 'none';
            document.getElementById('fileTable').style.display = 'none';
            $('#excelFile').val('');
            document.getElementById('fileListContainer').style.display = 'none';

            if (confirm("목록 페이지로 이동하시겠습니까?")) {
                window.location.href = "/material/materialList";
            } else {
                window.location.href = "/material/materialRegister";
            }

        },
        error: function(xhr, status, error) {
            alert("파일 업로드에 실패했습니다. : " + error);
        }
    });
});