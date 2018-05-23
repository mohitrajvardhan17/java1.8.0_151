package org.omg.PortableInterceptor;

import org.omg.CORBA.Any;
import org.omg.CORBA.Policy;
import org.omg.IOP.ServiceContext;

public abstract interface ServerRequestInfoOperations
  extends RequestInfoOperations
{
  public abstract Any sending_exception();
  
  public abstract byte[] object_id();
  
  public abstract byte[] adapter_id();
  
  public abstract String server_id();
  
  public abstract String orb_id();
  
  public abstract String[] adapter_name();
  
  public abstract String target_most_derived_interface();
  
  public abstract Policy get_server_policy(int paramInt);
  
  public abstract void set_slot(int paramInt, Any paramAny)
    throws InvalidSlot;
  
  public abstract boolean target_is_a(String paramString);
  
  public abstract void add_reply_service_context(ServiceContext paramServiceContext, boolean paramBoolean);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\PortableInterceptor\ServerRequestInfoOperations.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */