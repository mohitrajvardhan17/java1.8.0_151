package java.io;

final class SerialCallbackContext
{
  private final Object obj;
  private final ObjectStreamClass desc;
  private Thread thread;
  
  public SerialCallbackContext(Object paramObject, ObjectStreamClass paramObjectStreamClass)
  {
    obj = paramObject;
    desc = paramObjectStreamClass;
    thread = Thread.currentThread();
  }
  
  public Object getObj()
    throws NotActiveException
  {
    checkAndSetUsed();
    return obj;
  }
  
  public ObjectStreamClass getDesc()
  {
    return desc;
  }
  
  public void check()
    throws NotActiveException
  {
    if ((thread != null) && (thread != Thread.currentThread())) {
      throw new NotActiveException("expected thread: " + thread + ", but got: " + Thread.currentThread());
    }
  }
  
  private void checkAndSetUsed()
    throws NotActiveException
  {
    if (thread != Thread.currentThread()) {
      throw new NotActiveException("not in readObject invocation or fields already read");
    }
    thread = null;
  }
  
  public void setUsed()
  {
    thread = null;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\io\SerialCallbackContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */