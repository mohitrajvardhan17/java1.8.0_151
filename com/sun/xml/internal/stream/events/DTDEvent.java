package com.sun.xml.internal.stream.events;

import java.io.IOException;
import java.io.Writer;
import java.util.List;
import javax.xml.stream.events.DTD;

public class DTDEvent
  extends DummyEvent
  implements DTD
{
  private String fDoctypeDeclaration;
  private List fNotations;
  private List fEntities;
  
  public DTDEvent()
  {
    init();
  }
  
  public DTDEvent(String paramString)
  {
    init();
    fDoctypeDeclaration = paramString;
  }
  
  public void setDocumentTypeDeclaration(String paramString)
  {
    fDoctypeDeclaration = paramString;
  }
  
  public String getDocumentTypeDeclaration()
  {
    return fDoctypeDeclaration;
  }
  
  public void setEntities(List paramList)
  {
    fEntities = paramList;
  }
  
  public List getEntities()
  {
    return fEntities;
  }
  
  public void setNotations(List paramList)
  {
    fNotations = paramList;
  }
  
  public List getNotations()
  {
    return fNotations;
  }
  
  public Object getProcessedDTD()
  {
    return null;
  }
  
  protected void init()
  {
    setEventType(11);
  }
  
  public String toString()
  {
    return fDoctypeDeclaration;
  }
  
  protected void writeAsEncodedUnicodeEx(Writer paramWriter)
    throws IOException
  {
    paramWriter.write(fDoctypeDeclaration);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\stream\events\DTDEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */