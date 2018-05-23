package org.w3c.dom.html;

import org.w3c.dom.DOMException;

public abstract interface HTMLTableSectionElement
  extends HTMLElement
{
  public abstract String getAlign();
  
  public abstract void setAlign(String paramString);
  
  public abstract String getCh();
  
  public abstract void setCh(String paramString);
  
  public abstract String getChOff();
  
  public abstract void setChOff(String paramString);
  
  public abstract String getVAlign();
  
  public abstract void setVAlign(String paramString);
  
  public abstract HTMLCollection getRows();
  
  public abstract HTMLElement insertRow(int paramInt)
    throws DOMException;
  
  public abstract void deleteRow(int paramInt)
    throws DOMException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\w3c\dom\html\HTMLTableSectionElement.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */