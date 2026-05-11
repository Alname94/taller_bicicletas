/**
 * Pantalla de acceso inicial al sistema.
 */
export function renderLogin() {
    return `
    <div class="min-h-screen flex items-center justify-center bg-gray-50 px-4">
        <div class="max-w-md w-full space-y-8 p-10 bg-white rounded-2xl shadow-xl border border-gray-100 animate-fade-in">
            <div class="text-center">
                <div class="mx-auto h-16 w-16 bg-blue-600 rounded-2xl flex items-center justify-center shadow-lg shadow-blue-200 mb-4">
                    <span class="text-3xl text-white">⚙️</span>
                </div>
                <h2 class="text-3xl font-black text-gray-900 tracking-tight">Sistema Taller</h2>
                <p class="mt-2 text-sm text-gray-500 font-medium italic">Gestión de Presupuestos y Stock</p>
            </div>

            <form class="mt-8 space-y-6" id="loginForm">
                <div class="space-y-4">
                    <div>
                        <label for="username" class="block text-[10px] font-bold text-gray-400 uppercase mb-1 ml-1">Usuario</label>
                        <input id="username" name="username" type="text" required 
                            class="appearance-none relative block w-full px-4 py-3 border border-gray-200 placeholder-gray-400 text-gray-900 rounded-xl focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all sm:text-sm bg-gray-50" 
                            placeholder="Ej: alejo_dev">
                    </div>
                    <div>
                        <label for="password" class="block text-[10px] font-bold text-gray-400 uppercase mb-1 ml-1">Contraseña</label>
                        <input id="password" name="password" type="password" required 
                            class="appearance-none relative block w-full px-4 py-3 border border-gray-200 placeholder-gray-400 text-gray-900 rounded-xl focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all sm:text-sm bg-gray-50" 
                            placeholder="••••••••">
                    </div>
                </div>

                <div class="pt-2">
                    <button type="submit" 
                        class="group relative w-full flex justify-center py-3 px-4 border border-transparent text-sm font-bold rounded-xl text-white bg-gray-900 hover:bg-black focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 shadow-lg transition-all active:scale-95">
                        <span class="absolute left-0 inset-y-0 flex items-center pl-3">
                            <svg class="h-5 w-5 text-gray-500 group-hover:text-gray-400" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor" aria-hidden="true">
                                <path fill-rule="evenodd" d="M5 9V7a5 5 0 0110 0v2a2 2 0 012 2v5a2 2 0 01-2 2H5a2 2 0 01-2-2v-5a2 2 0 012-2zm8-2v2H7V7a3 3 0 016 0z" clip-rule="evenodd" />
                            </svg>
                        </span>
                        Ingresar al Panel
                    </button>
                </div>
            </form>
            
            <div class="text-center mt-4">
                <p class="text-[10px] text-gray-400 uppercase tracking-widest font-medium">Acceso Restringido a Personal</p>
            </div>
        </div>
    </div>
    `;
}