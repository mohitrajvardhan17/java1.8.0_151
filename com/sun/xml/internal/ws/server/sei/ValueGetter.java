package com.sun.xml.internal.ws.server.sei;

import com.sun.xml.internal.ws.model.ParameterImpl;
import javax.jws.WebParam.Mode;
import javax.xml.ws.Holder;

public enum ValueGetter
{
  PLAIN,  HOLDER;
  
  private ValueGetter() {}
  
  public abstract Object get(Object paramObject);
  
  public static ValueGetter get(ParameterImpl paramParameterImpl)
  {
    if ((paramParameterImpl.getMode() == WebParam.Mode.IN) || (paramParameterImpl.getIndex() == -1)) {
      return PLAIN;
    }
    return HOLDER;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\server\sei\ValueGetter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */