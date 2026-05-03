package gui;

import java.awt.*;
import javax.swing.*;

public class StatCard extends JPanel {
    private String label;
    private String value;
    private String icon;
    private Color accentColor;
    private JLabel valueLabel;

    public StatCard(String icon, String label, String value, Color accent) {
        this.icon = icon;
        this.label = label;
        this.value = value;
        this.accentColor = accent;

        setOpaque(false);
        setLayout(new BorderLayout(0, 6));
        setBorder(BorderFactory.createEmptyBorder(18, 20, 18, 20));
        setPreferredSize(new Dimension(170, 110));

        // Icon + label row
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        top.setOpaque(false);
        JLabel iconL = new JLabel(icon + "  " + label);
        iconL.setFont(UITheme.FONT_SMALL);
        iconL.setForeground(UITheme.TEXT_SECONDARY);
        top.add(iconL);
        add(top, BorderLayout.NORTH);

        // Value
        valueLabel = new JLabel(value);
        valueLabel.setFont(UITheme.FONT_HUGE);
        valueLabel.setForeground(UITheme.TEXT_PRIMARY);
        add(valueLabel, BorderLayout.CENTER);

        // Accent bar
        JPanel bar = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(accentColor);
                g2.fillRoundRect(0, 0, 36, 3, 3, 3);
                g2.dispose();
            }
        };
        bar.setOpaque(false);
        bar.setPreferredSize(new Dimension(36, 5));
        add(bar, BorderLayout.SOUTH);
    }

    public void setValue(String v) {
        this.value = v;
        valueLabel.setText(v);
        repaint();
    }

    @Override protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Card background
        g2.setColor(UITheme.BG_CARD);
        g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);

        // Border
        g2.setColor(UITheme.BORDER);
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);

        // Left accent bar
        g2.setColor(accentColor);
        g2.fillRoundRect(0, 20, 4, getHeight() - 40, 4, 4);

        // Subtle corner glow
        java.awt.RadialGradientPaint glow = new java.awt.RadialGradientPaint(
            getWidth(), 0, getWidth(),
            new float[]{0f, 1f},
            new Color[]{new Color(accentColor.getRed(), accentColor.getGreen(), accentColor.getBlue(), 18), new Color(0,0,0,0)}
        );
        g2.setPaint(glow);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);

        g2.dispose();
        super.paintComponent(g);
    }
}
