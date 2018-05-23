package javax.swing.plaf.basic;

import java.awt.dnd.DragSource;
import java.awt.event.MouseEvent;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import sun.awt.AppContext;
import sun.awt.dnd.SunDragSourceContextPeer;

class DragRecognitionSupport
{
  private int motionThreshold;
  private MouseEvent dndArmedEvent;
  private JComponent component;
  
  DragRecognitionSupport() {}
  
  private static DragRecognitionSupport getDragRecognitionSupport()
  {
    DragRecognitionSupport localDragRecognitionSupport = (DragRecognitionSupport)AppContext.getAppContext().get(DragRecognitionSupport.class);
    if (localDragRecognitionSupport == null)
    {
      localDragRecognitionSupport = new DragRecognitionSupport();
      AppContext.getAppContext().put(DragRecognitionSupport.class, localDragRecognitionSupport);
    }
    return localDragRecognitionSupport;
  }
  
  public static boolean mousePressed(MouseEvent paramMouseEvent)
  {
    return getDragRecognitionSupport().mousePressedImpl(paramMouseEvent);
  }
  
  public static MouseEvent mouseReleased(MouseEvent paramMouseEvent)
  {
    return getDragRecognitionSupport().mouseReleasedImpl(paramMouseEvent);
  }
  
  public static boolean mouseDragged(MouseEvent paramMouseEvent, BeforeDrag paramBeforeDrag)
  {
    return getDragRecognitionSupport().mouseDraggedImpl(paramMouseEvent, paramBeforeDrag);
  }
  
  private void clearState()
  {
    dndArmedEvent = null;
    component = null;
  }
  
  private int mapDragOperationFromModifiers(MouseEvent paramMouseEvent, TransferHandler paramTransferHandler)
  {
    if ((paramTransferHandler == null) || (!SwingUtilities.isLeftMouseButton(paramMouseEvent))) {
      return 0;
    }
    return SunDragSourceContextPeer.convertModifiersToDropAction(paramMouseEvent.getModifiersEx(), paramTransferHandler.getSourceActions(component));
  }
  
  private boolean mousePressedImpl(MouseEvent paramMouseEvent)
  {
    component = ((JComponent)paramMouseEvent.getSource());
    if (mapDragOperationFromModifiers(paramMouseEvent, component.getTransferHandler()) != 0)
    {
      motionThreshold = DragSource.getDragThreshold();
      dndArmedEvent = paramMouseEvent;
      return true;
    }
    clearState();
    return false;
  }
  
  private MouseEvent mouseReleasedImpl(MouseEvent paramMouseEvent)
  {
    if (dndArmedEvent == null) {
      return null;
    }
    MouseEvent localMouseEvent = null;
    if (paramMouseEvent.getSource() == component) {
      localMouseEvent = dndArmedEvent;
    }
    clearState();
    return localMouseEvent;
  }
  
  private boolean mouseDraggedImpl(MouseEvent paramMouseEvent, BeforeDrag paramBeforeDrag)
  {
    if (dndArmedEvent == null) {
      return false;
    }
    if (paramMouseEvent.getSource() != component)
    {
      clearState();
      return false;
    }
    int i = Math.abs(paramMouseEvent.getX() - dndArmedEvent.getX());
    int j = Math.abs(paramMouseEvent.getY() - dndArmedEvent.getY());
    if ((i > motionThreshold) || (j > motionThreshold))
    {
      TransferHandler localTransferHandler = component.getTransferHandler();
      int k = mapDragOperationFromModifiers(paramMouseEvent, localTransferHandler);
      if (k != 0)
      {
        if (paramBeforeDrag != null) {
          paramBeforeDrag.dragStarting(dndArmedEvent);
        }
        localTransferHandler.exportAsDrag(component, dndArmedEvent, k);
        clearState();
      }
    }
    return true;
  }
  
  public static abstract interface BeforeDrag
  {
    public abstract void dragStarting(MouseEvent paramMouseEvent);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\plaf\basic\DragRecognitionSupport.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */