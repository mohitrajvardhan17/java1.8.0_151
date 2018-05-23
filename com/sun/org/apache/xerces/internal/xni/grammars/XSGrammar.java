package com.sun.org.apache.xerces.internal.xni.grammars;

import com.sun.org.apache.xerces.internal.xs.XSModel;

public abstract interface XSGrammar
  extends Grammar
{
  public abstract XSModel toXSModel();
  
  public abstract XSModel toXSModel(XSGrammar[] paramArrayOfXSGrammar);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\xni\grammars\XSGrammar.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */