package com.sun.corba.se.impl.corba;

import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import org.omg.CORBA.Any;
import org.omg.CORBA.Context;
import org.omg.CORBA.NVList;

public final class ContextImpl
  extends Context
{
  private org.omg.CORBA.ORB _orb;
  private ORBUtilSystemException wrapper;
  
  public ContextImpl(org.omg.CORBA.ORB paramORB)
  {
    _orb = paramORB;
    wrapper = ORBUtilSystemException.get((com.sun.corba.se.spi.orb.ORB)paramORB, "rpc.presentation");
  }
  
  public ContextImpl(Context paramContext)
  {
    throw wrapper.contextNotImplemented();
  }
  
  public String context_name()
  {
    throw wrapper.contextNotImplemented();
  }
  
  public Context parent()
  {
    throw wrapper.contextNotImplemented();
  }
  
  public Context create_child(String paramString)
  {
    throw wrapper.contextNotImplemented();
  }
  
  public void set_one_value(String paramString, Any paramAny)
  {
    throw wrapper.contextNotImplemented();
  }
  
  public void set_values(NVList paramNVList)
  {
    throw wrapper.contextNotImplemented();
  }
  
  public void delete_values(String paramString)
  {
    throw wrapper.contextNotImplemented();
  }
  
  public NVList get_values(String paramString1, int paramInt, String paramString2)
  {
    throw wrapper.contextNotImplemented();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\corba\ContextImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */