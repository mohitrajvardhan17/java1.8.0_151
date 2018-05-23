package com.sun.org.apache.xerces.internal.dom;

import org.w3c.dom.DOMImplementation;

public class DeferredDOMImplementationImpl
  extends DOMImplementationImpl
{
  static DeferredDOMImplementationImpl singleton = new DeferredDOMImplementationImpl();
  
  public DeferredDOMImplementationImpl() {}
  
  public static DOMImplementation getDOMImplementation()
  {
    return singleton;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\dom\DeferredDOMImplementationImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */