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

        if (response.status === 204) return true;

        if (!response.ok) {
            const errorData = await response.json().catch(() => ({}));
            throw new Error(errorData.message || `Error ${response.status}: ${response.statusText}`);
        }

        return await response.json();
    } catch (error) {
        console.error(`Error en la petición ${method} ${endpoint}:`, error);
        throw error; // Re-lanzamos el error para que pueda ser manejado por quien llame a esta función
    }
}

export const apiService = {
    // --- SERVICIOS ---
    getServicios: () => request('/servicios'),
    
    saveServicio: (data) => request('/servicios', 'POST', data),
    
    updateServicio: (id, data) => request(`/servicios/${id}`, 'PUT', data),
    
    deleteServicio: (id) => request(`/servicios/${id}`, 'DELETE'),
};
