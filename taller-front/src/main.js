import './css/style.css';
import { ENTITY_CONFIG } from './config/entityConfig';
import { notifications } from  './utils/notifications';
import { authService } from './services/authService';
import { apiService } from './services/apiService';
import { renderLogin } from './views/loginView';
import { renderDashboardLayout, renderHomeContent } from './views/dashboardView';
import { renderServices, renderServiceModal } from './views/servicesView';

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

    const mainContent = document.querySelector('#main-content');

    document.querySelector('#link-home').addEventListener('click', (e) => {
        e.preventDefault();
        mainContent.innerHTML = renderHomeContent();
    });

    document.querySelector('#link-servicios').onclick = (e) => {
        e.preventDefault();
        navigateTo('servicios');
    };
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

// async function navigateToServices() {
//     const mainContent = document.querySelector('#main-content');
//     mainContent.innerHTML = renderServices([]);
//     const servicios = await apiService.getServicios();
//     mainContent.innerHTML = renderServices(servicios);

//     const btnNewService = document.querySelector('#btnNewService');
//     btnNewService.addEventListener('click', () => openServiceModal());
//     setupTableEventListeners(servicios);
// }

// function openServiceModal(servicio = null) {
//     const container = document.querySelector('#modal-container');
//     container.innerHTML = renderServiceModal(servicio);

//     const modal = document.querySelector('#serviceModal');
//     modal.classList.remove('hidden');
//     setupModalListeners();
// }

// function setupModalListeners() {
//     const modal = document.querySelector('#serviceModal');
//     const form = document.querySelector('#serviceForm');

//     document.querySelector('#btnCloseModal').onclick = () => modal.classList.add('hidden');
//     document.querySelector('#btnCloseX').onclick = () => modal.classList.add('hidden');

//     form.onsubmit = async (e) => {
//         e.preventDefault();
//         const id = document.querySelector('#serviceId').value;
//         const data = {
//             nombre: document.querySelector('#serviceName').value,
//             descripcion: document.querySelector('#serviceDesc').value,
//             valor: parseFloat(document.querySelector('#serviceValue').value),
//             activo: document.querySelector('#serviceActive').value === 'true'
//         };

//         const success = id
//             ? await apiService.updateServicio(id, data) // Si hay ID, es PUT
//             : await apiService.saveServicio(data);      // Si no, es POST

//         if (success) {
//             modal.classList.add('hidden');
//             notifications.showToast(id ? 'Servicio actualizado' : 'Servicio creado');
//             navigateToServices();
//         }
//     };
// }

function openGenericModal(item = null, entityKey) {
    const config = ENTITY_CONFIG[entityKey];
    const modalContainer = document.querySelector('.js-modal-container');
    
    modalContainer.innerHTML = config.renderModal(item);
    
    const modal = modalContainer.querySelector('.js-entity-modal');
    const form = modalContainer.querySelector('.js-entity-form');
    const closeBtn = modalContainer.querySelector('.js-btn-close-modal');
    const cancelBtn = modalContainer.querySelector('.js-btn-cancel-modal');

    modal.classList.remove('hidden');
    closeBtn.onclick = () => modal.classList.add('hidden');
    cancelBtn.onclick = () => modal.classList.add('hidden');

    form.onsubmit = async (e) => {
        e.preventDefault();
        const formData = Object.fromEntries(new FormData(form));
        
        try {
            const success = await config.onSave(item?.id, formData);
            if (success) {
                notifications.showToast(`${config.title} guardado con éxito`);
                modal.classList.add('hidden');
                navigateTo(entityKey);
            }
        } catch (error) {
            notifications.showAlert('Error', 'No se pudo guardar los cambios', 'error');
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
    };
}

// function setupTableEventListeners(servicios) {
//     const tableBody = document.querySelector('tbody');
//     if (!tableBody) return;

//     tableBody.addEventListener('click', async (e) => {
//         const id = e.target.dataset.id;
//         if (!id) return;

//         if (e.target.classList.contains('btn-edit-service')) {
//             const servicioAEditar = servicios.find(s => s.id == id);
//             openServiceModal(servicioAEditar);
//         }

//         if (e.target.classList.contains('btn-delete-service')) {
//             const confirmar = await notifications.showConfirm(
//                 '¿Eliminar servicio?',
//                 `Estás por borrar el servicio #${id}. Esta acción es permanente.`,
//                 'Eliminar'
//             );
//             if (confirmar) {
//                 const ok = await apiService.deleteServicio(id);
//                 if (ok) {
//                     notifications.showToast('Servicio eliminado correctamente');
//                     navigateToServices();
//                 } else {
//                     notifications.showAlert('Error', 'No se pudo eliminar el servicio.', 'error');
//                 }
//             }
//         }
//     });
// }

// ----------------------------------------------
init();