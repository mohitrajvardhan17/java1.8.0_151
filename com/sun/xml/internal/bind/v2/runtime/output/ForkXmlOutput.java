package com.sun.xml.internal.bind.v2.runtime.output;

import com.sun.xml.internal.bind.v2.runtime.Name;
import com.sun.xml.internal.bind.v2.runtime.XMLSerializer;
import java.io.IOException;
import javax.xml.stream.XMLStreamException;
import org.xml.sax.SAXException;

public final class ForkXmlOutput
  extends XmlOutputAbstractImpl
{
  private final XmlOutput lhs;
  private final XmlOutput rhs;
  
  public ForkXmlOutput(XmlOutput paramXmlOutput1, XmlOutput paramXmlOutput2)
  {
    lhs = paramXmlOutput1;
    rhs = paramXmlOutput2;
  }
  
  public void startDocument(XMLSerializer paramXMLSerializer, boolean paramBoolean, int[] paramArrayOfInt, NamespaceContextImpl paramNamespaceContextImpl)
    throws IOException, SAXException, XMLStreamException
  {
    lhs.startDocument(paramXMLSerializer, paramBoolean, paramArrayOfInt, paramNamespaceContextImpl);
    rhs.startDocument(paramXMLSerializer, paramBoolean, paramArrayOfInt, paramNamespaceContextImpl);
  }
  
  public void endDocument(boolean paramBoolean)
    throws IOException, SAXException, XMLStreamException
  {
    lhs.endDocument(paramBoolean);
    rhs.endDocument(paramBoolean);
  }
  
  public void beginStartTag(Name paramName)
    throws IOException, XMLStreamException
  {
    lhs.beginStartTag(paramName);
    rhs.beginStartTag(paramName);
  }
  
  public void attribute(Name paramName, String paramString)
    throws IOException, XMLStreamException
  {
    lhs.attribute(paramName, paramString);
    rhs.attribute(paramName, paramString);
  }
  
  public void endTag(Name paramName)
    throws IOException, SAXException, XMLStreamException
  {
    lhs.endTag(paramName);
    rhs.endTag(paramName);
  }
  
  public void beginStartTag(int paramInt, String paramString)
    throws IOException, XMLStreamException
  {
    lhs.beginStartTag(paramInt, paramString);
    rhs.beginStartTag(paramInt, paramString);
  }
  
  public void attribute(int paramInt, String paramString1, String paramString2)
    throws IOException, XMLStreamException
  {
    lhs.attribute(paramInt, paramString1, paramString2);
    rhs.attribute(paramInt, paramString1, paramString2);
  }
  
  public void endStartTag()
    throws IOException, SAXException
  {
    lhs.endStartTag();
    rhs.endStartTag();
  }
  
  public void endTag(int paramInt, String paramString)
    throws IOException, SAXException, XMLStreamException
  {
    lhs.endTag(paramInt, paramString);
    rhs.endTag(paramInt, paramString);
  }
  
  public void text(String paramString, boolean paramBoolean)
    throws IOException, SAXException, XMLStreamException
  {
    lhs.text(paramString, paramBoolean);
    rhs.text(paramString, paramBoolean);
  }
  
  public void text(Pcdata paramPcdata, boolean paramBoolean)
    throws IOException, SAXException, XMLStreamException
  {
    lhs.text(paramPcdata, paramBoolean);
    rhs.text(paramPcdata, paramBoolean);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\runtime\output\ForkXmlOutput.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */