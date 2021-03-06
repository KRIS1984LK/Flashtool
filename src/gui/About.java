package gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import org.lang.Language;
import org.system.OS;

public class About extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	public static String build = About.class.getPackage().getImplementationVersion();


	public static String getVersion() {
		if (build==null) build="version : run from eclipse";
		return build;
	}
	
	/**
	 * Create the dialog.
	 */
	public About() {
		setTitle("About");
		setName("About");
		setModalityType(ModalityType.APPLICATION_MODAL);
		setModal(true);
		setResizable(false);
		setAlwaysOnTop(true);
		setBounds(100, 100, 374, 217);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		{
			JLabel lblName = new JLabel("X10 Flashing Tool");
			lblName.setName(getName()+"_msg1");
			lblName.setHorizontalAlignment(SwingConstants.CENTER);
			lblName.setBounds(10, 11, 348, 14);
			contentPanel.add(lblName);
		}
		{
			JLabel lblVersion = new JLabel(build);
			lblVersion.setHorizontalAlignment(SwingConstants.CENTER);
			lblVersion.setBounds(10, 36, 348, 14);
			contentPanel.add(lblVersion);
		}
		{
			
			JLabel lblJavaVersion = new JLabel("Java Version " + System.getProperty("java.version") + " " + System.getProperty("sun.arch.data.model") + "bits Edition");
			lblJavaVersion.setHorizontalAlignment(SwingConstants.CENTER);
			lblJavaVersion.setBounds(10, 61, 348, 14);
			contentPanel.add(lblJavaVersion);
		}
		{
			JLabel lblFrom = new JLabel("By Bin4ry & Androxyde");
			lblFrom.setName(getName()+"_msg2");
			lblFrom.setHorizontalAlignment(SwingConstants.CENTER);
			lblFrom.setBounds(10, 111, 348, 14);
			contentPanel.add(lblFrom);
		}
		JLabel lblOs = new JLabel("OS Version "+OS.getVersion());
		lblOs.setHorizontalAlignment(SwingConstants.CENTER);
		lblOs.setBounds(10, 86, 348, 14);
		contentPanel.add(lblOs);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						dispose();
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
		}
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setLanguage();
	}
	
	public void setLanguage() {
		Language.translate(this);
	}

}
