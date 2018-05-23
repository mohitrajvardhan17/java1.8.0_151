package jdk.management.resource.internal;

import jdk.management.resource.ResourceRequest;
import jdk.management.resource.ResourceType;
import jdk.management.resource.SimpleMeter;

public class UnassignedContext
  extends SimpleResourceContext
{
  private static final UnassignedContext unassignedContext = new UnassignedContext("Unassigned");
  private static final UnassignedContext systemContext = new UnassignedContext("System", 0);
  
  private UnassignedContext(String paramString)
  {
    super(paramString);
  }
  
  private UnassignedContext(String paramString, int paramInt)
  {
    super(paramString, paramInt);
  }
  
  public static UnassignedContext getSystemContext()
  {
    return systemContext;
  }
  
  public static UnassignedContext getUnassignedContext()
  {
    return unassignedContext;
  }
  
  public void close() {}
  
  public ResourceRequest getResourceRequest(ResourceType paramResourceType)
  {
    ResourceRequest localResourceRequest = super.getResourceRequest(paramResourceType);
    if (localResourceRequest == null)
    {
      try
      {
        addResourceMeter(SimpleMeter.create(paramResourceType));
      }
      catch (IllegalArgumentException localIllegalArgumentException) {}
      localResourceRequest = super.getResourceRequest(paramResourceType);
    }
    return localResourceRequest;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\jdk\management\resource\internal\UnassignedContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */