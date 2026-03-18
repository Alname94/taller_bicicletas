import { authService } from './authService';

const API_URL = 'http://localhost:8080';

export const apiService = {
    getServicios: async () => {
        try {
            const response = await fetch(`${API_URL}/servicios`, {
                method: 'GET',
                headers: {
                    'Authorization': authService.getAuthHeader(),
                    'Content-Type': 'application/json'
                }
            });

            if (!response.ok) throw new Error('Error al obtener servicios');
            return await response.json();
        } catch (error) {
            console.error(error);
            return [];
        }
    },

    saveServicio: async (servicio) => {
        try {
            const response = await fetch('http://localhost:8080/servicios', {
                method: 'POST',
                headers: {
                    'Authorization': authService.getAuthHeader(),
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(servicio)
            });
            if (!response.ok) throw new Error('Error al guardar');
            return true;
        } catch (error) {
            console.error(error);
            return false;
        }
    },

    updateServicio: async (id, servicio) => {
        const response = await fetch(`http://localhost:8080/servicios/${id}`, {
            method: 'PUT',
            headers: {
                'Authorization': authService.getAuthHeader(),
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(servicio)
        });
        return response.ok;
    },

    deleteServicio: async (id) => {
        const response = await fetch(`http://localhost:8080/servicios/borrar/${id}`, {
            method: 'DELETE',
            headers: {
                'Authorization': authService.getAuthHeader()
            }
        });
        return response.ok;
    }
};