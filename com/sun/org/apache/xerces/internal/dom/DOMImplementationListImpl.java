package com.sun.org.apache.xerces.internal.dom;

import java.util.Vector;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.DOMImplementationList;

public class DOMImplementationListImpl
  implements DOMImplementationList
{
  private Vector fImplementations;
  
  public DOMImplementationListImpl()
  {
    fImplementations = new Vector();
  }
  
  public DOMImplementationListImpl(Vector paramVector)
  {
    fImplementations = paramVector;
  }
  
  public DOMImplementation item(int paramInt)
  {
    try
    {
      return (DOMImplementation)fImplementations.elementAt(paramInt);
    }
    catch (ArrayIndexOutOfBoundsException localArrayIndexOutOfBoundsException) {}
    return null;
  }
  
  public int getLength()
  {
    return fImplementations.size();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\dom\DOMImplementationListImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */