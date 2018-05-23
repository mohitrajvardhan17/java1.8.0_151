package java.awt.dnd.peer;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DropTarget;
import java.awt.dnd.InvalidDnDOperationException;

public abstract interface DropTargetContextPeer
{
  public abstract void setTargetActions(int paramInt);
  
  public abstract int getTargetActions();
  
  public abstract DropTarget getDropTarget();
  
  public abstract DataFlavor[] getTransferDataFlavors();
  
  public abstract Transferable getTransferable()
    throws InvalidDnDOperationException;
  
  public abstract boolean isTransferableJVMLocal();
  
  public abstract void acceptDrag(int paramInt);
  
  public abstract void rejectDrag();
  
  public abstract void acceptDrop(int paramInt);
  
  public abstract void rejectDrop();
  
  public abstract void dropComplete(boolean paramBoolean);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\dnd\peer\DropTargetContextPeer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */