import { authService } from './authService';

const API_URL = 'http://localhost:8080';

async function request(endpoint, method = 'GET', body = null) {
    const options = {
        method,
        headers: {
            'Authorization': authService.getAuthHeader(),
            'Content-Type': 'application/json'
        }
    };

    if (body) {
        options.body = JSON.stringify(body);
    }

    try {
        const response = await fetch(`${API_URL}${endpoint}`, options);

        if (response.status === 204 || response.ok && method === 'DELETE') {
            return true; 
        }

        if (!response.ok) {
            throw new Error(`Error ${response.status}`);
        }

        const contentType = response.headers.get("content-type");
        if (contentType && contentType.includes("application/json")) {
            return await response.json();
        }
        
        return true;
    } catch (error) {
        console.error(`Error en la petición ${method} ${endpoint}:`, error);
        throw error; // Re-lanzamos el error para que pueda ser manejado en la UI
    }
}

export const apiService = {
    // --- SERVICIOS ---
    getServicios: () => request('/servicios'),    
    saveServicio: (data) => request('/servicios', 'POST', data),    
    updateServicio: (id, data) => request(`/servicios/${id}`, 'PUT', data),    
    deleteServicio: (id) => request(`/servicios/borrar/${id}`, 'DELETE'),
};
