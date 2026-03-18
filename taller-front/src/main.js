import './css/style.css';
import Swal from 'sweetalert2';
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

    const linkServicios = document.querySelector('#link-servicios');
    linkServicios.addEventListener('click', (e) => {
        e.preventDefault();
        navigateToServices();
    });
}

async function navigateToServices() {
    const mainContent = document.querySelector('#main-content');
    mainContent.innerHTML = renderServices([]);
    const servicios = await apiService.getServicios();
    mainContent.innerHTML = renderServices(servicios);

    const btnNewService = document.querySelector('#btnNewService');
    btnNewService.addEventListener('click', () => openServiceModal());
    setupTableEventListeners(servicios);
}

function openServiceModal(servicio = null) {
    const container = document.querySelector('#modal-container');
    container.innerHTML = renderServiceModal(servicio);
    
    const modal = document.querySelector('#serviceModal');
    modal.classList.remove('hidden');
    setupModalListeners();
}

function setupModalListeners() {
    const modal = document.querySelector('#serviceModal');
    const form = document.querySelector('#serviceForm');
    
    document.querySelector('#btnCloseModal').onclick = () => modal.classList.add('hidden');
    document.querySelector('#btnCloseX').onclick = () => modal.classList.add('hidden');

    form.onsubmit = async (e) => {
        e.preventDefault();
        const id = document.querySelector('#serviceId').value;
        const data = {
            nombre: document.querySelector('#serviceName').value,
            descripcion: document.querySelector('#serviceDesc').value,
            valor: parseFloat(document.querySelector('#serviceValue').value),
            activo: document.querySelector('#serviceActive').value === 'true'
        };

        const success = id 
            ? await apiService.updateServicio(id, data) // Si hay ID, es PUT
            : await apiService.saveServicio(data);      // Si no, es POST

        if (success) {
            modal.classList.add('hidden');
            navigateToServices(); 
        }
    };
}

function setupTableEventListeners(servicios) {
    const tableBody = document.querySelector('tbody');
    if (!tableBody) return;

    tableBody.addEventListener('click', async (e) => {
        const id = e.target.dataset.id;
        if (!id) return;

        if (e.target.classList.contains('btn-edit-service')) {
            const servicioAEditar = servicios.find(s => s.id == id);
            openServiceModal(servicioAEditar);
        }

        if (e.target.classList.contains('btn-delete-service')) {
            const confirmar = confirm(`¿Estás seguro de eliminar el servicio #${id}?`);
            if (confirmar) {
                const ok = await apiService.deleteServicio(id);
                if (ok) {
                    navigateToServices();
                } else {
                    alert("No se pudo eliminar el servicio.");
                }
            }
        }
    });
}

// ----------------------------------------------
init();