package sun.text.normalizer;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

public abstract class Trie
{
  protected static final int LEAD_INDEX_OFFSET_ = 320;
  protected static final int INDEX_STAGE_1_SHIFT_ = 5;
  protected static final int INDEX_STAGE_2_SHIFT_ = 2;
  protected static final int DATA_BLOCK_LENGTH = 32;
  protected static final int INDEX_STAGE_3_MASK_ = 31;
  protected static final int SURROGATE_BLOCK_BITS = 5;
  protected static final int SURROGATE_BLOCK_COUNT = 32;
  protected static final int BMP_INDEX_LENGTH = 2048;
  protected static final int SURROGATE_MASK_ = 1023;
  protected char[] m_index_;
  protected DataManipulate m_dataManipulate_;
  protected int m_dataOffset_;
  protected int m_dataLength_;
  protected static final int HEADER_OPTIONS_LATIN1_IS_LINEAR_MASK_ = 512;
  protected static final int HEADER_SIGNATURE_ = 1416784229;
  private static final int HEADER_OPTIONS_SHIFT_MASK_ = 15;
  protected static final int HEADER_OPTIONS_INDEX_SHIFT_ = 4;
  protected static final int HEADER_OPTIONS_DATA_IS_32_BIT_ = 256;
  private boolean m_isLatin1Linear_;
  private int m_options_;
  
  protected Trie(InputStream paramInputStream, DataManipulate paramDataManipulate)
    throws IOException
  {
    DataInputStream localDataInputStream = new DataInputStream(paramInputStream);
    int i = localDataInputStream.readInt();
    m_options_ = localDataInputStream.readInt();
    if (!checkHeader(i)) {
      throw new IllegalArgumentException("ICU data file error: Trie header authentication failed, please check if you have the most updated ICU data file");
    }
    if (paramDataManipulate != null) {
      m_dataManipulate_ = paramDataManipulate;
    } else {
      m_dataManipulate_ = new DefaultGetFoldingOffset(null);
    }
    m_isLatin1Linear_ = ((m_options_ & 0x200) != 0);
    m_dataOffset_ = localDataInputStream.readInt();
    m_dataLength_ = localDataInputStream.readInt();
    unserialize(paramInputStream);
  }
  
  protected Trie(char[] paramArrayOfChar, int paramInt, DataManipulate paramDataManipulate)
  {
    m_options_ = paramInt;
    if (paramDataManipulate != null) {
      m_dataManipulate_ = paramDataManipulate;
    } else {
      m_dataManipulate_ = new DefaultGetFoldingOffset(null);
    }
    m_isLatin1Linear_ = ((m_options_ & 0x200) != 0);
    m_index_ = paramArrayOfChar;
    m_dataOffset_ = m_index_.length;
  }
  
  protected abstract int getSurrogateOffset(char paramChar1, char paramChar2);
  
  protected abstract int getValue(int paramInt);
  
  protected abstract int getInitialValue();
  
  protected final int getRawOffset(int paramInt, char paramChar)
  {
    return (m_index_[(paramInt + (paramChar >> '\005'))] << '\002') + (paramChar & 0x1F);
  }
  
  protected final int getBMPOffset(char paramChar)
  {
    return (paramChar >= 55296) && (paramChar <= 56319) ? getRawOffset(320, paramChar) : getRawOffset(0, paramChar);
  }
  
  protected final int getLeadOffset(char paramChar)
  {
    return getRawOffset(0, paramChar);
  }
  
  protected final int getCodePointOffset(int paramInt)
  {
    if (paramInt < 0) {
      return -1;
    }
    if (paramInt < 55296) {
      return getRawOffset(0, (char)paramInt);
    }
    if (paramInt < 65536) {
      return getBMPOffset((char)paramInt);
    }
    if (paramInt <= 1114111) {
      return getSurrogateOffset(UTF16.getLeadSurrogate(paramInt), (char)(paramInt & 0x3FF));
    }
    return -1;
  }
  
  protected void unserialize(InputStream paramInputStream)
    throws IOException
  {
    m_index_ = new char[m_dataOffset_];
    DataInputStream localDataInputStream = new DataInputStream(paramInputStream);
    for (int i = 0; i < m_dataOffset_; i++) {
      m_index_[i] = localDataInputStream.readChar();
    }
  }
  
  protected final boolean isIntTrie()
  {
    return (m_options_ & 0x100) != 0;
  }
  
  protected final boolean isCharTrie()
  {
    return (m_options_ & 0x100) == 0;
  }
  
  private final boolean checkHeader(int paramInt)
  {
    if (paramInt != 1416784229) {
      return false;
    }
    return ((m_options_ & 0xF) == 5) && ((m_options_ >> 4 & 0xF) == 2);
  }
  
  public static abstract interface DataManipulate
  {
    public abstract int getFoldingOffset(int paramInt);
  }
  
  private static class DefaultGetFoldingOffset
    implements Trie.DataManipulate
  {
    private DefaultGetFoldingOffset() {}
    
    public int getFoldingOffset(int paramInt)
    {
      return paramInt;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\text\normalizer\Trie.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */