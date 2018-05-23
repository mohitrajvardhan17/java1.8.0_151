package org.w3c.dom;

public abstract interface ProcessingInstruction
  extends Node
{
  public abstract String getTarget();
  
  public abstract String getData();
  
  public abstract void setData(String paramString)
    throws DOMException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\w3c\dom\ProcessingInstruction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */