package com.sun.org.apache.xerces.internal.jaxp.validation;

import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarPool;

final class XMLSchema
  extends AbstractXMLSchema
{
  private final XMLGrammarPool fGrammarPool;
  
  public XMLSchema(XMLGrammarPool paramXMLGrammarPool)
  {
    fGrammarPool = paramXMLGrammarPool;
  }
  
  public XMLGrammarPool getGrammarPool()
  {
    return fGrammarPool;
  }
  
  public boolean isFullyComposed()
  {
    return true;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\jaxp\validation\XMLSchema.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */