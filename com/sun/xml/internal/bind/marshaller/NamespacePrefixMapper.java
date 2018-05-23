package com.sun.xml.internal.bind.marshaller;

public abstract class NamespacePrefixMapper
{
  private static final String[] EMPTY_STRING = new String[0];
  
  public NamespacePrefixMapper() {}
  
  public abstract String getPreferredPrefix(String paramString1, String paramString2, boolean paramBoolean);
  
  public String[] getPreDeclaredNamespaceUris()
  {
    return EMPTY_STRING;
  }
  
  public String[] getPreDeclaredNamespaceUris2()
  {
    return EMPTY_STRING;
  }
  
  public String[] getContextualNamespaceDecls()
  {
    return EMPTY_STRING;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\marshaller\NamespacePrefixMapper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */