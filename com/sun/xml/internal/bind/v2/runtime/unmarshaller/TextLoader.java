package com.sun.xml.internal.bind.v2.runtime.unmarshaller;

import com.sun.xml.internal.bind.api.AccessorException;
import com.sun.xml.internal.bind.v2.runtime.Transducer;
import org.xml.sax.SAXException;

public class TextLoader
  extends Loader
{
  private final Transducer xducer;
  
  public TextLoader(Transducer paramTransducer)
  {
    super(true);
    xducer = paramTransducer;
  }
  
  public void text(UnmarshallingContext.State paramState, CharSequence paramCharSequence)
    throws SAXException
  {
    try
    {
      paramState.setTarget(xducer.parse(paramCharSequence));
    }
    catch (AccessorException localAccessorException)
    {
      handleGenericException(localAccessorException, true);
    }
    catch (RuntimeException localRuntimeException)
    {
      handleParseConversionException(paramState, localRuntimeException);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\runtime\unmarshaller\TextLoader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */