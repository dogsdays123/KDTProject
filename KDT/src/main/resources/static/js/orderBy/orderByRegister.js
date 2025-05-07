let orderByList = [];
let worldValue = [];
let dppCode;

document.addEventListener('DOMContentLoaded', function () {
    const form = document.getElementById('orderByForm');
    const buttons = document.querySelectorAll('.openOrderByModal');
    const modalElement = document.getElementById('orderByModal');
    const tbody = document.getElementById('orderByBody');

    buttons.forEach(function (button) {
        button.addEventListener('click', function () {

            // 클릭된 버튼의 부모 <tr>을 찾음
            const row = button.closest('tr');
            const dppCode1 = row.children[0].textContent.trim();
            const pName = row.children[1].textContent.trim();
            const mName = row.children[3].textContent.trim();
            const dppNum = row.children[4].textContent.trim();
            const rqNum = row.children[5].textContent.trim();

            dppCode = dppCode1;

            // 모달에 값 주입
            document.getElementById('dppCodeInput').textContent = dppCode1;
            document.getElementById('pNameInput').textContent = pName;
            document.getElementById('mNameInput').textContent = mName;
            document.querySelector('.dppNumInput').value = dppNum;
            document.querySelector('.rqNumInput').value = rqNum;
            document.querySelector('.mPerPrice').value = document.querySelector('.mPerPriceHidden').value;

            loadMName(pName);

            // 모달 열기
            const modal = new bootstrap.Modal(document.getElementById('orderByModal'));
            modal.show();
        });
    });

    form.addEventListener('submit', function (event) {
        event.preventDefault(); // 기본 제출 막기

        alert(`구매 발주가 등록되었습니다`);

        // 폼을 서버에 전송하는 로직
        form.submit(); // 수동으로 다시 제출

        const modal = bootstrap.Modal.getInstance(document.getElementById('orderByModal'));
        if (modal) modal.hide();

        if (tbody) tbody.innerHTML = '';
        orderByList.length = 0;
    });
});

function addOrderBy(button) {
    const container = button.closest('.tab-pane'); // 현재 탭 안

    const textInputs = container.querySelectorAll('input[type="text"]');
    const numberInputs = container.querySelectorAll('input[type="number"]');
    const selectElements = container.querySelectorAll('select');
    const dateInput = container.querySelector('input[type="date"]');
    const oRemarks = document.getElementById('oRemarks').value;
    console.log(oRemarks);

// 발주서 코드(이제는 자동생성으로 돌릴거임)
    //const dppCode = textInputs.length > 0 ? textInputs[0].value.trim() : '';

// 공급업체, 자재명, 자재코드
    //const supplier = selectElements.length > 0 ? selectElements[0].value : '';

// 수량
    const oNum = numberInputs.length > 0 ? numberInputs[0].value.trim() : '';
    const oTotalPrice = numberInputs.length > 1 ? numberInputs[1].value.trim() : '';

// 납기일
    const oExpectDate = dateInput ? dateInput.value.trim() : '';

    // 유효성 체크 (선택)
    if (!oNum || !oTotalPrice || !oExpectDate) {
        alert('필수 정보를 입력해주세요');
        return;
    }

    // 데이터 객체 생성
    const item = {
        oNum,
        oTotalPrice,
        oExpectDate,
        oRemarks
    };

    console.log(item);

    orderByList.push(item);

    renderOrderByTable();
}


function renderOrderByTable() {
    const tbody = document.getElementById('orderByBody');
    tbody.innerHTML = ''; // 초기화
    let rowIndex = 0;

    orderByList.forEach((item, index) => {
        const row = document.createElement('tr');
        //이런식으로 input값 넣어줄거임
        row.innerHTML = `
      <td>
      <input type="hidden" name="orders[${rowIndex}].oCode" value="0">
      <input type="hidden" name="orders[${rowIndex}].dppCode" value="${dppCode}">
      <input type="hidden" name="orders[${rowIndex}].oNum" value="${item.oNum}">
        ${item.oNum}
        </td>
      <td><input type="hidden" name="orders[${rowIndex}].oTotalPrice" value="${item.oTotalPrice}">${item.oTotalPrice}</td>
      <td><input type="hidden" name="orders[${rowIndex}].oExpectDate" value="${item.oExpectDate}">${item.oExpectDate}</td>
      <td><input type="hidden" name="orders[${rowIndex}].oRemarks" value="${item.oRemarks}">${item.oRemarks}</td>
      <td class="text-center">
        <button class="btn btn-sm btn-outline-danger" onclick="removeOrderBy(${index})">삭제</button>
      </td>
    `;
        tbody.appendChild(row);
        rowIndex++;
    });
}

function removeOrderBy(index) {
    orderByList.splice(index, 1);
    renderOrderByTable();
}

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

function loadMCode(mName) {
    if (!mName) {return;}

    // URL 인코딩을 통해 상품명이 URL로 안전하게 전달되도록 함
    const encodes = encodeURIComponent(mName);

    // AJAX를 사용하여 부품 목록을 가져오는 코드
    $.ajax({
        url: `/dpp/${encodes}/mCode`,  // URL 인코딩 적용
        method: 'GET',  // HTTP GET 요청
        success: function (mCodes) {
            // 부품명 선택 요소 초기화
            const mCodeSelect = $('#mCode');
            mCodeSelect.empty();  // 기존 옵션 제거
            mCodeSelect.append('<option value="" selected>선택</option>');
            mCodes.forEach(type => {
                mCodeSelect.append(`<option value="${type}">${type}</option>`);
            });
            mCodeSelect.trigger('change');  // select2가 최신 값을 반영하도록 트리거
        },
        error: function (error) {
            console.error('목록을 가져오는 중 오류 발생:', error);
        }
    });
}


function resetView() {
    const modal = bootstrap.Modal.getInstance(document.getElementById('orderByModal'));

    const tbody = document.getElementById('orderByBody');
    if (tbody) tbody.innerHTML = '';
    orderByList.length = 0;

    if (modal) modal.hide();
}