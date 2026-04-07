import { apiService } from '../services/apiService';
import { renderServiciosTable, renderServicioModal } from '../views/servicesView';
import { renderClientesTable, renderClienteModal, renderClientePerfil } from '../views/clientsView';
import { renderBicicletasTable, renderBicicletaModal } from '../views/bicyclesView';

export const ENTITY_CONFIG = {
    servicios: {
        entity: 'Servicio',
        title: 'Servicios',
        path: 'servicios',
        transformations: {
            capitalize: ['nombre', 'descripcion']
        },
        fetchData: () => apiService.getServicios(),
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
        fetchData: () => apiService.getClientes(),
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
        fetchData: () => apiService.getBicicletas(),
        onSave: (id, data) => id ? apiService.updateBicicleta(id, data) : apiService.saveBicicleta(data),
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
    }
};    