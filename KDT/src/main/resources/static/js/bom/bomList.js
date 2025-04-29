document.getElementById('selectAll').addEventListener('change', function () {
    document.querySelectorAll('.selectPlan').forEach(cb => {
        cb.checked = this.checked;
    });
});

document.querySelectorAll('.icon-button').forEach(button => {
    button.addEventListener('click', function () {
        const row = this.closest('tr'); // 클릭한 버튼이 속한 tr

        // 각 td 값을 가져오기
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
        document.getElementById('cType').value = cType;
        document.getElementById('mCode').value = mCode;
        document.getElementById('mName').value = mName;
        document.getElementById('reNum').value = reNum;

        // 모달 띄우기
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
    tbody.innerHTML = ''; // 기존 내용 비우기

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