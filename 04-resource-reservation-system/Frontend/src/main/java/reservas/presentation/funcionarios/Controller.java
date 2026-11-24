package reservas.presentation.funcionarios;

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
import reservas.logic.Funcionario;
import reservas.logic.Service;

import java.awt.Desktop;
import java.io.File;
import java.util.List;

public class Controller {

    private final View view;
    private final Model model;

    public Controller(View view, Model model) {
        this.view = view;
        this.model = model;
        view.setController(this);
        view.setModel(model);
        model.setList(Service.instance().listarFuncionarios());
    }

    public void guardar(Funcionario funcionario) throws Exception {
        boolean esNuevo = Service.instance().buscarFuncionarioPorId(funcionario.getId()) == null;
        if (esNuevo) {
            Service.instance().crearFuncionario(funcionario);
        } else {
            Service.instance().modificarFuncionario(funcionario);
        }
        model.setCurrent(new Funcionario());
        model.setList(Service.instance().listarFuncionarios());
    }

    public void consultar(String id) throws Exception {
        Funcionario encontrado = Service.instance().buscarFuncionarioPorId(id);
        if (encontrado == null) {
            throw new Exception("Funcionario no existe");
        }
        model.setCurrent(encontrado);
    }

    public void edit(int row) {
        model.setCurrent(model.getList().get(row));
    }

    public void borrar(String id) throws Exception {
        Service.instance().eliminarFuncionario(id);
        model.setCurrent(new Funcionario());
        model.setList(Service.instance().listarFuncionarios());
    }

    public void clear() {
        model.setCurrent(new Funcionario());
    }

    public void search(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            model.setList(Service.instance().listarFuncionarios());
        } else {
            model.setList(Service.instance().buscarFuncionariosPorNombre(nombre));
        }
    }

    public void searchById(String id) {
        Funcionario encontrado = Service.instance().buscarFuncionarioPorId(id);
        model.setList(encontrado != null ? java.util.List.of(encontrado) : java.util.List.of());
    }

    public void print() throws Exception {
        String dest = "funcionarios.pdf";
        PdfFont font = PdfFontFactory.createFont(StandardFonts.HELVETICA);
        PdfWriter writer = new PdfWriter(dest);
        PdfDocument pdf = new PdfDocument(writer);

        Document document = new Document(pdf);
        document.setMargins(20, 20, 20, 20);

        Table header = new Table(1);
        header.setWidth(400);
        header.setHorizontalAlignment(HorizontalAlignment.CENTER);
        header.addCell(getCell(new Paragraph("Listado de Funcionarios").setFont(font).setBold().setFontSize(16), TextAlignment.CENTER, false));
        document.add(header);

        List<Funcionario> funcionarios = model.getList();

        Table table = new Table(3);
        table.setWidth(500);
        table.setHorizontalAlignment(HorizontalAlignment.CENTER);

        table.addHeaderCell(getCell(new Paragraph("Id").setFont(font).setBold(), TextAlignment.CENTER, true));
        table.addHeaderCell(getCell(new Paragraph("Nombre").setFont(font).setBold(), TextAlignment.CENTER, true));
        table.addHeaderCell(getCell(new Paragraph("Telefono").setFont(font).setBold(), TextAlignment.CENTER, true));

        for (Funcionario f : funcionarios) {
            table.addCell(getCell(new Paragraph(f.getId()).setFont(font), TextAlignment.LEFT, true));
            table.addCell(getCell(new Paragraph(f.getNombre()).setFont(font), TextAlignment.LEFT, true));
            table.addCell(getCell(new Paragraph(f.getTelefono()).setFont(font), TextAlignment.LEFT, true));
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