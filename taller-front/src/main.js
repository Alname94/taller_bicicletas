import './css/style.css';
import { ENTITY_CONFIG } from './config/entityConfig';
import { notifications } from './utils/notifications';
import { authService } from './services/authService';
import { renderLogin } from './views/loginView';
import { renderDashboardLayout, renderHomeContent } from './views/dashboardView';

const app = document.querySelector('#app');

// ----------------------------------------------

// function router() {
//     if (authService.isLoggedIn()) {
//         app.innerHTML = renderDashboardLayout();
//     } else {
//         app.innerHTML = renderLogin();
//         setupLoginEvents();
//     }
// }

function init() {
    if (authService.isLoggedIn()) {
        app.innerHTML = renderDashboardLayout();
        const mainContent = document.querySelector('#main-content');
        mainContent.innerHTML = renderHomeContent();
        setupDashboardEvents();
    } else {
        app.innerHTML = renderLogin();
        handleLoginEvents();
    }
}

function handleLoginEvents() {
    const form = document.querySelector('#loginForm');
    if (!form) return;
    form.addEventListener('submit', async (e) => {
        e.preventDefault();
        const user = document.querySelector('#username').value;
        const pass = document.querySelector('#password').value;

        const success = await authService.login(user, pass);
        if (success) {
            init();
        } else {
            alert("Error: Usuario o clave incorrectos");
        }
    });
}

function setupDashboardEvents() {
    const logoutBtn = document.querySelector('#logoutBtn');
    if (logoutBtn) {
        logoutBtn.addEventListener('click', () => {
            authService.logout();
            init();
        });
    };

    const sidebar = document.querySelector('#sidebar-menu');
    if (sidebar) {
        sidebar.onclick = (e) => {
            const link = e.target.closest('[data-link]');
            if (!link) return;

            e.preventDefault();
            const target = link.dataset.link;

            if (target === 'home') {
                document.querySelector('#main-content').innerHTML = renderHomeContent();
            } else {
                navigateTo(target);
            }

            updateActiveLink(link);
        };
    }
}

async function navigateTo(entityKey) {
    const config = ENTITY_CONFIG[entityKey];
    if (!config) return;

    const mainContent = document.querySelector('#main-content');
    mainContent.innerHTML = `<div class="p-10 text-center text-gray-500 italic">Cargando ${config.title}...</div>`;

    try {
        const data = await config.fetchData();
        mainContent.innerHTML = config.renderTable(data);

        const btnNewEntity = document.querySelector('.js-btn-new-entity');
        if (btnNewEntity) {
            btnNewEntity.onclick = () => openGenericModal(null, entityKey);
        }

        setupTableListeners(data, entityKey);

    } catch (error) {
        notifications.showAlert('Error', `No se pudo cargar la sección de ${config.title}`, 'error');
    }
}

function openGenericModal(item = null, entityKey) {
    const config = ENTITY_CONFIG[entityKey];
    const modalContainer = document.querySelector('.js-modal-container');

    modalContainer.innerHTML = config.renderModal(item);

    const modal = modalContainer.querySelector('.js-entity-modal');
    const form = modalContainer.querySelector('.js-entity-form');
    const closeBtn = modalContainer.querySelector('.js-btn-close-modal');
    const cancelBtn = modalContainer.querySelector('.js-btn-cancel-modal');

    const closeModal = () => {
        modal.classList.add('hidden');
        modal.classList.remove('flex');
    };

    modal.classList.remove('hidden');
    modal.classList.add('flex');

    closeBtn.onclick = closeModal;
    cancelBtn.onclick = closeModal;

    form.onsubmit = async (e) => {
        e.preventDefault();

        const formData = Object.fromEntries(new FormData(e.target));

        try {
            const success = await config.onSave(item?.id, formData);
            if (success) {
                notifications.showToast(`${config.title} guardado con éxito`);
                closeModal();
                navigateTo(entityKey);
            }
        } catch (error) {
            notifications.showAlert(
                'Atención',
                error.message || 'No se pudo procesar la solicitud',
                'warning'
            );
        }
    };
}

function setupTableListeners(data, entityKey) {
    const config = ENTITY_CONFIG[entityKey];
    const tbody = document.querySelector('tbody');
    if (!tbody) return;

    tbody.onclick = async (e) => {
        const btn = e.target.closest('button');
        if (!btn) return;

        const id = btn.dataset.id;

        if (btn.classList.contains(config.btnEditClass)) {
            const item = data.find(i => i.id == id);
            openGenericModal(item, entityKey);
        }

        if (btn.classList.contains(config.btnDeleteClass)) {
            const confirmado = await notifications.showConfirm(
                '¿Estás seguro?',
                'Esta acción no se puede deshacer.'
            );

            if (confirmado) {
                const ok = await config.onDelete(id);
                if (ok) {
                    notifications.showToast(`${config.title} eliminado`);
                    navigateTo(entityKey);
                } else {
                    notifications.showAlert('Error', `No se pudo eliminar el ${config.title}.`, 'error');
                }
            }
        }

        // if (btn.classList.contains(config.btnViewClass)) {
        // navigateToClientProfile(id); 
        // }
    };
}

function updateActiveLink(activeLink) {
    const links = document.querySelectorAll('[data-link]');

    links.forEach(link => {
        link.classList.remove('bg-blue-50', 'text-gray-700');
        link.classList.add('text-gray-600', 'hover:bg-gray-100');
        
        const span = link.querySelector('span');
        if (span) span.classList.remove('font-medium');
    });

    activeLink.classList.remove('text-gray-600', 'hover:bg-gray-100');
    activeLink.classList.add('bg-blue-50', 'text-gray-700');
    
    const activeSpan = activeLink.querySelector('span');
    if (activeSpan) activeSpan.classList.add('font-medium');
}

// ----------------------------------------------
init();