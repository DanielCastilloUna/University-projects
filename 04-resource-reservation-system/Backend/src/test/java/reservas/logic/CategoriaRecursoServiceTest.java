package reservas.logic;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CategoriaRecursoServiceTest {

    @Test
    void crearCategoriaSinDescripcionDebeFallar() {
        CategoriaRecurso c = new CategoriaRecurso();

        Exception ex = assertThrows(Exception.class, () -> {
            Service.instance().crearCategoria(c);
        });

        assertEquals("La descripcion es obligatoria", ex.getMessage());
    }

    @Test
    void crearCategoriaNulaDebeFallar() {
        Exception ex = assertThrows(Exception.class, () -> {
            Service.instance().crearCategoria(null);
        });

        assertEquals("La categoria no puede ser nula", ex.getMessage());
    }

    @Test
    void buscarCategoriaConIdVacioDebeRetornarNull() {
        CategoriaRecurso resultado = Service.instance().buscarCategoriaPorId("");
        assertNull(resultado);
    }
}