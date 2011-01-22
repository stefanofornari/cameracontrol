/*
 * cameracontrol
 * Copyright (C) 2010 Stefano Fornari
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License version 3 as published by
 * the Free Software Foundation with the addition of the following permission
 * added to Section 15 as permitted in Section 7(a): FOR ANY PART OF THE COVERED
 * WORK IN WHICH THE COPYRIGHT IS OWNED BY Stefano Fornari, Stefano Fornari
 * DISCLAIMS THE WARRANTY OF NON INFRINGEMENT OF THIRD PARTY RIGHTS.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program; if not, see http://www.gnu.org/licenses or write to
 * the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301 USA.
 */

package ste.cameracontrol.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Image;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.PrintWriter;
import java.io.StringWriter;
import javax.imageio.ImageIO;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import org.jdesktop.swingx.JXCollapsiblePane;
import org.jdesktop.swingx.JXDialog;

/**
 *
 * @author ste
 */
public class BaseFrame extends JFrame {

    public static final String ICON_CAMERACONTROL = "images/camera-control.png";
    public static final String ICON_ERROR = "images/error-48x48.png";

    public void error(final String msg, final Throwable t) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        t.printStackTrace(pw);
        JLabel icon = new JLabel();
        Icon img = UIManager.getIcon("OptionPane.errorIcon");
        if (img == null) {
            img = getIcon(CameraControlWindow.ICON_ERROR);
        }
        icon.setIcon(img);
        JLabel message = new JLabel();
        if (msg != null) {
            message.setText("<html>" + msg + "</html>");
        } else {
            message.setText("<html>" + t.getMessage() + "</html>");
        }
        message.setPreferredSize(new Dimension(200, 50));
        JTextArea text = new JTextArea(sw.toString(), 60, 80);
        text.setCaretPosition(0);
        text.setEditable(false);
        JScrollPane stext = new JScrollPane(text);
        stext.setPreferredSize(new Dimension(500, 200));
        Box content = Box.createHorizontalBox();
        JXDialog dialog = new JXDialog(content);
        dialog.setTitle("Error");
        dialog.setIconImage(getIconImage());
        //dialog.setMaximumSize(new Dimension(400, 200));
        JXCollapsiblePane cp = new JXCollapsiblePane(new BorderLayout());
        cp.setAnimated(false);
        cp.addPropertyChangeListener(new CollapseListener(dialog));
        cp.add(stext, BorderLayout.CENTER);
        // get the built-in toggle action
        Action toggleAction = cp.getActionMap().get(JXCollapsiblePane.TOGGLE_ACTION);
        // use the collapse/expand icons from the JTree UI
        toggleAction.putValue(JXCollapsiblePane.COLLAPSE_ICON, UIManager.getIcon("Tree.expandedIcon"));
        toggleAction.putValue(JXCollapsiblePane.EXPAND_ICON, UIManager.getIcon("Tree.collapsedIcon"));
        cp.setCollapsed(true);
        JButton toggle = new JButton(toggleAction);
        toggle.setText("");
        toggle.setSize(new Dimension(40, 40));
        Box messagePanel = Box.createHorizontalBox();
        messagePanel.add(message);
        messagePanel.add(toggle);
        JPanel exceptionPanel = new JPanel(new BorderLayout());
        exceptionPanel.add(messagePanel, BorderLayout.PAGE_START);
        exceptionPanel.add(cp, BorderLayout.PAGE_END);
        icon.setAlignmentY(TOP_ALIGNMENT);
        exceptionPanel.setAlignmentY(TOP_ALIGNMENT);
        content.add(icon);
        content.add(Box.createRigidArea(new Dimension(5, 5)));
        content.add(exceptionPanel);
        // Show the MODAL dialog
        dialog.setModal(true);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    protected ImageIcon getIcon(String name) {
        return new ImageIcon(ClassLoader.getSystemResource(name));
    }

    protected Image getImage(String name) {
        try {
            return ImageIO.read(ClassLoader.getSystemResource(name));
        } catch (Exception e) {
            error("", e);
        }

        return null;
    }

    private class CollapseListener implements PropertyChangeListener {
        public static final String PROPERTY_COLLAPTION_STATE = "collapsed";
        private JDialog dialog;

        public CollapseListener(JDialog dialog) {
            this.dialog = dialog;
        }

        public void propertyChange(PropertyChangeEvent e) {
            if (PROPERTY_COLLAPTION_STATE.equals(e.getPropertyName())) {
                dialog.pack();
            }
        }

    }

}
