package sun.rmi.transport.tcp;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

final class MultiplexInputStream
  extends InputStream
{
  private ConnectionMultiplexer manager;
  private MultiplexConnectionInfo info;
  private byte[] buffer;
  private int present = 0;
  private int pos = 0;
  private int requested = 0;
  private boolean disconnected = false;
  private Object lock = new Object();
  private int waterMark;
  private byte[] temp = new byte[1];
  
  MultiplexInputStream(ConnectionMultiplexer paramConnectionMultiplexer, MultiplexConnectionInfo paramMultiplexConnectionInfo, int paramInt)
  {
    manager = paramConnectionMultiplexer;
    info = paramMultiplexConnectionInfo;
    buffer = new byte[paramInt];
    waterMark = (paramInt / 2);
  }
  
  public synchronized int read()
    throws IOException
  {
    int i = read(temp, 0, 1);
    if (i != 1) {
      return -1;
    }
    return temp[0] & 0xFF;
  }
  
  public synchronized int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    if (paramInt2 <= 0) {
      return 0;
    }
    int i;
    synchronized (lock)
    {
      if (pos >= present)
      {
        pos = (present = 0);
      }
      else if (pos >= waterMark)
      {
        System.arraycopy(buffer, pos, buffer, 0, present - pos);
        present -= pos;
        pos = 0;
      }
      int j = buffer.length - present;
      i = Math.max(j - requested, 0);
    }
    if (i > 0) {
      manager.sendRequest(info, i);
    }
    synchronized (lock)
    {
      requested += i;
      while ((pos >= present) && (!disconnected)) {
        try
        {
          lock.wait();
        }
        catch (InterruptedException localInterruptedException) {}
      }
      if ((disconnected) && (pos >= present)) {
        return -1;
      }
      int k = present - pos;
      if (paramInt2 < k)
      {
        System.arraycopy(buffer, pos, paramArrayOfByte, paramInt1, paramInt2);
        pos += paramInt2;
        return paramInt2;
      }
      System.arraycopy(buffer, pos, paramArrayOfByte, paramInt1, k);
      pos = (present = 0);
      return k;
    }
  }
  
  /* Error */
  public int available()
    throws IOException
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 94	sun/rmi/transport/tcp/MultiplexInputStream:lock	Ljava/lang/Object;
    //   4: dup
    //   5: astore_1
    //   6: monitorenter
    //   7: aload_0
    //   8: getfield 88	sun/rmi/transport/tcp/MultiplexInputStream:present	I
    //   11: aload_0
    //   12: getfield 87	sun/rmi/transport/tcp/MultiplexInputStream:pos	I
    //   15: isub
    //   16: aload_1
    //   17: monitorexit
    //   18: ireturn
    //   19: astore_2
    //   20: aload_1
    //   21: monitorexit
    //   22: aload_2
    //   23: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	24	0	this	MultiplexInputStream
    //   5	16	1	Ljava/lang/Object;	Object
    //   19	4	2	localObject1	Object
    // Exception table:
    //   from	to	target	type
    //   7	18	19	finally
    //   19	22	19	finally
  }
  
  public void close()
    throws IOException
  {
    manager.sendClose(info);
  }
  
  void receive(int paramInt, DataInputStream paramDataInputStream)
    throws IOException
  {
    synchronized (lock)
    {
      if ((pos > 0) && (buffer.length - present < paramInt))
      {
        System.arraycopy(buffer, pos, buffer, 0, present - pos);
        present -= pos;
        pos = 0;
      }
      if (buffer.length - present < paramInt) {
        throw new IOException("Receive buffer overflow");
      }
      paramDataInputStream.readFully(buffer, present, paramInt);
      present += paramInt;
      requested -= paramInt;
      lock.notifyAll();
    }
  }
  
  void disconnect()
  {
    synchronized (lock)
    {
      disconnected = true;
      lock.notifyAll();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\rmi\transport\tcp\MultiplexInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */