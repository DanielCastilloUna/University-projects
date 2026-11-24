package reservas.presentation.actividad;

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
import reservas.logic.EstadoReserva;
import reservas.logic.Reserva;
import reservas.logic.Service;

import java.awt.Desktop;
import java.io.File;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Controller {

    private final View vista;
    private final Model model;

    private static final int HORA_INICIO = 8;
    private static final int HORA_FIN = 22;

    public Controller(View vista, Model model) {
        this.vista = vista;
        this.model = model;
        vista.setController(this);
        vista.setModel(model);
        vista.refrescar();
    }

    public void cargar(LocalDate fechaReferencia) {
        LocalDate lunes = fechaReferencia.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));

        List<LocalDate> dias = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            dias.add(lunes.plusDays(i));
        }

        List<Integer> horas = new ArrayList<>();
        for (int h = HORA_INICIO; h <= HORA_FIN; h++) {
            horas.add(h);
        }

        List<Reserva> reservasSemana = Service.instance().listarReservas().stream()
                .filter(r -> r.getFecha() != null && !r.getFecha().isBefore(lunes) && !r.getFecha().isAfter(lunes.plusDays(6)))
                .filter(r -> r.getEstado() == EstadoReserva.CONFIRMADA || r.getEstado() == EstadoReserva.ACTIVA)
                .toList();

        String[][] celdas = new String[horas.size()][dias.size()];

        for (int fila = 0; fila < horas.size(); fila++) {
            int hora = horas.get(fila);
            for (int col = 0; col < dias.size(); col++) {
                LocalDate dia = dias.get(col);
                celdas[fila][col] = buscarTextoCelda(dia, hora, reservasSemana);
            }
        }

        model.setMatriz(dias, horas, celdas);
    }

    private String buscarTextoCelda(LocalDate dia, int hora, List<Reserva> reservasSemana) {
        LocalTime horaInicioSlot = LocalTime.of(hora, 0);
        for (Reserva reserva : reservasSemana) {
            if (reserva.getFecha().equals(dia)) {
                boolean dentroDeHorario = !horaInicioSlot.isBefore(reserva.getHoraInicio()) && horaInicioSlot.isBefore(reserva.getHoraFin());
                if (dentroDeHorario) {
                    String nombreFuncionario = reserva.getFuncionario() != null ? reserva.getFuncionario().getNombre() : "";
                    return nombreFuncionario.isBlank()
                            ? reserva.getActividad()
                            : reserva.getActividad() + "\n(" + nombreFuncionario + ")";
                }
            }
        }
        return "";
    }

    public void print() throws Exception {
        String dest = "actividades.pdf";
        PdfFont font = PdfFontFactory.createFont(StandardFonts.HELVETICA);
        PdfWriter writer = new PdfWriter(dest);
        PdfDocument pdf = new PdfDocument(writer);

        Document document = new Document(pdf);
        document.setMargins(20, 20, 20, 20);

        Table header = new Table(1);
        header.setWidth(400);
        header.setHorizontalAlignment(HorizontalAlignment.CENTER);
        header.addCell(getCell(new Paragraph("Programación de Actividades").setFont(font).setBold().setFontSize(16), TextAlignment.CENTER, false));
        document.add(header);

        List<LocalDate> dias = model.getDias();
        List<Integer> horas = model.getHoras();
        String[][] celdas = model.getCeldas();

        int numColumnas = dias.size() + 1;
        Table table = new Table(numColumnas);
        table.setWidth(500);
        table.setHorizontalAlignment(HorizontalAlignment.CENTER);

        table.addHeaderCell(getCell(new Paragraph("Hora").setFont(font).setBold(), TextAlignment.CENTER, true));
        DateTimeFormatter formatoDia = DateTimeFormatter.ofPattern("dd/MM");
        for (LocalDate dia : dias) {
            String nombreDia = dia.getDayOfWeek().getDisplayName(TextStyle.SHORT, new Locale("es"));
            table.addHeaderCell(getCell(new Paragraph(nombreDia + " " + dia.format(formatoDia)).setFont(font).setBold(), TextAlignment.CENTER, true));
        }

        for (int fila = 0; fila < horas.size(); fila++) {
            table.addCell(getCell(new Paragraph(String.format("%02d:00", horas.get(fila))).setFont(font), TextAlignment.CENTER, true));
            for (int col = 0; col < dias.size(); col++) {
                String texto = celdas[fila][col] == null ? "" : celdas[fila][col].replace("\n", " ");
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