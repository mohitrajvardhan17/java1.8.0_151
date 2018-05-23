package com.sun.corba.se.spi.servicecontext;

import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.spi.orb.ORB;
import java.util.Enumeration;
import java.util.Vector;
import org.omg.CORBA.BAD_PARAM;

public class ServiceContextRegistry
{
  private ORB orb;
  private Vector scCollection = new Vector();
  
  private void dprint(String paramString)
  {
    ORBUtility.dprint(this, paramString);
  }
  
  public ServiceContextRegistry(ORB paramORB)
  {
    orb = paramORB;
  }
  
  public void register(Class paramClass)
  {
    if (ORB.ORBInitDebug) {
      dprint("Registering service context class " + paramClass);
    }
    ServiceContextData localServiceContextData = new ServiceContextData(paramClass);
    if (findServiceContextData(localServiceContextData.getId()) == null) {
      scCollection.addElement(localServiceContextData);
    } else {
      throw new BAD_PARAM("Tried to register duplicate service context");
    }
  }
  
  public ServiceContextData findServiceContextData(int paramInt)
  {
    if (ORB.ORBInitDebug) {
      dprint("Searching registry for service context id " + paramInt);
    }
    Enumeration localEnumeration = scCollection.elements();
    while (localEnumeration.hasMoreElements())
    {
      ServiceContextData localServiceContextData = (ServiceContextData)localEnumeration.nextElement();
      if (localServiceContextData.getId() == paramInt)
      {
        if (ORB.ORBInitDebug) {
          dprint("Service context data found: " + localServiceContextData);
        }
        return localServiceContextData;
      }
    }
    if (ORB.ORBInitDebug) {
      dprint("Service context data not found");
    }
    return null;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\spi\servicecontext\ServiceContextRegistry.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */