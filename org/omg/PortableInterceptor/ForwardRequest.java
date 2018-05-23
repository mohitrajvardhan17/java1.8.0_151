package org.omg.PortableInterceptor;

import org.omg.CORBA.Object;
import org.omg.CORBA.UserException;

public final class ForwardRequest
  extends UserException
{
  public Object forward = null;
  
  public ForwardRequest()
  {
    super(ForwardRequestHelper.id());
  }
  
  public ForwardRequest(Object paramObject)
  {
    super(ForwardRequestHelper.id());
    forward = paramObject;
  }
  
  public ForwardRequest(String paramString, Object paramObject)
  {
    super(ForwardRequestHelper.id() + "  " + paramString);
    forward = paramObject;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\PortableInterceptor\ForwardRequest.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */