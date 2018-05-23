package com.sun.jndi.ldap;

import java.io.IOException;
import java.io.PrintStream;
import javax.naming.NamingException;
import javax.naming.directory.InvalidSearchFilterException;

final class Filter
{
  private static final boolean dbg = false;
  private static int dbgIndent = 0;
  static final int LDAP_FILTER_AND = 160;
  static final int LDAP_FILTER_OR = 161;
  static final int LDAP_FILTER_NOT = 162;
  static final int LDAP_FILTER_EQUALITY = 163;
  static final int LDAP_FILTER_SUBSTRINGS = 164;
  static final int LDAP_FILTER_GE = 165;
  static final int LDAP_FILTER_LE = 166;
  static final int LDAP_FILTER_PRESENT = 135;
  static final int LDAP_FILTER_APPROX = 168;
  static final int LDAP_FILTER_EXT = 169;
  static final int LDAP_FILTER_EXT_RULE = 129;
  static final int LDAP_FILTER_EXT_TYPE = 130;
  static final int LDAP_FILTER_EXT_VAL = 131;
  static final int LDAP_FILTER_EXT_DN = 132;
  static final int LDAP_SUBSTRING_INITIAL = 128;
  static final int LDAP_SUBSTRING_ANY = 129;
  static final int LDAP_SUBSTRING_FINAL = 130;
  
  Filter() {}
  
  static void encodeFilterString(BerEncoder paramBerEncoder, String paramString, boolean paramBoolean)
    throws IOException, NamingException
  {
    if ((paramString == null) || (paramString.equals(""))) {
      throw new InvalidSearchFilterException("Empty filter");
    }
    byte[] arrayOfByte;
    if (paramBoolean) {
      arrayOfByte = paramString.getBytes("UTF8");
    } else {
      arrayOfByte = paramString.getBytes("8859_1");
    }
    int i = arrayOfByte.length;
    encodeFilter(paramBerEncoder, arrayOfByte, 0, i);
  }
  
  private static void encodeFilter(BerEncoder paramBerEncoder, byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException, NamingException
  {
    if (paramInt2 - paramInt1 <= 0) {
      throw new InvalidSearchFilterException("Empty filter");
    }
    int j = 0;
    int[] arrayOfInt = new int[1];
    arrayOfInt[0] = paramInt1;
    while (arrayOfInt[0] < paramInt2)
    {
      switch (paramArrayOfByte[arrayOfInt[0]])
      {
      case 40: 
        arrayOfInt[0] += 1;
        j++;
        switch (paramArrayOfByte[arrayOfInt[0]])
        {
        case 38: 
          encodeComplexFilter(paramBerEncoder, paramArrayOfByte, 160, arrayOfInt, paramInt2);
          j--;
          break;
        case 124: 
          encodeComplexFilter(paramBerEncoder, paramArrayOfByte, 161, arrayOfInt, paramInt2);
          j--;
          break;
        case 33: 
          encodeComplexFilter(paramBerEncoder, paramArrayOfByte, 162, arrayOfInt, paramInt2);
          j--;
          break;
        default: 
          int k = 1;
          int m = 0;
          int i = arrayOfInt[0];
          while ((i < paramInt2) && (k > 0))
          {
            if (m == 0) {
              if (paramArrayOfByte[i] == 40) {
                k++;
              } else if (paramArrayOfByte[i] == 41) {
                k--;
              }
            }
            if ((paramArrayOfByte[i] == 92) && (m == 0)) {
              m = 1;
            } else {
              m = 0;
            }
            if (k > 0) {
              i++;
            }
          }
          if (k != 0) {
            throw new InvalidSearchFilterException("Unbalanced parenthesis");
          }
          encodeSimpleFilter(paramBerEncoder, paramArrayOfByte, arrayOfInt[0], i);
          arrayOfInt[0] = (i + 1);
          j--;
        }
        break;
      case 41: 
        paramBerEncoder.endSeq();
        arrayOfInt[0] += 1;
        j--;
        break;
      case 32: 
        arrayOfInt[0] += 1;
        break;
      default: 
        encodeSimpleFilter(paramBerEncoder, paramArrayOfByte, arrayOfInt[0], paramInt2);
        arrayOfInt[0] = paramInt2;
      }
      if (j < 0) {
        throw new InvalidSearchFilterException("Unbalanced parenthesis");
      }
    }
    if (j != 0) {
      throw new InvalidSearchFilterException("Unbalanced parenthesis");
    }
  }
  
  private static int hexchar2int(byte paramByte)
  {
    if ((paramByte >= 48) && (paramByte <= 57)) {
      return paramByte - 48;
    }
    if ((paramByte >= 65) && (paramByte <= 70)) {
      return paramByte - 65 + 10;
    }
    if ((paramByte >= 97) && (paramByte <= 102)) {
      return paramByte - 97 + 10;
    }
    return -1;
  }
  
  static byte[] unescapeFilterValue(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws NamingException
  {
    int i = 0;
    int j = 0;
    int m = paramInt2 - paramInt1;
    byte[] arrayOfByte1 = new byte[m];
    int n = 0;
    for (int i1 = paramInt1; i1 < paramInt2; i1++)
    {
      byte b = paramArrayOfByte[i1];
      if (i != 0)
      {
        int k;
        if ((k = hexchar2int(b)) < 0)
        {
          if (j != 0)
          {
            i = 0;
            arrayOfByte1[(n++)] = b;
          }
          else
          {
            throw new InvalidSearchFilterException("invalid escape sequence: " + paramArrayOfByte);
          }
        }
        else if (j != 0)
        {
          arrayOfByte1[n] = ((byte)(k << 4));
          j = 0;
        }
        else
        {
          int tmp124_121 = (n++);
          byte[] tmp124_117 = arrayOfByte1;
          tmp124_117[tmp124_121] = ((byte)(tmp124_117[tmp124_121] | (byte)k));
          i = 0;
        }
      }
      else if (b != 92)
      {
        arrayOfByte1[(n++)] = b;
        i = 0;
      }
      else
      {
        j = i = 1;
      }
    }
    byte[] arrayOfByte2 = new byte[n];
    System.arraycopy(arrayOfByte1, 0, arrayOfByte2, 0, n);
    return arrayOfByte2;
  }
  
  private static int indexOf(byte[] paramArrayOfByte, char paramChar, int paramInt1, int paramInt2)
  {
    for (int i = paramInt1; i < paramInt2; i++) {
      if (paramArrayOfByte[i] == paramChar) {
        return i;
      }
    }
    return -1;
  }
  
  private static int indexOf(byte[] paramArrayOfByte, String paramString, int paramInt1, int paramInt2)
  {
    int i = indexOf(paramArrayOfByte, paramString.charAt(0), paramInt1, paramInt2);
    if (i >= 0) {
      for (int j = 1; j < paramString.length(); j++) {
        if (paramArrayOfByte[(i + j)] != paramString.charAt(j)) {
          return -1;
        }
      }
    }
    return i;
  }
  
  private static int findUnescaped(byte[] paramArrayOfByte, char paramChar, int paramInt1, int paramInt2)
  {
    while (paramInt1 < paramInt2)
    {
      int i = indexOf(paramArrayOfByte, paramChar, paramInt1, paramInt2);
      int k = 0;
      int j = i - 1;
      while ((j >= paramInt1) && (paramArrayOfByte[j] == 92))
      {
        j--;
        k++;
      }
      if ((i == paramInt1) || (i == -1) || (k % 2 == 0)) {
        return i;
      }
      paramInt1 = i + 1;
    }
    return -1;
  }
  
  private static void encodeSimpleFilter(BerEncoder paramBerEncoder, byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException, NamingException
  {
    int n;
    if ((n = indexOf(paramArrayOfByte, '=', paramInt1, paramInt2)) == -1) {
      throw new InvalidSearchFilterException("Missing 'equals'");
    }
    int i = n + 1;
    int j = paramInt2;
    int k = paramInt1;
    int i1;
    int m;
    switch (paramArrayOfByte[(n - 1)])
    {
    case 60: 
      i1 = 166;
      m = n - 1;
      break;
    case 62: 
      i1 = 165;
      m = n - 1;
      break;
    case 126: 
      i1 = 168;
      m = n - 1;
      break;
    case 58: 
      i1 = 169;
      m = n - 1;
      break;
    default: 
      m = n;
      i1 = 0;
    }
    int i2 = -1;
    int i3 = -1;
    int i4;
    int i5;
    if (((paramArrayOfByte[k] >= 48) && (paramArrayOfByte[k] <= 57)) || ((paramArrayOfByte[k] >= 65) && (paramArrayOfByte[k] <= 90)) || ((paramArrayOfByte[k] >= 97) && (paramArrayOfByte[k] <= 122)))
    {
      i4 = (paramArrayOfByte[k] >= 48) && (paramArrayOfByte[k] <= 57) ? 1 : 0;
      for (i5 = k + 1; i5 < m; i5++)
      {
        if (paramArrayOfByte[i5] == 59)
        {
          if ((i4 != 0) && (paramArrayOfByte[(i5 - 1)] == 46)) {
            throw new InvalidSearchFilterException("invalid attribute description");
          }
          i2 = i5;
          break;
        }
        if ((paramArrayOfByte[i5] == 58) && (i1 == 169))
        {
          if ((i4 != 0) && (paramArrayOfByte[(i5 - 1)] == 46)) {
            throw new InvalidSearchFilterException("invalid attribute description");
          }
          i3 = i5;
          break;
        }
        if (i4 != 0)
        {
          if (((paramArrayOfByte[i5] == 46) && (paramArrayOfByte[(i5 - 1)] == 46)) || ((paramArrayOfByte[i5] != 46) && ((paramArrayOfByte[i5] < 48) || (paramArrayOfByte[i5] > 57)))) {
            throw new InvalidSearchFilterException("invalid attribute description");
          }
        }
        else if ((paramArrayOfByte[i5] != 45) && (paramArrayOfByte[i5] != 95) && ((paramArrayOfByte[i5] < 48) || (paramArrayOfByte[i5] > 57)) && ((paramArrayOfByte[i5] < 65) || (paramArrayOfByte[i5] > 90)) && ((paramArrayOfByte[i5] < 97) || (paramArrayOfByte[i5] > 122))) {
          throw new InvalidSearchFilterException("invalid attribute description");
        }
      }
    }
    else if ((i1 == 169) && (paramArrayOfByte[k] == 58))
    {
      i3 = k;
    }
    else
    {
      throw new InvalidSearchFilterException("invalid attribute description");
    }
    if (i2 > 0) {
      for (i4 = i2 + 1; i4 < m; i4++) {
        if (paramArrayOfByte[i4] == 59)
        {
          if (paramArrayOfByte[(i4 - 1)] == 59) {
            throw new InvalidSearchFilterException("invalid attribute description");
          }
        }
        else
        {
          if ((paramArrayOfByte[i4] == 58) && (i1 == 169))
          {
            if (paramArrayOfByte[(i4 - 1)] == 59) {
              throw new InvalidSearchFilterException("invalid attribute description");
            }
            i3 = i4;
            break;
          }
          if ((paramArrayOfByte[i4] != 45) && (paramArrayOfByte[i4] != 95) && ((paramArrayOfByte[i4] < 48) || (paramArrayOfByte[i4] > 57)) && ((paramArrayOfByte[i4] < 65) || (paramArrayOfByte[i4] > 90)) && ((paramArrayOfByte[i4] < 97) || (paramArrayOfByte[i4] > 122))) {
            throw new InvalidSearchFilterException("invalid attribute description");
          }
        }
      }
    }
    if (i3 > 0)
    {
      i4 = 0;
      for (i5 = i3 + 1; i5 < m; i5++)
      {
        if (paramArrayOfByte[i5] == 58) {
          throw new InvalidSearchFilterException("invalid attribute description");
        }
        if (((paramArrayOfByte[i5] >= 48) && (paramArrayOfByte[i5] <= 57)) || ((paramArrayOfByte[i5] >= 65) && (paramArrayOfByte[i5] <= 90)) || ((paramArrayOfByte[i5] >= 97) && (paramArrayOfByte[i5] <= 122)))
        {
          int i6 = (paramArrayOfByte[i5] >= 48) && (paramArrayOfByte[i5] <= 57) ? 1 : 0;
          i5++;
          int i7 = i5;
          while (i7 < m)
          {
            if (paramArrayOfByte[i7] == 58)
            {
              if (i4 != 0) {
                throw new InvalidSearchFilterException("invalid attribute description");
              }
              if ((i6 != 0) && (paramArrayOfByte[(i7 - 1)] == 46)) {
                throw new InvalidSearchFilterException("invalid attribute description");
              }
              i4 = 1;
              break;
            }
            if (i6 != 0)
            {
              if (((paramArrayOfByte[i7] == 46) && (paramArrayOfByte[(i7 - 1)] == 46)) || ((paramArrayOfByte[i7] != 46) && ((paramArrayOfByte[i7] < 48) || (paramArrayOfByte[i7] > 57)))) {
                throw new InvalidSearchFilterException("invalid attribute description");
              }
            }
            else if ((paramArrayOfByte[i7] != 45) && (paramArrayOfByte[i7] != 95) && ((paramArrayOfByte[i7] < 48) || (paramArrayOfByte[i7] > 57)) && ((paramArrayOfByte[i7] < 65) || (paramArrayOfByte[i7] > 90)) && ((paramArrayOfByte[i7] < 97) || (paramArrayOfByte[i7] > 122))) {
              throw new InvalidSearchFilterException("invalid attribute description");
            }
            i7++;
            i5++;
          }
        }
        else
        {
          throw new InvalidSearchFilterException("invalid attribute description");
        }
      }
    }
    if ((paramArrayOfByte[(m - 1)] == 46) || (paramArrayOfByte[(m - 1)] == 59) || (paramArrayOfByte[(m - 1)] == 58)) {
      throw new InvalidSearchFilterException("invalid attribute description");
    }
    if (m == n) {
      if (findUnescaped(paramArrayOfByte, '*', i, j) == -1)
      {
        i1 = 163;
      }
      else if ((paramArrayOfByte[i] == 42) && (i == j - 1))
      {
        i1 = 135;
      }
      else
      {
        encodeSubstringFilter(paramBerEncoder, paramArrayOfByte, k, m, i, j);
        return;
      }
    }
    if (i1 == 135)
    {
      paramBerEncoder.encodeOctetString(paramArrayOfByte, i1, k, m - k);
    }
    else if (i1 == 169)
    {
      encodeExtensibleMatch(paramBerEncoder, paramArrayOfByte, k, m, i, j);
    }
    else
    {
      paramBerEncoder.beginSeq(i1);
      paramBerEncoder.encodeOctetString(paramArrayOfByte, 4, k, m - k);
      paramBerEncoder.encodeOctetString(unescapeFilterValue(paramArrayOfByte, i, j), 4);
      paramBerEncoder.endSeq();
    }
  }
  
  private static void encodeSubstringFilter(BerEncoder paramBerEncoder, byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    throws IOException, NamingException
  {
    paramBerEncoder.beginSeq(164);
    paramBerEncoder.encodeOctetString(paramArrayOfByte, 4, paramInt1, paramInt2 - paramInt1);
    paramBerEncoder.beginSeq(48);
    int i;
    for (int j = paramInt3; (i = findUnescaped(paramArrayOfByte, '*', j, paramInt4)) != -1; j = i + 1) {
      if (j == paramInt3)
      {
        if (j < i) {
          paramBerEncoder.encodeOctetString(unescapeFilterValue(paramArrayOfByte, j, i), 128);
        }
      }
      else if (j < i) {
        paramBerEncoder.encodeOctetString(unescapeFilterValue(paramArrayOfByte, j, i), 129);
      }
    }
    if (j < paramInt4) {
      paramBerEncoder.encodeOctetString(unescapeFilterValue(paramArrayOfByte, j, paramInt4), 130);
    }
    paramBerEncoder.endSeq();
    paramBerEncoder.endSeq();
  }
  
  private static void encodeComplexFilter(BerEncoder paramBerEncoder, byte[] paramArrayOfByte, int paramInt1, int[] paramArrayOfInt, int paramInt2)
    throws IOException, NamingException
  {
    paramArrayOfInt[0] += 1;
    paramBerEncoder.beginSeq(paramInt1);
    int[] arrayOfInt = findRightParen(paramArrayOfByte, paramArrayOfInt, paramInt2);
    encodeFilterList(paramBerEncoder, paramArrayOfByte, paramInt1, arrayOfInt[0], arrayOfInt[1]);
    paramBerEncoder.endSeq();
  }
  
  private static int[] findRightParen(byte[] paramArrayOfByte, int[] paramArrayOfInt, int paramInt)
    throws IOException, NamingException
  {
    int i = 1;
    int j = 0;
    int k = paramArrayOfInt[0];
    while ((k < paramInt) && (i > 0))
    {
      if (j == 0) {
        if (paramArrayOfByte[k] == 40) {
          i++;
        } else if (paramArrayOfByte[k] == 41) {
          i--;
        }
      }
      if ((paramArrayOfByte[k] == 92) && (j == 0)) {
        j = 1;
      } else {
        j = 0;
      }
      if (i > 0) {
        k++;
      }
    }
    if (i != 0) {
      throw new InvalidSearchFilterException("Unbalanced parenthesis");
    }
    int[] arrayOfInt = { paramArrayOfInt[0], k };
    paramArrayOfInt[0] = (k + 1);
    return arrayOfInt;
  }
  
  private static void encodeFilterList(BerEncoder paramBerEncoder, byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3)
    throws IOException, NamingException
  {
    int[] arrayOfInt1 = new int[1];
    int i = 0;
    arrayOfInt1[0] = paramInt2;
    while (arrayOfInt1[0] < paramInt3)
    {
      if (!Character.isSpaceChar((char)paramArrayOfByte[arrayOfInt1[0]]))
      {
        if ((paramInt1 == 162) && (i > 0)) {
          throw new InvalidSearchFilterException("Filter (!) cannot be followed by more than one filters");
        }
        if (paramArrayOfByte[arrayOfInt1[0]] != 40)
        {
          int[] arrayOfInt2 = findRightParen(paramArrayOfByte, arrayOfInt1, paramInt3);
          int j = arrayOfInt2[1] - arrayOfInt2[0];
          byte[] arrayOfByte = new byte[j + 2];
          System.arraycopy(paramArrayOfByte, arrayOfInt2[0], arrayOfByte, 1, j);
          arrayOfByte[0] = 40;
          arrayOfByte[(j + 1)] = 41;
          encodeFilter(paramBerEncoder, arrayOfByte, 0, arrayOfByte.length);
          i++;
        }
      }
      arrayOfInt1[0] += 1;
    }
  }
  
  private static void encodeExtensibleMatch(BerEncoder paramBerEncoder, byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    throws IOException, NamingException
  {
    boolean bool = false;
    paramBerEncoder.beginSeq(169);
    int i;
    if ((i = indexOf(paramArrayOfByte, ':', paramInt1, paramInt2)) >= 0)
    {
      int k;
      if ((k = indexOf(paramArrayOfByte, ":dn", i, paramInt2)) >= 0) {
        bool = true;
      }
      int j;
      if (((j = indexOf(paramArrayOfByte, ':', i + 1, paramInt2)) >= 0) || (k == -1)) {
        if (k == i) {
          paramBerEncoder.encodeOctetString(paramArrayOfByte, 129, j + 1, paramInt2 - (j + 1));
        } else if ((k == j) && (k >= 0)) {
          paramBerEncoder.encodeOctetString(paramArrayOfByte, 129, i + 1, j - (i + 1));
        } else {
          paramBerEncoder.encodeOctetString(paramArrayOfByte, 129, i + 1, paramInt2 - (i + 1));
        }
      }
      if (i > paramInt1) {
        paramBerEncoder.encodeOctetString(paramArrayOfByte, 130, paramInt1, i - paramInt1);
      }
    }
    else
    {
      paramBerEncoder.encodeOctetString(paramArrayOfByte, 130, paramInt1, paramInt2 - paramInt1);
    }
    paramBerEncoder.encodeOctetString(unescapeFilterValue(paramArrayOfByte, paramInt3, paramInt4), 131);
    paramBerEncoder.encodeBoolean(bool, 132);
    paramBerEncoder.endSeq();
  }
  
  private static void dprint(String paramString)
  {
    dprint(paramString, new byte[0], 0, 0);
  }
  
  private static void dprint(String paramString, byte[] paramArrayOfByte)
  {
    dprint(paramString, paramArrayOfByte, 0, paramArrayOfByte.length);
  }
  
  private static void dprint(String paramString, byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    String str = "  ";
    int i = dbgIndent;
    while (i-- > 0) {
      str = str + "  ";
    }
    str = str + paramString;
    System.err.print(str);
    for (int j = paramInt1; j < paramInt2; j++) {
      System.err.print((char)paramArrayOfByte[j]);
    }
    System.err.println();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jndi\ldap\Filter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */