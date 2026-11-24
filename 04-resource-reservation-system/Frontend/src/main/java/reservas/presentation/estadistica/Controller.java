package reservas.presentation.estadistica;

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
import reservas.logic.Service;

import java.awt.Desktop;
import java.io.File;
import java.time.LocalDate;
import java.util.Map;

public class Controller {

    private final View vista;
    private final Model model;

    public Controller(View vista, Model model) {
        this.vista = vista;
        this.model = model;
        vista.setController(this);
        vista.setModel(model);

    }

    public void cargarEstadisticaRecursos(LocalDate desde, LocalDate hasta){
        Map<String, Integer> resultado = Service.instance().estadisticaRecursosPorCategoria(desde,hasta);
        model.setEstadisticasRecursos(resultado);
        model.setDesdeRecursos(desde);
        model.setHastaRecursos(hasta);
    }

    public void cargarEstadisticaActividades(LocalDate desde, LocalDate hasta) {
        Map<String, Integer> resultado = Service.instance().estadisticaActividadesPorSemana(desde,hasta);
        model.setEstadisticasActividades(resultado);
        model.setDesdeActividades(desde);
        model.setHastaActividades(hasta);
    }

    public void print() throws Exception {
        String dest = "estadisticas.pdf";
        PdfFont font = PdfFontFactory.createFont(StandardFonts.HELVETICA);
        PdfWriter writer = new PdfWriter(dest);
        PdfDocument pdf = new PdfDocument(writer);

        Document document = new Document(pdf);
        document.setMargins(20, 20, 20, 20);

        Table header = new Table(1);
        header.setWidth(400);
        header.setHorizontalAlignment(HorizontalAlignment.CENTER);
        header.addCell(getCell(new Paragraph("Estadísticas").setFont(font).setBold().setFontSize(16), TextAlignment.CENTER, false));
        document.add(header);

        Table subHeader1 = new Table(1);
        subHeader1.setWidth(400);
        subHeader1.setHorizontalAlignment(HorizontalAlignment.CENTER);
        subHeader1.addCell(getCell(new Paragraph("Recursos reservados por categoría").setFont(font).setBold().setFontSize(13), TextAlignment.CENTER, false));
        document.add(subHeader1);

        Map<String, Integer> estadisticasRecursos = model.getEstadisticasRecursos();
        Table tableRecursos = new Table(2);
        tableRecursos.setWidth(400);
        tableRecursos.setHorizontalAlignment(HorizontalAlignment.CENTER);
        tableRecursos.addHeaderCell(getCell(new Paragraph("Categoria").setFont(font).setBold(), TextAlignment.CENTER, true));
        tableRecursos.addHeaderCell(getCell(new Paragraph("Cantidad").setFont(font).setBold(), TextAlignment.CENTER, true));

        if (estadisticasRecursos != null) {
            for (Map.Entry<String, Integer> entry : estadisticasRecursos.entrySet()) {
                tableRecursos.addCell(getCell(new Paragraph(entry.getKey()).setFont(font), TextAlignment.LEFT, true));
                tableRecursos.addCell(getCell(new Paragraph(String.valueOf(entry.getValue())).setFont(font), TextAlignment.CENTER, true));
            }
        }
        document.add(tableRecursos);

        Table subHeader2 = new Table(1);
        subHeader2.setWidth(400);
        subHeader2.setHorizontalAlignment(HorizontalAlignment.CENTER);
        subHeader2.addCell(getCell(new Paragraph("Actividades programadas por semana").setFont(font).setBold().setFontSize(13), TextAlignment.CENTER, false));
        document.add(subHeader2);

        Map<String, Integer> estadisticasActividades = model.getEstadisticasActividades();
        Table tableActividades = new Table(2);
        tableActividades.setWidth(400);
        tableActividades.setHorizontalAlignment(HorizontalAlignment.CENTER);
        tableActividades.addHeaderCell(getCell(new Paragraph("Semana").setFont(font).setBold(), TextAlignment.CENTER, true));
        tableActividades.addHeaderCell(getCell(new Paragraph("Cantidad").setFont(font).setBold(), TextAlignment.CENTER, true));

        if (estadisticasActividades != null) {
            for (Map.Entry<String, Integer> entry : estadisticasActividades.entrySet()) {
                tableActividades.addCell(getCell(new Paragraph(entry.getKey()).setFont(font), TextAlignment.LEFT, true));
                tableActividades.addCell(getCell(new Paragraph(String.valueOf(entry.getValue())).setFont(font), TextAlignment.CENTER, true));
            }
        }
        document.add(tableActividades);

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