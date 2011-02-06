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

import javax.swing.SwingWorker;

import org.apache.commons.lang.StringUtils;

import ste.cameracontrol.CameraController;
import ste.cameracontrol.Photo;

/**
 *
 * @author ste
 */
public class CameraControlWindow extends BaseFrame {

    public static final String ICON_CAMERA_CONNECT    = "images/camera-connect-24x24.png";
    public static final String ICON_CAMERA_DISCONNECT = "images/camera-disconnect-24x24.png";

    /** Creates new form CameraControlWindow */
    public CameraControlWindow() {
        initComponents();
        setLocationRelativeTo(null);
        setStatus("");
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        statusPanel = new javax.swing.JPanel();
        connectionLabel = new javax.swing.JLabel();
        statusLabel = new javax.swing.JLabel();
        menu = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        editMenu = new javax.swing.JMenu();
        cameraMenu = new javax.swing.JMenu();
        shootMenuItem = new javax.swing.JMenuItem();
        helpMenu = new javax.swing.JMenu();
        aboutMenuItem = new javax.swing.JMenuItem();
        jMenuItem1 = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Camera connection status");
        setBackground(javax.swing.UIManager.getDefaults().getColor("window"));
        setIconImage(getImage(ICON_CAMERACONTROL));
        setMinimumSize(new java.awt.Dimension(500, 400));
        setName("connectionframe"); // NOI18N

        statusPanel.setLayout(new java.awt.BorderLayout());

        connectionLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/camera-connect-24x24.png"))); // NOI18N
        connectionLabel.setText("connection status");
        connectionLabel.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        connectionLabel.setPreferredSize(null);
        statusPanel.add(connectionLabel, java.awt.BorderLayout.LINE_START);

        statusLabel.setText("status");
        statusLabel.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        statusPanel.add(statusLabel, java.awt.BorderLayout.CENTER);

        getContentPane().add(statusPanel, java.awt.BorderLayout.PAGE_END);

        fileMenu.setMnemonic('F');
        fileMenu.setText("File");
        menu.add(fileMenu);

        editMenu.setMnemonic('E');
        editMenu.setText("Edit");
        menu.add(editMenu);

        cameraMenu.setMnemonic('C');
        cameraMenu.setText("Camera");

        shootMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.ALT_MASK));
        shootMenuItem.setMnemonic('S');
        shootMenuItem.setText("Shoot");
        shootMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                shootMenuItemActionPerformed(evt);
            }
        });
        cameraMenu.add(shootMenuItem);

        menu.add(cameraMenu);

        helpMenu.setMnemonic('H');
        helpMenu.setText("Help");

        aboutMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A, java.awt.event.InputEvent.ALT_MASK));
        aboutMenuItem.setMnemonic('A');
        aboutMenuItem.setText("About");
        aboutMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aboutMenuItemActionPerformed1(evt);
            }
        });
        helpMenu.add(aboutMenuItem);

        jMenuItem1.setText("jMenuItem1");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        helpMenu.add(jMenuItem1);

        menu.add(helpMenu);

        setJMenuBar(menu);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void shootMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_shootMenuItemActionPerformed
        setStatus("Taking picture");
        new SwingWorker<Void, Object>() {
            @Override
            public Void doInBackground() {
                setStatus("Wait...");
                Photo photos[] = null;
                try {
                    photos = CameraController.getInstance().shootAndDownload();
                } catch (Exception e) {
                    error(e.getMessage(), e);
                    return null;
                }

                for (Photo photo: photos) {
                    //
                    // TODO: remove this limitation; the current version of
                    // jrawio does not work
                    //
                    if (!photo.getName().toLowerCase().endsWith("cr2")) {
                        new ImageFrame(photo).setVisible(true);
                    }
                }
                return null;
            }

            @Override
            protected void done() {
                setStatus("");
            }
        }.execute();
    }//GEN-LAST:event_shootMenuItemActionPerformed

    private void aboutMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aboutMenuItemActionPerformed
        new AboutDialog(this, true).setVisible(true);
    }//GEN-LAST:event_aboutMenuItemActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void aboutMenuItemActionPerformed1(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aboutMenuItemActionPerformed1
        new AboutDialog(this, true).setVisible(true);
    }//GEN-LAST:event_aboutMenuItemActionPerformed1

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new CameraControlWindow().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem aboutMenuItem;
    private javax.swing.JMenu cameraMenu;
    private javax.swing.JLabel connectionLabel;
    private javax.swing.JMenu editMenu;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JMenu helpMenu;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuBar menu;
    private javax.swing.JMenuItem shootMenuItem;
    private javax.swing.JLabel statusLabel;
    private javax.swing.JPanel statusPanel;
    // End of variables declaration//GEN-END:variables


    // ---------------------------------------------------------- Public methods

    public void setConnectionStatus(String status) {
        if (status != null) {
            connectionLabel.setIcon(getIcon(ICON_CAMERA_CONNECT));
            connectionLabel.setText(status);
        } else {
            connectionLabel.setIcon(getIcon(ICON_CAMERA_DISCONNECT));
            connectionLabel.setText("Not connected");
        }
    }

    /**
     * Displays a message in the status bar.
     *
     * @param status the status message
     *
     */
    public void setStatus(final String status) {
        if (status == null) {
            statusLabel.setText("");
        }
        
        statusLabel.setText(StringUtils.abbreviateMiddle(status, "...", 50));
    }

    public void enableCameraControls() {
        shootMenuItem.setEnabled(true);
    }

    public void disableCameraControls() {
        shootMenuItem.setEnabled(false);
    }
}
