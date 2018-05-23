package com.sun.org.apache.xerces.internal.impl.dv.xs;

import com.sun.org.apache.xerces.internal.impl.dv.XSSimpleType;
import com.sun.org.apache.xerces.internal.util.SymbolHash;

public class ExtendedSchemaDVFactoryImpl
  extends BaseSchemaDVFactory
{
  static SymbolHash fBuiltInTypes = new SymbolHash();
  
  public ExtendedSchemaDVFactoryImpl() {}
  
  static void createBuiltInTypes()
  {
    String str1 = "anyAtomicType";
    String str2 = "duration";
    String str3 = "yearMonthDuration";
    String str4 = "dayTimeDuration";
    createBuiltInTypes(fBuiltInTypes, XSSimpleTypeDecl.fAnyAtomicType);
    fBuiltInTypes.put("anyAtomicType", XSSimpleTypeDecl.fAnyAtomicType);
    XSSimpleTypeDecl localXSSimpleTypeDecl = (XSSimpleTypeDecl)fBuiltInTypes.get("duration");
    fBuiltInTypes.put("yearMonthDuration", new XSSimpleTypeDecl(localXSSimpleTypeDecl, "yearMonthDuration", (short)27, (short)1, false, false, false, true, (short)46));
    fBuiltInTypes.put("dayTimeDuration", new XSSimpleTypeDecl(localXSSimpleTypeDecl, "dayTimeDuration", (short)28, (short)1, false, false, false, true, (short)47));
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


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\impl\dv\xs\ExtendedSchemaDVFactoryImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */