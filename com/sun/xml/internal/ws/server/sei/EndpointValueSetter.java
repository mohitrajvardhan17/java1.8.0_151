package com.sun.xml.internal.ws.server.sei;

import com.sun.xml.internal.ws.model.ParameterImpl;
import javax.xml.ws.Holder;

public abstract class EndpointValueSetter
{
  private static final EndpointValueSetter[] POOL = new EndpointValueSetter[16];
  
  private EndpointValueSetter() {}
  
  abstract void put(Object paramObject, Object[] paramArrayOfObject);
  
  public static EndpointValueSetter get(ParameterImpl paramParameterImpl)
  {
    int i = paramParameterImpl.getIndex();
    if (paramParameterImpl.isIN())
    {
      if (i < POOL.length) {
        return POOL[i];
      }
      return new Param(i);
    }
    return new HolderParam(i);
  }
  
  static
  {
    for (int i = 0; i < POOL.length; i++) {
      POOL[i] = new Param(i);
    }
  }
  
  static final class HolderParam
    extends EndpointValueSetter.Param
  {
    public HolderParam(int paramInt)
    {
      super();
    }
    
    void put(Object paramObject, Object[] paramArrayOfObject)
    {
      Holder localHolder = new Holder();
      if (paramObject != null) {
        value = paramObject;
      }
      paramArrayOfObject[idx] = localHolder;
    }
  }
  
  static class Param
    extends EndpointValueSetter
  {
    protected final int idx;
    
    public Param(int paramInt)
    {
      super();
      idx = paramInt;
    }
    
    void put(Object paramObject, Object[] paramArrayOfObject)
    {
      if (paramObject != null) {
        paramArrayOfObject[idx] = paramObject;
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\server\sei\EndpointValueSetter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */