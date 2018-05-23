package org.w3c.dom.css;

import org.w3c.dom.DOMException;

public abstract interface CSSCharsetRule
  extends CSSRule
{
  public abstract String getEncoding();
  
  public abstract void setEncoding(String paramString)
    throws DOMException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\w3c\dom\css\CSSCharsetRule.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */