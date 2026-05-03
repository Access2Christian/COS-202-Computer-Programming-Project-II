package gui;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import javax.swing.*;

public class StyledButton extends JButton {

    public enum Style { PRIMARY, SUCCESS, DANGER, WARNING, GHOST, GOLD }

    private Color baseColor;
    private Color hoverColor;
    private Color pressColor;
    private Color textColor;
    private float hoverAlpha = 0f;
    private boolean hovered = false;
    private boolean pressed = false;
    private Timer hoverTimer;

    public StyledButton(String text, Style style) {
        super(text);
        applyStyle(style);
        setUpPainting();
        setUpHover();
    }

    public StyledButton(String text) {
        this(text, Style.PRIMARY);
    }

    private void applyStyle(Style style) {
        switch (style) {
            case SUCCESS:
                baseColor  = new Color(30, 180, 90);
                hoverColor = new Color(40, 205, 110);
                pressColor = new Color(20, 150, 70);
                textColor  = Color.WHITE;
                break;
            case DANGER:
                baseColor  = new Color(220, 55, 60);
                hoverColor = new Color(245, 70, 75);
                pressColor = new Color(180, 40, 45);
                textColor  = Color.WHITE;
                break;
            case WARNING:
                baseColor  = new Color(220, 155, 20);
                hoverColor = new Color(245, 175, 30);
                pressColor = new Color(185, 130, 15);
                textColor  = new Color(20, 10, 5);
                break;
            case GHOST:
                baseColor  = new Color(35, 55, 90);
                hoverColor = new Color(50, 75, 120);
                pressColor = new Color(25, 40, 70);
                textColor  = UITheme.TEXT_PRIMARY;
                break;
            case GOLD:
                baseColor  = new Color(200, 140, 20);
                hoverColor = new Color(225, 165, 35);
                pressColor = new Color(170, 115, 10);
                textColor  = new Color(255, 248, 220);
                break;
            default: // PRIMARY
                baseColor  = new Color(45, 120, 230);
                hoverColor = new Color(60, 142, 255);
                pressColor = new Color(35, 95, 195);
                textColor  = Color.WHITE;
        }
    }

    private void setUpPainting() {
        setOpaque(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setFont(UITheme.FONT_BUTTON);
        setForeground(textColor);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
    }

    private void setUpHover() {
        hoverTimer = new Timer(15, e -> {
            if (hovered && hoverAlpha < 1f) {
                hoverAlpha = Math.min(1f, hoverAlpha + 0.12f);
                repaint();
            } else if (!hovered && hoverAlpha > 0f) {
                hoverAlpha = Math.max(0f, hoverAlpha - 0.12f);
                repaint();
            } else {
                hoverTimer.stop();
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                hovered = true; hoverTimer.start();
            }
            @Override public void mouseExited(MouseEvent e) {
                hovered = false; pressed = false; hoverTimer.start();
            }
            @Override public void mousePressed(MouseEvent e) {
                pressed = true; repaint();
            }
            @Override public void mouseReleased(MouseEvent e) {
                pressed = false; repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int w = getWidth(), h = getHeight();

        // Determine current color
        Color current = pressed ? pressColor :
                interpolate(baseColor, hoverColor, hoverAlpha);

        // Shadow
        if (!pressed) {
            g2.setColor(new Color(0, 0, 0, 60));
            g2.fillRoundRect(1, 3, w - 2, h - 2, 10, 10);
        }

        // Button background
        g2.setColor(current);
        g2.fillRoundRect(0, pressed ? 1 : 0, w - 1, h - (pressed ? 2 : 1), 10, 10);

        // Top highlight
        if (!pressed) {
            GradientPaint shine = new GradientPaint(0, 0, new Color(255,255,255,25), 0, h/2, new Color(255,255,255,0));
            g2.setPaint(shine);
            g2.fillRoundRect(1, 1, w - 2, h / 2, 10, 10);
        }

        // Enabled state check
        if (!isEnabled()) {
            g2.setColor(new Color(0, 0, 0, 80));
            g2.fillRoundRect(0, 0, w, h, 10, 10);
        }

        g2.dispose();
        super.paintComponent(g);
    }

    private Color interpolate(Color a, Color b, float t) {
        int r = (int)(a.getRed()   + (b.getRed()   - a.getRed())   * t);
        int g = (int)(a.getGreen() + (b.getGreen() - a.getGreen()) * t);
        int bv= (int)(a.getBlue()  + (b.getBlue()  - a.getBlue())  * t);
        return new Color(r, g, bv);
    }

    @Override
    public Dimension getPreferredSize() {
        Dimension d = super.getPreferredSize();
        return new Dimension(Math.max(d.width, 80), Math.max(d.height, 34));
    }
}
