public static void main(String[] args) {
    javax.swing.SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        try {
          for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
            if ("Nimbus".equals(info.getName())) {
              javax.swing.UIManager.setLookAndFeel(info.getClassName());
              break;
            }
          }
        } catch (Exception ignored) {
          // ignore Look and Feel setup failures
        }

        javax.swing.JFrame frame = new javax.swing.JFrame("Tab test");
        frame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 400);

        com.opendoorlogistics.codefromweb.DraggableTabbedPane tabbedPane = new com.opendoorlogistics.codefromweb.DraggableTabbedPane();
        tabbedPane.addTab("Tab 1", new javax.swing.JButton("Button 1"));
        tabbedPane.addTab("Tab 2", new javax.swing.JButton("Button 2"));
        tabbedPane.addTab("Tab 3", new javax.swing.JButton("Button 3"));
        tabbedPane.addTab("Tab 4", new javax.swing.JButton("Button 4"));

        frame.getContentPane().add(tabbedPane);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
      }
    });
  }