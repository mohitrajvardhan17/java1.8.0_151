package java.util.jar;

import java.io.DataOutputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class Manifest
  implements Cloneable
{
  private Attributes attr = new Attributes();
  private Map<String, Attributes> entries = new HashMap();
  
  public Manifest() {}
  
  public Manifest(InputStream paramInputStream)
    throws IOException
  {
    read(paramInputStream);
  }
  
  public Manifest(Manifest paramManifest)
  {
    attr.putAll(paramManifest.getMainAttributes());
    entries.putAll(paramManifest.getEntries());
  }
  
  public Attributes getMainAttributes()
  {
    return attr;
  }
  
  public Map<String, Attributes> getEntries()
  {
    return entries;
  }
  
  public Attributes getAttributes(String paramString)
  {
    return (Attributes)getEntries().get(paramString);
  }
  
  public void clear()
  {
    attr.clear();
    entries.clear();
  }
  
  public void write(OutputStream paramOutputStream)
    throws IOException
  {
    DataOutputStream localDataOutputStream = new DataOutputStream(paramOutputStream);
    attr.writeMain(localDataOutputStream);
    Iterator localIterator = entries.entrySet().iterator();
    while (localIterator.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      StringBuffer localStringBuffer = new StringBuffer("Name: ");
      String str = (String)localEntry.getKey();
      if (str != null)
      {
        byte[] arrayOfByte = str.getBytes("UTF8");
        str = new String(arrayOfByte, 0, 0, arrayOfByte.length);
      }
      localStringBuffer.append(str);
      localStringBuffer.append("\r\n");
      make72Safe(localStringBuffer);
      localDataOutputStream.writeBytes(localStringBuffer.toString());
      ((Attributes)localEntry.getValue()).write(localDataOutputStream);
    }
    localDataOutputStream.flush();
  }
  
  static void make72Safe(StringBuffer paramStringBuffer)
  {
    int i = paramStringBuffer.length();
    if (i > 72)
    {
      int j = 70;
      while (j < i - 2)
      {
        paramStringBuffer.insert(j, "\r\n ");
        j += 72;
        i += 3;
      }
    }
  }
  
  public void read(InputStream paramInputStream)
    throws IOException
  {
    FastInputStream localFastInputStream = new FastInputStream(paramInputStream);
    byte[] arrayOfByte = new byte['Ȁ'];
    attr.read(localFastInputStream, arrayOfByte);
    int i = 0;
    int j = 0;
    int k = 2;
    String str = null;
    int n = 1;
    Object localObject1 = null;
    int m;
    while ((m = localFastInputStream.readLine(arrayOfByte)) != -1)
    {
      if (arrayOfByte[(--m)] != 10) {
        throw new IOException("manifest line too long");
      }
      if ((m > 0) && (arrayOfByte[(m - 1)] == 13)) {
        m--;
      }
      if ((m != 0) || (n == 0))
      {
        n = 0;
        if (str == null)
        {
          str = parseName(arrayOfByte, m);
          if (str == null) {
            throw new IOException("invalid manifest format");
          }
          if (localFastInputStream.peek() == 32)
          {
            localObject1 = new byte[m - 6];
            System.arraycopy(arrayOfByte, 6, localObject1, 0, m - 6);
          }
        }
        else
        {
          localObject2 = new byte[localObject1.length + m - 1];
          System.arraycopy(localObject1, 0, localObject2, 0, localObject1.length);
          System.arraycopy(arrayOfByte, 1, localObject2, localObject1.length, m - 1);
          if (localFastInputStream.peek() == 32)
          {
            localObject1 = localObject2;
            continue;
          }
          str = new String((byte[])localObject2, 0, localObject2.length, "UTF8");
          localObject1 = null;
        }
        Object localObject2 = getAttributes(str);
        if (localObject2 == null)
        {
          localObject2 = new Attributes(k);
          entries.put(str, localObject2);
        }
        ((Attributes)localObject2).read(localFastInputStream, arrayOfByte);
        i++;
        j += ((Attributes)localObject2).size();
        k = Math.max(2, j / i);
        str = null;
        n = 1;
      }
    }
  }
  
  private String parseName(byte[] paramArrayOfByte, int paramInt)
  {
    if ((toLower(paramArrayOfByte[0]) == 110) && (toLower(paramArrayOfByte[1]) == 97) && (toLower(paramArrayOfByte[2]) == 109) && (toLower(paramArrayOfByte[3]) == 101) && (paramArrayOfByte[4] == 58) && (paramArrayOfByte[5] == 32)) {
      try
      {
        return new String(paramArrayOfByte, 6, paramInt - 6, "UTF8");
      }
      catch (Exception localException) {}
    }
    return null;
  }
  
  private int toLower(int paramInt)
  {
    return (paramInt >= 65) && (paramInt <= 90) ? 97 + (paramInt - 65) : paramInt;
  }
  
  public boolean equals(Object paramObject)
  {
    if ((paramObject instanceof Manifest))
    {
      Manifest localManifest = (Manifest)paramObject;
      return (attr.equals(localManifest.getMainAttributes())) && (entries.equals(localManifest.getEntries()));
    }
    return false;
  }
  
  public int hashCode()
  {
    return attr.hashCode() + entries.hashCode();
  }
  
  public Object clone()
  {
    return new Manifest(this);
  }
  
  static class FastInputStream
    extends FilterInputStream
  {
    private byte[] buf;
    private int count = 0;
    private int pos = 0;
    
    FastInputStream(InputStream paramInputStream)
    {
      this(paramInputStream, 8192);
    }
    
    FastInputStream(InputStream paramInputStream, int paramInt)
    {
      super();
      buf = new byte[paramInt];
    }
    
    public int read()
      throws IOException
    {
      if (pos >= count)
      {
        fill();
        if (pos >= count) {
          return -1;
        }
      }
      return Byte.toUnsignedInt(buf[(pos++)]);
    }
    
    public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
      throws IOException
    {
      int i = count - pos;
      if (i <= 0)
      {
        if (paramInt2 >= buf.length) {
          return in.read(paramArrayOfByte, paramInt1, paramInt2);
        }
        fill();
        i = count - pos;
        if (i <= 0) {
          return -1;
        }
      }
      if (paramInt2 > i) {
        paramInt2 = i;
      }
      System.arraycopy(buf, pos, paramArrayOfByte, paramInt1, paramInt2);
      pos += paramInt2;
      return paramInt2;
    }
    
    public int readLine(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
      throws IOException
    {
      byte[] arrayOfByte = buf;
      int i = 0;
      while (i < paramInt2)
      {
        int j = count - pos;
        if (j <= 0)
        {
          fill();
          j = count - pos;
          if (j <= 0) {
            return -1;
          }
        }
        int k = paramInt2 - i;
        if (k > j) {
          k = j;
        }
        int m = pos;
        int n = m + k;
        while ((m < n) && (arrayOfByte[(m++)] != 10)) {}
        k = m - pos;
        System.arraycopy(arrayOfByte, pos, paramArrayOfByte, paramInt1, k);
        paramInt1 += k;
        i += k;
        pos = m;
        if (arrayOfByte[(m - 1)] == 10) {
          break;
        }
      }
      return i;
    }
    
    public byte peek()
      throws IOException
    {
      if (pos == count) {
        fill();
      }
      if (pos == count) {
        return -1;
      }
      return buf[pos];
    }
    
    public int readLine(byte[] paramArrayOfByte)
      throws IOException
    {
      return readLine(paramArrayOfByte, 0, paramArrayOfByte.length);
    }
    
    public long skip(long paramLong)
      throws IOException
    {
      if (paramLong <= 0L) {
        return 0L;
      }
      long l = count - pos;
      if (l <= 0L) {
        return in.skip(paramLong);
      }
      if (paramLong > l) {
        paramLong = l;
      }
      pos = ((int)(pos + paramLong));
      return paramLong;
    }
    
    public int available()
      throws IOException
    {
      return count - pos + in.available();
    }
    
    public void close()
      throws IOException
    {
      if (in != null)
      {
        in.close();
        in = null;
        buf = null;
      }
    }
    
    private void fill()
      throws IOException
    {
      count = (pos = 0);
      int i = in.read(buf, 0, buf.length);
      if (i > 0) {
        count = i;
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\jar\Manifest.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */