package org.w3c.dom.stylesheets;

import org.w3c.dom.Node;

public abstract interface StyleSheet
{
  public abstract String getType();
  
  public abstract boolean getDisabled();
  
  public abstract void setDisabled(boolean paramBoolean);
  
  public abstract Node getOwnerNode();
  
  public abstract StyleSheet getParentStyleSheet();
  
  public abstract String getHref();
  
  public abstract String getTitle();
  
  public abstract MediaList getMedia();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\w3c\dom\stylesheets\StyleSheet.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */