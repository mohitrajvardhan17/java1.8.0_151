package com.sun.xml.internal.ws.util.xml;

import com.sun.xml.internal.ws.api.streaming.XMLStreamWriterFactory;
import com.sun.xml.internal.ws.api.streaming.XMLStreamWriterFactory.RecycleAware;
import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public class XMLStreamWriterFilter
  implements XMLStreamWriter, XMLStreamWriterFactory.RecycleAware
{
  protected XMLStreamWriter writer;
  
  public XMLStreamWriterFilter(XMLStreamWriter paramXMLStreamWriter)
  {
    writer = paramXMLStreamWriter;
  }
  
  public void close()
    throws XMLStreamException
  {
    writer.close();
  }
  
  public void flush()
    throws XMLStreamException
  {
    writer.flush();
  }
  
  public void writeEndDocument()
    throws XMLStreamException
  {
    writer.writeEndDocument();
  }
  
  public void writeEndElement()
    throws XMLStreamException
  {
    writer.writeEndElement();
  }
  
  public void writeStartDocument()
    throws XMLStreamException
  {
    writer.writeStartDocument();
  }
  
  public void writeCharacters(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws XMLStreamException
  {
    writer.writeCharacters(paramArrayOfChar, paramInt1, paramInt2);
  }
  
  public void setDefaultNamespace(String paramString)
    throws XMLStreamException
  {
    writer.setDefaultNamespace(paramString);
  }
  
  public void writeCData(String paramString)
    throws XMLStreamException
  {
    writer.writeCData(paramString);
  }
  
  public void writeCharacters(String paramString)
    throws XMLStreamException
  {
    writer.writeCharacters(paramString);
  }
  
  public void writeComment(String paramString)
    throws XMLStreamException
  {
    writer.writeComment(paramString);
  }
  
  public void writeDTD(String paramString)
    throws XMLStreamException
  {
    writer.writeDTD(paramString);
  }
  
  public void writeDefaultNamespace(String paramString)
    throws XMLStreamException
  {
    writer.writeDefaultNamespace(paramString);
  }
  
  public void writeEmptyElement(String paramString)
    throws XMLStreamException
  {
    writer.writeEmptyElement(paramString);
  }
  
  public void writeEntityRef(String paramString)
    throws XMLStreamException
  {
    writer.writeEntityRef(paramString);
  }
  
  public void writeProcessingInstruction(String paramString)
    throws XMLStreamException
  {
    writer.writeProcessingInstruction(paramString);
  }
  
  public void writeStartDocument(String paramString)
    throws XMLStreamException
  {
    writer.writeStartDocument(paramString);
  }
  
  public void writeStartElement(String paramString)
    throws XMLStreamException
  {
    writer.writeStartElement(paramString);
  }
  
  public NamespaceContext getNamespaceContext()
  {
    return writer.getNamespaceContext();
  }
  
  public void setNamespaceContext(NamespaceContext paramNamespaceContext)
    throws XMLStreamException
  {
    writer.setNamespaceContext(paramNamespaceContext);
  }
  
  public Object getProperty(String paramString)
    throws IllegalArgumentException
  {
    return writer.getProperty(paramString);
  }
  
  public String getPrefix(String paramString)
    throws XMLStreamException
  {
    return writer.getPrefix(paramString);
  }
  
  public void setPrefix(String paramString1, String paramString2)
    throws XMLStreamException
  {
    writer.setPrefix(paramString1, paramString2);
  }
  
  public void writeAttribute(String paramString1, String paramString2)
    throws XMLStreamException
  {
    writer.writeAttribute(paramString1, paramString2);
  }
  
  public void writeEmptyElement(String paramString1, String paramString2)
    throws XMLStreamException
  {
    writer.writeEmptyElement(paramString1, paramString2);
  }
  
  public void writeNamespace(String paramString1, String paramString2)
    throws XMLStreamException
  {
    writer.writeNamespace(paramString1, paramString2);
  }
  
  public void writeProcessingInstruction(String paramString1, String paramString2)
    throws XMLStreamException
  {
    writer.writeProcessingInstruction(paramString1, paramString2);
  }
  
  public void writeStartDocument(String paramString1, String paramString2)
    throws XMLStreamException
  {
    writer.writeStartDocument(paramString1, paramString2);
  }
  
  public void writeStartElement(String paramString1, String paramString2)
    throws XMLStreamException
  {
    writer.writeStartElement(paramString1, paramString2);
  }
  
  public void writeAttribute(String paramString1, String paramString2, String paramString3)
    throws XMLStreamException
  {
    writer.writeAttribute(paramString1, paramString2, paramString3);
  }
  
  public void writeEmptyElement(String paramString1, String paramString2, String paramString3)
    throws XMLStreamException
  {
    writer.writeEmptyElement(paramString1, paramString2, paramString3);
  }
  
  public void writeStartElement(String paramString1, String paramString2, String paramString3)
    throws XMLStreamException
  {
    writer.writeStartElement(paramString1, paramString2, paramString3);
  }
  
  public void writeAttribute(String paramString1, String paramString2, String paramString3, String paramString4)
    throws XMLStreamException
  {
    writer.writeAttribute(paramString1, paramString2, paramString3, paramString4);
  }
  
  public void onRecycled()
  {
    XMLStreamWriterFactory.recycle(writer);
    writer = null;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\util\xml\XMLStreamWriterFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */