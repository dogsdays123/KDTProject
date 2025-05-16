document.getElementById('selectAll').addEventListener('change', function () {
    document.querySelectorAll('.selectPlan').forEach(cb => {
        cb.checked = this.checked;
    });
});

document.getElementById('openModifyModal').addEventListener('click', function () {
    const row = this.closest('tr'); // 클릭한 버튼이 속한 tr

    // 각 td 값을 가져오기
    const ppCode = row.querySelector('td:nth-child(2)').innerText;
    const pCode = row.querySelector('td:nth-child(3)').innerText;

    document.getElementById('ppProductCode').value = ppCode;
    document.getElementById('ppProductName').value = pCode;

    // 모달 띄우기
    const modal = new bootstrap.Modal(document.getElementById('ModifyModal'));
    modal.show();
});

document.getElementById('openRemoveModal').addEventListener('click', function () {
    const selectedRows = Array.from(document.querySelectorAll('.selectPlan:checked'))
        .map(cb => cb.closest('tr')); // 체크된 체크박스의 행 가져오기

    if (selectedRows.length === 0) {
        alert('하나 이상의 항목을 선택해주세요.');
        return;
    }

    const tbody = document.getElementById('deleteTableBody');
    tbody.innerHTML = ''; // 기존 내용 비우기

    selectedRows.forEach(checkbox => {
        const row = checkbox.closest('tr');
        const cells = row.querySelectorAll('td');
        const newRow = document.createElement('tr');

        newRow.innerHTML = `
            <td>${cells[2].textContent.trim()}</td>
            <td>${cells[4].textContent.trim()}</td>
            <td>${cells[5].textContent.trim()}</td>
            <td>${cells[6].textContent.trim()}</td>
            <td>${cells[7].textContent.trim()}</td>
        `;

        tbody.appendChild(newRow);
    });


    const modal = new bootstrap.Modal(document.getElementById('RemoveModal'));
    modal.show();
});

$(document).ready(function () {

    $('#mName').on('change', function () {
        const input = $(this).val();  // 선택된 상품 값
        loadMCode(input);
    });

});

function loadMName(pName) {
    if (!pName) {return;}

    // URL 인코딩을 통해 상품명이 URL로 안전하게 전달되도록 함
    const encodes = encodeURIComponent(pName);

    // AJAX를 사용하여 부품 목록을 가져오는 코드
    $.ajax({
        url: `/dpp/${encodes}/mName`,  // URL 인코딩 적용
        method: 'GET',  // HTTP GET 요청
        success: function (mNames) {
            console.log(mNames);
            // 부품명 선택 요소 초기화
            const mNameSelect = $('#mName');
            mNameSelect.empty();  // 기존 옵션 제거
            mNameSelect.append('<option value="" selected>선택</option>');
            mNames.forEach(type => {
                mNameSelect.append(`<option value="${type}">${type}</option>`);
            });
            mNameSelect.trigger('change');  // select2가 최신 값을 반영하도록 트리거
        },
        error: function (error) {
            console.error('목록을 가져오는 중 오류 발생:', error);
        }
    });
}


// $(document).ready(function () {
//     $('#ppCode').on('change', function () {
//         const ppCode = $(this).val();  // 선택된 상품 값
//
//         if (ppCode) {
//             const ppCodeEncode = encodeURIComponent(ppCode);
//
//             $.ajax({
//                 url: `/dpp/${ppCodeEncode}/ppCode`,  // URL 인코딩 적용
//                 method: 'GET',  // HTTP GET 요청
//                 success: function (ppCode) {
//
//                     const dppCodeSelect = $('#dppCode');
//                     dppCodeSelect.empty();  // 기존 옵션 제거
//                     dppCodeSelect.append('<option value="" selected>전체</option>');
//                     ppCode.forEach(type => {
//                         dppCodeSelect.append(`<option value="${type}">${type}</option>`);
//                     });
//                     dppCodeSelect.trigger('change');  // select2가 최신 값을 반영하도록 트리거
//                 },
//                 error: function (error) {
//                     console.error('부품 목록을 가져오는 중 오류 발생:', error);
//                 }
//             });
//         } else {
//             $('#dppCode').empty();  // 부품 목록 초기화
//             var dppCodeHTML = $('#dppCodeHTML').html();  // 서버에서 렌더링된 HTML 가져오기
//             $('#dppCode').append(dppCodeHTML);  // mNameList의 option을 append
//             $('#dppCode').trigger('change');  // 변경 이벤트 트리거
//         }
//     });
// });

