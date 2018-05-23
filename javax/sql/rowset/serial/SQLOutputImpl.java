package javax.sql.rowset.serial;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
import java.sql.SQLOutput;
import java.sql.SQLXML;
import java.sql.Struct;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Map;
import java.util.Vector;

public class SQLOutputImpl
  implements SQLOutput
{
  private Vector attribs;
  private Map map;
  
  public SQLOutputImpl(Vector<?> paramVector, Map<String, ?> paramMap)
    throws SQLException
  {
    if ((paramVector == null) || (paramMap == null)) {
      throw new SQLException("Cannot instantiate a SQLOutputImpl instance with null parameters");
    }
    attribs = paramVector;
    map = paramMap;
  }
  
  public void writeString(String paramString)
    throws SQLException
  {
    attribs.add(paramString);
  }
  
  public void writeBoolean(boolean paramBoolean)
    throws SQLException
  {
    attribs.add(Boolean.valueOf(paramBoolean));
  }
  
  public void writeByte(byte paramByte)
    throws SQLException
  {
    attribs.add(Byte.valueOf(paramByte));
  }
  
  public void writeShort(short paramShort)
    throws SQLException
  {
    attribs.add(Short.valueOf(paramShort));
  }
  
  public void writeInt(int paramInt)
    throws SQLException
  {
    attribs.add(Integer.valueOf(paramInt));
  }
  
  public void writeLong(long paramLong)
    throws SQLException
  {
    attribs.add(Long.valueOf(paramLong));
  }
  
  public void writeFloat(float paramFloat)
    throws SQLException
  {
    attribs.add(Float.valueOf(paramFloat));
  }
  
  public void writeDouble(double paramDouble)
    throws SQLException
  {
    attribs.add(Double.valueOf(paramDouble));
  }
  
  public void writeBigDecimal(BigDecimal paramBigDecimal)
    throws SQLException
  {
    attribs.add(paramBigDecimal);
  }
  
  public void writeBytes(byte[] paramArrayOfByte)
    throws SQLException
  {
    attribs.add(paramArrayOfByte);
  }
  
  public void writeDate(Date paramDate)
    throws SQLException
  {
    attribs.add(paramDate);
  }
  
  public void writeTime(Time paramTime)
    throws SQLException
  {
    attribs.add(paramTime);
  }
  
  public void writeTimestamp(Timestamp paramTimestamp)
    throws SQLException
  {
    attribs.add(paramTimestamp);
  }
  
  public void writeCharacterStream(Reader paramReader)
    throws SQLException
  {
    BufferedReader localBufferedReader = new BufferedReader(paramReader);
    try
    {
      int i;
      while ((i = localBufferedReader.read()) != -1)
      {
        char c = (char)i;
        StringBuffer localStringBuffer = new StringBuffer();
        localStringBuffer.append(c);
        String str1 = new String(localStringBuffer);
        String str2 = localBufferedReader.readLine();
        writeString(str1.concat(str2));
      }
    }
    catch (IOException localIOException) {}
  }
  
  public void writeAsciiStream(InputStream paramInputStream)
    throws SQLException
  {
    BufferedReader localBufferedReader = new BufferedReader(new InputStreamReader(paramInputStream));
    try
    {
      int i;
      while ((i = localBufferedReader.read()) != -1)
      {
        char c = (char)i;
        StringBuffer localStringBuffer = new StringBuffer();
        localStringBuffer.append(c);
        String str1 = new String(localStringBuffer);
        String str2 = localBufferedReader.readLine();
        writeString(str1.concat(str2));
      }
    }
    catch (IOException localIOException)
    {
      throw new SQLException(localIOException.getMessage());
    }
  }
  
  public void writeBinaryStream(InputStream paramInputStream)
    throws SQLException
  {
    BufferedReader localBufferedReader = new BufferedReader(new InputStreamReader(paramInputStream));
    try
    {
      int i;
      while ((i = localBufferedReader.read()) != -1)
      {
        char c = (char)i;
        StringBuffer localStringBuffer = new StringBuffer();
        localStringBuffer.append(c);
        String str1 = new String(localStringBuffer);
        String str2 = localBufferedReader.readLine();
        writeString(str1.concat(str2));
      }
    }
    catch (IOException localIOException)
    {
      throw new SQLException(localIOException.getMessage());
    }
  }
  
  public void writeObject(SQLData paramSQLData)
    throws SQLException
  {
    if (paramSQLData == null) {
      attribs.add(null);
    } else {
      attribs.add(new SerialStruct(paramSQLData, map));
    }
  }
  
  public void writeRef(Ref paramRef)
    throws SQLException
  {
    if (paramRef == null) {
      attribs.add(null);
    } else {
      attribs.add(new SerialRef(paramRef));
    }
  }
  
  public void writeBlob(Blob paramBlob)
    throws SQLException
  {
    if (paramBlob == null) {
      attribs.add(null);
    } else {
      attribs.add(new SerialBlob(paramBlob));
    }
  }
  
  public void writeClob(Clob paramClob)
    throws SQLException
  {
    if (paramClob == null) {
      attribs.add(null);
    } else {
      attribs.add(new SerialClob(paramClob));
    }
  }
  
  public void writeStruct(Struct paramStruct)
    throws SQLException
  {
    SerialStruct localSerialStruct = new SerialStruct(paramStruct, map);
    attribs.add(localSerialStruct);
  }
  
  public void writeArray(Array paramArray)
    throws SQLException
  {
    if (paramArray == null) {
      attribs.add(null);
    } else {
      attribs.add(new SerialArray(paramArray, map));
    }
  }
  
  public void writeURL(URL paramURL)
    throws SQLException
  {
    if (paramURL == null) {
      attribs.add(null);
    } else {
      attribs.add(new SerialDatalink(paramURL));
    }
  }
  
  public void writeNString(String paramString)
    throws SQLException
  {
    attribs.add(paramString);
  }
  
  public void writeNClob(NClob paramNClob)
    throws SQLException
  {
    attribs.add(paramNClob);
  }
  
  public void writeRowId(RowId paramRowId)
    throws SQLException
  {
    attribs.add(paramRowId);
  }
  
  public void writeSQLXML(SQLXML paramSQLXML)
    throws SQLException
  {
    attribs.add(paramSQLXML);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\sql\rowset\serial\SQLOutputImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */