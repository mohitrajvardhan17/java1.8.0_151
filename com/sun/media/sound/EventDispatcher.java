package com.sun.media.sound;

import java.util.ArrayList;
import java.util.List;
import javax.sound.midi.ControllerEventListener;
import javax.sound.midi.MetaEventListener;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.ShortMessage;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;

final class EventDispatcher
  implements Runnable
{
  private static final int AUTO_CLOSE_TIME = 5000;
  private final ArrayList eventQueue = new ArrayList();
  private Thread thread = null;
  private final ArrayList<ClipInfo> autoClosingClips = new ArrayList();
  private final ArrayList<LineMonitor> lineMonitors = new ArrayList();
  static final int LINE_MONITOR_TIME = 400;
  
  EventDispatcher() {}
  
  synchronized void start()
  {
    if (thread == null) {
      thread = JSSecurityManager.createThread(this, "Java Sound Event Dispatcher", true, -1, true);
    }
  }
  
  void processEvent(EventInfo paramEventInfo)
  {
    int i = paramEventInfo.getListenerCount();
    Object localObject;
    int j;
    if ((paramEventInfo.getEvent() instanceof LineEvent))
    {
      localObject = (LineEvent)paramEventInfo.getEvent();
      for (j = 0; j < i; j++) {
        try
        {
          ((LineListener)paramEventInfo.getListener(j)).update((LineEvent)localObject);
        }
        catch (Throwable localThrowable1) {}
      }
      return;
    }
    if ((paramEventInfo.getEvent() instanceof MetaMessage))
    {
      localObject = (MetaMessage)paramEventInfo.getEvent();
      for (j = 0; j < i; j++) {
        try
        {
          ((MetaEventListener)paramEventInfo.getListener(j)).meta((MetaMessage)localObject);
        }
        catch (Throwable localThrowable2) {}
      }
      return;
    }
    if ((paramEventInfo.getEvent() instanceof ShortMessage))
    {
      localObject = (ShortMessage)paramEventInfo.getEvent();
      j = ((ShortMessage)localObject).getStatus();
      if ((j & 0xF0) == 176) {
        for (int k = 0; k < i; k++) {
          try
          {
            ((ControllerEventListener)paramEventInfo.getListener(k)).controlChange((ShortMessage)localObject);
          }
          catch (Throwable localThrowable3) {}
        }
      }
      return;
    }
    Printer.err("Unknown event type: " + paramEventInfo.getEvent());
  }
  
  void dispatchEvents()
  {
    EventInfo localEventInfo = null;
    synchronized (this)
    {
      try
      {
        if (eventQueue.size() == 0) {
          if ((autoClosingClips.size() > 0) || (lineMonitors.size() > 0))
          {
            int i = 5000;
            if (lineMonitors.size() > 0) {
              i = 400;
            }
            wait(i);
          }
          else
          {
            wait();
          }
        }
      }
      catch (InterruptedException localInterruptedException) {}
      if (eventQueue.size() > 0) {
        localEventInfo = (EventInfo)eventQueue.remove(0);
      }
    }
    if (localEventInfo != null)
    {
      processEvent(localEventInfo);
    }
    else
    {
      if (autoClosingClips.size() > 0) {
        closeAutoClosingClips();
      }
      if (lineMonitors.size() > 0) {
        monitorLines();
      }
    }
  }
  
  private synchronized void postEvent(EventInfo paramEventInfo)
  {
    eventQueue.add(paramEventInfo);
    notifyAll();
  }
  
  public void run()
  {
    try
    {
      for (;;)
      {
        dispatchEvents();
      }
    }
    catch (Throwable localThrowable) {}
  }
  
  void sendAudioEvents(Object paramObject, List paramList)
  {
    if ((paramList == null) || (paramList.size() == 0)) {
      return;
    }
    start();
    EventInfo localEventInfo = new EventInfo(paramObject, paramList);
    postEvent(localEventInfo);
  }
  
  private void closeAutoClosingClips()
  {
    synchronized (autoClosingClips)
    {
      long l = System.currentTimeMillis();
      for (int i = autoClosingClips.size() - 1; i >= 0; i--)
      {
        ClipInfo localClipInfo = (ClipInfo)autoClosingClips.get(i);
        if (localClipInfo.isExpired(l))
        {
          AutoClosingClip localAutoClosingClip = localClipInfo.getClip();
          if ((!localAutoClosingClip.isOpen()) || (!localAutoClosingClip.isAutoClosing())) {
            autoClosingClips.remove(i);
          } else if ((!localAutoClosingClip.isRunning()) && (!localAutoClosingClip.isActive()) && (localAutoClosingClip.isAutoClosing())) {
            localAutoClosingClip.close();
          }
        }
      }
    }
  }
  
  private int getAutoClosingClipIndex(AutoClosingClip paramAutoClosingClip)
  {
    synchronized (autoClosingClips)
    {
      for (int i = autoClosingClips.size() - 1; i >= 0; i--) {
        if (paramAutoClosingClip.equals(((ClipInfo)autoClosingClips.get(i)).getClip())) {
          return i;
        }
      }
    }
    return -1;
  }
  
  void autoClosingClipOpened(AutoClosingClip paramAutoClosingClip)
  {
    int i = 0;
    synchronized (autoClosingClips)
    {
      i = getAutoClosingClipIndex(paramAutoClosingClip);
      if (i == -1) {
        autoClosingClips.add(new ClipInfo(paramAutoClosingClip));
      }
    }
    if (i == -1) {
      synchronized (this)
      {
        notifyAll();
      }
    }
  }
  
  void autoClosingClipClosed(AutoClosingClip paramAutoClosingClip) {}
  
  private void monitorLines()
  {
    synchronized (lineMonitors)
    {
      for (int i = 0; i < lineMonitors.size(); i++) {
        ((LineMonitor)lineMonitors.get(i)).checkLine();
      }
    }
  }
  
  void addLineMonitor(LineMonitor paramLineMonitor)
  {
    synchronized (lineMonitors)
    {
      if (lineMonitors.indexOf(paramLineMonitor) >= 0) {
        return;
      }
      lineMonitors.add(paramLineMonitor);
    }
    synchronized (this)
    {
      notifyAll();
    }
  }
  
  void removeLineMonitor(LineMonitor paramLineMonitor)
  {
    synchronized (lineMonitors)
    {
      if (lineMonitors.indexOf(paramLineMonitor) < 0) {
        return;
      }
      lineMonitors.remove(paramLineMonitor);
    }
  }
  
  private class ClipInfo
  {
    private final AutoClosingClip clip;
    private final long expiration;
    
    ClipInfo(AutoClosingClip paramAutoClosingClip)
    {
      clip = paramAutoClosingClip;
      expiration = (System.currentTimeMillis() + 5000L);
    }
    
    AutoClosingClip getClip()
    {
      return clip;
    }
    
    boolean isExpired(long paramLong)
    {
      return paramLong > expiration;
    }
  }
  
  private class EventInfo
  {
    private final Object event;
    private final Object[] listeners;
    
    EventInfo(Object paramObject, List paramList)
    {
      event = paramObject;
      listeners = paramList.toArray();
    }
    
    Object getEvent()
    {
      return event;
    }
    
    int getListenerCount()
    {
      return listeners.length;
    }
    
    Object getListener(int paramInt)
    {
      return listeners[paramInt];
    }
  }
  
  static abstract interface LineMonitor
  {
    public abstract void checkLine();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\media\sound\EventDispatcher.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */