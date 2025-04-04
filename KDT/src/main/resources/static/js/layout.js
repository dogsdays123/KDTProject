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