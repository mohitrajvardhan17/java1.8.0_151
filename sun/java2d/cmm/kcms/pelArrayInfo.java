package sun.java2d.cmm.kcms;

class pelArrayInfo
{
  int nPels;
  int nSrc;
  int srcSize;
  int nDest;
  int destSize;
  
  pelArrayInfo(ICC_Transform paramICC_Transform, int paramInt, float[] paramArrayOfFloat1, float[] paramArrayOfFloat2)
  {
    nSrc = paramICC_Transform.getNumInComponents();
    nDest = paramICC_Transform.getNumOutComponents();
    nPels = paramInt;
    srcSize = (nPels * nSrc);
    destSize = (nPels * nDest);
    if (srcSize > paramArrayOfFloat1.length) {
      throw new IllegalArgumentException("Inconsistent pel structure");
    }
    if (paramArrayOfFloat2 != null) {
      checkDest(paramArrayOfFloat2.length);
    }
  }
  
  pelArrayInfo(ICC_Transform paramICC_Transform, short[] paramArrayOfShort1, short[] paramArrayOfShort2)
  {
    srcSize = paramArrayOfShort1.length;
    initInfo(paramICC_Transform);
    destSize = (nPels * nDest);
    if (paramArrayOfShort2 != null) {
      checkDest(paramArrayOfShort2.length);
    }
  }
  
  pelArrayInfo(ICC_Transform paramICC_Transform, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2)
  {
    srcSize = paramArrayOfByte1.length;
    initInfo(paramICC_Transform);
    destSize = (nPels * nDest);
    if (paramArrayOfByte2 != null) {
      checkDest(paramArrayOfByte2.length);
    }
  }
  
  void initInfo(ICC_Transform paramICC_Transform)
  {
    nSrc = paramICC_Transform.getNumInComponents();
    nDest = paramICC_Transform.getNumOutComponents();
    nPels = (srcSize / nSrc);
    if (nPels * nSrc != srcSize) {
      throw new IllegalArgumentException("Inconsistent pel structure");
    }
  }
  
  void checkDest(int paramInt)
  {
    if (destSize > paramInt) {
      throw new IllegalArgumentException("Inconsistent pel structure");
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\java2d\cmm\kcms\pelArrayInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */