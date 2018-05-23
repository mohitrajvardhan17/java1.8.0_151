package sun.misc;

import java.lang.ref.SoftReference;

@Deprecated
public abstract class Ref
{
  private SoftReference soft = null;
  
  public synchronized Object get()
  {
    Object localObject = check();
    if (localObject == null)
    {
      localObject = reconstitute();
      setThing(localObject);
    }
    return localObject;
  }
  
  public abstract Object reconstitute();
  
  public synchronized void flush()
  {
    SoftReference localSoftReference = soft;
    if (localSoftReference != null) {
      localSoftReference.clear();
    }
    soft = null;
  }
  
  public synchronized void setThing(Object paramObject)
  {
    flush();
    soft = new SoftReference(paramObject);
  }
  
  public synchronized Object check()
  {
    SoftReference localSoftReference = soft;
    if (localSoftReference == null) {
      return null;
    }
    return localSoftReference.get();
  }
  
  public Ref() {}
  
  public Ref(Object paramObject)
  {
    setThing(paramObject);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\misc\Ref.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */