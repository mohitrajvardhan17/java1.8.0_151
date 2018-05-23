package com.sun.org.apache.xerces.internal.jaxp.validation;

import com.sun.org.apache.xerces.internal.xni.grammars.Grammar;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarDescription;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarPool;

final class SimpleXMLSchema
  extends AbstractXMLSchema
  implements XMLGrammarPool
{
  private static final Grammar[] ZERO_LENGTH_GRAMMAR_ARRAY = new Grammar[0];
  private Grammar fGrammar;
  private Grammar[] fGrammars;
  private XMLGrammarDescription fGrammarDescription;
  
  public SimpleXMLSchema(Grammar paramGrammar)
  {
    fGrammar = paramGrammar;
    fGrammars = new Grammar[] { paramGrammar };
    fGrammarDescription = paramGrammar.getGrammarDescription();
  }
  
  public Grammar[] retrieveInitialGrammarSet(String paramString)
  {
    return "http://www.w3.org/2001/XMLSchema".equals(paramString) ? (Grammar[])fGrammars.clone() : ZERO_LENGTH_GRAMMAR_ARRAY;
  }
  
  public void cacheGrammars(String paramString, Grammar[] paramArrayOfGrammar) {}
  
  public Grammar retrieveGrammar(XMLGrammarDescription paramXMLGrammarDescription)
  {
    return fGrammarDescription.equals(paramXMLGrammarDescription) ? fGrammar : null;
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


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\jaxp\validation\SimpleXMLSchema.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */