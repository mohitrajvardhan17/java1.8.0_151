package java.security;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class GuardedObject
  implements Serializable
{
  private static final long serialVersionUID = -5240450096227834308L;
  private Object object;
  private Guard guard;
  
  public GuardedObject(Object paramObject, Guard paramGuard)
  {
    guard = paramGuard;
    object = paramObject;
  }
  
  public Object getObject()
    throws SecurityException
  {
    if (guard != null) {
      guard.checkGuard(object);
    }
    return object;
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    if (guard != null) {
      guard.checkGuard(object);
    }
    paramObjectOutputStream.defaultWriteObject();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\security\GuardedObject.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */