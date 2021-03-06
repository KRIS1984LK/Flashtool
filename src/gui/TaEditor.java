package gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import at.HexLib.library.HexLib;

import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.forms.factories.FormFactory;

import flashsystem.BytesUtil;
import flashsystem.HexDump;
import flashsystem.TaEntry;
import flashsystem.X10flash;

import javax.swing.JComboBox;
import javax.swing.JTextPane;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JLabel;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import java.awt.Dialog.ModalityType;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import org.logger.MyLogger;
import org.system.OS;

import java.awt.Color;
import java.awt.Component;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;

public class TaEditor extends JDialog {

	private final JPanel contentPanel = new JPanel();
	HashMap content = new HashMap();
	TaEntry ta = null;
	private JTable tablePartition;
	private DefaultTableModel modelPartition;
	private HexLib hex = new HexLib();
	private JPopupMenu popupMenu = new JPopupMenu();
	private X10flash _flash;

	class PopupListener extends MouseAdapter {
	    @Override
	    public void mousePressed(MouseEvent e) {
	        showPopup(e);
	    }
	    @Override
	    public void mouseReleased(MouseEvent e) {
	        showPopup(e);
	    }
	    private void showPopup(MouseEvent e) {
	        if (e.isPopupTrigger()) {
	            popupMenu.show(e.getComponent(), e.getX(), e.getY());
	        }
	    }
	}
	/**
	 * Create the dialog.
	 */
	public TaEditor(X10flash flash, Vector<TaEntry> v) {
		_flash = flash;
		setModalityType(ModalityType.APPLICATION_MODAL);
		setTitle("TA Editor");
		setBounds(100, 100, 715, 450);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("max(63dlu;min)"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,},
			new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default:grow"),}));
		{
			JScrollPane scrollPane = new JScrollPane();
			contentPanel.add(scrollPane, "2, 2, 1, 7, fill, fill");
			{
				tablePartition = new JTable() {
				    /**
					 * 
					 */
					private static final long serialVersionUID = 1L;

					public boolean isCellEditable(int rowIndex, int vColIndex) {
				        return false;
				    }
				};
				tablePartition.addKeyListener(new KeyAdapter() {
					@Override
					public void keyReleased(KeyEvent arg0) {
						String result=(String)modelPartition.getValueAt(tablePartition.getSelectedRow(), 0);
						try {
							if (ta!=null)
								ta.setData(hex.getByteContent());
							ta = (TaEntry)content.get(result);
							hex.setByteContent(ta.getDataString().getBytes());
						}
						catch (Exception e) {}
					}
				});
				tablePartition.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent arg0) {
						String result=(String)modelPartition.getValueAt(tablePartition.getSelectedRow(), 0);
						try {
							if (ta!=null)
								ta.setData(hex.getByteContent());
							ta = (TaEntry)content.get(result);
							hex.setByteContent(ta.getDataString().getBytes());
						}
						catch (Exception e) {}
					}
				});
				JMenuItem mntmResize = new JMenuItem("Resize Unit");
				mntmResize.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						ResizeSelectGUI resize = new ResizeSelectGUI();
						String newsize=resize.getUnitSize();
						if (newsize.length()>0) {
							ta.resize(Integer.parseInt(newsize));
							hex.setByteContent(ta.getDataString().getBytes());
						}
					}
				});
				popupMenu.add(mntmResize);
				JMenuItem mntmLoad = new JMenuItem("Load from file");
				mntmLoad.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						TaSelectGUI tasel = new TaSelectGUI();
						String result = tasel.getTa();
						if (result.length()>0) {
							String path = OS.getWorkDir()+"/custom/ta/"+result;
							try {
								byte[] array = BytesUtil.getBytesFromFile(new File(path));
								ta.setData(array);
								hex.setByteContent(array);
							}
							catch (Exception e) {
								MyLogger.getLogger().error(e.getMessage());
							}
						}
					}
				});
				popupMenu.add(mntmLoad);
				JMenuItem mntmWriteFile = new JMenuItem("Write to file");
				mntmWriteFile.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						String path = OS.getWorkDir()+"/custom/ta/"+_flash.getPhoneProperty("MSN")+"_"+(String)modelPartition.getValueAt(tablePartition.getSelectedRow(), 0)+".bin";
						File f = new File(path);
						try {
							FileOutputStream fos = new FileOutputStream(f);
							fos.write(hex.getByteContent());
							fos.flush();
							fos.close();
							AskBox.showOKbox("Unit saved to \n"+path);
						}
						catch (Exception e) {
						}
					}
				});
				popupMenu.add(mntmWriteFile);
				JMenuItem mntmWrite = new JMenuItem("Write to phone");
				mntmWrite.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						try {
							_flash.setFlashState(true);
							_flash.sendTAUnit(ta);
							_flash.setFlashState(false);
						}
						catch (Exception e) {
							MyLogger.getLogger().error(e.getMessage());
						}
					}
				});
				popupMenu.add(mntmWrite);
				MouseListener popupListener = new PopupListener();
				tablePartition.addMouseListener(popupListener);
				tablePartition.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
				scrollPane.setViewportView(tablePartition);
			}
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton cancelButton = new JButton("Close");
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						dispose();
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
		Enumeration<TaEntry> e = v.elements();
		modelPartition = new DefaultTableModel();
		modelPartition.addColumn("Unit");
		tablePartition.setModel(modelPartition);
		contentPanel.add(hex, "4, 2, 1, 7");
		hex.setColorBorderBackGround(Color.LIGHT_GRAY);
		while (e.hasMoreElements()) {
			TaEntry ta = e.nextElement();
			content.put(ta.getPartition(), ta);
			modelPartition.addRow(new String[]{ta.getPartition()});
		}		
	}

	private static void addPopup(Component component, final JPopupMenu popup) {
		component.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}
			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}
			private void showMenu(MouseEvent e) {
				popup.show(e.getComponent(), e.getX(), e.getY());
			}
		});
	}
}
