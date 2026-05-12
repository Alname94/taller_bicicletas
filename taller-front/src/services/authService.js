/**
 * Servicio encargado de gestionar la autenticación del usuario.
 * Utiliza Basic Auth enviando las credenciales codificadas en Base64.
 */

const API_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080';


export const authService = {
    /**
    * Intenta autenticar al usuario contra el endpoint de clientes.
    * Si la respuesta es exitosa, guarda las credenciales encriptadas en localStorage.
    */
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
   
    /**
     * Recupera el encabezado de autorización formateado para las peticiones fetch.
     * @returns {string|null} Header Basic Auth o null si no hay sesión activa.
     */
    getAuthHeader: () => {
        const auth = localStorage.getItem('user_auth');
        return auth ? `Basic ${auth}` : null;
    },

    logout: () => {
        localStorage.removeItem('user_auth');
        localStorage.removeItem('user_name');
        window.location.href = '/';
    },
    
    /**
     * Verifica de forma síncrona si existe una sesión guardada.
     * @returns {boolean}
     */
    isLoggedIn: () => {
        return localStorage.getItem('user_auth') !== null;
    }
};