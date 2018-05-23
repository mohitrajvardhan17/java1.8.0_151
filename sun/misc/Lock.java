package sun.misc;

public class Lock
{
  private boolean locked = false;
  
  public Lock() {}
  
  public final synchronized void lock()
    throws InterruptedException
  {
    while (locked) {
      wait();
    }
    locked = true;
  }
  
  public final synchronized void unlock()
  {
    locked = false;
    notifyAll();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\misc\Lock.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */