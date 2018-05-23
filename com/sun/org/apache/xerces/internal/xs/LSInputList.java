package com.sun.org.apache.xerces.internal.xs;

import java.util.List;
import org.w3c.dom.ls.LSInput;

public abstract interface LSInputList
  extends List
{
  public abstract int getLength();
  
  public abstract LSInput item(int paramInt);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\xs\LSInputList.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */