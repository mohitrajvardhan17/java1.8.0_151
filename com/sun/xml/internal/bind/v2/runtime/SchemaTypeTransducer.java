package com.sun.xml.internal.bind.v2.runtime;

import com.sun.xml.internal.bind.api.AccessorException;
import java.io.IOException;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import org.xml.sax.SAXException;

public class SchemaTypeTransducer<V>
  extends FilterTransducer<V>
{
  private final QName schemaType;
  
  public SchemaTypeTransducer(Transducer<V> paramTransducer, QName paramQName)
  {
    super(paramTransducer);
    schemaType = paramQName;
  }
  
  public CharSequence print(V paramV)
    throws AccessorException
  {
    XMLSerializer localXMLSerializer = XMLSerializer.getInstance();
    QName localQName = localXMLSerializer.setSchemaType(schemaType);
    try
    {
      CharSequence localCharSequence = core.print(paramV);
      return localCharSequence;
    }
    finally
    {
      localXMLSerializer.setSchemaType(localQName);
    }
  }
  
  public void writeText(XMLSerializer paramXMLSerializer, V paramV, String paramString)
    throws IOException, SAXException, XMLStreamException, AccessorException
  {
    QName localQName = paramXMLSerializer.setSchemaType(schemaType);
    try
    {
      core.writeText(paramXMLSerializer, paramV, paramString);
    }
    finally
    {
      paramXMLSerializer.setSchemaType(localQName);
    }
  }
  
  public void writeLeafElement(XMLSerializer paramXMLSerializer, Name paramName, V paramV, String paramString)
    throws IOException, SAXException, XMLStreamException, AccessorException
  {
    QName localQName = paramXMLSerializer.setSchemaType(schemaType);
    try
    {
      core.writeLeafElement(paramXMLSerializer, paramName, paramV, paramString);
    }
    finally
    {
      paramXMLSerializer.setSchemaType(localQName);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\runtime\SchemaTypeTransducer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */