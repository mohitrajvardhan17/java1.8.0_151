package org.w3c.dom;

public abstract interface Text
  extends CharacterData
{
  public abstract Text splitText(int paramInt)
    throws DOMException;
  
  public abstract boolean isElementContentWhitespace();
  
  public abstract String getWholeText();
  
  public abstract Text replaceWholeText(String paramString)
    throws DOMException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\w3c\dom\Text.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */