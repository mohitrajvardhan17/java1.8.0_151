package com.sun.org.apache.xerces.internal.impl.dv.xs;

import com.sun.org.apache.xerces.internal.impl.dv.XSSimpleType;
import com.sun.org.apache.xerces.internal.util.SymbolHash;

public class SchemaDVFactoryImpl
  extends BaseSchemaDVFactory
{
  static final SymbolHash fBuiltInTypes = new SymbolHash();
  
  public SchemaDVFactoryImpl() {}
  
  static void createBuiltInTypes()
  {
    createBuiltInTypes(fBuiltInTypes, XSSimpleTypeDecl.fAnySimpleType);
  }
  
  public XSSimpleType getBuiltInType(String paramString)
  {
    return (XSSimpleType)fBuiltInTypes.get(paramString);
  }
  
  public SymbolHash getBuiltInTypes()
  {
    return fBuiltInTypes.makeClone();
  }
  
  static
  {
    createBuiltInTypes();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\impl\dv\xs\SchemaDVFactoryImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */