package java.rmi.server;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.security.AccessController;
import java.security.SecureRandom;
import java.util.concurrent.atomic.AtomicLong;
import sun.security.action.GetPropertyAction;

public final class ObjID
  implements Serializable
{
  public static final int REGISTRY_ID = 0;
  public static final int ACTIVATOR_ID = 1;
  public static final int DGC_ID = 2;
  private static final long serialVersionUID = -6386392263968365220L;
  private static final AtomicLong nextObjNum = new AtomicLong(0L);
  private static final UID mySpace = new UID();
  private static final SecureRandom secureRandom = new SecureRandom();
  private final long objNum;
  private final UID space;
  
  public ObjID()
  {
    if (useRandomIDs())
    {
      space = new UID();
      objNum = secureRandom.nextLong();
    }
    else
    {
      space = mySpace;
      objNum = nextObjNum.getAndIncrement();
    }
  }
  
  public ObjID(int paramInt)
  {
    space = new UID((short)0);
    objNum = paramInt;
  }
  
  private ObjID(long paramLong, UID paramUID)
  {
    objNum = paramLong;
    space = paramUID;
  }
  
  public void write(ObjectOutput paramObjectOutput)
    throws IOException
  {
    paramObjectOutput.writeLong(objNum);
    space.write(paramObjectOutput);
  }
  
  public static ObjID read(ObjectInput paramObjectInput)
    throws IOException
  {
    long l = paramObjectInput.readLong();
    UID localUID = UID.read(paramObjectInput);
    return new ObjID(l, localUID);
  }
  
  public int hashCode()
  {
    return (int)objNum;
  }
  
  public boolean equals(Object paramObject)
  {
    if ((paramObject instanceof ObjID))
    {
      ObjID localObjID = (ObjID)paramObject;
      return (objNum == objNum) && (space.equals(space));
    }
    return false;
  }
  
  public String toString()
  {
    return "[" + (space.equals(mySpace) ? "" : new StringBuilder().append(space).append(", ").toString()) + objNum + "]";
  }
  
  private static boolean useRandomIDs()
  {
    String str = (String)AccessController.doPrivileged(new GetPropertyAction("java.rmi.server.randomIDs"));
    return str == null ? true : Boolean.parseBoolean(str);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\rmi\server\ObjID.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */