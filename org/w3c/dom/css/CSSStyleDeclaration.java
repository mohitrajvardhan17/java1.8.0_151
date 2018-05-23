package org.w3c.dom.css;

import org.w3c.dom.DOMException;

public abstract interface CSSStyleDeclaration
{
  public abstract String getCssText();
  
  public abstract void setCssText(String paramString)
    throws DOMException;
  
  public abstract String getPropertyValue(String paramString);
  
  public abstract CSSValue getPropertyCSSValue(String paramString);
  
  public abstract String removeProperty(String paramString)
    throws DOMException;
  
  public abstract String getPropertyPriority(String paramString);
  
  public abstract void setProperty(String paramString1, String paramString2, String paramString3)
    throws DOMException;
  
  public abstract int getLength();
  
  public abstract String item(int paramInt);
  
  public abstract CSSRule getParentRule();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\w3c\dom\css\CSSStyleDeclaration.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */