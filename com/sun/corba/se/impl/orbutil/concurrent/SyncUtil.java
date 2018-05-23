package com.sun.corba.se.impl.orbutil.concurrent;

public class SyncUtil
{
  private SyncUtil() {}
  
  public static void acquire(Sync paramSync)
  {
    int i = 0;
    while (i == 0) {
      try
      {
        paramSync.acquire();
        i = 1;
      }
      catch (InterruptedException localInterruptedException)
      {
        i = 0;
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\orbutil\concurrent\SyncUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */