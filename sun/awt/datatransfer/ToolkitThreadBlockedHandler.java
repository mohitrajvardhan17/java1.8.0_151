package sun.awt.datatransfer;

public abstract interface ToolkitThreadBlockedHandler
{
  public abstract void lock();
  
  public abstract void unlock();
  
  public abstract void enter();
  
  public abstract void exit();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\awt\datatransfer\ToolkitThreadBlockedHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */