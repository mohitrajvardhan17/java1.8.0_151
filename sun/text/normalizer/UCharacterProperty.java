package sun.text.normalizer;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.MissingResourceException;

public final class UCharacterProperty
{
  public CharTrie m_trie_;
  public char[] m_trieIndex_;
  public char[] m_trieData_;
  public int m_trieInitialValue_;
  public VersionInfo m_unicodeVersion_;
  public static final int SRC_PROPSVEC = 2;
  public static final int SRC_COUNT = 9;
  CharTrie m_additionalTrie_;
  int[] m_additionalVectors_;
  int m_additionalColumnsCount_;
  int m_maxBlockScriptValue_;
  int m_maxJTGValue_;
  private static UCharacterProperty INSTANCE_ = null;
  private static final String DATA_FILE_NAME_ = "/sun/text/resources/uprops.icu";
  private static final int DATA_BUFFER_SIZE_ = 25000;
  private static final int VALUE_SHIFT_ = 8;
  private static final int UNSIGNED_VALUE_MASK_AFTER_SHIFT_ = 255;
  private static final int LEAD_SURROGATE_SHIFT_ = 10;
  private static final int SURROGATE_OFFSET_ = -56613888;
  private static final int FIRST_NIBBLE_SHIFT_ = 4;
  private static final int LAST_NIBBLE_MASK_ = 15;
  private static final int AGE_SHIFT_ = 24;
  
  public void setIndexData(CharTrie.FriendAgent paramFriendAgent)
  {
    m_trieIndex_ = paramFriendAgent.getPrivateIndex();
    m_trieData_ = paramFriendAgent.getPrivateData();
    m_trieInitialValue_ = paramFriendAgent.getPrivateInitialValue();
  }
  
  public final int getProperty(int paramInt)
  {
    if ((paramInt < 55296) || ((paramInt > 56319) && (paramInt < 65536))) {
      try
      {
        return m_trieData_[((m_trieIndex_[(paramInt >> 5)] << '\002') + (paramInt & 0x1F))];
      }
      catch (ArrayIndexOutOfBoundsException localArrayIndexOutOfBoundsException)
      {
        return m_trieInitialValue_;
      }
    }
    if (paramInt <= 56319) {
      return m_trieData_[((m_trieIndex_[(320 + (paramInt >> 5))] << '\002') + (paramInt & 0x1F))];
    }
    if (paramInt <= 1114111) {
      return m_trie_.getSurrogateValue(UTF16.getLeadSurrogate(paramInt), (char)(paramInt & 0x3FF));
    }
    return m_trieInitialValue_;
  }
  
  public static int getUnsignedValue(int paramInt)
  {
    return paramInt >> 8 & 0xFF;
  }
  
  public int getAdditional(int paramInt1, int paramInt2)
  {
    if (paramInt2 == -1) {
      return getProperty(paramInt1);
    }
    if ((paramInt2 < 0) || (paramInt2 >= m_additionalColumnsCount_)) {
      return 0;
    }
    return m_additionalVectors_[(m_additionalTrie_.getCodePointValue(paramInt1) + paramInt2)];
  }
  
  public VersionInfo getAge(int paramInt)
  {
    int i = getAdditional(paramInt, 0) >> 24;
    return VersionInfo.getInstance(i >> 4 & 0xF, i & 0xF, 0, 0);
  }
  
  public static int getRawSupplementary(char paramChar1, char paramChar2)
  {
    return (paramChar1 << '\n') + paramChar2 + -56613888;
  }
  
  public static UCharacterProperty getInstance()
  {
    if (INSTANCE_ == null) {
      try
      {
        INSTANCE_ = new UCharacterProperty();
      }
      catch (Exception localException)
      {
        throw new MissingResourceException(localException.getMessage(), "", "");
      }
    }
    return INSTANCE_;
  }
  
  public static boolean isRuleWhiteSpace(int paramInt)
  {
    return (paramInt >= 9) && (paramInt <= 8233) && ((paramInt <= 13) || (paramInt == 32) || (paramInt == 133) || (paramInt == 8206) || (paramInt == 8207) || (paramInt >= 8232));
  }
  
  private UCharacterProperty()
    throws IOException
  {
    InputStream localInputStream = ICUData.getRequiredStream("/sun/text/resources/uprops.icu");
    BufferedInputStream localBufferedInputStream = new BufferedInputStream(localInputStream, 25000);
    UCharacterPropertyReader localUCharacterPropertyReader = new UCharacterPropertyReader(localBufferedInputStream);
    localUCharacterPropertyReader.read(this);
    localBufferedInputStream.close();
    m_trie_.putIndexData(this);
  }
  
  public void upropsvec_addPropertyStarts(UnicodeSet paramUnicodeSet)
  {
    if (m_additionalColumnsCount_ > 0)
    {
      TrieIterator localTrieIterator = new TrieIterator(m_additionalTrie_);
      RangeValueIterator.Element localElement = new RangeValueIterator.Element();
      while (localTrieIterator.next(localElement)) {
        paramUnicodeSet.add(start);
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\text\normalizer\UCharacterProperty.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */