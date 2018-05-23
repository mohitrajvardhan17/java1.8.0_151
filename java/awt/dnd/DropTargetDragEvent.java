package java.awt.dnd;

import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.util.List;

public class DropTargetDragEvent
  extends DropTargetEvent
{
  private static final long serialVersionUID = -8422265619058953682L;
  private Point location;
  private int actions;
  private int dropAction;
  
  public DropTargetDragEvent(DropTargetContext paramDropTargetContext, Point paramPoint, int paramInt1, int paramInt2)
  {
    super(paramDropTargetContext);
    if (paramPoint == null) {
      throw new NullPointerException("cursorLocn");
    }
    if ((paramInt1 != 0) && (paramInt1 != 1) && (paramInt1 != 2) && (paramInt1 != 1073741824)) {
      throw new IllegalArgumentException("dropAction" + paramInt1);
    }
    if ((paramInt2 & 0xBFFFFFFC) != 0) {
      throw new IllegalArgumentException("srcActions");
    }
    location = paramPoint;
    actions = paramInt2;
    dropAction = paramInt1;
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
  
  public void acceptDrag(int paramInt)
  {
    getDropTargetContext().acceptDrag(paramInt);
  }
  
  public void rejectDrag()
  {
    getDropTargetContext().rejectDrag();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\dnd\DropTargetDragEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */