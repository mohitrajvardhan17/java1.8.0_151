package com.sun.xml.internal.fastinfoset.stax.events;

import java.util.List;
import javax.xml.stream.events.DTD;

public class DTDEvent
  extends EventBase
  implements DTD
{
  private String _dtd;
  private List _notations;
  private List _entities;
  
  public DTDEvent()
  {
    setEventType(11);
  }
  
  public DTDEvent(String paramString)
  {
    setEventType(11);
    _dtd = paramString;
  }
  
  public String getDocumentTypeDeclaration()
  {
    return _dtd;
  }
  
  public void setDTD(String paramString)
  {
    _dtd = paramString;
  }
  
  public List getEntities()
  {
    return _entities;
  }
  
  public List getNotations()
  {
    return _notations;
  }
  
  public Object getProcessedDTD()
  {
    return null;
  }
  
  public void setEntities(List paramList)
  {
    _entities = paramList;
  }
  
  public void setNotations(List paramList)
  {
    _notations = paramList;
  }
  
  public String toString()
  {
    return _dtd;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\fastinfoset\stax\events\DTDEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */