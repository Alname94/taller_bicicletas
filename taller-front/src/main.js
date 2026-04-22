import './css/style.css';
import { ENTITY_CONFIG } from './config/entityConfig';
import { notifications } from './utils/notifications';
import { authService } from './services/authService';
import { renderLogin } from './views/loginView';
import { renderDashboardLayout, renderHomeContent } from './views/dashboardView';
import { capitalizeWords, toUpperCase, sanitizeText } from './utils/stringUtils.js';

const app = document.querySelector('#app');
let currentProfileData = null;

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

async function renderMainContent(entityKey, providedData = null, page = 0) {
    const config = ENTITY_CONFIG[entityKey];
    const container = document.getElementById('main-content');

    const response = providedData ? providedData : await config.fetchData(page);

    const items = response.content ? response.content : response;

    container.innerHTML = config.renderTable(items);

    if (response.totalPages && response.totalPages > 1) {
        container.innerHTML += renderPaginationControls(response);
    }

    setupEntityListeners(entityKey, items);

    if (response.totalPages > 1) {
        setupPaginationListeners(entityKey, response);
    }
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

async function openGenericModal(item = null, entityKey, parentId = null) {
    const config = ENTITY_CONFIG[entityKey];
    const modalContainer = document.querySelector('.js-modal-container');

    modalContainer.innerHTML = await config.renderModal(item, parentId);

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
        await handleModalSave(e, item, entityKey, parentId, closeModal);
    };
}

async function handleModalSave(event, item, entityKey, parentId, closeModalCallback) {
    const config = ENTITY_CONFIG[entityKey];
    const formData = new FormData(event.target);
    const idParaGuardar = item?.id || item?.codigo || item?.numero;

    const cleanData = processFormData(Object.fromEntries(formData), config);

    try {
        const result = await config.onSave(idParaGuardar, cleanData);

        if (result) {
            notifications.showToast(`${config.entity} guardado con éxito`);
            if (closeModalCallback) closeModalCallback();

            if (!idParaGuardar && entityKey === 'presupuestos' && result.numero) {
                return navigateToProfile(result.numero, config);
            }

            if (parentId) {
                const parentConfig = ENTITY_CONFIG[config.parentEntity];
                const idParaRecargar = parentId.id || parentId;
                return navigateToProfile(idParaRecargar, parentConfig);
            }

            navigateTo(config.path || entityKey);
        }
    } catch (error) {
        notifications.showAlert('Atención', error.message || 'Error al procesar', 'warning');
    }
}

function processFormData(rawEntries, config) {
    const cleanData = {};
    const { capitalize = [], uppercase = [] } = config.transformations || {};

    Object.keys(rawEntries).forEach(key => {
        let value = rawEntries[key];

        if (typeof value === 'string') {
            value = sanitizeText(value);

            if (capitalize.includes(key)) {
                value = capitalizeWords(value);
            } else if (uppercase.includes(key)) {
                value = toUpperCase(value);
            }
        }
        cleanData[key] = value;
    });

    return cleanData;
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
            const item = data.find(i => i.id == id || i.codigo == id || i.numero == id);
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
                    notifications.showToast(`${config.entity} eliminado`);
                    navigateTo(entityKey);
                } else {
                    notifications.showAlert('Error', `No se pudo eliminar el ${config.entity}.`, 'error');
                }
            }
        }

        if (btn.classList.contains('js-btn-view')) {
            if (config.renderProfile) {
                await navigateToProfile(id, config);
            }
        }

        if (btn.classList.contains('js-btn-add-presupuesto')) {
            const bicicleta = data.find(i => i.id == id);
            await openGenericModal(null, 'presupuestos', bicicleta);
        }
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

async function navigateToProfile(id, config) {
    const mainContent = document.querySelector('#main-content');
    mainContent.innerHTML = `<div class="p-10 text-center text-gray-500 italic">Cargando...</div>`;

    try {
        const { html, data } = await config.renderProfile(id);
        currentProfileData = data;
        mainContent.innerHTML = html;
        setupProfileEvents(id, config);

    } catch (error) {
        notifications.showAlert('Error', `No se pudo cargar el perfil de ${config.entity}`, 'error');
    }
}

function setupProfileEvents(id, config) {
    const container = document.querySelector('#main-content');

    const btnBack = container.querySelector('.js-btn-back');
    if (btnBack) btnBack.onclick = () => navigateTo(config.path);

    const btnAddSub = container.querySelector('.js-btn-add-subentity');
    if (btnAddSub) btnAddSub.onclick = () => { openGenericModal(null, config.subEntity, currentProfileData); };

    const selectServicio = container.querySelector('#js-select-servicio');
    if (selectServicio) selectServicio.onchange = (e) => handleServicioChange(e, id, config);

    const selectEstado = container.querySelector('#js-select-estado');
    if (selectEstado) selectEstado.onchange = (e) => handleEstadoChange(e, id, config);

    const subContainer = container.querySelector('.js-subentity-container');
    if (subContainer) subContainer.onclick = (e) => handleSubentityActions(e, id, config);

    const btnAddDetalle = container.querySelector('.js-btn-add-detalle');
    if (btnAddDetalle) btnAddDetalle.onclick = () => handleOpenSubEntitySearch(id, config);

    const textAreaDescripcion = container.querySelector('#js-textarea-descripcion');
    if (textAreaDescripcion) textAreaDescripcion.onblur = (e) => handleDescripcionChange(e, id, config);
}

function setupDetalleModalEvents(modalElement, id, subConfig, parentConfig) {
    const closeBtns = modalElement.querySelectorAll('.js-btn-close-modal');
    closeBtns.forEach(btn => btn.onclick = () => {
        modalElement.remove();
        navigateToProfile(id, parentConfig);
    });

    const input = modalElement.querySelector('#search-repuesto-modal');
    if (input) {
        input.oninput = () => {
            const query = input.value.toLowerCase();
            const rows = modalElement.querySelectorAll('tbody tr');
            rows.forEach(row => {
                row.style.display = row.innerText.toLowerCase().includes(query) ? '' : 'none';
            });
        };
    }

    const tbody = modalElement.querySelector('tbody');

    if (tbody) {
        tbody.onclick = async (e) => {
            const btn = e.target.closest('.js-btn-add-item-to-budget');
            if (!btn) return;

            const codigo = btn.dataset.id;
            const cantidad = parseInt(modalElement.querySelector(`#qty-${codigo}`).value);

            const ok = await subConfig.onSave(id, { repuestoCodigo: codigo, cantidad: cantidad });

            if (ok) {
                notifications.showToast('Repuesto añadido');
                btn.innerHTML = '✓';
                btn.classList.replace('bg-blue-600', 'bg-green-600');
                btn.disabled = true;
            }
        };
    }
}

async function handleEstadoChange(e, id, config) {
    const nuevoEstado = e.target.value;
    const mensaje = `¿Confirma que desea cambiar el estado a "${nuevoEstado}"?`;

    if (await notifications.showConfirm('Cambio de Estado', mensaje)) {
        const ok = await config.onAction('cambiarEstado', id, { nuevoEstado });
        if (ok) {
            notifications.showToast(`Presupuesto ${nuevoEstado}`);
            navigateToProfile(id, config);
        }
    } else {
        navigateToProfile(id, config);
    }
}

async function handleServicioChange(e, id, config) {
    const nuevoServicioId = e.target.value;
    if (!nuevoServicioId) return;

    const confirmado = await notifications.showConfirm(
        '¿Cambiar servicio?',
        'El costo de mano de obra se actualizará al valor actual del servicio seleccionado.'
    );

    if (confirmado) {
        const ok = await config.onAction('cambiarServicio', id, { nuevoServicioId });

        if (ok) {
            notifications.showToast('Servicio actualizado');
            navigateToProfile(id, config);
        }
    } else {
        navigateToProfile(id, config);
    }
}

async function handleSubentityActions(e, id, config) {
    const btn = e.target.closest('button');
    if (!btn) return;

    if (config.entity === 'Presupuesto' && currentProfileData.estado !== 'PENDIENTE') {
        notifications.showToast('No se pueden modificar ítems de un presupuesto cerrado', 'info');
        return;
    }

    const subId = btn.dataset.id;
    const listField = config.subEntity;

    if (btn.classList.contains('js-btn-edit-sub')) {
        const subItem = currentProfileData[listField].find(item => item.id == subId);
        openGenericModal(subItem, config.subEntity, currentProfileData);
    }

    if (btn.classList.contains('js-btn-delete-sub')) {
        const confirmado = await notifications.showConfirm(
            '¿Estás seguro?',
            'Esta acción no se puede deshacer y devolverá el stock si corresponde.'
        );

        if (confirmado) {
            const subConfig = ENTITY_CONFIG[config.subEntity];
            const ok = await subConfig.onDelete(subId);

            if (ok) {
                notifications.showToast('Eliminado correctamente');
                navigateToProfile(id, config);
            }
        }
    }
}

async function handleOpenSubEntitySearch(id, parentConfig) {
    const subConfig = ENTITY_CONFIG[parentConfig.subEntity];
    if (!subConfig || parentConfig.entity !== 'Presupuesto') return;

    const data = await subConfig.onAction('abrirBuscadorRepuestos');
    const wrapper = document.createElement('div');

    wrapper.innerHTML = subConfig.renderSearchModal(data);

    const modalElement = wrapper.firstElementChild;
    document.body.appendChild(modalElement);

    setupDetalleModalEvents(modalElement, id, subConfig, parentConfig);
}

async function handleDescripcionChange(e, id, config) {
    const nuevaDescripcion = e.target.value;

    if (currentProfileData.estado !== 'PENDIENTE') return;

    try {
        const ok = await config.onSave(id, {
            ...currentProfileData,
            descripcion: nuevaDescripcion
        });

        if (ok) {
            notifications.showToast('Descripción guardada automáticamente');
        }
    } catch (error) {
        console.error('Error en auto-guardado:', error);
    }
}

function renderPaginationControls(data) {
    const { number, totalPages, first, last } = data;

    return `
        <div class="flex flex-col sm:flex-row justify-between items-center p-4 bg-white border-t border-gray-100 gap-4">
            <span class="text-sm text-gray-500 font-medium">
                Página <span class="text-blue-600">${number + 1}</span> de ${totalPages}
            </span>
            
            <div class="inline-flex rounded-md shadow-sm">
                <button 
                    ${first ? 'disabled' : ''} 
                    class="js-pag-prev px-4 py-2 text-sm font-medium border border-gray-200 rounded-l-lg ${first ? 'bg-gray-50 text-gray-300 cursor-not-allowed' : 'bg-white text-gray-700 hover:bg-gray-50 hover:text-blue-600'}">
                    &larr; Anterior
                </button>
                
                <button 
                    ${last ? 'disabled' : ''} 
                    class="js-pag-next px-4 py-2 text-sm font-medium border-t border-b border-r border-gray-200 rounded-r-lg ${last ? 'bg-gray-50 text-gray-300 cursor-not-allowed' : 'bg-white text-gray-700 hover:bg-gray-50 hover:text-blue-600'}">
                    Siguiente &rarr;
                </button>
            </div>
        </div>
    `;
}

function setupPaginationListeners(entityKey, response) {
    const container = document.getElementById('main-content');
    const btnPrev = container.querySelector('.js-pag-prev');
    const btnNext = container.querySelector('.js-pag-next');

    if (btnPrev && !response.first) {
        btnPrev.onclick = () => renderMainContent(entityKey, null, response.number - 1);
    }

    if (btnNext && !response.last) {
        btnNext.onclick = () => renderMainContent(entityKey, null, response.number + 1);
    }
}

// ----------------------------------------------
init();