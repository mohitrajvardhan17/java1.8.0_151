package java.sql;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectInputStream.GetField;
import java.io.ObjectOutputStream;
import java.io.ObjectOutputStream.PutField;
import java.util.Arrays;

public class BatchUpdateException
  extends SQLException
{
  private int[] updateCounts;
  private long[] longUpdateCounts;
  private static final long serialVersionUID = 5977529877145521757L;
  
  public BatchUpdateException(String paramString1, String paramString2, int paramInt, int[] paramArrayOfInt)
  {
    super(paramString1, paramString2, paramInt);
    updateCounts = (paramArrayOfInt == null ? null : Arrays.copyOf(paramArrayOfInt, paramArrayOfInt.length));
    longUpdateCounts = (paramArrayOfInt == null ? null : copyUpdateCount(paramArrayOfInt));
  }
  
  public BatchUpdateException(String paramString1, String paramString2, int[] paramArrayOfInt)
  {
    this(paramString1, paramString2, 0, paramArrayOfInt);
  }
  
  public BatchUpdateException(String paramString, int[] paramArrayOfInt)
  {
    this(paramString, null, 0, paramArrayOfInt);
  }
  
  public BatchUpdateException(int[] paramArrayOfInt)
  {
    this(null, null, 0, paramArrayOfInt);
  }
  
  public BatchUpdateException()
  {
    this(null, null, 0, null);
  }
  
  public BatchUpdateException(Throwable paramThrowable)
  {
    this(paramThrowable == null ? null : paramThrowable.toString(), null, 0, (int[])null, paramThrowable);
  }
  
  public BatchUpdateException(int[] paramArrayOfInt, Throwable paramThrowable)
  {
    this(paramThrowable == null ? null : paramThrowable.toString(), null, 0, paramArrayOfInt, paramThrowable);
  }
  
  public BatchUpdateException(String paramString, int[] paramArrayOfInt, Throwable paramThrowable)
  {
    this(paramString, null, 0, paramArrayOfInt, paramThrowable);
  }
  
  public BatchUpdateException(String paramString1, String paramString2, int[] paramArrayOfInt, Throwable paramThrowable)
  {
    this(paramString1, paramString2, 0, paramArrayOfInt, paramThrowable);
  }
  
  public BatchUpdateException(String paramString1, String paramString2, int paramInt, int[] paramArrayOfInt, Throwable paramThrowable)
  {
    super(paramString1, paramString2, paramInt, paramThrowable);
    updateCounts = (paramArrayOfInt == null ? null : Arrays.copyOf(paramArrayOfInt, paramArrayOfInt.length));
    longUpdateCounts = (paramArrayOfInt == null ? null : copyUpdateCount(paramArrayOfInt));
  }
  
  public int[] getUpdateCounts()
  {
    return updateCounts == null ? null : Arrays.copyOf(updateCounts, updateCounts.length);
  }
  
  public BatchUpdateException(String paramString1, String paramString2, int paramInt, long[] paramArrayOfLong, Throwable paramThrowable)
  {
    super(paramString1, paramString2, paramInt, paramThrowable);
    longUpdateCounts = (paramArrayOfLong == null ? null : Arrays.copyOf(paramArrayOfLong, paramArrayOfLong.length));
    updateCounts = (longUpdateCounts == null ? null : copyUpdateCount(longUpdateCounts));
  }
  
  public long[] getLargeUpdateCounts()
  {
    return longUpdateCounts == null ? null : Arrays.copyOf(longUpdateCounts, longUpdateCounts.length);
  }
  
  private static long[] copyUpdateCount(int[] paramArrayOfInt)
  {
    long[] arrayOfLong = new long[paramArrayOfInt.length];
    for (int i = 0; i < paramArrayOfInt.length; i++) {
      arrayOfLong[i] = paramArrayOfInt[i];
    }
    return arrayOfLong;
  }
  
  private static int[] copyUpdateCount(long[] paramArrayOfLong)
  {
    int[] arrayOfInt = new int[paramArrayOfLong.length];
    for (int i = 0; i < paramArrayOfLong.length; i++) {
      arrayOfInt[i] = ((int)paramArrayOfLong[i]);
    }
    return arrayOfInt;
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    ObjectInputStream.GetField localGetField = paramObjectInputStream.readFields();
    int[] arrayOfInt = (int[])localGetField.get("updateCounts", null);
    long[] arrayOfLong = (long[])localGetField.get("longUpdateCounts", null);
    if ((arrayOfInt != null) && (arrayOfLong != null) && (arrayOfInt.length != arrayOfLong.length)) {
      throw new InvalidObjectException("update counts are not the expected size");
    }
    if (arrayOfInt != null) {
      updateCounts = ((int[])arrayOfInt.clone());
    }
    if (arrayOfLong != null) {
      longUpdateCounts = ((long[])arrayOfLong.clone());
    }
    if ((updateCounts == null) && (longUpdateCounts != null)) {
      updateCounts = copyUpdateCount(longUpdateCounts);
    }
    if ((longUpdateCounts == null) && (updateCounts != null)) {
      longUpdateCounts = copyUpdateCount(updateCounts);
    }
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException, ClassNotFoundException
  {
    ObjectOutputStream.PutField localPutField = paramObjectOutputStream.putFields();
    localPutField.put("updateCounts", updateCounts);
    localPutField.put("longUpdateCounts", longUpdateCounts);
    paramObjectOutputStream.writeFields();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\sql\BatchUpdateException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */