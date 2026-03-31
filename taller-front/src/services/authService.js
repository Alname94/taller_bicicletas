const API_URL = 'http://localhost:8080';

export const authService = {
    login: async (username, password) => {
        const credentials = btoa(`${username}:${password}`);
        
        try {
            const response = await fetch(`${API_URL}/clientes`, {
                method: 'GET',
                headers: {
                    'Authorization': `Basic ${credentials}`
                }
            });

            if (response.ok) {
                localStorage.setItem('user_auth', credentials);
                localStorage.setItem('user_name', username);
                return true;
            } else {
                return false;
            }
        } catch (error) {
            console.error("Error de conexión:", error);
            return false;
        }
    },

    getAuthHeader: () => {
        const auth = localStorage.getItem('user_auth');
        return auth ? `Basic ${auth}` : null;
    },

    logout: () => {
        localStorage.removeItem('user_auth');
        localStorage.removeItem('user_name');
        window.location.href = '/';
    },

    isLoggedIn: () => {
        return localStorage.getItem('user_auth') !== null;
    }
};