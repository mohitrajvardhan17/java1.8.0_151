package com.sun.xml.internal.stream.events;

import com.sun.org.apache.xerces.internal.xni.XMLResourceIdentifier;
import java.io.IOException;
import java.io.Writer;
import javax.xml.stream.events.EntityDeclaration;

public class EntityDeclarationImpl
  extends DummyEvent
  implements EntityDeclaration
{
  private XMLResourceIdentifier fXMLResourceIdentifier;
  private String fEntityName;
  private String fReplacementText;
  private String fNotationName;
  
  public EntityDeclarationImpl()
  {
    init();
  }
  
  public EntityDeclarationImpl(String paramString1, String paramString2)
  {
    this(paramString1, paramString2, null);
  }
  
  public EntityDeclarationImpl(String paramString1, String paramString2, XMLResourceIdentifier paramXMLResourceIdentifier)
  {
    init();
    fEntityName = paramString1;
    fReplacementText = paramString2;
    fXMLResourceIdentifier = paramXMLResourceIdentifier;
  }
  
  public void setEntityName(String paramString)
  {
    fEntityName = paramString;
  }
  
  public String getEntityName()
  {
    return fEntityName;
  }
  
  public void setEntityReplacementText(String paramString)
  {
    fReplacementText = paramString;
  }
  
  public void setXMLResourceIdentifier(XMLResourceIdentifier paramXMLResourceIdentifier)
  {
    fXMLResourceIdentifier = paramXMLResourceIdentifier;
  }
  
  public XMLResourceIdentifier getXMLResourceIdentifier()
  {
    return fXMLResourceIdentifier;
  }
  
  public String getSystemId()
  {
    if (fXMLResourceIdentifier != null) {
      return fXMLResourceIdentifier.getLiteralSystemId();
    }
    return null;
  }
  
  public String getPublicId()
  {
    if (fXMLResourceIdentifier != null) {
      return fXMLResourceIdentifier.getPublicId();
    }
    return null;
  }
  
  public String getBaseURI()
  {
    if (fXMLResourceIdentifier != null) {
      return fXMLResourceIdentifier.getBaseSystemId();
    }
    return null;
  }
  
  public String getName()
  {
    return fEntityName;
  }
  
  public String getNotationName()
  {
    return fNotationName;
  }
  
  public void setNotationName(String paramString)
  {
    fNotationName = paramString;
  }
  
  public String getReplacementText()
  {
    return fReplacementText;
  }
  
  protected void init()
  {
    setEventType(15);
  }
  
  protected void writeAsEncodedUnicodeEx(Writer paramWriter)
    throws IOException
  {
    paramWriter.write("<!ENTITY ");
    paramWriter.write(fEntityName);
    if (fReplacementText != null)
    {
      paramWriter.write(" \"");
      charEncode(paramWriter, fReplacementText);
    }
    else
    {
      String str = getPublicId();
      if (str != null)
      {
        paramWriter.write(" PUBLIC \"");
        paramWriter.write(str);
      }
      else
      {
        paramWriter.write(" SYSTEM \"");
        paramWriter.write(getSystemId());
      }
    }
    paramWriter.write("\"");
    if (fNotationName != null)
    {
      paramWriter.write(" NDATA ");
      paramWriter.write(fNotationName);
    }
    paramWriter.write(">");
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\stream\events\EntityDeclarationImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */