package com.sun.xml.internal.ws.client.sei;

import com.sun.xml.internal.ws.model.ParameterImpl;
import javax.xml.ws.WebServiceException;

public abstract class ValueSetterFactory
{
  public static final ValueSetterFactory SYNC = new ValueSetterFactory()
  {
    public ValueSetter get(ParameterImpl paramAnonymousParameterImpl)
    {
      return ValueSetter.getSync(paramAnonymousParameterImpl);
    }
  };
  public static final ValueSetterFactory NONE = new ValueSetterFactory()
  {
    public ValueSetter get(ParameterImpl paramAnonymousParameterImpl)
    {
      throw new WebServiceException("This shouldn't happen. No response parameters.");
    }
  };
  public static final ValueSetterFactory SINGLE = new ValueSetterFactory()
  {
    public ValueSetter get(ParameterImpl paramAnonymousParameterImpl)
    {
      return ValueSetter.SINGLE_VALUE;
    }
  };
  
  public ValueSetterFactory() {}
  
  public abstract ValueSetter get(ParameterImpl paramParameterImpl);
  
  public static final class AsyncBeanValueSetterFactory
    extends ValueSetterFactory
  {
    private Class asyncBean;
    
    public AsyncBeanValueSetterFactory(Class paramClass)
    {
      asyncBean = paramClass;
    }
    
    public ValueSetter get(ParameterImpl paramParameterImpl)
    {
      return new ValueSetter.AsyncBeanValueSetter(paramParameterImpl, asyncBean);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\client\sei\ValueSetterFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */