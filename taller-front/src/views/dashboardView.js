export function renderDashboardLayout() {
  return `
    <div class="flex h-screen bg-gray-50">
      <aside class="w-64 bg-white shadow-md flex flex-col">
        <div class="p-6">
          <h1 class="text-2xl font-bold text-blue-600">Taller</h1>
        </div>
        
        <nav class="flex-1 px-4 space-y-2">
          <a href="#" id="link-home" class="flex items-center p-2 text-gray-700 bg-blue-50 rounded-lg group">
            <span class="ml-3 font-medium">Dashboard</span>
          </a>
          <a href="#" id="link-clientes" class="flex items-center p-2 text-gray-600 hover:bg-gray-100 rounded-lg group">
            <span class="ml-3">Clientes</span>
          </a>
          <a href="#" id="link-bicicletas" class="flex items-center p-2 text-gray-600 hover:bg-gray-100 rounded-lg group">
            <span class="ml-3">Bicicletas</span>
          </a>
          <a href="#" id="link-presupuestos" class="flex items-center p-2 text-gray-600 hover:bg-gray-100 rounded-lg group">
            <span class="ml-3">Presupuestos</span>
          </a>
          <a href="#" id="link-servicios" class="flex items-center p-2 text-gray-600 hover:bg-gray-100 rounded-lg group">
            <span class="ml-3">Servicios</span>
          </a>
          <a href="#" id="link-repuestos" class="flex items-center p-2 text-gray-600 hover:bg-gray-100 rounded-lg group">
            <span class="ml-3">Repuestos</span>
          </a>
        </nav>

        <div class="p-4 border-t">
          <button id="logoutBtn" class="flex items-center w-full p-2 text-red-600 hover:bg-red-50 rounded-lg transition-colors">
            <span class="ml-3 font-medium">Cerrar Sesión</span>
          </button>
        </div>
      </aside>

      <main id="main-content" class="flex-1 overflow-y-auto p-8">        
      </main>
    </div>
    `;
}

export function renderHomeContent() {
  const userName = localStorage.getItem('user_name') || 'Mecánico';
  return `
    <header class="flex justify-between items-center mb-8">
          <div>
            <h2 class="text-3xl font-bold text-gray-800">Panel de Control</h2>
            <p class="text-gray-500">Hola, ${userName}. Este es el resumen de hoy.</p>
          </div>
          <div class="flex items-center space-x-4">
            <span class="text-sm font-medium text-gray-500">TC: $1.430,00</span>
            <div class="w-10 h-10 rounded-full bg-blue-500 flex items-center justify-center text-white">
              ${userName.charAt(0).toUpperCase()}
            </div>
          </div>
        </header>

        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
          <div class="bg-blue-500 p-6 rounded-xl shadow-sm text-white">
            <p class="text-blue-100 text-sm font-medium">Presupuestos Pendientes</p>
            <p class="text-3xl font-bold mt-1">12</p>
          </div>
          <div class="bg-emerald-500 p-6 rounded-xl shadow-sm text-white">
            <p class="text-emerald-100 text-sm font-medium">Presupuestos del Mes</p>
            <p class="text-3xl font-bold mt-1">5</p>
          </div>
          <div class="bg-orange-500 p-6 rounded-xl shadow-sm text-white">
            <p class="text-orange-100 text-sm font-medium">Clientes Activos</p>
            <p class="text-3xl font-bold mt-1">84</p>
          </div>
          <div class="bg-rose-500 p-6 rounded-xl shadow-sm text-white">
            <p class="text-rose-100 text-sm font-medium">Stock Bajo</p>
            <p class="text-3xl font-bold mt-1 text-white">3</p>
          </div>
        </div> 
        `;
}