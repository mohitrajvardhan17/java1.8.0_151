package com.sun.org.apache.xerces.internal.jaxp.validation;

import com.sun.org.apache.xerces.internal.xni.grammars.Grammar;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarDescription;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarPool;

final class EmptyXMLSchema
  extends AbstractXMLSchema
  implements XMLGrammarPool
{
  private static final Grammar[] ZERO_LENGTH_GRAMMAR_ARRAY = new Grammar[0];
  
  public EmptyXMLSchema() {}
  
  public Grammar[] retrieveInitialGrammarSet(String paramString)
  {
    return ZERO_LENGTH_GRAMMAR_ARRAY;
  }
  
  public void cacheGrammars(String paramString, Grammar[] paramArrayOfGrammar) {}
  
  public Grammar retrieveGrammar(XMLGrammarDescription paramXMLGrammarDescription)
  {
    return null;
  }
  
  public void lockPool() {}
  
  public void unlockPool() {}
  
  public void clear() {}
  
  public XMLGrammarPool getGrammarPool()
  {
    return this;
  }
  
  public boolean isFullyComposed()
  {
    return true;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\jaxp\validation\EmptyXMLSchema.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */