/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;



import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 *
 * @author as888211
 */
public final class Session {
    
    public SIDOSmain mainFrame;
    private static Session session;
    
    private Session(){
    	// Do all housekeeping for SIDOS here
    }
    
    public static Session getInstance(){
    	if(session==null){
    		session = new Session();
    	}
    	return session;
    }
    
    public void startWindow(){
    	try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(SIDOSmain.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(SIDOSmain.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(SIDOSmain.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(SIDOSmain.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                new SIDOSmain().setVisible(true);
            }
        });
    }
    
    public ActionListener getActionListener(int i){
    	switch (i) {
		case 1:
			return new NewActionListener();
		case 2:
			return new CreateAgentListener();

		default:
			break;
		}
    	return null;
    }
    
    
    class NewActionListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			
		}

	}
    
    class CreateAgentListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			
		}

	}
    
}
