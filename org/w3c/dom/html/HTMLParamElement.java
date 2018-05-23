package org.w3c.dom.html;

public abstract interface HTMLParamElement
  extends HTMLElement
{
  public abstract String getName();
  
  public abstract void setName(String paramString);
  
  public abstract String getType();
  
  public abstract void setType(String paramString);
  
  public abstract String getValue();
  
  public abstract void setValue(String paramString);
  
  public abstract String getValueType();
  
  public abstract void setValueType(String paramString);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\w3c\dom\html\HTMLParamElement.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */