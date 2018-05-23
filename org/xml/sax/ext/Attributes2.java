package org.xml.sax.ext;

import org.xml.sax.Attributes;

public abstract interface Attributes2
  extends Attributes
{
  public abstract boolean isDeclared(int paramInt);
  
  public abstract boolean isDeclared(String paramString);
  
  public abstract boolean isDeclared(String paramString1, String paramString2);
  
  public abstract boolean isSpecified(int paramInt);
  
  public abstract boolean isSpecified(String paramString1, String paramString2);
  
  public abstract boolean isSpecified(String paramString);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\xml\sax\ext\Attributes2.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */