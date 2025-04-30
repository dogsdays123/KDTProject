document.getElementById('selectAll').addEventListener('change', function () {
    document.querySelectorAll('.selectPlan').forEach(cb => {
        cb.checked = this.checked;
    });
});


document.querySelectorAll('.icon-button').forEach(button => {
    button.addEventListener('click', function () {
        const row = this.closest('tr');

        const bId = row.querySelector('td:nth-child(2)').innerText;
        const pCode = row.querySelector('td:nth-child(3)').innerText;
        const pName = row.querySelector('td:nth-child(4)').innerText;
        const cType = row.querySelector('td:nth-child(5)').innerText;
        const mCode = row.querySelector('td:nth-child(6)').innerText;
        const mName = row.querySelector('td:nth-child(7)').innerText;
        const reNum = row.querySelector('td:nth-child(8)').innerText;


        document.getElementById('bId').value = bId;
        document.getElementById('pCode').value = pCode;
        document.getElementById('pName').value = pName;
        document.getElementById('reNum').value = reNum;
        document.getElementById('mCode').value = mCode;


        fetch(`/bom/api/products/${pCode}/component-types`)
            .then(res => res.json())
            .then(componentTypes => {
                const cTypeSelect = document.getElementById("cType");
                cTypeSelect.innerHTML = '<option value="">선택</option>';
                componentTypes.forEach(type => {
                    let option = document.createElement("option");
                    option.value = type;
                    option.textContent = type;
                    cTypeSelect.appendChild(option);
                });


                cTypeSelect.value = cType;

                // cType 변경 시 자재 목록 불러오기
                cTypeSelect.addEventListener("change", function() {
                    const selectedCType = this.value;
                    document.getElementById('mCode').value = '';
                    if (selectedCType) {
                        fetch(`/bom/api/materials?componentType=${selectedCType}`)
                            .then(res => res.json())
                            .then(materials => {
                                const mNameSelect = document.getElementById('mName');
                                mNameSelect.innerHTML = '<option value="">선택</option>';

                                materials.forEach(material => {
                                    console.log(material);
                                    const option = document.createElement('option');
                                    option.value = material.mcode;
                                    option.textContent = material.mname;
                                    option.setAttribute("data-name", material.mname);
                                    option.setAttribute("data-code", material.mcode);
                                    mNameSelect.appendChild(option);
                                });

                                mNameSelect.replaceWith(mNameSelect.cloneNode(true));
                                const freshSelect = document.getElementById("mName");

                                freshSelect.addEventListener("change", function () {
                                    const selectedMaterial = this.options[this.selectedIndex];
                                    const materialCode = selectedMaterial.getAttribute("data-code");
                                    console.log("선택된 mcode:", materialCode);
                                    document.getElementById("mCode").value = materialCode;
                                });

                                const selectedOption = Array.from(mNameSelect.options).find(option => option.textContent === mName);
                                if (selectedOption) {
                                    mNameSelect.value = selectedOption.value;
                                } else {
                                    mNameSelect.value = materials.length > 0 ? materials[0].mcode : '';
                                }
                            })
                            .catch(error => console.error('Error fetching materials:', error));
                    }
                });

                // 최초 로드시, cType에 맞는 자재 목록 불러오기
                if (cType) {
                    fetch(`/bom/api/materials?componentType=${cType}`)
                        .then(res => res.json())
                        .then(materials => {
                            const mNameSelect = document.getElementById('mName');
                            mNameSelect.innerHTML = '<option value="">선택</option>';

                            materials.forEach(material => {
                                console.log(materials);
                                const option = document.createElement('option');
                                option.value = material.mcode;
                                option.textContent = material.mname;
                                option.setAttribute("data-name", material.mname);
                                option.setAttribute("data-code", material.mcode);
                                mNameSelect.appendChild(option);
                            });

                            const selectedOption = Array.from(mNameSelect.options).find(option => option.textContent === mName);
                            if (selectedOption) {
                                mNameSelect.value = selectedOption.value;
                                document.getElementById("mCode").value = selectedOption.getAttribute("data-code");
                            } else {
                                mNameSelect.value = materials.length > 0 ? materials[0].mcode : '';
                                document.getElementById("mCode").value = materials.length > 0 ? materials[0].mcode : '';
                            }

                            mNameSelect.addEventListener('change', function () {
                                const selectedMaterial = this.options[this.selectedIndex];
                                const materialCode = selectedMaterial.getAttribute("data-code");
                                document.getElementById("mCode").value = materialCode;
                            });

                        })
                        .catch(error => console.error('Error fetching materials:', error));
                }
            })
            .catch(error => console.error('Error fetching component types:', error));

        const modal = new bootstrap.Modal(document.getElementById('purchaseOrderModal'));
        modal.show();
    });
});


document.getElementById('openPurchaseDelModal').addEventListener('click', function () {

    const selectedRows = document.querySelectorAll('.selectPlan:checked');
    if (selectedRows.length === 0) {
        alert('삭제할 항목을 선택해 주세요.');
        return;
    }

    const tbody = document.getElementById('deleteTableBody');
    tbody.innerHTML = '';

    const bIds = [];

    selectedRows.forEach(checkbox => {
        const row = checkbox.closest('tr');
        const cells = row.querySelectorAll('td');
        const newRow = document.createElement('tr');

        const bId = cells[1].textContent.trim();
        bIds.push(bId);

        newRow.innerHTML = `
            <td class="d-none">${cells[1].textContent.trim()}</td>   
            <td>${cells[2].textContent.trim()}</td>
            <td>${cells[3].textContent.trim()}</td>
            <td>${cells[4].textContent.trim()}</td>
            <td>${cells[5].textContent.trim()}</td>
            <td>${cells[6].textContent.trim()}</td>
            <td>${cells[7].textContent.trim()}</td>
        `;

        tbody.appendChild(newRow);
    });

    const form = document.getElementById('purchaseOrderDelForm');
    form.querySelectorAll('input[name="bIds"]').forEach(input => input.remove());

    bIds.forEach(bId => {
        const hiddenInput = document.createElement('input');
        hiddenInput.type = 'hidden';
        hiddenInput.name = 'bIds';
        hiddenInput.value = bId;
        form.appendChild(hiddenInput);
    });

    const modal = new bootstrap.Modal(document.getElementById('purchaseOrderModalDel'));
    modal.show();
});

document.querySelector(".clearBtn").addEventListener("click", function (e) {
    e.preventDefault()
    e.stopPropagation()

    self.location = '/bom/bomList'
}, false)

