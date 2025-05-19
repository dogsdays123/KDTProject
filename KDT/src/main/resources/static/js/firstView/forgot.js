$(document).ready(function () {
    $('#checkPassword').on('click', function (e) {
        e.preventDefault();
        const email = $('#checkEmail').val();

        const loadingModal = new bootstrap.Modal(document.getElementById('loadingModal'), {
            backdrop: 'static',
            keyboard: false
        });

        $.ajax({
            url: '/firstView/forgot',
            type: 'POST',
            data: { checkEmail: email },
            beforeSend: function () {
                loadingModal.show();
            },
            success: function (response) {
                alert(response.msg);
            },
            complete: function () {
                loadingModal.hide();
            },
            error: function (xhr, status, error) {
                alert("서버 오류: " + error);
            }
        });
    });
});