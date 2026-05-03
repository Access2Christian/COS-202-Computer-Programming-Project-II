package gui;

import java.awt.*;
import java.awt.geom.*;
import javax.swing.*;

/** Panel with a subtle gradient background */
public class GradientPanel extends JPanel {
    private Color c1, c2;
    private boolean horizontal;

    public GradientPanel(Color c1, Color c2, boolean horizontal) {
        this.c1 = c1; this.c2 = c2; this.horizontal = horizontal;
        setOpaque(false);
    }

    public GradientPanel() { this(UITheme.BG_PRIMARY, UITheme.BG_SECONDARY, false); }
    public GradientPanel(Color c1, Color c2) { this(c1, c2, false); }

    @Override protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        GradientPaint gp;
        if (horizontal) gp = new GradientPaint(0, 0, c1, getWidth(), 0, c2);
        else            gp = new GradientPaint(0, 0, c1, 0, getHeight(), c2);
        g2.setPaint(gp);
        g2.fillRect(0, 0, getWidth(), getHeight());
        g2.dispose();
        super.paintComponent(g);
    }
}
