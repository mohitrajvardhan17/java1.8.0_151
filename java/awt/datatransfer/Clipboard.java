package java.awt.datatransfer;

import java.awt.EventQueue;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import sun.awt.EventListenerAggregate;

public class Clipboard
{
  String name;
  protected ClipboardOwner owner;
  protected Transferable contents;
  private EventListenerAggregate flavorListeners;
  private Set<DataFlavor> currentDataFlavors;
  
  public Clipboard(String paramString)
  {
    name = paramString;
  }
  
  public String getName()
  {
    return name;
  }
  
  public synchronized void setContents(Transferable paramTransferable, ClipboardOwner paramClipboardOwner)
  {
    final ClipboardOwner localClipboardOwner = owner;
    final Transferable localTransferable = contents;
    owner = paramClipboardOwner;
    contents = paramTransferable;
    if ((localClipboardOwner != null) && (localClipboardOwner != paramClipboardOwner)) {
      EventQueue.invokeLater(new Runnable()
      {
        public void run()
        {
          localClipboardOwner.lostOwnership(Clipboard.this, localTransferable);
        }
      });
    }
    fireFlavorsChanged();
  }
  
  public synchronized Transferable getContents(Object paramObject)
  {
    return contents;
  }
  
  public DataFlavor[] getAvailableDataFlavors()
  {
    Transferable localTransferable = getContents(null);
    if (localTransferable == null) {
      return new DataFlavor[0];
    }
    return localTransferable.getTransferDataFlavors();
  }
  
  public boolean isDataFlavorAvailable(DataFlavor paramDataFlavor)
  {
    if (paramDataFlavor == null) {
      throw new NullPointerException("flavor");
    }
    Transferable localTransferable = getContents(null);
    if (localTransferable == null) {
      return false;
    }
    return localTransferable.isDataFlavorSupported(paramDataFlavor);
  }
  
  public Object getData(DataFlavor paramDataFlavor)
    throws UnsupportedFlavorException, IOException
  {
    if (paramDataFlavor == null) {
      throw new NullPointerException("flavor");
    }
    Transferable localTransferable = getContents(null);
    if (localTransferable == null) {
      throw new UnsupportedFlavorException(paramDataFlavor);
    }
    return localTransferable.getTransferData(paramDataFlavor);
  }
  
  public synchronized void addFlavorListener(FlavorListener paramFlavorListener)
  {
    if (paramFlavorListener == null) {
      return;
    }
    if (flavorListeners == null)
    {
      currentDataFlavors = getAvailableDataFlavorSet();
      flavorListeners = new EventListenerAggregate(FlavorListener.class);
    }
    flavorListeners.add(paramFlavorListener);
  }
  
  public synchronized void removeFlavorListener(FlavorListener paramFlavorListener)
  {
    if ((paramFlavorListener == null) || (flavorListeners == null)) {
      return;
    }
    flavorListeners.remove(paramFlavorListener);
  }
  
  public synchronized FlavorListener[] getFlavorListeners()
  {
    return flavorListeners == null ? new FlavorListener[0] : (FlavorListener[])flavorListeners.getListenersCopy();
  }
  
  private void fireFlavorsChanged()
  {
    if (flavorListeners == null) {
      return;
    }
    Set localSet = currentDataFlavors;
    currentDataFlavors = getAvailableDataFlavorSet();
    if (localSet.equals(currentDataFlavors)) {
      return;
    }
    FlavorListener[] arrayOfFlavorListener = (FlavorListener[])flavorListeners.getListenersInternal();
    for (int i = 0; i < arrayOfFlavorListener.length; i++)
    {
      final FlavorListener localFlavorListener = arrayOfFlavorListener[i];
      EventQueue.invokeLater(new Runnable()
      {
        public void run()
        {
          localFlavorListener.flavorsChanged(new FlavorEvent(Clipboard.this));
        }
      });
    }
  }
  
  private Set<DataFlavor> getAvailableDataFlavorSet()
  {
    HashSet localHashSet = new HashSet();
    Transferable localTransferable = getContents(null);
    if (localTransferable != null)
    {
      DataFlavor[] arrayOfDataFlavor = localTransferable.getTransferDataFlavors();
      if (arrayOfDataFlavor != null) {
        localHashSet.addAll(Arrays.asList(arrayOfDataFlavor));
      }
    }
    return localHashSet;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\datatransfer\Clipboard.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */