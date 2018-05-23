package sun.awt.windows;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.io.NotSerializableException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import sun.awt.datatransfer.DataTransferer;
import sun.awt.datatransfer.SunClipboard;

final class WClipboard
  extends SunClipboard
{
  private boolean isClipboardViewerRegistered;
  
  WClipboard()
  {
    super("System");
  }
  
  public long getID()
  {
    return 0L;
  }
  
  protected void setContentsNative(Transferable paramTransferable)
  {
    SortedMap localSortedMap = WDataTransferer.getInstance().getFormatsForTransferable(paramTransferable, getDefaultFlavorTable());
    openClipboard(this);
    try
    {
      Iterator localIterator = localSortedMap.keySet().iterator();
      while (localIterator.hasNext())
      {
        Long localLong = (Long)localIterator.next();
        DataFlavor localDataFlavor = (DataFlavor)localSortedMap.get(localLong);
        try
        {
          byte[] arrayOfByte = WDataTransferer.getInstance().translateTransferable(paramTransferable, localDataFlavor, localLong.longValue());
          publishClipboardData(localLong.longValue(), arrayOfByte);
        }
        catch (IOException localIOException)
        {
          if ((localDataFlavor.isMimeTypeEqual("application/x-java-jvm-local-objectref")) && ((localIOException instanceof NotSerializableException))) {}
        }
      }
    }
    finally
    {
      closeClipboard();
    }
  }
  
  private void lostSelectionOwnershipImpl()
  {
    lostOwnershipImpl();
  }
  
  protected void clearNativeContext() {}
  
  public native void openClipboard(SunClipboard paramSunClipboard)
    throws IllegalStateException;
  
  public native void closeClipboard();
  
  private native void publishClipboardData(long paramLong, byte[] paramArrayOfByte);
  
  private static native void init();
  
  protected native long[] getClipboardFormats();
  
  protected native byte[] getClipboardData(long paramLong)
    throws IOException;
  
  protected void registerClipboardViewerChecked()
  {
    if (!isClipboardViewerRegistered)
    {
      registerClipboardViewer();
      isClipboardViewerRegistered = true;
    }
  }
  
  private native void registerClipboardViewer();
  
  protected void unregisterClipboardViewerChecked() {}
  
  /* Error */
  private void handleContentsChanged()
  {
    // Byte code:
    //   0: aload_0
    //   1: invokevirtual 136	sun/awt/windows/WClipboard:areFlavorListenersRegistered	()Z
    //   4: ifne +4 -> 8
    //   7: return
    //   8: aconst_null
    //   9: astore_1
    //   10: aload_0
    //   11: aconst_null
    //   12: invokevirtual 142	sun/awt/windows/WClipboard:openClipboard	(Lsun/awt/datatransfer/SunClipboard;)V
    //   15: aload_0
    //   16: invokevirtual 137	sun/awt/windows/WClipboard:getClipboardFormats	()[J
    //   19: astore_1
    //   20: aload_0
    //   21: invokevirtual 132	sun/awt/windows/WClipboard:closeClipboard	()V
    //   24: goto +18 -> 42
    //   27: astore_2
    //   28: aload_0
    //   29: invokevirtual 132	sun/awt/windows/WClipboard:closeClipboard	()V
    //   32: goto +10 -> 42
    //   35: astore_3
    //   36: aload_0
    //   37: invokevirtual 132	sun/awt/windows/WClipboard:closeClipboard	()V
    //   40: aload_3
    //   41: athrow
    //   42: aload_0
    //   43: aload_1
    //   44: invokevirtual 140	sun/awt/windows/WClipboard:checkChange	([J)V
    //   47: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	48	0	this	WClipboard
    //   9	35	1	arrayOfLong	long[]
    //   27	1	2	localIllegalStateException	IllegalStateException
    //   35	6	3	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   10	20	27	java/lang/IllegalStateException
    //   10	20	35	finally
  }
  
  protected Transferable createLocaleTransferable(long[] paramArrayOfLong)
    throws IOException
  {
    int i = 0;
    for (int j = 0; j < paramArrayOfLong.length; j++) {
      if (paramArrayOfLong[j] == 16L)
      {
        i = 1;
        break;
      }
    }
    if (i == 0) {
      return null;
    }
    byte[] arrayOfByte1 = null;
    try
    {
      arrayOfByte1 = getClipboardData(16L);
    }
    catch (IOException localIOException)
    {
      return null;
    }
    final byte[] arrayOfByte2 = arrayOfByte1;
    new Transferable()
    {
      public DataFlavor[] getTransferDataFlavors()
      {
        return new DataFlavor[] { DataTransferer.javaTextEncodingFlavor };
      }
      
      public boolean isDataFlavorSupported(DataFlavor paramAnonymousDataFlavor)
      {
        return paramAnonymousDataFlavor.equals(DataTransferer.javaTextEncodingFlavor);
      }
      
      public Object getTransferData(DataFlavor paramAnonymousDataFlavor)
        throws UnsupportedFlavorException
      {
        if (isDataFlavorSupported(paramAnonymousDataFlavor)) {
          return arrayOfByte2;
        }
        throw new UnsupportedFlavorException(paramAnonymousDataFlavor);
      }
    };
  }
  
  static {}
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\awt\windows\WClipboard.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */