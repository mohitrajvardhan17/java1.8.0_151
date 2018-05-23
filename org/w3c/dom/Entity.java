package org.w3c.dom;

public abstract interface Entity
  extends Node
{
  public abstract String getPublicId();
  
  public abstract String getSystemId();
  
  public abstract String getNotationName();
  
  public abstract String getInputEncoding();
  
  public abstract String getXmlEncoding();
  
  public abstract String getXmlVersion();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\w3c\dom\Entity.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */