package org.w3c.dom.css;

import org.w3c.dom.DOMException;

public abstract interface CSSPageRule
  extends CSSRule
{
  public abstract String getSelectorText();
  
  public abstract void setSelectorText(String paramString)
    throws DOMException;
  
  public abstract CSSStyleDeclaration getStyle();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\w3c\dom\css\CSSPageRule.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */