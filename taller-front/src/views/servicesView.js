export function renderServiciosTable(servicios = []) {
    const rows = servicios.map(({id, nombre, descripcion, valor, activo}) => {
        const badgeClass = activo ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800';
        const estadoTexto = activo ? 'Activo' : 'Inactivo';
        const precioFormateado = new Intl.NumberFormat('es-AR', { style: 'currency', currency: 'ARS' }).format(valor);

        return `
            <tr class="border-b hover:bg-gray-50 transition-colors">
                <td class="px-6 py-4 text-sm font-medium text-gray-900">#${id}</td>
                <td class="px-6 py-4 text-sm text-gray-700">${nombre}</td>
                <td class="px-6 py-4 text-sm text-gray-500">${descripcion}</td>
                <td class="px-6 py-4 text-sm font-bold text-gray-900">${precioFormateado}</td>
                <td class="px-6 py-4 text-sm">
                    <span class="px-2 py-1 rounded-full text-xs ${badgeClass}">
                        ${estadoTexto}
                    </span>
                </td>
                <td class="px-6 py-4 text-sm text-center space-x-2">
                    <button data-id="${id}" class="js-btn-edit text-blue-600 hover:text-blue-900 font-medium">Editar</button>
                    <button data-id="${id}" class="js-btn-delete text-red-600 hover:text-red-900 font-medium">Eliminar</button>
                </td>
            </tr>
        `;
    }).join('');

    return `
    <div class="space-y-6">
        <div class="flex justify-between items-center">
            <h3 class="text-2xl font-bold text-gray-800">Gestión de Servicios</h3>
            <div class="flex grow max-w-md">
                <div class="relative w-full md:w-96">
                    <input type="text"
                        class="js-search-input bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg rounded-r-none focus:ring-blue-500 focus:border-blue-500 block w-full pl-10 p-2.5" 
                        placeholder="Buscar por ID o nombre del servicio..."
                        autocomplete="off">
                </div>
                <button class="js-btn-search bg-gray-800 hover:bg-gray-900 text-white px-5 py-2.5 rounded-r-lg rounded-l-none text-sm font-medium transition-colors border border-gray-800">
                    Buscar
                </button>
            </div>
            <button class="js-btn-new-entity bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded-lg shadow transition-colors flex items-center">
                <span class="mr-2">+</span> Nuevo Servicio
            </button>
        </div>

        <div class="bg-white rounded-xl shadow-sm border border-gray-100 overflow-x-auto">
            <table class="min-w-full divide-y divide-gray-200">
                <thead class="bg-gray-50">
                    <tr>
                        <th class="px-6 py-3 text-left text-xs font-semibold text-gray-500 uppercase">ID</th>
                        <th class="px-6 py-3 text-left text-xs font-semibold text-gray-500 uppercase">Nombre</th>
                        <th class="px-6 py-3 text-left text-xs font-semibold text-gray-500 uppercase truncate">Descripción</th>
                        <th class="px-6 py-3 text-left text-xs font-semibold text-gray-500 uppercase">Valor</th>
                        <th class="px-6 py-3 text-left text-xs font-semibold text-gray-500 uppercase">Estado</th>
                        <th class="px-6 py-3 text-center text-xs font-semibold text-gray-500 uppercase">Acciones</th>
                    </tr>
                </thead>
                <tbody class="divide-y divide-gray-200 bg-white">
                    ${rows.length > 0 ? rows : '<tr><td colspan="6" class="text-center py-10 text-gray-400">No hay servicios cargados.</td></tr>'}
                </tbody>
            </table>
        </div>
    </div>

    <div class="js-modal-container">
        ${renderServicioModal()}
    </div>
    `;
}

export function renderServicioModal(servicio = null) {
    const isEdit = !!servicio; // true si estamos editando
    
    return `
    <div class="js-entity-modal fixed inset-0 z-50 items-center justify-center hidden">
        <div class="absolute inset-0 bg-gray-900/40 backdrop-blur-sm"></div>
        
        <div class="bg-white rounded-xl shadow-2xl w-full max-w-md overflow-hidden z-10 transform transition-all">
            <div class="p-6 border-b bg-gray-50 flex justify-between items-center">
                <h3 class="text-xl font-bold text-gray-800">
                    ${isEdit ? 'Editar Servicio' : 'Crear Nuevo Servicio'}
                </h3>
                <button class="js-btn-close-modal text-gray-400 hover:text-gray-600">&times;</button>
            </div>
            <form class="js-entity-form p-6 space-y-4">
                <input type="hidden" name="id" value="${servicio?.id || ''}">
                <div>
                    <label class="block text-sm font-medium text-gray-700">Nombre del Servicio</label>
                    <input type="text" name="nombre" required value="${servicio?.nombre || ''}" 
                        class="mt-1 block w-full border border-gray-300 rounded-lg p-2.5 focus:ring-2 focus:ring-blue-500 focus:outline-none">
                </div>
                <div>
                    <label class="block text-sm font-medium text-gray-700">Descripción</label>
                    <textarea name="descripcion" rows="3" 
                        class="mt-1 block w-full border border-gray-300 rounded-lg p-2.5 focus:ring-2 focus:ring-blue-500 focus:outline-none">${servicio?.descripcion || ''}</textarea>
                </div>
                <div>
                    <label class="block text-sm font-medium text-gray-700">Valor (ARS)</label>
                    <input type="number" name="valor" required value="${servicio?.valor || ''}" 
                        class="mt-1 block w-full border border-gray-300 rounded-lg p-2.5 focus:ring-2 focus:ring-blue-500 focus:outline-none">
                </div>
                <div>
                    <label class="block text-sm font-medium text-gray-700">Estado</label>
                    <select name="activo"
                        class="mt-1 block w-full border border-gray-300 rounded-lg p-2.5 focus:ring-2 focus:ring-blue-500 outline-none">
                        <option value="true" ${servicio?.activo ? 'selected' : ''}>Activo</option>
                        <option value="false" ${!servicio?.activo ? 'selected' : ''}>Inactivo</option>
                    </select>
                </div>
                <div class="flex justify-end space-x-3 pt-4 border-t">
                    <button type="button" class="js-btn-cancel-modal px-4 py-2 text-gray-600 hover:bg-gray-100 rounded-lg transition-colors">Cancelar</button>
                    <button type="submit" id="btnSaveService" class="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 shadow-lg shadow-blue-200 transition-all font-medium">
                        ${isEdit ? 'Guardar Cambios' : 'Crear Servicio'}
                    </button>
                </div>
            </form>
        </div>
    </div>
    `;
}