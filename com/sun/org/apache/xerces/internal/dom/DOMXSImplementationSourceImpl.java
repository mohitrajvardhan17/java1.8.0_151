package com.sun.org.apache.xerces.internal.dom;

import com.sun.org.apache.xerces.internal.impl.xs.XSImplementationImpl;
import java.util.Vector;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.DOMImplementationList;

public class DOMXSImplementationSourceImpl
  extends DOMImplementationSourceImpl
{
  public DOMXSImplementationSourceImpl() {}
  
  public DOMImplementation getDOMImplementation(String paramString)
  {
    DOMImplementation localDOMImplementation = super.getDOMImplementation(paramString);
    if (localDOMImplementation != null) {
      return localDOMImplementation;
    }
    localDOMImplementation = PSVIDOMImplementationImpl.getDOMImplementation();
    if (testImpl(localDOMImplementation, paramString)) {
      return localDOMImplementation;
    }
    localDOMImplementation = XSImplementationImpl.getDOMImplementation();
    if (testImpl(localDOMImplementation, paramString)) {
      return localDOMImplementation;
    }
    return null;
  }
  
  public DOMImplementationList getDOMImplementationList(String paramString)
  {
    Vector localVector = new Vector();
    DOMImplementationList localDOMImplementationList = super.getDOMImplementationList(paramString);
    for (int i = 0; i < localDOMImplementationList.getLength(); i++) {
      localVector.addElement(localDOMImplementationList.item(i));
    }
    DOMImplementation localDOMImplementation = PSVIDOMImplementationImpl.getDOMImplementation();
    if (testImpl(localDOMImplementation, paramString)) {
      localVector.addElement(localDOMImplementation);
    }
    localDOMImplementation = XSImplementationImpl.getDOMImplementation();
    if (testImpl(localDOMImplementation, paramString)) {
      localVector.addElement(localDOMImplementation);
    }
    return new DOMImplementationListImpl(localVector);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\dom\DOMXSImplementationSourceImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */