package java.awt.dnd;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Image;
import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.peer.DragSourceContextPeer;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectInputStream.GetField;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.TooManyListenersException;

public class DragSourceContext
  implements DragSourceListener, DragSourceMotionListener, Serializable
{
  private static final long serialVersionUID = -115407898692194719L;
  protected static final int DEFAULT = 0;
  protected static final int ENTER = 1;
  protected static final int OVER = 2;
  protected static final int CHANGED = 3;
  private static Transferable emptyTransferable;
  private transient DragSourceContextPeer peer;
  private DragGestureEvent trigger;
  private Cursor cursor;
  private transient Transferable transferable;
  private transient DragSourceListener listener;
  private boolean useCustomCursor;
  private int sourceActions;
  
  public DragSourceContext(DragSourceContextPeer paramDragSourceContextPeer, DragGestureEvent paramDragGestureEvent, Cursor paramCursor, Image paramImage, Point paramPoint, Transferable paramTransferable, DragSourceListener paramDragSourceListener)
  {
    if (paramDragSourceContextPeer == null) {
      throw new NullPointerException("DragSourceContextPeer");
    }
    if (paramDragGestureEvent == null) {
      throw new NullPointerException("Trigger");
    }
    if (paramDragGestureEvent.getDragSource() == null) {
      throw new IllegalArgumentException("DragSource");
    }
    if (paramDragGestureEvent.getComponent() == null) {
      throw new IllegalArgumentException("Component");
    }
    if (paramDragGestureEvent.getSourceAsDragGestureRecognizer().getSourceActions() == 0) {
      throw new IllegalArgumentException("source actions");
    }
    if (paramDragGestureEvent.getDragAction() == 0) {
      throw new IllegalArgumentException("no drag action");
    }
    if (paramTransferable == null) {
      throw new NullPointerException("Transferable");
    }
    if ((paramImage != null) && (paramPoint == null)) {
      throw new NullPointerException("offset");
    }
    peer = paramDragSourceContextPeer;
    trigger = paramDragGestureEvent;
    cursor = paramCursor;
    transferable = paramTransferable;
    listener = paramDragSourceListener;
    sourceActions = paramDragGestureEvent.getSourceAsDragGestureRecognizer().getSourceActions();
    useCustomCursor = (paramCursor != null);
    updateCurrentCursor(paramDragGestureEvent.getDragAction(), getSourceActions(), 0);
  }
  
  public DragSource getDragSource()
  {
    return trigger.getDragSource();
  }
  
  public Component getComponent()
  {
    return trigger.getComponent();
  }
  
  public DragGestureEvent getTrigger()
  {
    return trigger;
  }
  
  public int getSourceActions()
  {
    return sourceActions;
  }
  
  public synchronized void setCursor(Cursor paramCursor)
  {
    useCustomCursor = (paramCursor != null);
    setCursorImpl(paramCursor);
  }
  
  public Cursor getCursor()
  {
    return cursor;
  }
  
  public synchronized void addDragSourceListener(DragSourceListener paramDragSourceListener)
    throws TooManyListenersException
  {
    if (paramDragSourceListener == null) {
      return;
    }
    if (equals(paramDragSourceListener)) {
      throw new IllegalArgumentException("DragSourceContext may not be its own listener");
    }
    if (listener != null) {
      throw new TooManyListenersException();
    }
    listener = paramDragSourceListener;
  }
  
  public synchronized void removeDragSourceListener(DragSourceListener paramDragSourceListener)
  {
    if ((listener != null) && (listener.equals(paramDragSourceListener))) {
      listener = null;
    } else {
      throw new IllegalArgumentException();
    }
  }
  
  public void transferablesFlavorsChanged()
  {
    if (peer != null) {
      peer.transferablesFlavorsChanged();
    }
  }
  
  public void dragEnter(DragSourceDragEvent paramDragSourceDragEvent)
  {
    DragSourceListener localDragSourceListener = listener;
    if (localDragSourceListener != null) {
      localDragSourceListener.dragEnter(paramDragSourceDragEvent);
    }
    getDragSource().processDragEnter(paramDragSourceDragEvent);
    updateCurrentCursor(getSourceActions(), paramDragSourceDragEvent.getTargetActions(), 1);
  }
  
  public void dragOver(DragSourceDragEvent paramDragSourceDragEvent)
  {
    DragSourceListener localDragSourceListener = listener;
    if (localDragSourceListener != null) {
      localDragSourceListener.dragOver(paramDragSourceDragEvent);
    }
    getDragSource().processDragOver(paramDragSourceDragEvent);
    updateCurrentCursor(getSourceActions(), paramDragSourceDragEvent.getTargetActions(), 2);
  }
  
  public void dragExit(DragSourceEvent paramDragSourceEvent)
  {
    DragSourceListener localDragSourceListener = listener;
    if (localDragSourceListener != null) {
      localDragSourceListener.dragExit(paramDragSourceEvent);
    }
    getDragSource().processDragExit(paramDragSourceEvent);
    updateCurrentCursor(0, 0, 0);
  }
  
  public void dropActionChanged(DragSourceDragEvent paramDragSourceDragEvent)
  {
    DragSourceListener localDragSourceListener = listener;
    if (localDragSourceListener != null) {
      localDragSourceListener.dropActionChanged(paramDragSourceDragEvent);
    }
    getDragSource().processDropActionChanged(paramDragSourceDragEvent);
    updateCurrentCursor(getSourceActions(), paramDragSourceDragEvent.getTargetActions(), 3);
  }
  
  public void dragDropEnd(DragSourceDropEvent paramDragSourceDropEvent)
  {
    DragSourceListener localDragSourceListener = listener;
    if (localDragSourceListener != null) {
      localDragSourceListener.dragDropEnd(paramDragSourceDropEvent);
    }
    getDragSource().processDragDropEnd(paramDragSourceDropEvent);
  }
  
  public void dragMouseMoved(DragSourceDragEvent paramDragSourceDragEvent)
  {
    getDragSource().processDragMouseMoved(paramDragSourceDragEvent);
  }
  
  public Transferable getTransferable()
  {
    return transferable;
  }
  
  protected synchronized void updateCurrentCursor(int paramInt1, int paramInt2, int paramInt3)
  {
    if (useCustomCursor) {
      return;
    }
    Cursor localCursor = null;
    switch (paramInt3)
    {
    default: 
      paramInt2 = 0;
    }
    int i = paramInt1 & paramInt2;
    if (i == 0)
    {
      if ((paramInt1 & 0x40000000) == 1073741824) {
        localCursor = DragSource.DefaultLinkNoDrop;
      } else if ((paramInt1 & 0x2) == 2) {
        localCursor = DragSource.DefaultMoveNoDrop;
      } else {
        localCursor = DragSource.DefaultCopyNoDrop;
      }
    }
    else if ((i & 0x40000000) == 1073741824) {
      localCursor = DragSource.DefaultLinkDrop;
    } else if ((i & 0x2) == 2) {
      localCursor = DragSource.DefaultMoveDrop;
    } else {
      localCursor = DragSource.DefaultCopyDrop;
    }
    setCursorImpl(localCursor);
  }
  
  private void setCursorImpl(Cursor paramCursor)
  {
    if ((cursor == null) || (!cursor.equals(paramCursor)))
    {
      cursor = paramCursor;
      if (peer != null) {
        peer.setCursor(cursor);
      }
    }
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    paramObjectOutputStream.defaultWriteObject();
    paramObjectOutputStream.writeObject(SerializationTester.test(transferable) ? transferable : null);
    paramObjectOutputStream.writeObject(SerializationTester.test(listener) ? listener : null);
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws ClassNotFoundException, IOException
  {
    ObjectInputStream.GetField localGetField = paramObjectInputStream.readFields();
    DragGestureEvent localDragGestureEvent = (DragGestureEvent)localGetField.get("trigger", null);
    if (localDragGestureEvent == null) {
      throw new InvalidObjectException("Null trigger");
    }
    if (localDragGestureEvent.getDragSource() == null) {
      throw new InvalidObjectException("Null DragSource");
    }
    if (localDragGestureEvent.getComponent() == null) {
      throw new InvalidObjectException("Null trigger component");
    }
    int i = localGetField.get("sourceActions", 0) & 0x40000003;
    if (i == 0) {
      throw new InvalidObjectException("Invalid source actions");
    }
    int j = localDragGestureEvent.getDragAction();
    if ((j != 1) && (j != 2) && (j != 1073741824)) {
      throw new InvalidObjectException("No drag action");
    }
    trigger = localDragGestureEvent;
    cursor = ((Cursor)localGetField.get("cursor", null));
    useCustomCursor = localGetField.get("useCustomCursor", false);
    sourceActions = i;
    transferable = ((Transferable)paramObjectInputStream.readObject());
    listener = ((DragSourceListener)paramObjectInputStream.readObject());
    if (transferable == null)
    {
      if (emptyTransferable == null) {
        emptyTransferable = new Transferable()
        {
          public DataFlavor[] getTransferDataFlavors()
          {
            return new DataFlavor[0];
          }
          
          public boolean isDataFlavorSupported(DataFlavor paramAnonymousDataFlavor)
          {
            return false;
          }
          
          public Object getTransferData(DataFlavor paramAnonymousDataFlavor)
            throws UnsupportedFlavorException
          {
            throw new UnsupportedFlavorException(paramAnonymousDataFlavor);
          }
        };
      }
      transferable = emptyTransferable;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\dnd\DragSourceContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */