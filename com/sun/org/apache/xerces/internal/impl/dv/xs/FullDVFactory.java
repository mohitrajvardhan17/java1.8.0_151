package com.sun.org.apache.xerces.internal.impl.dv.xs;

import com.sun.org.apache.xerces.internal.impl.dv.XSFacets;
import com.sun.org.apache.xerces.internal.impl.dv.XSSimpleType;
import com.sun.org.apache.xerces.internal.util.SymbolHash;

public class FullDVFactory
  extends BaseDVFactory
{
  static final String URI_SCHEMAFORSCHEMA = "http://www.w3.org/2001/XMLSchema";
  static SymbolHash fFullTypes = new SymbolHash(89);
  
  public FullDVFactory() {}
  
  public XSSimpleType getBuiltInType(String paramString)
  {
    return (XSSimpleType)fFullTypes.get(paramString);
  }
  
  public SymbolHash getBuiltInTypes()
  {
    return fFullTypes.makeClone();
  }
  
  static void createBuiltInTypes(SymbolHash paramSymbolHash)
  {
    BaseDVFactory.createBuiltInTypes(paramSymbolHash);
    String str1 = "double";
    String str2 = "duration";
    String str3 = "ENTITY";
    String str4 = "ENTITIES";
    String str5 = "float";
    String str6 = "hexBinary";
    String str7 = "ID";
    String str8 = "IDREF";
    String str9 = "IDREFS";
    String str10 = "Name";
    String str11 = "NCName";
    String str12 = "NMTOKEN";
    String str13 = "NMTOKENS";
    String str14 = "language";
    String str15 = "normalizedString";
    String str16 = "NOTATION";
    String str17 = "QName";
    String str18 = "string";
    String str19 = "token";
    XSFacets localXSFacets = new XSFacets();
    XSSimpleTypeDecl localXSSimpleTypeDecl1 = XSSimpleTypeDecl.fAnySimpleType;
    XSSimpleTypeDecl localXSSimpleTypeDecl2 = (XSSimpleTypeDecl)paramSymbolHash.get("string");
    paramSymbolHash.put("float", new XSSimpleTypeDecl(localXSSimpleTypeDecl1, "float", (short)4, (short)1, true, true, true, true, (short)5));
    paramSymbolHash.put("double", new XSSimpleTypeDecl(localXSSimpleTypeDecl1, "double", (short)5, (short)1, true, true, true, true, (short)6));
    paramSymbolHash.put("duration", new XSSimpleTypeDecl(localXSSimpleTypeDecl1, "duration", (short)6, (short)1, false, false, false, true, (short)7));
    paramSymbolHash.put("hexBinary", new XSSimpleTypeDecl(localXSSimpleTypeDecl1, "hexBinary", (short)15, (short)0, false, false, false, true, (short)16));
    paramSymbolHash.put("QName", new XSSimpleTypeDecl(localXSSimpleTypeDecl1, "QName", (short)18, (short)0, false, false, false, true, (short)19));
    paramSymbolHash.put("NOTATION", new XSSimpleTypeDecl(localXSSimpleTypeDecl1, "NOTATION", (short)20, (short)0, false, false, false, true, (short)20));
    whiteSpace = 1;
    XSSimpleTypeDecl localXSSimpleTypeDecl3 = new XSSimpleTypeDecl(localXSSimpleTypeDecl2, "normalizedString", "http://www.w3.org/2001/XMLSchema", (short)0, false, null, (short)21);
    localXSSimpleTypeDecl3.applyFacets1(localXSFacets, (short)16, (short)0);
    paramSymbolHash.put("normalizedString", localXSSimpleTypeDecl3);
    whiteSpace = 2;
    XSSimpleTypeDecl localXSSimpleTypeDecl4 = new XSSimpleTypeDecl(localXSSimpleTypeDecl3, "token", "http://www.w3.org/2001/XMLSchema", (short)0, false, null, (short)22);
    localXSSimpleTypeDecl4.applyFacets1(localXSFacets, (short)16, (short)0);
    paramSymbolHash.put("token", localXSSimpleTypeDecl4);
    whiteSpace = 2;
    pattern = "([a-zA-Z]{1,8})(-[a-zA-Z0-9]{1,8})*";
    XSSimpleTypeDecl localXSSimpleTypeDecl5 = new XSSimpleTypeDecl(localXSSimpleTypeDecl4, "language", "http://www.w3.org/2001/XMLSchema", (short)0, false, null, (short)23);
    localXSSimpleTypeDecl5.applyFacets1(localXSFacets, (short)24, (short)0);
    paramSymbolHash.put("language", localXSSimpleTypeDecl5);
    whiteSpace = 2;
    XSSimpleTypeDecl localXSSimpleTypeDecl6 = new XSSimpleTypeDecl(localXSSimpleTypeDecl4, "Name", "http://www.w3.org/2001/XMLSchema", (short)0, false, null, (short)25);
    localXSSimpleTypeDecl6.applyFacets1(localXSFacets, (short)16, (short)0, (short)2);
    paramSymbolHash.put("Name", localXSSimpleTypeDecl6);
    whiteSpace = 2;
    XSSimpleTypeDecl localXSSimpleTypeDecl7 = new XSSimpleTypeDecl(localXSSimpleTypeDecl6, "NCName", "http://www.w3.org/2001/XMLSchema", (short)0, false, null, (short)26);
    localXSSimpleTypeDecl7.applyFacets1(localXSFacets, (short)16, (short)0, (short)3);
    paramSymbolHash.put("NCName", localXSSimpleTypeDecl7);
    paramSymbolHash.put("ID", new XSSimpleTypeDecl(localXSSimpleTypeDecl7, "ID", (short)21, (short)0, false, false, false, true, (short)27));
    XSSimpleTypeDecl localXSSimpleTypeDecl8 = new XSSimpleTypeDecl(localXSSimpleTypeDecl7, "IDREF", (short)22, (short)0, false, false, false, true, (short)28);
    paramSymbolHash.put("IDREF", localXSSimpleTypeDecl8);
    minLength = 1;
    XSSimpleTypeDecl localXSSimpleTypeDecl9 = new XSSimpleTypeDecl(null, "http://www.w3.org/2001/XMLSchema", (short)0, localXSSimpleTypeDecl8, true, null);
    XSSimpleTypeDecl localXSSimpleTypeDecl10 = new XSSimpleTypeDecl(localXSSimpleTypeDecl9, "IDREFS", "http://www.w3.org/2001/XMLSchema", (short)0, false, null);
    localXSSimpleTypeDecl10.applyFacets1(localXSFacets, (short)2, (short)0);
    paramSymbolHash.put("IDREFS", localXSSimpleTypeDecl10);
    XSSimpleTypeDecl localXSSimpleTypeDecl11 = new XSSimpleTypeDecl(localXSSimpleTypeDecl7, "ENTITY", (short)23, (short)0, false, false, false, true, (short)29);
    paramSymbolHash.put("ENTITY", localXSSimpleTypeDecl11);
    minLength = 1;
    localXSSimpleTypeDecl9 = new XSSimpleTypeDecl(null, "http://www.w3.org/2001/XMLSchema", (short)0, localXSSimpleTypeDecl11, true, null);
    XSSimpleTypeDecl localXSSimpleTypeDecl12 = new XSSimpleTypeDecl(localXSSimpleTypeDecl9, "ENTITIES", "http://www.w3.org/2001/XMLSchema", (short)0, false, null);
    localXSSimpleTypeDecl12.applyFacets1(localXSFacets, (short)2, (short)0);
    paramSymbolHash.put("ENTITIES", localXSSimpleTypeDecl12);
    whiteSpace = 2;
    XSSimpleTypeDecl localXSSimpleTypeDecl13 = new XSSimpleTypeDecl(localXSSimpleTypeDecl4, "NMTOKEN", "http://www.w3.org/2001/XMLSchema", (short)0, false, null, (short)24);
    localXSSimpleTypeDecl13.applyFacets1(localXSFacets, (short)16, (short)0, (short)1);
    paramSymbolHash.put("NMTOKEN", localXSSimpleTypeDecl13);
    minLength = 1;
    localXSSimpleTypeDecl9 = new XSSimpleTypeDecl(null, "http://www.w3.org/2001/XMLSchema", (short)0, localXSSimpleTypeDecl13, true, null);
    XSSimpleTypeDecl localXSSimpleTypeDecl14 = new XSSimpleTypeDecl(localXSSimpleTypeDecl9, "NMTOKENS", "http://www.w3.org/2001/XMLSchema", (short)0, false, null);
    localXSSimpleTypeDecl14.applyFacets1(localXSFacets, (short)2, (short)0);
    paramSymbolHash.put("NMTOKENS", localXSSimpleTypeDecl14);
  }
  
  static
  {
    createBuiltInTypes(fFullTypes);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\impl\dv\xs\FullDVFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */