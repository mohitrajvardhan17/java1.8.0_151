package jdk.management.resource;

import jdk.management.resource.internal.ResourceIdImpl;

public class NotifyingMeter
  extends SimpleMeter
{
  private final ResourceApprover approver;
  private long granularity;
  
  public static NotifyingMeter create(ResourceType paramResourceType, ResourceApprover paramResourceApprover)
  {
    return new NotifyingMeter(paramResourceType, null, paramResourceApprover);
  }
  
  public static NotifyingMeter create(ResourceType paramResourceType, ResourceRequest paramResourceRequest, ResourceApprover paramResourceApprover)
  {
    return new NotifyingMeter(paramResourceType, paramResourceRequest, paramResourceApprover);
  }
  
  protected NotifyingMeter(ResourceType paramResourceType, ResourceRequest paramResourceRequest, ResourceApprover paramResourceApprover)
  {
    super(paramResourceType, paramResourceRequest);
    approver = paramResourceApprover;
    granularity = 1L;
  }
  
  protected long validate(long paramLong1, long paramLong2, ResourceId paramResourceId)
  {
    long l1 = paramLong2;
    if (approver != null)
    {
      long l2 = Math.floorDiv(paramLong1, granularity);
      long l3 = Math.floorDiv(paramLong1 + paramLong2, granularity);
      if ((l2 != l3) || ((paramLong2 == 0L) && (paramResourceId != null) && ((paramResourceId instanceof ResourceIdImpl)) && (((ResourceIdImpl)paramResourceId).isForcedUpdate())))
      {
        l1 = approver.request(this, paramLong1, paramLong2, paramResourceId);
        if ((l1 != paramLong2) && (l1 != 0L)) {
          l1 = paramLong2;
        }
      }
    }
    return l1;
  }
  
  public final synchronized long getGranularity()
  {
    return granularity;
  }
  
  public final long setGranularity(long paramLong)
  {
    return setGranularityInternal(paramLong);
  }
  
  synchronized long setGranularityInternal(long paramLong)
  {
    if (paramLong <= 0L) {
      throw new IllegalArgumentException("granularity must be greater than zero");
    }
    long l = granularity;
    granularity = paramLong;
    return l;
  }
  
  public final ResourceApprover getApprover()
  {
    return approver;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\jdk\management\resource\NotifyingMeter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */