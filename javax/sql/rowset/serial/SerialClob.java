package javax.sql.rowset.serial;

import java.io.BufferedReader;
import java.io.CharArrayReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectInputStream.GetField;
import java.io.ObjectOutputStream;
import java.io.ObjectOutputStream.PutField;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Serializable;
import java.io.Writer;
import java.sql.Clob;
import java.sql.SQLException;
import java.util.Arrays;

public class SerialClob
  implements Clob, Serializable, Cloneable
{
  private char[] buf;
  private Clob clob;
  private long len;
  private long origLen;
  static final long serialVersionUID = -1662519690087375313L;
  
  public SerialClob(char[] paramArrayOfChar)
    throws SerialException, SQLException
  {
    len = paramArrayOfChar.length;
    buf = new char[(int)len];
    for (int i = 0; i < len; i++) {
      buf[i] = paramArrayOfChar[i];
    }
    origLen = len;
    clob = null;
  }
  
  public SerialClob(Clob paramClob)
    throws SerialException, SQLException
  {
    if (paramClob == null) {
      throw new SQLException("Cannot instantiate a SerialClob object with a null Clob object");
    }
    len = paramClob.length();
    clob = paramClob;
    buf = new char[(int)len];
    int i = 0;
    int j = 0;
    try
    {
      Reader localReader = paramClob.getCharacterStream();
      Object localObject1 = null;
      try
      {
        if (localReader == null) {
          throw new SQLException("Invalid Clob object. The call to getCharacterStream returned null which cannot be serialized.");
        }
        Object localObject2 = paramClob.getAsciiStream();
        Object localObject3 = null;
        try
        {
          if (localObject2 == null) {
            throw new SQLException("Invalid Clob object. The call to getAsciiStream returned null which cannot be serialized.");
          }
        }
        catch (Throwable localThrowable4)
        {
          localObject3 = localThrowable4;
          throw localThrowable4;
        }
        finally
        {
          if (localObject2 != null) {
            if (localObject3 != null) {
              try
              {
                ((InputStream)localObject2).close();
              }
              catch (Throwable localThrowable7)
              {
                ((Throwable)localObject3).addSuppressed(localThrowable7);
              }
            } else {
              ((InputStream)localObject2).close();
            }
          }
        }
        localObject2 = new BufferedReader(localReader);
        localObject3 = null;
        try
        {
          do
          {
            i = ((Reader)localObject2).read(buf, j, (int)(len - j));
            j += i;
          } while (i > 0);
        }
        catch (Throwable localThrowable6)
        {
          localObject3 = localThrowable6;
          throw localThrowable6;
        }
        finally
        {
          if (localObject2 != null) {
            if (localObject3 != null) {
              try
              {
                ((Reader)localObject2).close();
              }
              catch (Throwable localThrowable8)
              {
                ((Throwable)localObject3).addSuppressed(localThrowable8);
              }
            } else {
              ((Reader)localObject2).close();
            }
          }
        }
      }
      catch (Throwable localThrowable2)
      {
        localObject1 = localThrowable2;
        throw localThrowable2;
      }
      finally
      {
        if (localReader != null) {
          if (localObject1 != null) {
            try
            {
              localReader.close();
            }
            catch (Throwable localThrowable9)
            {
              ((Throwable)localObject1).addSuppressed(localThrowable9);
            }
          } else {
            localReader.close();
          }
        }
      }
    }
    catch (IOException localIOException)
    {
      throw new SerialException("SerialClob: " + localIOException.getMessage());
    }
    origLen = len;
  }
  
  public long length()
    throws SerialException
  {
    isValid();
    return len;
  }
  
  public Reader getCharacterStream()
    throws SerialException
  {
    isValid();
    return new CharArrayReader(buf);
  }
  
  public InputStream getAsciiStream()
    throws SerialException, SQLException
  {
    isValid();
    if (clob != null) {
      return clob.getAsciiStream();
    }
    throw new SerialException("Unsupported operation. SerialClob cannot return a the CLOB value as an ascii stream, unless instantiated with a fully implemented Clob object.");
  }
  
  public String getSubString(long paramLong, int paramInt)
    throws SerialException
  {
    isValid();
    if ((paramLong < 1L) || (paramLong > length())) {
      throw new SerialException("Invalid position in SerialClob object set");
    }
    if (paramLong - 1L + paramInt > length()) {
      throw new SerialException("Invalid position and substring length");
    }
    try
    {
      return new String(buf, (int)paramLong - 1, paramInt);
    }
    catch (StringIndexOutOfBoundsException localStringIndexOutOfBoundsException)
    {
      throw new SerialException("StringIndexOutOfBoundsException: " + localStringIndexOutOfBoundsException.getMessage());
    }
  }
  
  public long position(String paramString, long paramLong)
    throws SerialException, SQLException
  {
    isValid();
    if ((paramLong < 1L) || (paramLong > len)) {
      return -1L;
    }
    char[] arrayOfChar = paramString.toCharArray();
    int i = (int)paramLong - 1;
    int j = 0;
    long l = arrayOfChar.length;
    while (i < len) {
      if (arrayOfChar[j] == buf[i])
      {
        if (j + 1 == l) {
          return i + 1 - (l - 1L);
        }
        j++;
        i++;
      }
      else if (arrayOfChar[j] != buf[i])
      {
        i++;
      }
    }
    return -1L;
  }
  
  public long position(Clob paramClob, long paramLong)
    throws SerialException, SQLException
  {
    isValid();
    return position(paramClob.getSubString(1L, (int)paramClob.length()), paramLong);
  }
  
  public int setString(long paramLong, String paramString)
    throws SerialException
  {
    return setString(paramLong, paramString, 0, paramString.length());
  }
  
  public int setString(long paramLong, String paramString, int paramInt1, int paramInt2)
    throws SerialException
  {
    isValid();
    String str = paramString.substring(paramInt1);
    char[] arrayOfChar = str.toCharArray();
    if ((paramInt1 < 0) || (paramInt1 > paramString.length())) {
      throw new SerialException("Invalid offset in byte array set");
    }
    if ((paramLong < 1L) || (paramLong > length())) {
      throw new SerialException("Invalid position in Clob object set");
    }
    if (paramInt2 > origLen) {
      throw new SerialException("Buffer is not sufficient to hold the value");
    }
    if (paramInt2 + paramInt1 > paramString.length()) {
      throw new SerialException("Invalid OffSet. Cannot have combined offset  and length that is greater that the Blob buffer");
    }
    int i = 0;
    paramLong -= 1L;
    while ((i < paramInt2) || (paramInt1 + i + 1 < paramString.length() - paramInt1))
    {
      buf[((int)paramLong + i)] = arrayOfChar[(paramInt1 + i)];
      i++;
    }
    return i;
  }
  
  public OutputStream setAsciiStream(long paramLong)
    throws SerialException, SQLException
  {
    isValid();
    if (clob != null) {
      return clob.setAsciiStream(paramLong);
    }
    throw new SerialException("Unsupported operation. SerialClob cannot return a writable ascii stream\n unless instantiated with a Clob object that has a setAsciiStream() implementation");
  }
  
  public Writer setCharacterStream(long paramLong)
    throws SerialException, SQLException
  {
    isValid();
    if (clob != null) {
      return clob.setCharacterStream(paramLong);
    }
    throw new SerialException("Unsupported operation. SerialClob cannot return a writable character stream\n unless instantiated with a Clob object that has a setCharacterStream implementation");
  }
  
  public void truncate(long paramLong)
    throws SerialException
  {
    isValid();
    if (paramLong > len) {
      throw new SerialException("Length more than what can be truncated");
    }
    len = paramLong;
    if (len == 0L) {
      buf = new char[0];
    } else {
      buf = getSubString(1L, (int)len).toCharArray();
    }
  }
  
  public Reader getCharacterStream(long paramLong1, long paramLong2)
    throws SQLException
  {
    isValid();
    if ((paramLong1 < 1L) || (paramLong1 > len)) {
      throw new SerialException("Invalid position in Clob object set");
    }
    if (paramLong1 - 1L + paramLong2 > len) {
      throw new SerialException("Invalid position and substring length");
    }
    if (paramLong2 <= 0L) {
      throw new SerialException("Invalid length specified");
    }
    return new CharArrayReader(buf, (int)paramLong1, (int)paramLong2);
  }
  
  public void free()
    throws SQLException
  {
    if (buf != null)
    {
      buf = null;
      if (clob != null) {
        clob.free();
      }
      clob = null;
    }
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if ((paramObject instanceof SerialClob))
    {
      SerialClob localSerialClob = (SerialClob)paramObject;
      if (len == len) {
        return Arrays.equals(buf, buf);
      }
    }
    return false;
  }
  
  public int hashCode()
  {
    return ((31 + Arrays.hashCode(buf)) * 31 + (int)len) * 31 + (int)origLen;
  }
  
  public Object clone()
  {
    try
    {
      SerialClob localSerialClob = (SerialClob)super.clone();
      buf = (buf != null ? Arrays.copyOf(buf, (int)len) : null);
      clob = null;
      return localSerialClob;
    }
    catch (CloneNotSupportedException localCloneNotSupportedException)
    {
      throw new InternalError();
    }
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    ObjectInputStream.GetField localGetField = paramObjectInputStream.readFields();
    char[] arrayOfChar = (char[])localGetField.get("buf", null);
    if (arrayOfChar == null) {
      throw new InvalidObjectException("buf is null and should not be!");
    }
    buf = ((char[])arrayOfChar.clone());
    len = localGetField.get("len", 0L);
    if (buf.length != len) {
      throw new InvalidObjectException("buf is not the expected size");
    }
    origLen = localGetField.get("origLen", 0L);
    clob = ((Clob)localGetField.get("clob", null));
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException, ClassNotFoundException
  {
    ObjectOutputStream.PutField localPutField = paramObjectOutputStream.putFields();
    localPutField.put("buf", buf);
    localPutField.put("len", len);
    localPutField.put("origLen", origLen);
    localPutField.put("clob", (clob instanceof Serializable) ? clob : null);
    paramObjectOutputStream.writeFields();
  }
  
  private void isValid()
    throws SerialException
  {
    if (buf == null) {
      throw new SerialException("Error: You cannot call a method on a SerialClob instance once free() has been called.");
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\sql\rowset\serial\SerialClob.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */