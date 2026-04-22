import { apiService } from '../services/apiService';
import { renderServiciosTable, renderServicioModal } from '../views/servicesView';
import { renderClientesTable, renderClienteModal, renderClientePerfil } from '../views/clientsView';
import { renderBicicletasTable, renderBicicletaModal } from '../views/bicyclesView';
import { renderRepuestosTable, renderRepuestoModal } from '../views/partsView';
import { renderPresupuestosTable, renderPresupuestoDetalle, renderPresupuestoModal } from '../views/budgetsView';
import { renderDetallesTable, renderSelectRepuestosModal } from '../views/detailsView';

export const ENTITY_CONFIG = {
    servicios: {
        entity: 'Servicio',
        title: 'Servicios',
        path: 'servicios',
        transformations: {
            capitalize: ['nombre', 'descripcion']
        },
        fetchData: (page = 0) => apiService.getServicios(page),
        onSave: (id, data) => id ? apiService.updateServicio(id, data) : apiService.saveServicio(data),
        onDelete: (id) => apiService.deleteServicio(id),
        onSearch: (query) => apiService.searchEntity('servicios', query, 'nombre'),

        renderTable: (data) => renderServiciosTable(data),
        renderModal: (item) => renderServicioModal(item),
    },

    clientes: {
        entity: 'Cliente',
        subEntity: 'bicicletas',
        title: 'Clientes',
        path: 'clientes',
        transformations: {
            capitalize: ['nombre', 'apellido']
        },
        fetchData: (page = 0) => apiService.getClientes(page),
        onSave: (id, data) => id ? apiService.updateCliente(id, data) : apiService.saveCliente(data),
        onDelete: (id) => apiService.deleteCliente(id),
        onSearch: (query) => apiService.searchEntity('clientes', query, 'nombre'),

        renderTable: (data) => renderClientesTable(data),
        renderModal: (item) => renderClienteModal(item),
        renderProfile: async (id) => {
            const data = await apiService.getClienteById(id);
            return { html: renderClientePerfil(data), data: data };
        }
    },

    bicicletas: {
        entity: 'Bicicleta',
        parentEntity: 'clientes',
        title: 'Bicicletas',
        path: 'bicicletas',
        transformations: {
            capitalize: ['color'],
            uppercase: ['marca', 'modelo']
        },
        fetchData: (page = 0) => apiService.getBicicletas(page),
        onSave: async (id, formData) => {
            // Transformo el formData plano en la estructura que espera el backend
            const dataParaEnviar = {
                marca: formData.marca,
                modelo: formData.modelo,
                color: formData.color,
                rodado: formData.rodado,
                fechaIngreso: formData.fechaIngreso,
                // Construyo el objeto cliente que espera Spring
                cliente: {
                    id: formData.clienteId
                }
            };
            return id ? apiService.updateBicicleta(id, dataParaEnviar) : apiService.saveBicicleta(dataParaEnviar);
        },
        onDelete: (id) => apiService.deleteBicicleta(id),
        onSearch: (query) => apiService.searchEntity('bicicletas', query, 'marca'),

        renderTable: (data) => renderBicicletasTable(data),
        renderModal: (item, parentId) => renderBicicletaModal(item, parentId),
    },

    repuestos: {
        entity: 'Repuesto',
        title: 'Repuestos',
        path: 'repuestos',
        transformations: {
            capitalize: ['producto', 'color'],
            uppercase: ['codigo', 'marca']
        },
        fetchData: (page = 0) => apiService.getRepuestos(page),
        onSave: async (id, formData) => {
            return id
                ? apiService.updateRepuesto(id, formData)
                : apiService.saveRepuesto(formData);
        },
        onDelete: (codigo) => apiService.deleteRepuesto(codigo),
        onSearch: (query) => apiService.searchEntity('repuestos', query, 'query'),

        renderTable: (data) => renderRepuestosTable(data),
        renderModal: (item) => renderRepuestoModal(item),
    },

    presupuestos: {
        entity: 'Presupuesto',
        subEntity: 'detalles',
        title: 'Presupuestos',
        path: 'presupuestos',
        fetchData: () => apiService.getPresupuestos(),
        onDelete: (numero) => apiService.deletePresupuesto(numero),
        onSearch: (query) => apiService.searchEntity('presupuestos', query, 'query'),
        fetchFullData: async (id) => {
            const [presupuesto, servicios] = await Promise.all([
                request(`/presupuestos/${id}`, 'GET'),
                request('/servicios/activos', 'GET')
            ]);
            return { presupuesto, servicios };
        },
        onAction: async (action, id, payload) => {
            if (action === 'cambiarEstado') {
                return await apiService.patchEstadoPresupuesto(id, payload.nuevoEstado);
            }
            if (action === 'cambiarServicio') {
                return await apiService.patchServicioPresupuesto(id, payload.nuevoServicioId);
            }
        },
        onSave: async (id, formData) => {
            const dataParaEnviar = {
                numero: id ? Number(id) : null,
                cliente: { id: Number(formData.cliente?.id || formData.clienteId) },
                bicicleta: { id: Number(formData.bicicleta?.id || formData.bicicletaId) },
                servicio: formData.servicioId
                    ? { id: Number(formData.servicioId) }
                    : (formData.servicio?.id ? { id: Number(formData.servicio.id) } : null),
                fecha: formData.fecha,
                descripcion: formData.descripcion || "",
                valorTotal: formData.valorTotal || 0,
                valorServicioAplicado: formData.valorServicioAplicado,
                detalles: formData.detalles || []
            };

            if (id) {
                return apiService.updatePresupuesto(id, dataParaEnviar);
            } else {
                return apiService.savePresupuesto(dataParaEnviar);
            }
        },

        renderTable: (data) => renderPresupuestosTable(data),
        renderProfile: async (id) => {
            const [presupuesto, servicios] = await Promise.all([
                apiService.getPresupuestoByNumero(id),
                apiService.getServiciosActivos()
            ]);
            return {
                html: renderPresupuestoDetalle(presupuesto, servicios),
                data: presupuesto
            };
        },
        renderModal: async (item, extraData) => {
            // extraData puede ser el objeto Bicicleta que viene del botón
            const servicios = await apiService.getServiciosActivos();
            return renderPresupuestoModal(extraData, servicios);
        },
    },

    detalles: {
        entity: 'Detalle',
        parentEntity: 'presupuestos',
        onSave: async (presupuestoId, data) => {
            return await apiService.saveDetalle(
                presupuestoId,
                data.repuestoCodigo,
                data.cantidad
            );
        },
        onDelete: async (compositeId) => {
            const [presupuestoId, repuestoCodigo] = compositeId.split('-');
            return await apiService.deleteDetalle(presupuestoId, repuestoCodigo);
        },
        onAction: async (action) => {
            if (action === 'abrirBuscadorRepuestos') return await apiService.getRepuestosDisponibles();
        },
        getData: async (presupuestoId) => await apiService.getDetallesByPresupuesto(presupuestoId),

        renderTable: (data) => renderDetallesTable(data),
        renderSearchModal: (data) => renderSelectRepuestosModal(data),
    }
};    