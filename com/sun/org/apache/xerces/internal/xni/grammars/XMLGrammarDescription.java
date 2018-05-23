package com.sun.org.apache.xerces.internal.xni.grammars;

import com.sun.org.apache.xerces.internal.xni.XMLResourceIdentifier;

public abstract interface XMLGrammarDescription
  extends XMLResourceIdentifier
{
  public static final String XML_SCHEMA = "http://www.w3.org/2001/XMLSchema";
  public static final String XML_DTD = "http://www.w3.org/TR/REC-xml";
  
  public abstract String getGrammarType();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\xni\grammars\XMLGrammarDescription.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */