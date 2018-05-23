package org.w3c.dom.css;

import org.w3c.dom.stylesheets.MediaList;

public abstract interface CSSImportRule
  extends CSSRule
{
  public abstract String getHref();
  
  public abstract MediaList getMedia();
  
  public abstract CSSStyleSheet getStyleSheet();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\w3c\dom\css\CSSImportRule.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */