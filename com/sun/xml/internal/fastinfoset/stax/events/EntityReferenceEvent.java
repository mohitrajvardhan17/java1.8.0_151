package com.sun.xml.internal.fastinfoset.stax.events;

import javax.xml.stream.events.EntityDeclaration;
import javax.xml.stream.events.EntityReference;

public class EntityReferenceEvent
  extends EventBase
  implements EntityReference
{
  private EntityDeclaration _entityDeclaration;
  private String _entityName;
  
  public EntityReferenceEvent()
  {
    init();
  }
  
  public EntityReferenceEvent(String paramString, EntityDeclaration paramEntityDeclaration)
  {
    init();
    _entityName = paramString;
    _entityDeclaration = paramEntityDeclaration;
  }
  
  public String getName()
  {
    return _entityName;
  }
  
  public EntityDeclaration getDeclaration()
  {
    return _entityDeclaration;
  }
  
  public void setName(String paramString)
  {
    _entityName = paramString;
  }
  
  public void setDeclaration(EntityDeclaration paramEntityDeclaration)
  {
    _entityDeclaration = paramEntityDeclaration;
  }
  
  public String toString()
  {
    String str = _entityDeclaration.getReplacementText();
    if (str == null) {
      str = "";
    }
    return "&" + getName() + ";='" + str + "'";
  }
  
  protected void init()
  {
    setEventType(9);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\fastinfoset\stax\events\EntityReferenceEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */