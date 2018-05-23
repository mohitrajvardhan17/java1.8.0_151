package java.nio.channels.spi;

import java.nio.channels.SelectionKey;

public abstract class AbstractSelectionKey
  extends SelectionKey
{
  private volatile boolean valid = true;
  
  protected AbstractSelectionKey() {}
  
  public final boolean isValid()
  {
    return valid;
  }
  
  void invalidate()
  {
    valid = false;
  }
  
  public final void cancel()
  {
    synchronized (this)
    {
      if (valid)
      {
        valid = false;
        ((AbstractSelector)selector()).cancel(this);
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\nio\channels\spi\AbstractSelectionKey.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */