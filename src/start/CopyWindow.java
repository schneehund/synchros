package start;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.DefaultCaret;
import util.ListItem;


/**
 *
 * @author micha
 */
public class CopyWindow extends JFrame implements ActionListener, ListSelectionListener, WindowListener, ItemListener, ChangeListener
{
	private boolean cUeSchr, cAUeSchr, abbruch;
	private JButton btnQAuswahl, btnZAuswahl, btnQEntfernen, btnZEntfernen, btnSync, btnAbbruch, btnBackup;
	private JList<ListItem> quellJList, zielJList;
	private DefaultListModel<ListItem> quellListModel, zielListModel;
	private JScrollPane listBoxScroller, listBoxScroller2, textAreaScroller;
	private Path saveName = Paths.get("Synchros.conf"), neuesLaufwerk;
	private JLabel quellLabel, zielLabel;
	private JRadioButton nUebSchr, aUeSchr, ueSchr;
	private JPanel optionPanel, fcPanel, syncPanel, logPanel, mainPanel;
	private JProgressBar progressBar;
	private Thread u, t;
	private JTextArea textArea;


	/**
     *
     */
	public CopyWindow()
		{
			initializeComponents();

		}

	private void initializeComponents()
	{
		this.setTitle("Synchro - Kopierassistent");
		this.setBounds(0, 0, 550, 600);

		this.setResizable(true);
		this.setLayout(null);
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.addWindowListener(this);

		
		mainPanel = new JPanel();
		mainPanel.setBounds(0, 0 , this.getWidth() - 20, 25);
		// fcPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		mainPanel.setLayout(null);
		
		btnBackup = new JButton("Backup");
		btnBackup.setBounds(this.getWidth()/2, 0, this.getWidth()/2 -20, mainPanel.getHeight());
		btnBackup.addActionListener(this);
		mainPanel.add(btnBackup);
		
		this.add(mainPanel);
		
		fcPanel = new JPanel();
		fcPanel.setBounds(10, 40 , this.getWidth(), 260);
		// fcPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		fcPanel.setLayout(null);

		quellLabel = new JLabel("Bitte Quellverzeichnis auswählen");
		quellLabel.setBounds(10, 5, 320, 20);
		fcPanel.add(quellLabel);

		quellListModel = new DefaultListModel<>();
		quellJList = new JList<ListItem>(quellListModel);
		quellJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		quellJList.setLayoutOrientation(JList.VERTICAL);
		quellJList.addListSelectionListener(this);

		listBoxScroller = new JScrollPane(quellJList);
		listBoxScroller.setBounds(0, 30, 315, 100);
		fcPanel.add(listBoxScroller);

		btnQAuswahl = new JButton("Quellverz. hinzufügen");
		btnQAuswahl.setBounds(320, 30, 200, 25);
		btnQAuswahl.addActionListener(this);
		fcPanel.add(btnQAuswahl);
		btnQEntfernen = new JButton("Quellverz. entfernen");
		btnQEntfernen.setBounds(320, 60, 200, 25);
		btnQEntfernen.addActionListener(this);
		fcPanel.add(btnQEntfernen);
		
		zielLabel = new JLabel("Bitte Zielverzeichnis auswählen");
		zielLabel.setBounds(10, 135, 320, 20);
		fcPanel.add(zielLabel);

		zielListModel = new DefaultListModel<>();
		zielJList = new JList<ListItem>(zielListModel);
		zielJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		zielJList.setLayoutOrientation(JList.VERTICAL);
		zielJList.addListSelectionListener(this);

		listBoxScroller2 = new JScrollPane(zielJList);
		listBoxScroller2.setBounds(0, 160, 315, 100);
		fcPanel.add(listBoxScroller2);

		btnZAuswahl = new JButton("Zielverz. hinzufügen");
		btnZAuswahl.setBounds(320, 160, 200, 25);
		btnZAuswahl.addActionListener(this);
		fcPanel.add(btnZAuswahl);
		btnZEntfernen = new JButton("Zielverz. entfernen");
		btnZEntfernen.setBounds(320, 190, 200, 25);
		btnZEntfernen.addActionListener(this);
		fcPanel.add(btnZEntfernen);
		this.add(fcPanel);

		ButtonGroup bGrp = new ButtonGroup();

		optionPanel = new JPanel();
		optionPanel.setBounds(10, 300 + 10, this.getWidth() -40, 90);
		optionPanel.setPreferredSize(new Dimension( this.getWidth() -40, 90));
		// optionPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		optionPanel.setLayout(new GridLayout(3,1));

		nUebSchr = new JRadioButton("Keine Dateien überschreiben");
		nUebSchr.addItemListener(this);
		bGrp.add(nUebSchr);
		optionPanel.add(nUebSchr);

		ueSchr = new JRadioButton("Neuere Dateien überschreiben");
		ueSchr.addItemListener(this);
		bGrp.add(ueSchr);
		optionPanel.add(ueSchr);

		aUeSchr = new JRadioButton("Alle Dateien überschreiben");
		aUeSchr.addItemListener(this);
		bGrp.add(aUeSchr);
		optionPanel.add(aUeSchr);

		this.add(optionPanel);
		
		
		
		
		
		
		
		syncPanel = new JPanel();
		syncPanel.setBounds(10, optionPanel.getY()+ optionPanel.getHeight() + 10 , this.getWidth() -30, 25);
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
	
		logPanel.add(textAreaScroller,BorderLayout.CENTER);
		this.add(logPanel, BorderLayout.SOUTH);



	}

	private void initFrame()
	{
		this.setLocationRelativeTo(null);
		
		try
		{
			if (!Files.exists(saveName))
				Files.createFile(saveName);

			laden(saveName);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		
		nUebSchr.setSelected(true);
		if (!quellListModel.isEmpty())
			quellJList.setSelectedIndex(0);
		if (!zielListModel.isEmpty())
			zielJList.setSelectedIndex(0);
	
		starteLaufwerkspruefung();
		
	}
	
	private void starteLaufwerkspruefung()
	{
		u = new Thread(new laufwerksPruefung());
   	u.setDaemon(true);
		u.start();
		
	}
	
	
	/**
     *
     */
	public void Show()
	{
		initFrame();
		this.setVisible(true);

	}
	public void visible()
	{
		this.setVisible(true);
	}
	public void invisible()
	{
		this.setVisible(false);
	}
	

	/**
     *
     */
	public Path dateiAuswählen()
	{
		JFileChooser fc1 = new JFileChooser();

		fc1.setDialogTitle("SyncOrdner auswählen");
		fc1.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

		if (fc1.showSaveDialog(this) == JFileChooser.APPROVE_OPTION)
			return fc1.getSelectedFile().toPath();
		else
			return null;
	}

	public Path dateiAuswählen(Path neuesLaufwerk)
	{
		JFileChooser fc1 = new JFileChooser();
		fc1.setDialogTitle("SyncOrdner auswählen");
		fc1.setCurrentDirectory(neuesLaufwerk.toFile());
		fc1.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

		if (fc1.showSaveDialog(this) == JFileChooser.APPROVE_OPTION)
			return fc1.getSelectedFile().toPath();
		else
			return null;
	}
	
	private void neuesLaufwerkDialog()
	{
		Object[] options = {"Quell", "Ziel" , "Abbruch"};
		
		
		System.out.println("windowClosing()");
		
		int retValue = JOptionPane.showOptionDialog(null, "Neues Laufwerk wurde gefunden. Soll es eingebunden werden?", "Laufwerk endeckt", 
				                                    JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, 
				                                    null, options, options[2]);
		
		if (retValue == JOptionPane.CANCEL_OPTION || retValue == JOptionPane.CLOSED_OPTION)
			return;
		else if (retValue == JOptionPane.YES_OPTION)
			addQListBoxEintrag(dateiAuswählen(neuesLaufwerk));
		else if (retValue == JOptionPane.NO_OPTION)
			addZListBoxEintrag(dateiAuswählen(neuesLaufwerk));
		
		
		
		
	}
	private void addQListBoxEintrag(Path quellOrdner)
	{
		if (quellOrdner == null)
			return;
		quellListModel.addElement(new ListItem(quellOrdner, quellOrdner));
		quellJList.setSelectedIndex(quellListModel.getSize()-1);
	}

	private void subQListBoxEintrag()
	{
		if (quellJList.getSelectedIndex() < 0)
			return;
		else
			quellListModel.remove(quellJList.getSelectedIndex());
			if (quellListModel.getSize() > 0)
				quellJList.setSelectedIndex(quellListModel.getSize()-1);
	}

	/**
    *
    */
	private void addZListBoxEintrag(Path zielOrdner)
	{
		if (zielOrdner == null)
			return;
		zielListModel.addElement(new ListItem(zielOrdner, zielOrdner.toString()));
		zielJList.setSelectedIndex(zielListModel.getSize()-1);
	}

	private void subZListBoxEintrag()
	{
		if (zielJList.getSelectedIndex() < 0)
			return;
		else
			zielListModel.remove(zielJList.getSelectedIndex());
		if (zielListModel.getSize() > 0)
			zielJList.setSelectedIndex(zielListModel.getSize()-1);
	}

	private void speichern(Path saveName)
	{
		Properties prop = new Properties();

		if (!quellListModel.isEmpty())
			for (int i = 0; i < quellListModel.getSize(); i++)
				prop.setProperty(String.format("quellMenu%d", i), quellListModel.getElementAt(i).getValueMember().toString());

		if (!zielListModel.isEmpty())
			for (int i = 0; i < zielListModel.getSize(); i++)
				prop.setProperty(String.format("zielMenu%d", i), zielListModel.getElementAt(i).getValueMember().toString());

		try
		{
			FileOutputStream out = new FileOutputStream(saveName.toString());
			prop.store(out, null);
			out.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private void laden(Path saveName) throws IOException
	{
		Properties prop = new Properties();

		FileInputStream in = new FileInputStream(saveName.toString());
		prop.load(in);

		for (int i = 0; prop.containsKey(String.format("quellMenu%d", i)); i++)
			quellListModel.addElement(new ListItem(Paths.get(prop.getProperty(String.format("quellMenu%d", i))), Paths.get(prop.getProperty(String.format(
					"quellMenu%d", i)))));
		for (int i = 0; prop.containsKey(String.format("zielMenu%d", i)); i++)
			zielListModel.addElement(new ListItem(Paths.get(prop.getProperty(String.format("zielMenu%d", i))), Paths.get(prop.getProperty(String.format("zielMenu%d",
					i)))));

		in.close();
	}

	public void checkUeberSchreib()
	{
		if (ueSchr.isSelected())
		{
			cUeSchr = true;
			cAUeSchr = false;
		}
		else if (aUeSchr.isSelected())
		{
			cUeSchr = false;
			cAUeSchr = true;
		}
		else if (nUebSchr.isSelected())
		{
			cUeSchr = false;
			cAUeSchr = false;
		}
	}



	private boolean checkAlter(Path quellDatei, Path zielDatei)
	{
		boolean retValue = false;
		try
		{
			if ((Files.getLastModifiedTime(quellDatei).compareTo(Files.getLastModifiedTime(zielDatei)) > 0))
				retValue = true;
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return retValue;
	}
	private void startCopy(Path quellOrdner,Path zielOrdner)
	{
		t = new Thread(new copyNprogress(quellOrdner,zielOrdner));
		t.start();
	}
	private void setAbbruch()
	{
		abbruch = true;
	}
	
	@Override
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == btnQAuswahl)
		{
			addQListBoxEintrag(dateiAuswählen());
		}
		if (e.getSource() == btnQEntfernen)
		{
			subQListBoxEintrag();
		}
		if (e.getSource() == btnZAuswahl)
		{
			addZListBoxEintrag(dateiAuswählen());
		}
		if (e.getSource() == btnZEntfernen)
		{
			subZListBoxEintrag();
		}
		if (e.getSource() == btnSync)
		{
			
			startCopy((Path) quellJList.getSelectedValue().getValueMember(),(Path) zielJList.getSelectedValue().getValueMember());

		}
		if (e.getSource() == btnAbbruch)
		{
			setAbbruch();
		}
		if (e.getSource() == btnBackup)
		{
			MainNtray.bwvisible();
			this.invisible();
		}
	}

	@Override
	public void valueChanged(ListSelectionEvent e)
	{
		
	}

	@Override
	public void windowOpened(WindowEvent e)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void windowClosing(WindowEvent e)
	{
		speichern(saveName);
		MainNtray.bwdispose();
		MainNtray.removeTrayIcon();
		this.dispose();

	}

	@Override
	public void windowClosed(WindowEvent e)
	{

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

	@Override
	public void itemStateChanged(ItemEvent e)
	{
		if (e.getSource() instanceof JRadioButton)
			checkUeberSchreib();

	}
	
	
	
	private class copyNprogress implements Runnable 
	{
		private Path quellOrdner, zielOrdner ;
		int pbCounter= 0, i = 0;
		
		
		
		
		public copyNprogress(Path quellOrdner,Path zielOrdner) 
		{
				this.quellOrdner = quellOrdner;
				this.zielOrdner = zielOrdner;			
				
		}

		public void cnpStart(Path quellOrdner, Path zielOrdner)
		{
			try
			{
				DirectoryStream<Path> qstream = Files.newDirectoryStream(quellOrdner);
				for (Path qfile : qstream)
				{
					Path target = Paths.get(zielOrdner.toString() + "/" + qfile.getFileName());
					if (abbruch)
						break;
					if (Files.isDirectory(qfile) && !Files.exists(target))
					{
						Files.createDirectory(target);
						textArea.append("Verzeichnis: " + qfile + " wurde erstellt" + System.lineSeparator());
						cnpStart(Paths.get(quellOrdner.toString() + "/" + qfile.getFileName()), Paths.get(zielOrdner.toString() + "/" + qfile.getFileName()));
					}

					else if (Files.isDirectory(qfile) && Files.exists(target))
					{
						textArea.append("Wechsle in Verzeichnis: " + qfile + System.lineSeparator());
						cnpStart(Paths.get(quellOrdner.toString() + "/" + qfile.getFileName()), Paths.get(zielOrdner.toString() + "/" + qfile.getFileName()));
					}
					// Wenn die Datei noch nicht existiert
					else if (!Files.exists(target))
					{
						textArea.append("Datei " + target.toString() + " wurde erstellt" + System.lineSeparator());
						Files.copy(qfile, target, StandardCopyOption.REPLACE_EXISTING);

					}
					// Wenn Datei im Zielverzeichnis schon existiert
					else if (Files.exists(target))
					{
						if (cAUeSchr)
						{
							textArea.append("Datei " + target.toString() + " wird absolut überschrieben" + System.lineSeparator());
							Files.copy(qfile, target, StandardCopyOption.REPLACE_EXISTING);
						} else if (cUeSchr)
						{
							if (checkAlter(Paths.get(quellOrdner.toString() + "/" + qfile.getFileName()),
									Paths.get(zielOrdner.toString() + "/" + qfile.getFileName())))
							{
								textArea.append(target.toString() + " wird mit neuer Datei überschrieben" + System.lineSeparator());
								Files.copy(qfile, target, StandardCopyOption.REPLACE_EXISTING);
							} else
							{
								textArea.append(target.toString() + " alte Datei bleibt bestehen" + System.lineSeparator());
							}
						} else

							textArea.append(target.toString() + " alte Datei bleibt bestehen" + System.lineSeparator());
					}
					pbCounter++;
					progressBar.setValue(pbCounter);
				}

				qstream.close();
			} catch (IOException e)
			{
				
				e.printStackTrace();
			}
		}
		private int getNumberOfItems(Path quellOrdner)
		{
			int retValue = 0;
			
			try
			{
				DirectoryStream<Path> qstream = Files.newDirectoryStream(quellOrdner);
				for (Path qfile : qstream)
				{
					if (Files.isDirectory(qfile))
					{
						getNumberOfItems(Paths.get(quellOrdner.toString() + "/" + qfile.getFileName()));	
					}
					i++;
				}
				qstream.close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			
			retValue = i;
			return retValue;
			
		}
		
		@Override
		public void run()
		{
			abbruch = false;
			btnSync.setVisible(false);
			btnAbbruch.setVisible(true);
			pbCounter = 0;
			i = 0;
			progressBar.setMaximum(getNumberOfItems(quellOrdner));
			progressBar.setValue(0);
			progressBar.setMinimum(0);
			progressBar.setVisible(true);
			cnpStart(quellOrdner,zielOrdner);
			btnSync.setVisible(true);
			btnAbbruch.setVisible(false);
			progressBar.setValue(0);
			
		}
	
		}
	
	
	
	
	
private class laufwerksPruefung extends Thread
	{
		private ArrayList<Path> initial;
		private ArrayList<Path> aktuell;

		public laufwerksPruefung() 
		{
			if (istWindows())
				initial = holeLaufwerkeWindows();
			
			else
				initial = holeLaufwerkeUnix();
			textArea.append("Laufwerksprüfung aktiv" + System.lineSeparator());

		}
		private boolean istWindows()
	{
		boolean retValue = false;
		if (System.getProperty("os.name").contains("Windows"))
		{
			retValue = true;
		}
		return retValue;
	}

	private ArrayList<Path> holeLaufwerkeWindows()
	{
		ArrayList<Path> laufwerksRoot = new ArrayList<>();
		
		 Iterable<Path> rootDirectories = FileSystems.getDefault().getRootDirectories();
	  
	   for (Path path : rootDirectories)
	   {
	  	 laufwerksRoot.add(path.getRoot());
	   }
		return laufwerksRoot;
	}

	private ArrayList<Path> holeLaufwerkeUnix()
	{
		ArrayList<Path> laufwerksRoot = new ArrayList<>();
		for (FileStore store : FileSystems.getDefault().getFileStores())
		{
			if (store.name().contains("/dev/sd"))
			{
		laufwerksRoot.add(Paths.get(store.toString().substring(0 ,  store.toString().indexOf(' '))));
			}
		}
		return laufwerksRoot;
	}

	
	private Path holePathVonNeuemLaufwerk(ArrayList<Path> initial,ArrayList<Path> aktuell)
	{
		ArrayList<Path> test ,test1;
		test = (ArrayList<Path>) aktuell.clone();
		test1 = (ArrayList<Path>) initial.clone();
		test.removeAll(test1);
		return test.get(test.size()-1);
	}


	
	
	@Override
	public void run()
	{ 
		while (true)
		{
		if (istWindows())
			aktuell = holeLaufwerkeWindows();
		else
			aktuell = holeLaufwerkeUnix();
		
		if (initial.size() != aktuell.size())
		{
			if (!initial.containsAll(aktuell))
			{
				neuesLaufwerk = holePathVonNeuemLaufwerk(initial,aktuell);
				textArea.append("Neues Laufwerk endeckt:  " + neuesLaufwerk + System.lineSeparator());
				this.initial = (ArrayList<Path>) aktuell.clone();
				neuesLaufwerkDialog();
				
			}
			else 
			{
				this.initial = (ArrayList<Path>) aktuell.clone();
				textArea.append("Laufwerk wurde entfernt" + System.lineSeparator());
			}
		}
		
					try
						{
							Thread.sleep(5000);
						}
					catch (InterruptedException e)
						{
							System.out.println("Laufwerksprüfung wird abgebrochen");
						}
		
		}
	}
	}








@Override
public void stateChanged(ChangeEvent e)
{
	// TODO Auto-generated method stub
	
}
	
}


