package com.sun.xml.internal.txw2.output;

import com.sun.xml.internal.txw2.TypedXmlWriter;
import javax.xml.transform.Result;

public class TXWResult
  implements Result
{
  private String systemId;
  private TypedXmlWriter writer;
  
  public TXWResult(TypedXmlWriter paramTypedXmlWriter)
  {
    writer = paramTypedXmlWriter;
  }
  
  public TypedXmlWriter getWriter()
  {
    return writer;
  }
  
  public void setWriter(TypedXmlWriter paramTypedXmlWriter)
  {
    writer = paramTypedXmlWriter;
  }
  
  public String getSystemId()
  {
    return systemId;
  }
  
  public void setSystemId(String paramString)
  {
    systemId = paramString;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\txw2\output\TXWResult.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */