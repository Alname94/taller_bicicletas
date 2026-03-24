export function renderClientesTable(clientes = []) {
    const rows = clientes.map(c => `
        <tr class="border-b hover:bg-gray-50 transition-colors">
            <td class="px-6 py-4 text-sm font-medium text-gray-900">#${c.id}</td>
            <td class="px-6 py-4 text-sm text-gray-700">${c.nombre} ${c.apellido}</td>
            <td class="px-6 py-4 text-sm text-gray-700">${c.dni}</td>
            <td class="px-6 py-4 text-sm text-center space-x-2">
                <button data-id="${c.id}" class="js-btn-view-client text-emerald-600 hover:text-blue-900 font-medium">Ver Ficha</button>
                <button data-id="${c.id}" class="js-btn-edit-client text-blue-600 hover:text-blue-900 font-medium">Editar</button>
                <button data-id="${c.id}" class="js-btn-delete-client text-red-600 hover:text-red-900 font-medium">Eliminar</button>
            </td>
        </tr>
    `).join('');

    return `
    <div class="space-y-6">
        <div class="flex justify-between items-center">
            <h3 class="text-2xl font-bold text-gray-800">Gestión de Clientes</h3>
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
    <div class="js-modal-container">
        ${renderClienteModal()}        
    </div>
    `;
}

export function renderClienteModal(cliente = null) {
    const isEdit = !!cliente; // true si estamos editando
    
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
                <input type="hidden" name="id" value="${cliente?.id || ''}">
                <div>
                    <label class="block text-sm font-medium text-gray-700">Nombre</label>
                    <input type="text" name="nombre" required value="${cliente?.nombre || ''}" 
                        class="mt-1 block w-full border border-gray-300 rounded-lg p-2.5 focus:ring-2 focus:ring-blue-500 focus:outline-none">
                </div>
                <div>
                    <label class="block text-sm font-medium text-gray-700">Apellido</label>
                    <input type="text" name="apellido" required value="${cliente?.apellido || ''}" 
                        class="mt-1 block w-full border border-gray-300 rounded-lg p-2.5 focus:ring-2 focus:ring-blue-500 focus:outline-none">
                </div>
                <div>
                    <label class="block text-sm font-medium text-gray-700">DNI</label>
                    <input type="text" maxlength="8" name="dni" required value="${cliente?.dni || ''}" 
                        class="mt-1 block w-full border border-gray-300 rounded-lg p-2.5 focus:ring-2 focus:ring-blue-500 focus:outline-none">
                </div>
                <div>
                    <label class="block text-sm font-medium text-gray-700">Teléfono</label>
                    <input type="tel" name="telefono" pattern="^\\+?[0-9]{8,15}$" minlength="8" maxlength="15" inputmode="numeric" required value="${cliente?.telefono || ''}" 
                        class="mt-1 block w-full border border-gray-300 rounded-lg p-2.5 focus:ring-2 focus:ring-blue-500 focus:outline-none">
                </div>
                <div>
                    <label class="block text-sm font-medium text-gray-700">Email</label>
                    <input type="email" name="email" maxlength="100" required value="${cliente?.email || ''}" 
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