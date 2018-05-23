package org.w3c.dom.html;

import org.w3c.dom.Document;

public abstract interface HTMLFrameElement
  extends HTMLElement
{
  public abstract String getFrameBorder();
  
  public abstract void setFrameBorder(String paramString);
  
  public abstract String getLongDesc();
  
  public abstract void setLongDesc(String paramString);
  
  public abstract String getMarginHeight();
  
  public abstract void setMarginHeight(String paramString);
  
  public abstract String getMarginWidth();
  
  public abstract void setMarginWidth(String paramString);
  
  public abstract String getName();
  
  public abstract void setName(String paramString);
  
  public abstract boolean getNoResize();
  
  public abstract void setNoResize(boolean paramBoolean);
  
  public abstract String getScrolling();
  
  public abstract void setScrolling(String paramString);
  
  public abstract String getSrc();
  
  public abstract void setSrc(String paramString);
  
  public abstract Document getContentDocument();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\w3c\dom\html\HTMLFrameElement.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */