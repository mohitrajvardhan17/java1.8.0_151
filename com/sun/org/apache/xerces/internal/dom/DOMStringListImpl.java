package com.sun.org.apache.xerces.internal.dom;

import java.util.Vector;
import org.w3c.dom.DOMStringList;

public class DOMStringListImpl
  implements DOMStringList
{
  private Vector fStrings;
  
  public DOMStringListImpl()
  {
    fStrings = new Vector();
  }
  
  public DOMStringListImpl(Vector paramVector)
  {
    fStrings = paramVector;
  }
  
  public String item(int paramInt)
  {
    try
    {
      return (String)fStrings.elementAt(paramInt);
    }
    catch (ArrayIndexOutOfBoundsException localArrayIndexOutOfBoundsException) {}
    return null;
  }
  
  public int getLength()
  {
    return fStrings.size();
  }
  
  public boolean contains(String paramString)
  {
    return fStrings.contains(paramString);
  }
  
  public void add(String paramString)
  {
    fStrings.add(paramString);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\dom\DOMStringListImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */