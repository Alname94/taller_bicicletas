import { apiService } from '../services/apiService';
import { renderServiciosTable, renderServicioModal } from '../views/servicesView';
import { renderClientesTable, renderClienteModal } from '../views/clientsView';

export const ENTITY_CONFIG = {
    servicios: {
        title: 'Servicios',
        path: 'servicios',
        fetchData: () => apiService.getServicios(),
        onSave: (id, data) => id ? apiService.updateServicio(id, data) : apiService.saveServicio(data),
        onDelete: (id) => apiService.deleteServicio(id),
        onSearch: (query) => apiService.searchEntity('servicios', query, 'nombre'),
        
        renderTable: (data) => renderServiciosTable(data),
        renderModal: (item) => renderServicioModal(item),
    },

    clientes: {
        title: 'Clientes',
        path: 'clientes',
        fetchData: () => apiService.getClientes(),
        onSave: (id, data) => id ? apiService.updateCliente(id, data) : apiService.saveCliente(data),
        onDelete: (id) => apiService.deleteCliente(id),
        onSearch: (query) => apiService.searchEntity('clientes', query, 'nombre'),

        renderTable: (data) => renderClientesTable(data),
        renderModal: (item) => renderClienteModal(item),
    }
};    