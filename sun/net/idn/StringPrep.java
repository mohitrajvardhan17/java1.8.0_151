package sun.net.idn;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.Normalizer.Form;
import java.text.ParseException;
import sun.text.Normalizer;
import sun.text.normalizer.CharTrie;
import sun.text.normalizer.NormalizerImpl;
import sun.text.normalizer.Trie.DataManipulate;
import sun.text.normalizer.UCharacter;
import sun.text.normalizer.UCharacterIterator;
import sun.text.normalizer.UTF16;
import sun.text.normalizer.VersionInfo;

public final class StringPrep
{
  public static final int DEFAULT = 0;
  public static final int ALLOW_UNASSIGNED = 1;
  private static final int UNASSIGNED = 0;
  private static final int MAP = 1;
  private static final int PROHIBITED = 2;
  private static final int DELETE = 3;
  private static final int TYPE_LIMIT = 4;
  private static final int NORMALIZATION_ON = 1;
  private static final int CHECK_BIDI_ON = 2;
  private static final int TYPE_THRESHOLD = 65520;
  private static final int MAX_INDEX_VALUE = 16319;
  private static final int MAX_INDEX_TOP_LENGTH = 3;
  private static final int INDEX_TRIE_SIZE = 0;
  private static final int INDEX_MAPPING_DATA_SIZE = 1;
  private static final int NORM_CORRECTNS_LAST_UNI_VERSION = 2;
  private static final int ONE_UCHAR_MAPPING_INDEX_START = 3;
  private static final int TWO_UCHARS_MAPPING_INDEX_START = 4;
  private static final int THREE_UCHARS_MAPPING_INDEX_START = 5;
  private static final int FOUR_UCHARS_MAPPING_INDEX_START = 6;
  private static final int OPTIONS = 7;
  private static final int INDEX_TOP = 16;
  private static final int DATA_BUFFER_SIZE = 25000;
  private StringPrepTrieImpl sprepTrieImpl;
  private int[] indexes;
  private char[] mappingData;
  private byte[] formatVersion;
  private VersionInfo sprepUniVer;
  private VersionInfo normCorrVer;
  private boolean doNFKC;
  private boolean checkBiDi;
  
  private char getCodePointValue(int paramInt)
  {
    return sprepTrieImpl.sprepTrie.getCodePointValue(paramInt);
  }
  
  private static VersionInfo getVersionInfo(int paramInt)
  {
    int i = paramInt & 0xFF;
    int j = paramInt >> 8 & 0xFF;
    int k = paramInt >> 16 & 0xFF;
    int m = paramInt >> 24 & 0xFF;
    return VersionInfo.getInstance(m, k, j, i);
  }
  
  private static VersionInfo getVersionInfo(byte[] paramArrayOfByte)
  {
    if (paramArrayOfByte.length != 4) {
      return null;
    }
    return VersionInfo.getInstance(paramArrayOfByte[0], paramArrayOfByte[1], paramArrayOfByte[2], paramArrayOfByte[3]);
  }
  
  public StringPrep(InputStream paramInputStream)
    throws IOException
  {
    BufferedInputStream localBufferedInputStream = new BufferedInputStream(paramInputStream, 25000);
    StringPrepDataReader localStringPrepDataReader = new StringPrepDataReader(localBufferedInputStream);
    indexes = localStringPrepDataReader.readIndexes(16);
    byte[] arrayOfByte = new byte[indexes[0]];
    mappingData = new char[indexes[1] / 2];
    localStringPrepDataReader.read(arrayOfByte, mappingData);
    sprepTrieImpl = new StringPrepTrieImpl(null);
    sprepTrieImpl.sprepTrie = new CharTrie(new ByteArrayInputStream(arrayOfByte), sprepTrieImpl);
    formatVersion = localStringPrepDataReader.getDataFormatVersion();
    doNFKC = ((indexes[7] & 0x1) > 0);
    checkBiDi = ((indexes[7] & 0x2) > 0);
    sprepUniVer = getVersionInfo(localStringPrepDataReader.getUnicodeVersion());
    normCorrVer = getVersionInfo(indexes[2]);
    VersionInfo localVersionInfo = NormalizerImpl.getUnicodeVersion();
    if ((localVersionInfo.compareTo(sprepUniVer) < 0) && (localVersionInfo.compareTo(normCorrVer) < 0) && ((indexes[7] & 0x1) > 0)) {
      throw new IOException("Normalization Correction version not supported");
    }
    localBufferedInputStream.close();
  }
  
  private static final void getValues(char paramChar, Values paramValues)
  {
    paramValues.reset();
    if (paramChar == 0)
    {
      type = 4;
    }
    else if (paramChar >= 65520)
    {
      type = (paramChar - 65520);
    }
    else
    {
      type = 1;
      if ((paramChar & 0x2) > 0)
      {
        isIndex = true;
        value = (paramChar >> '\002');
      }
      else
      {
        isIndex = false;
        value = (paramChar << '\020' >> 16);
        value >>= 2;
      }
      if (paramChar >> '\002' == 16319)
      {
        type = 3;
        isIndex = false;
        value = 0;
      }
    }
  }
  
  private StringBuffer map(UCharacterIterator paramUCharacterIterator, int paramInt)
    throws ParseException
  {
    Values localValues = new Values(null);
    char c = '\000';
    int i = -1;
    StringBuffer localStringBuffer = new StringBuffer();
    int j = (paramInt & 0x1) > 0 ? 1 : 0;
    while ((i = paramUCharacterIterator.nextCodePoint()) != -1)
    {
      c = getCodePointValue(i);
      getValues(c, localValues);
      if ((type == 0) && (j == 0)) {
        throw new ParseException("An unassigned code point was found in the input " + paramUCharacterIterator.getText(), paramUCharacterIterator.getIndex());
      }
      if (type == 1)
      {
        if (isIndex)
        {
          int k = value;
          int m;
          if ((k >= indexes[3]) && (k < indexes[4])) {
            m = 1;
          } else if ((k >= indexes[4]) && (k < indexes[5])) {
            m = 2;
          } else if ((k >= indexes[5]) && (k < indexes[6])) {
            m = 3;
          } else {
            m = mappingData[(k++)];
          }
          localStringBuffer.append(mappingData, k, m);
          continue;
        }
        i -= value;
      }
      else
      {
        if (type == 3) {
          continue;
        }
      }
      UTF16.append(localStringBuffer, i);
    }
    return localStringBuffer;
  }
  
  private StringBuffer normalize(StringBuffer paramStringBuffer)
  {
    return new StringBuffer(Normalizer.normalize(paramStringBuffer.toString(), Normalizer.Form.NFKC, 262432));
  }
  
  public StringBuffer prepare(UCharacterIterator paramUCharacterIterator, int paramInt)
    throws ParseException
  {
    StringBuffer localStringBuffer1 = map(paramUCharacterIterator, paramInt);
    StringBuffer localStringBuffer2 = localStringBuffer1;
    if (doNFKC) {
      localStringBuffer2 = normalize(localStringBuffer1);
    }
    UCharacterIterator localUCharacterIterator = UCharacterIterator.getInstance(localStringBuffer2);
    Values localValues = new Values(null);
    int j = 19;
    int k = 19;
    int m = -1;
    int n = -1;
    int i1 = 0;
    int i2 = 0;
    int i;
    while ((i = localUCharacterIterator.nextCodePoint()) != -1)
    {
      char c = getCodePointValue(i);
      getValues(c, localValues);
      if (type == 2) {
        throw new ParseException("A prohibited code point was found in the input" + localUCharacterIterator.getText(), value);
      }
      j = UCharacter.getDirection(i);
      if (k == 19) {
        k = j;
      }
      if (j == 0)
      {
        i2 = 1;
        n = localUCharacterIterator.getIndex() - 1;
      }
      if ((j == 1) || (j == 13))
      {
        i1 = 1;
        m = localUCharacterIterator.getIndex() - 1;
      }
    }
    if (checkBiDi == true)
    {
      if ((i2 == 1) && (i1 == 1)) {
        throw new ParseException("The input does not conform to the rules for BiDi code points." + localUCharacterIterator.getText(), m > n ? m : n);
      }
      if ((i1 == 1) && (((k != 1) && (k != 13)) || ((j != 1) && (j != 13)))) {
        throw new ParseException("The input does not conform to the rules for BiDi code points." + localUCharacterIterator.getText(), m > n ? m : n);
      }
    }
    return localStringBuffer2;
  }
  
  private static final class StringPrepTrieImpl
    implements Trie.DataManipulate
  {
    private CharTrie sprepTrie = null;
    
    private StringPrepTrieImpl() {}
    
    public int getFoldingOffset(int paramInt)
    {
      return paramInt;
    }
  }
  
  private static final class Values
  {
    boolean isIndex;
    int value;
    int type;
    
    private Values() {}
    
    public void reset()
    {
      isIndex = false;
      value = 0;
      type = -1;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\net\idn\StringPrep.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */