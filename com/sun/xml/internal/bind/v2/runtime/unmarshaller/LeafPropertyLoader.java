package com.sun.xml.internal.bind.v2.runtime.unmarshaller;

import com.sun.xml.internal.bind.api.AccessorException;
import com.sun.xml.internal.bind.v2.runtime.reflect.TransducedAccessor;
import org.xml.sax.SAXException;

public class LeafPropertyLoader
  extends Loader
{
  private final TransducedAccessor xacc;
  
  public LeafPropertyLoader(TransducedAccessor paramTransducedAccessor)
  {
    super(true);
    xacc = paramTransducedAccessor;
  }
  
  public void text(UnmarshallingContext.State paramState, CharSequence paramCharSequence)
    throws SAXException
  {
    try
    {
      xacc.parse(paramState.getPrev().getTarget(), paramCharSequence);
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


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\runtime\unmarshaller\LeafPropertyLoader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */