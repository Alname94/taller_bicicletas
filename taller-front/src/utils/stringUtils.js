export const capitalizeWords = (str) => {
    if (!str) return "";

    return str.toLowerCase().replace(
        /(^|[\s\-])\p{L}/gu,
        (match) => match.toUpperCase()
    );
};

export const toUpperCase = (str) => {
    return str ? str.toUpperCase().trim() : "";
};

export const sanitizeText = (str) => {
    if (typeof str !== 'string') return str;
    return str.trim().replace(/\s+/g, ' ');
};