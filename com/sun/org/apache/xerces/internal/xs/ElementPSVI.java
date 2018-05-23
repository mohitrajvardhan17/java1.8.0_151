package com.sun.org.apache.xerces.internal.xs;

public abstract interface ElementPSVI
  extends ItemPSVI
{
  public abstract XSElementDeclaration getElementDeclaration();
  
  public abstract XSNotationDeclaration getNotation();
  
  public abstract boolean getNil();
  
  public abstract XSModel getSchemaInformation();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\xs\ElementPSVI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */