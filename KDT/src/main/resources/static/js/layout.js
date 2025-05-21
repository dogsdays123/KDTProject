function toggleSidebar() {
    const sidebar = document.querySelector('.sidebar');
    sidebar.classList.toggle('closed');
}

// 사이드바 메뉴 클릭 관련 함수
function toggleSubmenu(id) {
    var allSubmenus = document.querySelectorAll('.submenu');
    var allLinks = document.querySelectorAll('.nav-link');

    allSubmenus.forEach(function (menu) {
        if (menu.id !== id) {
            menu.style.display = 'none';
        }
    });

    allLinks.forEach(function (link) {
        if (link.innerHTML.includes('▴')) {
            link.innerHTML = link.innerHTML.replace('▴', '▾');
        }
    });

    var submenu = document.getElementById(id);
    var link = submenu.previousElementSibling;
    var isOpen = submenu.style.display === 'block';

    submenu.style.display = isOpen ? 'none' : 'block';
    link.innerHTML = link.innerHTML.replace(isOpen ? '▴' : '▾', isOpen ? '▾' : '▴');
}

function toggleUserDropdown() {
    const dropdown = document.querySelector('.user-dropdown');
    dropdown.style.display = dropdown.style.display === 'block' ? 'none' : 'block';
}

window.addEventListener('click', function (e) {
    const dropdown = document.querySelector('.user-dropdown');
    const icon = document.querySelector('.bi-person-circle');
    if (!dropdown.contains(e.target) && !icon.contains(e.target)) {
        dropdown.style.display = 'none';
    }
});


document.addEventListener('DOMContentLoaded', function () {
    const bellIcon = document.getElementById('bellIcon');
    const dot = document.querySelector('.noticeDot');
    const buttons = document.querySelectorAll('.noticeClick');

    const totalNotices = buttons.length;
    let checkedCount = 0;

    // 알림 클릭 이벤트
    buttons.forEach(button => {
        button.addEventListener('click', function (event) {
            const nId = event.target.value;
            const thisButton = event.target;

            fetch('/notice/read', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({ nId })
            })
                .then(response => response.json())
                .then(data => {
                    if (data.status === 'success') {
                        console.log(`알림 ${nId} 삭제됨`);

                        // 해당 버튼을 화면에서 제거
                        thisButton.remove();

                        // 확인된 알림 수 증가
                        checkedCount++;

                        // 모든 알림을 확인했으면 빨간 점 숨기기
                        if (checkedCount === totalNotices && dot) {
                            dot.style.display = 'none';
                        }
                    }
                })
                .catch(error => console.error('알림 삭제 중 오류 발생:', error));
        });
    });

    // 벨 아이콘 클릭 시 드롭다운 열기
    if (bellIcon) {
        bellIcon.addEventListener('click', function (event) {
            event.stopPropagation();
            const dropdown = document.getElementById('alertDropdown');
            dropdown.style.display = (dropdown.style.display === 'none' || dropdown.style.display === '') ? 'block' : 'none';
        });
    }
});


// 외부 클릭 시 드롭다운 닫기
document.addEventListener('click', function (event) {
    const bellIcon = document.getElementById('bellIcon');
    const dropdown = document.getElementById('alertDropdown');
    // bellIcon 또는 dropdown 내부를 클릭한 것이 아니면 닫기
    if (!bellIcon.contains(event.target) && !dropdown.contains(event.target)) {
        dropdown.style.display = 'none';
    }
});

$(document).ready(function () {
    $('#clearNoticeBtn').on('click', function () {
        $.ajax({
            url: `/notice/clear`,
            method: 'GET',
            success: function (mComponentTypes) {
                if (!Array.isArray(mComponentTypes) || mComponentTypes.length === 0) {
                    defaultValueInner("mComponentType");
                    return;  // 이 시점에서 종료해주는 게 좋음
                }
                // 부품명 선택 요소 초기화
                const mComponentTypeSelect = $('#mComponentType');
                mComponentTypeSelect.empty();  // 기존 옵션 제거
                mComponentTypeSelect.append('<option value="" selected>선택</option>');
                mComponentTypes.forEach(type => {
                    mComponentTypeSelect.append(`<option value="${type}">${type}</option>`);
                });
                mComponentTypeSelect.trigger('change');  // select2가 최신 값을 반영하도록 트리거
            },
            error: function (error) {
                console.error('목록을 가져오는 중 오류 발생:', error);
            }
        });
    });
})

//통합 환경-------------------------------------------------------------------------

function confirmSubmit(message) {
    // 알림 창을 띄우기
    var confirmResult = confirm(message + " 하시겠습니까?");

    // 사용자가 "확인"을 클릭하면 폼 제출, 아니면 제출하지 않음
    if (confirmResult) {
        return true; // 폼 제출
    } else {
        return false; // 폼 제출을 취소
    }
}

//이 부분에서 모든 searchSelect 클래스는 검색찾기가 가능
$(document).ready(function () {
    $('.searchSelect').select2({
        placeholder: "선택 또는 직접 입력",
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
//통합 환경 END --------------------------------------------------------------------