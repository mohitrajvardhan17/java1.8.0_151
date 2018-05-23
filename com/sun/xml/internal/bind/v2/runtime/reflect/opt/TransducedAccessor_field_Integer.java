package com.sun.xml.internal.bind.v2.runtime.reflect.opt;

import com.sun.xml.internal.bind.DatatypeConverterImpl;
import com.sun.xml.internal.bind.api.AccessorException;
import com.sun.xml.internal.bind.v2.runtime.Name;
import com.sun.xml.internal.bind.v2.runtime.XMLSerializer;
import com.sun.xml.internal.bind.v2.runtime.reflect.DefaultTransducedAccessor;
import java.io.IOException;
import javax.xml.stream.XMLStreamException;
import org.xml.sax.SAXException;

public final class TransducedAccessor_field_Integer
  extends DefaultTransducedAccessor
{
  public TransducedAccessor_field_Integer() {}
  
  public String print(Object paramObject)
  {
    return DatatypeConverterImpl._printInt(f_int);
  }
  
  public void parse(Object paramObject, CharSequence paramCharSequence)
  {
    f_int = DatatypeConverterImpl._parseInt(paramCharSequence);
  }
  
  public boolean hasValue(Object paramObject)
  {
    return true;
  }
  
  public void writeLeafElement(XMLSerializer paramXMLSerializer, Name paramName, Object paramObject, String paramString)
    throws SAXException, AccessorException, IOException, XMLStreamException
  {
    paramXMLSerializer.leafElement(paramName, f_int, paramString);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\runtime\reflect\opt\TransducedAccessor_field_Integer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */