package com.sun.org.apache.xerces.internal.jaxp.validation;

import com.sun.org.apache.xerces.internal.xni.grammars.Grammar;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarDescription;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarPool;

final class ReadOnlyGrammarPool
  implements XMLGrammarPool
{
  private final XMLGrammarPool core;
  
  public ReadOnlyGrammarPool(XMLGrammarPool paramXMLGrammarPool)
  {
    core = paramXMLGrammarPool;
  }
  
  public void cacheGrammars(String paramString, Grammar[] paramArrayOfGrammar) {}
  
  public void clear() {}
  
  public void lockPool() {}
  
  public Grammar retrieveGrammar(XMLGrammarDescription paramXMLGrammarDescription)
  {
    return core.retrieveGrammar(paramXMLGrammarDescription);
  }
  
  public Grammar[] retrieveInitialGrammarSet(String paramString)
  {
    return core.retrieveInitialGrammarSet(paramString);
  }
  
  public void unlockPool() {}
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\jaxp\validation\ReadOnlyGrammarPool.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */