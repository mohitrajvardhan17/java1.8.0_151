package java.rmi.activation;

import java.io.Serializable;
import java.rmi.MarshalledObject;

public final class ActivationDesc
  implements Serializable
{
  private ActivationGroupID groupID;
  private String className;
  private String location;
  private MarshalledObject<?> data;
  private boolean restart;
  private static final long serialVersionUID = 7455834104417690957L;
  
  public ActivationDesc(String paramString1, String paramString2, MarshalledObject<?> paramMarshalledObject)
    throws ActivationException
  {
    this(ActivationGroup.internalCurrentGroupID(), paramString1, paramString2, paramMarshalledObject, false);
  }
  
  public ActivationDesc(String paramString1, String paramString2, MarshalledObject<?> paramMarshalledObject, boolean paramBoolean)
    throws ActivationException
  {
    this(ActivationGroup.internalCurrentGroupID(), paramString1, paramString2, paramMarshalledObject, paramBoolean);
  }
  
  public ActivationDesc(ActivationGroupID paramActivationGroupID, String paramString1, String paramString2, MarshalledObject<?> paramMarshalledObject)
  {
    this(paramActivationGroupID, paramString1, paramString2, paramMarshalledObject, false);
  }
  
  public ActivationDesc(ActivationGroupID paramActivationGroupID, String paramString1, String paramString2, MarshalledObject<?> paramMarshalledObject, boolean paramBoolean)
  {
    if (paramActivationGroupID == null) {
      throw new IllegalArgumentException("groupID can't be null");
    }
    groupID = paramActivationGroupID;
    className = paramString1;
    location = paramString2;
    data = paramMarshalledObject;
    restart = paramBoolean;
  }
  
  public ActivationGroupID getGroupID()
  {
    return groupID;
  }
  
  public String getClassName()
  {
    return className;
  }
  
  public String getLocation()
  {
    return location;
  }
  
  public MarshalledObject<?> getData()
  {
    return data;
  }
  
  public boolean getRestartMode()
  {
    return restart;
  }
  
  public boolean equals(Object paramObject)
  {
    if ((paramObject instanceof ActivationDesc))
    {
      ActivationDesc localActivationDesc = (ActivationDesc)paramObject;
      return (groupID == null ? groupID == null : groupID.equals(groupID)) && (className == null ? className == null : className.equals(className)) && (location == null ? location == null : location.equals(location)) && (data == null ? data == null : data.equals(data)) && (restart == restart);
    }
    return false;
  }
  
  public int hashCode()
  {
    return (location == null ? 0 : location.hashCode() << 24) ^ (groupID == null ? 0 : groupID.hashCode() << 16) ^ (className == null ? 0 : className.hashCode() << 9) ^ (data == null ? 0 : data.hashCode() << 1) ^ (restart ? 1 : 0);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\rmi\activation\ActivationDesc.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */