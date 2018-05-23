package com.sun.xml.internal.ws.client.sei;

import com.sun.xml.internal.ws.model.ParameterImpl;
import javax.jws.WebParam.Mode;

abstract class ValueGetterFactory
{
  static final ValueGetterFactory SYNC = new ValueGetterFactory()
  {
    ValueGetter get(ParameterImpl paramAnonymousParameterImpl)
    {
      return (paramAnonymousParameterImpl.getMode() == WebParam.Mode.IN) || (paramAnonymousParameterImpl.getIndex() == -1) ? ValueGetter.PLAIN : ValueGetter.HOLDER;
    }
  };
  static final ValueGetterFactory ASYNC = new ValueGetterFactory()
  {
    ValueGetter get(ParameterImpl paramAnonymousParameterImpl)
    {
      return ValueGetter.PLAIN;
    }
  };
  
  ValueGetterFactory() {}
  
  abstract ValueGetter get(ParameterImpl paramParameterImpl);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\client\sei\ValueGetterFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */