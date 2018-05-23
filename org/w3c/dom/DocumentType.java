package org.w3c.dom;

public abstract interface DocumentType
  extends Node
{
  public abstract String getName();
  
  public abstract NamedNodeMap getEntities();
  
  public abstract NamedNodeMap getNotations();
  
  public abstract String getPublicId();
  
  public abstract String getSystemId();
  
  public abstract String getInternalSubset();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\w3c\dom\DocumentType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */