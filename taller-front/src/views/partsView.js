export function renderRepuestosTable(repuestos = []) {
    const rows = repuestos.map(({codigo, producto, marca, color, precioVenta, precioCosto, stock}) => {
        const precioVentaFormateado = new Intl.NumberFormat('es-AR', { style: 'currency', currency: 'ARS' }).format(precioVenta);
        const precioCostoFormateado = new Intl.NumberFormat('es-AR', { style: 'currency', currency: 'ARS' }).format(precioCosto);
        const badgeClass = stock > 5 ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800';

        return `
            <tr class="border-b hover:bg-gray-50 transition-colors">
                <td class="px-6 py-4 text-sm font-medium text-gray-900">${codigo}</td>
                <td class="px-6 py-4 text-sm text-gray-700">${producto}</td>
                <td class="px-6 py-4 text-sm text-gray-500">${marca}</td>
                <td class="px-6 py-4 text-sm text-gray-500">${color}</td>
                <td class="px-6 py-4 text-sm font-bold text-gray-900">${precioVentaFormateado}</td>
                <td class="px-6 py-4 text-sm font-bold text-gray-900">${precioCostoFormateado}</td>
                <td class="px-6 py-4 text-sm text-center">
                    <span class="px-2 py-1 rounded-full text-xs ${badgeClass}">
                        ${stock}
                    </span>
                </td>
                <td class="px-6 py-4 text-sm text-center space-x-2">
                    <button data-id="${codigo}" class="js-btn-edit text-blue-600 hover:text-blue-900 font-medium">Editar</button>
                    <button data-id="${codigo}" class="js-btn-delete text-red-600 hover:text-red-900 font-medium">Eliminar</button>
                </td>
            </tr>
        `;
    }).join('');

    return `
    <div class="space-y-6">
        <div class="flex justify-between items-center">
            <h3 class="text-2xl font-bold text-gray-800">Gestión de Repuestos</h3>
            <div class="flex grow max-w-md">
                <div class="relative w-full md:w-96">
                    <input type="text"
                        class="js-search-input bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg rounded-r-none focus:ring-blue-500 focus:border-blue-500 block w-full pl-10 p-2.5" 
                        placeholder="Buscar por código o marca del repuesto..."
                        autocomplete="off">
                </div>
                <button class="js-btn-search bg-gray-800 hover:bg-gray-900 text-white px-5 py-2.5 rounded-r-lg rounded-l-none text-sm font-medium transition-colors border border-gray-800">
                    Buscar
                </button>
            </div>
            <button class="js-btn-new-entity bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded-lg shadow transition-colors flex items-center">
                <span class="mr-2">+</span> Nuevo Repuesto
            </button>
        </div>

        <div class="bg-white rounded-xl shadow-sm border border-gray-100 overflow-x-auto">
            <table class="min-w-full divide-y divide-gray-200">
                <thead class="bg-gray-50">
                    <tr>
                        <th class="px-6 py-3 text-left text-xs font-semibold text-gray-500 uppercase tracking-wider">CÓDIGO</th>
                        <th class="px-6 py-3 text-left text-xs font-semibold text-gray-500 uppercase tracking-wider">Producto</th>
                        <th class="px-6 py-3 text-left text-xs font-semibold text-gray-500 uppercase tracking-wider">Marca</th>
                        <th class="px-6 py-3 text-left text-xs font-semibold text-gray-500 uppercase tracking-wider">Color</th>
                        <th class="px-6 py-3 text-left text-xs font-semibold text-gray-500 uppercase tracking-wider">Precio Venta</th>
                        <th class="px-6 py-3 text-left text-xs font-semibold text-gray-500 uppercase tracking-wider">Precio Costo</th>
                        <th class="px-6 py-3 text-center text-xs font-semibold text-gray-500 uppercase tracking-wider">Stock</th>
                        <th class="px-6 py-3 text-center text-xs font-semibold text-gray-500 uppercase tracking-wider">Acciones</th>
                    </tr>
                </thead>
                <tbody class="divide-y divide-gray-200 bg-white">
                    ${rows.length > 0 ? rows : '<tr><td colspan="8" class="text-center py-10 text-gray-400">No hay repuestos cargados.</td></tr>'}
                </tbody>
            </table>
        </div>
    </div>
    `;
}

export function renderRepuestoModal(repuesto = null) {
    const isEdit = !!repuesto; // true si estamos editando

    const { codigo = '', producto = '', marca = '', color = '', precioVenta = '', precioCosto = '', stock = '' } = repuesto || {};
    
    return `
    <div class="js-entity-modal fixed inset-0 z-50 items-center justify-center hidden">
        <div class="absolute inset-0 bg-gray-900/40 backdrop-blur-sm"></div>
        
        <div class="bg-white rounded-xl shadow-2xl w-full max-w-md overflow-hidden z-10 transform transition-all">
            <div class="p-6 border-b bg-gray-50 flex justify-between items-center">
                <h3 class="text-xl font-bold text-gray-800">
                    ${isEdit ? 'Editar Repuesto' : 'Crear Nuevo Repuesto'}
                </h3>
                <button type="button" class="js-btn-close-modal text-gray-400 hover:text-gray-600 text-2xl">&times;</button>
            </div>
            <form class="js-entity-form p-6 space-y-4">
                <div>
                    <label class="block text-sm font-medium text-gray-700">Código</label>
                    <input type="text" name="codigo" required value="${codigo}" 
                        ${isEdit ? 'readonly' : ''} 
                        class="uppercase mt-1 block w-full border border-gray-300 rounded-lg p-2.5 focus:ring-2 focus:ring-blue-500 focus:outline-none ${isEdit ? 'bg-gray-100 cursor-not-allowed' : 'bg-white'}">
                    ${isEdit ? '<p class="text-xs text-gray-400 mt-1">El código no se puede modificar.</p>' : ''}
                </div>
                <div>
                    <label class="block text-sm font-medium text-gray-700">Producto</label>
                    <input type="text" name="producto" required value="${producto}" 
                        class="capitalize mt-1 block w-full border border-gray-300 rounded-lg p-2.5 focus:ring-2 focus:ring-blue-500 focus:outline-none">
                </div>
                <div>
                    <label class="block text-sm font-medium text-gray-700">Marca</label>
                    <input type="text" name="marca" required value="${marca}" 
                        class="uppercase mt-1 block w-full border border-gray-300 rounded-lg p-2.5 focus:ring-2 focus:ring-blue-500 focus:outline-none">
                </div>
                <div>
                    <label class="block text-sm font-medium text-gray-700">Color</label>
                    <input type="text" name="color" required value="${color}" 
                        class="capitalize mt-1 block w-full border border-gray-300 rounded-lg p-2.5 focus:ring-2 focus:ring-blue-500 focus:outline-none">
                </div>
                <div class="grid grid-cols-2 gap-4">
                    <div>
                        <label class="block text-sm font-medium text-gray-700">Precio Venta</label>
                        <input type="number" step="0.01" name="precioVenta" required value="${precioVenta}" 
                            class="mt-1 block w-full border border-gray-300 rounded-lg p-2.5 focus:ring-2 focus:ring-blue-500 focus:outline-none">
                    </div>
                    <div>
                        <label class="block text-sm font-medium text-gray-700">Precio Costo</label>
                        <input type="number" step="0.01" name="precioCosto" required value="${precioCosto}" 
                            class="mt-1 block w-full border border-gray-300 rounded-lg p-2.5 focus:ring-2 focus:ring-blue-500 focus:outline-none">
                    </div>
                </div>
                <div>
                    <label class="block text-sm font-medium text-gray-700">Stock</label>
                    <input type="number" name="stock" required value="${stock}" 
                        class="mt-1 block w-full border border-gray-300 rounded-lg p-2.5 focus:ring-2 focus:ring-blue-500 focus:outline-none">
                </div>
                <div class="flex justify-end space-x-3 pt-4 border-t">
                    <button type="button" class="js-btn-cancel-modal px-4 py-2 text-gray-600 hover:bg-gray-100 rounded-lg transition-colors">Cancelar</button>
                    <button type="submit" class="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 shadow-lg shadow-blue-200 transition-all font-medium">
                        ${isEdit ? 'Guardar Cambios' : 'Crear Repuesto'}
                    </button>
                </div>
            </form>
        </div>
    </div>
    `;
}