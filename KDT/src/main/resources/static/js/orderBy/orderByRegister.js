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

            // 모달 열기
            const modal = new bootstrap.Modal(document.getElementById('orderByModal'));
            modal.show();
        });
    });

    //모든 폼 자동제출 막기
    document.querySelectorAll('form').forEach(form => {
        form.addEventListener('keydown', function (e) {
            if (e.key === 'Enter' && e.target.tagName === 'INPUT') {
                e.preventDefault();
            }
        });
    });

    form.addEventListener('submit', function (e) {
        e.preventDefault();

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

function resetView() {
    const modal = bootstrap.Modal.getInstance(document.getElementById('orderByModal'));

    const tbody = document.getElementById('orderByBody');
    if (tbody) tbody.innerHTML = '';
    orderByList.length = 0;

    if (modal) modal.hide();
}

$(document).ready(function () {

    $('#orderInput').on('change', function () {
        const input = $(this).val();  // 선택된 상품 값
        loadTotalPrice(input);
    });

});

function loadTotalPrice(orderInput) {
    if (!orderInput || isNaN(orderInput)) return;

    const unitPrice = parseFloat($('.mPerPrice').val());  // 단가
    if (isNaN(unitPrice)) return;

    const total = unitPrice * orderInput;

    const totalPrice = $('#totalPrice');
    totalPrice.val(total);  // input 요소일 경우
    totalPrice.trigger('change');  // 리스너가 있을 경우만 유효
}
