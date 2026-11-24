package reservas.logic;

import reservas.presentation.reserva.View;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IAService {

    // Nombre de la variable de entorno que cada dev/máquina debe configurar por su cuenta.
    // NUNCA se pone el valor real de nuestra API
    private static final String ENV_VAR_NAME = "LLM_API_KEY";

    private static final String GROQ_URL = "https://api.groq.com/openai/v1/chat/completions";
    private static final String GROQ_MODEL = "openai/gpt-oss-120b";

    public static class DatosReservaExtraidos {
        private String actividad;
        private LocalDate fecha;
        private LocalTime horaInicio;
        private LocalTime horaFin;
        private List<CategoriaRecurso> categoriasSugeridas;

        public String getActividad() { return actividad; }
        public LocalDate getFecha() { return fecha; }
        public LocalTime getHoraInicio() { return horaInicio; }
        public LocalTime getHoraFin() { return horaFin; }
        public List<CategoriaRecurso> getCategoriasSugeridas() { return categoriasSugeridas; }

        public void setActividad(String actividad) { this.actividad = actividad; }
        public void setFecha(LocalDate fecha) { this.fecha = fecha; }
        public void setHoraInicio(LocalTime horaInicio) { this.horaInicio = horaInicio; }
        public void setHoraFin(LocalTime horaFin) { this.horaFin = horaFin; }
        public void setCategoriasSugeridas(List<CategoriaRecurso> categoriasSugeridas) { this.categoriasSugeridas = categoriasSugeridas; }
    }

    // ============================================================
    // MÉTODO PRINCIPAL: intenta usar el LLM real; si falla por
    // cualquier razón (sin internet, sin key, error de la API),
    // cae automáticamente al método por reglas (regex) como respaldo.
    // ============================================================
    public DatosReservaExtraidos extraerDatosDesdeFrase(String frase) {
        if (frase == null || frase.trim().isEmpty()) {
            return null;
        }

        String apiKey = System.getenv(ENV_VAR_NAME);
        if (apiKey != null && !apiKey.isBlank()) {
            try {
                return extraerConLLM(frase, apiKey);
            } catch (Exception e) {
                System.out.println("Fallo la llamada al LLM, usando respaldo por reglas: " + e.getMessage());
            }
        } else {
            System.out.println("No se encontró " + ENV_VAR_NAME + " configurada; usando respaldo por reglas.");
        }

        return extraerConReglas(frase);
    }

    // ============================================================
    // LLAMADA REAL AL LLM (Groq — API
    // ============================================================
    private DatosReservaExtraidos extraerConLLM(String frase, String apiKey) throws Exception {
        List<CategoriaRecurso> categoriasDisponibles = Service.instance().listarCategorias();
        StringBuilder nombresCategorias = new StringBuilder();
        for (CategoriaRecurso c : categoriasDisponibles) {
            nombresCategorias.append("\"").append(c.getDescripcion()).append("\", ");
        }

        String prompt = """
                Extrae de la siguiente frase en español los datos de una reserva de recursos.
                Responde ÚNICAMENTE con un JSON válido, sin texto adicional, sin marcado markdown, con este formato exacto:
                {"actividad": "texto", "fecha": "YYYY-MM-DD", "horaInicio": "HH:mm", "horaFin": "HH:mm", "categorias": ["nombre1", "nombre2"]}

                Las categorías DEBEN ser exactamente alguna(s) de estas opciones disponibles: [%s]
                Si no se menciona una fecha explícita, usa la fecha de mañana.
                Si no se menciona hora de fin, asume 2 horas después del inicio.

                Frase: "%s"
                """.formatted(nombresCategorias.toString(), frase);

        String jsonBody = new org.json.JSONObject()
                .put("model", GROQ_MODEL)
                .put("messages", new org.json.JSONArray()
                        .put(new org.json.JSONObject().put("role", "user").put("content", prompt)))
                .toString();

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(GROQ_URL))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + apiKey)
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new Exception("LLM respondió con código " + response.statusCode() + ": " + response.body());
        }

        // Extraer el texto generado de la respuesta de Groq (formato OpenAI)
        org.json.JSONObject respuestaJson = new org.json.JSONObject(response.body());
        String textoGenerado = respuestaJson
                .getJSONArray("choices")
                .getJSONObject(0)
                .getJSONObject("message")
                .getString("content");

        // Limpiar por si el modelo agrega ```json ... ``` a pesar de la instrucción
        String jsonLimpio = textoGenerado.replaceAll("```json", "").replaceAll("```", "").trim();

        org.json.JSONObject datosJson = new org.json.JSONObject(jsonLimpio);

        DatosReservaExtraidos datos = new DatosReservaExtraidos();
        datos.setActividad(datosJson.optString("actividad", ""));
        datos.setFecha(LocalDate.parse(datosJson.optString("fecha", LocalDate.now().plusDays(1).toString())));
        datos.setHoraInicio(LocalTime.parse(datosJson.optString("horaInicio", "08:00")));
        datos.setHoraFin(LocalTime.parse(datosJson.optString("horaFin", "10:00")));

        List<CategoriaRecurso> sugeridas = new ArrayList<>();
        org.json.JSONArray categoriasArray = datosJson.optJSONArray("categorias");
        if (categoriasArray != null) {
            for (int i = 0; i < categoriasArray.length(); i++) {
                String nombreCat = categoriasArray.getString(i);
                for (CategoriaRecurso c : categoriasDisponibles) {
                    if (c.getDescripcion().equalsIgnoreCase(nombreCat)) {
                        sugeridas.add(c);
                        break;
                    }
                }
            }
        }
        datos.setCategoriasSugeridas(sugeridas);

        return datos;
    }

    // ============================================================
    // RESPALDO con regex
    // ============================================================
    private DatosReservaExtraidos extraerConReglas(String frase) {
        DatosReservaExtraidos datos = new DatosReservaExtraidos();
        String fraseLower = frase.toLowerCase();

        if (fraseLower.contains("mañana")) {
            datos.setFecha(LocalDate.now().plusDays(1));
        } else if (fraseLower.contains("hoy")) {
            datos.setFecha(LocalDate.now());
        } else {
            Pattern fechaPattern = Pattern.compile("\\b(\\d{4}-\\d{2}-\\d{2})\\b");
            Matcher mFecha = fechaPattern.matcher(frase);
            if (mFecha.find()) {
                datos.setFecha(LocalDate.parse(mFecha.group(1)));
            } else {
                datos.setFecha(LocalDate.now().plusDays(1));
            }
        }

        Pattern horaPattern = Pattern.compile("\\b([01]?\\d|2[0-3]):([0-5]\\d)\\b");
        Matcher mHora = horaPattern.matcher(frase);
        List<String> horas = new ArrayList<>();
        while (mHora.find()) {
            horas.add(mHora.group(0));
        }

        if (horas.size() >= 2) {
            datos.setHoraInicio(parseHora(horas.get(0)));
            datos.setHoraFin(parseHora(horas.get(1)));
        } else if (horas.size() == 1) {
            LocalTime inicio = parseHora(horas.get(0));
            datos.setHoraInicio(inicio);
            datos.setHoraFin(inicio.plusHours(2));
        } else {
            datos.setHoraInicio(LocalTime.of(8, 0));
            datos.setHoraFin(LocalTime.of(10, 0));
        }

        datos.setActividad(frase.length() > 35 ? frase.substring(0, 35) + "..." : frase);
        datos.setCategoriasSugeridas(new ArrayList<>()); // el regex no intenta adivinar categorías

        return datos;
    }

    private LocalTime parseHora(String hStr) {
        String[] partes = hStr.split(":");
        int h = Integer.parseInt(partes[0]);
        int m = Integer.parseInt(partes[1]);
        return LocalTime.of(h, m);
    }

    public void procesarFraseEInyectar(String frase, View vista) {
        DatosReservaExtraidos datos = extraerDatosDesdeFrase(frase);
        if (datos != null && vista != null) {
            String fechaStr = datos.getFecha() != null ? datos.getFecha().toString() : "";
            String hInicioStr = datos.getHoraInicio() != null ? datos.getHoraInicio().toString() : "";
            String hFinStr = datos.getHoraFin() != null ? datos.getHoraFin().toString() : "";
            vista.setDatosExtraidosIA(datos.getActividad(), fechaStr, hInicioStr, hFinStr, datos.getCategoriasSugeridas());
        }
    }
}