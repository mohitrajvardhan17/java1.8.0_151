package jdk.internal.util.xml;

public abstract interface XMLStreamWriter
{
  public static final String DEFAULT_XML_VERSION = "1.0";
  public static final String DEFAULT_ENCODING = "UTF-8";
  
  public abstract void writeStartElement(String paramString)
    throws XMLStreamException;
  
  public abstract void writeEmptyElement(String paramString)
    throws XMLStreamException;
  
  public abstract void writeEndElement()
    throws XMLStreamException;
  
  public abstract void writeEndDocument()
    throws XMLStreamException;
  
  public abstract void close()
    throws XMLStreamException;
  
  public abstract void flush()
    throws XMLStreamException;
  
  public abstract void writeAttribute(String paramString1, String paramString2)
    throws XMLStreamException;
  
  public abstract void writeCData(String paramString)
    throws XMLStreamException;
  
  public abstract void writeDTD(String paramString)
    throws XMLStreamException;
  
  public abstract void writeStartDocument()
    throws XMLStreamException;
  
  public abstract void writeStartDocument(String paramString)
    throws XMLStreamException;
  
  public abstract void writeStartDocument(String paramString1, String paramString2)
    throws XMLStreamException;
  
  public abstract void writeCharacters(String paramString)
    throws XMLStreamException;
  
  public abstract void writeCharacters(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws XMLStreamException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\jdk\internal\util\xml\XMLStreamWriter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */