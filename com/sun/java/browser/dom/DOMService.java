package com.sun.java.browser.dom;

import java.security.AccessController;
import sun.security.action.GetPropertyAction;

public abstract class DOMService
{
  public static DOMService getService(Object paramObject)
    throws DOMUnsupportedException
  {
    try
    {
      String str = (String)AccessController.doPrivileged(new GetPropertyAction("com.sun.java.browser.dom.DOMServiceProvider"));
      DOMService.class;
      Class localClass = Class.forName("sun.plugin.dom.DOMService");
      return (DOMService)localClass.newInstance();
    }
    catch (Throwable localThrowable)
    {
      throw new DOMUnsupportedException(localThrowable.toString());
    }
  }
  
  public DOMService() {}
  
  public abstract Object invokeAndWait(DOMAction paramDOMAction)
    throws DOMAccessException;
  
  public abstract void invokeLater(DOMAction paramDOMAction);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\java\browser\dom\DOMService.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */