package java.awt.dnd;

import java.awt.Component;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.peer.DropTargetContextPeer;
import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import sun.awt.datatransfer.TransferableProxy;

public class DropTargetContext
  implements Serializable
{
  private static final long serialVersionUID = -634158968993743371L;
  private DropTarget dropTarget;
  private transient DropTargetContextPeer dropTargetContextPeer;
  private transient Transferable transferable;
  
  DropTargetContext(DropTarget paramDropTarget)
  {
    dropTarget = paramDropTarget;
  }
  
  public DropTarget getDropTarget()
  {
    return dropTarget;
  }
  
  public Component getComponent()
  {
    return dropTarget.getComponent();
  }
  
  public void addNotify(DropTargetContextPeer paramDropTargetContextPeer)
  {
    dropTargetContextPeer = paramDropTargetContextPeer;
  }
  
  public void removeNotify()
  {
    dropTargetContextPeer = null;
    transferable = null;
  }
  
  protected void setTargetActions(int paramInt)
  {
    DropTargetContextPeer localDropTargetContextPeer = getDropTargetContextPeer();
    if (localDropTargetContextPeer != null) {
      synchronized (localDropTargetContextPeer)
      {
        localDropTargetContextPeer.setTargetActions(paramInt);
        getDropTarget().doSetDefaultActions(paramInt);
      }
    } else {
      getDropTarget().doSetDefaultActions(paramInt);
    }
  }
  
  protected int getTargetActions()
  {
    DropTargetContextPeer localDropTargetContextPeer = getDropTargetContextPeer();
    return localDropTargetContextPeer != null ? localDropTargetContextPeer.getTargetActions() : dropTarget.getDefaultActions();
  }
  
  public void dropComplete(boolean paramBoolean)
    throws InvalidDnDOperationException
  {
    DropTargetContextPeer localDropTargetContextPeer = getDropTargetContextPeer();
    if (localDropTargetContextPeer != null) {
      localDropTargetContextPeer.dropComplete(paramBoolean);
    }
  }
  
  protected void acceptDrag(int paramInt)
  {
    DropTargetContextPeer localDropTargetContextPeer = getDropTargetContextPeer();
    if (localDropTargetContextPeer != null) {
      localDropTargetContextPeer.acceptDrag(paramInt);
    }
  }
  
  protected void rejectDrag()
  {
    DropTargetContextPeer localDropTargetContextPeer = getDropTargetContextPeer();
    if (localDropTargetContextPeer != null) {
      localDropTargetContextPeer.rejectDrag();
    }
  }
  
  protected void acceptDrop(int paramInt)
  {
    DropTargetContextPeer localDropTargetContextPeer = getDropTargetContextPeer();
    if (localDropTargetContextPeer != null) {
      localDropTargetContextPeer.acceptDrop(paramInt);
    }
  }
  
  protected void rejectDrop()
  {
    DropTargetContextPeer localDropTargetContextPeer = getDropTargetContextPeer();
    if (localDropTargetContextPeer != null) {
      localDropTargetContextPeer.rejectDrop();
    }
  }
  
  protected DataFlavor[] getCurrentDataFlavors()
  {
    DropTargetContextPeer localDropTargetContextPeer = getDropTargetContextPeer();
    return localDropTargetContextPeer != null ? localDropTargetContextPeer.getTransferDataFlavors() : new DataFlavor[0];
  }
  
  protected List<DataFlavor> getCurrentDataFlavorsAsList()
  {
    return Arrays.asList(getCurrentDataFlavors());
  }
  
  protected boolean isDataFlavorSupported(DataFlavor paramDataFlavor)
  {
    return getCurrentDataFlavorsAsList().contains(paramDataFlavor);
  }
  
  protected Transferable getTransferable()
    throws InvalidDnDOperationException
  {
    DropTargetContextPeer localDropTargetContextPeer = getDropTargetContextPeer();
    if (localDropTargetContextPeer == null) {
      throw new InvalidDnDOperationException();
    }
    if (transferable == null)
    {
      Transferable localTransferable = localDropTargetContextPeer.getTransferable();
      boolean bool = localDropTargetContextPeer.isTransferableJVMLocal();
      synchronized (this)
      {
        if (transferable == null) {
          transferable = createTransferableProxy(localTransferable, bool);
        }
      }
    }
    return transferable;
  }
  
  DropTargetContextPeer getDropTargetContextPeer()
  {
    return dropTargetContextPeer;
  }
  
  protected Transferable createTransferableProxy(Transferable paramTransferable, boolean paramBoolean)
  {
    return new TransferableProxy(paramTransferable, paramBoolean);
  }
  
  protected class TransferableProxy
    implements Transferable
  {
    protected Transferable transferable;
    protected boolean isLocal;
    private TransferableProxy proxy;
    
    TransferableProxy(Transferable paramTransferable, boolean paramBoolean)
    {
      proxy = new TransferableProxy(paramTransferable, paramBoolean);
      transferable = paramTransferable;
      isLocal = paramBoolean;
    }
    
    public DataFlavor[] getTransferDataFlavors()
    {
      return proxy.getTransferDataFlavors();
    }
    
    public boolean isDataFlavorSupported(DataFlavor paramDataFlavor)
    {
      return proxy.isDataFlavorSupported(paramDataFlavor);
    }
    
    public Object getTransferData(DataFlavor paramDataFlavor)
      throws UnsupportedFlavorException, IOException
    {
      return proxy.getTransferData(paramDataFlavor);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\dnd\DropTargetContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */