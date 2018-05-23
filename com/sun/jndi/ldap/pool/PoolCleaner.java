package com.sun.jndi.ldap.pool;

public final class PoolCleaner
  extends Thread
{
  private final Pool[] pools;
  private final long period;
  
  public PoolCleaner(long paramLong, Pool[] paramArrayOfPool)
  {
    period = paramLong;
    pools = ((Pool[])paramArrayOfPool.clone());
    setDaemon(true);
  }
  
  public void run()
  {
    for (;;)
    {
      synchronized (this)
      {
        try
        {
          wait(period);
        }
        catch (InterruptedException localInterruptedException) {}
        long l = System.currentTimeMillis() - period;
        int i = 0;
        if (i < pools.length)
        {
          if (pools[i] != null) {
            pools[i].expire(l);
          }
          i++;
        }
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jndi\ldap\pool\PoolCleaner.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */