package sun.instrument;

import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;

public class TransformerManager
{
  private TransformerInfo[] mTransformerList = new TransformerInfo[0];
  private boolean mIsRetransformable;
  
  TransformerManager(boolean paramBoolean)
  {
    mIsRetransformable = paramBoolean;
  }
  
  boolean isRetransformable()
  {
    return mIsRetransformable;
  }
  
  public synchronized void addTransformer(ClassFileTransformer paramClassFileTransformer)
  {
    TransformerInfo[] arrayOfTransformerInfo1 = mTransformerList;
    TransformerInfo[] arrayOfTransformerInfo2 = new TransformerInfo[arrayOfTransformerInfo1.length + 1];
    System.arraycopy(arrayOfTransformerInfo1, 0, arrayOfTransformerInfo2, 0, arrayOfTransformerInfo1.length);
    arrayOfTransformerInfo2[arrayOfTransformerInfo1.length] = new TransformerInfo(paramClassFileTransformer);
    mTransformerList = arrayOfTransformerInfo2;
  }
  
  public synchronized boolean removeTransformer(ClassFileTransformer paramClassFileTransformer)
  {
    boolean bool = false;
    TransformerInfo[] arrayOfTransformerInfo1 = mTransformerList;
    int i = arrayOfTransformerInfo1.length;
    int j = i - 1;
    int k = 0;
    for (int m = i - 1; m >= 0; m--) {
      if (arrayOfTransformerInfo1[m].transformer() == paramClassFileTransformer)
      {
        bool = true;
        k = m;
        break;
      }
    }
    if (bool)
    {
      TransformerInfo[] arrayOfTransformerInfo2 = new TransformerInfo[j];
      if (k > 0) {
        System.arraycopy(arrayOfTransformerInfo1, 0, arrayOfTransformerInfo2, 0, k);
      }
      if (k < j) {
        System.arraycopy(arrayOfTransformerInfo1, k + 1, arrayOfTransformerInfo2, k, j - k);
      }
      mTransformerList = arrayOfTransformerInfo2;
    }
    return bool;
  }
  
  synchronized boolean includesTransformer(ClassFileTransformer paramClassFileTransformer)
  {
    for (TransformerInfo localTransformerInfo : mTransformerList) {
      if (localTransformerInfo.transformer() == paramClassFileTransformer) {
        return true;
      }
    }
    return false;
  }
  
  private TransformerInfo[] getSnapshotTransformerList()
  {
    return mTransformerList;
  }
  
  public byte[] transform(ClassLoader paramClassLoader, String paramString, Class<?> paramClass, ProtectionDomain paramProtectionDomain, byte[] paramArrayOfByte)
  {
    int i = 0;
    TransformerInfo[] arrayOfTransformerInfo = getSnapshotTransformerList();
    Object localObject1 = paramArrayOfByte;
    for (int j = 0; j < arrayOfTransformerInfo.length; j++)
    {
      TransformerInfo localTransformerInfo = arrayOfTransformerInfo[j];
      ClassFileTransformer localClassFileTransformer = localTransformerInfo.transformer();
      byte[] arrayOfByte = null;
      try
      {
        arrayOfByte = localClassFileTransformer.transform(paramClassLoader, paramString, paramClass, paramProtectionDomain, (byte[])localObject1);
      }
      catch (Throwable localThrowable) {}
      if (arrayOfByte != null)
      {
        i = 1;
        localObject1 = arrayOfByte;
      }
    }
    Object localObject2;
    if (i != 0) {
      localObject2 = localObject1;
    } else {
      localObject2 = null;
    }
    return (byte[])localObject2;
  }
  
  int getTransformerCount()
  {
    TransformerInfo[] arrayOfTransformerInfo = getSnapshotTransformerList();
    return arrayOfTransformerInfo.length;
  }
  
  boolean setNativeMethodPrefix(ClassFileTransformer paramClassFileTransformer, String paramString)
  {
    TransformerInfo[] arrayOfTransformerInfo = getSnapshotTransformerList();
    for (int i = 0; i < arrayOfTransformerInfo.length; i++)
    {
      TransformerInfo localTransformerInfo = arrayOfTransformerInfo[i];
      ClassFileTransformer localClassFileTransformer = localTransformerInfo.transformer();
      if (localClassFileTransformer == paramClassFileTransformer)
      {
        localTransformerInfo.setPrefix(paramString);
        return true;
      }
    }
    return false;
  }
  
  String[] getNativeMethodPrefixes()
  {
    TransformerInfo[] arrayOfTransformerInfo = getSnapshotTransformerList();
    String[] arrayOfString = new String[arrayOfTransformerInfo.length];
    for (int i = 0; i < arrayOfTransformerInfo.length; i++)
    {
      TransformerInfo localTransformerInfo = arrayOfTransformerInfo[i];
      arrayOfString[i] = localTransformerInfo.getPrefix();
    }
    return arrayOfString;
  }
  
  private class TransformerInfo
  {
    final ClassFileTransformer mTransformer;
    String mPrefix;
    
    TransformerInfo(ClassFileTransformer paramClassFileTransformer)
    {
      mTransformer = paramClassFileTransformer;
      mPrefix = null;
    }
    
    ClassFileTransformer transformer()
    {
      return mTransformer;
    }
    
    String getPrefix()
    {
      return mPrefix;
    }
    
    void setPrefix(String paramString)
    {
      mPrefix = paramString;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\instrument\TransformerManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */