package com.sun.corba.se.impl.orbutil;

import java.io.IOException;
import java.io.ObjectInputStream.GetField;
import java.io.ObjectStreamClass;
import java.util.Hashtable;

class LegacyHookGetFields
  extends ObjectInputStream.GetField
{
  private Hashtable fields = null;
  
  LegacyHookGetFields(Hashtable paramHashtable)
  {
    fields = paramHashtable;
  }
  
  public ObjectStreamClass getObjectStreamClass()
  {
    return null;
  }
  
  public boolean defaulted(String paramString)
    throws IOException, IllegalArgumentException
  {
    return !fields.containsKey(paramString);
  }
  
  public boolean get(String paramString, boolean paramBoolean)
    throws IOException, IllegalArgumentException
  {
    if (defaulted(paramString)) {
      return paramBoolean;
    }
    return ((Boolean)fields.get(paramString)).booleanValue();
  }
  
  public char get(String paramString, char paramChar)
    throws IOException, IllegalArgumentException
  {
    if (defaulted(paramString)) {
      return paramChar;
    }
    return ((Character)fields.get(paramString)).charValue();
  }
  
  public byte get(String paramString, byte paramByte)
    throws IOException, IllegalArgumentException
  {
    if (defaulted(paramString)) {
      return paramByte;
    }
    return ((Byte)fields.get(paramString)).byteValue();
  }
  
  public short get(String paramString, short paramShort)
    throws IOException, IllegalArgumentException
  {
    if (defaulted(paramString)) {
      return paramShort;
    }
    return ((Short)fields.get(paramString)).shortValue();
  }
  
  public int get(String paramString, int paramInt)
    throws IOException, IllegalArgumentException
  {
    if (defaulted(paramString)) {
      return paramInt;
    }
    return ((Integer)fields.get(paramString)).intValue();
  }
  
  public long get(String paramString, long paramLong)
    throws IOException, IllegalArgumentException
  {
    if (defaulted(paramString)) {
      return paramLong;
    }
    return ((Long)fields.get(paramString)).longValue();
  }
  
  public float get(String paramString, float paramFloat)
    throws IOException, IllegalArgumentException
  {
    if (defaulted(paramString)) {
      return paramFloat;
    }
    return ((Float)fields.get(paramString)).floatValue();
  }
  
  public double get(String paramString, double paramDouble)
    throws IOException, IllegalArgumentException
  {
    if (defaulted(paramString)) {
      return paramDouble;
    }
    return ((Double)fields.get(paramString)).doubleValue();
  }
  
  public Object get(String paramString, Object paramObject)
    throws IOException, IllegalArgumentException
  {
    if (defaulted(paramString)) {
      return paramObject;
    }
    return fields.get(paramString);
  }
  
  public String toString()
  {
    return fields.toString();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\orbutil\LegacyHookGetFields.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */