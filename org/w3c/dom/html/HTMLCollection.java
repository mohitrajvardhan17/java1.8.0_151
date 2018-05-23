package org.w3c.dom.html;

import org.w3c.dom.Node;

public abstract interface HTMLCollection
{
  public abstract int getLength();
  
  public abstract Node item(int paramInt);
  
  public abstract Node namedItem(String paramString);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\w3c\dom\html\HTMLCollection.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */