package sun.text.normalizer;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

public class IntTrie
  extends Trie
{
  private int m_initialValue_;
  private int[] m_data_;
  
  public IntTrie(InputStream paramInputStream, Trie.DataManipulate paramDataManipulate)
    throws IOException
  {
    super(paramInputStream, paramDataManipulate);
    if (!isIntTrie()) {
      throw new IllegalArgumentException("Data given does not belong to a int trie.");
    }
  }
  
  public final int getCodePointValue(int paramInt)
  {
    int i = getCodePointOffset(paramInt);
    return i >= 0 ? m_data_[i] : m_initialValue_;
  }
  
  public final int getLeadValue(char paramChar)
  {
    return m_data_[getLeadOffset(paramChar)];
  }
  
  public final int getTrailValue(int paramInt, char paramChar)
  {
    if (m_dataManipulate_ == null) {
      throw new NullPointerException("The field DataManipulate in this Trie is null");
    }
    int i = m_dataManipulate_.getFoldingOffset(paramInt);
    if (i > 0) {
      return m_data_[getRawOffset(i, (char)(paramChar & 0x3FF))];
    }
    return m_initialValue_;
  }
  
  protected final void unserialize(InputStream paramInputStream)
    throws IOException
  {
    super.unserialize(paramInputStream);
    m_data_ = new int[m_dataLength_];
    DataInputStream localDataInputStream = new DataInputStream(paramInputStream);
    for (int i = 0; i < m_dataLength_; i++) {
      m_data_[i] = localDataInputStream.readInt();
    }
    m_initialValue_ = m_data_[0];
  }
  
  protected final int getSurrogateOffset(char paramChar1, char paramChar2)
  {
    if (m_dataManipulate_ == null) {
      throw new NullPointerException("The field DataManipulate in this Trie is null");
    }
    int i = m_dataManipulate_.getFoldingOffset(getLeadValue(paramChar1));
    if (i > 0) {
      return getRawOffset(i, (char)(paramChar2 & 0x3FF));
    }
    return -1;
  }
  
  protected final int getValue(int paramInt)
  {
    return m_data_[paramInt];
  }
  
  protected final int getInitialValue()
  {
    return m_initialValue_;
  }
  
  IntTrie(char[] paramArrayOfChar, int[] paramArrayOfInt, int paramInt1, int paramInt2, Trie.DataManipulate paramDataManipulate)
  {
    super(paramArrayOfChar, paramInt2, paramDataManipulate);
    m_data_ = paramArrayOfInt;
    m_dataLength_ = m_data_.length;
    m_initialValue_ = paramInt1;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\text\normalizer\IntTrie.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */