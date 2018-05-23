package sun.security.util;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.util.HashMap;

public class ManifestDigester
{
  public static final String MF_MAIN_ATTRS = "Manifest-Main-Attributes";
  private byte[] rawBytes;
  private HashMap<String, Entry> entries;
  
  private boolean findSection(int paramInt, Position paramPosition)
  {
    int i = paramInt;
    int j = rawBytes.length;
    int k = paramInt;
    int m = 1;
    endOfFirstLine = -1;
    while (i < j)
    {
      int n = rawBytes[i];
      switch (n)
      {
      case 13: 
        if (endOfFirstLine == -1) {
          endOfFirstLine = (i - 1);
        }
        if ((i < j) && (rawBytes[(i + 1)] == 10)) {
          i++;
        }
      case 10: 
        if (endOfFirstLine == -1) {
          endOfFirstLine = (i - 1);
        }
        if ((m != 0) || (i == j - 1))
        {
          if (i == j - 1) {
            endOfSection = i;
          } else {
            endOfSection = k;
          }
          startOfNext = (i + 1);
          return true;
        }
        k = i;
        m = 1;
        break;
      }
      m = 0;
      i++;
    }
    return false;
  }
  
  public ManifestDigester(byte[] paramArrayOfByte)
  {
    rawBytes = paramArrayOfByte;
    entries = new HashMap();
    ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
    Position localPosition = new Position();
    if (!findSection(0, localPosition)) {
      return;
    }
    entries.put("Manifest-Main-Attributes", new Entry(0, endOfSection + 1, startOfNext, rawBytes));
    for (int i = startOfNext; findSection(i, localPosition); i = startOfNext)
    {
      int j = endOfFirstLine - i + 1;
      int k = endOfSection - i + 1;
      int m = startOfNext - i;
      if ((j > 6) && (isNameAttr(paramArrayOfByte, i)))
      {
        StringBuilder localStringBuilder = new StringBuilder(k);
        try
        {
          localStringBuilder.append(new String(paramArrayOfByte, i + 6, j - 6, "UTF8"));
          int n = i + j;
          if (n - i < k) {
            if (paramArrayOfByte[n] == 13) {
              n += 2;
            } else {
              n++;
            }
          }
          while ((n - i < k) && (paramArrayOfByte[(n++)] == 32))
          {
            int i1 = n;
            while ((n - i < k) && (paramArrayOfByte[(n++)] != 10)) {}
            if (paramArrayOfByte[(n - 1)] != 10) {
              return;
            }
            int i2;
            if (paramArrayOfByte[(n - 2)] == 13) {
              i2 = n - i1 - 2;
            } else {
              i2 = n - i1 - 1;
            }
            localStringBuilder.append(new String(paramArrayOfByte, i1, i2, "UTF8"));
          }
          entries.put(localStringBuilder.toString(), new Entry(i, k, m, rawBytes));
        }
        catch (UnsupportedEncodingException localUnsupportedEncodingException)
        {
          throw new IllegalStateException("UTF8 not available on platform");
        }
      }
    }
  }
  
  private boolean isNameAttr(byte[] paramArrayOfByte, int paramInt)
  {
    return ((paramArrayOfByte[paramInt] == 78) || (paramArrayOfByte[paramInt] == 110)) && ((paramArrayOfByte[(paramInt + 1)] == 97) || (paramArrayOfByte[(paramInt + 1)] == 65)) && ((paramArrayOfByte[(paramInt + 2)] == 109) || (paramArrayOfByte[(paramInt + 2)] == 77)) && ((paramArrayOfByte[(paramInt + 3)] == 101) || (paramArrayOfByte[(paramInt + 3)] == 69)) && (paramArrayOfByte[(paramInt + 4)] == 58) && (paramArrayOfByte[(paramInt + 5)] == 32);
  }
  
  public Entry get(String paramString, boolean paramBoolean)
  {
    Entry localEntry = (Entry)entries.get(paramString);
    if (localEntry != null) {
      oldStyle = paramBoolean;
    }
    return localEntry;
  }
  
  public byte[] manifestDigest(MessageDigest paramMessageDigest)
  {
    paramMessageDigest.reset();
    paramMessageDigest.update(rawBytes, 0, rawBytes.length);
    return paramMessageDigest.digest();
  }
  
  public static class Entry
  {
    int offset;
    int length;
    int lengthWithBlankLine;
    byte[] rawBytes;
    boolean oldStyle;
    
    public Entry(int paramInt1, int paramInt2, int paramInt3, byte[] paramArrayOfByte)
    {
      offset = paramInt1;
      length = paramInt2;
      lengthWithBlankLine = paramInt3;
      rawBytes = paramArrayOfByte;
    }
    
    public byte[] digest(MessageDigest paramMessageDigest)
    {
      paramMessageDigest.reset();
      if (oldStyle) {
        doOldStyle(paramMessageDigest, rawBytes, offset, lengthWithBlankLine);
      } else {
        paramMessageDigest.update(rawBytes, offset, lengthWithBlankLine);
      }
      return paramMessageDigest.digest();
    }
    
    private void doOldStyle(MessageDigest paramMessageDigest, byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    {
      int i = paramInt1;
      int j = paramInt1;
      int k = paramInt1 + paramInt2;
      int m = -1;
      while (i < k)
      {
        if ((paramArrayOfByte[i] == 13) && (m == 32))
        {
          paramMessageDigest.update(paramArrayOfByte, j, i - j - 1);
          j = i;
        }
        m = paramArrayOfByte[i];
        i++;
      }
      paramMessageDigest.update(paramArrayOfByte, j, i - j);
    }
    
    public byte[] digestWorkaround(MessageDigest paramMessageDigest)
    {
      paramMessageDigest.reset();
      paramMessageDigest.update(rawBytes, offset, length);
      return paramMessageDigest.digest();
    }
  }
  
  static class Position
  {
    int endOfFirstLine;
    int endOfSection;
    int startOfNext;
    
    Position() {}
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\util\ManifestDigester.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */