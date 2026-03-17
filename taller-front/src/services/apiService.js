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
    }
};