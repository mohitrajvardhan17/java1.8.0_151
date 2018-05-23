package sun.text.normalizer;

import java.text.ParsePosition;
import java.util.Iterator;
import java.util.TreeSet;

public class UnicodeSet
  implements UnicodeMatcher
{
  private static final int LOW = 0;
  private static final int HIGH = 1114112;
  public static final int MIN_VALUE = 0;
  public static final int MAX_VALUE = 1114111;
  private int len;
  private int[] list = new int[17];
  private int[] rangeList;
  private int[] buffer;
  TreeSet<String> strings = new TreeSet();
  private String pat = null;
  private static final int START_EXTRA = 16;
  private static final int GROW_EXTRA = 16;
  private static UnicodeSet[] INCLUSIONS = null;
  static final VersionInfo NO_VERSION = VersionInfo.getInstance(0, 0, 0, 0);
  public static final int IGNORE_SPACE = 1;
  
  public UnicodeSet()
  {
    list[(len++)] = 1114112;
  }
  
  public UnicodeSet(int paramInt1, int paramInt2)
  {
    this();
    complement(paramInt1, paramInt2);
  }
  
  public UnicodeSet(String paramString)
  {
    this();
    applyPattern(paramString, null, null, 1);
  }
  
  public UnicodeSet set(UnicodeSet paramUnicodeSet)
  {
    list = ((int[])list.clone());
    len = len;
    pat = pat;
    strings = ((TreeSet)strings.clone());
    return this;
  }
  
  public final UnicodeSet applyPattern(String paramString)
  {
    return applyPattern(paramString, null, null, 1);
  }
  
  private static void _appendToPat(StringBuffer paramStringBuffer, String paramString, boolean paramBoolean)
  {
    int i = 0;
    while (i < paramString.length())
    {
      _appendToPat(paramStringBuffer, UTF16.charAt(paramString, i), paramBoolean);
      i += UTF16.getCharCount(i);
    }
  }
  
  private static void _appendToPat(StringBuffer paramStringBuffer, int paramInt, boolean paramBoolean)
  {
    if ((paramBoolean) && (Utility.isUnprintable(paramInt)) && (Utility.escapeUnprintable(paramStringBuffer, paramInt))) {
      return;
    }
    switch (paramInt)
    {
    case 36: 
    case 38: 
    case 45: 
    case 58: 
    case 91: 
    case 92: 
    case 93: 
    case 94: 
    case 123: 
    case 125: 
      paramStringBuffer.append('\\');
      break;
    default: 
      if (UCharacterProperty.isRuleWhiteSpace(paramInt)) {
        paramStringBuffer.append('\\');
      }
      break;
    }
    UTF16.append(paramStringBuffer, paramInt);
  }
  
  private StringBuffer _toPattern(StringBuffer paramStringBuffer, boolean paramBoolean)
  {
    if (pat != null)
    {
      int j = 0;
      int i = 0;
      while (i < pat.length())
      {
        int k = UTF16.charAt(pat, i);
        i += UTF16.getCharCount(k);
        if ((paramBoolean) && (Utility.isUnprintable(k)))
        {
          if (j % 2 == 1) {
            paramStringBuffer.setLength(paramStringBuffer.length() - 1);
          }
          Utility.escapeUnprintable(paramStringBuffer, k);
          j = 0;
        }
        else
        {
          UTF16.append(paramStringBuffer, k);
          if (k == 92) {
            j++;
          } else {
            j = 0;
          }
        }
      }
      return paramStringBuffer;
    }
    return _generatePattern(paramStringBuffer, paramBoolean, true);
  }
  
  public StringBuffer _generatePattern(StringBuffer paramStringBuffer, boolean paramBoolean1, boolean paramBoolean2)
  {
    paramStringBuffer.append('[');
    int i = getRangeCount();
    int j;
    int k;
    int m;
    if ((i > 1) && (getRangeStart(0) == 0) && (getRangeEnd(i - 1) == 1114111))
    {
      paramStringBuffer.append('^');
      for (j = 1; j < i; j++)
      {
        k = getRangeEnd(j - 1) + 1;
        m = getRangeStart(j) - 1;
        _appendToPat(paramStringBuffer, k, paramBoolean1);
        if (k != m)
        {
          if (k + 1 != m) {
            paramStringBuffer.append('-');
          }
          _appendToPat(paramStringBuffer, m, paramBoolean1);
        }
      }
    }
    else
    {
      for (j = 0; j < i; j++)
      {
        k = getRangeStart(j);
        m = getRangeEnd(j);
        _appendToPat(paramStringBuffer, k, paramBoolean1);
        if (k != m)
        {
          if (k + 1 != m) {
            paramStringBuffer.append('-');
          }
          _appendToPat(paramStringBuffer, m, paramBoolean1);
        }
      }
    }
    if ((paramBoolean2) && (strings.size() > 0))
    {
      Iterator localIterator = strings.iterator();
      while (localIterator.hasNext())
      {
        paramStringBuffer.append('{');
        _appendToPat(paramStringBuffer, (String)localIterator.next(), paramBoolean1);
        paramStringBuffer.append('}');
      }
    }
    return paramStringBuffer.append(']');
  }
  
  private UnicodeSet add_unchecked(int paramInt1, int paramInt2)
  {
    if ((paramInt1 < 0) || (paramInt1 > 1114111)) {
      throw new IllegalArgumentException("Invalid code point U+" + Utility.hex(paramInt1, 6));
    }
    if ((paramInt2 < 0) || (paramInt2 > 1114111)) {
      throw new IllegalArgumentException("Invalid code point U+" + Utility.hex(paramInt2, 6));
    }
    if (paramInt1 < paramInt2) {
      add(range(paramInt1, paramInt2), 2, 0);
    } else if (paramInt1 == paramInt2) {
      add(paramInt1);
    }
    return this;
  }
  
  public final UnicodeSet add(int paramInt)
  {
    return add_unchecked(paramInt);
  }
  
  private final UnicodeSet add_unchecked(int paramInt)
  {
    if ((paramInt < 0) || (paramInt > 1114111)) {
      throw new IllegalArgumentException("Invalid code point U+" + Utility.hex(paramInt, 6));
    }
    int i = findCodePoint(paramInt);
    if ((i & 0x1) != 0) {
      return this;
    }
    if (paramInt == list[i] - 1)
    {
      list[i] = paramInt;
      if (paramInt == 1114111)
      {
        ensureCapacity(len + 1);
        list[(len++)] = 1114112;
      }
      if ((i > 0) && (paramInt == list[(i - 1)]))
      {
        System.arraycopy(list, i + 1, list, i - 1, len - i - 1);
        len -= 2;
      }
    }
    else if ((i > 0) && (paramInt == list[(i - 1)]))
    {
      list[(i - 1)] += 1;
    }
    else
    {
      if (len + 2 > list.length)
      {
        int[] arrayOfInt = new int[len + 2 + 16];
        if (i != 0) {
          System.arraycopy(list, 0, arrayOfInt, 0, i);
        }
        System.arraycopy(list, i, arrayOfInt, i + 2, len - i);
        list = arrayOfInt;
      }
      else
      {
        System.arraycopy(list, i, list, i + 2, len - i);
      }
      list[i] = paramInt;
      list[(i + 1)] = (paramInt + 1);
      len += 2;
    }
    pat = null;
    return this;
  }
  
  public final UnicodeSet add(String paramString)
  {
    int i = getSingleCP(paramString);
    if (i < 0)
    {
      strings.add(paramString);
      pat = null;
    }
    else
    {
      add_unchecked(i, i);
    }
    return this;
  }
  
  private static int getSingleCP(String paramString)
  {
    if (paramString.length() < 1) {
      throw new IllegalArgumentException("Can't use zero-length strings in UnicodeSet");
    }
    if (paramString.length() > 2) {
      return -1;
    }
    if (paramString.length() == 1) {
      return paramString.charAt(0);
    }
    int i = UTF16.charAt(paramString, 0);
    if (i > 65535) {
      return i;
    }
    return -1;
  }
  
  public UnicodeSet complement(int paramInt1, int paramInt2)
  {
    if ((paramInt1 < 0) || (paramInt1 > 1114111)) {
      throw new IllegalArgumentException("Invalid code point U+" + Utility.hex(paramInt1, 6));
    }
    if ((paramInt2 < 0) || (paramInt2 > 1114111)) {
      throw new IllegalArgumentException("Invalid code point U+" + Utility.hex(paramInt2, 6));
    }
    if (paramInt1 <= paramInt2) {
      xor(range(paramInt1, paramInt2), 2, 0);
    }
    pat = null;
    return this;
  }
  
  public UnicodeSet complement()
  {
    if (list[0] == 0)
    {
      System.arraycopy(list, 1, list, 0, len - 1);
      len -= 1;
    }
    else
    {
      ensureCapacity(len + 1);
      System.arraycopy(list, 0, list, 1, len);
      list[0] = 0;
      len += 1;
    }
    pat = null;
    return this;
  }
  
  public boolean contains(int paramInt)
  {
    if ((paramInt < 0) || (paramInt > 1114111)) {
      throw new IllegalArgumentException("Invalid code point U+" + Utility.hex(paramInt, 6));
    }
    int i = findCodePoint(paramInt);
    return (i & 0x1) != 0;
  }
  
  private final int findCodePoint(int paramInt)
  {
    if (paramInt < list[0]) {
      return 0;
    }
    if ((len >= 2) && (paramInt >= list[(len - 2)])) {
      return len - 1;
    }
    int i = 0;
    int j = len - 1;
    for (;;)
    {
      int k = i + j >>> 1;
      if (k == i) {
        return j;
      }
      if (paramInt < list[k]) {
        j = k;
      } else {
        i = k;
      }
    }
  }
  
  public UnicodeSet addAll(UnicodeSet paramUnicodeSet)
  {
    add(list, len, 0);
    strings.addAll(strings);
    return this;
  }
  
  public UnicodeSet retainAll(UnicodeSet paramUnicodeSet)
  {
    retain(list, len, 0);
    strings.retainAll(strings);
    return this;
  }
  
  public UnicodeSet removeAll(UnicodeSet paramUnicodeSet)
  {
    retain(list, len, 2);
    strings.removeAll(strings);
    return this;
  }
  
  public UnicodeSet clear()
  {
    list[0] = 1114112;
    len = 1;
    pat = null;
    strings.clear();
    return this;
  }
  
  public int getRangeCount()
  {
    return len / 2;
  }
  
  public int getRangeStart(int paramInt)
  {
    return list[(paramInt * 2)];
  }
  
  public int getRangeEnd(int paramInt)
  {
    return list[(paramInt * 2 + 1)] - 1;
  }
  
  UnicodeSet applyPattern(String paramString, ParsePosition paramParsePosition, SymbolTable paramSymbolTable, int paramInt)
  {
    int i = paramParsePosition == null ? 1 : 0;
    if (i != 0) {
      paramParsePosition = new ParsePosition(0);
    }
    StringBuffer localStringBuffer = new StringBuffer();
    RuleCharacterIterator localRuleCharacterIterator = new RuleCharacterIterator(paramString, paramSymbolTable, paramParsePosition);
    applyPattern(localRuleCharacterIterator, paramSymbolTable, localStringBuffer, paramInt);
    if (localRuleCharacterIterator.inVariable()) {
      syntaxError(localRuleCharacterIterator, "Extra chars in variable value");
    }
    pat = localStringBuffer.toString();
    if (i != 0)
    {
      int j = paramParsePosition.getIndex();
      if ((paramInt & 0x1) != 0) {
        j = Utility.skipWhitespace(paramString, j);
      }
      if (j != paramString.length()) {
        throw new IllegalArgumentException("Parse of \"" + paramString + "\" failed at " + j);
      }
    }
    return this;
  }
  
  void applyPattern(RuleCharacterIterator paramRuleCharacterIterator, SymbolTable paramSymbolTable, StringBuffer paramStringBuffer, int paramInt)
  {
    int i = 3;
    if ((paramInt & 0x1) != 0) {
      i |= 0x4;
    }
    StringBuffer localStringBuffer1 = new StringBuffer();
    StringBuffer localStringBuffer2 = null;
    int j = 0;
    UnicodeSet localUnicodeSet1 = null;
    Object localObject = null;
    int k = 0;
    int m = 0;
    int n = 0;
    char c = '\000';
    int i1 = 0;
    clear();
    while ((n != 2) && (!paramRuleCharacterIterator.atEnd()))
    {
      int i2 = 0;
      boolean bool = false;
      UnicodeSet localUnicodeSet2 = null;
      int i3 = 0;
      if (resemblesPropertyPattern(paramRuleCharacterIterator, i))
      {
        i3 = 2;
      }
      else
      {
        localObject = paramRuleCharacterIterator.getPos(localObject);
        i2 = paramRuleCharacterIterator.next(i);
        bool = paramRuleCharacterIterator.isEscaped();
        if ((i2 == 91) && (!bool))
        {
          if (n == 1)
          {
            paramRuleCharacterIterator.setPos(localObject);
            i3 = 1;
          }
          else
          {
            n = 1;
            localStringBuffer1.append('[');
            localObject = paramRuleCharacterIterator.getPos(localObject);
            i2 = paramRuleCharacterIterator.next(i);
            bool = paramRuleCharacterIterator.isEscaped();
            if ((i2 == 94) && (!bool))
            {
              i1 = 1;
              localStringBuffer1.append('^');
              localObject = paramRuleCharacterIterator.getPos(localObject);
              i2 = paramRuleCharacterIterator.next(i);
              bool = paramRuleCharacterIterator.isEscaped();
            }
            if (i2 == 45) {
              bool = true;
            } else {
              paramRuleCharacterIterator.setPos(localObject);
            }
          }
        }
        else if (paramSymbolTable != null)
        {
          UnicodeMatcher localUnicodeMatcher = paramSymbolTable.lookupMatcher(i2);
          if (localUnicodeMatcher != null) {
            try
            {
              localUnicodeSet2 = (UnicodeSet)localUnicodeMatcher;
              i3 = 3;
            }
            catch (ClassCastException localClassCastException)
            {
              syntaxError(paramRuleCharacterIterator, "Syntax error");
            }
          }
        }
      }
      if (i3 != 0)
      {
        if (k == 1)
        {
          if (c != 0) {
            syntaxError(paramRuleCharacterIterator, "Char expected after operator");
          }
          add_unchecked(m, m);
          _appendToPat(localStringBuffer1, m, false);
          k = c = 0;
        }
        if ((c == '-') || (c == '&')) {
          localStringBuffer1.append(c);
        }
        if (localUnicodeSet2 == null)
        {
          if (localUnicodeSet1 == null) {
            localUnicodeSet1 = new UnicodeSet();
          }
          localUnicodeSet2 = localUnicodeSet1;
        }
        switch (i3)
        {
        case 1: 
          localUnicodeSet2.applyPattern(paramRuleCharacterIterator, paramSymbolTable, localStringBuffer1, paramInt);
          break;
        case 2: 
          paramRuleCharacterIterator.skipIgnored(i);
          localUnicodeSet2.applyPropertyPattern(paramRuleCharacterIterator, localStringBuffer1, paramSymbolTable);
          break;
        case 3: 
          localUnicodeSet2._toPattern(localStringBuffer1, false);
        }
        j = 1;
        if (n == 0)
        {
          set(localUnicodeSet2);
          n = 2;
          break;
        }
        switch (c)
        {
        case '-': 
          removeAll(localUnicodeSet2);
          break;
        case '&': 
          retainAll(localUnicodeSet2);
          break;
        case '\000': 
          addAll(localUnicodeSet2);
        }
        c = '\000';
        k = 2;
      }
      else
      {
        if (n == 0) {
          syntaxError(paramRuleCharacterIterator, "Missing '['");
        }
        if (!bool) {
          switch (i2)
          {
          case 93: 
            if (k == 1)
            {
              add_unchecked(m, m);
              _appendToPat(localStringBuffer1, m, false);
            }
            if (c == '-')
            {
              add_unchecked(c, c);
              localStringBuffer1.append(c);
            }
            else if (c == '&')
            {
              syntaxError(paramRuleCharacterIterator, "Trailing '&'");
            }
            localStringBuffer1.append(']');
            n = 2;
            break;
          case 45: 
            if (c == 0)
            {
              if (k != 0)
              {
                c = (char)i2;
                continue;
              }
              add_unchecked(i2, i2);
              i2 = paramRuleCharacterIterator.next(i);
              bool = paramRuleCharacterIterator.isEscaped();
              if ((i2 == 93) && (!bool))
              {
                localStringBuffer1.append("-]");
                n = 2;
                continue;
              }
            }
            syntaxError(paramRuleCharacterIterator, "'-' not after char or set");
            break;
          case 38: 
            if ((k == 2) && (c == 0))
            {
              c = (char)i2;
              continue;
            }
            syntaxError(paramRuleCharacterIterator, "'&' not after set");
            break;
          case 94: 
            syntaxError(paramRuleCharacterIterator, "'^' not after '['");
            break;
          case 123: 
            if (c != 0) {
              syntaxError(paramRuleCharacterIterator, "Missing operand after operator");
            }
            if (k == 1)
            {
              add_unchecked(m, m);
              _appendToPat(localStringBuffer1, m, false);
            }
            k = 0;
            if (localStringBuffer2 == null) {
              localStringBuffer2 = new StringBuffer();
            } else {
              localStringBuffer2.setLength(0);
            }
            int i4 = 0;
            while (!paramRuleCharacterIterator.atEnd())
            {
              i2 = paramRuleCharacterIterator.next(i);
              bool = paramRuleCharacterIterator.isEscaped();
              if ((i2 == 125) && (!bool))
              {
                i4 = 1;
                break;
              }
              UTF16.append(localStringBuffer2, i2);
            }
            if ((localStringBuffer2.length() < 1) || (i4 == 0)) {
              syntaxError(paramRuleCharacterIterator, "Invalid multicharacter string");
            }
            add(localStringBuffer2.toString());
            localStringBuffer1.append('{');
            _appendToPat(localStringBuffer1, localStringBuffer2.toString(), false);
            localStringBuffer1.append('}');
            break;
          case 36: 
            localObject = paramRuleCharacterIterator.getPos(localObject);
            i2 = paramRuleCharacterIterator.next(i);
            bool = paramRuleCharacterIterator.isEscaped();
            int i5 = (i2 == 93) && (!bool) ? 1 : 0;
            if ((paramSymbolTable == null) && (i5 == 0))
            {
              i2 = 36;
              paramRuleCharacterIterator.setPos(localObject);
            }
            else
            {
              if ((i5 != 0) && (c == 0))
              {
                if (k == 1)
                {
                  add_unchecked(m, m);
                  _appendToPat(localStringBuffer1, m, false);
                }
                add_unchecked(65535);
                j = 1;
                localStringBuffer1.append('$').append(']');
                n = 2;
                continue;
              }
              syntaxError(paramRuleCharacterIterator, "Unquoted '$'");
            }
            break;
          }
        } else {
          switch (k)
          {
          case 0: 
            k = 1;
            m = i2;
            break;
          case 1: 
            if (c == '-')
            {
              if (m >= i2) {
                syntaxError(paramRuleCharacterIterator, "Invalid range");
              }
              add_unchecked(m, i2);
              _appendToPat(localStringBuffer1, m, false);
              localStringBuffer1.append(c);
              _appendToPat(localStringBuffer1, i2, false);
              k = c = 0;
            }
            else
            {
              add_unchecked(m, m);
              _appendToPat(localStringBuffer1, m, false);
              m = i2;
            }
            break;
          case 2: 
            if (c != 0) {
              syntaxError(paramRuleCharacterIterator, "Set expected after operator");
            }
            m = i2;
            k = 1;
          }
        }
      }
    }
    if (n != 2) {
      syntaxError(paramRuleCharacterIterator, "Missing ']'");
    }
    paramRuleCharacterIterator.skipIgnored(i);
    if (i1 != 0) {
      complement();
    }
    if (j != 0) {
      paramStringBuffer.append(localStringBuffer1.toString());
    } else {
      _generatePattern(paramStringBuffer, false, true);
    }
  }
  
  private static void syntaxError(RuleCharacterIterator paramRuleCharacterIterator, String paramString)
  {
    throw new IllegalArgumentException("Error: " + paramString + " at \"" + Utility.escape(paramRuleCharacterIterator.toString()) + '"');
  }
  
  private void ensureCapacity(int paramInt)
  {
    if (paramInt <= list.length) {
      return;
    }
    int[] arrayOfInt = new int[paramInt + 16];
    System.arraycopy(list, 0, arrayOfInt, 0, len);
    list = arrayOfInt;
  }
  
  private void ensureBufferCapacity(int paramInt)
  {
    if ((buffer != null) && (paramInt <= buffer.length)) {
      return;
    }
    buffer = new int[paramInt + 16];
  }
  
  private int[] range(int paramInt1, int paramInt2)
  {
    if (rangeList == null)
    {
      rangeList = new int[] { paramInt1, paramInt2 + 1, 1114112 };
    }
    else
    {
      rangeList[0] = paramInt1;
      rangeList[1] = (paramInt2 + 1);
    }
    return rangeList;
  }
  
  private UnicodeSet xor(int[] paramArrayOfInt, int paramInt1, int paramInt2)
  {
    ensureBufferCapacity(len + paramInt1);
    int i = 0;
    int j = 0;
    int k = 0;
    int m = list[(i++)];
    int n;
    if ((paramInt2 == 1) || (paramInt2 == 2))
    {
      n = 0;
      if (paramArrayOfInt[j] == 0)
      {
        j++;
        n = paramArrayOfInt[j];
      }
    }
    else
    {
      n = paramArrayOfInt[(j++)];
    }
    for (;;)
    {
      if (m < n)
      {
        buffer[(k++)] = m;
        m = list[(i++)];
      }
      else if (n < m)
      {
        buffer[(k++)] = n;
        n = paramArrayOfInt[(j++)];
      }
      else
      {
        if (m == 1114112) {
          break;
        }
        m = list[(i++)];
        n = paramArrayOfInt[(j++)];
      }
    }
    buffer[(k++)] = 1114112;
    len = k;
    int[] arrayOfInt = list;
    list = buffer;
    buffer = arrayOfInt;
    pat = null;
    return this;
  }
  
  private UnicodeSet add(int[] paramArrayOfInt, int paramInt1, int paramInt2)
  {
    ensureBufferCapacity(len + paramInt1);
    int i = 0;
    int j = 0;
    int k = 0;
    int m = list[(i++)];
    int n = paramArrayOfInt[(j++)];
    for (;;)
    {
      switch (paramInt2)
      {
      case 0: 
        if (m < n)
        {
          if ((k > 0) && (m <= buffer[(k - 1)]))
          {
            m = max(list[i], buffer[(--k)]);
          }
          else
          {
            buffer[(k++)] = m;
            m = list[i];
          }
          i++;
          paramInt2 ^= 0x1;
        }
        else if (n < m)
        {
          if ((k > 0) && (n <= buffer[(k - 1)]))
          {
            n = max(paramArrayOfInt[j], buffer[(--k)]);
          }
          else
          {
            buffer[(k++)] = n;
            n = paramArrayOfInt[j];
          }
          j++;
          paramInt2 ^= 0x2;
        }
        else
        {
          if (m == 1114112) {
            break label620;
          }
          if ((k > 0) && (m <= buffer[(k - 1)]))
          {
            m = max(list[i], buffer[(--k)]);
          }
          else
          {
            buffer[(k++)] = m;
            m = list[i];
          }
          i++;
          paramInt2 ^= 0x1;
          n = paramArrayOfInt[(j++)];
          paramInt2 ^= 0x2;
        }
        break;
      case 3: 
        if (n <= m)
        {
          if (m == 1114112) {
            break label620;
          }
          buffer[(k++)] = m;
        }
        else
        {
          if (n == 1114112) {
            break label620;
          }
          buffer[(k++)] = n;
        }
        m = list[(i++)];
        paramInt2 ^= 0x1;
        n = paramArrayOfInt[(j++)];
        paramInt2 ^= 0x2;
        break;
      case 1: 
        if (m < n)
        {
          buffer[(k++)] = m;
          m = list[(i++)];
          paramInt2 ^= 0x1;
        }
        else if (n < m)
        {
          n = paramArrayOfInt[(j++)];
          paramInt2 ^= 0x2;
        }
        else
        {
          if (m == 1114112) {
            break label620;
          }
          m = list[(i++)];
          paramInt2 ^= 0x1;
          n = paramArrayOfInt[(j++)];
          paramInt2 ^= 0x2;
        }
        break;
      case 2: 
        if (n < m)
        {
          buffer[(k++)] = n;
          n = paramArrayOfInt[(j++)];
          paramInt2 ^= 0x2;
        }
        else if (m < n)
        {
          m = list[(i++)];
          paramInt2 ^= 0x1;
        }
        else
        {
          if (m == 1114112) {
            break label620;
          }
          m = list[(i++)];
          paramInt2 ^= 0x1;
          n = paramArrayOfInt[(j++)];
          paramInt2 ^= 0x2;
        }
        break;
      }
    }
    label620:
    buffer[(k++)] = 1114112;
    len = k;
    int[] arrayOfInt = list;
    list = buffer;
    buffer = arrayOfInt;
    pat = null;
    return this;
  }
  
  private UnicodeSet retain(int[] paramArrayOfInt, int paramInt1, int paramInt2)
  {
    ensureBufferCapacity(len + paramInt1);
    int i = 0;
    int j = 0;
    int k = 0;
    int m = list[(i++)];
    int n = paramArrayOfInt[(j++)];
    for (;;)
    {
      switch (paramInt2)
      {
      case 0: 
        if (m < n)
        {
          m = list[(i++)];
          paramInt2 ^= 0x1;
        }
        else if (n < m)
        {
          n = paramArrayOfInt[(j++)];
          paramInt2 ^= 0x2;
        }
        else
        {
          if (m == 1114112) {
            break label508;
          }
          buffer[(k++)] = m;
          m = list[(i++)];
          paramInt2 ^= 0x1;
          n = paramArrayOfInt[(j++)];
          paramInt2 ^= 0x2;
        }
        break;
      case 3: 
        if (m < n)
        {
          buffer[(k++)] = m;
          m = list[(i++)];
          paramInt2 ^= 0x1;
        }
        else if (n < m)
        {
          buffer[(k++)] = n;
          n = paramArrayOfInt[(j++)];
          paramInt2 ^= 0x2;
        }
        else
        {
          if (m == 1114112) {
            break label508;
          }
          buffer[(k++)] = m;
          m = list[(i++)];
          paramInt2 ^= 0x1;
          n = paramArrayOfInt[(j++)];
          paramInt2 ^= 0x2;
        }
        break;
      case 1: 
        if (m < n)
        {
          m = list[(i++)];
          paramInt2 ^= 0x1;
        }
        else if (n < m)
        {
          buffer[(k++)] = n;
          n = paramArrayOfInt[(j++)];
          paramInt2 ^= 0x2;
        }
        else
        {
          if (m == 1114112) {
            break label508;
          }
          m = list[(i++)];
          paramInt2 ^= 0x1;
          n = paramArrayOfInt[(j++)];
          paramInt2 ^= 0x2;
        }
        break;
      case 2: 
        if (n < m)
        {
          n = paramArrayOfInt[(j++)];
          paramInt2 ^= 0x2;
        }
        else if (m < n)
        {
          buffer[(k++)] = m;
          m = list[(i++)];
          paramInt2 ^= 0x1;
        }
        else
        {
          if (m == 1114112) {
            break label508;
          }
          m = list[(i++)];
          paramInt2 ^= 0x1;
          n = paramArrayOfInt[(j++)];
          paramInt2 ^= 0x2;
        }
        break;
      }
    }
    label508:
    buffer[(k++)] = 1114112;
    len = k;
    int[] arrayOfInt = list;
    list = buffer;
    buffer = arrayOfInt;
    pat = null;
    return this;
  }
  
  private static final int max(int paramInt1, int paramInt2)
  {
    return paramInt1 > paramInt2 ? paramInt1 : paramInt2;
  }
  
  private static synchronized UnicodeSet getInclusions(int paramInt)
  {
    if (INCLUSIONS == null) {
      INCLUSIONS = new UnicodeSet[9];
    }
    if (INCLUSIONS[paramInt] == null)
    {
      UnicodeSet localUnicodeSet = new UnicodeSet();
      switch (paramInt)
      {
      case 2: 
        UCharacterProperty.getInstance().upropsvec_addPropertyStarts(localUnicodeSet);
        break;
      default: 
        throw new IllegalStateException("UnicodeSet.getInclusions(unknown src " + paramInt + ")");
      }
      INCLUSIONS[paramInt] = localUnicodeSet;
    }
    return INCLUSIONS[paramInt];
  }
  
  private UnicodeSet applyFilter(Filter paramFilter, int paramInt)
  {
    clear();
    int i = -1;
    UnicodeSet localUnicodeSet = getInclusions(paramInt);
    int j = localUnicodeSet.getRangeCount();
    for (int k = 0; k < j; k++)
    {
      int m = localUnicodeSet.getRangeStart(k);
      int n = localUnicodeSet.getRangeEnd(k);
      for (int i1 = m; i1 <= n; i1++) {
        if (paramFilter.contains(i1))
        {
          if (i < 0) {
            i = i1;
          }
        }
        else if (i >= 0)
        {
          add_unchecked(i, i1 - 1);
          i = -1;
        }
      }
    }
    if (i >= 0) {
      add_unchecked(i, 1114111);
    }
    return this;
  }
  
  private static String mungeCharName(String paramString)
  {
    StringBuffer localStringBuffer = new StringBuffer();
    int i = 0;
    while (i < paramString.length())
    {
      int j = UTF16.charAt(paramString, i);
      i += UTF16.getCharCount(j);
      if (UCharacterProperty.isRuleWhiteSpace(j))
      {
        if ((localStringBuffer.length() != 0) && (localStringBuffer.charAt(localStringBuffer.length() - 1) != ' ')) {
          j = 32;
        }
      }
      else {
        UTF16.append(localStringBuffer, j);
      }
    }
    if ((localStringBuffer.length() != 0) && (localStringBuffer.charAt(localStringBuffer.length() - 1) == ' ')) {
      localStringBuffer.setLength(localStringBuffer.length() - 1);
    }
    return localStringBuffer.toString();
  }
  
  public UnicodeSet applyPropertyAlias(String paramString1, String paramString2, SymbolTable paramSymbolTable)
  {
    if ((paramString2.length() > 0) && (paramString1.equals("Age")))
    {
      VersionInfo localVersionInfo = VersionInfo.getInstance(mungeCharName(paramString2));
      applyFilter(new VersionFilter(localVersionInfo), 2);
      return this;
    }
    throw new IllegalArgumentException("Unsupported property: " + paramString1);
  }
  
  private static boolean resemblesPropertyPattern(RuleCharacterIterator paramRuleCharacterIterator, int paramInt)
  {
    boolean bool = false;
    paramInt &= 0xFFFFFFFD;
    Object localObject = paramRuleCharacterIterator.getPos(null);
    int i = paramRuleCharacterIterator.next(paramInt);
    if ((i == 91) || (i == 92))
    {
      int j = paramRuleCharacterIterator.next(paramInt & 0xFFFFFFFB);
      bool = j == 58;
    }
    paramRuleCharacterIterator.setPos(localObject);
    return bool;
  }
  
  private UnicodeSet applyPropertyPattern(String paramString, ParsePosition paramParsePosition, SymbolTable paramSymbolTable)
  {
    int i = paramParsePosition.getIndex();
    if (i + 5 > paramString.length()) {
      return null;
    }
    int j = 0;
    int k = 0;
    int m = 0;
    if (paramString.regionMatches(i, "[:", 0, 2))
    {
      j = 1;
      i = Utility.skipWhitespace(paramString, i + 2);
      if ((i < paramString.length()) && (paramString.charAt(i) == '^'))
      {
        i++;
        m = 1;
      }
    }
    else if ((paramString.regionMatches(true, i, "\\p", 0, 2)) || (paramString.regionMatches(i, "\\N", 0, 2)))
    {
      n = paramString.charAt(i + 1);
      m = n == 80 ? 1 : 0;
      k = n == 78 ? 1 : 0;
      i = Utility.skipWhitespace(paramString, i + 2);
      if ((i == paramString.length()) || (paramString.charAt(i++) != '{')) {
        return null;
      }
    }
    else
    {
      return null;
    }
    int n = paramString.indexOf(j != 0 ? ":]" : "}", i);
    if (n < 0) {
      return null;
    }
    int i1 = paramString.indexOf('=', i);
    String str1;
    String str2;
    if ((i1 >= 0) && (i1 < n) && (k == 0))
    {
      str1 = paramString.substring(i, i1);
      str2 = paramString.substring(i1 + 1, n);
    }
    else
    {
      str1 = paramString.substring(i, n);
      str2 = "";
      if (k != 0)
      {
        str2 = str1;
        str1 = "na";
      }
    }
    applyPropertyAlias(str1, str2, paramSymbolTable);
    if (m != 0) {
      complement();
    }
    paramParsePosition.setIndex(n + (j != 0 ? 2 : 1));
    return this;
  }
  
  private void applyPropertyPattern(RuleCharacterIterator paramRuleCharacterIterator, StringBuffer paramStringBuffer, SymbolTable paramSymbolTable)
  {
    String str = paramRuleCharacterIterator.lookahead();
    ParsePosition localParsePosition = new ParsePosition(0);
    applyPropertyPattern(str, localParsePosition, paramSymbolTable);
    if (localParsePosition.getIndex() == 0) {
      syntaxError(paramRuleCharacterIterator, "Invalid property pattern");
    }
    paramRuleCharacterIterator.jumpahead(localParsePosition.getIndex());
    paramStringBuffer.append(str.substring(0, localParsePosition.getIndex()));
  }
  
  private static abstract interface Filter
  {
    public abstract boolean contains(int paramInt);
  }
  
  private static class VersionFilter
    implements UnicodeSet.Filter
  {
    VersionInfo version;
    
    VersionFilter(VersionInfo paramVersionInfo)
    {
      version = paramVersionInfo;
    }
    
    public boolean contains(int paramInt)
    {
      VersionInfo localVersionInfo = UCharacter.getAge(paramInt);
      return (localVersionInfo != UnicodeSet.NO_VERSION) && (localVersionInfo.compareTo(version) <= 0);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\text\normalizer\UnicodeSet.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */