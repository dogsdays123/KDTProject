let dppList = [];
let ppCode;
let mCodeForLeadTime;

document.addEventListener('DOMContentLoaded', function () {
    const form = document.getElementById('dppForm');
    const buttons = document.querySelectorAll('.openDppModal');

    buttons.forEach(function (button) {
        button.addEventListener('click', function () {

            // 클릭된 버튼의 부모 <tr>을 찾음
            const row = button.closest('tr');
            const ppCode1 = row.children[0].textContent.trim();
            const pName = row.children[1].textContent.trim();
            const ppNum = row.children[4].textContent.trim();

            ppCode = ppCode1;

            // 모달에 값 주입
            document.getElementById('planCodeInput').textContent = ppCode1;
            document.getElementById('productNameInput').textContent = pName;
            document.getElementById('productQtyInput').textContent = ppNum;

            loadMName(pName);

            // 모달 열기
            const modal = new bootstrap.Modal(document.getElementById('procurementModal'));
            modal.show();
        });
    });

    form.addEventListener('submit', function (event) {
        event.preventDefault(); // 기본 제출 막기

        alert(`조달 계획이 등록되었습니다`);

        // 폼을 서버에 전송하는 로직
        form.submit(); // 수동으로 다시 제출

        const modal = bootstrap.Modal.getInstance(document.getElementById('orderByModal'));
        if (modal) modal.hide();

        if (tbody) tbody.innerHTML = '';
        orderByList.length = 0;
    });
});

function addProcurement(button) {
    const container = button.closest('.tab-pane'); // 현재 탭 안

    const textInputs = container.querySelectorAll('input[type="text"]');
    const numberInputs = container.querySelectorAll('input[type="number"]');
    const selectElements = container.querySelectorAll('select');
    const dateInput = container.querySelector('input[type="date"]');

// 공급업체, 자재명, 자재코드
    const mName = selectElements.length > 0 ? selectElements[0].value : '';
    const mCode = selectElements.length > 1 ? selectElements[1].value : '';
    const supplier = selectElements.length > 2 ? selectElements[2].value : '';

// 수량
    const needQty = numberInputs.length > 0 ? numberInputs[0].value.trim() : '';
    const supplyQty = numberInputs.length > 1 ? numberInputs[1].value.trim() : '';

// 납기일
    const dueDate = dateInput ? dateInput.value.trim() : '';

    // 유효성 체크 (선택)
    if (!supplier || !mName || !mCode || !needQty || !supplyQty || !dueDate) {
        alert('필수 정보를 입력해주세요');
        return;
    }

    // 데이터 객체 생성
    const item = {
        supplier,
        mName,
        mCode,
        needQty,
        supplyQty,
        ppCode,
        dueDate
    };

    console.log(item);

    dppList.push(item);

    renderProcurementTable();
}


function renderProcurementTable() {
    const uId = document.getElementById('uId').value;
    const tbody = document.getElementById('dppListBody');
    tbody.innerHTML = ''; // 초기화
    let rowIndex = 0;

    dppList.forEach((item, index) => {
        const row = document.createElement('tr');
        //이런식으로 input값 넣어줄거임
        row.innerHTML = `
      <td><input type="hidden" name="dpps[${rowIndex}].mName" value="${item.mName}">${item.mName}</td>
      <td><input type="hidden" name="dpps[${rowIndex}].mCode" value="${item.mCode}">${item.mCode}</td>
      <td><input type="hidden" name="dpps[${rowIndex}].sName" value="${item.supplier}">${item.supplier}</td>
      <td class="text-end"><input type="hidden" name="dpps[${rowIndex}].dppRequireNum" value="${item.needQty}">${item.needQty}</td>
      <td class="text-end"><input type="hidden" name="dpps[${rowIndex}].dppNum" value="${item.supplyQty}">${item.supplyQty}</td>
      <td class="text-center"><input type="hidden" name="dpps[${rowIndex}].dppDate" value="${item.dueDate}">${item.dueDate}</td>
      <td class="text-center">
        <input type="hidden" name="dpps[${rowIndex}].ppCode" value="${item.ppCode}">
        <button class="btn btn-sm btn-outline-danger" onclick="removeProcurement(${index})">삭제</button>
      </td>
    `;
        tbody.appendChild(row);
        rowIndex++;
    });
}

function removeProcurement(index) {
    dppList.splice(index, 1);
    renderProcurementTable();
}

$(document).ready(function () {

    $('#mName').on('change', function () {
        const input = $(this).val();  // 선택된 상품 값
        loadMCode(input);
    });

    $('#mCode').on('change', function () {
        const input = $(this).val();  // 선택된 상품 값
        mCodeForLeadTime = input;
        loadSup(input);
    });

    $('#sup').on('change', function () {
        const input = $(this).val();  // 선택된 상품 값
        loadLeadTime(input, mCodeForLeadTime);
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

function loadMCode(mName) {
    if (!mName) {return;}

    // URL 인코딩을 통해 상품명이 URL로 안전하게 전달되도록 함
    const encodes = encodeURIComponent(mName);

    // AJAX를 사용하여 부품 목록을 가져오는 코드
    $.ajax({
        url: `/dpp/${encodes}/mCode`,  // URL 인코딩 적용
        method: 'GET',  // HTTP GET 요청
        success: function (mCodes) {
            console.log(mCodes);
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

function loadSup(mCode) {
    if (!mCode) {return;}

    // URL 인코딩을 통해 상품명이 URL로 안전하게 전달되도록 함
    const encodes = encodeURIComponent(mCode);

    // AJAX를 사용하여 부품 목록을 가져오는 코드
    $.ajax({
        url: `/dpp/${encodes}/ss`,  // URL 인코딩 적용
        method: 'GET',  // HTTP GET 요청
        success: function (sss) {
            console.log(sss);
            // 부품명 선택 요소 초기화
            const sssSelect = $('#sup');
            sssSelect.empty();  // 기존 옵션 제거
            sssSelect.append('<option value="" selected>선택</option>');
            sss.forEach(type => {
                sssSelect.append(`<option value="${type}">${type}</option>`);
            });
            sssSelect.trigger('change');  // select2가 최신 값을 반영하도록 트리거
        },
        error: function (error) {
            console.error('목록을 가져오는 중 오류 발생:', error);
        }
    });
}

function loadLeadTime(sup, mCode) {
    if (!sup) {return;}

    // URL 인코딩을 통해 상품명이 URL로 안전하게 전달되도록 함
    const encodes = [];
    encodes[0] = encodeURIComponent(sup);
    encodes[1] = encodeURIComponent(mCode);

    // AJAX를 사용하여 부품 목록을 가져오는 코드
    $.ajax({
        url: `/dpp/${encodes[0]}/${encodes[1]}/leadTime`,  // URL 인코딩 적용
        method: 'GET',  // HTTP GET 요청
        success: function (leadTime) {
            console.log(leadTime);
            // 리드타임 입력 필드에 값 설정
            const leadTimeInput = $('#leadTime');
            if (leadTime) {
                leadTimeInput.val(leadTime);
            } else {
                leadTimeInput.val('미배정');
            }
        },
        error: function (error) {
            console.error('리드타임 가져오다가 오류 발생:', error);
        }
    });
}

function resetView() {
    const modal = bootstrap.Modal.getInstance(document.getElementById('procurementModal'));

    const tbody = document.getElementById('dppListBOdy');
    if (tbody) tbody.innerHTML = '';
    dppList.length = 0;

    if (modal) modal.hide();
}