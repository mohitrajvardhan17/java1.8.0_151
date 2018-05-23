package com.sun.org.apache.xerces.internal.impl;

import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.util.XML11Char;
import com.sun.org.apache.xerces.internal.util.XMLChar;
import com.sun.org.apache.xerces.internal.util.XMLStringBuffer;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityManager.Limit;
import com.sun.org.apache.xerces.internal.xni.QName;
import com.sun.org.apache.xerces.internal.xni.XMLString;
import com.sun.xml.internal.stream.Entity.ScannedEntity;
import java.io.IOException;

public class XML11EntityScanner
  extends XMLEntityScanner
{
  public XML11EntityScanner() {}
  
  public int peekChar()
    throws IOException
  {
    if (fCurrentEntity.position == fCurrentEntity.count) {
      load(0, true, true);
    }
    int i = fCurrentEntity.ch[fCurrentEntity.position];
    if (fCurrentEntity.isExternal()) {
      return (i != 13) && (i != 133) && (i != 8232) ? i : 10;
    }
    return i;
  }
  
  protected int scanChar(XMLScanner.NameType paramNameType)
    throws IOException
  {
    if (fCurrentEntity.position == fCurrentEntity.count) {
      load(0, true, true);
    }
    int i = fCurrentEntity.position;
    int j = fCurrentEntity.ch[(fCurrentEntity.position++)];
    boolean bool = false;
    if ((j == 10) || (((j == 13) || (j == 133) || (j == 8232)) && ((bool = fCurrentEntity.isExternal()))))
    {
      fCurrentEntity.lineNumber += 1;
      fCurrentEntity.columnNumber = 1;
      if (fCurrentEntity.position == fCurrentEntity.count)
      {
        invokeListeners(1);
        fCurrentEntity.ch[0] = ((char)j);
        load(1, false, false);
        i = 0;
      }
      if ((j == 13) && (bool))
      {
        int k = fCurrentEntity.ch[(fCurrentEntity.position++)];
        if ((k != 10) && (k != 133)) {
          fCurrentEntity.position -= 1;
        }
      }
      j = 10;
    }
    fCurrentEntity.columnNumber += 1;
    if (!detectingVersion) {
      checkEntityLimit(paramNameType, fCurrentEntity, i, fCurrentEntity.position - i);
    }
    return j;
  }
  
  protected String scanNmtoken()
    throws IOException
  {
    if (fCurrentEntity.position == fCurrentEntity.count) {
      load(0, true, true);
    }
    int i = fCurrentEntity.position;
    for (;;)
    {
      char c = fCurrentEntity.ch[fCurrentEntity.position];
      int k;
      char[] arrayOfChar1;
      if (XML11Char.isXML11Name(c))
      {
        if (++fCurrentEntity.position == fCurrentEntity.count)
        {
          k = fCurrentEntity.position - i;
          invokeListeners(k);
          if (k == fCurrentEntity.ch.length)
          {
            arrayOfChar1 = new char[fCurrentEntity.ch.length << 1];
            System.arraycopy(fCurrentEntity.ch, i, arrayOfChar1, 0, k);
            fCurrentEntity.ch = arrayOfChar1;
          }
          else
          {
            System.arraycopy(fCurrentEntity.ch, i, fCurrentEntity.ch, 0, k);
          }
          i = 0;
          if (load(k, false, false)) {
            break;
          }
        }
      }
      else
      {
        if (!XML11Char.isXML11NameHighSurrogate(c)) {
          break;
        }
        if (++fCurrentEntity.position == fCurrentEntity.count)
        {
          k = fCurrentEntity.position - i;
          invokeListeners(k);
          if (k == fCurrentEntity.ch.length)
          {
            arrayOfChar1 = new char[fCurrentEntity.ch.length << 1];
            System.arraycopy(fCurrentEntity.ch, i, arrayOfChar1, 0, k);
            fCurrentEntity.ch = arrayOfChar1;
          }
          else
          {
            System.arraycopy(fCurrentEntity.ch, i, fCurrentEntity.ch, 0, k);
          }
          i = 0;
          if (load(k, false, false))
          {
            fCurrentEntity.startPosition -= 1;
            fCurrentEntity.position -= 1;
            break;
          }
        }
        k = fCurrentEntity.ch[fCurrentEntity.position];
        if ((!XMLChar.isLowSurrogate(k)) || (!XML11Char.isXML11Name(XMLChar.supplemental(c, k))))
        {
          fCurrentEntity.position -= 1;
        }
        else if (++fCurrentEntity.position == fCurrentEntity.count)
        {
          int m = fCurrentEntity.position - i;
          invokeListeners(m);
          if (m == fCurrentEntity.ch.length)
          {
            char[] arrayOfChar2 = new char[fCurrentEntity.ch.length << 1];
            System.arraycopy(fCurrentEntity.ch, i, arrayOfChar2, 0, m);
            fCurrentEntity.ch = arrayOfChar2;
          }
          else
          {
            System.arraycopy(fCurrentEntity.ch, i, fCurrentEntity.ch, 0, m);
          }
          i = 0;
          if (load(m, false, false)) {
            break;
          }
        }
      }
    }
    int j = fCurrentEntity.position - i;
    fCurrentEntity.columnNumber += j;
    String str = null;
    if (j > 0) {
      str = fSymbolTable.addSymbol(fCurrentEntity.ch, i, j);
    }
    return str;
  }
  
  protected String scanName(XMLScanner.NameType paramNameType)
    throws IOException
  {
    if (fCurrentEntity.position == fCurrentEntity.count) {
      load(0, true, true);
    }
    int i = fCurrentEntity.position;
    char c1 = fCurrentEntity.ch[i];
    if (XML11Char.isXML11NameStart(c1))
    {
      if (++fCurrentEntity.position == fCurrentEntity.count)
      {
        invokeListeners(1);
        fCurrentEntity.ch[0] = c1;
        i = 0;
        if (load(1, false, false))
        {
          fCurrentEntity.columnNumber += 1;
          String str1 = fSymbolTable.addSymbol(fCurrentEntity.ch, 0, 1);
          return str1;
        }
      }
    }
    else if (XML11Char.isXML11NameHighSurrogate(c1))
    {
      if (++fCurrentEntity.position == fCurrentEntity.count)
      {
        invokeListeners(1);
        fCurrentEntity.ch[0] = c1;
        i = 0;
        if (load(1, false, false))
        {
          fCurrentEntity.position -= 1;
          fCurrentEntity.startPosition -= 1;
          return null;
        }
      }
      c2 = fCurrentEntity.ch[fCurrentEntity.position];
      if ((!XMLChar.isLowSurrogate(c2)) || (!XML11Char.isXML11NameStart(XMLChar.supplemental(c1, c2))))
      {
        fCurrentEntity.position -= 1;
        return null;
      }
      if (++fCurrentEntity.position == fCurrentEntity.count)
      {
        invokeListeners(2);
        fCurrentEntity.ch[0] = c1;
        fCurrentEntity.ch[1] = c2;
        i = 0;
        if (load(2, false, false))
        {
          fCurrentEntity.columnNumber += 2;
          String str2 = fSymbolTable.addSymbol(fCurrentEntity.ch, 0, 2);
          return str2;
        }
      }
    }
    else
    {
      return null;
    }
    char c2 = '\000';
    for (;;)
    {
      c1 = fCurrentEntity.ch[fCurrentEntity.position];
      if (XML11Char.isXML11Name(c1))
      {
        if ((j = checkBeforeLoad(fCurrentEntity, i, i)) > 0)
        {
          i = 0;
          if (load(j, false, false)) {
            break;
          }
        }
      }
      else
      {
        if (!XML11Char.isXML11NameHighSurrogate(c1)) {
          break;
        }
        if ((j = checkBeforeLoad(fCurrentEntity, i, i)) > 0)
        {
          i = 0;
          if (load(j, false, false))
          {
            fCurrentEntity.position -= 1;
            fCurrentEntity.startPosition -= 1;
            break;
          }
        }
        char c3 = fCurrentEntity.ch[fCurrentEntity.position];
        if ((!XMLChar.isLowSurrogate(c3)) || (!XML11Char.isXML11Name(XMLChar.supplemental(c1, c3))))
        {
          fCurrentEntity.position -= 1;
        }
        else if ((j = checkBeforeLoad(fCurrentEntity, i, i)) > 0)
        {
          i = 0;
          if (load(j, false, false)) {
            break;
          }
        }
      }
    }
    int j = fCurrentEntity.position - i;
    fCurrentEntity.columnNumber += j;
    String str3 = null;
    if (j > 0)
    {
      checkLimit(XMLSecurityManager.Limit.MAX_NAME_LIMIT, fCurrentEntity, i, j);
      checkEntityLimit(paramNameType, fCurrentEntity, i, j);
      str3 = fSymbolTable.addSymbol(fCurrentEntity.ch, i, j);
    }
    return str3;
  }
  
  protected String scanNCName()
    throws IOException
  {
    if (fCurrentEntity.position == fCurrentEntity.count) {
      load(0, true, true);
    }
    int i = fCurrentEntity.position;
    char c1 = fCurrentEntity.ch[i];
    Object localObject;
    if (XML11Char.isXML11NCNameStart(c1))
    {
      if (++fCurrentEntity.position == fCurrentEntity.count)
      {
        invokeListeners(1);
        fCurrentEntity.ch[0] = c1;
        i = 0;
        if (load(1, false, false))
        {
          fCurrentEntity.columnNumber += 1;
          String str1 = fSymbolTable.addSymbol(fCurrentEntity.ch, 0, 1);
          return str1;
        }
      }
    }
    else if (XML11Char.isXML11NameHighSurrogate(c1))
    {
      if (++fCurrentEntity.position == fCurrentEntity.count)
      {
        invokeListeners(1);
        fCurrentEntity.ch[0] = c1;
        i = 0;
        if (load(1, false, false))
        {
          fCurrentEntity.position -= 1;
          fCurrentEntity.startPosition -= 1;
          return null;
        }
      }
      char c2 = fCurrentEntity.ch[fCurrentEntity.position];
      if ((!XMLChar.isLowSurrogate(c2)) || (!XML11Char.isXML11NCNameStart(XMLChar.supplemental(c1, c2))))
      {
        fCurrentEntity.position -= 1;
        return null;
      }
      if (++fCurrentEntity.position == fCurrentEntity.count)
      {
        invokeListeners(2);
        fCurrentEntity.ch[0] = c1;
        fCurrentEntity.ch[1] = c2;
        i = 0;
        if (load(2, false, false))
        {
          fCurrentEntity.columnNumber += 2;
          localObject = fSymbolTable.addSymbol(fCurrentEntity.ch, 0, 2);
          return (String)localObject;
        }
      }
    }
    else
    {
      return null;
    }
    for (;;)
    {
      c1 = fCurrentEntity.ch[fCurrentEntity.position];
      int j;
      if (XML11Char.isXML11NCName(c1))
      {
        if (++fCurrentEntity.position == fCurrentEntity.count)
        {
          j = fCurrentEntity.position - i;
          invokeListeners(j);
          if (j == fCurrentEntity.ch.length)
          {
            localObject = new char[fCurrentEntity.ch.length << 1];
            System.arraycopy(fCurrentEntity.ch, i, localObject, 0, j);
            fCurrentEntity.ch = ((char[])localObject);
          }
          else
          {
            System.arraycopy(fCurrentEntity.ch, i, fCurrentEntity.ch, 0, j);
          }
          i = 0;
          if (load(j, false, false)) {
            break;
          }
        }
      }
      else
      {
        if (!XML11Char.isXML11NameHighSurrogate(c1)) {
          break;
        }
        if (++fCurrentEntity.position == fCurrentEntity.count)
        {
          j = fCurrentEntity.position - i;
          invokeListeners(j);
          if (j == fCurrentEntity.ch.length)
          {
            localObject = new char[fCurrentEntity.ch.length << 1];
            System.arraycopy(fCurrentEntity.ch, i, localObject, 0, j);
            fCurrentEntity.ch = ((char[])localObject);
          }
          else
          {
            System.arraycopy(fCurrentEntity.ch, i, fCurrentEntity.ch, 0, j);
          }
          i = 0;
          if (load(j, false, false))
          {
            fCurrentEntity.startPosition -= 1;
            fCurrentEntity.position -= 1;
            break;
          }
        }
        j = fCurrentEntity.ch[fCurrentEntity.position];
        if ((!XMLChar.isLowSurrogate(j)) || (!XML11Char.isXML11NCName(XMLChar.supplemental(c1, j))))
        {
          fCurrentEntity.position -= 1;
        }
        else if (++fCurrentEntity.position == fCurrentEntity.count)
        {
          int m = fCurrentEntity.position - i;
          invokeListeners(m);
          if (m == fCurrentEntity.ch.length)
          {
            char[] arrayOfChar = new char[fCurrentEntity.ch.length << 1];
            System.arraycopy(fCurrentEntity.ch, i, arrayOfChar, 0, m);
            fCurrentEntity.ch = arrayOfChar;
          }
          else
          {
            System.arraycopy(fCurrentEntity.ch, i, fCurrentEntity.ch, 0, m);
          }
          i = 0;
          if (load(m, false, false)) {
            break;
          }
        }
      }
    }
    int k = fCurrentEntity.position - i;
    fCurrentEntity.columnNumber += k;
    String str2 = null;
    if (k > 0) {
      str2 = fSymbolTable.addSymbol(fCurrentEntity.ch, i, k);
    }
    return str2;
  }
  
  protected boolean scanQName(QName paramQName, XMLScanner.NameType paramNameType)
    throws IOException
  {
    if (fCurrentEntity.position == fCurrentEntity.count) {
      load(0, true, true);
    }
    int i = fCurrentEntity.position;
    char c1 = fCurrentEntity.ch[i];
    if (XML11Char.isXML11NCNameStart(c1))
    {
      if (++fCurrentEntity.position == fCurrentEntity.count)
      {
        invokeListeners(1);
        fCurrentEntity.ch[0] = c1;
        i = 0;
        if (load(1, false, false))
        {
          fCurrentEntity.columnNumber += 1;
          String str1 = fSymbolTable.addSymbol(fCurrentEntity.ch, 0, 1);
          paramQName.setValues(null, str1, str1, null);
          checkEntityLimit(paramNameType, fCurrentEntity, 0, 1);
          return true;
        }
      }
    }
    else if (XML11Char.isXML11NameHighSurrogate(c1))
    {
      if (++fCurrentEntity.position == fCurrentEntity.count)
      {
        invokeListeners(1);
        fCurrentEntity.ch[0] = c1;
        i = 0;
        if (load(1, false, false))
        {
          fCurrentEntity.startPosition -= 1;
          fCurrentEntity.position -= 1;
          return false;
        }
      }
      char c2 = fCurrentEntity.ch[fCurrentEntity.position];
      if ((!XMLChar.isLowSurrogate(c2)) || (!XML11Char.isXML11NCNameStart(XMLChar.supplemental(c1, c2))))
      {
        fCurrentEntity.position -= 1;
        return false;
      }
      if (++fCurrentEntity.position == fCurrentEntity.count)
      {
        invokeListeners(2);
        fCurrentEntity.ch[0] = c1;
        fCurrentEntity.ch[1] = c2;
        i = 0;
        if (load(2, false, false))
        {
          fCurrentEntity.columnNumber += 2;
          String str2 = fSymbolTable.addSymbol(fCurrentEntity.ch, 0, 2);
          paramQName.setValues(null, str2, str2, null);
          checkEntityLimit(paramNameType, fCurrentEntity, 0, 2);
          return true;
        }
      }
    }
    else
    {
      return false;
    }
    int j = -1;
    int k = 0;
    int m = 0;
    for (;;)
    {
      c1 = fCurrentEntity.ch[fCurrentEntity.position];
      if (XML11Char.isXML11Name(c1))
      {
        if (c1 == ':')
        {
          if (j != -1) {
            break;
          }
          j = fCurrentEntity.position;
          checkLimit(XMLSecurityManager.Limit.MAX_NAME_LIMIT, fCurrentEntity, i, j - i);
        }
        if ((k = checkBeforeLoad(fCurrentEntity, i, j)) > 0)
        {
          if (j != -1) {
            j -= i;
          }
          i = 0;
          if (load(k, false, false)) {
            break;
          }
        }
      }
      else
      {
        if (!XML11Char.isXML11NameHighSurrogate(c1)) {
          break;
        }
        if ((k = checkBeforeLoad(fCurrentEntity, i, j)) > 0)
        {
          if (j != -1) {
            j -= i;
          }
          i = 0;
          if (load(k, false, false))
          {
            m = 1;
            fCurrentEntity.startPosition -= 1;
            fCurrentEntity.position -= 1;
            break;
          }
        }
        char c3 = fCurrentEntity.ch[fCurrentEntity.position];
        if ((!XMLChar.isLowSurrogate(c3)) || (!XML11Char.isXML11Name(XMLChar.supplemental(c1, c3))))
        {
          m = 1;
          fCurrentEntity.position -= 1;
        }
        else if ((k = checkBeforeLoad(fCurrentEntity, i, j)) > 0)
        {
          if (j != -1) {
            j -= i;
          }
          i = 0;
          if (load(k, false, false)) {
            break;
          }
        }
      }
    }
    k = fCurrentEntity.position - i;
    fCurrentEntity.columnNumber += k;
    if (k > 0)
    {
      String str3 = null;
      Object localObject = null;
      String str4 = fSymbolTable.addSymbol(fCurrentEntity.ch, i, k);
      if (j != -1)
      {
        int n = j - i;
        checkLimit(XMLSecurityManager.Limit.MAX_NAME_LIMIT, fCurrentEntity, i, n);
        str3 = fSymbolTable.addSymbol(fCurrentEntity.ch, i, n);
        int i1 = k - n - 1;
        int i2 = j + 1;
        if ((!XML11Char.isXML11NCNameStart(fCurrentEntity.ch[i2])) && ((!XML11Char.isXML11NameHighSurrogate(fCurrentEntity.ch[i2])) || (m != 0))) {
          fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "IllegalQName", null, (short)2);
        }
        checkLimit(XMLSecurityManager.Limit.MAX_NAME_LIMIT, fCurrentEntity, j + 1, i1);
        localObject = fSymbolTable.addSymbol(fCurrentEntity.ch, j + 1, i1);
      }
      else
      {
        localObject = str4;
        checkLimit(XMLSecurityManager.Limit.MAX_NAME_LIMIT, fCurrentEntity, i, k);
      }
      paramQName.setValues(str3, (String)localObject, str4, null);
      checkEntityLimit(paramNameType, fCurrentEntity, i, k);
      return true;
    }
    return false;
  }
  
  protected int scanContent(XMLString paramXMLString)
    throws IOException
  {
    if (fCurrentEntity.position == fCurrentEntity.count)
    {
      load(0, true, true);
    }
    else if (fCurrentEntity.position == fCurrentEntity.count - 1)
    {
      invokeListeners(1);
      fCurrentEntity.ch[0] = fCurrentEntity.ch[(fCurrentEntity.count - 1)];
      load(1, false, false);
      fCurrentEntity.position = 0;
      fCurrentEntity.startPosition = 0;
    }
    int i = fCurrentEntity.position;
    int j = fCurrentEntity.ch[i];
    int k = 0;
    int m = 0;
    boolean bool = fCurrentEntity.isExternal();
    if ((j == 10) || (((j == 13) || (j == 133) || (j == 8232)) && (bool)))
    {
      do
      {
        j = fCurrentEntity.ch[(fCurrentEntity.position++)];
        if ((j == 13) && (bool))
        {
          k++;
          fCurrentEntity.lineNumber += 1;
          fCurrentEntity.columnNumber = 1;
          if (fCurrentEntity.position == fCurrentEntity.count)
          {
            checkEntityLimit(null, fCurrentEntity, i, k);
            i = 0;
            fCurrentEntity.baseCharOffset += fCurrentEntity.position - fCurrentEntity.startPosition;
            fCurrentEntity.position = k;
            fCurrentEntity.startPosition = k;
            if (load(k, false, true))
            {
              m = 1;
              break;
            }
          }
          n = fCurrentEntity.ch[fCurrentEntity.position];
          if ((n == 10) || (n == 133))
          {
            fCurrentEntity.position += 1;
            i++;
          }
          else
          {
            k++;
          }
        }
        else if ((j == 10) || (((j == 133) || (j == 8232)) && (bool)))
        {
          k++;
          fCurrentEntity.lineNumber += 1;
          fCurrentEntity.columnNumber = 1;
          if (fCurrentEntity.position == fCurrentEntity.count)
          {
            checkEntityLimit(null, fCurrentEntity, i, k);
            i = 0;
            fCurrentEntity.baseCharOffset += fCurrentEntity.position - fCurrentEntity.startPosition;
            fCurrentEntity.position = k;
            fCurrentEntity.startPosition = k;
            if (load(k, false, true))
            {
              m = 1;
              break;
            }
          }
        }
        else
        {
          fCurrentEntity.position -= 1;
          break;
        }
      } while (fCurrentEntity.position < fCurrentEntity.count - 1);
      for (n = i; n < fCurrentEntity.position; n++) {
        fCurrentEntity.ch[n] = '\n';
      }
      n = fCurrentEntity.position - i;
      if (fCurrentEntity.position == fCurrentEntity.count - 1)
      {
        checkEntityLimit(null, fCurrentEntity, i, n);
        paramXMLString.setValues(fCurrentEntity.ch, i, n);
        return -1;
      }
    }
    if (bool)
    {
      do
      {
        if (fCurrentEntity.position >= fCurrentEntity.count) {
          break;
        }
        j = fCurrentEntity.ch[(fCurrentEntity.position++)];
      } while ((XML11Char.isXML11Content(j)) && (j != 133) && (j != 8232));
      fCurrentEntity.position -= 1;
    }
    else
    {
      while (fCurrentEntity.position < fCurrentEntity.count)
      {
        j = fCurrentEntity.ch[(fCurrentEntity.position++)];
        if (!XML11Char.isXML11InternalEntityContent(j)) {
          fCurrentEntity.position -= 1;
        }
      }
    }
    int n = fCurrentEntity.position - i;
    fCurrentEntity.columnNumber += n - k;
    if (m == 0) {
      checkEntityLimit(null, fCurrentEntity, i, n);
    }
    paramXMLString.setValues(fCurrentEntity.ch, i, n);
    if (fCurrentEntity.position != fCurrentEntity.count)
    {
      j = fCurrentEntity.ch[fCurrentEntity.position];
      if (((j == 13) || (j == 133) || (j == 8232)) && (bool)) {
        j = 10;
      }
    }
    else
    {
      j = -1;
    }
    return j;
  }
  
  protected int scanLiteral(int paramInt, XMLString paramXMLString, boolean paramBoolean)
    throws IOException
  {
    if (fCurrentEntity.position == fCurrentEntity.count)
    {
      load(0, true, true);
    }
    else if (fCurrentEntity.position == fCurrentEntity.count - 1)
    {
      invokeListeners(1);
      fCurrentEntity.ch[0] = fCurrentEntity.ch[(fCurrentEntity.count - 1)];
      load(1, false, false);
      fCurrentEntity.startPosition = 0;
      fCurrentEntity.position = 0;
    }
    int i = fCurrentEntity.position;
    int j = fCurrentEntity.ch[i];
    int k = 0;
    boolean bool = fCurrentEntity.isExternal();
    if ((j == 10) || (((j == 13) || (j == 133) || (j == 8232)) && (bool)))
    {
      do
      {
        j = fCurrentEntity.ch[(fCurrentEntity.position++)];
        if ((j == 13) && (bool))
        {
          k++;
          fCurrentEntity.lineNumber += 1;
          fCurrentEntity.columnNumber = 1;
          if (fCurrentEntity.position == fCurrentEntity.count)
          {
            i = 0;
            fCurrentEntity.baseCharOffset += fCurrentEntity.position - fCurrentEntity.startPosition;
            fCurrentEntity.position = k;
            fCurrentEntity.startPosition = k;
            if (load(k, false, true)) {
              break;
            }
          }
          m = fCurrentEntity.ch[fCurrentEntity.position];
          if ((m == 10) || (m == 133))
          {
            fCurrentEntity.position += 1;
            i++;
          }
          else
          {
            k++;
          }
        }
        else if ((j == 10) || (((j == 133) || (j == 8232)) && (bool)))
        {
          k++;
          fCurrentEntity.lineNumber += 1;
          fCurrentEntity.columnNumber = 1;
          if (fCurrentEntity.position == fCurrentEntity.count)
          {
            i = 0;
            fCurrentEntity.baseCharOffset += fCurrentEntity.position - fCurrentEntity.startPosition;
            fCurrentEntity.position = k;
            fCurrentEntity.startPosition = k;
            if (load(k, false, true)) {
              break;
            }
          }
        }
        else
        {
          fCurrentEntity.position -= 1;
          break;
        }
      } while (fCurrentEntity.position < fCurrentEntity.count - 1);
      for (m = i; m < fCurrentEntity.position; m++) {
        fCurrentEntity.ch[m] = '\n';
      }
      m = fCurrentEntity.position - i;
      if (fCurrentEntity.position == fCurrentEntity.count - 1)
      {
        paramXMLString.setValues(fCurrentEntity.ch, i, m);
        return -1;
      }
    }
    if (bool)
    {
      do
      {
        if (fCurrentEntity.position >= fCurrentEntity.count) {
          break;
        }
        j = fCurrentEntity.ch[(fCurrentEntity.position++)];
      } while ((j != paramInt) && (j != 37) && (XML11Char.isXML11Content(j)) && (j != 133) && (j != 8232));
      fCurrentEntity.position -= 1;
    }
    else
    {
      while (fCurrentEntity.position < fCurrentEntity.count)
      {
        j = fCurrentEntity.ch[(fCurrentEntity.position++)];
        if (((j == paramInt) && (!fCurrentEntity.literal)) || (j == 37) || (!XML11Char.isXML11InternalEntityContent(j))) {
          fCurrentEntity.position -= 1;
        }
      }
    }
    int m = fCurrentEntity.position - i;
    fCurrentEntity.columnNumber += m - k;
    checkEntityLimit(null, fCurrentEntity, i, m);
    if (paramBoolean) {
      checkLimit(XMLSecurityManager.Limit.MAX_NAME_LIMIT, fCurrentEntity, i, m);
    }
    paramXMLString.setValues(fCurrentEntity.ch, i, m);
    if (fCurrentEntity.position != fCurrentEntity.count)
    {
      j = fCurrentEntity.ch[fCurrentEntity.position];
      if ((j == paramInt) && (fCurrentEntity.literal)) {
        j = -1;
      }
    }
    else
    {
      j = -1;
    }
    return j;
  }
  
  protected boolean scanData(String paramString, XMLStringBuffer paramXMLStringBuffer)
    throws IOException
  {
    int i = 0;
    int j = paramString.length();
    int k = paramString.charAt(0);
    boolean bool1 = fCurrentEntity.isExternal();
    label1451:
    do
    {
      if (fCurrentEntity.position == fCurrentEntity.count) {
        load(0, true, false);
      }
      boolean bool2 = false;
      while ((fCurrentEntity.position >= fCurrentEntity.count - j) && (!bool2))
      {
        System.arraycopy(fCurrentEntity.ch, fCurrentEntity.position, fCurrentEntity.ch, 0, fCurrentEntity.count - fCurrentEntity.position);
        bool2 = load(fCurrentEntity.count - fCurrentEntity.position, false, false);
        fCurrentEntity.position = 0;
        fCurrentEntity.startPosition = 0;
      }
      if (fCurrentEntity.position >= fCurrentEntity.count - j)
      {
        m = fCurrentEntity.count - fCurrentEntity.position;
        checkEntityLimit(XMLScanner.NameType.COMMENT, fCurrentEntity, fCurrentEntity.position, m);
        paramXMLStringBuffer.append(fCurrentEntity.ch, fCurrentEntity.position, m);
        fCurrentEntity.columnNumber += fCurrentEntity.count;
        fCurrentEntity.baseCharOffset += fCurrentEntity.position - fCurrentEntity.startPosition;
        fCurrentEntity.position = fCurrentEntity.count;
        fCurrentEntity.startPosition = fCurrentEntity.count;
        load(0, true, false);
        return false;
      }
      int m = fCurrentEntity.position;
      int n = fCurrentEntity.ch[m];
      int i1 = 0;
      if ((n == 10) || (((n == 13) || (n == 133) || (n == 8232)) && (bool1)))
      {
        do
        {
          n = fCurrentEntity.ch[(fCurrentEntity.position++)];
          if ((n == 13) && (bool1))
          {
            i1++;
            fCurrentEntity.lineNumber += 1;
            fCurrentEntity.columnNumber = 1;
            if (fCurrentEntity.position == fCurrentEntity.count)
            {
              m = 0;
              fCurrentEntity.baseCharOffset += fCurrentEntity.position - fCurrentEntity.startPosition;
              fCurrentEntity.position = i1;
              fCurrentEntity.startPosition = i1;
              if (load(i1, false, true)) {
                break;
              }
            }
            i2 = fCurrentEntity.ch[fCurrentEntity.position];
            if ((i2 == 10) || (i2 == 133))
            {
              fCurrentEntity.position += 1;
              m++;
            }
            else
            {
              i1++;
            }
          }
          else if ((n == 10) || (((n == 133) || (n == 8232)) && (bool1)))
          {
            i1++;
            fCurrentEntity.lineNumber += 1;
            fCurrentEntity.columnNumber = 1;
            if (fCurrentEntity.position == fCurrentEntity.count)
            {
              m = 0;
              fCurrentEntity.baseCharOffset += fCurrentEntity.position - fCurrentEntity.startPosition;
              fCurrentEntity.position = i1;
              fCurrentEntity.startPosition = i1;
              fCurrentEntity.count = i1;
              if (load(i1, false, true)) {
                break;
              }
            }
          }
          else
          {
            fCurrentEntity.position -= 1;
            break;
          }
        } while (fCurrentEntity.position < fCurrentEntity.count - 1);
        for (i2 = m; i2 < fCurrentEntity.position; i2++) {
          fCurrentEntity.ch[i2] = '\n';
        }
        i2 = fCurrentEntity.position - m;
        if (fCurrentEntity.position == fCurrentEntity.count - 1)
        {
          checkEntityLimit(XMLScanner.NameType.COMMENT, fCurrentEntity, m, i2);
          paramXMLStringBuffer.append(fCurrentEntity.ch, m, i2);
          return true;
        }
      }
      int i3;
      if (bool1)
      {
        do
        {
          for (;;)
          {
            if (fCurrentEntity.position >= fCurrentEntity.count) {
              break label1451;
            }
            n = fCurrentEntity.ch[(fCurrentEntity.position++)];
            if (n != k) {
              break;
            }
            i2 = fCurrentEntity.position - 1;
            for (i3 = 1; i3 < j; i3++)
            {
              if (fCurrentEntity.position == fCurrentEntity.count)
              {
                fCurrentEntity.position -= i3;
                break label1451;
              }
              n = fCurrentEntity.ch[(fCurrentEntity.position++)];
              if (paramString.charAt(i3) != n)
              {
                fCurrentEntity.position -= 1;
                break;
              }
            }
            if (fCurrentEntity.position == i2 + j)
            {
              i = 1;
              break label1451;
            }
          }
          if ((n == 10) || (n == 13) || (n == 133) || (n == 8232))
          {
            fCurrentEntity.position -= 1;
            break;
          }
        } while (XML11Char.isXML11ValidLiteral(n));
        fCurrentEntity.position -= 1;
        i2 = fCurrentEntity.position - m;
        fCurrentEntity.columnNumber += i2 - i1;
        checkEntityLimit(XMLScanner.NameType.COMMENT, fCurrentEntity, m, i2);
        paramXMLStringBuffer.append(fCurrentEntity.ch, m, i2);
        return true;
      }
      while (fCurrentEntity.position < fCurrentEntity.count)
      {
        n = fCurrentEntity.ch[(fCurrentEntity.position++)];
        if (n == k)
        {
          i2 = fCurrentEntity.position - 1;
          for (i3 = 1; i3 < j; i3++)
          {
            if (fCurrentEntity.position == fCurrentEntity.count)
            {
              fCurrentEntity.position -= i3;
              break label1451;
            }
            n = fCurrentEntity.ch[(fCurrentEntity.position++)];
            if (paramString.charAt(i3) != n)
            {
              fCurrentEntity.position -= 1;
              break;
            }
          }
          if (fCurrentEntity.position == i2 + j) {
            i = 1;
          }
        }
        else if (n == 10)
        {
          fCurrentEntity.position -= 1;
        }
        else if (!XML11Char.isXML11Valid(n))
        {
          fCurrentEntity.position -= 1;
          i2 = fCurrentEntity.position - m;
          fCurrentEntity.columnNumber += i2 - i1;
          checkEntityLimit(XMLScanner.NameType.COMMENT, fCurrentEntity, m, i2);
          paramXMLStringBuffer.append(fCurrentEntity.ch, m, i2);
          return true;
        }
      }
      int i2 = fCurrentEntity.position - m;
      fCurrentEntity.columnNumber += i2 - i1;
      checkEntityLimit(XMLScanner.NameType.COMMENT, fCurrentEntity, m, i2);
      if (i != 0) {
        i2 -= j;
      }
      paramXMLStringBuffer.append(fCurrentEntity.ch, m, i2);
    } while (i == 0);
    return i == 0;
  }
  
  protected boolean skipChar(int paramInt, XMLScanner.NameType paramNameType)
    throws IOException
  {
    if (fCurrentEntity.position == fCurrentEntity.count) {
      load(0, true, true);
    }
    int i = fCurrentEntity.position;
    int j = fCurrentEntity.ch[fCurrentEntity.position];
    if (j == paramInt)
    {
      fCurrentEntity.position += 1;
      if (paramInt == 10)
      {
        fCurrentEntity.lineNumber += 1;
        fCurrentEntity.columnNumber = 1;
      }
      else
      {
        fCurrentEntity.columnNumber += 1;
      }
      checkEntityLimit(paramNameType, fCurrentEntity, i, fCurrentEntity.position - i);
      return true;
    }
    if ((paramInt == 10) && ((j == 8232) || (j == 133)) && (fCurrentEntity.isExternal()))
    {
      fCurrentEntity.position += 1;
      fCurrentEntity.lineNumber += 1;
      fCurrentEntity.columnNumber = 1;
      checkEntityLimit(paramNameType, fCurrentEntity, i, fCurrentEntity.position - i);
      return true;
    }
    if ((paramInt == 10) && (j == 13) && (fCurrentEntity.isExternal()))
    {
      if (fCurrentEntity.position == fCurrentEntity.count)
      {
        invokeListeners(1);
        fCurrentEntity.ch[0] = ((char)j);
        load(1, false, false);
      }
      int k = fCurrentEntity.ch[(++fCurrentEntity.position)];
      if ((k == 10) || (k == 133)) {
        fCurrentEntity.position += 1;
      }
      fCurrentEntity.lineNumber += 1;
      fCurrentEntity.columnNumber = 1;
      checkEntityLimit(paramNameType, fCurrentEntity, i, fCurrentEntity.position - i);
      return true;
    }
    return false;
  }
  
  protected boolean skipSpaces()
    throws IOException
  {
    if (fCurrentEntity.position == fCurrentEntity.count) {
      load(0, true, true);
    }
    if (fCurrentEntity == null) {
      return false;
    }
    int i = fCurrentEntity.ch[fCurrentEntity.position];
    int j = fCurrentEntity.position - 1;
    boolean bool;
    if (fCurrentEntity.isExternal())
    {
      if (XML11Char.isXML11Space(i))
      {
        do
        {
          bool = false;
          if ((i == 10) || (i == 13) || (i == 133) || (i == 8232))
          {
            fCurrentEntity.lineNumber += 1;
            fCurrentEntity.columnNumber = 1;
            if (fCurrentEntity.position == fCurrentEntity.count - 1)
            {
              invokeListeners(1);
              fCurrentEntity.ch[0] = ((char)i);
              bool = load(1, true, false);
              if (!bool)
              {
                fCurrentEntity.startPosition = 0;
                fCurrentEntity.position = 0;
              }
              else if (fCurrentEntity == null)
              {
                return true;
              }
            }
            if (i == 13)
            {
              int k = fCurrentEntity.ch[(++fCurrentEntity.position)];
              if ((k != 10) && (k != 133)) {
                fCurrentEntity.position -= 1;
              }
            }
          }
          else
          {
            fCurrentEntity.columnNumber += 1;
          }
          checkEntityLimit(null, fCurrentEntity, j, fCurrentEntity.position - j);
          j = fCurrentEntity.position;
          if (!bool) {
            fCurrentEntity.position += 1;
          }
          if (fCurrentEntity.position == fCurrentEntity.count)
          {
            load(0, true, true);
            if (fCurrentEntity == null) {
              return true;
            }
          }
        } while (XML11Char.isXML11Space(i = fCurrentEntity.ch[fCurrentEntity.position]));
        return true;
      }
    }
    else if (XMLChar.isSpace(i))
    {
      do
      {
        bool = false;
        if (i == 10)
        {
          fCurrentEntity.lineNumber += 1;
          fCurrentEntity.columnNumber = 1;
          if (fCurrentEntity.position == fCurrentEntity.count - 1)
          {
            invokeListeners(1);
            fCurrentEntity.ch[0] = ((char)i);
            bool = load(1, true, false);
            if (!bool)
            {
              fCurrentEntity.startPosition = 0;
              fCurrentEntity.position = 0;
            }
            else if (fCurrentEntity == null)
            {
              return true;
            }
          }
        }
        else
        {
          fCurrentEntity.columnNumber += 1;
        }
        checkEntityLimit(null, fCurrentEntity, j, fCurrentEntity.position - j);
        j = fCurrentEntity.position;
        if (!bool) {
          fCurrentEntity.position += 1;
        }
        if (fCurrentEntity.position == fCurrentEntity.count)
        {
          load(0, true, true);
          if (fCurrentEntity == null) {
            return true;
          }
        }
      } while (XMLChar.isSpace(i = fCurrentEntity.ch[fCurrentEntity.position]));
      return true;
    }
    return false;
  }
  
  protected boolean skipString(String paramString)
    throws IOException
  {
    if (fCurrentEntity.position == fCurrentEntity.count) {
      load(0, true, true);
    }
    int i = paramString.length();
    int j = fCurrentEntity.position;
    for (int k = 0; k < i; k++)
    {
      int m = fCurrentEntity.ch[(fCurrentEntity.position++)];
      if (m != paramString.charAt(k))
      {
        fCurrentEntity.position -= k + 1;
        return false;
      }
      if ((k < i - 1) && (fCurrentEntity.position == fCurrentEntity.count))
      {
        invokeListeners(0);
        System.arraycopy(fCurrentEntity.ch, fCurrentEntity.count - k - 1, fCurrentEntity.ch, 0, k + 1);
        if (load(k + 1, false, false))
        {
          fCurrentEntity.startPosition -= k + 1;
          fCurrentEntity.position -= k + 1;
          return false;
        }
      }
    }
    fCurrentEntity.columnNumber += i;
    if (!detectingVersion) {
      checkEntityLimit(null, fCurrentEntity, j, i);
    }
    return true;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\impl\XML11EntityScanner.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */