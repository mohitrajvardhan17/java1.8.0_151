package com.sun.xml.internal.txw2.output;

import com.sun.xml.internal.txw2.TxwException;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public class StaxSerializer
  implements XmlSerializer
{
  private final XMLStreamWriter out;
  
  public StaxSerializer(XMLStreamWriter paramXMLStreamWriter)
  {
    this(paramXMLStreamWriter, true);
  }
  
  public StaxSerializer(XMLStreamWriter paramXMLStreamWriter, boolean paramBoolean)
  {
    if (paramBoolean) {
      paramXMLStreamWriter = new IndentingXMLStreamWriter(paramXMLStreamWriter);
    }
    out = paramXMLStreamWriter;
  }
  
  public void startDocument()
  {
    try
    {
      out.writeStartDocument();
    }
    catch (XMLStreamException localXMLStreamException)
    {
      throw new TxwException(localXMLStreamException);
    }
  }
  
  public void beginStartTag(String paramString1, String paramString2, String paramString3)
  {
    try
    {
      out.writeStartElement(paramString3, paramString2, paramString1);
    }
    catch (XMLStreamException localXMLStreamException)
    {
      throw new TxwException(localXMLStreamException);
    }
  }
  
  public void writeAttribute(String paramString1, String paramString2, String paramString3, StringBuilder paramStringBuilder)
  {
    try
    {
      out.writeAttribute(paramString3, paramString1, paramString2, paramStringBuilder.toString());
    }
    catch (XMLStreamException localXMLStreamException)
    {
      throw new TxwException(localXMLStreamException);
    }
  }
  
  public void writeXmlns(String paramString1, String paramString2)
  {
    try
    {
      if (paramString1.length() == 0) {
        out.setDefaultNamespace(paramString2);
      } else {
        out.setPrefix(paramString1, paramString2);
      }
      out.writeNamespace(paramString1, paramString2);
    }
    catch (XMLStreamException localXMLStreamException)
    {
      throw new TxwException(localXMLStreamException);
    }
  }
  
  public void endStartTag(String paramString1, String paramString2, String paramString3) {}
  
  public void endTag()
  {
    try
    {
      out.writeEndElement();
    }
    catch (XMLStreamException localXMLStreamException)
    {
      throw new TxwException(localXMLStreamException);
    }
  }
  
  public void text(StringBuilder paramStringBuilder)
  {
    try
    {
      out.writeCharacters(paramStringBuilder.toString());
    }
    catch (XMLStreamException localXMLStreamException)
    {
      throw new TxwException(localXMLStreamException);
    }
  }
  
  public void cdata(StringBuilder paramStringBuilder)
  {
    try
    {
      out.writeCData(paramStringBuilder.toString());
    }
    catch (XMLStreamException localXMLStreamException)
    {
      throw new TxwException(localXMLStreamException);
    }
  }
  
  public void comment(StringBuilder paramStringBuilder)
  {
    try
    {
      out.writeComment(paramStringBuilder.toString());
    }
    catch (XMLStreamException localXMLStreamException)
    {
      throw new TxwException(localXMLStreamException);
    }
  }
  
  public void endDocument()
  {
    try
    {
      out.writeEndDocument();
      out.flush();
    }
    catch (XMLStreamException localXMLStreamException)
    {
      throw new TxwException(localXMLStreamException);
    }
  }
  
  public void flush()
  {
    try
    {
      out.flush();
    }
    catch (XMLStreamException localXMLStreamException)
    {
      throw new TxwException(localXMLStreamException);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\txw2\output\StaxSerializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */