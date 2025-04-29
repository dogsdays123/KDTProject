document.getElementById('selectAll').addEventListener('change', function () {
    document.querySelectorAll('.selectPlan').forEach(cb => {
        cb.checked = this.checked;
    });
});

document.querySelectorAll('.icon-button').forEach(button => {
    button.addEventListener('click', function () {
        const row = this.closest('tr'); // 클릭한 버튼이 속한 tr

        // 각 td 값을 가져오기
        const ppCode = row.querySelector('td:nth-child(2)').innerText; //자재명
        const pCode = row.querySelector('td:nth-child(3)').innerText; //자재코드
        const pType = row.querySelector('td:nth-child(4)').innerText; //자재유형
        const pGoods = row.querySelector('td:nth-child(5)').innerText; //분류품목
        const cStock = row.querySelector('td:nth-child(6)').innerText; //현재재고
        const aStock = row.querySelector('td:nth-child(7)').innerText; //가용재고
        const hLocal = row.querySelector('td:nth-child(8)').innerText; //위치


        document.getElementById('materialName').value = ppCode;
        document.getElementById('materialCode').value = pCode;
        document.getElementById('materialType').value = pType;
        document.getElementById('goodsType').value = pGoods;
        document.getElementById('currentStock').value = cStock;
        document.getElementById('availableStock').value = aStock;
        document.getElementById('houseLocation').value = hLocal;

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

    selectedRows.forEach(checkbox => {
        const row = checkbox.closest('tr');
        const cells = row.querySelectorAll('td');
        const newRow = document.createElement('tr');

        newRow.innerHTML = `
            <td>${cells[1].textContent.trim()}</td>
            <td>${cells[2].textContent.trim()}</td>
            <td>${cells[3].textContent.trim()}</td>
            <td>${cells[4].textContent.trim()}</td>
            <td>${cells[5].textContent.trim()}</td>
            <td>${cells[6].textContent.trim()}</td>
            <td>${cells[7].textContent.trim()}</td>
            <td>${cells[8].textContent.trim()}</td>
        `;

        tbody.appendChild(newRow);
    });

    const modal = new bootstrap.Modal(document.getElementById('purchaseOrderModalDel'));
    modal.show();
});