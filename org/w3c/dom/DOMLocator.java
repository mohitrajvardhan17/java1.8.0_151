package org.w3c.dom;

public abstract interface DOMLocator
{
  public abstract int getLineNumber();
  
  public abstract int getColumnNumber();
  
  public abstract int getByteOffset();
  
  public abstract int getUtf16Offset();
  
  public abstract Node getRelatedNode();
  
  public abstract String getUri();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\w3c\dom\DOMLocator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */