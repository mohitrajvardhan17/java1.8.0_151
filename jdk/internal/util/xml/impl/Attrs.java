package jdk.internal.util.xml.impl;

import jdk.internal.org.xml.sax.Attributes;

public class Attrs
  implements Attributes
{
  String[] mItems = new String[64];
  private char mLength;
  private char mAttrIdx = '\000';
  
  public Attrs() {}
  
  public void setLength(char paramChar)
  {
    if (paramChar > (char)(mItems.length >> 3)) {
      mItems = new String[paramChar << '\003'];
    }
    mLength = paramChar;
  }
  
  public int getLength()
  {
    return mLength;
  }
  
  public String getURI(int paramInt)
  {
    return (paramInt >= 0) && (paramInt < mLength) ? mItems[(paramInt << 3)] : null;
  }
  
  public String getLocalName(int paramInt)
  {
    return (paramInt >= 0) && (paramInt < mLength) ? mItems[((paramInt << 3) + 2)] : null;
  }
  
  public String getQName(int paramInt)
  {
    if ((paramInt < 0) || (paramInt >= mLength)) {
      return null;
    }
    return mItems[((paramInt << 3) + 1)];
  }
  
  public String getType(int paramInt)
  {
    return (paramInt >= 0) && (paramInt < mItems.length >> 3) ? mItems[((paramInt << 3) + 4)] : null;
  }
  
  public String getValue(int paramInt)
  {
    return (paramInt >= 0) && (paramInt < mLength) ? mItems[((paramInt << 3) + 3)] : null;
  }
  
  public int getIndex(String paramString1, String paramString2)
  {
    int i = mLength;
    for (int j = 0; j < i; j = (char)(j + 1)) {
      if ((mItems[(j << 3)].equals(paramString1)) && (mItems[((j << 3) + 2)].equals(paramString2))) {
        return j;
      }
    }
    return -1;
  }
  
  int getIndexNullNS(String paramString1, String paramString2)
  {
    int i = mLength;
    int j;
    if (paramString1 != null) {
      for (j = 0; j < i; j = (char)(j + 1)) {
        if ((mItems[(j << 3)].equals(paramString1)) && (mItems[((j << 3) + 2)].equals(paramString2))) {
          return j;
        }
      }
    } else {
      for (j = 0; j < i; j = (char)(j + 1)) {
        if (mItems[((j << 3) + 2)].equals(paramString2)) {
          return j;
        }
      }
    }
    return -1;
  }
  
  public int getIndex(String paramString)
  {
    int i = mLength;
    for (int j = 0; j < i; j = (char)(j + 1)) {
      if (mItems[((j << 3) + 1)].equals(paramString)) {
        return j;
      }
    }
    return -1;
  }
  
  public String getType(String paramString1, String paramString2)
  {
    int i = getIndex(paramString1, paramString2);
    return i >= 0 ? mItems[((i << 3) + 4)] : null;
  }
  
  public String getType(String paramString)
  {
    int i = getIndex(paramString);
    return i >= 0 ? mItems[((i << 3) + 4)] : null;
  }
  
  public String getValue(String paramString1, String paramString2)
  {
    int i = getIndex(paramString1, paramString2);
    return i >= 0 ? mItems[((i << 3) + 3)] : null;
  }
  
  public String getValue(String paramString)
  {
    int i = getIndex(paramString);
    return i >= 0 ? mItems[((i << 3) + 3)] : null;
  }
  
  public boolean isDeclared(int paramInt)
  {
    if ((paramInt < 0) || (paramInt >= mLength)) {
      throw new ArrayIndexOutOfBoundsException("");
    }
    return mItems[((paramInt << 3) + 5)] != null;
  }
  
  public boolean isDeclared(String paramString)
  {
    int i = getIndex(paramString);
    if (i < 0) {
      throw new IllegalArgumentException("");
    }
    return mItems[((i << 3) + 5)] != null;
  }
  
  public boolean isDeclared(String paramString1, String paramString2)
  {
    int i = getIndex(paramString1, paramString2);
    if (i < 0) {
      throw new IllegalArgumentException("");
    }
    return mItems[((i << 3) + 5)] != null;
  }
  
  public boolean isSpecified(int paramInt)
  {
    if ((paramInt < 0) || (paramInt >= mLength)) {
      throw new ArrayIndexOutOfBoundsException("");
    }
    String str = mItems[((paramInt << 3) + 5)];
    return str.charAt(0) == 'd';
  }
  
  public boolean isSpecified(String paramString1, String paramString2)
  {
    int i = getIndex(paramString1, paramString2);
    if (i < 0) {
      throw new IllegalArgumentException("");
    }
    String str = mItems[((i << 3) + 5)];
    return str.charAt(0) == 'd';
  }
  
  public boolean isSpecified(String paramString)
  {
    int i = getIndex(paramString);
    if (i < 0) {
      throw new IllegalArgumentException("");
    }
    String str = mItems[((i << 3) + 5)];
    return str.charAt(0) == 'd';
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\jdk\internal\util\xml\impl\Attrs.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */