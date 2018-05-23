package sun.text.normalizer;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

public class CharTrie
  extends Trie
{
  private char m_initialValue_;
  private char[] m_data_;
  private FriendAgent m_friendAgent_;
  
  public CharTrie(InputStream paramInputStream, Trie.DataManipulate paramDataManipulate)
    throws IOException
  {
    super(paramInputStream, paramDataManipulate);
    if (!isCharTrie()) {
      throw new IllegalArgumentException("Data given does not belong to a char trie.");
    }
    m_friendAgent_ = new FriendAgent();
  }
  
  public CharTrie(int paramInt1, int paramInt2, Trie.DataManipulate paramDataManipulate)
  {
    super(new char['ࠠ'], 512, paramDataManipulate);
    int j;
    int i = j = 'Ā';
    if (paramInt2 != paramInt1) {
      i += 32;
    }
    m_data_ = new char[i];
    m_dataLength_ = i;
    m_initialValue_ = ((char)paramInt1);
    for (int k = 0; k < j; k++) {
      m_data_[k] = ((char)paramInt1);
    }
    if (paramInt2 != paramInt1)
    {
      int n = (char)(j >> 2);
      k = 1728;
      int m = 1760;
      while (k < m)
      {
        m_index_[k] = n;
        k++;
      }
      m = j + 32;
      for (k = j; k < m; k++) {
        m_data_[k] = ((char)paramInt2);
      }
    }
    m_friendAgent_ = new FriendAgent();
  }
  
  public void putIndexData(UCharacterProperty paramUCharacterProperty)
  {
    paramUCharacterProperty.setIndexData(m_friendAgent_);
  }
  
  public final char getCodePointValue(int paramInt)
  {
    if ((0 <= paramInt) && (paramInt < 55296))
    {
      i = (m_index_[(paramInt >> 5)] << '\002') + (paramInt & 0x1F);
      return m_data_[i];
    }
    int i = getCodePointOffset(paramInt);
    return i >= 0 ? m_data_[i] : m_initialValue_;
  }
  
  public final char getLeadValue(char paramChar)
  {
    return m_data_[getLeadOffset(paramChar)];
  }
  
  public final char getSurrogateValue(char paramChar1, char paramChar2)
  {
    int i = getSurrogateOffset(paramChar1, paramChar2);
    if (i > 0) {
      return m_data_[i];
    }
    return m_initialValue_;
  }
  
  public final char getTrailValue(int paramInt, char paramChar)
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
    DataInputStream localDataInputStream = new DataInputStream(paramInputStream);
    int i = m_dataOffset_ + m_dataLength_;
    m_index_ = new char[i];
    for (int j = 0; j < i; j++) {
      m_index_[j] = localDataInputStream.readChar();
    }
    m_data_ = m_index_;
    m_initialValue_ = m_data_[m_dataOffset_];
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
  
  public class FriendAgent
  {
    public FriendAgent() {}
    
    public char[] getPrivateIndex()
    {
      return m_index_;
    }
    
    public char[] getPrivateData()
    {
      return m_data_;
    }
    
    public int getPrivateInitialValue()
    {
      return m_initialValue_;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\text\normalizer\CharTrie.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */