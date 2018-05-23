package sun.awt;

public class Mutex
{
  private boolean locked;
  private Thread owner;
  
  public Mutex() {}
  
  public synchronized void lock()
  {
    if ((locked) && (Thread.currentThread() == owner)) {
      throw new IllegalMonitorStateException();
    }
    do
    {
      if (!locked)
      {
        locked = true;
        owner = Thread.currentThread();
      }
      else
      {
        try
        {
          wait();
        }
        catch (InterruptedException localInterruptedException) {}
      }
    } while (owner != Thread.currentThread());
  }
  
  public synchronized void unlock()
  {
    if (Thread.currentThread() != owner) {
      throw new IllegalMonitorStateException();
    }
    owner = null;
    locked = false;
    notify();
  }
  
  protected boolean isOwned()
  {
    return (locked) && (Thread.currentThread() == owner);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\awt\Mutex.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */