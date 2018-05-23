package com.sun.org.apache.xpath.internal.compiler;

import com.sun.org.apache.xml.internal.utils.ObjectVector;
import com.sun.org.apache.xml.internal.utils.PrefixResolver;
import java.util.Vector;
import javax.xml.transform.TransformerException;

class Lexer
{
  private Compiler m_compiler;
  PrefixResolver m_namespaceContext;
  XPathParser m_processor;
  static final int TARGETEXTRA = 10000;
  private int[] m_patternMap = new int[100];
  private int m_patternMapSize;
  
  Lexer(Compiler paramCompiler, PrefixResolver paramPrefixResolver, XPathParser paramXPathParser)
  {
    m_compiler = paramCompiler;
    m_namespaceContext = paramPrefixResolver;
    m_processor = paramXPathParser;
  }
  
  void tokenize(String paramString)
    throws TransformerException
  {
    tokenize(paramString, null);
  }
  
  void tokenize(String paramString, Vector paramVector)
    throws TransformerException
  {
    m_compiler.m_currentPattern = paramString;
    m_patternMapSize = 0;
    m_compiler.m_opMap = new OpMapVector(2500, 2500, 1);
    int i = paramString.length();
    int j = -1;
    int k = -1;
    boolean bool1 = true;
    boolean bool2 = false;
    boolean bool3 = false;
    int m = 0;
    for (int n = 0; n < i; n++)
    {
      char c = paramString.charAt(n);
      switch (c)
      {
      case '"': 
        if (j != -1)
        {
          bool3 = false;
          bool1 = mapPatternElemPos(m, bool1, bool2);
          bool2 = false;
          if (-1 != k) {
            k = mapNSTokens(paramString, j, k, n);
          } else {
            addToTokenQueue(paramString.substring(j, n));
          }
        }
        j = n;
        n++;
        while ((n < i) && ((c = paramString.charAt(n)) != '"')) {
          n++;
        }
        if ((c == '"') && (n < i))
        {
          addToTokenQueue(paramString.substring(j, n + 1));
          j = -1;
        }
        else
        {
          m_processor.error("ER_EXPECTED_DOUBLE_QUOTE", null);
        }
        break;
      case '\'': 
        if (j != -1)
        {
          bool3 = false;
          bool1 = mapPatternElemPos(m, bool1, bool2);
          bool2 = false;
          if (-1 != k) {
            k = mapNSTokens(paramString, j, k, n);
          } else {
            addToTokenQueue(paramString.substring(j, n));
          }
        }
        j = n;
        n++;
        while ((n < i) && ((c = paramString.charAt(n)) != '\'')) {
          n++;
        }
        if ((c == '\'') && (n < i))
        {
          addToTokenQueue(paramString.substring(j, n + 1));
          j = -1;
        }
        else
        {
          m_processor.error("ER_EXPECTED_SINGLE_QUOTE", null);
        }
        break;
      case '\t': 
      case '\n': 
      case '\r': 
      case ' ': 
        if (j == -1) {
          continue;
        }
        bool3 = false;
        bool1 = mapPatternElemPos(m, bool1, bool2);
        bool2 = false;
        if (-1 != k) {
          k = mapNSTokens(paramString, j, k, n);
        } else {
          addToTokenQueue(paramString.substring(j, n));
        }
        j = -1;
        break;
      case '@': 
        bool2 = true;
      case '-': 
        if ('-' == c)
        {
          if ((!bool3) && (j != -1)) {
            continue;
          }
          bool3 = false;
        }
      case '!': 
      case '$': 
      case '(': 
      case ')': 
      case '*': 
      case '+': 
      case ',': 
      case '/': 
      case '<': 
      case '=': 
      case '>': 
      case '[': 
      case '\\': 
      case ']': 
      case '^': 
      case '|': 
        if (j != -1)
        {
          bool3 = false;
          bool1 = mapPatternElemPos(m, bool1, bool2);
          bool2 = false;
          if (-1 != k) {
            k = mapNSTokens(paramString, j, k, n);
          } else {
            addToTokenQueue(paramString.substring(j, n));
          }
          j = -1;
        }
        else if (('/' == c) && (bool1))
        {
          bool1 = mapPatternElemPos(m, bool1, bool2);
        }
        else if ('*' == c)
        {
          bool1 = mapPatternElemPos(m, bool1, bool2);
          bool2 = false;
        }
        if ((0 == m) && ('|' == c))
        {
          if (null != paramVector) {
            recordTokenString(paramVector);
          }
          bool1 = true;
        }
        if ((')' == c) || (']' == c)) {
          m--;
        } else if (('(' == c) || ('[' == c)) {
          m++;
        }
        addToTokenQueue(paramString.substring(n, n + 1));
        break;
      case ':': 
        if (n > 0)
        {
          if (k == n - 1)
          {
            if ((j != -1) && (j < n - 1)) {
              addToTokenQueue(paramString.substring(j, n - 1));
            }
            bool3 = false;
            bool2 = false;
            j = -1;
            k = -1;
            addToTokenQueue(paramString.substring(n - 1, n + 1));
            continue;
          }
          k = n;
        }
        break;
      }
      if (-1 == j)
      {
        j = n;
        bool3 = Character.isDigit(c);
      }
      else if (bool3)
      {
        bool3 = Character.isDigit(c);
      }
    }
    if (j != -1)
    {
      bool3 = false;
      bool1 = mapPatternElemPos(m, bool1, bool2);
      if ((-1 != k) || ((m_namespaceContext != null) && (m_namespaceContext.handlesNullPrefixes()))) {
        k = mapNSTokens(paramString, j, k, i);
      } else {
        addToTokenQueue(paramString.substring(j, i));
      }
    }
    if (0 == m_compiler.getTokenQueueSize()) {
      m_processor.error("ER_EMPTY_EXPRESSION", null);
    } else if (null != paramVector) {
      recordTokenString(paramVector);
    }
    m_processor.m_queueMark = 0;
  }
  
  private boolean mapPatternElemPos(int paramInt, boolean paramBoolean1, boolean paramBoolean2)
  {
    if (0 == paramInt)
    {
      if (m_patternMapSize >= m_patternMap.length)
      {
        int[] arrayOfInt = m_patternMap;
        int i = m_patternMap.length;
        m_patternMap = new int[m_patternMapSize + 100];
        System.arraycopy(arrayOfInt, 0, m_patternMap, 0, i);
      }
      if (!paramBoolean1) {
        m_patternMap[(m_patternMapSize - 1)] -= 10000;
      }
      m_patternMap[m_patternMapSize] = (m_compiler.getTokenQueueSize() - (paramBoolean2 ? 1 : 0) + 10000);
      m_patternMapSize += 1;
      paramBoolean1 = false;
    }
    return paramBoolean1;
  }
  
  private int getTokenQueuePosFromMap(int paramInt)
  {
    int i = m_patternMap[paramInt];
    return i >= 10000 ? i - 10000 : i;
  }
  
  private final void resetTokenMark(int paramInt)
  {
    int i = m_compiler.getTokenQueueSize();
    m_processor.m_queueMark = (paramInt > 0 ? paramInt : paramInt <= i ? paramInt - 1 : 0);
    if (m_processor.m_queueMark < i)
    {
      m_processor.m_token = ((String)m_compiler.getTokenQueue().elementAt(m_processor.m_queueMark++));
      m_processor.m_tokenChar = m_processor.m_token.charAt(0);
    }
    else
    {
      m_processor.m_token = null;
      m_processor.m_tokenChar = '\000';
    }
  }
  
  final int getKeywordToken(String paramString)
  {
    int i;
    try
    {
      Integer localInteger = Keywords.getKeyWord(paramString);
      i = null != localInteger ? localInteger.intValue() : 0;
    }
    catch (NullPointerException localNullPointerException)
    {
      i = 0;
    }
    catch (ClassCastException localClassCastException)
    {
      i = 0;
    }
    return i;
  }
  
  private void recordTokenString(Vector paramVector)
  {
    int i = getTokenQueuePosFromMap(m_patternMapSize - 1);
    resetTokenMark(i + 1);
    if (m_processor.lookahead('(', 1))
    {
      int j = getKeywordToken(m_processor.m_token);
      switch (j)
      {
      case 1030: 
        paramVector.addElement("#comment");
        break;
      case 1031: 
        paramVector.addElement("#text");
        break;
      case 1033: 
        paramVector.addElement("*");
        break;
      case 35: 
        paramVector.addElement("/");
        break;
      case 36: 
        paramVector.addElement("*");
        break;
      case 1032: 
        paramVector.addElement("*");
        break;
      default: 
        paramVector.addElement("*");
      }
    }
    else
    {
      if (m_processor.tokenIs('@'))
      {
        i++;
        resetTokenMark(i + 1);
      }
      if (m_processor.lookahead(':', 1)) {
        i += 2;
      }
      paramVector.addElement(m_compiler.getTokenQueue().elementAt(i));
    }
  }
  
  private final void addToTokenQueue(String paramString)
  {
    m_compiler.getTokenQueue().addElement(paramString);
  }
  
  private int mapNSTokens(String paramString, int paramInt1, int paramInt2, int paramInt3)
    throws TransformerException
  {
    String str1 = "";
    if ((paramInt1 >= 0) && (paramInt2 >= 0)) {
      str1 = paramString.substring(paramInt1, paramInt2);
    }
    String str2;
    if ((null != m_namespaceContext) && (!str1.equals("*")) && (!str1.equals("xmlns"))) {
      try
      {
        if (str1.length() > 0) {
          str2 = m_namespaceContext.getNamespaceForPrefix(str1);
        } else {
          str2 = m_namespaceContext.getNamespaceForPrefix(str1);
        }
      }
      catch (ClassCastException localClassCastException)
      {
        str2 = m_namespaceContext.getNamespaceForPrefix(str1);
      }
    } else {
      str2 = str1;
    }
    if ((null != str2) && (str2.length() > 0))
    {
      addToTokenQueue(str2);
      addToTokenQueue(":");
      String str3 = paramString.substring(paramInt2 + 1, paramInt3);
      if (str3.length() > 0) {
        addToTokenQueue(str3);
      }
    }
    else
    {
      m_processor.errorForDOM3("ER_PREFIX_MUST_RESOLVE", new String[] { str1 });
    }
    return -1;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xpath\internal\compiler\Lexer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */