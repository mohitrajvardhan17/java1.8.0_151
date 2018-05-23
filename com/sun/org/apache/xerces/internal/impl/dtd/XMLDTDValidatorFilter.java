package com.sun.org.apache.xerces.internal.impl.dtd;

import com.sun.org.apache.xerces.internal.xni.parser.XMLDocumentFilter;

public abstract interface XMLDTDValidatorFilter
  extends XMLDocumentFilter
{
  public abstract boolean hasGrammar();
  
  public abstract boolean validate();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\impl\dtd\XMLDTDValidatorFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */