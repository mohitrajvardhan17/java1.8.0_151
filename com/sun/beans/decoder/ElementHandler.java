package com.sun.beans.decoder;

public abstract class ElementHandler
{
  private DocumentHandler owner;
  private ElementHandler parent;
  private String id;
  
  public ElementHandler() {}
  
  public final DocumentHandler getOwner()
  {
    return owner;
  }
  
  final void setOwner(DocumentHandler paramDocumentHandler)
  {
    if (paramDocumentHandler == null) {
      throw new IllegalArgumentException("Every element should have owner");
    }
    owner = paramDocumentHandler;
  }
  
  public final ElementHandler getParent()
  {
    return parent;
  }
  
  final void setParent(ElementHandler paramElementHandler)
  {
    parent = paramElementHandler;
  }
  
  protected final Object getVariable(String paramString)
  {
    if (paramString.equals(id))
    {
      ValueObject localValueObject = getValueObject();
      if (localValueObject.isVoid()) {
        throw new IllegalStateException("The element does not return value");
      }
      return localValueObject.getValue();
    }
    return parent != null ? parent.getVariable(paramString) : owner.getVariable(paramString);
  }
  
  protected Object getContextBean()
  {
    if (parent != null)
    {
      localObject = parent.getValueObject();
      if (!((ValueObject)localObject).isVoid()) {
        return ((ValueObject)localObject).getValue();
      }
      throw new IllegalStateException("The outer element does not return value");
    }
    Object localObject = owner.getOwner();
    if (localObject != null) {
      return localObject;
    }
    throw new IllegalStateException("The topmost element does not have context");
  }
  
  public void addAttribute(String paramString1, String paramString2)
  {
    if (paramString1.equals("id")) {
      id = paramString2;
    } else {
      throw new IllegalArgumentException("Unsupported attribute: " + paramString1);
    }
  }
  
  public void startElement() {}
  
  public void endElement()
  {
    ValueObject localValueObject = getValueObject();
    if (!localValueObject.isVoid())
    {
      if (id != null) {
        owner.setVariable(id, localValueObject.getValue());
      }
      if (isArgument()) {
        if (parent != null) {
          parent.addArgument(localValueObject.getValue());
        } else {
          owner.addObject(localValueObject.getValue());
        }
      }
    }
  }
  
  public void addCharacter(char paramChar)
  {
    if ((paramChar != ' ') && (paramChar != '\n') && (paramChar != '\t') && (paramChar != '\r')) {
      throw new IllegalStateException("Illegal character with code " + paramChar);
    }
  }
  
  protected void addArgument(Object paramObject)
  {
    throw new IllegalStateException("Could not add argument to simple element");
  }
  
  protected boolean isArgument()
  {
    return id == null;
  }
  
  protected abstract ValueObject getValueObject();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\beans\decoder\ElementHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */