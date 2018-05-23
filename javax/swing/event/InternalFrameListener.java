package javax.swing.event;

import java.util.EventListener;

public abstract interface InternalFrameListener
  extends EventListener
{
  public abstract void internalFrameOpened(InternalFrameEvent paramInternalFrameEvent);
  
  public abstract void internalFrameClosing(InternalFrameEvent paramInternalFrameEvent);
  
  public abstract void internalFrameClosed(InternalFrameEvent paramInternalFrameEvent);
  
  public abstract void internalFrameIconified(InternalFrameEvent paramInternalFrameEvent);
  
  public abstract void internalFrameDeiconified(InternalFrameEvent paramInternalFrameEvent);
  
  public abstract void internalFrameActivated(InternalFrameEvent paramInternalFrameEvent);
  
  public abstract void internalFrameDeactivated(InternalFrameEvent paramInternalFrameEvent);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\event\InternalFrameListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */