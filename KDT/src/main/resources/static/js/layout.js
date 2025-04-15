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

// const ctx = document.getElementById('productionChart').getContext('2d');
// new Chart(ctx, {
//     type: 'bar',
//     data: {
//         labels: ['4/1', '4/2', '4/3', '4/4'],
//         datasets: [
//             {
//                 label: '계획',
//                 data: [100, 120, 130, 100],
//                 backgroundColor: '#8884d8'
//             },
//             {
//                 label: '실적',
//                 data: [90, 110, 140, 80],
//                 backgroundColor: '#82ca9d'
//             }
//         ]
//     },
//     options: {
//         responsive: true,
//         scales: {
//             y: { beginAtZero: true }
//         }
//     }
// });