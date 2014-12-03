/**
 * Copyright (C) 2013, 2014 Johannes Taelman
 *
 * This file is part of Axoloti.
 *
 * Axoloti is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * Axoloti is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Axoloti. If not, see <http://www.gnu.org/licenses/>.
 */
package components.control;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

/**
 *
 * @author Johannes Taelman
 */
public class Checkbox4StatesComponent extends ACtrlComponent {

    private double value;
    private int n;
    private final int bsize = 12;
    private int selIndex = -1;

    public Checkbox4StatesComponent(int value, int n) {
        //setInheritsPopupMenu(true);
        this.value = 0;//value;
        this.n = n;
        SetupTransferHandler();
    }

    private boolean dragAction = false;
    private int dragValue;

    @Override
    protected void mouseDragged(MouseEvent e) {
        if (dragAction) {
            int i = e.getX() / bsize;
            if ((i >= 0) && (i < n)) {
                selIndex = i;
                SetFieldValue(i, dragValue);
            }
        }
    }

    private void SetFieldValue(int position, int val) {
        int mask = 3 << (position * 2);
        int v = (int) value;
        v = (v & ~mask) + (val << (position * 2));
        setValue((double) v);
    }

    private int GetFieldValue(int position) {
        return (((int) value) >> (position * 2)) & 3;
    }

    @Override
    protected void mousePressed(MouseEvent e) {
        grabFocus();
        if (e.getButton() == 1) {
            int i = e.getX() / bsize;
            if ((i >= 0) && (i < n)) {
                if (e.isShiftDown()) {
                    dragValue = GetFieldValue(i);
                } else {
                    dragValue = (GetFieldValue(i) + 1) & 3;
                }
                SetFieldValue(i, dragValue);
                selIndex = i;
                dragAction = true;
            }
        }
        System.out.println("cb" + value);
    }

    @Override
    protected void mouseReleased(MouseEvent e) {
        dragAction = false;
    }

    @Override
    public void keyPressed(KeyEvent ke) {
        if (ke.isAltDown() || ke.isAltGraphDown() || ke.isControlDown() || ke.isMetaDown()) {
            return;
        }
        switch (ke.getKeyCode()) {
            case KeyEvent.VK_LEFT: {
                selIndex = selIndex - 1;
                if (selIndex < 0) {
                    selIndex = n - 1;
                }
                repaint();
                ke.consume();
                return;
            }
            case KeyEvent.VK_RIGHT: {
                selIndex = selIndex + 1;
                if (selIndex >= n) {
                    selIndex = 0;
                }
                repaint();
                ke.consume();
                return;
            }
            case KeyEvent.VK_UP: {
                int v = GetFieldValue(selIndex) + 1;
                if (v > 3) {
                    v = 3;
                }
                SetFieldValue(selIndex, v);
                ke.consume();
                return;
            }
            case KeyEvent.VK_DOWN: {
                int v = GetFieldValue(selIndex) - 1;
                if (v < 0) {
                    v = 0;
                }
                SetFieldValue(selIndex, v);
                ke.consume();
                return;
            }
            case KeyEvent.VK_PAGE_UP: {
                SetFieldValue(selIndex, 3);
                ke.consume();
                return;
            }
            case KeyEvent.VK_PAGE_DOWN: {
                SetFieldValue(selIndex, 0);
                ke.consume();
                return;
            }
        }
        switch (ke.getKeyChar()) {
            case '0':
                SetFieldValue(selIndex, 0);
                ke.consume();
                return;
            case '1':
                SetFieldValue(selIndex, 1);
                ke.consume();
                return;
            case '2':
                SetFieldValue(selIndex, 2);
                ke.consume();
                return;
            case '3':
                SetFieldValue(selIndex, 3);
                ke.consume();
                return;
            case ' ':
                SetFieldValue(selIndex, (GetFieldValue(selIndex) + 1) & 3);
                ke.consume();
                return;
        }
    }

    private static final Stroke strokeThin = new BasicStroke(1);
    private static final Stroke strokeThick = new BasicStroke(2);

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        if (isEnabled()) {
            g2.setColor(Color.white);
        } else {
            g2.setColor(getBackground());
        }
        g2.fillRect(0, 0, bsize * n, bsize + 1);
        g2.setPaint(getForeground());

        if (isFocusOwner()) {
            g2.setStroke(strokeThick);
            g2.drawRect(0, 0, bsize * n + 0, bsize + 1);
            g2.drawRect(selIndex * bsize, 0, bsize, bsize + 1);
        } else {
            g2.drawRect(0, 0, bsize * n, bsize + 1);
        }

        g2.setStroke(strokeThin);
        for (int i = 1; i < n; i++) {
            g2.drawLine(bsize * i, 0, bsize * i, bsize + 1);
        }

        if (isEnabled()) {
            int v = (int) value;
            int inset = 2;
            for (int i = 0; i < n; i++) {
                switch (v & 3) {
                    case 0:
                        g2.setColor(Color.white);
                        break;
                    case 1:
                        g2.setColor(Color.getHSBColor(0.0f, 0.0f, 0.66f));
                        break;
                    case 2:
                        g2.setColor(Color.getHSBColor(0.0f, 0.0f, 0.33f));
                        break;
                    case 3:
                        g2.setColor(Color.black);
                        break;
                }
                g2.fillRect(i * bsize + inset, inset, bsize - inset - 1, bsize - inset);
                v = v >> 2;
            }
        }
    }

    @Override
    public Dimension getMinimumSize() {
        return new Dimension(bsize * n + 2, bsize + 2);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(bsize * n + 2, bsize + 2);
    }

    @Override
    public Dimension getMaximumSize() {
        return new Dimension(bsize * n + 2, bsize + 2);
    }

    @Override
    public void setValue(double value) {
        if (this.value != value) {
            this.value = value;
            repaint();
        }
        fireEvent();
    }

    @Override
    public double getValue() {
        return value;
    }

    @Override
    void keyReleased(KeyEvent ke) {
    }
}