package java.rmi.activation;

import java.io.Serializable;
import java.rmi.server.UID;

public class ActivationGroupID
  implements Serializable
{
  private ActivationSystem system;
  private UID uid = new UID();
  private static final long serialVersionUID = -1648432278909740833L;
  
  public ActivationGroupID(ActivationSystem paramActivationSystem)
  {
    system = paramActivationSystem;
  }
  
  public ActivationSystem getSystem()
  {
    return system;
  }
  
  public int hashCode()
  {
    return uid.hashCode();
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if ((paramObject instanceof ActivationGroupID))
    {
      ActivationGroupID localActivationGroupID = (ActivationGroupID)paramObject;
      return (uid.equals(uid)) && (system.equals(system));
    }
    return false;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\rmi\activation\ActivationGroupID.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */