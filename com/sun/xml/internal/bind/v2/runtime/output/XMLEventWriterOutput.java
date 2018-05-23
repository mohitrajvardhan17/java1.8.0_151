package com.sun.xml.internal.bind.v2.runtime.output;

import com.sun.xml.internal.bind.v2.runtime.XMLSerializer;
import java.io.IOException;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Characters;
import org.xml.sax.SAXException;

public class XMLEventWriterOutput
  extends XmlOutputAbstractImpl
{
  private final XMLEventWriter out;
  private final XMLEventFactory ef;
  private final Characters sp;
  
  public XMLEventWriterOutput(XMLEventWriter paramXMLEventWriter)
  {
    out = paramXMLEventWriter;
    ef = XMLEventFactory.newInstance();
    sp = ef.createCharacters(" ");
  }
  
  public void startDocument(XMLSerializer paramXMLSerializer, boolean paramBoolean, int[] paramArrayOfInt, NamespaceContextImpl paramNamespaceContextImpl)
    throws IOException, SAXException, XMLStreamException
  {
    super.startDocument(paramXMLSerializer, paramBoolean, paramArrayOfInt, paramNamespaceContextImpl);
    if (!paramBoolean) {
      out.add(ef.createStartDocument());
    }
  }
  
  public void endDocument(boolean paramBoolean)
    throws IOException, SAXException, XMLStreamException
  {
    if (!paramBoolean)
    {
      out.add(ef.createEndDocument());
      out.flush();
    }
    super.endDocument(paramBoolean);
  }
  
  public void beginStartTag(int paramInt, String paramString)
    throws IOException, XMLStreamException
  {
    out.add(ef.createStartElement(nsContext.getPrefix(paramInt), nsContext.getNamespaceURI(paramInt), paramString));
    NamespaceContextImpl.Element localElement = nsContext.getCurrent();
    if (localElement.count() > 0) {
      for (int i = localElement.count() - 1; i >= 0; i--)
      {
        String str = localElement.getNsUri(i);
        if ((str.length() != 0) || (localElement.getBase() != 1)) {
          out.add(ef.createNamespace(localElement.getPrefix(i), str));
        }
      }
    }
  }
  
  public void attribute(int paramInt, String paramString1, String paramString2)
    throws IOException, XMLStreamException
  {
    Attribute localAttribute;
    if (paramInt == -1) {
      localAttribute = ef.createAttribute(paramString1, paramString2);
    } else {
      localAttribute = ef.createAttribute(nsContext.getPrefix(paramInt), nsContext.getNamespaceURI(paramInt), paramString1, paramString2);
    }
    out.add(localAttribute);
  }
  
  public void endStartTag()
    throws IOException, SAXException
  {}
  
  public void endTag(int paramInt, String paramString)
    throws IOException, SAXException, XMLStreamException
  {
    out.add(ef.createEndElement(nsContext.getPrefix(paramInt), nsContext.getNamespaceURI(paramInt), paramString));
  }
  
  public void text(String paramString, boolean paramBoolean)
    throws IOException, SAXException, XMLStreamException
  {
    if (paramBoolean) {
      out.add(sp);
    }
    out.add(ef.createCharacters(paramString));
  }
  
  public void text(Pcdata paramPcdata, boolean paramBoolean)
    throws IOException, SAXException, XMLStreamException
  {
    text(paramPcdata.toString(), paramBoolean);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\runtime\output\XMLEventWriterOutput.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */