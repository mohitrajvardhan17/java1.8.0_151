package com.sun.org.apache.xerces.internal.dom;

import java.util.StringTokenizer;
import java.util.Vector;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.DOMImplementationList;
import org.w3c.dom.DOMImplementationSource;

public class DOMImplementationSourceImpl
  implements DOMImplementationSource
{
  public DOMImplementationSourceImpl() {}
  
  public DOMImplementation getDOMImplementation(String paramString)
  {
    DOMImplementation localDOMImplementation = CoreDOMImplementationImpl.getDOMImplementation();
    if (testImpl(localDOMImplementation, paramString)) {
      return localDOMImplementation;
    }
    localDOMImplementation = DOMImplementationImpl.getDOMImplementation();
    if (testImpl(localDOMImplementation, paramString)) {
      return localDOMImplementation;
    }
    return null;
  }
  
  public DOMImplementationList getDOMImplementationList(String paramString)
  {
    DOMImplementation localDOMImplementation = CoreDOMImplementationImpl.getDOMImplementation();
    Vector localVector = new Vector();
    if (testImpl(localDOMImplementation, paramString)) {
      localVector.addElement(localDOMImplementation);
    }
    localDOMImplementation = DOMImplementationImpl.getDOMImplementation();
    if (testImpl(localDOMImplementation, paramString)) {
      localVector.addElement(localDOMImplementation);
    }
    return new DOMImplementationListImpl(localVector);
  }
  
  boolean testImpl(DOMImplementation paramDOMImplementation, String paramString)
  {
    StringTokenizer localStringTokenizer = new StringTokenizer(paramString);
    Object localObject = null;
    String str = null;
    if (localStringTokenizer.hasMoreTokens()) {
      localObject = localStringTokenizer.nextToken();
    }
    while (localObject != null)
    {
      int i = 0;
      if (localStringTokenizer.hasMoreTokens())
      {
        str = localStringTokenizer.nextToken();
        int j = str.charAt(0);
        switch (j)
        {
        case 48: 
        case 49: 
        case 50: 
        case 51: 
        case 52: 
        case 53: 
        case 54: 
        case 55: 
        case 56: 
        case 57: 
          i = 1;
        }
      }
      else
      {
        str = null;
      }
      if (i != 0)
      {
        if (!paramDOMImplementation.hasFeature((String)localObject, str)) {
          return false;
        }
        if (localStringTokenizer.hasMoreTokens()) {
          localObject = localStringTokenizer.nextToken();
        } else {
          localObject = null;
        }
      }
      else
      {
        if (!paramDOMImplementation.hasFeature((String)localObject, null)) {
          return false;
        }
        localObject = str;
      }
    }
    return true;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\dom\DOMImplementationSourceImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */