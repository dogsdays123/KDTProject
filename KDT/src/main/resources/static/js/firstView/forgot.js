$(document).ready(function() {
    $('#checkPassword').on('click', function(e) {
        e.preventDefault();

        const email = $('#checkEmail').val();

        // AJAX 요청 보내기
        $.ajax({
            url: '/firstView/forgot', // Controller의 URL로 수정 (예: '/user/checkId')
            type: 'POST',
            data: { checkEmail: email }, // 전송할 데이터
            success: function(response) {
                // 서버에서 응답이 성공적으로 왔을 때 처리
                alert(response.msg);
            },
            error: function(xhr, status, error) {
                alert("서버 오류" + error);
            }
        });
    });
});