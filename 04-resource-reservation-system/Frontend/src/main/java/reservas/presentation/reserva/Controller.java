package reservas.presentation.reserva;

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
import reservas.logic.Funcionario;
import reservas.logic.IAService;
import reservas.logic.Recurso;
import reservas.logic.Reserva;
import reservas.logic.ResultadoReserva;
import reservas.logic.Service;

import java.awt.Desktop;
import java.io.File;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class Controller {

    private final View vista;
    private final Model model;
    private final IAService servicioIA;

    public Controller(View vista, Model model, IAService servicioIA) {
        this.vista = vista;
        this.model = model;
        this.servicioIA = servicioIA;

        this.vista.setController(this);
        this.vista.setModel(model);

        this.model.setCategorias(Service.instance().buscarCategoriasPorDescripcion(""));
    }

    public Reserva reservar(Funcionario funcionario, String actividad, LocalDate fecha,
                         LocalTime horaInicio, LocalTime horaFin,
                         List<CategoriaRecurso> categorias) throws Exception {

        ResultadoReserva resultado = Service.instance().intentarReservar(funcionario, actividad, fecha, horaInicio, horaFin, categorias);

        if (resultado != null && resultado.isExito()) {
            refrescarReservas(funcionario);
            vista.limpiarCampos();
            return resultado.getReserva();
        } else {
            throw new Exception("No se pudieron asignar los recursos solicitados para ese horario.");
        }
    }

    public Reserva modificar(String idOriginal, Funcionario funcionario, String actividad, LocalDate fecha,
                         LocalTime horaInicio, LocalTime horaFin,
                         List<CategoriaRecurso> categorias) throws Exception {

        ResultadoReserva resultado = Service.instance().modificarReserva(idOriginal, actividad, fecha, horaInicio, horaFin, categorias);

        if (resultado != null && resultado.isExito()) {
            refrescarReservas(funcionario);
            vista.limpiarCampos();
            return resultado.getReserva();
        } else {
            throw new Exception("No se pudieron asignar los recursos solicitados para ese horario.");
        }
    }

    public void cancelar(String idReserva, Funcionario funcionario) throws Exception {
        Service.instance().cancelarReserva(idReserva);
        refrescarReservas(funcionario);
    }

    public void extraerDatosDesdeIA(String frase) {
        if (frase != null && !frase.trim().isEmpty()) {
            servicioIA.procesarFraseEInyectar(frase, vista);
        }
    }

    public void refrescarCategorias() {
        model.setCategorias(Service.instance().buscarCategoriasPorDescripcion(""));
    }

    public void refrescarReservas(Funcionario funcionario) {
        if (funcionario != null) {
            model.setMisReservas(Service.instance().listarReservasDe(funcionario));
        } else {
            model.setMisReservas(Service.instance().listarReservas());
        }
    }

    public void print() throws Exception {
        String dest = "reservas.pdf";
        PdfFont font = PdfFontFactory.createFont(StandardFonts.HELVETICA);
        PdfWriter writer = new PdfWriter(dest);
        PdfDocument pdf = new PdfDocument(writer);

        Document document = new Document(pdf);
        document.setMargins(20, 20, 20, 20);

        Table header = new Table(1);
        header.setWidth(400);
        header.setHorizontalAlignment(HorizontalAlignment.CENTER);
        header.addCell(getCell(new Paragraph("Mis Reservas").setFont(font).setBold().setFontSize(16), TextAlignment.CENTER, false));
        document.add(header);

        List<Reserva> reservas = model.getMisReservas();

        Table table = new Table(6);
        table.setWidth(500);
        table.setHorizontalAlignment(HorizontalAlignment.CENTER);

        table.addHeaderCell(getCell(new Paragraph("Id").setFont(font).setBold(), TextAlignment.CENTER, true));
        table.addHeaderCell(getCell(new Paragraph("Actividad").setFont(font).setBold(), TextAlignment.CENTER, true));
        table.addHeaderCell(getCell(new Paragraph("Fecha").setFont(font).setBold(), TextAlignment.CENTER, true));
        table.addHeaderCell(getCell(new Paragraph("Horario").setFont(font).setBold(), TextAlignment.CENTER, true));
        table.addHeaderCell(getCell(new Paragraph("Recursos").setFont(font).setBold(), TextAlignment.CENTER, true));
        table.addHeaderCell(getCell(new Paragraph("Estado").setFont(font).setBold(), TextAlignment.CENTER, true));

        for (Reserva r : reservas) {
            table.addCell(getCell(new Paragraph(r.getId()).setFont(font), TextAlignment.LEFT, true));
            table.addCell(getCell(new Paragraph(r.getActividad()).setFont(font), TextAlignment.LEFT, true));
            table.addCell(getCell(new Paragraph(r.getFecha() != null ? r.getFecha().toString() : "").setFont(font), TextAlignment.LEFT, true));
            String horario = (r.getHoraInicio() != null ? r.getHoraInicio().toString() : "") + " - " + (r.getHoraFin() != null ? r.getHoraFin().toString() : "");
            table.addCell(getCell(new Paragraph(horario).setFont(font), TextAlignment.LEFT, true));
            String recursosTexto = recursosComoTexto(r.getRecursosAsignados());
            table.addCell(getCell(new Paragraph(recursosTexto).setFont(font), TextAlignment.LEFT, true));
            table.addCell(getCell(new Paragraph(r.getEstado() != null ? r.getEstado().toString() : "").setFont(font), TextAlignment.LEFT, true));
        }

        document.add(table);
        document.close();

        openPdf(dest);
    }

    private String recursosComoTexto(List<Recurso> recursos) {
        if (recursos == null || recursos.isEmpty()) return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < recursos.size(); i++) {
            if (i > 0) sb.append(", ");
            sb.append(recursos.get(i).getId());
        }
        return sb.toString();
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