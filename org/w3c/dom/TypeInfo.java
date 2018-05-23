package org.w3c.dom;

public abstract interface TypeInfo
{
  public static final int DERIVATION_RESTRICTION = 1;
  public static final int DERIVATION_EXTENSION = 2;
  public static final int DERIVATION_UNION = 4;
  public static final int DERIVATION_LIST = 8;
  
  public abstract String getTypeName();
  
  public abstract String getTypeNamespace();
  
  public abstract boolean isDerivedFrom(String paramString1, String paramString2, int paramInt);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\w3c\dom\TypeInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */