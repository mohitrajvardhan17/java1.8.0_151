package com.sun.org.apache.xerces.internal.jaxp.validation;

import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarPool;

public abstract interface XSGrammarPoolContainer
{
  public abstract XMLGrammarPool getGrammarPool();
  
  public abstract boolean isFullyComposed();
  
  public abstract Boolean getFeature(String paramString);
  
  public abstract void setFeature(String paramString, boolean paramBoolean);
  
  public abstract Object getProperty(String paramString);
  
  public abstract void setProperty(String paramString, Object paramObject);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\jaxp\validation\XSGrammarPoolContainer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */