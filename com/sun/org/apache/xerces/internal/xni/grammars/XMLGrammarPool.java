package com.sun.org.apache.xerces.internal.xni.grammars;

public abstract interface XMLGrammarPool
{
  public abstract Grammar[] retrieveInitialGrammarSet(String paramString);
  
  public abstract void cacheGrammars(String paramString, Grammar[] paramArrayOfGrammar);
  
  public abstract Grammar retrieveGrammar(XMLGrammarDescription paramXMLGrammarDescription);
  
  public abstract void lockPool();
  
  public abstract void unlockPool();
  
  public abstract void clear();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\xni\grammars\XMLGrammarPool.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */