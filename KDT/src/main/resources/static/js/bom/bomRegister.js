let bomList = [];
let errorChecks = null;
let duplicates = null;
let selectedFiles = []; // 전역 변수로 따로 관리
let worlds = [];

function addPlan() {

    const pName = document.getElementById('productName').value;
    const mComponentType = document.getElementById('componentType').value;
    const mName = document.getElementById('materialName').value;
    const mCode = document.getElementById('materialCode').value;
    const bRequireNum = document.getElementById('productQuantity').value;
    const uId = document.getElementById("uId").value;
    //uId는 따로 받아온다.

    if (!pName || !mComponentType || !mName || !bRequireNum) {
        alert('모든 항목을 입력해 주세요!');
        return;
    }

    // 데이터 객체 생성
    const item = {
        pName,
        mComponentType,
        mName,
        mCode,
        bRequireNum,
        uId
    };

    bomList.push(item);
    renderBomTable();
}

function renderBomTable() {
    const tableBody = document.querySelector("#planTable tbody");
    const newRow = document.createElement('tr');

    bomList.forEach((item, index) => {
        newRow.innerHTML = `
        <td><input type="hidden" name="boms[${index}].pName" value="${item.pName}">${item.pName}</td>
        <td><input type="hidden" name="boms[${index}].mComponentType" value="${item.mComponentType}">${item.mComponentType}</td>
        <td><input type="hidden" name="boms[${index}].mName" value="${item.mName}">${item.mName}</td>
        <td><input type="hidden" name="boms[${index}].mCode" value="${item.mCode}">${item.mCode}</td>
        <td><input type="hidden" name="boms[${index}].bRequireNum" value="${item.bRequireNum}">${item.bRequireNum}</td>
        <input type="hidden" name="boms[${index}].uId" value="${item.uId}">
        <td>
          <button type="button" class="icon-button" onclick="removeRow(this)" aria-label="삭제" title="해당 행 삭제">
            <i class="bi bi-x-lg"></i>
          </button>
        </td>
    `;

        tableBody.appendChild(newRow);
    });

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
    }``
    // 입력값 초기화
    document.getElementById('productName').selectedIndex = 0;
    document.getElementById('componentType').selectedIndex = 0;
    document.getElementById('materialName').selectedIndex = 0;
    document.getElementById('materialCode').selectedIndex = 0;
    document.getElementById('productQuantity').value = '';
}

// 삭제 버튼 클릭 시 해당 행 삭제
function removeRow(button) {
    const row = button.closest('tr');
    row.remove();

    // #registerRow를 제외한 나머지 데이터 행이 0개인지 확인
    const tableBody = document.getElementById('bomTbody'); // 실제 tbody id로 변경
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

    //----------------------업로드된 파일 확인용

    const formData = new FormData();
    const uId = $('#uId').val();
    console.log(uId);

    selectedFiles.forEach(file => {
        formData.append('file', file);
    });

    formData.append('uId', uId);
    formData.append('check', "true");

    // AJAX 요청 보내기
    $.ajax({
        url: '/bom/addBom',
        method: 'POST',
        data: formData,
        processData: false,
        contentType: false,
        success: function(response) {
            errorChecks = response.errorCheck;
            duplicates = response.duplicate;
            console.log(errorChecks);
            console.log(duplicates);
        },
        error: function(xhr, status, error) {
            alert("파일 읽기에 실패했습니다. : " + error);
        }
    });
    //--------------------------------------

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
        const workbook = XLSX.read(data, { type: 'binary', cellDates: true }); // cellDates: true 추가
        const sheet = workbook.Sheets[workbook.SheetNames[0]];
        const rows = XLSX.utils.sheet_to_json(sheet, { header: 1 });

        const tableHeader = document.getElementById('tableHeader');
        const tableBody = document.getElementById('tableBody');

        tableHeader.innerHTML = '';
        tableBody.innerHTML = '';

        rows[0]?.forEach(header => {
            const th = document.createElement('th');
            th.textContent = header;
            tableHeader.appendChild(th);
        });

        rows.slice(1).forEach(row => {
            const tr = document.createElement('tr');
            tr.setAttribute('data-file-name', file.name);

            row.forEach((cell, colIndex) => {
                const td = document.createElement('td');
                td.textContent = cell;

                if (colIndex === 0 && errorChecks.includes(cell)) {
                    td.style.color = 'red';
                    td.style.fontWeight = 'bold';
                } else if (colIndex === 1) {
                    const currentPName = row[0]; // 상품명은 0번째 열
                    const currentMName = cell;   // 3번째 열: 부품명
                    const isDuplicate = duplicates?.some(d =>
                        d.pName === currentPName && d.mName === currentMName
                    );

                    if (isDuplicate) {
                        td.style.color = 'red';
                        td.style.fontWeight = 'bold';
                    }
                }

                tr.appendChild(td);
            });
            tableBody.appendChild(tr);
        });

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

    selectedFiles.forEach(file => {
        formData.append('file', file);
    });

    formData.append('uId', uId);
    console.log(uId);
    formData.append('check', "false");

    // AJAX 요청 보내기
    $.ajax({
        url: '/bom/addBom',
        method: 'POST',
        data: formData,
        processData: false,
        contentType: false,
        beforeSend: function() {
            $('#loadingModal').modal('show');  // 로딩 모달 띄우기
        },
        success: function(response) {
            setTimeout(() => {
                $('#makeAdminModal').modal('hide');
            }, 500); // 0.5초 후에 닫기
            errorChecks = response.errorCheck;
            duplicates = response.duplicate;

            document.getElementById('fileList').innerHTML = '';
            document.getElementById('uploadedFileList').style.display = 'none';
            document.getElementById('fileTable').style.display = 'none';
            $('#excelFile').val('');
            document.getElementById('fileListContainer').style.display = 'none';

            if (confirm("목록 페이지로 이동하시겠습니까?")) {
                window.location.href = "/bom/bomList";
            } else {
                window.location.href = "/bom/bomRegister";
            }

        },
        error: function(xhr, status, error) {
            setTimeout(() => {
                $('#loadingModal').modal('hide');
            }, 500);
            alert("파일 업로드에 실패했습니다. : " + error);
        }
    });
});


$(document).ready(function () {
    // 상품 선택 시 동적으로 부품 목록 업데이트
    $('#productName').on('change', function () {
        const pName = $(this).val();  // 선택된 상품 값
         worlds[0] = pName;

        if (pName) {
            // URL 인코딩을 통해 상품명이 URL로 안전하게 전달되도록 함
            const pNameEncode = encodeURIComponent(pName);

            // AJAX를 사용하여 부품 목록을 가져오는 코드
            $.ajax({
                url: `/bom/${pNameEncode}/forMType`,  // URL 인코딩 적용
                method: 'GET',  // HTTP GET 요청
                success: function (componentTypes) {
                    // 부품명 선택 요소 초기화
                    const cType = $('#componentType');
                    cType.empty();  // 기존 옵션 제거

                    // "선택" 옵션 추가
                    cType.append('<option value="" selected>전체</option>');

                    // 받아온 부품 목록을 옵션으로 추가
                    componentTypes.forEach(type => {
                        cType.append(`<option value="${type}">${type}</option>`);
                    });

                    // select2 업데이트
                    cType.trigger('change');  // select2가 최신 값을 반영하도록 트리거
                },
                error: function (error) {
                    console.error('부품 목록을 가져오는 중 오류 발생:', error);
                }
            });
        } else {
            $('#componentType').empty();  // 부품 목록 초기화
            var typeHTML = $('#typeHTML').html();  // 서버에서 렌더링된 HTML 가져오기
            $('#componentType').append(typeHTML);  // mNameList의 option을 append
            $('#componentType').trigger('change');  // 변경 이벤트 트리거
        }
    });
});


$(document).ready(function () {
    $('#componentType').on('change', function () {
        const type = $(this).val();  // 선택된 상품 값
        worlds[1] = type;

        if (type) {
            const encodes = [];
            encodes[0] = encodeURIComponent(worlds[0]);
            encodes[1] = encodeURIComponent(type);

            $.ajax({
                url: `/bom/${encodes[0]}/${encodes[1]}/forMName`,  // URL 인코딩 적용
                method: 'GET',  // HTTP GET 요청
                success: function (mNames) {

                    const mName = $('#materialName');
                    mName.empty();  // 기존 옵션 제거
                    mName.append('<option value="" selected>전체</option>');
                    mNames.forEach(type => {
                        mName.append(`<option value="${type}">${type}</option>`);
                    });
                    mName.trigger('change');  // select2가 최신 값을 반영하도록 트리거
                },
                error: function (error) {
                    console.error('부품 목록을 가져오는 중 오류 발생:', error);
                }
            });
        } else {
            $('#materialName').empty();  // 부품 목록 초기화
            var mNameHTML = $('#mNameHTML').html();  // 서버에서 렌더링된 HTML 가져오기
            $('#materialName').append(mNameHTML);  // mNameList의 option을 append
            $('#materialName').trigger('change');  // 변경 이벤트 트리거
        }
    });
});

$(document).ready(function () {
    $('#materialName').on('change', function () {
        const mName = $(this).val();  // 선택된 상품 값

        if (mName) {
            const encodes = [];
            encodes[0] = encodeURIComponent(worlds[1]);
            encodes[1] = encodeURIComponent(mName);

            $.ajax({
                url: `/bom/${encodes[0]}/${encodes[1]}/forMCode`,  // URL 인코딩 적용
                method: 'GET',  // HTTP GET 요청
                success: function (mCodes) {

                    const mCode = $('#materialCode');
                    mCode.empty();  // 기존 옵션 제거
                    mCode.append('<option value="" selected>전체</option>');
                    mCodes.forEach(type => {
                        mCode.append(`<option value="${type}">${type}</option>`);
                    });
                    mCode.trigger('change');  // select2가 최신 값을 반영하도록 트리거
                },
                error: function (error) {
                    console.error('부품 코드를 가져오는 중 오류 발생:', error);
                }
            });
        } else {
            $('#materialCode').empty();  // 부품 목록 초기화
            var mCodeHTML = $('#mCodeHTML').html();  // 서버에서 렌더링된 HTML 가져오기
            $('#materialCode').append(mCodeHTML);  // mNameList의 option을 append
            $('#materialCode').trigger('change');  // 변경 이벤트 트리거
        }
    });
});