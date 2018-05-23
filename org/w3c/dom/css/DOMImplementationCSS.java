package org.w3c.dom.css;

import org.w3c.dom.DOMException;
import org.w3c.dom.DOMImplementation;

public abstract interface DOMImplementationCSS
  extends DOMImplementation
{
  public abstract CSSStyleSheet createCSSStyleSheet(String paramString1, String paramString2)
    throws DOMException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\w3c\dom\css\DOMImplementationCSS.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */