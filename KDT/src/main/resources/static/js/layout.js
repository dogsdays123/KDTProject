function toggleSidebar() {
    const sidebar = document.querySelector('.sidebar');
    sidebar.classList.toggle('closed');
}

// 사이드바 메뉴 클릭 관련 함수
function toggleSubmenu(id) {
    var allSubmenus = document.querySelectorAll('.submenu');
    var allLinks = document.querySelectorAll('.nav-link');

    allSubmenus.forEach(function(menu) {
        if (menu.id !== id) {
            menu.style.display = 'none';
        }
    });

    allLinks.forEach(function(link) {
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

window.addEventListener('click', function(e) {
    const dropdown = document.querySelector('.user-dropdown');
    const icon = document.querySelector('.bi-person-circle');
    if (!dropdown.contains(e.target) && !icon.contains(e.target)) {
        dropdown.style.display = 'none';
    }
});

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