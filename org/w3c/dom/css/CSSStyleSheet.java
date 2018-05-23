package org.w3c.dom.css;

import org.w3c.dom.DOMException;
import org.w3c.dom.stylesheets.StyleSheet;

public abstract interface CSSStyleSheet
  extends StyleSheet
{
  public abstract CSSRule getOwnerRule();
  
  public abstract CSSRuleList getCssRules();
  
  public abstract int insertRule(String paramString, int paramInt)
    throws DOMException;
  
  public abstract void deleteRule(int paramInt)
    throws DOMException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\w3c\dom\css\CSSStyleSheet.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */