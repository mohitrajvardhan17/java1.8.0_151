package org.w3c.dom.stylesheets;

import org.w3c.dom.DOMException;

public abstract interface MediaList
{
  public abstract String getMediaText();
  
  public abstract void setMediaText(String paramString)
    throws DOMException;
  
  public abstract int getLength();
  
  public abstract String item(int paramInt);
  
  public abstract void deleteMedium(String paramString)
    throws DOMException;
  
  public abstract void appendMedium(String paramString)
    throws DOMException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\w3c\dom\stylesheets\MediaList.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */