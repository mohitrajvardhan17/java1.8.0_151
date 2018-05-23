package javax.naming.ldap;

import com.sun.naming.internal.FactoryEnumeration;
import com.sun.naming.internal.ResourceManager;
import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.NamingException;

public abstract class ControlFactory
{
  protected ControlFactory() {}
  
  public abstract Control getControlInstance(Control paramControl)
    throws NamingException;
  
  public static Control getControlInstance(Control paramControl, Context paramContext, Hashtable<?, ?> paramHashtable)
    throws NamingException
  {
    FactoryEnumeration localFactoryEnumeration = ResourceManager.getFactories("java.naming.factory.control", paramHashtable, paramContext);
    if (localFactoryEnumeration == null) {
      return paramControl;
    }
    ControlFactory localControlFactory;
    for (Control localControl = null; (localControl == null) && (localFactoryEnumeration.hasMore()); localControl = localControlFactory.getControlInstance(paramControl)) {
      localControlFactory = (ControlFactory)localFactoryEnumeration.next();
    }
    return localControl != null ? localControl : paramControl;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\naming\ldap\ControlFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */