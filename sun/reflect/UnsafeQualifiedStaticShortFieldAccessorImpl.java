package sun.reflect;

import java.lang.reflect.Field;
import sun.misc.Unsafe;

class UnsafeQualifiedStaticShortFieldAccessorImpl
  extends UnsafeQualifiedStaticFieldAccessorImpl
{
  UnsafeQualifiedStaticShortFieldAccessorImpl(Field paramField, boolean paramBoolean)
  {
    super(paramField, paramBoolean);
  }
  
  public Object get(Object paramObject)
    throws IllegalArgumentException
  {
    return new Short(getShort(paramObject));
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
    return unsafe.getShortVolatile(base, fieldOffset);
  }
  
  public int getInt(Object paramObject)
    throws IllegalArgumentException
  {
    return getShort(paramObject);
  }
  
  public long getLong(Object paramObject)
    throws IllegalArgumentException
  {
    return getShort(paramObject);
  }
  
  public float getFloat(Object paramObject)
    throws IllegalArgumentException
  {
    return getShort(paramObject);
  }
  
  public double getDouble(Object paramObject)
    throws IllegalArgumentException
  {
    return getShort(paramObject);
  }
  
  public void set(Object paramObject1, Object paramObject2)
    throws IllegalArgumentException, IllegalAccessException
  {
    if (isReadOnly) {
      throwFinalFieldIllegalAccessException(paramObject2);
    }
    if (paramObject2 == null) {
      throwSetIllegalArgumentException(paramObject2);
    }
    if ((paramObject2 instanceof Byte))
    {
      unsafe.putShortVolatile(base, fieldOffset, (short)((Byte)paramObject2).byteValue());
      return;
    }
    if ((paramObject2 instanceof Short))
    {
      unsafe.putShortVolatile(base, fieldOffset, ((Short)paramObject2).shortValue());
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
    setShort(paramObject, (short)paramByte);
  }
  
  public void setChar(Object paramObject, char paramChar)
    throws IllegalArgumentException, IllegalAccessException
  {
    throwSetIllegalArgumentException(paramChar);
  }
  
  public void setShort(Object paramObject, short paramShort)
    throws IllegalArgumentException, IllegalAccessException
  {
    if (isReadOnly) {
      throwFinalFieldIllegalAccessException(paramShort);
    }
    unsafe.putShortVolatile(base, fieldOffset, paramShort);
  }
  
  public void setInt(Object paramObject, int paramInt)
    throws IllegalArgumentException, IllegalAccessException
  {
    throwSetIllegalArgumentException(paramInt);
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


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\reflect\UnsafeQualifiedStaticShortFieldAccessorImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */