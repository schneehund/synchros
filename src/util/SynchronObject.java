package util;

public class SynchronObject
{
	private boolean	stop;
	
	public SynchronObject()
	{
		stop = true;
	}
	
	public synchronized void warte()
	{
		
		while(stop)
		{
			
			try
			{
				/*
				 * wait() blockiert den aufrufenden Thread so lange, bis ein anderer
				 * Thread in einer anderen Methode dieses Objekts notify() oder
				 * notifyAll() aufruft. Das ist in diesem Fall der Thread, der die
				 * Methode "weiter()" ausfuehrt.
				 */
				wait();
			}
			catch (Exception ex) {} 
			
		}
		
		
	}
	public synchronized void weiter()
	{
		stop = false;
		System.out.println("Sperre ist aufgehoben.");
		
		// Benachrichtigung per notify() an den wartenden Thread.
		notify();
		
		System.out.println("Wartender Thread wurde benachrichtigt.");
		
		
	}
}
