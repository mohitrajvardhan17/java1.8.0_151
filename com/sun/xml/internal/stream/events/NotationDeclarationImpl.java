package com.sun.xml.internal.stream.events;

import com.sun.xml.internal.stream.dtd.nonvalidating.XMLNotationDecl;
import java.io.IOException;
import java.io.Writer;
import javax.xml.stream.events.NotationDeclaration;

public class NotationDeclarationImpl
  extends DummyEvent
  implements NotationDeclaration
{
  String fName = null;
  String fPublicId = null;
  String fSystemId = null;
  
  public NotationDeclarationImpl()
  {
    setEventType(14);
  }
  
  public NotationDeclarationImpl(String paramString1, String paramString2, String paramString3)
  {
    fName = paramString1;
    fPublicId = paramString2;
    fSystemId = paramString3;
    setEventType(14);
  }
  
  public NotationDeclarationImpl(XMLNotationDecl paramXMLNotationDecl)
  {
    fName = name;
    fPublicId = publicId;
    fSystemId = systemId;
    setEventType(14);
  }
  
  public String getName()
  {
    return fName;
  }
  
  public String getPublicId()
  {
    return fPublicId;
  }
  
  public String getSystemId()
  {
    return fSystemId;
  }
  
  void setPublicId(String paramString)
  {
    fPublicId = paramString;
  }
  
  void setSystemId(String paramString)
  {
    fSystemId = paramString;
  }
  
  void setName(String paramString)
  {
    fName = paramString;
  }
  
  protected void writeAsEncodedUnicodeEx(Writer paramWriter)
    throws IOException
  {
    paramWriter.write("<!NOTATION ");
    paramWriter.write(getName());
    if (fPublicId != null)
    {
      paramWriter.write(" PUBLIC \"");
      paramWriter.write(fPublicId);
      paramWriter.write("\"");
    }
    else if (fSystemId != null)
    {
      paramWriter.write(" SYSTEM");
      paramWriter.write(" \"");
      paramWriter.write(fSystemId);
      paramWriter.write("\"");
    }
    paramWriter.write(62);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\stream\events\NotationDeclarationImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */