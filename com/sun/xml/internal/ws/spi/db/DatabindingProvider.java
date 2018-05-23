package com.sun.xml.internal.ws.spi.db;

import com.oracle.webservices.internal.api.databinding.Databinding;
import com.oracle.webservices.internal.api.databinding.WSDLGenerator;
import com.sun.xml.internal.ws.api.databinding.DatabindingConfig;
import java.util.Map;

public abstract interface DatabindingProvider
{
  public abstract boolean isFor(String paramString);
  
  public abstract void init(Map<String, Object> paramMap);
  
  public abstract Databinding create(DatabindingConfig paramDatabindingConfig);
  
  public abstract WSDLGenerator wsdlGen(DatabindingConfig paramDatabindingConfig);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\spi\db\DatabindingProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */