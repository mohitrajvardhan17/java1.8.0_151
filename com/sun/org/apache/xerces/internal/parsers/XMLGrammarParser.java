package com.sun.org.apache.xerces.internal.parsers;

import com.sun.org.apache.xerces.internal.impl.dv.DTDDVFactory;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.xni.parser.XMLParserConfiguration;

public abstract class XMLGrammarParser
  extends XMLParser
{
  protected DTDDVFactory fDatatypeValidatorFactory;
  
  protected XMLGrammarParser(SymbolTable paramSymbolTable)
  {
    super(new XIncludeAwareParserConfiguration());
    fConfiguration.setProperty("http://apache.org/xml/properties/internal/symbol-table", paramSymbolTable);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\parsers\XMLGrammarParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */