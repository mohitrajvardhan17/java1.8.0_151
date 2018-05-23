package sun.reflect;

import java.lang.reflect.Field;
import sun.misc.Unsafe;

class UnsafeStaticBooleanFieldAccessorImpl
  extends UnsafeStaticFieldAccessorImpl
{
  UnsafeStaticBooleanFieldAccessorImpl(Field paramField)
  {
    super(paramField);
  }
  
  public Object get(Object paramObject)
    throws IllegalArgumentException
  {
    return new Boolean(getBoolean(paramObject));
  }
  
  public boolean getBoolean(Object paramObject)
    throws IllegalArgumentException
  {
    return unsafe.getBoolean(base, fieldOffset);
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
    throw newGetIntIllegalArgumentException();
  }
  
  public long getLong(Object paramObject)
    throws IllegalArgumentException
  {
    throw newGetLongIllegalArgumentException();
  }
  
  public float getFloat(Object paramObject)
    throws IllegalArgumentException
  {
    throw newGetFloatIllegalArgumentException();
  }
  
  public double getDouble(Object paramObject)
    throws IllegalArgumentException
  {
    throw newGetDoubleIllegalArgumentException();
  }
  
  public void set(Object paramObject1, Object paramObject2)
    throws IllegalArgumentException, IllegalAccessException
  {
    if (isFinal) {
      throwFinalFieldIllegalAccessException(paramObject2);
    }
    if (paramObject2 == null) {
      throwSetIllegalArgumentException(paramObject2);
    }
    if ((paramObject2 instanceof Boolean))
    {
      unsafe.putBoolean(base, fieldOffset, ((Boolean)paramObject2).booleanValue());
      return;
    }
    throwSetIllegalArgumentException(paramObject2);
  }
  
  public void setBoolean(Object paramObject, boolean paramBoolean)
    throws IllegalArgumentException, IllegalAccessException
  {
    if (isFinal) {
      throwFinalFieldIllegalAccessException(paramBoolean);
    }
    unsafe.putBoolean(base, fieldOffset, paramBoolean);
  }
  
  public void setByte(Object paramObject, byte paramByte)
    throws IllegalArgumentException, IllegalAccessException
  {
    throwSetIllegalArgumentException(paramByte);
  }
  
  public void setChar(Object paramObject, char paramChar)
    throws IllegalArgumentException, IllegalAccessException
  {
    throwSetIllegalArgumentException(paramChar);
  }
  
  public void setShort(Object paramObject, short paramShort)
    throws IllegalArgumentException, IllegalAccessException
  {
    throwSetIllegalArgumentException(paramShort);
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


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\reflect\UnsafeStaticBooleanFieldAccessorImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */