package sun.awt.datatransfer;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class ClipboardTransferable
  implements Transferable
{
  private final HashMap flavorsToData = new HashMap();
  private DataFlavor[] flavors = new DataFlavor[0];
  
  public ClipboardTransferable(SunClipboard paramSunClipboard)
  {
    paramSunClipboard.openClipboard(null);
    try
    {
      long[] arrayOfLong = paramSunClipboard.getClipboardFormats();
      if ((arrayOfLong != null) && (arrayOfLong.length > 0))
      {
        HashMap localHashMap = new HashMap(arrayOfLong.length, 1.0F);
        Map localMap = DataTransferer.getInstance().getFlavorsForFormats(arrayOfLong, SunClipboard.getDefaultFlavorTable());
        Iterator localIterator = localMap.keySet().iterator();
        while (localIterator.hasNext())
        {
          DataFlavor localDataFlavor = (DataFlavor)localIterator.next();
          Long localLong = (Long)localMap.get(localDataFlavor);
          fetchOneFlavor(paramSunClipboard, localDataFlavor, localLong, localHashMap);
        }
        DataTransferer.getInstance();
        flavors = DataTransferer.setToSortedDataFlavorArray(flavorsToData.keySet());
      }
    }
    finally
    {
      paramSunClipboard.closeClipboard();
    }
  }
  
  private boolean fetchOneFlavor(SunClipboard paramSunClipboard, DataFlavor paramDataFlavor, Long paramLong, HashMap paramHashMap)
  {
    if (!flavorsToData.containsKey(paramDataFlavor))
    {
      long l = paramLong.longValue();
      Object localObject = null;
      if (!paramHashMap.containsKey(paramLong))
      {
        try
        {
          localObject = paramSunClipboard.getClipboardData(l);
        }
        catch (IOException localIOException)
        {
          localObject = localIOException;
        }
        catch (Throwable localThrowable)
        {
          localThrowable.printStackTrace();
        }
        paramHashMap.put(paramLong, localObject);
      }
      else
      {
        localObject = paramHashMap.get(paramLong);
      }
      if ((localObject instanceof IOException))
      {
        flavorsToData.put(paramDataFlavor, localObject);
        return false;
      }
      if (localObject != null)
      {
        flavorsToData.put(paramDataFlavor, new DataFactory(l, (byte[])localObject));
        return true;
      }
    }
    return false;
  }
  
  public DataFlavor[] getTransferDataFlavors()
  {
    return (DataFlavor[])flavors.clone();
  }
  
  public boolean isDataFlavorSupported(DataFlavor paramDataFlavor)
  {
    return flavorsToData.containsKey(paramDataFlavor);
  }
  
  public Object getTransferData(DataFlavor paramDataFlavor)
    throws UnsupportedFlavorException, IOException
  {
    if (!isDataFlavorSupported(paramDataFlavor)) {
      throw new UnsupportedFlavorException(paramDataFlavor);
    }
    Object localObject = flavorsToData.get(paramDataFlavor);
    if ((localObject instanceof IOException)) {
      throw ((IOException)localObject);
    }
    if ((localObject instanceof DataFactory))
    {
      DataFactory localDataFactory = (DataFactory)localObject;
      localObject = localDataFactory.getTransferData(paramDataFlavor);
    }
    return localObject;
  }
  
  private final class DataFactory
  {
    final long format;
    final byte[] data;
    
    DataFactory(long paramLong, byte[] paramArrayOfByte)
    {
      format = paramLong;
      data = paramArrayOfByte;
    }
    
    public Object getTransferData(DataFlavor paramDataFlavor)
      throws IOException
    {
      return DataTransferer.getInstance().translateBytes(data, paramDataFlavor, format, ClipboardTransferable.this);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\awt\datatransfer\ClipboardTransferable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */