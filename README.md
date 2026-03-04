# ⚙️ **Sistema de Gestión para Taller de Bicicletas** 🚲
Este es un sistema de gestión integral desarrollado con Java y Spring Boot para la administración de un taller de reparación de bicicletas. 
El sistema permite gestionar clientes, bicicletas, repuestos, servicios y la generación de presupuestos con control de stock automatizado.

# 🚀 Características Principales
- **Gestión de Clientes y Bicicletas**: Registro detallado de propietarios y sus bicicletas asociadas.

- **Control de Stock de Repuestos**: Validación de stock en tiempo real al agregar productos a un presupuesto.

- **Servicios**: Catálogo de mano de obra con gestión de "Baja Lógica" (Soft Delete) para mantener el historial contable.

- **Presupuestos Dinámicos**: Cálculo automático de totales finales (Servicios + Repuestos) y manejo de estados (PENDIENTE, FACTURADO, ANULADO).

- **Integridad de Datos**: Validaciones de negocio para evitar duplicados de DNI, Email, Teléfono y códigos de repuestos.

# 🛠️ Tecnologías Utilizadas
- **Lenguaje**: Java 21

- **Framework**: Spring Boot

- **Persistencia**: Spring Data JPA

- **Base de Datos**: MySQL (Producción/Desarrollo)

- **Testing**: JUnit 5, Mockito, AssertJ

- **Validaciones**: Bean Validation (Hibernate Validator)

# ✅ Calidad y Testing
El proyecto cuenta con una robusta suite de Tests Unitarios que aseguran la estabilidad de las reglas de negocio:

- **Pruebas de Servicio**: Se mockearon los repositorios para testear la lógica pura, asegurando que:

    + No se descuente stock si no hay disponibilidad.

    + No se borren registros con relaciones activas (ej. no borrar un cliente con bicicletas).

    + El stock se devuelva automáticamente al anular un presupuesto.

- **Pruebas de Controlador**: Uso de MockMvc para validar los endpoints REST y el manejo de excepciones personalizadas.

# 🛠️ Buenas Prácticas y Arquitectura
- **Arquitectura en Capas**: El proyecto está estructurado siguiendo el patrón de diseño por capas (Controller - Service - Repository), separando claramente las responsabilidades.

- **Código Limpio**: Nombramiento semántico de variables y métodos, código autodocumentado y métodos breves con una única responsabilidad.

- **Inyección de Dependencias**: Uso de @Autowired y constructores para facilitar el desacoplamiento y la testeabilidad.

- **Manejo Global de Excepciones**: Implementación de un GlobalExceptionHandler para centralizar los errores y devolver respuestas consistentes al cliente.

- **Validación de Datos**: Uso de anotaciones de Jakarta Validation para asegurar la integridad de los datos antes de que lleguen a la capa de servicio.
##
> [!NOTE]
> Proyecto en desarrollo.

### Autor: Alejo Méndez
