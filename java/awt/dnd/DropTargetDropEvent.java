package java.awt.dnd;

import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.util.List;

public class DropTargetDropEvent
  extends DropTargetEvent
{
  private static final long serialVersionUID = -1721911170440459322L;
  private static final Point zero = new Point(0, 0);
  private Point location = zero;
  private int actions = 0;
  private int dropAction = 0;
  private boolean isLocalTx = false;
  
  public DropTargetDropEvent(DropTargetContext paramDropTargetContext, Point paramPoint, int paramInt1, int paramInt2)
  {
    super(paramDropTargetContext);
    if (paramPoint == null) {
      throw new NullPointerException("cursorLocn");
    }
    if ((paramInt1 != 0) && (paramInt1 != 1) && (paramInt1 != 2) && (paramInt1 != 1073741824)) {
      throw new IllegalArgumentException("dropAction = " + paramInt1);
    }
    if ((paramInt2 & 0xBFFFFFFC) != 0) {
      throw new IllegalArgumentException("srcActions");
    }
    location = paramPoint;
    actions = paramInt2;
    dropAction = paramInt1;
  }
  
  public DropTargetDropEvent(DropTargetContext paramDropTargetContext, Point paramPoint, int paramInt1, int paramInt2, boolean paramBoolean)
  {
    this(paramDropTargetContext, paramPoint, paramInt1, paramInt2);
    isLocalTx = paramBoolean;
  }
  
  public Point getLocation()
  {
    return location;
  }
  
  public DataFlavor[] getCurrentDataFlavors()
  {
    return getDropTargetContext().getCurrentDataFlavors();
  }
  
  public List<DataFlavor> getCurrentDataFlavorsAsList()
  {
    return getDropTargetContext().getCurrentDataFlavorsAsList();
  }
  
  public boolean isDataFlavorSupported(DataFlavor paramDataFlavor)
  {
    return getDropTargetContext().isDataFlavorSupported(paramDataFlavor);
  }
  
  public int getSourceActions()
  {
    return actions;
  }
  
  public int getDropAction()
  {
    return dropAction;
  }
  
  public Transferable getTransferable()
  {
    return getDropTargetContext().getTransferable();
  }
  
  public void acceptDrop(int paramInt)
  {
    getDropTargetContext().acceptDrop(paramInt);
  }
  
  public void rejectDrop()
  {
    getDropTargetContext().rejectDrop();
  }
  
  public void dropComplete(boolean paramBoolean)
  {
    getDropTargetContext().dropComplete(paramBoolean);
  }
  
  public boolean isLocalTransfer()
  {
    return isLocalTx;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\dnd\DropTargetDropEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */