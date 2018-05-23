package com.sun.org.apache.xalan.internal.xsltc.runtime;

import com.sun.org.apache.xalan.internal.xsltc.DOM;
import org.xml.sax.AttributeList;

public final class Attributes
  implements AttributeList
{
  private int _element;
  private DOM _document;
  
  public Attributes(DOM paramDOM, int paramInt)
  {
    _element = paramInt;
    _document = paramDOM;
  }
  
  public int getLength()
  {
    return 0;
  }
  
  public String getName(int paramInt)
  {
    return null;
  }
  
  public String getType(int paramInt)
  {
    return null;
  }
  
  public String getType(String paramString)
  {
    return null;
  }
  
  public String getValue(int paramInt)
  {
    return null;
  }
  
  public String getValue(String paramString)
  {
    return null;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\runtime\Attributes.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */