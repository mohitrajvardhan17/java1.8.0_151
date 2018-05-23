package org.omg.PortableInterceptor;

import org.omg.CORBA.Any;
import org.omg.CORBA.Policy;
import org.omg.IOP.ServiceContext;
import org.omg.IOP.TaggedComponent;
import org.omg.IOP.TaggedProfile;

public abstract interface ClientRequestInfoOperations
  extends RequestInfoOperations
{
  public abstract org.omg.CORBA.Object target();
  
  public abstract org.omg.CORBA.Object effective_target();
  
  public abstract TaggedProfile effective_profile();
  
  public abstract Any received_exception();
  
  public abstract String received_exception_id();
  
  public abstract TaggedComponent get_effective_component(int paramInt);
  
  public abstract TaggedComponent[] get_effective_components(int paramInt);
  
  public abstract Policy get_request_policy(int paramInt);
  
  public abstract void add_request_service_context(ServiceContext paramServiceContext, boolean paramBoolean);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\PortableInterceptor\ClientRequestInfoOperations.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */