function addPlan() {
    console.log('추가 버튼 눌림!');
    const startDate = document.getElementById('startDate').value;
    const endDate = document.getElementById('endDate').value;
    const productName = document.getElementById('productName').value;
    const productCode = document.getElementById('productCode').value;
    const productQuantity = document.getElementById('productQuantity').value;

    if (!startDate || !endDate || !productName || !productCode || !productQuantity) {
        alert('모든 항목을 입력해 주세요!');
        return;
    }

    const planItem = document.createElement('li');
    planItem.className = 'list-group-item';
    planItem.textContent = `📌 ${startDate} ~ ${endDate} | ${productName} (${productCode}) - ${productQuantity}개`;

    document.getElementById('planList').appendChild(planItem);

    // 입력값 초기화
    document.getElementById('productName').value = '';
    document.getElementById('productCode').value = '';
    document.getElementById('productQuantity').value = '';
}