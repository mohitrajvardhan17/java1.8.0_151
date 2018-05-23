package com.sun.org.apache.xpath.internal.compiler;

import com.sun.org.apache.xalan.internal.res.XSLMessages;
import com.sun.org.apache.xml.internal.utils.ObjectVector;
import com.sun.org.apache.xml.internal.utils.PrefixResolver;
import com.sun.org.apache.xpath.internal.XPathProcessorException;
import com.sun.org.apache.xpath.internal.domapi.XPathStylesheetDOM3Exception;
import com.sun.org.apache.xpath.internal.objects.XNumber;
import com.sun.org.apache.xpath.internal.objects.XString;
import java.io.PrintStream;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;

public class XPathParser
{
  public static final String CONTINUE_AFTER_FATAL_ERROR = "CONTINUE_AFTER_FATAL_ERROR";
  private OpMap m_ops;
  transient String m_token;
  transient char m_tokenChar = '\000';
  int m_queueMark = 0;
  protected static final int FILTER_MATCH_FAILED = 0;
  protected static final int FILTER_MATCH_PRIMARY = 1;
  protected static final int FILTER_MATCH_PREDICATES = 2;
  PrefixResolver m_namespaceContext;
  private ErrorListener m_errorListener;
  SourceLocator m_sourceLocator;
  private FunctionTable m_functionTable;
  
  public XPathParser(ErrorListener paramErrorListener, SourceLocator paramSourceLocator)
  {
    m_errorListener = paramErrorListener;
    m_sourceLocator = paramSourceLocator;
  }
  
  public void initXPath(Compiler paramCompiler, String paramString, PrefixResolver paramPrefixResolver)
    throws TransformerException
  {
    m_ops = paramCompiler;
    m_namespaceContext = paramPrefixResolver;
    m_functionTable = paramCompiler.getFunctionTable();
    Lexer localLexer = new Lexer(paramCompiler, paramPrefixResolver, this);
    localLexer.tokenize(paramString);
    m_ops.setOp(0, 1);
    m_ops.setOp(1, 2);
    try
    {
      nextToken();
      Expr();
      if (null != m_token)
      {
        String str = "";
        while (null != m_token)
        {
          str = str + "'" + m_token + "'";
          nextToken();
          if (null != m_token) {
            str = str + ", ";
          }
        }
        error("ER_EXTRA_ILLEGAL_TOKENS", new Object[] { str });
      }
    }
    catch (XPathProcessorException localXPathProcessorException)
    {
      if ("CONTINUE_AFTER_FATAL_ERROR".equals(localXPathProcessorException.getMessage())) {
        initXPath(paramCompiler, "/..", paramPrefixResolver);
      } else {
        throw localXPathProcessorException;
      }
    }
    paramCompiler.shrink();
  }
  
  public void initMatchPattern(Compiler paramCompiler, String paramString, PrefixResolver paramPrefixResolver)
    throws TransformerException
  {
    m_ops = paramCompiler;
    m_namespaceContext = paramPrefixResolver;
    m_functionTable = paramCompiler.getFunctionTable();
    Lexer localLexer = new Lexer(paramCompiler, paramPrefixResolver, this);
    localLexer.tokenize(paramString);
    m_ops.setOp(0, 30);
    m_ops.setOp(1, 2);
    nextToken();
    Pattern();
    if (null != m_token)
    {
      String str = "";
      while (null != m_token)
      {
        str = str + "'" + m_token + "'";
        nextToken();
        if (null != m_token) {
          str = str + ", ";
        }
      }
      error("ER_EXTRA_ILLEGAL_TOKENS", new Object[] { str });
    }
    m_ops.setOp(m_ops.getOp(1), -1);
    m_ops.setOp(1, m_ops.getOp(1) + 1);
    m_ops.shrink();
  }
  
  public void setErrorHandler(ErrorListener paramErrorListener)
  {
    m_errorListener = paramErrorListener;
  }
  
  public ErrorListener getErrorListener()
  {
    return m_errorListener;
  }
  
  final boolean tokenIs(String paramString)
  {
    return paramString == null ? true : m_token != null ? m_token.equals(paramString) : false;
  }
  
  final boolean tokenIs(char paramChar)
  {
    return m_tokenChar == paramChar;
  }
  
  final boolean lookahead(char paramChar, int paramInt)
  {
    int i = m_queueMark + paramInt;
    boolean bool;
    if ((i <= m_ops.getTokenQueueSize()) && (i > 0) && (m_ops.getTokenQueueSize() != 0))
    {
      String str = (String)m_ops.m_tokenQueue.elementAt(i - 1);
      bool = str.charAt(0) == paramChar;
    }
    else
    {
      bool = false;
    }
    return bool;
  }
  
  private final boolean lookbehind(char paramChar, int paramInt)
  {
    int i = m_queueMark - (paramInt + 1);
    boolean bool;
    if (i >= 0)
    {
      String str = (String)m_ops.m_tokenQueue.elementAt(i);
      if (str.length() == 1)
      {
        char c = str == null ? '|' : str.charAt(0);
        bool = c != '|';
      }
      else
      {
        bool = false;
      }
    }
    else
    {
      bool = false;
    }
    return bool;
  }
  
  private final boolean lookbehindHasToken(int paramInt)
  {
    boolean bool;
    if (m_queueMark - paramInt > 0)
    {
      String str = (String)m_ops.m_tokenQueue.elementAt(m_queueMark - (paramInt - 1));
      int i = str == null ? '|' : str.charAt(0);
      bool = i != 124;
    }
    else
    {
      bool = false;
    }
    return bool;
  }
  
  private final boolean lookahead(String paramString, int paramInt)
  {
    boolean bool;
    if (m_queueMark + paramInt <= m_ops.getTokenQueueSize())
    {
      String str = (String)m_ops.m_tokenQueue.elementAt(m_queueMark + (paramInt - 1));
      bool = paramString == null ? true : str != null ? str.equals(paramString) : false;
    }
    else
    {
      bool = null == paramString;
    }
    return bool;
  }
  
  private final void nextToken()
  {
    if (m_queueMark < m_ops.getTokenQueueSize())
    {
      m_token = ((String)m_ops.m_tokenQueue.elementAt(m_queueMark++));
      m_tokenChar = m_token.charAt(0);
    }
    else
    {
      m_token = null;
      m_tokenChar = '\000';
    }
  }
  
  private final String getTokenRelative(int paramInt)
  {
    int i = m_queueMark + paramInt;
    String str;
    if ((i > 0) && (i < m_ops.getTokenQueueSize())) {
      str = (String)m_ops.m_tokenQueue.elementAt(i);
    } else {
      str = null;
    }
    return str;
  }
  
  private final void prevToken()
  {
    if (m_queueMark > 0)
    {
      m_queueMark -= 1;
      m_token = ((String)m_ops.m_tokenQueue.elementAt(m_queueMark));
      m_tokenChar = m_token.charAt(0);
    }
    else
    {
      m_token = null;
      m_tokenChar = '\000';
    }
  }
  
  private final void consumeExpected(String paramString)
    throws TransformerException
  {
    if (tokenIs(paramString))
    {
      nextToken();
    }
    else
    {
      error("ER_EXPECTED_BUT_FOUND", new Object[] { paramString, m_token });
      throw new XPathProcessorException("CONTINUE_AFTER_FATAL_ERROR");
    }
  }
  
  private final void consumeExpected(char paramChar)
    throws TransformerException
  {
    if (tokenIs(paramChar))
    {
      nextToken();
    }
    else
    {
      error("ER_EXPECTED_BUT_FOUND", new Object[] { String.valueOf(paramChar), m_token });
      throw new XPathProcessorException("CONTINUE_AFTER_FATAL_ERROR");
    }
  }
  
  void warn(String paramString, Object[] paramArrayOfObject)
    throws TransformerException
  {
    String str = XSLMessages.createXPATHWarning(paramString, paramArrayOfObject);
    ErrorListener localErrorListener = getErrorListener();
    if (null != localErrorListener) {
      localErrorListener.warning(new TransformerException(str, m_sourceLocator));
    } else {
      System.err.println(str);
    }
  }
  
  private void assertion(boolean paramBoolean, String paramString)
  {
    if (!paramBoolean)
    {
      String str = XSLMessages.createXPATHMessage("ER_INCORRECT_PROGRAMMER_ASSERTION", new Object[] { paramString });
      throw new RuntimeException(str);
    }
  }
  
  void error(String paramString, Object[] paramArrayOfObject)
    throws TransformerException
  {
    String str = XSLMessages.createXPATHMessage(paramString, paramArrayOfObject);
    ErrorListener localErrorListener = getErrorListener();
    TransformerException localTransformerException = new TransformerException(str, m_sourceLocator);
    if (null != localErrorListener) {
      localErrorListener.fatalError(localTransformerException);
    } else {
      throw localTransformerException;
    }
  }
  
  void errorForDOM3(String paramString, Object[] paramArrayOfObject)
    throws TransformerException
  {
    String str = XSLMessages.createXPATHMessage(paramString, paramArrayOfObject);
    ErrorListener localErrorListener = getErrorListener();
    XPathStylesheetDOM3Exception localXPathStylesheetDOM3Exception = new XPathStylesheetDOM3Exception(str, m_sourceLocator);
    if (null != localErrorListener) {
      localErrorListener.fatalError(localXPathStylesheetDOM3Exception);
    } else {
      throw localXPathStylesheetDOM3Exception;
    }
  }
  
  protected String dumpRemainingTokenQueue()
  {
    int i = m_queueMark;
    String str1;
    if (i < m_ops.getTokenQueueSize())
    {
      String str3;
      for (String str2 = "\n Remaining tokens: ("; i < m_ops.getTokenQueueSize(); str2 = str2 + " '" + str3 + "'") {
        str3 = (String)m_ops.m_tokenQueue.elementAt(i++);
      }
      str1 = str2 + ")";
    }
    else
    {
      str1 = "";
    }
    return str1;
  }
  
  final int getFunctionToken(String paramString)
  {
    int i;
    try
    {
      Object localObject = Keywords.lookupNodeTest(paramString);
      if (null == localObject) {
        localObject = m_functionTable.getFunctionID(paramString);
      }
      i = ((Integer)localObject).intValue();
    }
    catch (NullPointerException localNullPointerException)
    {
      i = -1;
    }
    catch (ClassCastException localClassCastException)
    {
      i = -1;
    }
    return i;
  }
  
  void insertOp(int paramInt1, int paramInt2, int paramInt3)
  {
    int i = m_ops.getOp(1);
    for (int j = i - 1; j >= paramInt1; j--) {
      m_ops.setOp(j + paramInt2, m_ops.getOp(j));
    }
    m_ops.setOp(paramInt1, paramInt3);
    m_ops.setOp(1, i + paramInt2);
  }
  
  void appendOp(int paramInt1, int paramInt2)
  {
    int i = m_ops.getOp(1);
    m_ops.setOp(i, paramInt2);
    m_ops.setOp(i + 1, paramInt1);
    m_ops.setOp(1, i + paramInt1);
  }
  
  protected void Expr()
    throws TransformerException
  {
    OrExpr();
  }
  
  protected void OrExpr()
    throws TransformerException
  {
    int i = m_ops.getOp(1);
    AndExpr();
    if ((null != m_token) && (tokenIs("or")))
    {
      nextToken();
      insertOp(i, 2, 2);
      OrExpr();
      m_ops.setOp(i + 1, m_ops.getOp(1) - i);
    }
  }
  
  protected void AndExpr()
    throws TransformerException
  {
    int i = m_ops.getOp(1);
    EqualityExpr(-1);
    if ((null != m_token) && (tokenIs("and")))
    {
      nextToken();
      insertOp(i, 2, 3);
      AndExpr();
      m_ops.setOp(i + 1, m_ops.getOp(1) - i);
    }
  }
  
  protected int EqualityExpr(int paramInt)
    throws TransformerException
  {
    int i = m_ops.getOp(1);
    if (-1 == paramInt) {
      paramInt = i;
    }
    RelationalExpr(-1);
    if (null != m_token)
    {
      int j;
      if ((tokenIs('!')) && (lookahead('=', 1)))
      {
        nextToken();
        nextToken();
        insertOp(paramInt, 2, 4);
        j = m_ops.getOp(1) - paramInt;
        paramInt = EqualityExpr(paramInt);
        m_ops.setOp(paramInt + 1, m_ops.getOp(paramInt + j + 1) + j);
        paramInt += 2;
      }
      else if (tokenIs('='))
      {
        nextToken();
        insertOp(paramInt, 2, 5);
        j = m_ops.getOp(1) - paramInt;
        paramInt = EqualityExpr(paramInt);
        m_ops.setOp(paramInt + 1, m_ops.getOp(paramInt + j + 1) + j);
        paramInt += 2;
      }
    }
    return paramInt;
  }
  
  protected int RelationalExpr(int paramInt)
    throws TransformerException
  {
    int i = m_ops.getOp(1);
    if (-1 == paramInt) {
      paramInt = i;
    }
    AdditiveExpr(-1);
    if (null != m_token)
    {
      int j;
      if (tokenIs('<'))
      {
        nextToken();
        if (tokenIs('='))
        {
          nextToken();
          insertOp(paramInt, 2, 6);
        }
        else
        {
          insertOp(paramInt, 2, 7);
        }
        j = m_ops.getOp(1) - paramInt;
        paramInt = RelationalExpr(paramInt);
        m_ops.setOp(paramInt + 1, m_ops.getOp(paramInt + j + 1) + j);
        paramInt += 2;
      }
      else if (tokenIs('>'))
      {
        nextToken();
        if (tokenIs('='))
        {
          nextToken();
          insertOp(paramInt, 2, 8);
        }
        else
        {
          insertOp(paramInt, 2, 9);
        }
        j = m_ops.getOp(1) - paramInt;
        paramInt = RelationalExpr(paramInt);
        m_ops.setOp(paramInt + 1, m_ops.getOp(paramInt + j + 1) + j);
        paramInt += 2;
      }
    }
    return paramInt;
  }
  
  protected int AdditiveExpr(int paramInt)
    throws TransformerException
  {
    int i = m_ops.getOp(1);
    if (-1 == paramInt) {
      paramInt = i;
    }
    MultiplicativeExpr(-1);
    if (null != m_token)
    {
      int j;
      if (tokenIs('+'))
      {
        nextToken();
        insertOp(paramInt, 2, 10);
        j = m_ops.getOp(1) - paramInt;
        paramInt = AdditiveExpr(paramInt);
        m_ops.setOp(paramInt + 1, m_ops.getOp(paramInt + j + 1) + j);
        paramInt += 2;
      }
      else if (tokenIs('-'))
      {
        nextToken();
        insertOp(paramInt, 2, 11);
        j = m_ops.getOp(1) - paramInt;
        paramInt = AdditiveExpr(paramInt);
        m_ops.setOp(paramInt + 1, m_ops.getOp(paramInt + j + 1) + j);
        paramInt += 2;
      }
    }
    return paramInt;
  }
  
  protected int MultiplicativeExpr(int paramInt)
    throws TransformerException
  {
    int i = m_ops.getOp(1);
    if (-1 == paramInt) {
      paramInt = i;
    }
    UnaryExpr();
    if (null != m_token)
    {
      int j;
      if (tokenIs('*'))
      {
        nextToken();
        insertOp(paramInt, 2, 12);
        j = m_ops.getOp(1) - paramInt;
        paramInt = MultiplicativeExpr(paramInt);
        m_ops.setOp(paramInt + 1, m_ops.getOp(paramInt + j + 1) + j);
        paramInt += 2;
      }
      else if (tokenIs("div"))
      {
        nextToken();
        insertOp(paramInt, 2, 13);
        j = m_ops.getOp(1) - paramInt;
        paramInt = MultiplicativeExpr(paramInt);
        m_ops.setOp(paramInt + 1, m_ops.getOp(paramInt + j + 1) + j);
        paramInt += 2;
      }
      else if (tokenIs("mod"))
      {
        nextToken();
        insertOp(paramInt, 2, 14);
        j = m_ops.getOp(1) - paramInt;
        paramInt = MultiplicativeExpr(paramInt);
        m_ops.setOp(paramInt + 1, m_ops.getOp(paramInt + j + 1) + j);
        paramInt += 2;
      }
      else if (tokenIs("quo"))
      {
        nextToken();
        insertOp(paramInt, 2, 15);
        j = m_ops.getOp(1) - paramInt;
        paramInt = MultiplicativeExpr(paramInt);
        m_ops.setOp(paramInt + 1, m_ops.getOp(paramInt + j + 1) + j);
        paramInt += 2;
      }
    }
    return paramInt;
  }
  
  protected void UnaryExpr()
    throws TransformerException
  {
    int i = m_ops.getOp(1);
    int j = 0;
    if (m_tokenChar == '-')
    {
      nextToken();
      appendOp(2, 16);
      j = 1;
    }
    UnionExpr();
    if (j != 0) {
      m_ops.setOp(i + 1, m_ops.getOp(1) - i);
    }
  }
  
  protected void StringExpr()
    throws TransformerException
  {
    int i = m_ops.getOp(1);
    appendOp(2, 17);
    Expr();
    m_ops.setOp(i + 1, m_ops.getOp(1) - i);
  }
  
  protected void BooleanExpr()
    throws TransformerException
  {
    int i = m_ops.getOp(1);
    appendOp(2, 18);
    Expr();
    int j = m_ops.getOp(1) - i;
    if (j == 2) {
      error("ER_BOOLEAN_ARG_NO_LONGER_OPTIONAL", null);
    }
    m_ops.setOp(i + 1, j);
  }
  
  protected void NumberExpr()
    throws TransformerException
  {
    int i = m_ops.getOp(1);
    appendOp(2, 19);
    Expr();
    m_ops.setOp(i + 1, m_ops.getOp(1) - i);
  }
  
  protected void UnionExpr()
    throws TransformerException
  {
    int i = m_ops.getOp(1);
    int j = 1;
    int k = 0;
    do
    {
      PathExpr();
      if (!tokenIs('|')) {
        break;
      }
      if (0 == k)
      {
        k = 1;
        insertOp(i, 2, 20);
      }
      nextToken();
    } while (j != 0);
    m_ops.setOp(i + 1, m_ops.getOp(1) - i);
  }
  
  protected void PathExpr()
    throws TransformerException
  {
    int i = m_ops.getOp(1);
    int j = FilterExpr();
    if (j != 0)
    {
      int k = j == 2 ? 1 : 0;
      if (tokenIs('/'))
      {
        nextToken();
        if (k == 0)
        {
          insertOp(i, 2, 28);
          k = 1;
        }
        if (!RelativeLocationPath()) {
          error("ER_EXPECTED_REL_LOC_PATH", null);
        }
      }
      if (k != 0)
      {
        m_ops.setOp(m_ops.getOp(1), -1);
        m_ops.setOp(1, m_ops.getOp(1) + 1);
        m_ops.setOp(i + 1, m_ops.getOp(1) - i);
      }
    }
    else
    {
      LocationPath();
    }
  }
  
  protected int FilterExpr()
    throws TransformerException
  {
    int i = m_ops.getOp(1);
    int j;
    if (PrimaryExpr())
    {
      if (tokenIs('['))
      {
        insertOp(i, 2, 28);
        while (tokenIs('[')) {
          Predicate();
        }
        j = 2;
      }
      else
      {
        j = 1;
      }
    }
    else {
      j = 0;
    }
    return j;
  }
  
  protected boolean PrimaryExpr()
    throws TransformerException
  {
    int i = m_ops.getOp(1);
    boolean bool;
    if ((m_tokenChar == '\'') || (m_tokenChar == '"'))
    {
      appendOp(2, 21);
      Literal();
      m_ops.setOp(i + 1, m_ops.getOp(1) - i);
      bool = true;
    }
    else if (m_tokenChar == '$')
    {
      nextToken();
      appendOp(2, 22);
      QName();
      m_ops.setOp(i + 1, m_ops.getOp(1) - i);
      bool = true;
    }
    else if (m_tokenChar == '(')
    {
      nextToken();
      appendOp(2, 23);
      Expr();
      consumeExpected(')');
      m_ops.setOp(i + 1, m_ops.getOp(1) - i);
      bool = true;
    }
    else if ((null != m_token) && ((('.' == m_tokenChar) && (m_token.length() > 1) && (Character.isDigit(m_token.charAt(1)))) || (Character.isDigit(m_tokenChar))))
    {
      appendOp(2, 27);
      Number();
      m_ops.setOp(i + 1, m_ops.getOp(1) - i);
      bool = true;
    }
    else if ((lookahead('(', 1)) || ((lookahead(':', 1)) && (lookahead('(', 3))))
    {
      bool = FunctionCall();
    }
    else
    {
      bool = false;
    }
    return bool;
  }
  
  protected void Argument()
    throws TransformerException
  {
    int i = m_ops.getOp(1);
    appendOp(2, 26);
    Expr();
    m_ops.setOp(i + 1, m_ops.getOp(1) - i);
  }
  
  protected boolean FunctionCall()
    throws TransformerException
  {
    int i = m_ops.getOp(1);
    if (lookahead(':', 1))
    {
      appendOp(4, 24);
      m_ops.setOp(i + 1 + 1, m_queueMark - 1);
      nextToken();
      consumeExpected(':');
      m_ops.setOp(i + 1 + 2, m_queueMark - 1);
      nextToken();
    }
    else
    {
      int j = getFunctionToken(m_token);
      if (-1 == j) {
        error("ER_COULDNOT_FIND_FUNCTION", new Object[] { m_token });
      }
      switch (j)
      {
      case 1030: 
      case 1031: 
      case 1032: 
      case 1033: 
        return false;
      }
      appendOp(3, 25);
      m_ops.setOp(i + 1 + 1, j);
      nextToken();
    }
    consumeExpected('(');
    while ((!tokenIs(')')) && (m_token != null))
    {
      if (tokenIs(',')) {
        error("ER_FOUND_COMMA_BUT_NO_PRECEDING_ARG", null);
      }
      Argument();
      if (!tokenIs(')'))
      {
        consumeExpected(',');
        if (tokenIs(')')) {
          error("ER_FOUND_COMMA_BUT_NO_FOLLOWING_ARG", null);
        }
      }
    }
    consumeExpected(')');
    m_ops.setOp(m_ops.getOp(1), -1);
    m_ops.setOp(1, m_ops.getOp(1) + 1);
    m_ops.setOp(i + 1, m_ops.getOp(1) - i);
    return true;
  }
  
  protected void LocationPath()
    throws TransformerException
  {
    int i = m_ops.getOp(1);
    appendOp(2, 28);
    boolean bool = tokenIs('/');
    if (bool)
    {
      appendOp(4, 50);
      m_ops.setOp(m_ops.getOp(1) - 2, 4);
      m_ops.setOp(m_ops.getOp(1) - 1, 35);
      nextToken();
    }
    else if (m_token == null)
    {
      error("ER_EXPECTED_LOC_PATH_AT_END_EXPR", null);
    }
    if ((m_token != null) && (!RelativeLocationPath()) && (!bool)) {
      error("ER_EXPECTED_LOC_PATH", new Object[] { m_token });
    }
    m_ops.setOp(m_ops.getOp(1), -1);
    m_ops.setOp(1, m_ops.getOp(1) + 1);
    m_ops.setOp(i + 1, m_ops.getOp(1) - i);
  }
  
  protected boolean RelativeLocationPath()
    throws TransformerException
  {
    if (!Step()) {
      return false;
    }
    while (tokenIs('/'))
    {
      nextToken();
      if (!Step()) {
        error("ER_EXPECTED_LOC_STEP", null);
      }
    }
    return true;
  }
  
  protected boolean Step()
    throws TransformerException
  {
    int i = m_ops.getOp(1);
    boolean bool = tokenIs('/');
    if (bool)
    {
      nextToken();
      appendOp(2, 42);
      m_ops.setOp(1, m_ops.getOp(1) + 1);
      m_ops.setOp(m_ops.getOp(1), 1033);
      m_ops.setOp(1, m_ops.getOp(1) + 1);
      m_ops.setOp(i + 1 + 1, m_ops.getOp(1) - i);
      m_ops.setOp(i + 1, m_ops.getOp(1) - i);
      i = m_ops.getOp(1);
    }
    if (tokenIs("."))
    {
      nextToken();
      if (tokenIs('[')) {
        error("ER_PREDICATE_ILLEGAL_SYNTAX", null);
      }
      appendOp(4, 48);
      m_ops.setOp(m_ops.getOp(1) - 2, 4);
      m_ops.setOp(m_ops.getOp(1) - 1, 1033);
    }
    else if (tokenIs(".."))
    {
      nextToken();
      appendOp(4, 45);
      m_ops.setOp(m_ops.getOp(1) - 2, 4);
      m_ops.setOp(m_ops.getOp(1) - 1, 1033);
    }
    else if ((tokenIs('*')) || (tokenIs('@')) || (tokenIs('_')) || ((m_token != null) && (Character.isLetter(m_token.charAt(0)))))
    {
      Basis();
      while (tokenIs('[')) {
        Predicate();
      }
      m_ops.setOp(i + 1, m_ops.getOp(1) - i);
    }
    else
    {
      if (bool) {
        error("ER_EXPECTED_LOC_STEP", null);
      }
      return false;
    }
    return true;
  }
  
  protected void Basis()
    throws TransformerException
  {
    int i = m_ops.getOp(1);
    int j;
    if (lookahead("::", 1))
    {
      j = AxisName();
      nextToken();
      nextToken();
    }
    else if (tokenIs('@'))
    {
      j = 39;
      appendOp(2, j);
      nextToken();
    }
    else
    {
      j = 40;
      appendOp(2, j);
    }
    m_ops.setOp(1, m_ops.getOp(1) + 1);
    NodeTest(j);
    m_ops.setOp(i + 1 + 1, m_ops.getOp(1) - i);
  }
  
  protected int AxisName()
    throws TransformerException
  {
    Integer localInteger = Keywords.getAxisName(m_token);
    if (null == localInteger) {
      error("ER_ILLEGAL_AXIS_NAME", new Object[] { m_token });
    }
    int i = ((Integer)localInteger).intValue();
    appendOp(2, i);
    return i;
  }
  
  protected void NodeTest(int paramInt)
    throws TransformerException
  {
    if (lookahead('(', 1))
    {
      Integer localInteger = Keywords.getNodeType(m_token);
      if (null == localInteger)
      {
        error("ER_UNKNOWN_NODETYPE", new Object[] { m_token });
      }
      else
      {
        nextToken();
        int i = ((Integer)localInteger).intValue();
        m_ops.setOp(m_ops.getOp(1), i);
        m_ops.setOp(1, m_ops.getOp(1) + 1);
        consumeExpected('(');
        if ((1032 == i) && (!tokenIs(')'))) {
          Literal();
        }
        consumeExpected(')');
      }
    }
    else
    {
      m_ops.setOp(m_ops.getOp(1), 34);
      m_ops.setOp(1, m_ops.getOp(1) + 1);
      if (lookahead(':', 1))
      {
        if (tokenIs('*'))
        {
          m_ops.setOp(m_ops.getOp(1), -3);
        }
        else
        {
          m_ops.setOp(m_ops.getOp(1), m_queueMark - 1);
          if ((!Character.isLetter(m_tokenChar)) && (!tokenIs('_'))) {
            error("ER_EXPECTED_NODE_TEST", null);
          }
        }
        nextToken();
        consumeExpected(':');
      }
      else
      {
        m_ops.setOp(m_ops.getOp(1), -2);
      }
      m_ops.setOp(1, m_ops.getOp(1) + 1);
      if (tokenIs('*'))
      {
        m_ops.setOp(m_ops.getOp(1), -3);
      }
      else
      {
        m_ops.setOp(m_ops.getOp(1), m_queueMark - 1);
        if ((!Character.isLetter(m_tokenChar)) && (!tokenIs('_'))) {
          error("ER_EXPECTED_NODE_TEST", null);
        }
      }
      m_ops.setOp(1, m_ops.getOp(1) + 1);
      nextToken();
    }
  }
  
  protected void Predicate()
    throws TransformerException
  {
    if (tokenIs('['))
    {
      nextToken();
      PredicateExpr();
      consumeExpected(']');
    }
  }
  
  protected void PredicateExpr()
    throws TransformerException
  {
    int i = m_ops.getOp(1);
    appendOp(2, 29);
    Expr();
    m_ops.setOp(m_ops.getOp(1), -1);
    m_ops.setOp(1, m_ops.getOp(1) + 1);
    m_ops.setOp(i + 1, m_ops.getOp(1) - i);
  }
  
  protected void QName()
    throws TransformerException
  {
    if (lookahead(':', 1))
    {
      m_ops.setOp(m_ops.getOp(1), m_queueMark - 1);
      m_ops.setOp(1, m_ops.getOp(1) + 1);
      nextToken();
      consumeExpected(':');
    }
    else
    {
      m_ops.setOp(m_ops.getOp(1), -2);
      m_ops.setOp(1, m_ops.getOp(1) + 1);
    }
    m_ops.setOp(m_ops.getOp(1), m_queueMark - 1);
    m_ops.setOp(1, m_ops.getOp(1) + 1);
    nextToken();
  }
  
  protected void NCName()
  {
    m_ops.setOp(m_ops.getOp(1), m_queueMark - 1);
    m_ops.setOp(1, m_ops.getOp(1) + 1);
    nextToken();
  }
  
  protected void Literal()
    throws TransformerException
  {
    int i = m_token.length() - 1;
    int j = m_tokenChar;
    int k = m_token.charAt(i);
    if (((j == 34) && (k == 34)) || ((j == 39) && (k == 39)))
    {
      int m = m_queueMark - 1;
      m_ops.m_tokenQueue.setElementAt(null, m);
      XString localXString = new XString(m_token.substring(1, i));
      m_ops.m_tokenQueue.setElementAt(localXString, m);
      m_ops.setOp(m_ops.getOp(1), m);
      m_ops.setOp(1, m_ops.getOp(1) + 1);
      nextToken();
    }
    else
    {
      error("ER_PATTERN_LITERAL_NEEDS_BE_QUOTED", new Object[] { m_token });
    }
  }
  
  protected void Number()
    throws TransformerException
  {
    if (null != m_token)
    {
      double d;
      try
      {
        if ((m_token.indexOf('e') > -1) || (m_token.indexOf('E') > -1)) {
          throw new NumberFormatException();
        }
        d = Double.valueOf(m_token).doubleValue();
      }
      catch (NumberFormatException localNumberFormatException)
      {
        d = 0.0D;
        error("ER_COULDNOT_BE_FORMATTED_TO_NUMBER", new Object[] { m_token });
      }
      m_ops.m_tokenQueue.setElementAt(new XNumber(d), m_queueMark - 1);
      m_ops.setOp(m_ops.getOp(1), m_queueMark - 1);
      m_ops.setOp(1, m_ops.getOp(1) + 1);
      nextToken();
    }
  }
  
  protected void Pattern()
    throws TransformerException
  {
    for (;;)
    {
      LocationPathPattern();
      if (!tokenIs('|')) {
        break;
      }
      nextToken();
    }
  }
  
  protected void LocationPathPattern()
    throws TransformerException
  {
    int i = m_ops.getOp(1);
    int j = 0;
    int k = 1;
    int m = 2;
    int n = 0;
    appendOp(2, 31);
    if ((lookahead('(', 1)) && ((tokenIs("id")) || (tokenIs("key"))))
    {
      IdKeyPattern();
      if (tokenIs('/'))
      {
        nextToken();
        if (tokenIs('/'))
        {
          appendOp(4, 52);
          nextToken();
        }
        else
        {
          appendOp(4, 53);
        }
        m_ops.setOp(m_ops.getOp(1) - 2, 4);
        m_ops.setOp(m_ops.getOp(1) - 1, 1034);
        n = 2;
      }
    }
    else if (tokenIs('/'))
    {
      if (lookahead('/', 1))
      {
        appendOp(4, 52);
        nextToken();
        n = 2;
      }
      else
      {
        appendOp(4, 50);
        n = 1;
      }
      m_ops.setOp(m_ops.getOp(1) - 2, 4);
      m_ops.setOp(m_ops.getOp(1) - 1, 35);
      nextToken();
    }
    else
    {
      n = 2;
    }
    if (n != 0) {
      if ((!tokenIs('|')) && (null != m_token)) {
        RelativePathPattern();
      } else if (n == 2) {
        error("ER_EXPECTED_REL_PATH_PATTERN", null);
      }
    }
    m_ops.setOp(m_ops.getOp(1), -1);
    m_ops.setOp(1, m_ops.getOp(1) + 1);
    m_ops.setOp(i + 1, m_ops.getOp(1) - i);
  }
  
  protected void IdKeyPattern()
    throws TransformerException
  {
    FunctionCall();
  }
  
  protected void RelativePathPattern()
    throws TransformerException
  {
    for (boolean bool = StepPattern(false); tokenIs('/'); bool = StepPattern(!bool)) {
      nextToken();
    }
  }
  
  protected boolean StepPattern(boolean paramBoolean)
    throws TransformerException
  {
    return AbbreviatedNodeTestStep(paramBoolean);
  }
  
  protected boolean AbbreviatedNodeTestStep(boolean paramBoolean)
    throws TransformerException
  {
    int i = m_ops.getOp(1);
    int k = -1;
    int j;
    if (tokenIs('@'))
    {
      j = 51;
      appendOp(2, j);
      nextToken();
    }
    else if (lookahead("::", 1))
    {
      if (tokenIs("attribute"))
      {
        j = 51;
        appendOp(2, j);
      }
      else if (tokenIs("child"))
      {
        k = m_ops.getOp(1);
        j = 53;
        appendOp(2, j);
      }
      else
      {
        j = -1;
        error("ER_AXES_NOT_ALLOWED", new Object[] { m_token });
      }
      nextToken();
      nextToken();
    }
    else if (tokenIs('/'))
    {
      if (!paramBoolean) {
        error("ER_EXPECTED_STEP_PATTERN", null);
      }
      j = 52;
      appendOp(2, j);
      nextToken();
    }
    else
    {
      k = m_ops.getOp(1);
      j = 53;
      appendOp(2, j);
    }
    m_ops.setOp(1, m_ops.getOp(1) + 1);
    NodeTest(j);
    m_ops.setOp(i + 1 + 1, m_ops.getOp(1) - i);
    while (tokenIs('[')) {
      Predicate();
    }
    boolean bool;
    if ((k > -1) && (tokenIs('/')) && (lookahead('/', 1)))
    {
      m_ops.setOp(k, 52);
      nextToken();
      bool = true;
    }
    else
    {
      bool = false;
    }
    m_ops.setOp(i + 1, m_ops.getOp(1) - i);
    return bool;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xpath\internal\compiler\XPathParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */