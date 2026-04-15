export function renderBicicletasTable(bicicletas = []) {
    const rows = bicicletas.map(({ id, marca, modelo, fechaIngreso, cliente }) => `
        <tr class="border-b hover:bg-gray-50 transition-colors">
            <td class="px-6 py-4 text-sm font-medium text-gray-900">#${id}</td>
            <td class="px-6 py-4 text-sm text-gray-700">${marca} ${modelo}</td>
            <td class="px-6 py-4 text-sm text-gray-700">#${cliente.id} - ${cliente.nombre} ${cliente.apellido} </td>
            <td class="px-6 py-4 text-sm text-center text-gray-700">${fechaIngreso}</td>
            <td class="px-6 py-4 text-sm text-center space-x-2">
                <button data-id="${id}" class="js-btn-add-presupuesto text-emerald-600 hover:text-blue-900 font-medium">Crear Presupuesto</button>
                <button data-id="${id}" class="js-btn-edit text-blue-600 hover:text-blue-900 font-medium">Editar</button>
                <button data-id="${id}" class="js-btn-delete text-red-600 hover:text-red-900 font-medium">Eliminar</button>
            </td>
        </tr>
    `).join('');

    return `
    <div class="space-y-6">
        <div class="flex justify-between items-center">
            <h3 class="text-2xl font-bold text-gray-800">Gestión de Bicicletas</h3>
            <div class="flex grow max-w-md">
                <div class="relative w-full md:w-96">
                    <input type="text"
                        class="js-search-input bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg rounded-r-none focus:ring-blue-500 focus:border-blue-500 block w-full pl-10 p-2.5" 
                        placeholder="Buscar por ID o marca..."
                        autocomplete="off">
                </div>
                <button class="js-btn-search bg-gray-800 hover:bg-gray-900 text-white px-5 py-2.5 rounded-r-lg rounded-l-none text-sm font-medium transition-colors border border-gray-800">
                    Buscar
                </button>
            </div>
            <div class="hidden md:block w-44"></div>
        </div>


        <div class="bg-white rounded-xl shadow-sm border border-gray-100 overflow-x-auto">
            <table class="min-w-full divide-y divide-gray-200">
                <thead class="bg-gray-50">
                    <tr>
                        <th class="px-6 py-3 text-left text-xs font-semibold text-gray-500 uppercase tracking-wider">ID</th>
                        <th class="px-6 py-3 text-left text-xs font-semibold text-gray-500 uppercase tracking-wider">Marca y Modelo</th>
                        <th class="px-6 py-3 text-left text-xs font-semibold text-gray-500 uppercase tracking-wider">Cliente</th>
                        <th class="px-6 py-3 text-center text-xs font-semibold text-gray-500 uppercase tracking-wider">Fecha de Ingreso</th>
                        <th class="px-6 py-3 text-center text-xs font-semibold text-gray-500 uppercase tracking-wider">Acciones</th>
                    </tr>
                </thead>
                <tbody class="divide-y divide-gray-200 bg-white">
                    ${rows.length > 0 ? rows : '<tr><td colspan="5" class="text-center py-10 text-gray-400">No hay bicicletas cargadas.</td></tr>'}
                </tbody>
            </table>
        </div>
    </div>
    `;
}

export function renderBicicletasList(bicicletas = []) {
    return bicicletas.map(({ id, marca, modelo, color, rodado, fechaIngreso }) => `
        <div class="p-4 flex justify-between items-center hover:bg-gray-50 transition-colors group">
            <div class="flex items-center space-x-4">
                <div class="p-2 bg-blue-50 rounded-lg text-blue-600">
                    <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 19l9 2-9-18-9 18 9-2zm0 0v-8" />
                    </svg>
                </div>
                <div>
                    <h4 class="font-bold text-gray-800">${marca} ${modelo}</h4>
                </div>
            </div>
            
            <div class="flex space-x-2 opacity-0 group-hover:opacity-100 transition-opacity">
                <button data-id="${id}" class="js-btn-edit-sub p-2 text-blue-600 hover:bg-blue-100 rounded-full" title="Editar">
                    <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" viewBox="0 0 20 20" fill="currentColor">
                        <path d="M13.586 3.586a2 2 0 112.828 2.828l-.793.793-2.828-2.828.793-.793zM11.379 5.793L3 14.172V17h2.828l8.38-8.379-2.83-2.828z" />
                    </svg>
                </button>
                <button data-id="${id}" class="js-btn-delete-sub p-2 text-red-600 hover:bg-red-100 rounded-full" title="Eliminar">
                    <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" viewBox="0 0 20 20" fill="currentColor">
                        <path fill-rule="evenodd" d="M9 2a1 1 0 00-.894.553L7.382 4H4a1 1 0 000 2v10a2 2 0 002 2h8a2 2 0 002-2V6a1 1 0 100-2h-3.382l-.724-1.447A1 1 0 0011 2H9zM7 8a1 1 0 012 0v6a1 1 0 11-2 0V8zm5-1a1 1 0 00-1 1v6a1 1 0 102 0V8a1 1 0 00-1-1z" clip-rule="evenodd" />
                    </svg>
                </button>
            </div>
        </div>
    `).join('');
}

export function renderBicicletaModal(bicicleta = null, cliente = null) {
    const isEdit = !!bicicleta;
    
    // Si editamos, sacamos los datos de la bici. 
    // Si es nueva, usamos valores por defecto (como la fecha de hoy).
    const hoy = new Date().toISOString().split('T')[0];
    
    const { id = '', marca = '', modelo = '', color = '', rodado = '', fechaIngreso = hoy } = bicicleta || {};

    // El cliente puede venir como objeto (si es nuevo desde perfil) 
    // o estar dentro de la bicicleta (si estamos editando)
    const clienteData = cliente || bicicleta?.cliente || {};
    const { id: clienteId = '', nombre = '', apellido = '' } = clienteData;

    return `
    <div class="js-entity-modal fixed inset-0 z-50 items-center justify-center hidden">
        <div class="absolute inset-0 bg-gray-900/40 backdrop-blur-sm"></div>
        <div class="bg-white rounded-xl shadow-2xl w-full max-w-md overflow-hidden z-10">
            <div class="p-6 border-b bg-gray-50 flex justify-between items-center">
                <h3 class="text-xl font-bold text-gray-800">
                    ${isEdit ? 'Editar Bicicleta' : 'Registrar Bicicleta'}
                </h3>
                <button class="js-btn-close-modal text-3xl text-gray-400 hover:text-gray-600">&times;</button>
            </div>

            <form class="js-entity-form p-6 space-y-4">
                <input type="hidden" name="id" value="${id}">
                <input type="hidden" name="clienteId" value="${clienteId}">

                <div class="bg-blue-50 p-3 rounded-lg border border-blue-100 mb-4">
                    <label class="block text-xs font-semibold text-blue-600 uppercase">Cliente Asociado</label>
                    <p class="text-gray-800 font-medium">${nombre} ${apellido} (ID: ${clienteId})</p>
                </div>

                <div class="grid grid-cols-2 gap-4">
                    <div>
                        <label class="block text-sm font-medium text-gray-700">Marca</label>
                        <input type="text" name="marca" required value="${marca}" 
                            class="uppercase mt-1 block w-full border border-gray-300 rounded-lg p-2.5 focus:ring-2 focus:ring-blue-500 outline-none">
                    </div>
                    <div>
                        <label class="block text-sm font-medium text-gray-700">Modelo</label>
                        <input type="text" name="modelo" required value="${modelo}" 
                            class="uppercase mt-1 block w-full border border-gray-300 rounded-lg p-2.5 focus:ring-2 focus:ring-blue-500 outline-none">
                    </div>
                </div>

                <div class="grid grid-cols-2 gap-4">
                    <div>
                        <label class="block text-sm font-medium text-gray-700">Color</label>
                        <input type="text" name="color" required value="${color}" 
                            class="capitalize mt-1 block w-full border border-gray-300 rounded-lg p-2.5 focus:ring-2 focus:ring-blue-500 outline-none">
                    </div>
                    <div>
                        <label class="block text-sm font-medium text-gray-700">Rodado</label>
                        <input type="number" name="rodado" required value="${rodado}" 
                            class="mt-1 block w-full border border-gray-300 rounded-lg p-2.5 focus:ring-2 focus:ring-blue-500 outline-none">
                    </div>
                </div>

                <div>
                    <label class="block text-sm font-medium text-gray-700">Fecha de Ingreso</label>
                    <input type="date" name="fechaIngreso" required value="${fechaIngreso}" 
                        ${isEdit ? 'readonly class="mt-1 block w-full bg-gray-100 border border-gray-200 rounded-lg p-2.5 text-gray-500 cursor-not-allowed"' : 'class="mt-1 block w-full border border-gray-300 rounded-lg p-2.5 focus:ring-2 focus:ring-blue-500 outline-none"'}
                    >
                    ${isEdit ? '<p class="text-xs text-gray-400 mt-1 italic">La fecha de ingreso no puede modificarse.</p>' : ''}
                </div>

                <div class="flex justify-end space-x-3 pt-4 border-t">
                    <button type="button" class="js-btn-cancel-modal px-4 py-2 text-gray-600 hover:bg-gray-100 rounded-lg transition-colors">Cancelar</button>
                    <button type="submit" class="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 font-medium">
                        ${isEdit ? 'Actualizar' : 'Registrar'}
                    </button>
                </div>
            </form>
        </div>
    </div>`;
}