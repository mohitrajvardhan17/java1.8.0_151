package java.time.chrono;

import java.io.Externalizable;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.StreamCorruptedException;

final class Ser
  implements Externalizable
{
  private static final long serialVersionUID = -6103370247208168577L;
  static final byte CHRONO_TYPE = 1;
  static final byte CHRONO_LOCAL_DATE_TIME_TYPE = 2;
  static final byte CHRONO_ZONE_DATE_TIME_TYPE = 3;
  static final byte JAPANESE_DATE_TYPE = 4;
  static final byte JAPANESE_ERA_TYPE = 5;
  static final byte HIJRAH_DATE_TYPE = 6;
  static final byte MINGUO_DATE_TYPE = 7;
  static final byte THAIBUDDHIST_DATE_TYPE = 8;
  static final byte CHRONO_PERIOD_TYPE = 9;
  private byte type;
  private Object object;
  
  public Ser() {}
  
  Ser(byte paramByte, Object paramObject)
  {
    type = paramByte;
    object = paramObject;
  }
  
  public void writeExternal(ObjectOutput paramObjectOutput)
    throws IOException
  {
    writeInternal(type, object, paramObjectOutput);
  }
  
  private static void writeInternal(byte paramByte, Object paramObject, ObjectOutput paramObjectOutput)
    throws IOException
  {
    paramObjectOutput.writeByte(paramByte);
    switch (paramByte)
    {
    case 1: 
      ((AbstractChronology)paramObject).writeExternal(paramObjectOutput);
      break;
    case 2: 
      ((ChronoLocalDateTimeImpl)paramObject).writeExternal(paramObjectOutput);
      break;
    case 3: 
      ((ChronoZonedDateTimeImpl)paramObject).writeExternal(paramObjectOutput);
      break;
    case 4: 
      ((JapaneseDate)paramObject).writeExternal(paramObjectOutput);
      break;
    case 5: 
      ((JapaneseEra)paramObject).writeExternal(paramObjectOutput);
      break;
    case 6: 
      ((HijrahDate)paramObject).writeExternal(paramObjectOutput);
      break;
    case 7: 
      ((MinguoDate)paramObject).writeExternal(paramObjectOutput);
      break;
    case 8: 
      ((ThaiBuddhistDate)paramObject).writeExternal(paramObjectOutput);
      break;
    case 9: 
      ((ChronoPeriodImpl)paramObject).writeExternal(paramObjectOutput);
      break;
    default: 
      throw new InvalidClassException("Unknown serialized type");
    }
  }
  
  public void readExternal(ObjectInput paramObjectInput)
    throws IOException, ClassNotFoundException
  {
    type = paramObjectInput.readByte();
    object = readInternal(type, paramObjectInput);
  }
  
  static Object read(ObjectInput paramObjectInput)
    throws IOException, ClassNotFoundException
  {
    byte b = paramObjectInput.readByte();
    return readInternal(b, paramObjectInput);
  }
  
  private static Object readInternal(byte paramByte, ObjectInput paramObjectInput)
    throws IOException, ClassNotFoundException
  {
    switch (paramByte)
    {
    case 1: 
      return AbstractChronology.readExternal(paramObjectInput);
    case 2: 
      return ChronoLocalDateTimeImpl.readExternal(paramObjectInput);
    case 3: 
      return ChronoZonedDateTimeImpl.readExternal(paramObjectInput);
    case 4: 
      return JapaneseDate.readExternal(paramObjectInput);
    case 5: 
      return JapaneseEra.readExternal(paramObjectInput);
    case 6: 
      return HijrahDate.readExternal(paramObjectInput);
    case 7: 
      return MinguoDate.readExternal(paramObjectInput);
    case 8: 
      return ThaiBuddhistDate.readExternal(paramObjectInput);
    case 9: 
      return ChronoPeriodImpl.readExternal(paramObjectInput);
    }
    throw new StreamCorruptedException("Unknown serialized type");
  }
  
  private Object readResolve()
  {
    return object;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\time\chrono\Ser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */