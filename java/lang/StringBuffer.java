package java.lang;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectInputStream.GetField;
import java.io.ObjectOutputStream;
import java.io.ObjectOutputStream.PutField;
import java.io.ObjectStreamField;
import java.io.Serializable;
import java.util.Arrays;

public final class StringBuffer
  extends AbstractStringBuilder
  implements Serializable, CharSequence
{
  private transient char[] toStringCache;
  static final long serialVersionUID = 3388685877147921107L;
  private static final ObjectStreamField[] serialPersistentFields = { new ObjectStreamField("value", char[].class), new ObjectStreamField("count", Integer.TYPE), new ObjectStreamField("shared", Boolean.TYPE) };
  
  public StringBuffer()
  {
    super(16);
  }
  
  public StringBuffer(int paramInt)
  {
    super(paramInt);
  }
  
  public StringBuffer(String paramString)
  {
    super(paramString.length() + 16);
    append(paramString);
  }
  
  public StringBuffer(CharSequence paramCharSequence)
  {
    this(paramCharSequence.length() + 16);
    append(paramCharSequence);
  }
  
  public synchronized int length()
  {
    return count;
  }
  
  public synchronized int capacity()
  {
    return value.length;
  }
  
  public synchronized void ensureCapacity(int paramInt)
  {
    super.ensureCapacity(paramInt);
  }
  
  public synchronized void trimToSize()
  {
    super.trimToSize();
  }
  
  public synchronized void setLength(int paramInt)
  {
    toStringCache = null;
    super.setLength(paramInt);
  }
  
  public synchronized char charAt(int paramInt)
  {
    if ((paramInt < 0) || (paramInt >= count)) {
      throw new StringIndexOutOfBoundsException(paramInt);
    }
    return value[paramInt];
  }
  
  public synchronized int codePointAt(int paramInt)
  {
    return super.codePointAt(paramInt);
  }
  
  public synchronized int codePointBefore(int paramInt)
  {
    return super.codePointBefore(paramInt);
  }
  
  public synchronized int codePointCount(int paramInt1, int paramInt2)
  {
    return super.codePointCount(paramInt1, paramInt2);
  }
  
  public synchronized int offsetByCodePoints(int paramInt1, int paramInt2)
  {
    return super.offsetByCodePoints(paramInt1, paramInt2);
  }
  
  public synchronized void getChars(int paramInt1, int paramInt2, char[] paramArrayOfChar, int paramInt3)
  {
    super.getChars(paramInt1, paramInt2, paramArrayOfChar, paramInt3);
  }
  
  public synchronized void setCharAt(int paramInt, char paramChar)
  {
    if ((paramInt < 0) || (paramInt >= count)) {
      throw new StringIndexOutOfBoundsException(paramInt);
    }
    toStringCache = null;
    value[paramInt] = paramChar;
  }
  
  public synchronized StringBuffer append(Object paramObject)
  {
    toStringCache = null;
    super.append(String.valueOf(paramObject));
    return this;
  }
  
  public synchronized StringBuffer append(String paramString)
  {
    toStringCache = null;
    super.append(paramString);
    return this;
  }
  
  public synchronized StringBuffer append(StringBuffer paramStringBuffer)
  {
    toStringCache = null;
    super.append(paramStringBuffer);
    return this;
  }
  
  synchronized StringBuffer append(AbstractStringBuilder paramAbstractStringBuilder)
  {
    toStringCache = null;
    super.append(paramAbstractStringBuilder);
    return this;
  }
  
  public synchronized StringBuffer append(CharSequence paramCharSequence)
  {
    toStringCache = null;
    super.append(paramCharSequence);
    return this;
  }
  
  public synchronized StringBuffer append(CharSequence paramCharSequence, int paramInt1, int paramInt2)
  {
    toStringCache = null;
    super.append(paramCharSequence, paramInt1, paramInt2);
    return this;
  }
  
  public synchronized StringBuffer append(char[] paramArrayOfChar)
  {
    toStringCache = null;
    super.append(paramArrayOfChar);
    return this;
  }
  
  public synchronized StringBuffer append(char[] paramArrayOfChar, int paramInt1, int paramInt2)
  {
    toStringCache = null;
    super.append(paramArrayOfChar, paramInt1, paramInt2);
    return this;
  }
  
  public synchronized StringBuffer append(boolean paramBoolean)
  {
    toStringCache = null;
    super.append(paramBoolean);
    return this;
  }
  
  public synchronized StringBuffer append(char paramChar)
  {
    toStringCache = null;
    super.append(paramChar);
    return this;
  }
  
  public synchronized StringBuffer append(int paramInt)
  {
    toStringCache = null;
    super.append(paramInt);
    return this;
  }
  
  public synchronized StringBuffer appendCodePoint(int paramInt)
  {
    toStringCache = null;
    super.appendCodePoint(paramInt);
    return this;
  }
  
  public synchronized StringBuffer append(long paramLong)
  {
    toStringCache = null;
    super.append(paramLong);
    return this;
  }
  
  public synchronized StringBuffer append(float paramFloat)
  {
    toStringCache = null;
    super.append(paramFloat);
    return this;
  }
  
  public synchronized StringBuffer append(double paramDouble)
  {
    toStringCache = null;
    super.append(paramDouble);
    return this;
  }
  
  public synchronized StringBuffer delete(int paramInt1, int paramInt2)
  {
    toStringCache = null;
    super.delete(paramInt1, paramInt2);
    return this;
  }
  
  public synchronized StringBuffer deleteCharAt(int paramInt)
  {
    toStringCache = null;
    super.deleteCharAt(paramInt);
    return this;
  }
  
  public synchronized StringBuffer replace(int paramInt1, int paramInt2, String paramString)
  {
    toStringCache = null;
    super.replace(paramInt1, paramInt2, paramString);
    return this;
  }
  
  public synchronized String substring(int paramInt)
  {
    return substring(paramInt, count);
  }
  
  public synchronized CharSequence subSequence(int paramInt1, int paramInt2)
  {
    return super.substring(paramInt1, paramInt2);
  }
  
  public synchronized String substring(int paramInt1, int paramInt2)
  {
    return super.substring(paramInt1, paramInt2);
  }
  
  public synchronized StringBuffer insert(int paramInt1, char[] paramArrayOfChar, int paramInt2, int paramInt3)
  {
    toStringCache = null;
    super.insert(paramInt1, paramArrayOfChar, paramInt2, paramInt3);
    return this;
  }
  
  public synchronized StringBuffer insert(int paramInt, Object paramObject)
  {
    toStringCache = null;
    super.insert(paramInt, String.valueOf(paramObject));
    return this;
  }
  
  public synchronized StringBuffer insert(int paramInt, String paramString)
  {
    toStringCache = null;
    super.insert(paramInt, paramString);
    return this;
  }
  
  public synchronized StringBuffer insert(int paramInt, char[] paramArrayOfChar)
  {
    toStringCache = null;
    super.insert(paramInt, paramArrayOfChar);
    return this;
  }
  
  public StringBuffer insert(int paramInt, CharSequence paramCharSequence)
  {
    super.insert(paramInt, paramCharSequence);
    return this;
  }
  
  public synchronized StringBuffer insert(int paramInt1, CharSequence paramCharSequence, int paramInt2, int paramInt3)
  {
    toStringCache = null;
    super.insert(paramInt1, paramCharSequence, paramInt2, paramInt3);
    return this;
  }
  
  public StringBuffer insert(int paramInt, boolean paramBoolean)
  {
    super.insert(paramInt, paramBoolean);
    return this;
  }
  
  public synchronized StringBuffer insert(int paramInt, char paramChar)
  {
    toStringCache = null;
    super.insert(paramInt, paramChar);
    return this;
  }
  
  public StringBuffer insert(int paramInt1, int paramInt2)
  {
    super.insert(paramInt1, paramInt2);
    return this;
  }
  
  public StringBuffer insert(int paramInt, long paramLong)
  {
    super.insert(paramInt, paramLong);
    return this;
  }
  
  public StringBuffer insert(int paramInt, float paramFloat)
  {
    super.insert(paramInt, paramFloat);
    return this;
  }
  
  public StringBuffer insert(int paramInt, double paramDouble)
  {
    super.insert(paramInt, paramDouble);
    return this;
  }
  
  public int indexOf(String paramString)
  {
    return super.indexOf(paramString);
  }
  
  public synchronized int indexOf(String paramString, int paramInt)
  {
    return super.indexOf(paramString, paramInt);
  }
  
  public int lastIndexOf(String paramString)
  {
    return lastIndexOf(paramString, count);
  }
  
  public synchronized int lastIndexOf(String paramString, int paramInt)
  {
    return super.lastIndexOf(paramString, paramInt);
  }
  
  public synchronized StringBuffer reverse()
  {
    toStringCache = null;
    super.reverse();
    return this;
  }
  
  public synchronized String toString()
  {
    if (toStringCache == null) {
      toStringCache = Arrays.copyOfRange(value, 0, count);
    }
    return new String(toStringCache, true);
  }
  
  private synchronized void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    ObjectOutputStream.PutField localPutField = paramObjectOutputStream.putFields();
    localPutField.put("value", value);
    localPutField.put("count", count);
    localPutField.put("shared", false);
    paramObjectOutputStream.writeFields();
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    ObjectInputStream.GetField localGetField = paramObjectInputStream.readFields();
    value = ((char[])localGetField.get("value", null));
    count = localGetField.get("count", 0);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\lang\StringBuffer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */