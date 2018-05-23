package sun.misc;

public final class ConditionLock
  extends Lock
{
  private int state = 0;
  
  public ConditionLock() {}
  
  public ConditionLock(int paramInt)
  {
    state = paramInt;
  }
  
  public synchronized void lockWhen(int paramInt)
    throws InterruptedException
  {
    while (state != paramInt) {
      wait();
    }
    lock();
  }
  
  public synchronized void unlockWith(int paramInt)
  {
    state = paramInt;
    unlock();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\misc\ConditionLock.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */