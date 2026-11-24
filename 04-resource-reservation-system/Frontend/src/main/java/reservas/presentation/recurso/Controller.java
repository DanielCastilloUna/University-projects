package reservas.presentation.recurso;

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
import reservas.logic.Recurso;
import reservas.logic.Service;

import java.awt.Desktop;
import java.io.File;
import java.util.List;

public class Controller {
    View view;
    Model model;

    public Controller(View view, Model model) {
        this.view = view;
        this.model = model;
        view.setController(this);
        view.setModel(model);
        model.setCategorias(Service.instance().buscarCategoriasPorDescripcion(""));
        model.setList(Service.instance().buscarRecursos(null, ""));
    }

    public void refrescarCategorias() {
        model.setCategorias(Service.instance().buscarCategoriasPorDescripcion(""));
    }

    public void create(Recurso e) throws Exception {
        Service.instance().crearRecurso(e);
        model.setCurrent(new Recurso());
        model.setList(Service.instance().buscarRecursos(null, ""));
    }

    public void update(Recurso e) throws Exception {
        Service.instance().modificarRecurso(e);
        model.setCurrent(new Recurso());
        model.setList(Service.instance().buscarRecursos(null, ""));
    }

    public void delete(Recurso e) throws Exception {
        Service.instance().eliminarRecurso(e.getId());
        model.setCurrent(new Recurso());
        model.setList(Service.instance().buscarRecursos(null, ""));
    }

    public void search(CategoriaRecurso categoria, String descripcion) {
        model.setList(Service.instance().buscarRecursos(categoria, descripcion));
    }

    public void clear() {
        model.setCurrent(new Recurso());
    }

    public void edit(int row) {
        Recurso e = model.getList().get(row);
        model.setCurrent(e);
    }

    public void print() throws Exception {
        String dest = "recursos.pdf";
        PdfFont font = PdfFontFactory.createFont(StandardFonts.HELVETICA);
        PdfWriter writer = new PdfWriter(dest);
        PdfDocument pdf = new PdfDocument(writer);

        Document document = new Document(pdf);
        document.setMargins(20, 20, 20, 20);

        Table header = new Table(1);
        header.setWidth(400);
        header.setHorizontalAlignment(HorizontalAlignment.CENTER);
        header.addCell(getCell(new Paragraph("Listado de Recursos").setFont(font).setBold().setFontSize(16), TextAlignment.CENTER, false));
        document.add(header);

        List<Recurso> recursos = model.getList();

        Table table = new Table(3);
        table.setWidth(500);
        table.setHorizontalAlignment(HorizontalAlignment.CENTER);

        table.addHeaderCell(getCell(new Paragraph("Id").setFont(font).setBold(), TextAlignment.CENTER, true));
        table.addHeaderCell(getCell(new Paragraph("Categoria").setFont(font).setBold(), TextAlignment.CENTER, true));
        table.addHeaderCell(getCell(new Paragraph("Descripcion").setFont(font).setBold(), TextAlignment.CENTER, true));

        for (Recurso r : recursos) {
            table.addCell(getCell(new Paragraph(r.getId()).setFont(font), TextAlignment.LEFT, true));
            String categoria = r.getCategoria() != null ? r.getCategoria().getDescripcion() : "";
            table.addCell(getCell(new Paragraph(categoria).setFont(font), TextAlignment.LEFT, true));
            table.addCell(getCell(new Paragraph(r.getDescripcion()).setFont(font), TextAlignment.LEFT, true));
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