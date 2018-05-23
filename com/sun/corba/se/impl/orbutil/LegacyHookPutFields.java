package com.sun.corba.se.impl.orbutil;

import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream.PutField;
import java.util.Hashtable;

class LegacyHookPutFields
  extends ObjectOutputStream.PutField
{
  private Hashtable fields = new Hashtable();
  
  LegacyHookPutFields() {}
  
  public void put(String paramString, boolean paramBoolean)
  {
    fields.put(paramString, new Boolean(paramBoolean));
  }
  
  public void put(String paramString, char paramChar)
  {
    fields.put(paramString, new Character(paramChar));
  }
  
  public void put(String paramString, byte paramByte)
  {
    fields.put(paramString, new Byte(paramByte));
  }
  
  public void put(String paramString, short paramShort)
  {
    fields.put(paramString, new Short(paramShort));
  }
  
  public void put(String paramString, int paramInt)
  {
    fields.put(paramString, new Integer(paramInt));
  }
  
  public void put(String paramString, long paramLong)
  {
    fields.put(paramString, new Long(paramLong));
  }
  
  public void put(String paramString, float paramFloat)
  {
    fields.put(paramString, new Float(paramFloat));
  }
  
  public void put(String paramString, double paramDouble)
  {
    fields.put(paramString, new Double(paramDouble));
  }
  
  public void put(String paramString, Object paramObject)
  {
    fields.put(paramString, paramObject);
  }
  
  public void write(ObjectOutput paramObjectOutput)
    throws IOException
  {
    paramObjectOutput.writeObject(fields);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\orbutil\LegacyHookPutFields.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */