package reservas.presentation.categoriarecurso;

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
import reservas.logic.Service;

import java.awt.Desktop;
import java.io.File;
import java.util.List;

public class Controller {

    private final View view;
    private final Model model;

    public Controller(View vista, Model model) {
        this.view = vista;
        this.model = model;
        view.setController(this);
        view.setModel(model);
        model.setList(Service.instance().listarCategorias());
    }

    public void guardar(CategoriaRecurso categoria) throws Exception {
        boolean esNueva = categoria.getId() == null || categoria.getId().isBlank();
        if (esNueva) {
            Service.instance().crearCategoria(categoria);
        } else {
            Service.instance().modificarCategoria(categoria);
        }
        model.setCurrent(new CategoriaRecurso());
        model.setList(Service.instance().listarCategorias());
    }

    public void edit(int row) {
        model.setCurrent(model.getList().get(row));
    }

    public void borrar(String id) throws Exception {
        Service.instance().eliminarCategoria(id);
        model.setCurrent(new CategoriaRecurso());
        model.setList(Service.instance().listarCategorias());
    }

    public void buscarPorDescripcion(String descripcion) {
        if (descripcion == null || descripcion.isBlank()) {
            model.setList(Service.instance().listarCategorias());
        } else {
            model.setList(Service.instance().buscarCategoriasPorDescripcion(descripcion));
        }
    }

    public void clear() {
        model.setCurrent(new CategoriaRecurso());
    }

    public void print() throws Exception {
        String dest = "categorias.pdf";
        PdfFont font = PdfFontFactory.createFont(StandardFonts.HELVETICA);
        PdfWriter writer = new PdfWriter(dest);
        PdfDocument pdf = new PdfDocument(writer);

        Document document = new Document(pdf);
        document.setMargins(20, 20, 20, 20);

        Table header = new Table(1);
        header.setWidth(400);
        header.setHorizontalAlignment(HorizontalAlignment.CENTER);
        header.addCell(getCell(new Paragraph("Listado de Categorías").setFont(font).setBold().setFontSize(16), TextAlignment.CENTER, false));
        document.add(header);

        List<CategoriaRecurso> categorias = model.getList();

        Table table = new Table(2);
        table.setWidth(500);
        table.setHorizontalAlignment(HorizontalAlignment.CENTER);

        table.addHeaderCell(getCell(new Paragraph("Id").setFont(font).setBold(), TextAlignment.CENTER, true));
        table.addHeaderCell(getCell(new Paragraph("Descripcion").setFont(font).setBold(), TextAlignment.CENTER, true));

        for (CategoriaRecurso c : categorias) {
            table.addCell(getCell(new Paragraph(c.getId()).setFont(font), TextAlignment.LEFT, true));
            table.addCell(getCell(new Paragraph(c.getDescripcion()).setFont(font), TextAlignment.LEFT, true));
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