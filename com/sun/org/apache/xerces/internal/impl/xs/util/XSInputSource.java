package com.sun.org.apache.xerces.internal.impl.xs.util;

import com.sun.org.apache.xerces.internal.impl.xs.SchemaGrammar;
import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
import com.sun.org.apache.xerces.internal.xs.XSObject;

public final class XSInputSource
  extends XMLInputSource
{
  private SchemaGrammar[] fGrammars;
  private XSObject[] fComponents;
  
  public XSInputSource(SchemaGrammar[] paramArrayOfSchemaGrammar)
  {
    super(null, null, null);
    fGrammars = paramArrayOfSchemaGrammar;
    fComponents = null;
  }
  
  public XSInputSource(XSObject[] paramArrayOfXSObject)
  {
    super(null, null, null);
    fGrammars = null;
    fComponents = paramArrayOfXSObject;
  }
  
  public SchemaGrammar[] getGrammars()
  {
    return fGrammars;
  }
  
  public void setGrammars(SchemaGrammar[] paramArrayOfSchemaGrammar)
  {
    fGrammars = paramArrayOfSchemaGrammar;
  }
  
  public XSObject[] getComponents()
  {
    return fComponents;
  }
  
  public void setComponents(XSObject[] paramArrayOfXSObject)
  {
    fComponents = paramArrayOfXSObject;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\impl\xs\util\XSInputSource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */