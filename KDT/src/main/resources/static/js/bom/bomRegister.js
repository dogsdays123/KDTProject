function addPlan() {
    const productName = document.getElementById('productName').value;
    const productCode = document.getElementById('productCode').value;
    const partName = document.getElementById('partName').value;
    const materialName = document.getElementById('materialName').value;
    const productQuantity = document.getElementById('productQuantity').value;


    if (!productName || !productCode || !partName || !materialName || !productQuantity) {
        alert('모든 항목을 입력해 주세요!');
        return;
    }

    const tableBody = document.querySelector("#planTable tbody");

    const newRow = document.createElement('tr');

    newRow.innerHTML = `
        <td><input type="hidden" name="productNames[]" value="${productName}">${productName}</td> 
        <td><input type="hidden" name="productCodes[]" value="${productCode}">${productCode}</td> 
        <td><input type="hidden" name="partNames[]" value="${partName}">${partName}</td> 
        <td><input type="hidden" name="materialNames[]" value="${materialName}">${materialName}</td> 
        <td><input type="hidden" name="productQuantities[]" value="${productQuantity}">${productQuantity}</td> 
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
    document.getElementById('productCode').selectedIndex = 0;
    document.getElementById('partName').value = '';
    document.getElementById('materialName').value = '';
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

document.getElementById("productName").addEventListener("change", function() {
    var selectedOption = this.options[this.selectedIndex];  // 선택한 상품 옵션
    var productCode = selectedOption.getAttribute("data-code");  // 상품 코드 가져오기
    document.getElementById("productCode").value = productCode;

    if (productCode) {
        // 상품코드에 맞는 부품명 목록을 가져옵니다.
        fetch(`/product/api/products/${productCode}/component-types`)
            .then(response => response.json())
            .then(componentTypes => {
                const componentSelect = document.getElementById("componentType");
                componentSelect.innerHTML = '<option value="" selected>선택</option>'; // 초기화
                componentTypes.forEach(type => {
                    let option = document.createElement("option");
                    option.value = type;
                    option.textContent = type;
                    componentSelect.appendChild(option);
                });
            })
            .catch(error => console.error('Error fetching component types:', error));
    }// 상품 코드 입력란에 설정
});


document.getElementById("componentType").addEventListener("change", function() {
    let componentType = this.value;
    if (componentType) {
        // 부품명에 맞는 자재 목록을 가져옵니다.
        fetch(`/product/api/materials?componentType=${componentType}`)
            .then(response => response.json())
            .then(materials => {
                console.log(materials);  // 응답 데이터 확인
                const materialSelect = document.getElementById("materialList");
                materialSelect.innerHTML = '<option value="" selected>선택</option>'; // 초기화
                if (Array.isArray(materials)) {
                    materials.forEach(material => {
                        let option = document.createElement("option");
                        option.value = material.id; // 자재 ID로 설정
                        option.textContent = material.name; // 자재 이름을 표시
                        materialSelect.appendChild(option);
                    });
                } else {
                    console.error("Returned data is not an array:", materials);
                }
            })
            .catch(error => console.error('Error fetching materials:', error));
    }
});