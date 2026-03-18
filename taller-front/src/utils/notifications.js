import Swal from 'sweetalert2';

const colors = {
    primary: '#2563eb', // blue-600
    danger: '#ef4444',  // red-500
    success: '#10b981', // emerald-500
};

export const notifications = {

    showToast: (title, icon = 'success') => {
        Swal.fire({
            title,
            icon,
            toast: true,
            position: 'bottom-end',
            showConfirmButton: false,
            timer: 4000,
            timerProgressBar: true,
            didOpen: (toast) => {
                toast.onmouseenter = Swal.stopTimer;
                toast.onmouseleave = Swal.resumeTimer;
            }
        });
    },

    
    // Mensaje de éxito/error con botón de cerrar
    showAlert: (title, text, icon = 'success') => {
        return Swal.fire({
            title,
            text,
            icon,
            confirmButtonColor: colors.primary,
            confirmButtonText: 'Aceptar'
        });
    },

    
    // Diálogo de confirmación para acciones críticas    
    showConfirm: async (title, text, confirmText = 'Sí, continuar') => {
        const result = await Swal.fire({
            title,
            text,
            icon: 'warning',
            showCancelButton: true,
            confirmButtonColor: colors.primary,
            cancelButtonColor: colors.danger,
            confirmButtonText: confirmText,
            cancelButtonText: 'Cancelar',
            reverseButtons: true,
            backdrop: `rgba(15, 23, 42, 0.4)`
        });
        
        return result.isConfirmed;
    }
};