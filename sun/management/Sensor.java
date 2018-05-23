package sun.management;

import java.lang.management.MemoryUsage;

public abstract class Sensor
{
  private Object lock;
  private String name;
  private long count;
  private boolean on;
  
  public Sensor(String paramString)
  {
    name = paramString;
    count = 0L;
    on = false;
    lock = new Object();
  }
  
  public String getName()
  {
    return name;
  }
  
  /* Error */
  public long getCount()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 67	sun/management/Sensor:lock	Ljava/lang/Object;
    //   4: dup
    //   5: astore_1
    //   6: monitorenter
    //   7: aload_0
    //   8: getfield 65	sun/management/Sensor:count	J
    //   11: aload_1
    //   12: monitorexit
    //   13: lreturn
    //   14: astore_2
    //   15: aload_1
    //   16: monitorexit
    //   17: aload_2
    //   18: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	19	0	this	Sensor
    //   5	11	1	Ljava/lang/Object;	Object
    //   14	4	2	localObject1	Object
    // Exception table:
    //   from	to	target	type
    //   7	13	14	finally
    //   14	17	14	finally
  }
  
  /* Error */
  public boolean isOn()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 67	sun/management/Sensor:lock	Ljava/lang/Object;
    //   4: dup
    //   5: astore_1
    //   6: monitorenter
    //   7: aload_0
    //   8: getfield 66	sun/management/Sensor:on	Z
    //   11: aload_1
    //   12: monitorexit
    //   13: ireturn
    //   14: astore_2
    //   15: aload_1
    //   16: monitorexit
    //   17: aload_2
    //   18: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	19	0	this	Sensor
    //   5	11	1	Ljava/lang/Object;	Object
    //   14	4	2	localObject1	Object
    // Exception table:
    //   from	to	target	type
    //   7	13	14	finally
    //   14	17	14	finally
  }
  
  public void trigger()
  {
    synchronized (lock)
    {
      on = true;
      count += 1L;
    }
    triggerAction();
  }
  
  public void trigger(int paramInt)
  {
    synchronized (lock)
    {
      on = true;
      count += paramInt;
    }
    triggerAction();
  }
  
  public void trigger(int paramInt, MemoryUsage paramMemoryUsage)
  {
    synchronized (lock)
    {
      on = true;
      count += paramInt;
    }
    triggerAction(paramMemoryUsage);
  }
  
  public void clear()
  {
    synchronized (lock)
    {
      on = false;
    }
    clearAction();
  }
  
  public void clear(int paramInt)
  {
    synchronized (lock)
    {
      on = false;
      count += paramInt;
    }
    clearAction();
  }
  
  public String toString()
  {
    return "Sensor - " + getName() + (isOn() ? " on " : " off ") + " count = " + getCount();
  }
  
  abstract void triggerAction();
  
  abstract void triggerAction(MemoryUsage paramMemoryUsage);
  
  abstract void clearAction();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\management\Sensor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */