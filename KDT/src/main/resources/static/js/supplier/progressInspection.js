document.getElementById('selectAll').addEventListener('change', function () {
    document.querySelectorAll('.selectPlan').forEach(cb => {
        cb.checked = this.checked;
    });
});

// document.querySelectorAll('.openPurchaseModal').forEach(button => {
//     button.addEventListener('click', function () {
//         const row = this.closest('tr'); // 클릭한 버튼이 속한 tr
//
//         // 각 td 값을 가져오기
//         const ppCode = row.querySelector('td:nth-child(2)').innerText;
//         const pCode = row.querySelector('td:nth-child(3)').innerText;
//         const pName = row.querySelector('td:nth-child(4)').innerText;
//
//         document.getElementById('planCodeInput').innerText = ppCode;
//         document.getElementById('productSupplier').innerText = pCode;
//         document.getElementById('productNameInput').innerText = pName;
//
//         // 모달 띄우기
//         const modal = new bootstrap.Modal(document.getElementById('registerModal'));
//         modal.show();
//     });
// });

document.getElementById('openPurchaseModal').addEventListener('click', function () {
    const selectedRows = Array.from(document.querySelectorAll('.selectPlan:checked'))
        .map(cb => cb.closest('tr')); // 체크된 체크박스의 행 가져오기

    if (selectedRows.length === 0) {
        alert('하나 이상의 항목을 선택해주세요.');
        return;
    }

    const psIds = new Set();  // 중복을 방지하기 위해 Set 사용


    const tbody = document.getElementById('inspectionTableBody');
    tbody.innerHTML = ''; // 기존 내용 비우기

    selectedRows.forEach(checkbox => {
        const row = checkbox.closest('tr');
        const cells = row.querySelectorAll('td');
        const newRow = document.createElement('tr');

        const psId = cells[1].textContent.trim();
        psIds.add(psId);

        newRow.innerHTML = `
            <td>${cells[2].textContent.trim()}</td>
            <td>${cells[3].textContent.trim()}</td>
            <td>${cells[4].textContent.trim()}</td>
            <td>${cells[5].textContent.trim()}</td>
            <td>${cells[6].textContent.trim()}</td>
            <td>${cells[7].textContent.trim()}</td>
        `;

        tbody.appendChild(newRow);
    });

    const form = document.getElementById('purchaseOrderModalForm');
    form.querySelectorAll('input[name="psIds"]').forEach(input => input.remove());
    console.log(psIds);
    psIds.forEach(psId => {
        const hiddenInput = document.createElement('input');
        hiddenInput.type = 'hidden';
        hiddenInput.name = 'psIds';
        hiddenInput.value = psId;
        form.appendChild(hiddenInput);
    });

    const modal = new bootstrap.Modal(document.getElementById('purchaseOrderModal'));
    modal.show();
});


document.getElementById('openPurchaseDelModal').addEventListener('click', function () {
    const selectedRows = Array.from(document.querySelectorAll('.selectPlan:checked'))
        .map(cb => cb.closest('tr')); // 체크된 체크박스의 행 가져오기

    if (selectedRows.length === 0) {
        alert('하나 이상의 항목을 선택해주세요.');
        return;
    }

    const tbody = document.getElementById('deleteTableBody');
    tbody.innerHTML = ''; // 기존 내용 비우기

    const psIds = new Set();

    let hasPending = false;

    for (let checkbox of selectedRows) {
        const row = checkbox.closest('tr');
        const cells = row.querySelectorAll('td');
        const status = cells[8].textContent.trim();

        if (status === '검수 중') {
            hasPending = true;
            break;
        }
    }

    if (hasPending) {
        const proceed = confirm('선택한 항목 중 아직 검수중인 항목이 포함되어 있습니다. 그래도 진행하시겠습니까?');
        if (!proceed) {
            return;
        }
    }

    selectedRows.forEach(checkbox => {
        const row = checkbox.closest('tr');
        const cells = row.querySelectorAll('td');
        const newRow = document.createElement('tr');

        const psId = cells[1].textContent.trim();
        psIds.add(psId);

        newRow.innerHTML = `
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
    form.querySelectorAll('input[name="psIds"]').forEach(input => input.remove());
    psIds.forEach(psId => {
        const hiddenInput1 = document.createElement('input');
        hiddenInput1.type = 'hidden';
        hiddenInput1.name = 'psIdss';
        hiddenInput1.value = psId;
        form.appendChild(hiddenInput1);
    });


    const modal = new bootstrap.Modal(document.getElementById('purchaseOrderModalDel'));
    modal.show();
});