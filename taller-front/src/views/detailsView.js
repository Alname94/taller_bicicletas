/**
 * Renderiza la lista de repuestos ya asignados al presupuesto.
 */
export function renderDetallesTable(detalles = []) {
    const rows = detalles.map(d => `
        <tr class="border-b hover:bg-gray-50 transition-colors">
            <td class="px-4 py-3 text-sm text-gray-800 font-medium">
                ${d.repuesto.producto}
                <div class="text-xs text-gray-400">${d.id.repuestoCodigo}</div>
            </td>
            <td class="px-4 py-3 text-sm text-gray-600 text-center">
                ${d.cantidadAgregada}
            </td>
            <td class="px-4 py-3 text-sm text-gray-600">
                $${d.precioUnitario.toLocaleString()}
            </td>
            <td class="px-4 py-3 text-sm font-bold text-gray-900">
                $${d.subtotal.toLocaleString()}
            </td>
            <td class="px-4 py-3 text-right">
                <button data-id="${d.id.presupuestoNumero}-${d.id.repuestoCodigo}" class="js-btn-delete-sub p-2 text-red-600 hover:bg-red-100 rounded-full" title="Eliminar">
                    <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" viewBox="0 0 20 20" fill="currentColor">
                        <path fill-rule="evenodd" d="M9 2a1 1 0 00-.894.553L7.382 4H4a1 1 0 000 2v10a2 2 0 002 2h8a2 2 0 002-2V6a1 1 0 100-2h-3.382l-.724-1.447A1 1 0 0011 2H9zM7 8a1 1 0 012 0v6a1 1 0 11-2 0V8zm5-1a1 1 0 00-1 1v6a1 1 0 102 0V8a1 1 0 00-1-1z" clip-rule="evenodd" />
                    </svg>
                </button>
            </td>
        </tr>
    `).join('');

    return `
        <div class="overflow-x-auto">
            <table class="min-w-full bg-white">
                <thead class="bg-gray-50 border-b">
                    <tr>
                        <th class="px-4 py-3 text-left text-xs font-semibold text-gray-500 uppercase">Repuesto</th>
                        <th class="px-4 py-3 text-center text-xs font-semibold text-gray-500 uppercase">Cant.</th>
                        <th class="px-4 py-3 text-left text-xs font-semibold text-gray-500 uppercase">Precio Unit.</th>
                        <th class="px-4 py-3 text-left text-xs font-semibold text-gray-500 uppercase">Subtotal</th>
                        <th class="px-4 py-3 text-right text-xs font-semibold text-gray-500 uppercase">Acción</th>
                    </tr>
                </thead>
                <tbody>
                    ${rows.length > 0 ? rows : '<tr><td colspan="5" class="p-8 text-center text-gray-400 italic">No hay repuestos agregados.</td></tr>'}
                </tbody>
            </table>
        </div>
    `;
}

/**
 * Modal de búsqueda y selección de repuestos disponibles en stock.
 */
export function renderSelectRepuestosModal(repuestosDisponibles = []) {
    const rows = repuestosDisponibles.map(r => `
        <tr class="border-b hover:bg-gray-50">
            <td class="px-4 py-3">
                <div class="font-bold text-gray-800">${r.producto}</div>
                <div class="text-xs text-gray-500">${r.codigo} | ${r.marca}</div>
            </td>
            <td class="px-4 py-3 text-sm text-gray-600">${r.color}</td>
            <td class="px-4 py-3 text-sm text-center">
                <span class="${r.stock < 5 ? 'text-red-600 font-bold' : 'text-gray-700'}">
                    ${r.stock}
                </span>
            </td>
            <td class="px-4 py-3 text-sm font-bold text-gray-900">
                $${r.precioVenta}
            </td>
            <td class="px-4 py-3 text-center">
                <div class="flex items-center justify-center gap-2">
                    <input type="number" id="qty-${r.codigo}" value="1" min="1" max="${r.stock}" 
                        class="w-12 text-center border rounded p-1 text-sm">
                    <button data-id="${r.codigo}" class="js-btn-add-item-to-budget bg-blue-600 text-white p-1.5 rounded-lg hover:bg-blue-700">
                        ＋
                    </button>
                </div>
            </td>
        </tr>
    `).join('');

    return `
    <div class="js-entity-modal fixed inset-0 z-60 flex items-center justify-center">
        <div class="absolute inset-0 bg-gray-900/50 backdrop-blur-sm"></div>
        <div class="bg-white rounded-2xl shadow-2xl w-full max-w-3xl max-h-[80vh] flex flex-col z-10 overflow-hidden">
            
            <div class="p-6 border-b bg-gray-50">
                <div class="flex justify-between items-center mb-4">
                    <h3 class="text-xl font-bold text-gray-800">Seleccionar Repuestos</h3>
                    <button class="js-btn-close-modal text-gray-400 hover:text-gray-600 text-2xl">&times;</button>
                </div>
                <div class="relative">
                    <input type="text" id="search-repuesto-modal" 
                        class="w-full pl-10 pr-4 py-2.5 border border-gray-300 rounded-xl focus:ring-2 focus:ring-blue-500 outline-none" 
                        placeholder="Buscar por nombre, marca o código...">
                    <span class="absolute left-3 top-3 text-gray-400">🔍</span>
                </div>
            </div>

            <div class="overflow-y-auto grow p-2">
                <table class="min-w-full text-left">
                    <thead class="sticky top-0 bg-white shadow-sm">
                        <tr class="text-xs font-bold text-gray-500 uppercase">
                            <th class="px-4 py-3">Repuesto</th>
                            <th class="px-4 py-3">Color</th>
                            <th class="px-4 py-3 text-center">Stock</th>
                            <th class="px-4 py-3">Precio</th>
                            <th class="px-4 py-3 text-center">Acción</th>
                        </tr>
                    </thead>
                    <tbody>
                        ${rows.length > 0 ? rows : '<tr><td colspan="5" class="p-10 text-center text-gray-400 italic">No se encontraron repuestos.</td></tr>'}
                    </tbody>
                </table>
            </div>

            <div class="p-4 border-t bg-gray-50 flex justify-end">
                <button class="js-btn-close-modal px-6 py-2 bg-gray-800 text-white rounded-xl font-medium">Finalizar</button>
            </div>
        </div>
    </div>
    `;
}