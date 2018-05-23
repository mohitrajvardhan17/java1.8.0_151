package com.sun.xml.internal.fastinfoset.stax.events;

import javax.xml.stream.events.EntityDeclaration;

public class EntityDeclarationImpl
  extends EventBase
  implements EntityDeclaration
{
  private String _publicId;
  private String _systemId;
  private String _baseURI;
  private String _entityName;
  private String _replacement;
  private String _notationName;
  
  public EntityDeclarationImpl()
  {
    init();
  }
  
  public EntityDeclarationImpl(String paramString1, String paramString2)
  {
    init();
    _entityName = paramString1;
    _replacement = paramString2;
  }
  
  public String getPublicId()
  {
    return _publicId;
  }
  
  public String getSystemId()
  {
    return _systemId;
  }
  
  public String getName()
  {
    return _entityName;
  }
  
  public String getNotationName()
  {
    return _notationName;
  }
  
  public String getReplacementText()
  {
    return _replacement;
  }
  
  public String getBaseURI()
  {
    return _baseURI;
  }
  
  public void setPublicId(String paramString)
  {
    _publicId = paramString;
  }
  
  public void setSystemId(String paramString)
  {
    _systemId = paramString;
  }
  
  public void setBaseURI(String paramString)
  {
    _baseURI = paramString;
  }
  
  public void setName(String paramString)
  {
    _entityName = paramString;
  }
  
  public void setReplacementText(String paramString)
  {
    _replacement = paramString;
  }
  
  public void setNotationName(String paramString)
  {
    _notationName = paramString;
  }
  
  protected void init()
  {
    setEventType(15);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\fastinfoset\stax\events\EntityDeclarationImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */