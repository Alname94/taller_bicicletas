import { apiService } from '../services/apiService';
import { renderServices, renderServiceModal } from '../views/servicesView';

export const ENTITY_CONFIG = {
    servicios: {
        title: 'Servicio',
        fetchData: () => apiService.getServicios(),
        onSave: (id, data) => id ? apiService.updateServicio(id, data) : apiService.saveServicio(data),
        onDelete: (id) => apiService.deleteServicio(id),
        
        renderTable: (data) => renderServices(data),
        renderModal: (item) => renderServiceModal(item),
        
        btnEditClass: 'btn-edit-service',
        btnDeleteClass: 'btn-delete-service'
    }
};    