package start;

import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JFrame;


public class MainNtray implements ActionListener
	{
private static CopyWindow cw = new CopyWindow();
private static BackupWindow bw = new BackupWindow();
private static TrayIcon trayIcon;
private static SystemTray systemTray;

private static PopupMenu popupMenu;
private MenuItem miOeffnen, miBeenden;
private static MainNtray p = new MainNtray();
	public MainNtray() 
	{
		popupMenu = new PopupMenu();
		miOeffnen = new MenuItem("Öffnen");
		miOeffnen.addActionListener(this);
		popupMenu.add(miOeffnen);
		popupMenu.addSeparator();
		miBeenden = new MenuItem("Beenden");
		miBeenden.addActionListener(this);
		popupMenu.add(miBeenden);
		
	}
	
		public static void main(String[] args)
			{
				bw.Show();
				cw.Show();
				addTrayIcon();
			}

		private static void addTrayIcon()
		{
			
			if (!SystemTray.isSupported()) return;
		           
			 
			try
			{
				systemTray = SystemTray.getSystemTray();
				trayIcon = new TrayIcon(new ImageIcon("Synchros.png", "tray icon").getImage());
				
				// Passt die Größe des Images and die System Tray an. 
				trayIcon.setImageAutoSize(true);
				// Fügt das Popup-Menü hinzu.
				trayIcon.setPopupMenu(popupMenu);
				// Tooltip zum Tray Icon hinzufügen
				 trayIcon.setToolTip("Synchros");
				// Zeigt das Symbol ion der System Tray an.
				systemTray.add(trayIcon);
				// ActionListener für die MenuItems des Popup-Menüs.
				trayIcon.addActionListener(p);
			}
			catch (Exception ex)
			{
			}

		}
		
		
		public static void cwvisible()
		{
			cw.setVisible(true);
		}
		public static void cwinvisible()
		{
			cw.setVisible(false);
		}
		
		public static void bwshow()
		{
			bw.Show();
		}
		
		public static void bwvisible()
		{
			bw.setVisible(true);
		}
		public static void bwinvisible()
		{
			bw.setVisible(false);
		}
		
		public static void cwdispose()
		{
	
			cw.windowClosing(null);
		}
		public static void bwdispose()
		{
			bw.windowClosing(null);
			
		}

		public static void removeTrayIcon()
		{
			
				if (systemTray != null && trayIcon != null)
					SystemTray.getSystemTray().remove(trayIcon);

			
		}
		public void actionPerformed(ActionEvent e)
		{
			if (e.getSource() == miOeffnen)
			{			
				bw.setVisible(true);
			}
			else if (e.getSource() == miBeenden)
			{
				bw.dispose();
				removeTrayIcon();
			}
			
		}
	}
