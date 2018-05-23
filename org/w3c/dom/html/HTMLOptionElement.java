package org.w3c.dom.html;

public abstract interface HTMLOptionElement
  extends HTMLElement
{
  public abstract HTMLFormElement getForm();
  
  public abstract boolean getDefaultSelected();
  
  public abstract void setDefaultSelected(boolean paramBoolean);
  
  public abstract String getText();
  
  public abstract int getIndex();
  
  public abstract boolean getDisabled();
  
  public abstract void setDisabled(boolean paramBoolean);
  
  public abstract String getLabel();
  
  public abstract void setLabel(String paramString);
  
  public abstract boolean getSelected();
  
  public abstract void setSelected(boolean paramBoolean);
  
  public abstract String getValue();
  
  public abstract void setValue(String paramString);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\w3c\dom\html\HTMLOptionElement.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */