package start;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.TimeUnit;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.text.DefaultCaret;

import util.ListItem;
import util.SynchronObject;



public class BackupWindow extends JFrame implements ActionListener, ItemListener, WindowListener
	{
		
		private JComboBox<ListItem> cboZeit;
		private DefaultComboBoxModel<ListItem> cboZeitModel;
		private JTextField anzeige1, anzeige2;
		private JButton btnAnzeige1, btnAnzeige2, btnCopy, btnSync, btnAbbruch;
		private JProgressBar progressBar;
		private JPanel mainPanel , anzeigePanel, syncPanel, logPanel;
		private JCheckBox aktivateTimer;
		private Path anz1Ordner, anz2Ordner, standartBackupOrdner = Paths.get("SynchrosBackup");
		private Thread s, timer;
		private boolean abbruch = false;
		private JComboBox<ListItem> cbo;
		private SynchronObject syncObj;
		private JTextArea textArea;
		private JScrollPane textAreaScroller;
		
		public BackupWindow()
		{
			initializeComps();
			if (!Files.exists(standartBackupOrdner))
				{
				try
					{
						Files.createDirectory(standartBackupOrdner);
					} catch (IOException e)
					{
						
						e.printStackTrace();
					}	
				}
			anz2Ordner = standartBackupOrdner;	
			
			
		}
		private void initializeComps()
		{
			this.setTitle("Synchro - Sync/Backupmanager");
			this.setBounds(0, 0, 550, 420);
			this.setResizable(true);
			this.setLayout(null);
			this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
			this.addWindowListener(this);
			
			mainPanel = new JPanel();
			mainPanel.setBounds(0, 0 , this.getWidth(), 25);
			// fcPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
			mainPanel.setLayout(null);
			
			btnCopy = new JButton("Copy");
			btnCopy.setBounds(0, 0, this.getWidth()/2, mainPanel.getHeight());
			btnCopy.addActionListener(this);
			mainPanel.add(btnCopy);
			
			this.add(mainPanel);
			
			anzeigePanel = new JPanel();
			anzeigePanel.setBounds(0, mainPanel.getHeight()+ 30 , this.getWidth(), 160);
			// fcPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
			anzeigePanel.setLayout(null);
			
			aktivateTimer = new JCheckBox("Aktiviere Zeitsteuerung");
			aktivateTimer.setBounds(10, 0, 200, 25);
			aktivateTimer.addItemListener(this);
			anzeigePanel.add(aktivateTimer);
			
			cboZeitModel = new DefaultComboBoxModel<>();
			cboZeit = new JComboBox<>(cboZeitModel);
			cboZeit.setBounds(370, 0, 150, 25);
			// cboZeit.addItemListener(this);
			anzeigePanel.add(cboZeit);
			
			anzeige1 = new JTextField();
			anzeige1.setBounds(10, 60, 350, 35);
			anzeige1.setText("Bitte den zu sichernden Ordner wählen");
			//anzeige1.setFont(new Font("Fondor", 1, 12));
			anzeige1.setFocusable(false);
			anzeigePanel.add(anzeige1);
			
			btnAnzeige1 = new JButton("Ordner Auswählen");
			btnAnzeige1.setBounds(370, 65, 150, 25);
			btnAnzeige1.addActionListener(this);
			anzeigePanel.add(btnAnzeige1);
			
			anzeige2 = new JTextField();
			anzeige2.setText("wohin soll gesichert werden? (Standart:SynchrosBackup)");
			//anzeige2.setFont(new Font("Fondor", 1, 12));
			anzeige2.setBounds(10, 120, 350, 35);
			anzeige2.setFocusable(false);
			anzeigePanel.add(anzeige2);
					
			btnAnzeige2 = new JButton("Ordner Auswählen");
			btnAnzeige2.setBounds(370, 125, 150, 25);
			btnAnzeige2.addActionListener(this);
			anzeigePanel.add(btnAnzeige2);
			
			this.add(anzeigePanel);
			
			
			syncPanel = new JPanel();
			syncPanel.setBounds(10, anzeigePanel.getY()+ anzeigePanel.getHeight() + 20 , this.getWidth() -30, 25);
			// syncPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
			syncPanel.setLayout(new BorderLayout());

			btnSync = new JButton("Sync it!");
			btnSync.setBounds(0, 0, 150, (syncPanel.getHeight() / 3));
			btnSync.addActionListener(this);
			btnSync.setPreferredSize(new Dimension(150,(syncPanel.getHeight() / 3) ));
			btnSync.setMaximumSize(new Dimension(150, (syncPanel.getHeight() / 3) ));
			syncPanel.add(btnSync,BorderLayout.LINE_START);
			
			btnAbbruch = new JButton("Abbrechen");
			btnAbbruch.setBounds(0, 0, 150, (syncPanel.getHeight() / 3));
			btnAbbruch.addActionListener(this);
			btnAbbruch.setPreferredSize(new Dimension(150, (syncPanel.getHeight() /3)));
			btnAbbruch.setMaximumSize(new Dimension(150, (syncPanel.getHeight() /3)));
			btnAbbruch.setVisible(false);
			syncPanel.add(btnAbbruch,BorderLayout.LINE_END);
			
			progressBar = new JProgressBar(JProgressBar.HORIZONTAL);
			progressBar.setBorderPainted(true);
			progressBar.setPreferredSize(new Dimension(300,  (syncPanel.getHeight() /3) ));
			progressBar.setForeground(Color.RED);
			progressBar.setStringPainted(true);
			progressBar.setVisible(true);
			syncPanel.add(progressBar, BorderLayout.CENTER);
			this.add(syncPanel);
			
			logPanel = new JPanel();
			logPanel.setBounds(10, syncPanel.getY()+ syncPanel.getHeight() + 10 , this.getWidth() - 30, 105);
			logPanel.setLayout(new BorderLayout());
			
			textArea = new JTextArea();
			textArea.setMargin(new Insets(3, 3, 3, 3));
			textArea.setBackground(Color.black);
			textArea.setForeground(Color.LIGHT_GRAY);
			textArea.setAutoscrolls(true);
			textArea.setFocusable(false);
			textAreaScroller = new JScrollPane(textArea);
			DefaultCaret caret = (DefaultCaret)textArea.getCaret();
			caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
			textAreaScroller.setBounds(0,  0,  syncPanel.getWidth(),  50);
		
			logPanel.add(textAreaScroller);
			this.add(logPanel);
			
			
			this.add(syncPanel);
			
			
		}
		private void initFrame()
			{
				this.setLocationRelativeTo(null);
				populateCBOZeitModel();
				cboZeit.setSelectedIndex(1);
			}
		public void Show()
			{
				initFrame();
				toggleTimeCheckBox();
			}
		public void visible()
		{
			this.setVisible(true);
		}
		public void invisible()
		{
			this.setVisible(false);
		}
		private void toggleTimeCheckBox()
		{
			if (aktivateTimer.isSelected())
				cboZeit.setEnabled(true);
			else
				cboZeit.setEnabled(false);
		}

			private void populateCBOZeitModel()
			{
				cboZeitModel.addElement(new ListItem((Integer) 1, "1 Minute"));
				cboZeitModel.addElement(new ListItem((Integer) 5, "5 Minuten"));
				cboZeitModel.addElement(new ListItem((Integer) 10, "10 Minuten"));
				cboZeitModel.addElement(new ListItem((Integer) 30, "30 Minuten"));
				cboZeitModel.addElement(new ListItem((Integer) 60, "60 Minuten"));
			}
			private Path dateiAuswählen()
			{
				JFileChooser fc1 = new JFileChooser();

				fc1.setDialogTitle("SyncOrdner auswählen");
				fc1.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

				if (fc1.showSaveDialog(this) == JFileChooser.APPROVE_OPTION)
					return fc1.getSelectedFile().toPath();
				else
					return null;
			}
			
			private void textFeld1Fuellen(Path anz1Ordner)
			{
				if (anz1Ordner == null)
					return;
				this.anz1Ordner = anz1Ordner;
				anzeige1.setText(anz1Ordner.toString());
			}
			
			private void textFeld2Fuellen(Path anz2Ordner)
			{
				if (anz2Ordner == null)
					return;
				this.anz2Ordner = anz2Ordner;
				anzeige2.setText(anz2Ordner.toString());
			}
			private void showMessage(String nachricht)
				{
					JOptionPane.showMessageDialog(this, nachricht, "Hinweis", JOptionPane.INFORMATION_MESSAGE);

				}
			
	private void startSync(Path anz1Ordner, Path anz2Ordner)
	{
		s = new Thread(new syncNprogress(syncObj));
		s.start();
	}

	private void setAbbruch()
	{
		abbruch = true;
	}

	private void startTimer()
	{
		long time = ((Integer) ((ListItem) cboZeit.getSelectedItem()).getValueMember()).longValue();
		SynchronObject syncObj = new SynchronObject();

		timer = new Timer(syncObj, time);

		s = new Thread(new syncNprogress(syncObj));

		if (!abbruch)
		{
			timer.start();
			s.start();
		} else
			abbruch = false;
	}	
			
			@Override
			public void actionPerformed(ActionEvent e)
			{
				if (e.getSource() == btnCopy)
				{
					this.invisible();
					MainNtray.cwvisible();
				}
				if ( e.getSource() == btnAnzeige1)
				{
					textFeld1Fuellen(dateiAuswählen());
				}
				if ( e.getSource() == btnAnzeige2)
				{
					textFeld2Fuellen(dateiAuswählen());
				}
				if (e.getSource() == btnSync)
				{
					if (anz1Ordner != null && !aktivateTimer.isSelected())
					startSync( anz1Ordner, anz2Ordner);
					else if (anz1Ordner != null && aktivateTimer.isSelected())
						startTimer();
					else
						showMessage("Bitte den zu Sichernden Ordner auswählen");

				}
				if (e.getSource() == btnAbbruch && !aktivateTimer.isSelected())
				{
					setAbbruch();
				}
				if (e.getSource() == btnAbbruch && aktivateTimer.isSelected())
				{
					setAbbruch();
					timer.interrupt();
				}
			}
			@Override
			public void itemStateChanged(ItemEvent e)
			{
				
			
				if (e.getSource() == aktivateTimer)
					toggleTimeCheckBox();

				if (e.getSource() instanceof JComboBox)
				{

					cbo = (JComboBox<ListItem>) e.getSource();
								

					}
				}
			
	
		@Override
		public void windowOpened(WindowEvent e)
		{
			// TODO Auto-generated method stub
			
		}
		@Override
		public void windowClosing(WindowEvent e)
		{
			
			this.dispose();
			
		}
		@Override
		public void windowClosed(WindowEvent e)
		{
			MainNtray.cwdispose();
			MainNtray.removeTrayIcon();
			
		}
		@Override
		public void windowIconified(WindowEvent e)
		{
			// TODO Auto-generated method stub
			
		}
		@Override
		public void windowDeiconified(WindowEvent e)
		{
			// TODO Auto-generated method stub
			
		}
		@Override
		public void windowActivated(WindowEvent e)
		{
			// TODO Auto-generated method stub
			
		}
		@Override
		public void windowDeactivated(WindowEvent e)
		{
			// TODO Auto-generated method stub
			
		}
		 
		private class syncNprogress implements Runnable
		{
			int pbCounter= 0;
			private SynchronObject syncObj;
			public syncNprogress(SynchronObject syncObj)
			{
				this.syncObj = syncObj;
			}
			public void snpStart(Path anz1Ordner, Path anz2Ordner)
			{
			
				try
				{
					DirectoryStream<Path> qstream = Files.newDirectoryStream(anz1Ordner);
					for (Path qfile : qstream)
					{
						Path target = Paths.get(anz2Ordner.toString() + "/" + qfile.getFileName());
						if (abbruch)
							break;
						if (Files.isDirectory(qfile) && !Files.exists(target))
						{
							Files.createDirectory(target);
							snpStart(Paths.get(anz1Ordner.toString() + "/" + qfile.getFileName()), Paths.get(anz2Ordner.toString() + "/" + qfile.getFileName()) );
						}
						
						else if (Files.isDirectory(qfile) && Files.exists(target))
						{				
							snpStart(Paths.get(anz1Ordner.toString() + "/" + qfile.getFileName()), Paths.get(anz2Ordner.toString() + "/" + qfile.getFileName()) );
						}
						//Wenn die Datei noch nicht existiert
						else if (!Files.exists(target))
						{
							Files.copy(qfile, target, StandardCopyOption.REPLACE_EXISTING);
						
						}
						//Wenn Datei im Zielverzeichnis schon existiert
						else if (Files.exists(target))
						{
								if (checkAlter(Paths.get(anz1Ordner.toString() + "/" + qfile.getFileName()), Paths.get(anz2Ordner.toString() + "/" + qfile.getFileName())))
								{
								
									Files.copy(qfile, target, StandardCopyOption.REPLACE_EXISTING);
								}
							
						
						}
						pbCounter++;
						progressBar.setValue(pbCounter);
					}
					
					qstream.close();
				}
						catch (IOException e)
						{
								// TODO Auto-generated catch block
								e.printStackTrace();
						}
				
			}
			
			private int getNumberOfItems(Path anz1Ordner)
			{
				
					int retValue = 0;
				
					try
					{
						DirectoryStream<Path> qstream = Files.newDirectoryStream(anz1Ordner);
						for (Path qfile : qstream)
						{
						
							if (Files.isDirectory(qfile))
							{
								getNumberOfItems(Paths.get(anz1Ordner.toString() + "/" + qfile.getFileName()));	
							}
							retValue++;
						}
						qstream.close();
					}
					catch (IOException e)
					{
						e.printStackTrace();
					}
					
					return retValue;
			}
			
			private boolean checkAlter(Path anz1Ordner, Path anz2Ordner)
			{
				boolean retValue = false;
				try
				{
					if ((Files.getLastModifiedTime(anz1Ordner).compareTo(Files.getLastModifiedTime(anz2Ordner)) > 0))
						retValue = true;
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
				return retValue;
			}
			@Override
			public void run()
			{
				textArea.append("Beginne Synchronisation beider Ordner" + System.lineSeparator());
				abbruch = false;
				btnSync.setVisible(false);
				btnAbbruch.setVisible(true);
				progressBar.setMaximum(getNumberOfItems(anz1Ordner));
				progressBar.setValue(0);
				progressBar.setMinimum(0);
				progressBar.setVisible(true);
				snpStart(anz1Ordner,anz2Ordner);
				btnSync.setVisible(true);
				btnAbbruch.setVisible(false);
				progressBar.setValue(0);
				textArea.append("Synchronisation abgeschlossen " + System.lineSeparator());
				if (aktivateTimer.isSelected())
				syncObj.weiter();
			}
		
			
		}
		class Timer extends Thread
		{
			SynchronObject syncObj;
			long timeToWait;
			public Timer(SynchronObject syncObj, long timeToWait)
			{
				this.syncObj = syncObj;
				this.timeToWait = timeToWait;
			}
		
			@Override
			public void run()
			{
			
					
				syncObj.warte();
				textArea.append(" Warte auf nächsten Intervall " + System.lineSeparator());
				abbruch = false;
				btnSync.setVisible(false);
				btnAbbruch.setVisible(true);
				progressBar.setIndeterminate(true);
	
				try
				{
					TimeUnit.SECONDS.sleep(timeToWait * 60);
				}
				catch (InterruptedException e)
				{
					
					e.printStackTrace();
				}
			
			progressBar.setIndeterminate(false);
				btnSync.setVisible(true);
				btnAbbruch.setVisible(false);
				
				startTimer();
				
			}
		}
		
		
	}
