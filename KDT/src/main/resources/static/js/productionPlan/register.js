function addPlan() {
    const ppStart = document.getElementById('ppStart').value;
    const ppEnd = document.getElementById('ppEnd').value;
    const pName = document.getElementById('pName').value;
    const ppCode = document.getElementById('ppCode').value;
    const ppNum = document.getElementById('ppNum').value;
    const uId = document.getElementById("uId").value;
    let rowIndex = 0;

    if (!ppStart || !ppEnd || !pName || !ppCode || !ppNum) {
        alert('모든 항목을 입력해 주세요!');
        return;
    }

    const tableBody = document.querySelector("#planTable tbody");
    const newRow = document.createElement('tr');

    newRow.innerHTML = `
        <td><input type="hidden" name="plans[${rowIndex}].ppStart" value="${ppStart}">${ppStart}</td>
        <td><input type="hidden" name="plans[${rowIndex}].ppEnd" value="${ppEnd}">${ppEnd}</td>
        <td><input type="hidden" name="plans[${rowIndex}].pName" value="${pName}">${pName}</td>
        <td><input type="hidden" name="plans[${rowIndex}].ppCode" value="${ppCode}">${ppCode}</td>
        <td><input type="hidden" name="plans[${rowIndex}].ppNum" value="${ppNum}">${ppNum}</td>   
        <td><input type="hidden" name="plans[${rowIndex}].uId" value="${uId}">${uId}</td>       
        <td>
          <button type="button" class="btn btn-outline-dark btn-sm" onclick="removeRow(this)" aria-label="삭제">
            <i class="bi bi-x-lg"></i>
          </button>
        </td>
    `;

    tableBody.appendChild(newRow);
    rowIndex++;

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
    document.getElementById('pName').selectedIndex = 0;
    document.getElementById('ppCode').value = '';

}

// 삭제 버튼 클릭 시 해당 행 삭제
function removeRow(button) {
    const row = button.closest('tr');
    row.remove();

    // #registerRow를 제외한 나머지 데이터 행이 0개인지 확인
    const tableBody = document.getElementById('ppTbody'); // 실제 tbody id로 변경
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

function triggerExcelUpload() {
    const input = document.getElementById('excelFile');
    input.value = ''; // ✅ 값을 비워서 항상 change 이벤트 발생
    input.click();
}


$(document).ready(function () {
    $('#excelUpload').on('click', function (e) {
        e.preventDefault();  // 기본 폼 제출 방지

        // 숨겨진 입력 필드의 값 가져오기
        var whereValue = $('input[name="where"]').val();  // "where"라는 이름의 input 값 가져오기
        var files = $('#excelFile')[0].files;  // 여러 파일 객체 가져오기
        var uId = $('input[name="uId"]').val();

        // FormData 객체 생성
        var formData = new FormData();

        // 여러 파일을 formData에 추가
        for (var i = 0; i < files.length; i++) {
            formData.append('file', files[i]);  // 'file' 이름으로 여러 파일 추가
        }

        formData.append('uId', uId);
        formData.append('where', whereValue);  // 숨겨진 필드 'where' 값 추가



        //--------------------------------업로드 파일 보기
        const fileList = document.getElementById('fileList');

        // 업로드된 파일 리스트 초기화
        fileList.innerHTML = ''; // 기존 파일 리스트 초기화

        if (files.length > 0) {
            document.getElementById('uploadedFileList').style.display = 'block';
        } else {
            document.getElementById('uploadedFileList').style.display = 'none';
        }

        // 여러 파일에 대해 반복
        Array.from(files).forEach(file => {
            const fileName = file.name;

            // 항목 생성
            const li = document.createElement('li');
            li.className = 'list-group-item d-flex justify-content-between align-items-center';
            li.textContent = fileName;
            
            fileList.appendChild(li);
        });
        //------------------------업로드 파일 보기



        // AJAX 요청 보내기
        $.ajax({
            url: '/productionPlan/addProductPlan',  // Controller의 URL
            method: 'POST',
            data: formData,  // FormData 객체를 데이터로 전송
            processData: false,  // jQuery가 데이터를 자동으로 처리하지 않도록 설정
            contentType: false,  // jQuery가 콘텐츠 타입을 자동으로 설정하지 않도록 설정
            success: function(response) {
                // 서버에서 응답이 성공적으로 왔을 때 처리
                if (response.isAvailable) {
                    alert("파일 업로드에 성공했습니다.(특정)");
                } else {
                    alert("파일 업로드에 성공했습니다.");
                }
            },
            error: function(xhr, status, error) {
                alert("파일 업로드에 실패했습니다. : " + error);
            }
        });
    });
});

// 파일 선택 이벤트 처리
document.getElementById('excelFile').addEventListener('change', function () {
    const fileList = this.files;  // 선택된 파일들
    const fileCount = fileList.length;  // 파일 갯수
    const fileListElement = document.getElementById('fileList');
    const fileCountElement = document.getElementById('fileCount');

    // 파일 목록을 초기화
    fileListElement.innerHTML = '';

    // 파일 갯수 표시
    fileCountElement.textContent = fileCount;

    // 파일 갯수가 0보다 크면 파일 목록을 표시, 그렇지 않으면 숨김 처리
    if (fileCount > 0) {
        document.getElementById('uploadedFileCount').style.display = 'block';
    } else {
        document.getElementById('uploadedFileCount').style.display = 'none';
    }
});

// 파일 갯수 업데이트 함수
function updateFileCount() {
    const fileListElement = document.getElementById('fileList');
    const fileCountElement = document.getElementById('fileCount');
    const fileCount = fileListElement.children.length; // 현재 남아있는 파일의 갯수
    fileCountElement.textContent = fileCount; // 갯수 업데이트

    // 파일 리스트가 비었으면 갯수와 리스트 숨기기
    if (fileCount === 0) {
        document.getElementById('uploadedFileCount').style.display = 'none';
    }
}

$(document).ready(function () {
    $('#pName').select2({
        placeholder: "검색",
        allowClear: true
    });
});