import './css/style.css';
import { ENTITY_CONFIG } from './config/entityConfig';
import { notifications } from './utils/notifications';
import { authService } from './services/authService';
import { renderLogin } from './views/loginView';
import { renderDashboardLayout, renderHomeContent } from './views/dashboardView';

const app = document.querySelector('#app');

// ----------------------------------------------

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

        try {
            const success = await authService.login(user, pass);
            if (success) {
                init();
            } else {
                notifications.showAlert('Error', 'Usuario o contraseña incorrectos', 'error');
            }
        } catch (err) {
            notifications.showAlert('Error de conexión', 'No se pudo conectar con el servidor', 'error');
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
            target === 'home' ? document.querySelector('#main-content').innerHTML = renderHomeContent() : navigateTo(target);

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
        await renderMainContent(entityKey);
    } catch (error) {
        notifications.showAlert('Error', `No se pudo cargar la sección de ${config.title}`, 'error');
    }
}

async function renderMainContent(entityKey, providedData = null) {
    const config = ENTITY_CONFIG[entityKey];
    const container = document.getElementById('main-content');

    const data = providedData ? providedData : await config.fetchData();

    container.innerHTML = config.renderTable(data);

    setupEntityListeners(entityKey, data);
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

        if (btn.classList.contains('js-btn-edit')) {
            const item = data.find(i => i.id == id);
            openGenericModal(item, entityKey);
        }

        if (btn.classList.contains('js-btn-delete')) {
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

function setupSearchLogic(entityKey) {
    const btn = document.querySelector('.js-btn-search');
    const input = document.querySelector('.js-search-input');

    if (!btn || !input) return;

    btn.onclick = () => handleEntitySearch(entityKey);

    input.onkeydown = (e) => {
        if (e.key === 'Enter') {
            handleEntitySearch(entityKey);
        }
    };
}

async function handleEntitySearch(entityKey) {
    const input = document.querySelector('.js-search-input');
    if (!input) return;

    const query = input.value.trim();
    const config = ENTITY_CONFIG[entityKey];

    if (query === "") {
        await renderMainContent(entityKey);
        return;
    }

    try {
        let data = await config.onSearch(query);

        // Si la respuesta es un solo objeto, lo convertimos en un array para mantener la consistencia
        if (data && !Array.isArray(data)) {
            data = [data];
        }

        if (!data || data.length === 0) {
            notifications.showAlert('Sin resultados', `No se encontraron ${config.title} con el término: "${query}"`, 'info');
            await renderMainContent(entityKey); // Carga la lista completa por defecto
        } else {
            await renderMainContent(entityKey, data);

            // Devolvemos el foco al input para que el usuario pueda seguir operando
            const newInput = document.querySelector('.js-search-input');
            if (newInput) {
                newInput.focus();
                newInput.value = query;
            }
        }

    } catch (error) {
        notifications.showAlert('Sin resultados', `No se encontró ningún resultado para "${query}"`, 'warning');
        await renderMainContent(entityKey);
    }
}

function setupEntityListeners(entityKey, data) {
    const btnNewEntity = document.querySelector('.js-btn-new-entity');
    if (btnNewEntity) {
        btnNewEntity.onclick = () => openGenericModal(null, entityKey);
    }
    setupTableListeners(data, entityKey);
    setupSearchLogic(entityKey);
}

// ----------------------------------------------
init();