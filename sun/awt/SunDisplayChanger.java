package sun.awt;

import java.awt.IllegalComponentStateException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import sun.util.logging.PlatformLogger;
import sun.util.logging.PlatformLogger.Level;

public class SunDisplayChanger
{
  private static final PlatformLogger log = PlatformLogger.getLogger("sun.awt.multiscreen.SunDisplayChanger");
  private Map<DisplayChangedListener, Void> listeners = Collections.synchronizedMap(new WeakHashMap(1));
  
  public SunDisplayChanger() {}
  
  public void add(DisplayChangedListener paramDisplayChangedListener)
  {
    if ((log.isLoggable(PlatformLogger.Level.FINE)) && (paramDisplayChangedListener == null)) {
      log.fine("Assertion (theListener != null) failed");
    }
    if (log.isLoggable(PlatformLogger.Level.FINER)) {
      log.finer("Adding listener: " + paramDisplayChangedListener);
    }
    listeners.put(paramDisplayChangedListener, null);
  }
  
  public void remove(DisplayChangedListener paramDisplayChangedListener)
  {
    if ((log.isLoggable(PlatformLogger.Level.FINE)) && (paramDisplayChangedListener == null)) {
      log.fine("Assertion (theListener != null) failed");
    }
    if (log.isLoggable(PlatformLogger.Level.FINER)) {
      log.finer("Removing listener: " + paramDisplayChangedListener);
    }
    listeners.remove(paramDisplayChangedListener);
  }
  
  public void notifyListeners()
  {
    if (log.isLoggable(PlatformLogger.Level.FINEST)) {
      log.finest("notifyListeners");
    }
    HashSet localHashSet;
    synchronized (listeners)
    {
      localHashSet = new HashSet(listeners.keySet());
    }
    ??? = localHashSet.iterator();
    while (((Iterator)???).hasNext())
    {
      DisplayChangedListener localDisplayChangedListener = (DisplayChangedListener)((Iterator)???).next();
      try
      {
        if (log.isLoggable(PlatformLogger.Level.FINEST)) {
          log.finest("displayChanged for listener: " + localDisplayChangedListener);
        }
        localDisplayChangedListener.displayChanged();
      }
      catch (IllegalComponentStateException localIllegalComponentStateException)
      {
        listeners.remove(localDisplayChangedListener);
      }
    }
  }
  
  public void notifyPaletteChanged()
  {
    if (log.isLoggable(PlatformLogger.Level.FINEST)) {
      log.finest("notifyPaletteChanged");
    }
    HashSet localHashSet;
    synchronized (listeners)
    {
      localHashSet = new HashSet(listeners.keySet());
    }
    ??? = localHashSet.iterator();
    while (((Iterator)???).hasNext())
    {
      DisplayChangedListener localDisplayChangedListener = (DisplayChangedListener)((Iterator)???).next();
      try
      {
        if (log.isLoggable(PlatformLogger.Level.FINEST)) {
          log.finest("paletteChanged for listener: " + localDisplayChangedListener);
        }
        localDisplayChangedListener.paletteChanged();
      }
      catch (IllegalComponentStateException localIllegalComponentStateException)
      {
        listeners.remove(localDisplayChangedListener);
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\awt\SunDisplayChanger.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */