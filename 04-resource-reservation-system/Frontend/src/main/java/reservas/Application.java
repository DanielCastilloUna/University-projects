package reservas;

import reservas.logic.IAService;
import reservas.logic.Rol;
import reservas.logic.Service;
import reservas.presentation.Sesion;

import javax.swing.*;
import java.awt.Font;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Application {

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception ex) {
        }

        Service.instance();
        doLogin();

        if (Sesion.isLoggedIn()) {
            doRun();
        }
    }

    private static void doLogin() {
        reservas.presentation.login.View loginView = new reservas.presentation.login.View();
        loginView.setTitle("Sistema de Reservas");
        loginView.setLocationRelativeTo(null);
        reservas.presentation.login.Model loginModel = new reservas.presentation.login.Model();
        new reservas.presentation.login.Controller(loginView, loginModel);
        loginView.setVisible(true);
    }

    private static void doRun() {
        JFrame mainView = new JFrame();
        JTabbedPane tabbedPane = new JTabbedPane();

        // Area de Alertas: a la derecha, fuera de las pestanas, siempre visible
        reservas.presentation.alertas.View alertasView = new reservas.presentation.alertas.View();
        reservas.presentation.alertas.Model alertasModel = new reservas.presentation.alertas.Model();
        reservas.presentation.alertas.Controller alertasController =
                new reservas.presentation.alertas.Controller(alertasView, alertasModel);

        JPanel contenido = new JPanel(new java.awt.BorderLayout());
        contenido.add(tabbedPane, java.awt.BorderLayout.CENTER);
        contenido.add(alertasView.getPanel(), java.awt.BorderLayout.EAST);
        mainView.setContentPane(contenido);

        try {
           alertasController.iniciar();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "No se pudo abrir el canal de alertas: " + ex.getMessage(),
                    "Alertas", JOptionPane.WARNING_MESSAGE);
        }
        mainView.setTitle("SISTEMA DE RESERVAS - " + Sesion.getUsuario().getId()
                + " (" + Sesion.getUsuario().getRol().name() + ")");
        String rolTitulo = Sesion.getUsuario().getRol() == Rol.FUNCIONARIO
                ? "Funcionario" : "Administrador";
        String usuarioTitulo = Sesion.getUsuario().getRol() == Rol.FUNCIONARIO
                ? ((reservas.logic.Funcionario) Sesion.getUsuario()).getNombre()
                : Sesion.getUsuario().getId();
        javax.swing.border.TitledBorder bordeOpciones = BorderFactory.createTitledBorder(
                tabbedPane.getBorder(), "Opciones: " + rolTitulo + " - " + usuarioTitulo);
        bordeOpciones.setTitleFont(bordeOpciones.getTitleFont().deriveFont(Font.BOLD));
        tabbedPane.setBorder(bordeOpciones);

        java.util.List<Runnable> alCambiarPestana = new java.util.ArrayList<>();

        if (Sesion.getUsuario().getRol() == Rol.ADMINISTRADOR) {
            reservas.presentation.funcionarios.View funcionarioView = new reservas.presentation.funcionarios.View();
            reservas.presentation.funcionarios.Model funcionarioModel = new reservas.presentation.funcionarios.Model();
            new reservas.presentation.funcionarios.Controller(funcionarioView, funcionarioModel);
            tabbedPane.addTab("👥 Funcionarios", funcionarioView.getPanel());

            reservas.presentation.categoriarecurso.View categoriaView = new reservas.presentation.categoriarecurso.View();
            reservas.presentation.categoriarecurso.Model categoriaModel = new reservas.presentation.categoriarecurso.Model();
            new reservas.presentation.categoriarecurso.Controller(categoriaView, categoriaModel);
            tabbedPane.addTab("📁 Categorías", categoriaView.getPanel());

            reservas.presentation.recurso.View recursoView = new reservas.presentation.recurso.View();
            reservas.presentation.recurso.Model recursoModel = new reservas.presentation.recurso.Model();
            reservas.presentation.recurso.Controller recursoController =
                    new reservas.presentation.recurso.Controller(recursoView, recursoModel);
            tabbedPane.addTab("💻 Recursos", recursoView.getPanel());
            alCambiarPestana.add(() -> {
                if (tabbedPane.getSelectedComponent() == recursoView.getPanel()) {
                    recursoController.refrescarCategorias();
                }
            });

            reservas.presentation.calendarizacion.View calendarizacionViewAdmin = new reservas.presentation.calendarizacion.View();
            reservas.presentation.calendarizacion.Model calendarizacionModelAdmin = new reservas.presentation.calendarizacion.Model();
            new reservas.presentation.calendarizacion.Controller(calendarizacionViewAdmin, calendarizacionModelAdmin);
            tabbedPane.addTab("📅 Calendarización", calendarizacionViewAdmin.getPanel());
            alCambiarPestana.add(() -> {
                if (tabbedPane.getSelectedComponent() == calendarizacionViewAdmin.getPanel()) {
                    calendarizacionViewAdmin.refrescar();
                }
            });

            reservas.presentation.actividad.View actividadViewAdmin = new reservas.presentation.actividad.View();
            reservas.presentation.actividad.Model actividadModelAdmin = new reservas.presentation.actividad.Model();
            new reservas.presentation.actividad.Controller(actividadViewAdmin, actividadModelAdmin);
            tabbedPane.addTab("🗓️ Actividades", actividadViewAdmin.getPanel());
            alCambiarPestana.add(() -> {
                if (tabbedPane.getSelectedComponent() == actividadViewAdmin.getPanel()) {
                    actividadViewAdmin.refrescar();
                }
            });
        }

        if (Sesion.getUsuario().getRol() == Rol.FUNCIONARIO) {
            IAService iaService = new IAService();
            reservas.presentation.reserva.View reservaView = new reservas.presentation.reserva.View();
            reservas.presentation.reserva.Model reservaModel = new reservas.presentation.reserva.Model();
            reservas.presentation.reserva.Controller reservaController =
                    new reservas.presentation.reserva.Controller(reservaView, reservaModel, iaService);
            reservaView.setFuncionarioLogueado((reservas.logic.Funcionario) Sesion.getUsuario());
            tabbedPane.addTab("📝 Reservas", reservaView.getPanel());
            alCambiarPestana.add(() -> {
                if (tabbedPane.getSelectedComponent() == reservaView.getPanel()) {
                    reservaController.refrescarCategorias();
                }
            });

            reservas.presentation.calendarizacion.View calendarizacionView = new reservas.presentation.calendarizacion.View();
            reservas.presentation.calendarizacion.Model calendarizacionModel = new reservas.presentation.calendarizacion.Model();
            new reservas.presentation.calendarizacion.Controller(calendarizacionView, calendarizacionModel);
            tabbedPane.addTab("📅 Calendarización", calendarizacionView.getPanel());
            alCambiarPestana.add(() -> {
                if (tabbedPane.getSelectedComponent() == calendarizacionView.getPanel()) {
                    calendarizacionView.refrescar();
                }
            });

            reservas.presentation.actividad.View actividadView = new reservas.presentation.actividad.View();
            reservas.presentation.actividad.Model actividadModel = new reservas.presentation.actividad.Model();
            new reservas.presentation.actividad.Controller(actividadView, actividadModel);
            tabbedPane.addTab("🗓️ Actividades", actividadView.getPanel());
            alCambiarPestana.add(() -> {
                if (tabbedPane.getSelectedComponent() == actividadView.getPanel()) {
                    actividadView.refrescar();
                }
            });

            // Al crear una reserva, saltar de inmediato a esa fecha en Actividades y Calendarización
            reservaView.setOnReservaCreada(creada -> {
                actividadView.mostrarFecha(creada.getFecha());
                reservas.logic.CategoriaRecurso categoriaSugerida = creada.getRecursosAsignados() != null && !creada.getRecursosAsignados().isEmpty()
                        ? creada.getRecursosAsignados().get(0).getCategoria()
                        : null;
                calendarizacionView.mostrarFecha(creada.getFecha(), categoriaSugerida);
            });
        }

        reservas.presentation.estadistica.View estadisticaView = new reservas.presentation.estadistica.View();
        reservas.presentation.estadistica.Model estadisticaModel = new reservas.presentation.estadistica.Model();
        new reservas.presentation.estadistica.Controller(estadisticaView, estadisticaModel);
        tabbedPane.addTab("📊 Estadísticas", estadisticaView.getPanel());

        tabbedPane.addChangeListener(e -> alCambiarPestana.forEach(Runnable::run));

        mainView.setSize(1170, 650);
        mainView.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainView.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                alertasController.stop();
                Service.instance().stop();
            }
        });
        mainView.setLocationRelativeTo(null);
        mainView.setVisible(true);
    }

    public static final java.awt.Color BACKGROUND_ERROR = new java.awt.Color(255, 102, 102);
}