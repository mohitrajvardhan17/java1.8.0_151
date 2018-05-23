package com.sun.xml.internal.txw2.output;

import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

abstract class DelegatingXMLStreamWriter
  implements XMLStreamWriter
{
  private final XMLStreamWriter writer;
  
  public DelegatingXMLStreamWriter(XMLStreamWriter paramXMLStreamWriter)
  {
    writer = paramXMLStreamWriter;
  }
  
  public void writeStartElement(String paramString)
    throws XMLStreamException
  {
    writer.writeStartElement(paramString);
  }
  
  public void writeStartElement(String paramString1, String paramString2)
    throws XMLStreamException
  {
    writer.writeStartElement(paramString1, paramString2);
  }
  
  public void writeStartElement(String paramString1, String paramString2, String paramString3)
    throws XMLStreamException
  {
    writer.writeStartElement(paramString1, paramString2, paramString3);
  }
  
  public void writeEmptyElement(String paramString1, String paramString2)
    throws XMLStreamException
  {
    writer.writeEmptyElement(paramString1, paramString2);
  }
  
  public void writeEmptyElement(String paramString1, String paramString2, String paramString3)
    throws XMLStreamException
  {
    writer.writeEmptyElement(paramString1, paramString2, paramString3);
  }
  
  public void writeEmptyElement(String paramString)
    throws XMLStreamException
  {
    writer.writeEmptyElement(paramString);
  }
  
  public void writeEndElement()
    throws XMLStreamException
  {
    writer.writeEndElement();
  }
  
  public void writeEndDocument()
    throws XMLStreamException
  {
    writer.writeEndDocument();
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
  
  public void writeAttribute(String paramString1, String paramString2)
    throws XMLStreamException
  {
    writer.writeAttribute(paramString1, paramString2);
  }
  
  public void writeAttribute(String paramString1, String paramString2, String paramString3, String paramString4)
    throws XMLStreamException
  {
    writer.writeAttribute(paramString1, paramString2, paramString3, paramString4);
  }
  
  public void writeAttribute(String paramString1, String paramString2, String paramString3)
    throws XMLStreamException
  {
    writer.writeAttribute(paramString1, paramString2, paramString3);
  }
  
  public void writeNamespace(String paramString1, String paramString2)
    throws XMLStreamException
  {
    writer.writeNamespace(paramString1, paramString2);
  }
  
  public void writeDefaultNamespace(String paramString)
    throws XMLStreamException
  {
    writer.writeDefaultNamespace(paramString);
  }
  
  public void writeComment(String paramString)
    throws XMLStreamException
  {
    writer.writeComment(paramString);
  }
  
  public void writeProcessingInstruction(String paramString)
    throws XMLStreamException
  {
    writer.writeProcessingInstruction(paramString);
  }
  
  public void writeProcessingInstruction(String paramString1, String paramString2)
    throws XMLStreamException
  {
    writer.writeProcessingInstruction(paramString1, paramString2);
  }
  
  public void writeCData(String paramString)
    throws XMLStreamException
  {
    writer.writeCData(paramString);
  }
  
  public void writeDTD(String paramString)
    throws XMLStreamException
  {
    writer.writeDTD(paramString);
  }
  
  public void writeEntityRef(String paramString)
    throws XMLStreamException
  {
    writer.writeEntityRef(paramString);
  }
  
  public void writeStartDocument()
    throws XMLStreamException
  {
    writer.writeStartDocument();
  }
  
  public void writeStartDocument(String paramString)
    throws XMLStreamException
  {
    writer.writeStartDocument(paramString);
  }
  
  public void writeStartDocument(String paramString1, String paramString2)
    throws XMLStreamException
  {
    writer.writeStartDocument(paramString1, paramString2);
  }
  
  public void writeCharacters(String paramString)
    throws XMLStreamException
  {
    writer.writeCharacters(paramString);
  }
  
  public void writeCharacters(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws XMLStreamException
  {
    writer.writeCharacters(paramArrayOfChar, paramInt1, paramInt2);
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
  
  public void setDefaultNamespace(String paramString)
    throws XMLStreamException
  {
    writer.setDefaultNamespace(paramString);
  }
  
  public void setNamespaceContext(NamespaceContext paramNamespaceContext)
    throws XMLStreamException
  {
    writer.setNamespaceContext(paramNamespaceContext);
  }
  
  public NamespaceContext getNamespaceContext()
  {
    return writer.getNamespaceContext();
  }
  
  public Object getProperty(String paramString)
    throws IllegalArgumentException
  {
    return writer.getProperty(paramString);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\txw2\output\DelegatingXMLStreamWriter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */