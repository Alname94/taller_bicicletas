import './css/style.css';
import { authService } from './services/authService';
import { apiService } from './services/apiService';
import { renderLogin } from './views/loginView';
import { renderDashboardLayout, renderHomeContent } from './views/dashboardView';
import { renderServices } from './views/servicesView';

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
        const mainArea = document.querySelector('#main-content');
        mainArea.innerHTML = renderHomeContent();
        setupDashboardEvents();
    } else {
        app.innerHTML = renderLogin();
        handleLoginEvents();
    }
}

function handleLoginEvents() {
    const form = document.querySelector('#loginForm');
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

    const mainArea = document.querySelector('#main-content');

    document.querySelector('#link-home').addEventListener('click', (e) => {
        e.preventDefault();
        mainArea.innerHTML = renderHomeContent();
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
}

// ----------------------------------------------
init();