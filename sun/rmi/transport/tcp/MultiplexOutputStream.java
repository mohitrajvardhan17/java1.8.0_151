package sun.rmi.transport.tcp;

import java.io.IOException;
import java.io.OutputStream;

final class MultiplexOutputStream
  extends OutputStream
{
  private ConnectionMultiplexer manager;
  private MultiplexConnectionInfo info;
  private byte[] buffer;
  private int pos = 0;
  private int requested = 0;
  private boolean disconnected = false;
  private Object lock = new Object();
  
  MultiplexOutputStream(ConnectionMultiplexer paramConnectionMultiplexer, MultiplexConnectionInfo paramMultiplexConnectionInfo, int paramInt)
  {
    manager = paramConnectionMultiplexer;
    info = paramMultiplexConnectionInfo;
    buffer = new byte[paramInt];
    pos = 0;
  }
  
  public synchronized void write(int paramInt)
    throws IOException
  {
    while (pos >= buffer.length) {
      push();
    }
    buffer[(pos++)] = ((byte)paramInt);
  }
  
  public synchronized void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    if (paramInt2 <= 0) {
      return;
    }
    int i = buffer.length - pos;
    if (paramInt2 <= i)
    {
      System.arraycopy(paramArrayOfByte, paramInt1, buffer, pos, paramInt2);
      pos += paramInt2;
      return;
    }
    flush();
    for (;;)
    {
      int j;
      synchronized (lock)
      {
        if (((j = requested) < 1) && (!disconnected))
        {
          try
          {
            lock.wait();
          }
          catch (InterruptedException localInterruptedException) {}
          continue;
        }
        if (disconnected) {
          throw new IOException("Connection closed");
        }
      }
      if (j >= paramInt2) {
        break;
      }
      manager.sendTransmit(info, paramArrayOfByte, paramInt1, j);
      paramInt1 += j;
      paramInt2 -= j;
      synchronized (lock)
      {
        requested -= j;
      }
    }
    manager.sendTransmit(info, paramArrayOfByte, paramInt1, paramInt2);
    synchronized (lock)
    {
      requested -= paramInt2;
    }
  }
  
  public synchronized void flush()
    throws IOException
  {
    while (pos > 0) {
      push();
    }
  }
  
  public void close()
    throws IOException
  {
    manager.sendClose(info);
  }
  
  void request(int paramInt)
  {
    synchronized (lock)
    {
      requested += paramInt;
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
  
  private void push()
    throws IOException
  {
    int i;
    synchronized (lock)
    {
      while (((i = requested) < 1) && (!disconnected)) {
        try
        {
          lock.wait();
        }
        catch (InterruptedException localInterruptedException) {}
      }
      if (disconnected) {
        throw new IOException("Connection closed");
      }
    }
    if (i < pos)
    {
      manager.sendTransmit(info, buffer, 0, i);
      System.arraycopy(buffer, i, buffer, 0, pos - i);
      pos -= i;
      synchronized (lock)
      {
        requested -= i;
      }
    }
    else
    {
      manager.sendTransmit(info, buffer, 0, pos);
      synchronized (lock)
      {
        requested -= pos;
      }
      pos = 0;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\rmi\transport\tcp\MultiplexOutputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */