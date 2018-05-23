package javax.sql.rowset.serial;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.Ref;
import java.sql.RowId;
import java.sql.SQLData;
import java.sql.SQLException;
import java.sql.SQLInput;
import java.sql.SQLXML;
import java.sql.Struct;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Map;
import sun.reflect.misc.ReflectUtil;

public class SQLInputImpl
  implements SQLInput
{
  private boolean lastValueWasNull;
  private int idx;
  private Object[] attrib;
  private Map<String, Class<?>> map;
  
  public SQLInputImpl(Object[] paramArrayOfObject, Map<String, Class<?>> paramMap)
    throws SQLException
  {
    if ((paramArrayOfObject == null) || (paramMap == null)) {
      throw new SQLException("Cannot instantiate a SQLInputImpl object with null parameters");
    }
    attrib = Arrays.copyOf(paramArrayOfObject, paramArrayOfObject.length);
    idx = -1;
    map = paramMap;
  }
  
  private Object getNextAttribute()
    throws SQLException
  {
    if (++idx >= attrib.length) {
      throw new SQLException("SQLInputImpl exception: Invalid read position");
    }
    lastValueWasNull = (attrib[idx] == null);
    return attrib[idx];
  }
  
  public String readString()
    throws SQLException
  {
    return (String)getNextAttribute();
  }
  
  public boolean readBoolean()
    throws SQLException
  {
    Boolean localBoolean = (Boolean)getNextAttribute();
    return localBoolean == null ? false : localBoolean.booleanValue();
  }
  
  public byte readByte()
    throws SQLException
  {
    Byte localByte = (Byte)getNextAttribute();
    return localByte == null ? 0 : localByte.byteValue();
  }
  
  public short readShort()
    throws SQLException
  {
    Short localShort = (Short)getNextAttribute();
    return localShort == null ? 0 : localShort.shortValue();
  }
  
  public int readInt()
    throws SQLException
  {
    Integer localInteger = (Integer)getNextAttribute();
    return localInteger == null ? 0 : localInteger.intValue();
  }
  
  public long readLong()
    throws SQLException
  {
    Long localLong = (Long)getNextAttribute();
    return localLong == null ? 0L : localLong.longValue();
  }
  
  public float readFloat()
    throws SQLException
  {
    Float localFloat = (Float)getNextAttribute();
    return localFloat == null ? 0.0F : localFloat.floatValue();
  }
  
  public double readDouble()
    throws SQLException
  {
    Double localDouble = (Double)getNextAttribute();
    return localDouble == null ? 0.0D : localDouble.doubleValue();
  }
  
  public BigDecimal readBigDecimal()
    throws SQLException
  {
    return (BigDecimal)getNextAttribute();
  }
  
  public byte[] readBytes()
    throws SQLException
  {
    return (byte[])getNextAttribute();
  }
  
  public Date readDate()
    throws SQLException
  {
    return (Date)getNextAttribute();
  }
  
  public Time readTime()
    throws SQLException
  {
    return (Time)getNextAttribute();
  }
  
  public Timestamp readTimestamp()
    throws SQLException
  {
    return (Timestamp)getNextAttribute();
  }
  
  public Reader readCharacterStream()
    throws SQLException
  {
    return (Reader)getNextAttribute();
  }
  
  public InputStream readAsciiStream()
    throws SQLException
  {
    return (InputStream)getNextAttribute();
  }
  
  public InputStream readBinaryStream()
    throws SQLException
  {
    return (InputStream)getNextAttribute();
  }
  
  public Object readObject()
    throws SQLException
  {
    Object localObject = getNextAttribute();
    if ((localObject instanceof Struct))
    {
      Struct localStruct = (Struct)localObject;
      Class localClass = (Class)map.get(localStruct.getSQLTypeName());
      if (localClass != null)
      {
        SQLData localSQLData = null;
        try
        {
          localSQLData = (SQLData)ReflectUtil.newInstance(localClass);
        }
        catch (Exception localException)
        {
          throw new SQLException("Unable to Instantiate: ", localException);
        }
        Object[] arrayOfObject = localStruct.getAttributes(map);
        SQLInputImpl localSQLInputImpl = new SQLInputImpl(arrayOfObject, map);
        localSQLData.readSQL(localSQLInputImpl, localStruct.getSQLTypeName());
        return localSQLData;
      }
    }
    return localObject;
  }
  
  public Ref readRef()
    throws SQLException
  {
    return (Ref)getNextAttribute();
  }
  
  public Blob readBlob()
    throws SQLException
  {
    return (Blob)getNextAttribute();
  }
  
  public Clob readClob()
    throws SQLException
  {
    return (Clob)getNextAttribute();
  }
  
  public Array readArray()
    throws SQLException
  {
    return (Array)getNextAttribute();
  }
  
  public boolean wasNull()
    throws SQLException
  {
    return lastValueWasNull;
  }
  
  public URL readURL()
    throws SQLException
  {
    return (URL)getNextAttribute();
  }
  
  public NClob readNClob()
    throws SQLException
  {
    return (NClob)getNextAttribute();
  }
  
  public String readNString()
    throws SQLException
  {
    return (String)getNextAttribute();
  }
  
  public SQLXML readSQLXML()
    throws SQLException
  {
    return (SQLXML)getNextAttribute();
  }
  
  public RowId readRowId()
    throws SQLException
  {
    return (RowId)getNextAttribute();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\sql\rowset\serial\SQLInputImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */