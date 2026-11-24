package reservas.presentation.calendarizacion;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import reservas.logic.CategoriaRecurso;
import reservas.logic.EstadoReserva;
import reservas.logic.Reserva;
import reservas.logic.Recurso;
import reservas.logic.Service;

import java.awt.Desktop;
import java.io.File;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Controller {

    private final View view;
    private final Model model;

    private static final int HORA_INICIO = 8;
    private static final int HORA_FIN = 22;

    public Controller(View view, Model model) {
        this.view = view;
        this.model = model;
        view.setController(this);
        view.setModel(model);
        view.refrescar();
    }

    public void cargar(LocalDate fecha, CategoriaRecurso categoria) {
        List<Recurso> recursos = Service.instance().buscarRecursos(categoria, "");

        List<LocalTime> horas = new ArrayList<>();
        for (int h = HORA_INICIO; h <= HORA_FIN; h++) {
            horas.add(LocalTime.of(h, 0));
        }

        List<Reserva> reservasDelDia = Service.instance().listarReservas().stream()
                .filter(r -> r.getFecha() != null && r.getFecha().equals(fecha))
                .filter(r -> r.getEstado() == EstadoReserva.CONFIRMADA || r.getEstado() == EstadoReserva.ACTIVA)
                .toList();

        String[][] celdas = new String[horas.size()][recursos.size()];

        for (int fila = 0; fila < horas.size(); fila++) {
            LocalTime hora = horas.get(fila);
            for (int col = 0; col < recursos.size(); col++) {
                Recurso recurso = recursos.get(col);
                celdas[fila][col] = buscarTextoCelda(recurso, hora, reservasDelDia);
            }
        }

        model.setMatriz(recursos, horas, celdas);
    }

    private String buscarTextoCelda(Recurso recurso, LocalTime hora, List<Reserva> reservasDelDia) {
        for (Reserva reserva : reservasDelDia) {
            boolean tieneRecurso = reserva.getRecursosAsignados() != null &&
                    reserva.getRecursosAsignados().stream().anyMatch(r -> r.getId().equals(recurso.getId()));

            if (tieneRecurso) {
                boolean dentroDeHorario = !hora.isBefore(reserva.getHoraInicio()) && hora.isBefore(reserva.getHoraFin());
                if (dentroDeHorario) {
                    String nombreFuncionario = reserva.getFuncionario() != null ? reserva.getFuncionario().getNombre() : "";
                    return reserva.getActividad() + " - " + nombreFuncionario;
                }
            }
        }
        return "";
    }

    public void print() throws Exception {
        String dest = "calendarizacion.pdf";
        PdfFont font = PdfFontFactory.createFont(StandardFonts.HELVETICA);
        PdfWriter writer = new PdfWriter(dest);
        PdfDocument pdf = new PdfDocument(writer);

        Document document = new Document(pdf);
        document.setMargins(20, 20, 20, 20);

        Table header = new Table(1);
        header.setWidth(400);
        header.setHorizontalAlignment(HorizontalAlignment.CENTER);
        header.addCell(getCell(new Paragraph("Calendarización de Recursos").setFont(font).setBold().setFontSize(16), TextAlignment.CENTER, false));
        document.add(header);

        List<Recurso> recursos = model.getRecursos();
        List<LocalTime> horas = model.getHoras();
        String[][] celdas = model.getCeldas();

        int numColumnas = recursos.size() + 1;
        Table table = new Table(numColumnas);
        table.setWidth(500);
        table.setHorizontalAlignment(HorizontalAlignment.CENTER);

        table.addHeaderCell(getCell(new Paragraph("Hora").setFont(font).setBold(), TextAlignment.CENTER, true));
        for (Recurso recurso : recursos) {
            table.addHeaderCell(getCell(new Paragraph(recurso.getDescripcion()).setFont(font).setBold(), TextAlignment.CENTER, true));
        }

        DateTimeFormatter formatoHora = DateTimeFormatter.ofPattern("HH:mm");
        for (int fila = 0; fila < horas.size(); fila++) {
            table.addCell(getCell(new Paragraph(horas.get(fila).format(formatoHora)).setFont(font), TextAlignment.CENTER, true));
            for (int col = 0; col < recursos.size(); col++) {
                String texto = celdas[fila][col] == null ? "" : celdas[fila][col];
                table.addCell(getCell(new Paragraph(texto).setFont(font), TextAlignment.LEFT, true));
            }
        }

        document.add(table);
        document.close();

        openPdf(dest);
    }

    private Cell getCell(Paragraph paragraph, TextAlignment alignment, boolean hasBorder) {
        Cell cell = new Cell().add(paragraph);
        cell.setPadding(5);
        cell.setTextAlignment(alignment);
        if (!hasBorder) cell.setBorder(Border.NO_BORDER);
        return cell;
    }

    private void openPdf(String path) {
        try {
            File pdfFile = new File(path);
            if (pdfFile.exists()) {
                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().open(pdfFile);
                } else {
                    System.out.println("AWT Desktop is not supported on this platform.");
                }
            } else {
                System.out.println("The target PDF file does not exist.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}