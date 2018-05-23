package jdk.management.resource;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;
import jdk.management.resource.internal.ResourceIdImpl;

public class SimpleMeter
  implements ResourceMeter, ResourceRequest
{
  private final ResourceType type;
  private final AtomicLong value;
  private final AtomicLong allocated;
  private final ResourceRequest parent;
  
  public static SimpleMeter create(ResourceType paramResourceType)
  {
    return new SimpleMeter(paramResourceType, null);
  }
  
  public static SimpleMeter create(ResourceType paramResourceType, ResourceRequest paramResourceRequest)
  {
    return new SimpleMeter(paramResourceType, paramResourceRequest);
  }
  
  protected SimpleMeter(ResourceType paramResourceType, ResourceRequest paramResourceRequest)
  {
    type = ((ResourceType)Objects.requireNonNull(paramResourceType, "type"));
    parent = paramResourceRequest;
    value = new AtomicLong();
    allocated = new AtomicLong();
  }
  
  public final long getValue()
  {
    return value.get();
  }
  
  public final long getAllocated()
  {
    return allocated.get();
  }
  
  public final ResourceType getType()
  {
    return type;
  }
  
  public final ResourceRequest getParent()
  {
    return parent;
  }
  
  public final long request(long paramLong, ResourceId paramResourceId)
  {
    if (paramLong == 0L)
    {
      Object localObject1 = null;
      if ((paramResourceId == null) || (!(paramResourceId instanceof ResourceIdImpl)) || (!((ResourceIdImpl)paramResourceId).isForcedUpdate())) {
        return 0L;
      }
    }
    long l1 = 0L;
    long l2;
    if (paramLong > 0L)
    {
      try
      {
        l2 = value.getAndAdd(paramLong);
        l1 = validate(l2, paramLong, paramResourceId);
      }
      finally
      {
        long l4 = paramLong - l1;
        if (l4 != 0L) {
          value.getAndAdd(-l4);
        }
      }
    }
    else
    {
      l2 = getValue();
      l1 = validate(l2, paramLong, paramResourceId);
      value.getAndAdd(l1);
    }
    if (parent != null)
    {
      l2 = l1;
      l1 = 0L;
      try
      {
        l1 = parent.request(l2, paramResourceId);
      }
      finally
      {
        long l3;
        long l5 = l2 - l1;
        if (l5 != 0L) {
          value.getAndAdd(-l5);
        }
      }
    }
    if (l1 > 0L) {
      allocated.getAndAdd(l1);
    }
    return l1;
  }
  
  protected long validate(long paramLong1, long paramLong2, ResourceId paramResourceId)
    throws ResourceRequestDeniedException
  {
    return paramLong2;
  }
  
  public String toString()
  {
    long l1 = value.get();
    long l2 = allocated.get();
    return type.toString() + ": " + Long.toString(l1) + "/" + l2;
  }
  
  public final int hashCode()
  {
    return super.hashCode();
  }
  
  public final boolean equals(Object paramObject)
  {
    return super.equals(paramObject);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\jdk\management\resource\SimpleMeter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */