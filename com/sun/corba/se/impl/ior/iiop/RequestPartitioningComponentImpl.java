package com.sun.corba.se.impl.ior.iiop;

import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.spi.ior.TaggedComponentBase;
import com.sun.corba.se.spi.ior.iiop.RequestPartitioningComponent;
import org.omg.CORBA_2_3.portable.OutputStream;

public class RequestPartitioningComponentImpl
  extends TaggedComponentBase
  implements RequestPartitioningComponent
{
  private static ORBUtilSystemException wrapper = ORBUtilSystemException.get("oa.ior");
  private int partitionToUse;
  
  public boolean equals(Object paramObject)
  {
    if (!(paramObject instanceof RequestPartitioningComponentImpl)) {
      return false;
    }
    RequestPartitioningComponentImpl localRequestPartitioningComponentImpl = (RequestPartitioningComponentImpl)paramObject;
    return partitionToUse == partitionToUse;
  }
  
  public int hashCode()
  {
    return partitionToUse;
  }
  
  public String toString()
  {
    return "RequestPartitioningComponentImpl[partitionToUse=" + partitionToUse + "]";
  }
  
  public RequestPartitioningComponentImpl()
  {
    partitionToUse = 0;
  }
  
  public RequestPartitioningComponentImpl(int paramInt)
  {
    if ((paramInt < 0) || (paramInt > 63)) {
      throw wrapper.invalidRequestPartitioningComponentValue(new Integer(paramInt), new Integer(0), new Integer(63));
    }
    partitionToUse = paramInt;
  }
  
  public int getRequestPartitioningId()
  {
    return partitionToUse;
  }
  
  public void writeContents(OutputStream paramOutputStream)
  {
    paramOutputStream.write_ulong(partitionToUse);
  }
  
  public int getId()
  {
    return 1398099457;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\ior\iiop\RequestPartitioningComponentImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */