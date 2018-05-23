package java.rmi.server;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.Serializable;
import java.security.SecureRandom;

public final class UID
  implements Serializable
{
  private static int hostUnique;
  private static boolean hostUniqueSet = false;
  private static final Object lock = new Object();
  private static long lastTime = System.currentTimeMillis();
  private static short lastCount = Short.MIN_VALUE;
  private static final long serialVersionUID = 1086053664494604050L;
  private final int unique;
  private final long time;
  private final short count;
  
  public UID()
  {
    synchronized (lock)
    {
      if (!hostUniqueSet)
      {
        hostUnique = new SecureRandom().nextInt();
        hostUniqueSet = true;
      }
      unique = hostUnique;
      if (lastCount == Short.MAX_VALUE)
      {
        boolean bool = Thread.interrupted();
        for (int i = 0; i == 0; i = 1)
        {
          long l = System.currentTimeMillis();
          if (l == lastTime)
          {
            try
            {
              Thread.sleep(1L);
            }
            catch (InterruptedException localInterruptedException)
            {
              bool = true;
            }
          }
          else
          {
            lastTime = l < lastTime ? lastTime + 1L : l;
            lastCount = Short.MIN_VALUE;
          }
        }
        if (bool) {
          Thread.currentThread().interrupt();
        }
      }
      time = lastTime;
      count = (lastCount++);
    }
  }
  
  public UID(short paramShort)
  {
    unique = 0;
    time = 0L;
    count = paramShort;
  }
  
  private UID(int paramInt, long paramLong, short paramShort)
  {
    unique = paramInt;
    time = paramLong;
    count = paramShort;
  }
  
  public int hashCode()
  {
    return (int)time + count;
  }
  
  public boolean equals(Object paramObject)
  {
    if ((paramObject instanceof UID))
    {
      UID localUID = (UID)paramObject;
      return (unique == unique) && (count == count) && (time == time);
    }
    return false;
  }
  
  public String toString()
  {
    return Integer.toString(unique, 16) + ":" + Long.toString(time, 16) + ":" + Integer.toString(count, 16);
  }
  
  public void write(DataOutput paramDataOutput)
    throws IOException
  {
    paramDataOutput.writeInt(unique);
    paramDataOutput.writeLong(time);
    paramDataOutput.writeShort(count);
  }
  
  public static UID read(DataInput paramDataInput)
    throws IOException
  {
    int i = paramDataInput.readInt();
    long l = paramDataInput.readLong();
    short s = paramDataInput.readShort();
    return new UID(i, l, s);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\rmi\server\UID.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */