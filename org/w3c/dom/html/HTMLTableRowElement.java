package org.w3c.dom.html;

import org.w3c.dom.DOMException;

public abstract interface HTMLTableRowElement
  extends HTMLElement
{
  public abstract int getRowIndex();
  
  public abstract int getSectionRowIndex();
  
  public abstract HTMLCollection getCells();
  
  public abstract String getAlign();
  
  public abstract void setAlign(String paramString);
  
  public abstract String getBgColor();
  
  public abstract void setBgColor(String paramString);
  
  public abstract String getCh();
  
  public abstract void setCh(String paramString);
  
  public abstract String getChOff();
  
  public abstract void setChOff(String paramString);
  
  public abstract String getVAlign();
  
  public abstract void setVAlign(String paramString);
  
  public abstract HTMLElement insertCell(int paramInt)
    throws DOMException;
  
  public abstract void deleteCell(int paramInt)
    throws DOMException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\w3c\dom\html\HTMLTableRowElement.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */