package com.sun.xml.internal.bind.v2.runtime;

import javax.activation.DataHandler;
import javax.xml.bind.annotation.adapters.XmlAdapter;

public class SwaRefAdapterMarker
  extends XmlAdapter<String, DataHandler>
{
  public SwaRefAdapterMarker() {}
  
  public DataHandler unmarshal(String paramString)
    throws Exception
  {
    throw new IllegalStateException("Not implemented");
  }
  
  public String marshal(DataHandler paramDataHandler)
    throws Exception
  {
    throw new IllegalStateException("Not implemented");
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\runtime\SwaRefAdapterMarker.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */