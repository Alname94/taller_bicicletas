import { apiService } from '../services/apiService';
import { renderServiciosTable, renderServicioModal } from '../views/servicesView';
import { renderClientesTable, renderClienteModal } from '../views/clientsView';

export const ENTITY_CONFIG = {
    servicios: {
        title: 'Servicios',
        fetchData: () => apiService.getServicios(),
        onSave: (id, data) => id ? apiService.updateServicio(id, data) : apiService.saveServicio(data),
        onDelete: (id) => apiService.deleteServicio(id),
        
        renderTable: (data) => renderServiciosTable(data),
        renderModal: (item) => renderServicioModal(item),
        
        btnEditClass: 'js-btn-edit-service',
        btnDeleteClass: 'js-btn-delete-service'
    },

    clientes: {
        title: 'Clientes',
        fetchData: () => apiService.getClientes(),
        onSave: (id, data) => id ? apiService.updateCliente(id, data) : apiService.saveCliente(data),
        onDelete: (id) => apiService.deleteCliente(id),

        renderTable: (data) => renderClientesTable(data),
        renderModal: (item) => renderClienteModal(item),

        btnEditClass: 'js-btn-edit-client',
        btnDeleteClass: 'js-btn-delete-client',
        btnViewClass: 'js-btn-view-client'
    }
};    