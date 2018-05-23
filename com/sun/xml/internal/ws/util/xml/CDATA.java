package com.sun.xml.internal.ws.util.xml;

public final class CDATA
{
  private String _text;
  
  public CDATA(String paramString)
  {
    _text = paramString;
  }
  
  public String getText()
  {
    return _text;
  }
  
  public boolean equals(Object paramObject)
  {
    if (paramObject == null) {
      return false;
    }
    if (!(paramObject instanceof CDATA)) {
      return false;
    }
    CDATA localCDATA = (CDATA)paramObject;
    return _text.equals(_text);
  }
  
  public int hashCode()
  {
    return _text.hashCode();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\util\xml\CDATA.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */