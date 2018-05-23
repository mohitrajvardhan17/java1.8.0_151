package org.w3c.dom.css;

import org.w3c.dom.Element;
import org.w3c.dom.stylesheets.DocumentStyle;

public abstract interface DocumentCSS
  extends DocumentStyle
{
  public abstract CSSStyleDeclaration getOverrideStyle(Element paramElement, String paramString);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\w3c\dom\css\DocumentCSS.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */