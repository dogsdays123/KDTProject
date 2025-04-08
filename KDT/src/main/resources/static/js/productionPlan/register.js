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


$(document).ready(function () {
    $('#excelUpload').on('click', function (e) {
        e.preventDefault();  // 기본 폼 제출 방지

        // 숨겨진 입력 필드의 값 가져오기
        var whereValue = $('input[name="where"]').val();  // "where"라는 이름의 input 값 가져오기
        var files = $('#excelFile')[0].files;  // 여러 파일 객체 가져오기

        // FormData 객체 생성
        var formData = new FormData();

        // 여러 파일을 formData에 추가
        for (var i = 0; i < files.length; i++) {
            formData.append('file', files[i]);  // 'file' 이름으로 여러 파일 추가
        }

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
