import { renderDetallesTable } from './detailsView.js';

/**
 * Renderiza la tabla de presupuestos disponibles.
 * Incluye formateo de moneda local y badges de estado.
 */
export function renderPresupuestosTable(presupuestos = []) {
    const rows = presupuestos.map(({ numero, fecha, clienteResumen, bicicletaResumen, valorTotal, estado }) => {
        const estadoClases = {
            'PENDIENTE': 'bg-yellow-100 text-yellow-800',
            'FACTURADO': 'bg-green-100 text-green-800',
            'ANULADO': 'bg-red-100 text-red-800'
        };
        const totalFormateado = new Intl.NumberFormat('es-AR', { style: 'currency', currency: 'ARS' }).format(valorTotal);

        return `
        <tr class="border-b hover:bg-gray-50 transition-colors">
            <td class="px-6 py-4 text-sm font-medium text-gray-900">#${numero}</td>
            <td class="px-6 py-4 text-sm text-gray-700">${fecha}</td>
            <td class="px-6 py-4 text-sm text-gray-700">${clienteResumen}</td>
            <td class="px-6 py-4 text-sm text-gray-700">${bicicletaResumen}</td>
            <td class="px-6 py-4 text-sm font-bold text-gray-900">${totalFormateado}</td>
            <td class="px-6 py-4 text-sm text-center">
                <span class="px-2 py-1 rounded-full text-xs font-semibold ${estadoClases[estado] || 'bg-gray-100'}">
                    ${estado}
                </span>
            </td>
            <td class="px-6 py-4 text-sm text-center space-x-2">
                <button data-id="${numero}" class="js-btn-view text-emerald-600 hover:text-blue-900 font-medium">Ver Presupuesto</button>
            </td>
        </tr>
    `;
    }).join('');

    return `
    <div class="space-y-6">
        <div class="flex justify-between items-center">
            <h3 class="text-2xl font-bold text-gray-800">Gestión de Presupuestos</h3>
            <div class="flex grow max-w-md">
                <div class="relative w-full md:w-96">
                    <input type="text"
                        class="js-search-input bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg rounded-r-none focus:ring-blue-500 focus:border-blue-500 block w-full pl-10 p-2.5" 
                        placeholder="Buscar por Número"
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
                        <th class="px-6 py-3 text-left text-xs font-semibold text-gray-500 uppercase tracking-wider">Número</th>
                        <th class="px-6 py-3 text-left text-xs font-semibold text-gray-500 uppercase tracking-wider">Fecha</th>
                        <th class="px-6 py-3 text-left text-xs font-semibold text-gray-500 uppercase tracking-wider">Cliente</th>
                        <th class="px-6 py-3 text-left text-xs font-semibold text-gray-500 uppercase tracking-wider">Bicicleta</th>
                        <th class="px-6 py-3 text-left text-xs font-semibold text-gray-500 uppercase tracking-wider">Total</th>
                        <th class="px-6 py-3 text-center text-xs font-semibold text-gray-500 uppercase tracking-wider">Estado</th>
                        <th class="px-6 py-3 text-center text-xs font-semibold text-gray-500 uppercase tracking-wider">Acciones</th>
                    </tr>
                </thead>
                <tbody class="divide-y divide-gray-200 bg-white">
                    ${rows.length > 0 ? rows : '<tr><td colspan="7" class="text-center py-10 text-gray-400">No hay presupuestos cargados.</td></tr>'}
                </tbody>
            </table>
        </div>
    </div>
    `;
}

/**
 * Renderiza la ficha en detalle de un presupuesto.
 * Incluye información del cliente, bicicleta y servicios aplicados.
 * Permite edición de estado, servicio aplicado y descripción si el presupuesto está pendiente.
 * También permite agregar, editar o eliminar repuestos en el detalle si el presupuesto está pendiente.
 */
export function renderPresupuestoDetalle(presupuesto, serviciosDisponibles = []) {
    const { numero, fecha, valorTotal, estado, cliente, bicicleta, descripcion, detalles, servicio, valorServicioAplicado = [] } = presupuesto;

    const esEditable = estado === 'PENDIENTE';

    const estadoClases = {
        'PENDIENTE': 'bg-yellow-100 text-yellow-800',
        'FACTURADO': 'bg-green-100 text-green-800',
        'ANULADO': 'bg-red-100 text-red-800'
    };

    const servicioActualId = servicio ? servicio.id : null;
    const existeEnDisponibles = serviciosDisponibles.some(s => s.id === servicioActualId);

    let optionsHtml = serviciosDisponibles.map(s => {
        const isSelected = (servicioActualId && s.id === servicioActualId) ? 'selected' : '';
        return `<option value="${s.id}" data-precio="${s.valor}" ${isSelected}>
            ${s.nombre} ($${s.valor})
        </option>`;
    }).join('');

    if (servicio && !existeEnDisponibles) {
        optionsHtml += `
            <option value="${servicio.id}" selected disabled>
                ${servicio.nombre} ($${valorServicioAplicado} - Inactivo)
            </option>`;
    }

    const totalFormateado = new Intl.NumberFormat('es-AR', { style: 'currency', currency: 'ARS' }).format(valorTotal);

    return `
    ${!esEditable ? `
        <div class="bg-amber-50 border-l-4 border-amber-400 p-4 mb-6">
            <div class="flex items-center">
                <i class="fas fa-lock text-amber-400 mr-3"></i>
                <p class="text-sm text-amber-700">
                    Este presupuesto está <strong>${estado}</strong> y no puede ser modificado.
                </p>
            </div>
        </div>
    ` : ''}
    <div class="p-6 space-y-6 animate-fade-in">
        <div class="flex justify-between items-start">
            <div>
                <div class="flex items-center gap-3">
                    <h2 class="text-3xl font-bold text-gray-800">Presupuesto #${numero}</h2>
                    ${esEditable ? 
                        `<select id="js-select-estado" class="px-3 py-1 rounded-full text-xs font-bold bg-blue-100 text-blue-800 border-none outline-none focus:ring-2 focus:ring-blue-500 cursor-pointer">
                            <option value="PENDIENTE" ${estado === 'PENDIENTE' ? 'selected' : ''}>PENDIENTE</option>
                            <option value="FACTURADO" ${estado === 'FACTURADO' ? 'selected' : ''}>FACTURADO</option>
                            <option value="ANULADO" ${estado === 'ANULADO' ? 'selected' : ''}>ANULADO</option>
                        </select> `
                        : `<span class="px-3 py-1 rounded-full text-xs font-bold ${estadoClases[estado]}">${estado}</span>`
                    }
                </div>
                <p class="text-gray-500 font-medium text-sm">Fecha: ${fecha}</p>
            </div>
            <button class="js-btn-back px-4 py-2 bg-gray-100 text-gray-600 rounded-lg hover:bg-gray-200 transition-colors">
                Volver a la lista
            </button>
        </div>

        <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
            <div class="bg-white p-5 rounded-xl shadow-sm border border-gray-100">
                <h3 class="text-xs font-bold mb-3 text-blue-600 uppercase tracking-widest">Cliente</h3>
                <p class="text-base font-bold text-gray-800">${cliente.nombre} ${cliente.apellido}</p>
                <p class="text-gray-500 text-xs italic">ID: #${cliente.id}</p>
            </div>

            <div class="bg-white p-5 rounded-xl shadow-sm border border-gray-100">
                <h3 class="text-xs font-bold mb-3 text-emerald-600 uppercase tracking-widest">Bicicleta</h3>
                <p class="text-base font-bold text-gray-800">${bicicleta.marca} ${bicicleta.modelo}</p>
                <p class="text-gray-500 text-xs italic">ID: #${bicicleta.id}</p>
            </div>

            <div class="bg-white p-5 rounded-xl shadow-sm border border-gray-100">
                <h3 class="text-xs font-bold mb-3 text-purple-600 uppercase tracking-widest">Servicio Aplicado</h3>

                <select id="js-select-servicio" ${!esEditable ? 'disabled' : ''} data-presupuesto="${numero}" 
                    class="w-full text-sm border rounded-lg p-2 outline-none focus:ring-2 focus:ring-purple-500 ${!esEditable ? 'bg-gray-50 cursor-not-allowed' : 'bg-white'}">
                    <option value="" ${!servicio ? 'selected' : ''} disabled>-- Seleccionar Servicio --</option>
                    ${optionsHtml}
                </select>

                <div class="mt-2 flex justify-between items-center px-1">
                    <div class="flex flex-col">
                        <span class="text-[10px] text-gray-400 font-medium uppercase">Precio en presupuesto:</span>
                        <span class="text-sm font-bold text-purple-700">
                            $${valorServicioAplicado.toLocaleString('es-AR')}
                        </span>
                    </div>
                    
                    ${esEditable && servicio ? `
                        <span class="text-[10px] bg-purple-50 text-purple-600 px-2 py-1 rounded-md font-bold">
                            MODO EDICIÓN
                        </span>
                    ` : ''}
                </div>
            </div>
        </div>

        <div class="bg-white p-4 rounded-xl shadow-sm border border-gray-100 mt-4">
            <h3 class="text-xs font-bold mb-2 text-blue-600 uppercase tracking-widest">Descripción / Notas</h3>
            <textarea 
                id="js-textarea-descripcion"
                ${!esEditable ? 'readonly' : ''}
                data-presupuesto="${numero}"
                placeholder="Agregue notas sobre el estado de la bicicleta o detalles del trabajo..." maxLength="300"
                class="w-full h-24 text-ms border rounded-lg p-3 outline-none focus:ring-2 focus:ring-blue-500 transition-all resize-none
                    ${!esEditable ? 'bg-gray-50 text-gray-500 cursor-not-allowed' : 'bg-white text-gray-800'}"
                >${descripcion || ""}</textarea>
            ${esEditable ? `<p class="text-[10px] text-gray-400 mt-2 italic">Se guarda automáticamente al hacer clic fuera del campo.</p>` : ''}
        </div> 

        <div class="bg-white rounded-xl shadow-sm border border-gray-100 overflow-hidden">
            <div class="p-4 bg-gray-50 border-b flex justify-between items-center">
                <h3 class="font-bold text-gray-700 uppercase tracking-wider text-sm">Repuestos Agregados</h3>
                ${esEditable ? `
                    <button class="js-btn-add-detalle bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded-lg text-sm font-medium shadow transition-all flex items-center">
                        <span class="mr-2">+</span> Agregar Repuesto
                    </button>
                ` : ''}
            </div>

            <div class="js-subentity-container divide-y divide-gray-100">
                ${detalles.length > 0 ? renderDetallesTable(detalles) : '<div class="p-10 text-center text-gray-400 italic">No hay repuestos agregados.</div>'}
            </div>

            <div class="p-6 bg-gray-50 border-t flex justify-end">
                <div class="text-right">
                    <p class="text-xs text-gray-500 uppercase font-bold tracking-tighter">Total Final (Servicio + Repuestos)</p>
                    <p class="text-4xl font-black text-gray-900">${totalFormateado}</p>
                </div>
            </div>
        </div>
    </div>
    `;
}

/**
 * Renderiza el modal para crear un nuevo presupuesto.
 * Pre-carga información de la bicicleta y cliente.
 * Permite seleccionar un servicio inicial para el presupuesto.
 */
export function renderPresupuestoModal(bicicleta = null, servicios = []) {
    const hoy = new Date().toISOString().split('T')[0];

    // Extraemos datos de la bicicleta y el cliente asociado
    const { id: biciId = '', marca = '', modelo = '', cliente = {} } = bicicleta || {};
    const { id: clienteId = '', nombre = '', apellido = '' } = cliente;

    return `
    <div class="js-entity-modal fixed inset-0 z-50 items-center justify-center hidden">
        <div class="absolute inset-0 bg-gray-900/40 backdrop-blur-sm"></div>
        <div class="bg-white rounded-xl shadow-2xl w-full max-w-md overflow-hidden z-10">
            <div class="p-6 border-b bg-gray-50 flex justify-between items-center">
                <h3 class="text-xl font-bold text-gray-800">Crear Nuevo Presupuesto</h3>
                <button class="js-btn-close-modal text-3xl text-gray-400 hover:text-gray-600">&times;</button>
            </div>

            <form class="js-entity-form p-6 space-y-4">
                <input type="hidden" name="bicicletaId" value="${biciId}">
                <input type="hidden" name="clienteId" value="${clienteId}">
                <input type="hidden" name="fecha" value="${hoy}">

                <div class="space-y-3">
                    <div class="bg-blue-50 p-3 rounded-lg border border-blue-100">
                        <label class="block text-xs font-semibold text-blue-600 uppercase">Cliente</label>
                        <p class="text-gray-800 font-medium">${nombre} ${apellido}</p>
                    </div>
                    
                    <div class="bg-gray-50 p-3 rounded-lg border border-gray-200">
                        <label class="block text-xs font-semibold text-gray-500 uppercase">Bicicleta</label>
                        <p class="text-gray-800 font-medium">${marca} ${modelo} (ID: ${biciId})</p>
                    </div>
                </div>

                <hr class="my-4">

                <div>
                    <label class="block text-sm font-semibold text-gray-700 mb-1">Tipo de Servicio</label>
                    <select name="servicioId" required 
                        class="mt-1 block w-full border border-gray-300 rounded-lg p-2.5 focus:ring-2 focus:ring-blue-500 outline-none bg-white">
                        <option value="" disabled selected>Seleccione un servicio...</option>
                        ${servicios.map(s => `
                            <option value="${s.id}">
                                ${s.nombre} - $${s.valor.toLocaleString()}
                            </option>
                        `).join('')}
                    </select>
                    <p class="text-xs text-gray-400 mt-2">Luego podrá agregar los repuestos necesarios en el detalle del presupuesto.</p>
                </div>

                <div>
                    <label class="block text-sm font-semibold text-gray-700 mb-1">Descripción / Notas Iniciales</label>
                    <textarea name="descripcion" rows="2" placeholder="Ej: Ruido en la caja pedalera..."
                        class="mt-1 block w-full border border-gray-300 rounded-lg p-2.5 focus:ring-2 focus:ring-blue-500 outline-none"></textarea>
                </div>

                <div class="flex justify-end space-x-3 pt-4 border-t">
                    <button type="button" class="js-btn-cancel-modal px-4 py-2 text-gray-600 hover:bg-gray-100 rounded-lg transition-colors">
                        Cancelar
                    </button>
                    <button type="submit" class="px-4 py-2 bg-green-600 text-white rounded-lg hover:bg-green-700 font-bold shadow-lg transition-all transform active:scale-95">
                        Crear y Continuar &rarr;
                    </button>
                </div>
            </form>
        </div>
    </div>`;
}