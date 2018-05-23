package sun.print;

import java.util.Vector;
import javax.print.PrintService;
import javax.print.attribute.HashPrintServiceAttributeSet;
import javax.print.attribute.PrintServiceAttributeSet;
import javax.print.event.PrintServiceAttributeEvent;
import javax.print.event.PrintServiceAttributeListener;

class ServiceNotifier
  extends Thread
{
  private PrintService service;
  private Vector listeners;
  private boolean stop = false;
  private PrintServiceAttributeSet lastSet;
  
  ServiceNotifier(PrintService paramPrintService)
  {
    super(paramPrintService.getName() + " notifier");
    service = paramPrintService;
    listeners = new Vector();
    try
    {
      setPriority(4);
      setDaemon(true);
      start();
    }
    catch (SecurityException localSecurityException) {}
  }
  
  void addListener(PrintServiceAttributeListener paramPrintServiceAttributeListener)
  {
    synchronized (this)
    {
      if ((paramPrintServiceAttributeListener == null) || (listeners == null)) {
        return;
      }
      listeners.add(paramPrintServiceAttributeListener);
    }
  }
  
  void removeListener(PrintServiceAttributeListener paramPrintServiceAttributeListener)
  {
    synchronized (this)
    {
      if ((paramPrintServiceAttributeListener == null) || (listeners == null)) {
        return;
      }
      listeners.remove(paramPrintServiceAttributeListener);
    }
  }
  
  boolean isEmpty()
  {
    return (listeners == null) || (listeners.isEmpty());
  }
  
  void stopNotifier()
  {
    stop = true;
  }
  
  void wake()
  {
    try
    {
      interrupt();
    }
    catch (SecurityException localSecurityException) {}
  }
  
  public void run()
  {
    long l1 = 15000L;
    long l2 = 2000L;
    while (!stop)
    {
      try
      {
        Thread.sleep(l2);
      }
      catch (InterruptedException localInterruptedException) {}
      synchronized (this)
      {
        if (listeners != null)
        {
          long l3 = System.currentTimeMillis();
          if (listeners != null)
          {
            PrintServiceAttributeSet localPrintServiceAttributeSet;
            if ((service instanceof AttributeUpdater)) {
              localPrintServiceAttributeSet = ((AttributeUpdater)service).getUpdatedAttributes();
            } else {
              localPrintServiceAttributeSet = service.getAttributes();
            }
            if ((localPrintServiceAttributeSet != null) && (!localPrintServiceAttributeSet.isEmpty())) {
              for (int i = 0; i < listeners.size(); i++)
              {
                PrintServiceAttributeListener localPrintServiceAttributeListener = (PrintServiceAttributeListener)listeners.elementAt(i);
                HashPrintServiceAttributeSet localHashPrintServiceAttributeSet = new HashPrintServiceAttributeSet(localPrintServiceAttributeSet);
                PrintServiceAttributeEvent localPrintServiceAttributeEvent = new PrintServiceAttributeEvent(service, localHashPrintServiceAttributeSet);
                localPrintServiceAttributeListener.attributeUpdate(localPrintServiceAttributeEvent);
              }
            }
          }
          l2 = (System.currentTimeMillis() - l3) * 10L;
          if (l2 < l1) {
            l2 = l1;
          }
        }
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\print\ServiceNotifier.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */