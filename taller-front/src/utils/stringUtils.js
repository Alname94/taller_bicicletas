/**
 * Utilidades para el formateo y limpieza de texto.
 */

// Capitaliza la primera letra de cada palabra (soporta espacios y guiones).
export const capitalizeWords = (str) => {
    if (!str) return "";

    return str.toLowerCase().replace(
        /(^|[\s\-])\p{L}/gu,
        (match) => match.toUpperCase()
    );
};

// Convierte a mayúsculas y elimina espacios innecesarios.
export const toUpperCase = (str) => {
    return str ? str.toUpperCase().trim() : "";
};

// Elimina espacios en blanco sobrantes (al inicio, final y dobles espacios internos).
export const sanitizeText = (str) => {
    if (typeof str !== 'string') return str;
    return str.trim().replace(/\s+/g, ' ');
};