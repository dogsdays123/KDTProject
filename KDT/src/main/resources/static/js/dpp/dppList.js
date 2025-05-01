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

    const tbody = document.getElementById('procureTableBody');
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


//검색필터용
$(document).ready(function () {
    // 상품 선택 시 동적으로 부품 목록 업데이트
    $('#mName').on('change', function () {
        const mName = $(this).val();  // 선택된 상품 값

        if (mName) {
            // URL 인코딩을 통해 상품명이 URL로 안전하게 전달되도록 함
            const mNameEncode = encodeURIComponent(mName);

            // AJAX를 사용하여 부품 목록을 가져오는 코드
            $.ajax({
                url: `/dpp/${mNameEncode}/mName`,  // URL 인코딩 적용
                method: 'GET',  // HTTP GET 요청
                success: function (mCodes) {
                    // 부품명 선택 요소 초기화
                    const mCodesSelect = $('#mCode');
                    mCodesSelect.empty();  // 기존 옵션 제거

                    // "선택" 옵션 추가
                    mCodesSelect.append('<option value="" selected>전체</option>');

                    // 받아온 부품 목록을 옵션으로 추가
                    mCodes.forEach(type => {
                        mCodesSelect.append(`<option value="${type}">${type}</option>`);
                    });

                    // select2 업데이트
                    mCodesSelect.trigger('change');  // select2가 최신 값을 반영하도록 트리거
                },
                error: function (error) {
                    console.error('부품 목록을 가져오는 중 오류 발생:', error);
                }
            });
        } else {
            $('#mCode').empty();  // 부품 목록 초기화
            var mCodeHTML = $('#mCodeHTML').html();  // 서버에서 렌더링된 HTML 가져오기
            $('#mCode').append(mCodeHTML);  // mNameList의 option을 append
            $('#mCode').trigger('change');  // 변경 이벤트 트리거
        }
    });
});


$(document).ready(function () {
    $('#ppCode').on('change', function () {
        const ppCode = $(this).val();  // 선택된 상품 값

        if (ppCode) {
            const ppCodeEncode = encodeURIComponent(ppCode);

            $.ajax({
                url: `/dpp/${ppCodeEncode}/ppCode`,  // URL 인코딩 적용
                method: 'GET',  // HTTP GET 요청
                success: function (ppCode) {

                    const dppCodeSelect = $('#dppCode');
                    dppCodeSelect.empty();  // 기존 옵션 제거
                    dppCodeSelect.append('<option value="" selected>전체</option>');
                    ppCode.forEach(type => {
                        dppCodeSelect.append(`<option value="${type}">${type}</option>`);
                    });
                    dppCodeSelect.trigger('change');  // select2가 최신 값을 반영하도록 트리거
                },
                error: function (error) {
                    console.error('부품 목록을 가져오는 중 오류 발생:', error);
                }
            });
        } else {
            $('#dppCode').empty();  // 부품 목록 초기화
            var dppCodeHTML = $('#dppCodeHTML').html();  // 서버에서 렌더링된 HTML 가져오기
            $('#dppCode').append(dppCodeHTML);  // mNameList의 option을 append
            $('#dppCode').trigger('change');  // 변경 이벤트 트리거
        }
    });
});

