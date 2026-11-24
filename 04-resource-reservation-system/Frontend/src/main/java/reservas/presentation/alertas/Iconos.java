package reservas.presentation.alertas;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;

public final class Iconos {

    private static final Icon VER = crearVer();
    private static final Icon DESCARTAR = crearDescartar();

    private Iconos() {
    }

    public static Icon ver() {
        return VER;
    }

    public static Icon descartar() {
        return DESCARTAR;
    }

    private static Icon crearVer() {
        BufferedImage imagen = new BufferedImage(20, 20, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = imagen.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g.setColor(new Color(0xD9534F));
        g.fillRoundRect(2, 3, 12, 6, 2, 2);
        g.setColor(new Color(0x1F3A5F));
        g.drawRoundRect(2, 3, 12, 6, 2, 2);

        Path2D frente = new Path2D.Float();
        frente.moveTo(2, 8);
        frente.lineTo(8, 8);
        frente.lineTo(10, 10);
        frente.lineTo(18, 10);
        frente.lineTo(16, 17);
        frente.lineTo(3, 17);
        frente.closePath();
        g.setColor(new Color(0x62B4E6));
        g.fill(frente);
        g.setColor(new Color(0x1F3A5F));
        g.draw(frente);
        g.dispose();
        return new ImageIcon(imagen);
    }

    private static Icon crearDescartar() {
        BufferedImage imagen = new BufferedImage(20, 20, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = imagen.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g.setColor(new Color(0xE8453C));
        g.fillOval(1, 1, 18, 18);
        g.setColor(Color.WHITE);
        g.fillRoundRect(5, 9, 10, 2, 1, 1);
        g.dispose();
        return new ImageIcon(imagen);
    }
}
