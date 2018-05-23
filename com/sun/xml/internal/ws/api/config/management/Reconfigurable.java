package com.sun.xml.internal.ws.api.config.management;

import javax.xml.ws.WebServiceException;

public abstract interface Reconfigurable
{
  public abstract void reconfigure()
    throws WebServiceException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\api\config\management\Reconfigurable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */