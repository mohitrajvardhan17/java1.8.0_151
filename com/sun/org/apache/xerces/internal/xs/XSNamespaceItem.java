package com.sun.org.apache.xerces.internal.xs;

public abstract interface XSNamespaceItem
{
  public abstract String getSchemaNamespace();
  
  public abstract XSNamedMap getComponents(short paramShort);
  
  public abstract XSObjectList getAnnotations();
  
  public abstract XSElementDeclaration getElementDeclaration(String paramString);
  
  public abstract XSAttributeDeclaration getAttributeDeclaration(String paramString);
  
  public abstract XSTypeDefinition getTypeDefinition(String paramString);
  
  public abstract XSAttributeGroupDefinition getAttributeGroup(String paramString);
  
  public abstract XSModelGroupDefinition getModelGroupDefinition(String paramString);
  
  public abstract XSNotationDeclaration getNotationDeclaration(String paramString);
  
  public abstract StringList getDocumentLocations();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\xs\XSNamespaceItem.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */