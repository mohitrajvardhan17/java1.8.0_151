package com.sun.xml.internal.ws.api.databinding;

import com.oracle.webservices.internal.api.databinding.Databinding;
import com.sun.xml.internal.ws.db.DatabindingFactoryImpl;
import java.util.Map;

public abstract class DatabindingFactory
  extends com.oracle.webservices.internal.api.databinding.DatabindingFactory
{
  static final String ImplClass = DatabindingFactoryImpl.class.getName();
  
  public DatabindingFactory() {}
  
  public abstract Databinding createRuntime(DatabindingConfig paramDatabindingConfig);
  
  public abstract Map<String, Object> properties();
  
  public static DatabindingFactory newInstance()
  {
    try
    {
      Class localClass = Class.forName(ImplClass);
      return (DatabindingFactory)localClass.newInstance();
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
    }
    return null;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\api\databinding\DatabindingFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */