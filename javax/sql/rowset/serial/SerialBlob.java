package javax.sql.rowset.serial;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectInputStream.GetField;
import java.io.ObjectOutputStream;
import java.io.ObjectOutputStream.PutField;
import java.io.OutputStream;
import java.io.Serializable;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.Arrays;

public class SerialBlob
  implements Blob, Serializable, Cloneable
{
  private byte[] buf;
  private Blob blob;
  private long len;
  private long origLen;
  static final long serialVersionUID = -8144641928112860441L;
  
  public SerialBlob(byte[] paramArrayOfByte)
    throws SerialException, SQLException
  {
    len = paramArrayOfByte.length;
    buf = new byte[(int)len];
    for (int i = 0; i < len; i++) {
      buf[i] = paramArrayOfByte[i];
    }
    origLen = len;
  }
  
  public SerialBlob(Blob paramBlob)
    throws SerialException, SQLException
  {
    if (paramBlob == null) {
      throw new SQLException("Cannot instantiate a SerialBlob object with a null Blob object");
    }
    len = paramBlob.length();
    buf = paramBlob.getBytes(1L, (int)len);
    blob = paramBlob;
    origLen = len;
  }
  
  public byte[] getBytes(long paramLong, int paramInt)
    throws SerialException
  {
    isValid();
    if (paramInt > len) {
      paramInt = (int)len;
    }
    if ((paramLong < 1L) || (len - paramLong < 0L)) {
      throw new SerialException("Invalid arguments: position cannot be less than 1 or greater than the length of the SerialBlob");
    }
    paramLong -= 1L;
    byte[] arrayOfByte = new byte[paramInt];
    for (int i = 0; i < paramInt; i++)
    {
      arrayOfByte[i] = buf[((int)paramLong)];
      paramLong += 1L;
    }
    return arrayOfByte;
  }
  
  public long length()
    throws SerialException
  {
    isValid();
    return len;
  }
  
  public InputStream getBinaryStream()
    throws SerialException
  {
    isValid();
    ByteArrayInputStream localByteArrayInputStream = new ByteArrayInputStream(buf);
    return localByteArrayInputStream;
  }
  
  public long position(byte[] paramArrayOfByte, long paramLong)
    throws SerialException, SQLException
  {
    isValid();
    if ((paramLong < 1L) || (paramLong > len)) {
      return -1L;
    }
    int i = (int)paramLong - 1;
    int j = 0;
    long l = paramArrayOfByte.length;
    while (i < len) {
      if (paramArrayOfByte[j] == buf[i])
      {
        if (j + 1 == l) {
          return i + 1 - (l - 1L);
        }
        j++;
        i++;
      }
      else if (paramArrayOfByte[j] != buf[i])
      {
        i++;
      }
    }
    return -1L;
  }
  
  public long position(Blob paramBlob, long paramLong)
    throws SerialException, SQLException
  {
    isValid();
    return position(paramBlob.getBytes(1L, (int)paramBlob.length()), paramLong);
  }
  
  public int setBytes(long paramLong, byte[] paramArrayOfByte)
    throws SerialException, SQLException
  {
    return setBytes(paramLong, paramArrayOfByte, 0, paramArrayOfByte.length);
  }
  
  public int setBytes(long paramLong, byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws SerialException, SQLException
  {
    isValid();
    if ((paramInt1 < 0) || (paramInt1 > paramArrayOfByte.length)) {
      throw new SerialException("Invalid offset in byte array set");
    }
    if ((paramLong < 1L) || (paramLong > length())) {
      throw new SerialException("Invalid position in BLOB object set");
    }
    if (paramInt2 > origLen) {
      throw new SerialException("Buffer is not sufficient to hold the value");
    }
    if (paramInt2 + paramInt1 > paramArrayOfByte.length) {
      throw new SerialException("Invalid OffSet. Cannot have combined offset and length that is greater that the Blob buffer");
    }
    int i = 0;
    paramLong -= 1L;
    while ((i < paramInt2) || (paramInt1 + i + 1 < paramArrayOfByte.length - paramInt1))
    {
      buf[((int)paramLong + i)] = paramArrayOfByte[(paramInt1 + i)];
      i++;
    }
    return i;
  }
  
  public OutputStream setBinaryStream(long paramLong)
    throws SerialException, SQLException
  {
    isValid();
    if (blob != null) {
      return blob.setBinaryStream(paramLong);
    }
    throw new SerialException("Unsupported operation. SerialBlob cannot return a writable binary stream, unless instantiated with a Blob object that provides a setBinaryStream() implementation");
  }
  
  public void truncate(long paramLong)
    throws SerialException
  {
    isValid();
    if (paramLong > len) {
      throw new SerialException("Length more than what can be truncated");
    }
    if ((int)paramLong == 0)
    {
      buf = new byte[0];
      len = paramLong;
    }
    else
    {
      len = paramLong;
      buf = getBytes(1L, (int)len);
    }
  }
  
  public InputStream getBinaryStream(long paramLong1, long paramLong2)
    throws SQLException
  {
    isValid();
    if ((paramLong1 < 1L) || (paramLong1 > length())) {
      throw new SerialException("Invalid position in BLOB object set");
    }
    if ((paramLong2 < 1L) || (paramLong2 > len - paramLong1 + 1L)) {
      throw new SerialException("length is < 1 or pos + length > total number of bytes");
    }
    return new ByteArrayInputStream(buf, (int)paramLong1 - 1, (int)paramLong2);
  }
  
  public void free()
    throws SQLException
  {
    if (buf != null)
    {
      buf = null;
      if (blob != null) {
        blob.free();
      }
      blob = null;
    }
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if ((paramObject instanceof SerialBlob))
    {
      SerialBlob localSerialBlob = (SerialBlob)paramObject;
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
      SerialBlob localSerialBlob = (SerialBlob)super.clone();
      buf = (buf != null ? Arrays.copyOf(buf, (int)len) : null);
      blob = null;
      return localSerialBlob;
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
    byte[] arrayOfByte = (byte[])localGetField.get("buf", null);
    if (arrayOfByte == null) {
      throw new InvalidObjectException("buf is null and should not be!");
    }
    buf = ((byte[])arrayOfByte.clone());
    len = localGetField.get("len", 0L);
    if (buf.length != len) {
      throw new InvalidObjectException("buf is not the expected size");
    }
    origLen = localGetField.get("origLen", 0L);
    blob = ((Blob)localGetField.get("blob", null));
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException, ClassNotFoundException
  {
    ObjectOutputStream.PutField localPutField = paramObjectOutputStream.putFields();
    localPutField.put("buf", buf);
    localPutField.put("len", len);
    localPutField.put("origLen", origLen);
    localPutField.put("blob", (blob instanceof Serializable) ? blob : null);
    paramObjectOutputStream.writeFields();
  }
  
  private void isValid()
    throws SerialException
  {
    if (buf == null) {
      throw new SerialException("Error: You cannot call a method on a SerialBlob instance once free() has been called.");
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\sql\rowset\serial\SerialBlob.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */