package sun.text.normalizer;

public class TrieIterator
  implements RangeValueIterator
{
  private static final int BMP_INDEX_LENGTH_ = 2048;
  private static final int LEAD_SURROGATE_MIN_VALUE_ = 55296;
  private static final int TRAIL_SURROGATE_MIN_VALUE_ = 56320;
  private static final int TRAIL_SURROGATE_COUNT_ = 1024;
  private static final int TRAIL_SURROGATE_INDEX_BLOCK_LENGTH_ = 32;
  private static final int DATA_BLOCK_LENGTH_ = 32;
  private Trie m_trie_;
  private int m_initialValue_;
  private int m_currentCodepoint_;
  private int m_nextCodepoint_;
  private int m_nextValue_;
  private int m_nextIndex_;
  private int m_nextBlock_;
  private int m_nextBlockIndex_;
  private int m_nextTrailIndexOffset_;
  
  public TrieIterator(Trie paramTrie)
  {
    if (paramTrie == null) {
      throw new IllegalArgumentException("Argument trie cannot be null");
    }
    m_trie_ = paramTrie;
    m_initialValue_ = extract(m_trie_.getInitialValue());
    reset();
  }
  
  public final boolean next(RangeValueIterator.Element paramElement)
  {
    if (m_nextCodepoint_ > 1114111) {
      return false;
    }
    if ((m_nextCodepoint_ < 65536) && (calculateNextBMPElement(paramElement))) {
      return true;
    }
    calculateNextSupplementaryElement(paramElement);
    return true;
  }
  
  public final void reset()
  {
    m_currentCodepoint_ = 0;
    m_nextCodepoint_ = 0;
    m_nextIndex_ = 0;
    m_nextBlock_ = (m_trie_.m_index_[0] << '\002');
    if (m_nextBlock_ == 0) {
      m_nextValue_ = m_initialValue_;
    } else {
      m_nextValue_ = extract(m_trie_.getValue(m_nextBlock_));
    }
    m_nextBlockIndex_ = 0;
    m_nextTrailIndexOffset_ = 32;
  }
  
  protected int extract(int paramInt)
  {
    return paramInt;
  }
  
  private final void setResult(RangeValueIterator.Element paramElement, int paramInt1, int paramInt2, int paramInt3)
  {
    start = paramInt1;
    limit = paramInt2;
    value = paramInt3;
  }
  
  private final boolean calculateNextBMPElement(RangeValueIterator.Element paramElement)
  {
    int i = m_nextBlock_;
    int j = m_nextValue_;
    m_currentCodepoint_ = m_nextCodepoint_;
    m_nextCodepoint_ += 1;
    m_nextBlockIndex_ += 1;
    if (!checkBlockDetail(j))
    {
      setResult(paramElement, m_currentCodepoint_, m_nextCodepoint_, j);
      return true;
    }
    while (m_nextCodepoint_ < 65536)
    {
      m_nextIndex_ += 1;
      if (m_nextCodepoint_ == 55296) {
        m_nextIndex_ = 2048;
      } else if (m_nextCodepoint_ == 56320) {
        m_nextIndex_ = (m_nextCodepoint_ >> 5);
      }
      m_nextBlockIndex_ = 0;
      if (!checkBlock(i, j))
      {
        setResult(paramElement, m_currentCodepoint_, m_nextCodepoint_, j);
        return true;
      }
    }
    m_nextCodepoint_ -= 1;
    m_nextBlockIndex_ -= 1;
    return false;
  }
  
  private final void calculateNextSupplementaryElement(RangeValueIterator.Element paramElement)
  {
    int i = m_nextValue_;
    int j = m_nextBlock_;
    m_nextCodepoint_ += 1;
    m_nextBlockIndex_ += 1;
    if (UTF16.getTrailSurrogate(m_nextCodepoint_) != 56320)
    {
      if ((!checkNullNextTrailIndex()) && (!checkBlockDetail(i)))
      {
        setResult(paramElement, m_currentCodepoint_, m_nextCodepoint_, i);
        m_currentCodepoint_ = m_nextCodepoint_;
        return;
      }
      m_nextIndex_ += 1;
      m_nextTrailIndexOffset_ += 1;
      if (!checkTrailBlock(j, i))
      {
        setResult(paramElement, m_currentCodepoint_, m_nextCodepoint_, i);
        m_currentCodepoint_ = m_nextCodepoint_;
        return;
      }
    }
    int k = UTF16.getLeadSurrogate(m_nextCodepoint_);
    while (k < 56320)
    {
      int m = m_trie_.m_index_[(k >> 5)] << '\002';
      if (m == m_trie_.m_dataOffset_)
      {
        if (i != m_initialValue_)
        {
          m_nextValue_ = m_initialValue_;
          m_nextBlock_ = 0;
          m_nextBlockIndex_ = 0;
          setResult(paramElement, m_currentCodepoint_, m_nextCodepoint_, i);
          m_currentCodepoint_ = m_nextCodepoint_;
          return;
        }
        k += 32;
        m_nextCodepoint_ = UCharacterProperty.getRawSupplementary((char)k, 56320);
      }
      else
      {
        if (m_trie_.m_dataManipulate_ == null) {
          throw new NullPointerException("The field DataManipulate in this Trie is null");
        }
        m_nextIndex_ = m_trie_.m_dataManipulate_.getFoldingOffset(m_trie_.getValue(m + (k & 0x1F)));
        if (m_nextIndex_ <= 0)
        {
          if (i != m_initialValue_)
          {
            m_nextValue_ = m_initialValue_;
            m_nextBlock_ = 0;
            m_nextBlockIndex_ = 0;
            setResult(paramElement, m_currentCodepoint_, m_nextCodepoint_, i);
            m_currentCodepoint_ = m_nextCodepoint_;
            return;
          }
          m_nextCodepoint_ += 1024;
        }
        else
        {
          m_nextTrailIndexOffset_ = 0;
          if (!checkTrailBlock(j, i))
          {
            setResult(paramElement, m_currentCodepoint_, m_nextCodepoint_, i);
            m_currentCodepoint_ = m_nextCodepoint_;
            return;
          }
        }
        k++;
      }
    }
    setResult(paramElement, m_currentCodepoint_, 1114112, i);
  }
  
  private final boolean checkBlockDetail(int paramInt)
  {
    while (m_nextBlockIndex_ < 32)
    {
      m_nextValue_ = extract(m_trie_.getValue(m_nextBlock_ + m_nextBlockIndex_));
      if (m_nextValue_ != paramInt) {
        return false;
      }
      m_nextBlockIndex_ += 1;
      m_nextCodepoint_ += 1;
    }
    return true;
  }
  
  private final boolean checkBlock(int paramInt1, int paramInt2)
  {
    m_nextBlock_ = (m_trie_.m_index_[m_nextIndex_] << '\002');
    if ((m_nextBlock_ == paramInt1) && (m_nextCodepoint_ - m_currentCodepoint_ >= 32))
    {
      m_nextCodepoint_ += 32;
    }
    else if (m_nextBlock_ == 0)
    {
      if (paramInt2 != m_initialValue_)
      {
        m_nextValue_ = m_initialValue_;
        m_nextBlockIndex_ = 0;
        return false;
      }
      m_nextCodepoint_ += 32;
    }
    else if (!checkBlockDetail(paramInt2))
    {
      return false;
    }
    return true;
  }
  
  private final boolean checkTrailBlock(int paramInt1, int paramInt2)
  {
    while (m_nextTrailIndexOffset_ < 32)
    {
      m_nextBlockIndex_ = 0;
      if (!checkBlock(paramInt1, paramInt2)) {
        return false;
      }
      m_nextTrailIndexOffset_ += 1;
      m_nextIndex_ += 1;
    }
    return true;
  }
  
  private final boolean checkNullNextTrailIndex()
  {
    if (m_nextIndex_ <= 0)
    {
      m_nextCodepoint_ += 1023;
      int i = UTF16.getLeadSurrogate(m_nextCodepoint_);
      int j = m_trie_.m_index_[(i >> 5)] << '\002';
      if (m_trie_.m_dataManipulate_ == null) {
        throw new NullPointerException("The field DataManipulate in this Trie is null");
      }
      m_nextIndex_ = m_trie_.m_dataManipulate_.getFoldingOffset(m_trie_.getValue(j + (i & 0x1F)));
      m_nextIndex_ -= 1;
      m_nextBlockIndex_ = 32;
      return true;
    }
    return false;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\text\normalizer\TrieIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */