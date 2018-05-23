package com.sun.imageio.stream;

import java.io.Closeable;
import sun.java2d.DisposerRecord;

public class CloseableDisposerRecord
  implements DisposerRecord
{
  private Closeable closeable;
  
  public CloseableDisposerRecord(Closeable paramCloseable)
  {
    closeable = paramCloseable;
  }
  
  /* Error */
  public synchronized void dispose()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 25	com/sun/imageio/stream/CloseableDisposerRecord:closeable	Ljava/io/Closeable;
    //   4: ifnull +37 -> 41
    //   7: aload_0
    //   8: getfield 25	com/sun/imageio/stream/CloseableDisposerRecord:closeable	Ljava/io/Closeable;
    //   11: invokeinterface 27 1 0
    //   16: aload_0
    //   17: aconst_null
    //   18: putfield 25	com/sun/imageio/stream/CloseableDisposerRecord:closeable	Ljava/io/Closeable;
    //   21: goto +20 -> 41
    //   24: astore_1
    //   25: aload_0
    //   26: aconst_null
    //   27: putfield 25	com/sun/imageio/stream/CloseableDisposerRecord:closeable	Ljava/io/Closeable;
    //   30: goto +11 -> 41
    //   33: astore_2
    //   34: aload_0
    //   35: aconst_null
    //   36: putfield 25	com/sun/imageio/stream/CloseableDisposerRecord:closeable	Ljava/io/Closeable;
    //   39: aload_2
    //   40: athrow
    //   41: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	42	0	this	CloseableDisposerRecord
    //   24	1	1	localIOException	java.io.IOException
    //   33	7	2	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   7	16	24	java/io/IOException
    //   7	16	33	finally
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\imageio\stream\CloseableDisposerRecord.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */