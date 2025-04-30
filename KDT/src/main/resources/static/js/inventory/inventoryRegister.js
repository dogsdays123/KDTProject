function addPlan() {
    const productName = document.getElementById('productName').value;
    const productCode = document.getElementById('productCode').value;
    const componentType = document.getElementById('componentType').value;
    const materialList = document.getElementById('materialList');
    const materialCode = document.getElementById('materialCode').value;
    const isNum = document.getElementById('isNum').value;
    const isLocation = document.getElementById('isLocation').value;

    const materialName = materialList.options[materialList.selectedIndex].text;


    if (!productName || !productCode || !componentType || !materialList || !materialCode || !isNum
        || !isLocation) {
        alert('모든 항목을 입력해 주세요!');
        return;
    }

    const tableBody = document.querySelector("#planTable tbody");
    const newRow = document.createElement('tr');

    newRow.innerHTML = `
        <td><input type="hidden" name="pNames[]" value="${productName}">${productName}</td> 
        <td><input type="hidden" name="pCodes[]" value="${productCode}">${productCode}</td> 
        <td><input type="hidden" name="cTypes[]" value="${componentType}">${componentType}</td> 
        <td><input type="hidden" name="mNames[]" value="${materialName}">${materialName}</td> 
        <td><input type="hidden" name="mCodes[]" value="${materialCode}">${materialCode}</td> 
        <td><input type="hidden" name="isNums[]" value="${isNum}">${isNum}</td> 
        <td><input type="hidden" name="isLoca[]" value="${isLocation}">${isLocation}</td> 
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
    document.getElementById('productName').selectedIndex = 0;
    document.getElementById('productCode').value = '';
    document.getElementById('componentType').selectedIndex = 0;
    document.getElementById('materialList').selectedIndex = 0;
    document.getElementById('materialCode').value = '';
    document.getElementById('isNum').value = '';
    document.getElementById('isLocation').value = '';


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
        },
        theme: 'bootstrap-5' // 부트스트랩 5 테마 적용
    });
});

document.getElementById("productName").addEventListener("change", function() {
    var selectedOption = this.options[this.selectedIndex];  // 선택한 상품 옵션
    var productCode = selectedOption.getAttribute("data-code");  // 상품 코드 가져오기
    document.getElementById("productCode").value = productCode;

    if (productCode) {
        // 상품코드에 맞는 부품명 목록을 가져옵니다.
        fetch(`/bom/api/products/${productCode}/component-types`)
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
        fetch(`/bom/api/materials?componentType=${componentType}`)
            .then(response => response.json())
            .then(materials => {
                console.log(materials);  // 응답 데이터 확인
                const materialSelect = document.getElementById("materialList");
                materialSelect.innerHTML = '<option value="" selected>선택</option>'; // 초기화

                if (Array.isArray(materials) && materials.length > 0) {
                    materials.forEach(material => {
                        console.log(material);  // 각 material 객체 출력하여 mCode, mName 확인
                        let option = document.createElement("option");
                        option.value = material.mcode; // 자재 코드
                        option.textContent = material.mname; // 자재 이름
                        option.setAttribute("data-name", material.mname); // 자재 이름 저장
                        option.setAttribute("data-code", material.mcode);

                        materialSelect.appendChild(option);
                    });
                } else {
                    console.error("Returned data is not an array or is empty:", materials);
                }
            })
            .catch(error => console.error('Error fetching materials:', error));
    }
});

document.getElementById("materialList").addEventListener("change", function() {
    let selectedMaterial = this.options[this.selectedIndex];
    let materialCode = selectedMaterial.getAttribute("data-code");  // 자재 코드
    let materialName = selectedMaterial.getAttribute("data-name");  // 자재 이름

    // 자재 코드 입력 필드에 자재 코드 자동 입력
    document.getElementById("materialCode").value = materialCode;
    document.getElementById("materialList").options[this.selectedIndex].text = materialName;

});
