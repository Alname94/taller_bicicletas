import { renderBicicletasList } from './bicyclesView.js';

export function renderClientesTable(clientes = []) {
    const rows = clientes.map(({ id, nombre, apellido, dni }) => `
        <tr class="border-b hover:bg-gray-50 transition-colors">
            <td class="px-6 py-4 text-sm font-medium text-gray-900">#${id}</td>
            <td class="px-6 py-4 text-sm text-gray-700">${nombre} ${apellido}</td>
            <td class="px-6 py-4 text-sm text-gray-700">${dni}</td>
            <td class="px-6 py-4 text-sm text-center space-x-2">
                <button data-id="${id}" class="js-btn-view text-emerald-600 hover:text-blue-900 font-medium">Ver Ficha</button>
                <button data-id="${id}" class="js-btn-edit text-blue-600 hover:text-blue-900 font-medium">Editar</button>
                <button data-id="${id}" class="js-btn-delete text-red-600 hover:text-red-900 font-medium">Eliminar</button>
            </td>
        </tr>
    `).join('');

    return `
    <div class="space-y-6">
        <div class="flex justify-between items-center">
            <h3 class="text-2xl font-bold text-gray-800">Gestión de Clientes</h3>
            <div class="flex grow max-w-md">
                <div class="relative w-full md:w-96">
                    <input type="text"
                        class="js-search-input bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg rounded-r-none focus:ring-blue-500 focus:border-blue-500 block w-full pl-10 p-2.5" 
                        placeholder="Buscar por ID, nombre o apellido..."
                        autocomplete="off">
                </div>
                <button class="js-btn-search bg-gray-800 hover:bg-gray-900 text-white px-5 py-2.5 rounded-r-lg rounded-l-none text-sm font-medium transition-colors border border-gray-800">
                    Buscar
                </button>
            </div>
            <button class="js-btn-new-entity bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded-lg shadow transition-colors flex items-center">
                <span class="mr-2">+</span> Nuevo Cliente
            </button>
        </div>

        <div class="bg-white rounded-xl shadow-sm border border-gray-100 overflow-x-auto">
            <table class="min-w-full divide-y divide-gray-200">
                <thead class="bg-gray-50">
                    <tr>
                        <th class="px-6 py-3 text-left text-xs font-semibold text-gray-500 uppercase">ID</th>
                        <th class="px-6 py-3 text-left text-xs font-semibold text-gray-500 uppercase">Nombre y Apellido</th>
                        <th class="px-6 py-3 text-left text-xs font-semibold text-gray-500 uppercase">DNI</th>
                        <th class="px-6 py-3 text-center text-xs font-semibold text-gray-500 uppercase">Acciones</th>
                    </tr>
                </thead>
                <tbody class="divide-y divide-gray-200 bg-white">
                    ${rows.length > 0 ? rows : '<tr><td colspan="6" class="text-center py-10 text-gray-400">No hay clientes cargados.</td></tr>'}
                </tbody>
            </table>
        </div>
    </div>
    `;
}

export function renderClienteModal(cliente = null) {
    const isEdit = !!cliente; // true si estamos editando

    const { id = '', nombre = '', apellido = '', dni = '', telefono = '', email = '' } = cliente || {};

    return `
    <div class="js-entity-modal fixed inset-0 z-50 items-center justify-center hidden">
        <div class="absolute inset-0 bg-gray-900/40 backdrop-blur-sm"></div>
        
        <div class="bg-white rounded-xl shadow-2xl w-full max-w-md overflow-hidden z-10 transform transition-all">
            <div class="p-6 border-b bg-gray-50 flex justify-between items-center">
                <h3 class="text-xl font-bold text-gray-800">
                    ${isEdit ? 'Editar Cliente' : 'Crear Nuevo Cliente'}
                </h3>
                <button class="js-btn-close-modal text-gray-400 hover:text-gray-600">&times;</button>
            </div>
            <form class="js-entity-form p-6 space-y-4">
                <input type="hidden" name="id" value="${id}">
                <div>
                    <label class="block text-sm font-medium text-gray-700">Nombre</label>
                    <input type="text" name="nombre" required value="${nombre}" 
                        class="capitalize mt-1 block w-full border border-gray-300 rounded-lg p-2.5 focus:ring-2 focus:ring-blue-500 focus:outline-none">
                </div>
                <div>
                    <label class="block text-sm font-medium text-gray-700">Apellido</label>
                    <input type="text" name="apellido" required value="${apellido}" 
                        class="capitalize mt-1 block w-full border border-gray-300 rounded-lg p-2.5 focus:ring-2 focus:ring-blue-500 focus:outline-none">
                </div>
                <div>
                    <label class="block text-sm font-medium text-gray-700">DNI</label>
                    <input type="text" maxlength="8" name="dni" required value="${dni}" 
                        class="mt-1 block w-full border border-gray-300 rounded-lg p-2.5 focus:ring-2 focus:ring-blue-500 focus:outline-none">
                </div>
                <div>
                    <label class="block text-sm font-medium text-gray-700">Teléfono</label>
                    <input type="tel" name="telefono" pattern="^\\+?[0-9]{8,15}$" minlength="8" maxlength="15" inputmode="numeric" required value="${telefono}" 
                        class="mt-1 block w-full border border-gray-300 rounded-lg p-2.5 focus:ring-2 focus:ring-blue-500 focus:outline-none">
                </div>
                <div>
                    <label class="block text-sm font-medium text-gray-700">Email</label>
                    <input type="email" name="email" maxlength="100" required value="${email}" 
                        class="mt-1 block w-full border border-gray-300 rounded-lg p-2.5 focus:ring-2 focus:ring-blue-500 focus:outline-none">
                </div>
                <div class="flex justify-end space-x-3 pt-4 border-t">
                    <button type="button" class="js-btn-cancel-modal px-4 py-2 text-gray-600 hover:bg-gray-100 rounded-lg transition-colors">Cancelar</button>
                    <button type="submit" id="btnSaveClient" class="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 shadow-lg shadow-blue-200 transition-all font-medium">
                        ${isEdit ? 'Guardar Cambios' : 'Crear Cliente'}
                    </button>
                </div>
            </form>
        </div>
    </div>
    `;
}

export function renderClientePerfil(cliente) {
    const { id, nombre, apellido, dni, email, telefono, bicicletas = [] } = cliente;

    return `
    <div class="p-6 space-y-6 animate-fade-in">
        <div class="flex justify-between items-start">
            <div>
                <h2 class="text-3xl font-bold text-gray-800">${nombre} ${apellido}</h2>
                <p class="text-gray-500">Cliente #${id} | DNI: ${dni}</p>
            </div>
            <button class="js-btn-back px-4 py-2 bg-gray-100 text-gray-600 rounded-lg hover:bg-gray-200 transition-colors">
                Volver a la lista
            </button>
        </div>

        <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
            <div class="bg-white p-6 rounded-xl shadow-sm border border-gray-100">
                <h3 class="text-lg font-semibold mb-4 text-blue-600">Información de Contacto</h3>
                <div class="space-y-3">
                    <p class="flex items-center text-gray-600">
                        <span class="font-medium w-24">Email:</span> ${email}
                    </p>
                    <p class="flex items-center text-gray-600">
                        <span class="font-medium w-24">Teléfono:</span> ${telefono}
                    </p>
                </div>
            </div>

            <div class="bg-white p-6 rounded-xl shadow-sm border border-gray-100">
                <h3 class="text-lg font-semibold mb-4 text-blue-600">Resumen de Actividad</h3>
                <p class="text-3xl font-bold text-gray-800">${bicicletas.length} <span class="text-sm font-normal text-gray-500 text-uppercase">Bicicletas registradas</span></p>
            </div>
        </div>

        <div class="bg-white rounded-xl shadow-sm border border-gray-100 overflow-hidden">
            <div class="p-4 bg-gray-50 border-b flex justify-between items-center">
                <h3 class="font-bold text-gray-700 uppercase tracking-wider">Bicicletas</h3>
                <button class="js-btn-add-subentity bg-blue-600 text-white px-3 py-1.5 rounded-lg text-sm hover:bg-blue-700 transition-all">
                    + Agregar Bici
                </button>
            </div>
            <div class="js-subentity-container divide-y divide-gray-100">
                ${bicicletas.length > 0 
                    ? renderBicicletasList(bicicletas) 
                    : '<div class="p-10 text-center text-gray-400 italic">No hay bicicletas registradas.</div>'
                }
            </div>
        </div>
    </div>
    `;
}