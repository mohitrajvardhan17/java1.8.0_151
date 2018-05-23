package com.sun.org.apache.xerces.internal.impl.dv;

import com.sun.org.apache.xerces.internal.util.SymbolHash;
import com.sun.org.apache.xerces.internal.utils.ObjectFactory;
import com.sun.org.apache.xerces.internal.xs.XSObjectList;

public abstract class SchemaDVFactory
{
  private static final String DEFAULT_FACTORY_CLASS = "com.sun.org.apache.xerces.internal.impl.dv.xs.SchemaDVFactoryImpl";
  
  public static final synchronized SchemaDVFactory getInstance()
    throws DVFactoryException
  {
    return getInstance("com.sun.org.apache.xerces.internal.impl.dv.xs.SchemaDVFactoryImpl");
  }
  
  public static final synchronized SchemaDVFactory getInstance(String paramString)
    throws DVFactoryException
  {
    try
    {
      return (SchemaDVFactory)ObjectFactory.newInstance(paramString, true);
    }
    catch (ClassCastException localClassCastException)
    {
      throw new DVFactoryException("Schema factory class " + paramString + " does not extend from SchemaDVFactory.");
    }
  }
  
  protected SchemaDVFactory() {}
  
  public abstract XSSimpleType getBuiltInType(String paramString);
  
  public abstract SymbolHash getBuiltInTypes();
  
  public abstract XSSimpleType createTypeRestriction(String paramString1, String paramString2, short paramShort, XSSimpleType paramXSSimpleType, XSObjectList paramXSObjectList);
  
  public abstract XSSimpleType createTypeList(String paramString1, String paramString2, short paramShort, XSSimpleType paramXSSimpleType, XSObjectList paramXSObjectList);
  
  public abstract XSSimpleType createTypeUnion(String paramString1, String paramString2, short paramShort, XSSimpleType[] paramArrayOfXSSimpleType, XSObjectList paramXSObjectList);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\impl\dv\SchemaDVFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */