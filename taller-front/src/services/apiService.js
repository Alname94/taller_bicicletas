import { authService } from './authService';

const API_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080';

/**
 * Función base para realizar peticiones HTTP centralizadas.
 * Gestiona automáticamente los encabezados de autenticación y el parseo de errores.
 */
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

        // Caso especial: Respuestas exitosas sin contenido (ej: DELETE exitoso o 204 No Content)
        if (response.status === 204 || response.ok && method === 'DELETE') {
            return true;
        }

        if (!response.ok) {
            let errorMessage = "Error en la operación";
            try {
                const errorData = await response.json();
                // Si el error es un objeto de validación (Spring Validation), unimos los mensajes
                if (typeof errorData === 'object' && !errorData.message) {
                    errorMessage = Object.values(errorData).join(' / ');
                }
                else {
                    // Si viene un mensaje específico (BadRequestException)
                    errorMessage = errorData.message || errorData.error || errorMessage;
                }
            } catch (e) {
                errorMessage = `Error ${response.status}: ${response.statusText}`;
            }
            throw new Error(errorMessage);
        }

        // Si la respuesta es JSON, parseamos y retornamos el objeto. Si no, retornamos true para indicar éxito. 
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

// Define todos los métodos de acceso a datos del sistema organizados por módulos.
export const apiService = {
    // --- BÚSQUEDA GENÉRICA ---
    // Permite buscar por ID (si el query es numérico) o por un término específico (si es texto)
    searchEntity: (path, query, paramName = 'term') => {
        const url = isNaN(query)
            ? `/${path}/buscar?${paramName}=${encodeURIComponent(query)}`
            : `/${path}/${query}`;

        return request(url);
    },

    // --- SERVICIOS ---
    getServicios: (page = 0, size = 10) => 
        request(`/servicios/paginado?page=${page}&size=${size}`, 'GET'),
    saveServicio: (data) => request('/servicios', 'POST', data),
    updateServicio: (id, data) => request(`/servicios/${id}`, 'PUT', data),
    deleteServicio: (id) => request(`/servicios/borrar/${id}`, 'DELETE'),
    getServiciosActivos: () => request('/servicios/activos', 'GET'),

    // --- CLIENTES ---
    getClientes: (page = 0, size = 10) => 
        request(`/clientes/paginado?page=${page}&size=${size}`, 'GET'),
    saveCliente: (data) => request('/clientes', 'POST', data),
    updateCliente: (id, data) => request(`/clientes/${id}`, 'PUT', data),
    deleteCliente: (id) => request(`/clientes/borrar/${id}`, 'DELETE'),
    getClienteById: (id) => request(`/clientes/${id}`, 'GET'),

    // --- BICICLETAS ---
    getBicicletas: (page = 0, size = 10) => 
        request(`/bicicletas/paginado?page=${page}&size=${size}`, 'GET'),
    saveBicicleta: (data) => request('/bicicletas', 'POST', data),
    updateBicicleta: (id, data) => request(`/bicicletas/${id}`, 'PUT', data),
    deleteBicicleta: (id) => request(`/bicicletas/borrar/${id}`, 'DELETE'),
    getBicicletaById: (id) => request(`/bicicletas/${id}`, 'GET'),

    // --- REPUESTOS ---
    getRepuestos: (page = 0, size = 10) => 
        request(`/repuestos/paginado?page=${page}&size=${size}`),
    saveRepuesto: (data) => request('/repuestos', 'POST', data),
    updateRepuesto: (codigo, data) => request(`/repuestos/${codigo}`, 'PUT', data),
    deleteRepuesto: (codigo) => request(`/repuestos/borrar/${codigo}`, 'DELETE'),
    searchRepuestos: (query) => request(`/repuestos/buscar?query=${query}`),
    getRepuestosDisponibles: () => request('/repuestos/disponibles', 'GET'),

    // --- PRESUPUESTOS ---
    getPresupuestos: (page = 0, size = 10) => 
        request(`/presupuestos/paginado?page=${page}&size=${size}`, 'GET'),
    savePresupuesto: (data) => request('/presupuestos', 'POST', data),
    updatePresupuesto: (numero, data) => request(`/presupuestos/${numero}`, 'PUT', data),
    deletePresupuesto: (numero) => request(`/presupuestos/borrar/${numero}`, 'DELETE'),
    searchPresupuestos: (query, page = 0, size = 10) => request(`/presupuestos/buscar?query=${query}&page=${page}&size=${size}`, 'GET'),
    getPresupuestoByNumero: (numero) => request(`/presupuestos/${numero}`, 'GET'),
    patchEstadoPresupuesto: (numero, nuevoEstado) =>
        request(`/presupuestos/${numero}/estado?nuevoEstado=${nuevoEstado}`, 'PATCH'),
    patchServicioPresupuesto: (numero, servicioId) =>
        request(`/presupuestos/${numero}/servicio?servicioId=${servicioId}`, 'PATCH'),
    
    // --- DETALLES ---
    saveDetalle: (presupuestoNumero, repuestoCodigo, cantidad) => {
        const nuevoDetalle = {
            id: {
                presupuestoNumero: presupuestoNumero,
                repuestoCodigo: repuestoCodigo
            },
            cantidadAgregada: cantidad
        };
        return request('/detalles', 'POST', nuevoDetalle);
    },
    deleteDetalle: (presupuestoNumero, repuestoCodigo) => {
        return request(`/detalles/presupuesto/${presupuestoNumero}/repuesto/${repuestoCodigo}`, 'DELETE');
    },
    getDetallesByPresupuesto: (presupuestoNumero) => {
        return request(`/detalles/presupuesto/${presupuestoNumero}`, 'GET');
    },

    // --- API EXTERNA DÓLAR ---
    getDolarValue: async () => {
        try {
            const response = await fetch('https://dolarapi.com/v1/dolares/blue');
            const data = await response.json();
            return data.venta;
        } catch (error) {
            console.error("Error obteniendo el dólar:", error);
            return null;
        }
    },

    // --- DASHBOARD ---
    getDashboardStats: () => request('/dashboard/stats', 'GET'),
    
};
