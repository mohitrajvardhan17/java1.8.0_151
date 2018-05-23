package sun.util.locale.provider;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.text.BreakIterator;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.MissingResourceException;
import sun.text.CompactByteArray;
import sun.text.SupplementaryCharacterData;

class RuleBasedBreakIterator
  extends BreakIterator
{
  protected static final byte IGNORE = -1;
  private static final short START_STATE = 1;
  private static final short STOP_STATE = 0;
  static final byte[] LABEL = { 66, 73, 100, 97, 116, 97, 0 };
  static final int LABEL_LENGTH = LABEL.length;
  static final byte supportedVersion = 1;
  private static final int HEADER_LENGTH = 36;
  private static final int BMP_INDICES_LENGTH = 512;
  private CompactByteArray charCategoryTable = null;
  private SupplementaryCharacterData supplementaryCharCategoryTable = null;
  private short[] stateTable = null;
  private short[] backwardsStateTable = null;
  private boolean[] endStates = null;
  private boolean[] lookaheadStates = null;
  private byte[] additionalData = null;
  private int numCategories;
  private CharacterIterator text = null;
  private long checksum;
  private int cachedLastKnownBreak = -1;
  
  RuleBasedBreakIterator(String paramString)
    throws IOException, MissingResourceException
  {
    readTables(paramString);
  }
  
  protected final void readTables(String paramString)
    throws IOException, MissingResourceException
  {
    byte[] arrayOfByte1 = readFile(paramString);
    int i = getInt(arrayOfByte1, 0);
    int j = getInt(arrayOfByte1, 4);
    int k = getInt(arrayOfByte1, 8);
    int m = getInt(arrayOfByte1, 12);
    int n = getInt(arrayOfByte1, 16);
    int i1 = getInt(arrayOfByte1, 20);
    int i2 = getInt(arrayOfByte1, 24);
    checksum = getLong(arrayOfByte1, 28);
    stateTable = new short[i];
    int i3 = 36;
    int i4 = 0;
    while (i4 < i)
    {
      stateTable[i4] = getShort(arrayOfByte1, i3);
      i4++;
      i3 += 2;
    }
    backwardsStateTable = new short[j];
    i4 = 0;
    while (i4 < j)
    {
      backwardsStateTable[i4] = getShort(arrayOfByte1, i3);
      i4++;
      i3 += 2;
    }
    endStates = new boolean[k];
    i4 = 0;
    while (i4 < k)
    {
      endStates[i4] = (arrayOfByte1[i3] == 1 ? 1 : false);
      i4++;
      i3++;
    }
    lookaheadStates = new boolean[m];
    i4 = 0;
    while (i4 < m)
    {
      lookaheadStates[i4] = (arrayOfByte1[i3] == 1 ? 1 : false);
      i4++;
      i3++;
    }
    short[] arrayOfShort = new short['Ȁ'];
    int i5 = 0;
    while (i5 < 512)
    {
      arrayOfShort[i5] = getShort(arrayOfByte1, i3);
      i5++;
      i3 += 2;
    }
    byte[] arrayOfByte2 = new byte[n];
    System.arraycopy(arrayOfByte1, i3, arrayOfByte2, 0, n);
    i3 += n;
    charCategoryTable = new CompactByteArray(arrayOfShort, arrayOfByte2);
    int[] arrayOfInt = new int[i1];
    int i6 = 0;
    while (i6 < i1)
    {
      arrayOfInt[i6] = getInt(arrayOfByte1, i3);
      i6++;
      i3 += 4;
    }
    supplementaryCharCategoryTable = new SupplementaryCharacterData(arrayOfInt);
    if (i2 > 0)
    {
      additionalData = new byte[i2];
      System.arraycopy(arrayOfByte1, i3, additionalData, 0, i2);
    }
    numCategories = (stateTable.length / endStates.length);
  }
  
  protected byte[] readFile(final String paramString)
    throws IOException, MissingResourceException
  {
    BufferedInputStream localBufferedInputStream;
    try
    {
      localBufferedInputStream = (BufferedInputStream)AccessController.doPrivileged(new PrivilegedExceptionAction()
      {
        public BufferedInputStream run()
          throws Exception
        {
          return new BufferedInputStream(getClass().getResourceAsStream("/sun/text/resources/" + paramString));
        }
      });
    }
    catch (PrivilegedActionException localPrivilegedActionException)
    {
      throw new InternalError(localPrivilegedActionException.toString(), localPrivilegedActionException);
    }
    int i = 0;
    int j = LABEL_LENGTH + 5;
    byte[] arrayOfByte = new byte[j];
    if (localBufferedInputStream.read(arrayOfByte) != j) {
      throw new MissingResourceException("Wrong header length", paramString, "");
    }
    int k = 0;
    while (k < LABEL_LENGTH)
    {
      if (arrayOfByte[i] != LABEL[i]) {
        throw new MissingResourceException("Wrong magic number", paramString, "");
      }
      k++;
      i++;
    }
    if (arrayOfByte[i] != 1) {
      throw new MissingResourceException("Unsupported version(" + arrayOfByte[i] + ")", paramString, "");
    }
    j = getInt(arrayOfByte, ++i);
    arrayOfByte = new byte[j];
    if (localBufferedInputStream.read(arrayOfByte) != j) {
      throw new MissingResourceException("Wrong data length", paramString, "");
    }
    localBufferedInputStream.close();
    return arrayOfByte;
  }
  
  byte[] getAdditionalData()
  {
    return additionalData;
  }
  
  void setAdditionalData(byte[] paramArrayOfByte)
  {
    additionalData = paramArrayOfByte;
  }
  
  public Object clone()
  {
    RuleBasedBreakIterator localRuleBasedBreakIterator = (RuleBasedBreakIterator)super.clone();
    if (text != null) {
      text = ((CharacterIterator)text.clone());
    }
    return localRuleBasedBreakIterator;
  }
  
  public boolean equals(Object paramObject)
  {
    try
    {
      if (paramObject == null) {
        return false;
      }
      RuleBasedBreakIterator localRuleBasedBreakIterator = (RuleBasedBreakIterator)paramObject;
      if (checksum != checksum) {
        return false;
      }
      if (text == null) {
        return text == null;
      }
      return text.equals(text);
    }
    catch (ClassCastException localClassCastException) {}
    return false;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append('[');
    localStringBuilder.append("checksum=0x");
    localStringBuilder.append(Long.toHexString(checksum));
    localStringBuilder.append(']');
    return localStringBuilder.toString();
  }
  
  public int hashCode()
  {
    return (int)checksum;
  }
  
  public int first()
  {
    CharacterIterator localCharacterIterator = getText();
    localCharacterIterator.first();
    return localCharacterIterator.getIndex();
  }
  
  public int last()
  {
    CharacterIterator localCharacterIterator = getText();
    localCharacterIterator.setIndex(localCharacterIterator.getEndIndex());
    return localCharacterIterator.getIndex();
  }
  
  public int next(int paramInt)
  {
    int i = current();
    while (paramInt > 0)
    {
      i = handleNext();
      paramInt--;
    }
    while (paramInt < 0)
    {
      i = previous();
      paramInt++;
    }
    return i;
  }
  
  public int next()
  {
    return handleNext();
  }
  
  public int previous()
  {
    CharacterIterator localCharacterIterator = getText();
    if (current() == localCharacterIterator.getBeginIndex()) {
      return -1;
    }
    int i = current();
    int j = cachedLastKnownBreak;
    if ((j >= i) || (j <= -1))
    {
      getPrevious();
      j = handlePrevious();
    }
    else
    {
      localCharacterIterator.setIndex(j);
    }
    for (int k = j; (k != -1) && (k < i); k = handleNext()) {
      j = k;
    }
    localCharacterIterator.setIndex(j);
    cachedLastKnownBreak = j;
    return j;
  }
  
  private int getPrevious()
  {
    char c1 = text.previous();
    if ((Character.isLowSurrogate(c1)) && (text.getIndex() > text.getBeginIndex()))
    {
      char c2 = text.previous();
      if (Character.isHighSurrogate(c2)) {
        return Character.toCodePoint(c2, c1);
      }
      text.next();
    }
    return c1;
  }
  
  int getCurrent()
  {
    char c1 = text.current();
    if ((Character.isHighSurrogate(c1)) && (text.getIndex() < text.getEndIndex()))
    {
      char c2 = text.next();
      text.previous();
      if (Character.isLowSurrogate(c2)) {
        return Character.toCodePoint(c1, c2);
      }
    }
    return c1;
  }
  
  private int getCurrentCodePointCount()
  {
    char c1 = text.current();
    if ((Character.isHighSurrogate(c1)) && (text.getIndex() < text.getEndIndex()))
    {
      char c2 = text.next();
      text.previous();
      if (Character.isLowSurrogate(c2)) {
        return 2;
      }
    }
    return 1;
  }
  
  int getNext()
  {
    int i = text.getIndex();
    int j = text.getEndIndex();
    if ((i == j) || (i += getCurrentCodePointCount() >= j)) {
      return 65535;
    }
    text.setIndex(i);
    return getCurrent();
  }
  
  private int getNextIndex()
  {
    int i = text.getIndex() + getCurrentCodePointCount();
    int j = text.getEndIndex();
    if (i > j) {
      return j;
    }
    return i;
  }
  
  protected static final void checkOffset(int paramInt, CharacterIterator paramCharacterIterator)
  {
    if ((paramInt < paramCharacterIterator.getBeginIndex()) || (paramInt > paramCharacterIterator.getEndIndex())) {
      throw new IllegalArgumentException("offset out of bounds");
    }
  }
  
  public int following(int paramInt)
  {
    CharacterIterator localCharacterIterator = getText();
    checkOffset(paramInt, localCharacterIterator);
    localCharacterIterator.setIndex(paramInt);
    if (paramInt == localCharacterIterator.getBeginIndex())
    {
      cachedLastKnownBreak = handleNext();
      return cachedLastKnownBreak;
    }
    int i = cachedLastKnownBreak;
    if ((i >= paramInt) || (i <= -1)) {
      i = handlePrevious();
    } else {
      localCharacterIterator.setIndex(i);
    }
    while ((i != -1) && (i <= paramInt)) {
      i = handleNext();
    }
    cachedLastKnownBreak = i;
    return i;
  }
  
  public int preceding(int paramInt)
  {
    CharacterIterator localCharacterIterator = getText();
    checkOffset(paramInt, localCharacterIterator);
    localCharacterIterator.setIndex(paramInt);
    return previous();
  }
  
  public boolean isBoundary(int paramInt)
  {
    CharacterIterator localCharacterIterator = getText();
    checkOffset(paramInt, localCharacterIterator);
    if (paramInt == localCharacterIterator.getBeginIndex()) {
      return true;
    }
    return following(paramInt - 1) == paramInt;
  }
  
  public int current()
  {
    return getText().getIndex();
  }
  
  public CharacterIterator getText()
  {
    if (text == null) {
      text = new StringCharacterIterator("");
    }
    return text;
  }
  
  public void setText(CharacterIterator paramCharacterIterator)
  {
    int i = paramCharacterIterator.getEndIndex();
    int j;
    try
    {
      paramCharacterIterator.setIndex(i);
      j = paramCharacterIterator.getIndex() == i ? 1 : 0;
    }
    catch (IllegalArgumentException localIllegalArgumentException)
    {
      j = 0;
    }
    if (j != 0) {
      text = paramCharacterIterator;
    } else {
      text = new SafeCharIterator(paramCharacterIterator);
    }
    text.first();
    cachedLastKnownBreak = -1;
  }
  
  protected int handleNext()
  {
    CharacterIterator localCharacterIterator = getText();
    if (localCharacterIterator.getIndex() == localCharacterIterator.getEndIndex()) {
      return -1;
    }
    int i = getNextIndex();
    int j = 0;
    int k = 1;
    for (int n = getCurrent(); (n != 65535) && (k != 0); n = getNext())
    {
      int m = lookupCategory(n);
      if (m != -1) {
        k = lookupState(k, m);
      }
      if (lookaheadStates[k] != 0)
      {
        if (endStates[k] != 0) {
          i = j;
        } else {
          j = getNextIndex();
        }
      }
      else if (endStates[k] != 0) {
        i = getNextIndex();
      }
    }
    if ((n == 65535) && (j == localCharacterIterator.getEndIndex())) {
      i = j;
    }
    localCharacterIterator.setIndex(i);
    return i;
  }
  
  protected int handlePrevious()
  {
    CharacterIterator localCharacterIterator = getText();
    int i = 1;
    int j = 0;
    int k = 0;
    for (int m = getCurrent(); (m != 65535) && (i != 0); m = getPrevious())
    {
      k = j;
      j = lookupCategory(m);
      if (j != -1) {
        i = lookupBackwardState(i, j);
      }
    }
    if (m != 65535) {
      if (k != -1)
      {
        getNext();
        getNext();
      }
      else
      {
        getNext();
      }
    }
    return localCharacterIterator.getIndex();
  }
  
  protected int lookupCategory(int paramInt)
  {
    if (paramInt < 65536) {
      return charCategoryTable.elementAt((char)paramInt);
    }
    return supplementaryCharCategoryTable.getValue(paramInt);
  }
  
  protected int lookupState(int paramInt1, int paramInt2)
  {
    return stateTable[(paramInt1 * numCategories + paramInt2)];
  }
  
  protected int lookupBackwardState(int paramInt1, int paramInt2)
  {
    return backwardsStateTable[(paramInt1 * numCategories + paramInt2)];
  }
  
  static long getLong(byte[] paramArrayOfByte, int paramInt)
  {
    long l = paramArrayOfByte[paramInt] & 0xFF;
    for (int i = 1; i < 8; i++) {
      l = l << 8 | paramArrayOfByte[(paramInt + i)] & 0xFF;
    }
    return l;
  }
  
  static int getInt(byte[] paramArrayOfByte, int paramInt)
  {
    int i = paramArrayOfByte[paramInt] & 0xFF;
    for (int j = 1; j < 4; j++) {
      i = i << 8 | paramArrayOfByte[(paramInt + j)] & 0xFF;
    }
    return i;
  }
  
  static short getShort(byte[] paramArrayOfByte, int paramInt)
  {
    short s = (short)(paramArrayOfByte[paramInt] & 0xFF);
    s = (short)(s << 8 | paramArrayOfByte[(paramInt + 1)] & 0xFF);
    return s;
  }
  
  private static final class SafeCharIterator
    implements CharacterIterator, Cloneable
  {
    private CharacterIterator base;
    private int rangeStart;
    private int rangeLimit;
    private int currentIndex;
    
    SafeCharIterator(CharacterIterator paramCharacterIterator)
    {
      base = paramCharacterIterator;
      rangeStart = paramCharacterIterator.getBeginIndex();
      rangeLimit = paramCharacterIterator.getEndIndex();
      currentIndex = paramCharacterIterator.getIndex();
    }
    
    public char first()
    {
      return setIndex(rangeStart);
    }
    
    public char last()
    {
      return setIndex(rangeLimit - 1);
    }
    
    public char current()
    {
      if ((currentIndex < rangeStart) || (currentIndex >= rangeLimit)) {
        return 65535;
      }
      return base.setIndex(currentIndex);
    }
    
    public char next()
    {
      currentIndex += 1;
      if (currentIndex >= rangeLimit)
      {
        currentIndex = rangeLimit;
        return 65535;
      }
      return base.setIndex(currentIndex);
    }
    
    public char previous()
    {
      currentIndex -= 1;
      if (currentIndex < rangeStart)
      {
        currentIndex = rangeStart;
        return 65535;
      }
      return base.setIndex(currentIndex);
    }
    
    public char setIndex(int paramInt)
    {
      if ((paramInt < rangeStart) || (paramInt > rangeLimit)) {
        throw new IllegalArgumentException("Invalid position");
      }
      currentIndex = paramInt;
      return current();
    }
    
    public int getBeginIndex()
    {
      return rangeStart;
    }
    
    public int getEndIndex()
    {
      return rangeLimit;
    }
    
    public int getIndex()
    {
      return currentIndex;
    }
    
    public Object clone()
    {
      SafeCharIterator localSafeCharIterator = null;
      try
      {
        localSafeCharIterator = (SafeCharIterator)super.clone();
      }
      catch (CloneNotSupportedException localCloneNotSupportedException)
      {
        throw new Error("Clone not supported: " + localCloneNotSupportedException);
      }
      CharacterIterator localCharacterIterator = (CharacterIterator)base.clone();
      base = localCharacterIterator;
      return localSafeCharIterator;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\util\locale\provider\RuleBasedBreakIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */