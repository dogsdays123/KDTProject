let checkAll = { idCheck: false, emailCheck: false };
var signupButton = document.getElementById('signupButton');

$(document).ready(function() {
    $('#uId').on('input', function(e) {

        var textarea = document.getElementById('username-feedback'); //제한 텍스트
        var uId = $(this).val(); // 입력된 아이디 값을 가져옵니다.

        const eventCheck = new Event('input', {bubbles: true});

        // 아이디 값이 비어 있지 않은지 확인
        if (uId.trim() === "") {
            textarea.classList.remove('text-success');
            textarea.classList.add('text-danger');
            textarea.value = '아이디를 입력해 주세요.';
            checkAll.idCheck = false;
            toggleSignupButton();
            signupButton.dispatchEvent(eventCheck);
            return;
        } else{
            // 정규식 체크: 3자 이상, 16자 이하, 영어/숫자만 가능
            var regex = /^[a-zA-Z0-9]{3,16}$/;
            if (!regex.test(uId)) {
                textarea.classList.remove('text-success');
                textarea.classList.add('text-danger');
                textarea.value = '아이디는 3자 이상, 16자 이하의 영어 또는 숫자만 가능';
                checkAll.idCheck = false;
                toggleSignupButton();
                signupButton.dispatchEvent(eventCheck);
                return;
            }
        }

        // AJAX 요청 보내기
        $.ajax({
            url: '/firstView/checkId', // Controller의 URL로 수정 (예: '/user/checkId')
            type: 'POST',
            data: { uId: uId }, // 전송할 데이터
            success: function(response) {
                // 서버에서 응답이 성공적으로 왔을 때 처리
                if (response.isAvailable) {
                    textarea.value = "사용 가능한 아이디입니다.";
                    textarea.classList.add('text-success'); // 새 클래스를 추가
                    textarea.classList.remove('text-danger'); // 기존 클래스를 제거
                    checkAll.idCheck = true;
                } else {
                    textarea.value = "이미 존재하는 아이디입니다.";
                    textarea.classList.remove('text-success'); // 새 클래스를 추가
                    textarea.classList.add('text-danger'); // 기존 클래스를 제거
                }
                var event = new Event('input', {bubbles: true});
                textarea.dispatchEvent(event);

                toggleSignupButton();
                signupButton.dispatchEvent(eventCheck);
            },
            error: function(xhr, status, error) {
                alert("서버 오류" + error);
            }
        });
    });
});

$(document).ready(function() {
    $('#uEmail').on('input', function(e) {

        var textarea = document.getElementById('email-feedback'); //제한 텍스트
        var uEmail = $(this).val(); // 입력된 아이디 값을 가져옵니다.

        const eventCheck = new Event('input', {bubbles: true});

        // 이메일 값이 비어 있지 않은지 확인
        if (uEmail.trim() === "") {
            textarea.classList.remove('text-success');
            textarea.classList.add('text-danger');
            textarea.value = '이메일을 입력해 주세요.';
            checkAll.emailCheck = false;
            toggleSignupButton();
            signupButton.dispatchEvent(eventCheck);
            return;
        } else{
            // 정규식 체크: @와 .을 포함한 이메일 형식
            var regex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
            if (!regex.test(uEmail)) {
                textarea.classList.remove('text-success');
                textarea.classList.add('text-danger');
                textarea.value = '유효하지 않은 이메일 형식입니다.';
                checkAll.emailCheck = false;
                toggleSignupButton();
                signupButton.dispatchEvent(eventCheck);
                return;
            }
        }

        // AJAX 요청 보내기
        $.ajax({
            url: '/firstView/checkEmail', // Controller의 URL로 수정 (예: '/user/checkId')
            type: 'POST',
            data: { uEmail: uEmail }, // 전송할 데이터
            success: function(response) {
                // 서버에서 응답이 성공적으로 왔을 때 처리
                if (response.isAvailable) {
                    textarea.value = "사용 가능한 이메일입니다.";
                    textarea.classList.add('text-success'); // 새 클래스를 추가
                    textarea.classList.remove('text-danger'); // 기존 클래스를 제거
                    checkAll.emailCheck = true;
                } else {
                    textarea.value = "이미 존재하는 이메일입니다.";
                    textarea.classList.remove('text-success'); // 새 클래스를 추가
                    textarea.classList.add('text-danger'); // 기존 클래스를 제거
                    checkAll.emailCheck = false;
                }
                var event = new Event('input', {bubbles: true});
                textarea.dispatchEvent(event);

                toggleSignupButton();
                signupButton.dispatchEvent(eventCheck);

            },
            error: function(xhr, status, error) {
                alert("서버 오류" + error);
            }
        });
    });
});

// 버튼의 상태를 업데이트하는 함수
function toggleSignupButton() {
    // idCheck와 emailCheck가 모두 true일 때만 버튼을 활성화
    if (checkAll.idCheck && checkAll.emailCheck) {
        signupButton.disabled = false;  // 아이디와 이메일이 모두 유효하면 버튼 활성화
    } else {
        signupButton.disabled = true;   // 하나라도 비어 있으면 버튼 비활성화
    }
}

//비밀번호 확인
$(document).ready(function() {
    $('#uPassword, #aPswCheck').on('input', function() {
        var uPassword = $('#uPassword').val();  // 비밀번호 입력 값
        var passwordCheck = $('#aPswCheck').val();  // 비밀번호 확인 입력 값
        var feedback = $('#password-feedback');  // 비밀번호 확인 메시지

        // 비밀번호가 일치하면
        if(uPassword.length < 6 || uPassword.length > 16){
            feedback.removeClass('text-success').addClass('text-danger');
            feedback.text('비밀번호의 길이가 올바르지 않습니다.');
            return;
        } else{
            if (uPassword === passwordCheck) {
                feedback.removeClass('text-danger').addClass('text-success');
                feedback.text('비밀번호가 일치합니다.');
            } else {
                feedback.removeClass('text-success').addClass('text-danger');
                feedback.text('비밀번호가 일치하지 않습니다.');
            }
        }
    });
});

//유저타입 라디오함수
$(document).ready(function() {
    $('#userType').on('click', function (e){
        var userType = $('#userType').val();
        var uAddress = $('#uAddress');
        var uBirthDay = $('#uBirthDay');

        if(userType === 'our'){
            uAddress.show();
            uBirthDay.hide();
        } else {
            uBirthDay.show();
            uAddress.hide();
        }
    });
});

// $(document).ready(function() {
//     $('#uEmail').on('input', function(e) {
//
//         var textarea = document.getElementById('email-feedback'); //제한 텍스트
//         var uEmail = $(this).val(); // 입력된 아이디 값을 가져옵니다.
//
//         const eventCheck = new Event('input', {bubbles: true});
//
//         // 이메일 값이 비어 있지 않은지 확인
//         if (uEmail.trim() === "") {
//             textarea.classList.remove('text-success');
//             textarea.classList.add('text-danger');
//             textarea.value = '이메일을 입력해 주세요.';
//             checkAll.emailCheck = false;
//             toggleSignupButton();
//             signupButton.dispatchEvent(eventCheck);
//             return;
//         } else{
//             // 정규식 체크: @와 .을 포함한 이메일 형식
//             var regex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
//             if (!regex.test(uEmail)) {
//                 textarea.classList.remove('text-success');
//                 textarea.classList.add('text-danger');
//                 textarea.value = '유효하지 않은 이메일 형식입니다.';
//                 checkAll.emailCheck = false;
//                 toggleSignupButton();
//                 signupButton.dispatchEvent(eventCheck);
//                 return;
//             }
//         }
//
//         // AJAX 요청 보내기
//         $.ajax({
//             url: '/firstView/checkEmail', // Controller의 URL로 수정 (예: '/user/checkId')
//             type: 'POST',
//             data: { uEmail: uEmail }, // 전송할 데이터
//             success: function(response) {
//                 // 서버에서 응답이 성공적으로 왔을 때 처리
//                 if (response.isAvailable) {
//                     textarea.value = "사용 가능한 이메일입니다.";
//                     textarea.classList.add('text-success'); // 새 클래스를 추가
//                     textarea.classList.remove('text-danger'); // 기존 클래스를 제거
//                     checkAll.emailCheck = true;
//                 } else {
//                     textarea.value = "이미 존재하는 이메일입니다.";
//                     textarea.classList.remove('text-success'); // 새 클래스를 추가
//                     textarea.classList.add('text-danger'); // 기존 클래스를 제거
//                     checkAll.emailCheck = false;
//                 }
//                 var event = new Event('input', {bubbles: true});
//                 textarea.dispatchEvent(event);
//
//                 toggleSignupButton();
//                 signupButton.dispatchEvent(eventCheck);
//
//             },
//             error: function(xhr, status, error) {
//                 alert("서버 오류" + error);
//             }
//         });
//     });
// });