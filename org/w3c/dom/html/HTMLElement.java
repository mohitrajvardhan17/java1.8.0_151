package org.w3c.dom.html;

import org.w3c.dom.Element;

public abstract interface HTMLElement
  extends Element
{
  public abstract String getId();
  
  public abstract void setId(String paramString);
  
  public abstract String getTitle();
  
  public abstract void setTitle(String paramString);
  
  public abstract String getLang();
  
  public abstract void setLang(String paramString);
  
  public abstract String getDir();
  
  public abstract void setDir(String paramString);
  
  public abstract String getClassName();
  
  public abstract void setClassName(String paramString);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\w3c\dom\html\HTMLElement.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */