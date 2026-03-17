export function renderServices(servicios = []) {
    const rows = servicios.map(s => `
        <tr class="border-b hover:bg-gray-50 transition-colors">
            <td class="px-6 py-4 text-sm font-medium text-gray-900">#${s.id}</td>
            <td class="px-6 py-4 text-sm text-gray-700">${s.nombre}</td>
            <td class="px-6 py-4 text-sm text-gray-500">${s.descripcion}</td>
            <td class="px-6 py-4 text-sm font-bold text-gray-900">$${s.valor.toLocaleString()}</td>
            <td class="px-6 py-4 text-sm">
                <span class="px-2 py-1 rounded-full text-xs ${s.activo ? 'bg-green-100 text-green-700' : 'bg-red-100 text-red-700'}">
                    ${s.activo ? 'Activo' : 'Inactivo'}
                </span>
            </td>
            <td class="px-6 py-4 text-sm text-right space-x-2">
                <button onclick="console.log('Editar', ${s.id})" class="text-blue-600 hover:text-blue-900 font-medium">Editar</button>
                <button onclick="console.log('Borrar', ${s.id})" class="text-red-600 hover:text-red-900 font-medium">Eliminar</button>
            </td>
        </tr>
    `).join('');

    return `
    <div class="space-y-6">
        <div class="flex justify-between items-center">
            <h3 class="text-2xl font-bold text-gray-800">Gestión de Servicios</h3>
            <button id="btnNewService" class="bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded-lg shadow transition-colors flex items-center">
                <span class="mr-2">+</span> Nuevo Servicio
            </button>
        </div>

        <div class="bg-white rounded-xl shadow-sm border border-gray-100 overflow-hidden">
            <table class="min-w-full divide-y divide-gray-200">
                <thead class="bg-gray-50">
                    <tr>
                        <th class="px-6 py-3 text-left text-xs font-semibold text-gray-500 uppercase">ID</th>
                        <th class="px-6 py-3 text-left text-xs font-semibold text-gray-500 uppercase">Nombre</th>
                        <th class="px-6 py-3 text-left text-xs font-semibold text-gray-500 uppercase">Descripción</th>
                        <th class="px-6 py-3 text-left text-xs font-semibold text-gray-500 uppercase">Precio</th>
                        <th class="px-6 py-3 text-left text-xs font-semibold text-gray-500 uppercase">Estado</th>
                        <th class="px-6 py-3 text-right text-xs font-semibold text-gray-500 uppercase">Acciones</th>
                    </tr>
                </thead>
                <tbody class="divide-y divide-gray-200 bg-white">
                    ${rows.length > 0 ? rows : '<tr><td colspan="6" class="text-center py-10 text-gray-400">No hay servicios cargados.</td></tr>'}
                </tbody>
            </table>
        </div>
    </div>
    `;
}