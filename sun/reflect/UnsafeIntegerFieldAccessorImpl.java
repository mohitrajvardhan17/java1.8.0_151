package sun.reflect;

import java.lang.reflect.Field;
import sun.misc.Unsafe;

class UnsafeIntegerFieldAccessorImpl
  extends UnsafeFieldAccessorImpl
{
  UnsafeIntegerFieldAccessorImpl(Field paramField)
  {
    super(paramField);
  }
  
  public Object get(Object paramObject)
    throws IllegalArgumentException
  {
    return new Integer(getInt(paramObject));
  }
  
  public boolean getBoolean(Object paramObject)
    throws IllegalArgumentException
  {
    throw newGetBooleanIllegalArgumentException();
  }
  
  public byte getByte(Object paramObject)
    throws IllegalArgumentException
  {
    throw newGetByteIllegalArgumentException();
  }
  
  public char getChar(Object paramObject)
    throws IllegalArgumentException
  {
    throw newGetCharIllegalArgumentException();
  }
  
  public short getShort(Object paramObject)
    throws IllegalArgumentException
  {
    throw newGetShortIllegalArgumentException();
  }
  
  public int getInt(Object paramObject)
    throws IllegalArgumentException
  {
    ensureObj(paramObject);
    return unsafe.getInt(paramObject, fieldOffset);
  }
  
  public long getLong(Object paramObject)
    throws IllegalArgumentException
  {
    return getInt(paramObject);
  }
  
  public float getFloat(Object paramObject)
    throws IllegalArgumentException
  {
    return getInt(paramObject);
  }
  
  public double getDouble(Object paramObject)
    throws IllegalArgumentException
  {
    return getInt(paramObject);
  }
  
  public void set(Object paramObject1, Object paramObject2)
    throws IllegalArgumentException, IllegalAccessException
  {
    ensureObj(paramObject1);
    if (isFinal) {
      throwFinalFieldIllegalAccessException(paramObject2);
    }
    if (paramObject2 == null) {
      throwSetIllegalArgumentException(paramObject2);
    }
    if ((paramObject2 instanceof Byte))
    {
      unsafe.putInt(paramObject1, fieldOffset, ((Byte)paramObject2).byteValue());
      return;
    }
    if ((paramObject2 instanceof Short))
    {
      unsafe.putInt(paramObject1, fieldOffset, ((Short)paramObject2).shortValue());
      return;
    }
    if ((paramObject2 instanceof Character))
    {
      unsafe.putInt(paramObject1, fieldOffset, ((Character)paramObject2).charValue());
      return;
    }
    if ((paramObject2 instanceof Integer))
    {
      unsafe.putInt(paramObject1, fieldOffset, ((Integer)paramObject2).intValue());
      return;
    }
    throwSetIllegalArgumentException(paramObject2);
  }
  
  public void setBoolean(Object paramObject, boolean paramBoolean)
    throws IllegalArgumentException, IllegalAccessException
  {
    throwSetIllegalArgumentException(paramBoolean);
  }
  
  public void setByte(Object paramObject, byte paramByte)
    throws IllegalArgumentException, IllegalAccessException
  {
    setInt(paramObject, paramByte);
  }
  
  public void setChar(Object paramObject, char paramChar)
    throws IllegalArgumentException, IllegalAccessException
  {
    setInt(paramObject, paramChar);
  }
  
  public void setShort(Object paramObject, short paramShort)
    throws IllegalArgumentException, IllegalAccessException
  {
    setInt(paramObject, paramShort);
  }
  
  public void setInt(Object paramObject, int paramInt)
    throws IllegalArgumentException, IllegalAccessException
  {
    ensureObj(paramObject);
    if (isFinal) {
      throwFinalFieldIllegalAccessException(paramInt);
    }
    unsafe.putInt(paramObject, fieldOffset, paramInt);
  }
  
  public void setLong(Object paramObject, long paramLong)
    throws IllegalArgumentException, IllegalAccessException
  {
    throwSetIllegalArgumentException(paramLong);
  }
  
  public void setFloat(Object paramObject, float paramFloat)
    throws IllegalArgumentException, IllegalAccessException
  {
    throwSetIllegalArgumentException(paramFloat);
  }
  
  public void setDouble(Object paramObject, double paramDouble)
    throws IllegalArgumentException, IllegalAccessException
  {
    throwSetIllegalArgumentException(paramDouble);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\reflect\UnsafeIntegerFieldAccessorImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */