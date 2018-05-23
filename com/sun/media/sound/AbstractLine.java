package com.sun.media.sound;

import java.util.Map;
import java.util.Vector;
import java.util.WeakHashMap;
import javax.sound.sampled.Control;
import javax.sound.sampled.Control.Type;
import javax.sound.sampled.Line;
import javax.sound.sampled.Line.Info;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineEvent.Type;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;

abstract class AbstractLine
  implements Line
{
  protected final Line.Info info;
  protected Control[] controls;
  AbstractMixer mixer;
  private boolean open = false;
  private final Vector listeners = new Vector();
  private static final Map<ThreadGroup, EventDispatcher> dispatchers = new WeakHashMap();
  
  protected AbstractLine(Line.Info paramInfo, AbstractMixer paramAbstractMixer, Control[] paramArrayOfControl)
  {
    if (paramArrayOfControl == null) {
      paramArrayOfControl = new Control[0];
    }
    info = paramInfo;
    mixer = paramAbstractMixer;
    controls = paramArrayOfControl;
  }
  
  public final Line.Info getLineInfo()
  {
    return info;
  }
  
  public final boolean isOpen()
  {
    return open;
  }
  
  public final void addLineListener(LineListener paramLineListener)
  {
    synchronized (listeners)
    {
      if (!listeners.contains(paramLineListener)) {
        listeners.addElement(paramLineListener);
      }
    }
  }
  
  public final void removeLineListener(LineListener paramLineListener)
  {
    listeners.removeElement(paramLineListener);
  }
  
  public final Control[] getControls()
  {
    Control[] arrayOfControl = new Control[controls.length];
    for (int i = 0; i < controls.length; i++) {
      arrayOfControl[i] = controls[i];
    }
    return arrayOfControl;
  }
  
  public final boolean isControlSupported(Control.Type paramType)
  {
    if (paramType == null) {
      return false;
    }
    for (int i = 0; i < controls.length; i++) {
      if (paramType == controls[i].getType()) {
        return true;
      }
    }
    return false;
  }
  
  public final Control getControl(Control.Type paramType)
  {
    if (paramType != null) {
      for (int i = 0; i < controls.length; i++) {
        if (paramType == controls[i].getType()) {
          return controls[i];
        }
      }
    }
    throw new IllegalArgumentException("Unsupported control type: " + paramType);
  }
  
  final void setOpen(boolean paramBoolean)
  {
    int i = 0;
    long l = getLongFramePosition();
    synchronized (this)
    {
      if (open != paramBoolean)
      {
        open = paramBoolean;
        i = 1;
      }
    }
    if (i != 0) {
      if (paramBoolean) {
        sendEvents(new LineEvent(this, LineEvent.Type.OPEN, l));
      } else {
        sendEvents(new LineEvent(this, LineEvent.Type.CLOSE, l));
      }
    }
  }
  
  final void sendEvents(LineEvent paramLineEvent)
  {
    getEventDispatcher().sendAudioEvents(paramLineEvent, listeners);
  }
  
  public final int getFramePosition()
  {
    return (int)getLongFramePosition();
  }
  
  public long getLongFramePosition()
  {
    return -1L;
  }
  
  final AbstractMixer getMixer()
  {
    return mixer;
  }
  
  final EventDispatcher getEventDispatcher()
  {
    ThreadGroup localThreadGroup = Thread.currentThread().getThreadGroup();
    synchronized (dispatchers)
    {
      EventDispatcher localEventDispatcher = (EventDispatcher)dispatchers.get(localThreadGroup);
      if (localEventDispatcher == null)
      {
        localEventDispatcher = new EventDispatcher();
        dispatchers.put(localThreadGroup, localEventDispatcher);
        localEventDispatcher.start();
      }
      return localEventDispatcher;
    }
  }
  
  public abstract void open()
    throws LineUnavailableException;
  
  public abstract void close();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\media\sound\AbstractLine.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */