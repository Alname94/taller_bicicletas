import './css/style.css';
import { authService } from './services/authService';
import { renderLogin } from './views/loginView';
import { renderDashboard } from './views/dashboardView';

const app = document.querySelector('#app');

function router() {
    if (authService.isLoggedIn()) {
        app.innerHTML = renderDashboard();
    } else {
        app.innerHTML = renderLogin();
        setupLoginEvents();
    }
}

function init() {
    if (authService.isLoggedIn()) {
        app.innerHTML = renderDashboard();
        
        document.querySelector('#logoutBtn').addEventListener('click', () => {
            authService.logout();
            init();
        });
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

init();