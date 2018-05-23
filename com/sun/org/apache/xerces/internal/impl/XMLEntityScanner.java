package com.sun.org.apache.xerces.internal.impl;

import com.sun.org.apache.xerces.internal.impl.io.ASCIIReader;
import com.sun.org.apache.xerces.internal.impl.io.UCSReader;
import com.sun.org.apache.xerces.internal.impl.io.UTF8Reader;
import com.sun.org.apache.xerces.internal.util.EncodingMap;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.util.XMLChar;
import com.sun.org.apache.xerces.internal.util.XMLStringBuffer;
import com.sun.org.apache.xerces.internal.utils.XMLLimitAnalyzer;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityManager;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityManager.Limit;
import com.sun.org.apache.xerces.internal.xni.QName;
import com.sun.org.apache.xerces.internal.xni.XMLLocator;
import com.sun.org.apache.xerces.internal.xni.XMLResourceIdentifier;
import com.sun.org.apache.xerces.internal.xni.XMLString;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponentManager;
import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;
import com.sun.xml.internal.stream.Entity.ScannedEntity;
import com.sun.xml.internal.stream.XMLBufferListener;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Locale;

public class XMLEntityScanner
  implements XMLLocator
{
  protected Entity.ScannedEntity fCurrentEntity = null;
  protected int fBufferSize = 8192;
  protected XMLEntityManager fEntityManager;
  protected XMLSecurityManager fSecurityManager = null;
  protected XMLLimitAnalyzer fLimitAnalyzer = null;
  private static final boolean DEBUG_ENCODINGS = false;
  private ArrayList<XMLBufferListener> listeners = new ArrayList();
  private static final boolean[] VALID_NAMES = new boolean[127];
  private static final boolean DEBUG_BUFFER = false;
  private static final boolean DEBUG_SKIP_STRING = false;
  private static final EOFException END_OF_DOCUMENT_ENTITY = new EOFException()
  {
    private static final long serialVersionUID = 980337771224675268L;
    
    public Throwable fillInStackTrace()
    {
      return this;
    }
  };
  protected SymbolTable fSymbolTable = null;
  protected XMLErrorReporter fErrorReporter = null;
  int[] whiteSpaceLookup = new int[100];
  int whiteSpaceLen = 0;
  boolean whiteSpaceInfoNeeded = true;
  protected boolean fAllowJavaEncodings;
  protected static final String SYMBOL_TABLE = "http://apache.org/xml/properties/internal/symbol-table";
  protected static final String ERROR_REPORTER = "http://apache.org/xml/properties/internal/error-reporter";
  protected static final String ALLOW_JAVA_ENCODINGS = "http://apache.org/xml/features/allow-java-encodings";
  protected PropertyManager fPropertyManager = null;
  boolean isExternal = false;
  protected boolean xmlVersionSetExplicitly = false;
  boolean detectingVersion = false;
  
  public XMLEntityScanner() {}
  
  public XMLEntityScanner(PropertyManager paramPropertyManager, XMLEntityManager paramXMLEntityManager)
  {
    fEntityManager = paramXMLEntityManager;
    reset(paramPropertyManager);
  }
  
  public final void setBufferSize(int paramInt)
  {
    fBufferSize = paramInt;
  }
  
  public void reset(PropertyManager paramPropertyManager)
  {
    fSymbolTable = ((SymbolTable)paramPropertyManager.getProperty("http://apache.org/xml/properties/internal/symbol-table"));
    fErrorReporter = ((XMLErrorReporter)paramPropertyManager.getProperty("http://apache.org/xml/properties/internal/error-reporter"));
    resetCommon();
  }
  
  public void reset(XMLComponentManager paramXMLComponentManager)
    throws XMLConfigurationException
  {
    fAllowJavaEncodings = paramXMLComponentManager.getFeature("http://apache.org/xml/features/allow-java-encodings", false);
    fSymbolTable = ((SymbolTable)paramXMLComponentManager.getProperty("http://apache.org/xml/properties/internal/symbol-table"));
    fErrorReporter = ((XMLErrorReporter)paramXMLComponentManager.getProperty("http://apache.org/xml/properties/internal/error-reporter"));
    resetCommon();
  }
  
  public final void reset(SymbolTable paramSymbolTable, XMLEntityManager paramXMLEntityManager, XMLErrorReporter paramXMLErrorReporter)
  {
    fCurrentEntity = null;
    fSymbolTable = paramSymbolTable;
    fEntityManager = paramXMLEntityManager;
    fErrorReporter = paramXMLErrorReporter;
    fLimitAnalyzer = fEntityManager.fLimitAnalyzer;
    fSecurityManager = fEntityManager.fSecurityManager;
  }
  
  private void resetCommon()
  {
    fCurrentEntity = null;
    whiteSpaceLen = 0;
    whiteSpaceInfoNeeded = true;
    listeners.clear();
    fLimitAnalyzer = fEntityManager.fLimitAnalyzer;
    fSecurityManager = fEntityManager.fSecurityManager;
  }
  
  public final String getXMLVersion()
  {
    if (fCurrentEntity != null) {
      return fCurrentEntity.xmlVersion;
    }
    return null;
  }
  
  public final void setXMLVersion(String paramString)
  {
    xmlVersionSetExplicitly = true;
    fCurrentEntity.xmlVersion = paramString;
  }
  
  public final void setCurrentEntity(Entity.ScannedEntity paramScannedEntity)
  {
    fCurrentEntity = paramScannedEntity;
    if (fCurrentEntity != null) {
      isExternal = fCurrentEntity.isExternal();
    }
  }
  
  public Entity.ScannedEntity getCurrentEntity()
  {
    return fCurrentEntity;
  }
  
  public final String getBaseSystemId()
  {
    return (fCurrentEntity != null) && (fCurrentEntity.entityLocation != null) ? fCurrentEntity.entityLocation.getExpandedSystemId() : null;
  }
  
  public void setBaseSystemId(String paramString) {}
  
  public final int getLineNumber()
  {
    return fCurrentEntity != null ? fCurrentEntity.lineNumber : -1;
  }
  
  public void setLineNumber(int paramInt) {}
  
  public final int getColumnNumber()
  {
    return fCurrentEntity != null ? fCurrentEntity.columnNumber : -1;
  }
  
  public void setColumnNumber(int paramInt) {}
  
  public final int getCharacterOffset()
  {
    return fCurrentEntity != null ? fCurrentEntity.fTotalCountTillLastLoad + fCurrentEntity.position : -1;
  }
  
  public final String getExpandedSystemId()
  {
    return (fCurrentEntity != null) && (fCurrentEntity.entityLocation != null) ? fCurrentEntity.entityLocation.getExpandedSystemId() : null;
  }
  
  public void setExpandedSystemId(String paramString) {}
  
  public final String getLiteralSystemId()
  {
    return (fCurrentEntity != null) && (fCurrentEntity.entityLocation != null) ? fCurrentEntity.entityLocation.getLiteralSystemId() : null;
  }
  
  public void setLiteralSystemId(String paramString) {}
  
  public final String getPublicId()
  {
    return (fCurrentEntity != null) && (fCurrentEntity.entityLocation != null) ? fCurrentEntity.entityLocation.getPublicId() : null;
  }
  
  public void setPublicId(String paramString) {}
  
  public void setVersion(String paramString)
  {
    fCurrentEntity.version = paramString;
  }
  
  public String getVersion()
  {
    if (fCurrentEntity != null) {
      return fCurrentEntity.version;
    }
    return null;
  }
  
  public final String getEncoding()
  {
    if (fCurrentEntity != null) {
      return fCurrentEntity.encoding;
    }
    return null;
  }
  
  public final void setEncoding(String paramString)
    throws IOException
  {
    if ((fCurrentEntity.stream != null) && ((fCurrentEntity.encoding == null) || (!fCurrentEntity.encoding.equals(paramString))))
    {
      if ((fCurrentEntity.encoding != null) && (fCurrentEntity.encoding.startsWith("UTF-16")))
      {
        String str = paramString.toUpperCase(Locale.ENGLISH);
        if (str.equals("UTF-16")) {
          return;
        }
        if (str.equals("ISO-10646-UCS-4"))
        {
          if (fCurrentEntity.encoding.equals("UTF-16BE")) {
            fCurrentEntity.reader = new UCSReader(fCurrentEntity.stream, (short)8);
          } else {
            fCurrentEntity.reader = new UCSReader(fCurrentEntity.stream, (short)4);
          }
          return;
        }
        if (str.equals("ISO-10646-UCS-2"))
        {
          if (fCurrentEntity.encoding.equals("UTF-16BE")) {
            fCurrentEntity.reader = new UCSReader(fCurrentEntity.stream, (short)2);
          } else {
            fCurrentEntity.reader = new UCSReader(fCurrentEntity.stream, (short)1);
          }
          return;
        }
      }
      fCurrentEntity.reader = createReader(fCurrentEntity.stream, paramString, null);
      fCurrentEntity.encoding = paramString;
    }
  }
  
  public final boolean isExternal()
  {
    return fCurrentEntity.isExternal();
  }
  
  public int getChar(int paramInt)
    throws IOException
  {
    if (arrangeCapacity(paramInt + 1, false)) {
      return fCurrentEntity.ch[(fCurrentEntity.position + paramInt)];
    }
    return -1;
  }
  
  public int peekChar()
    throws IOException
  {
    if (fCurrentEntity.position == fCurrentEntity.count) {
      load(0, true, true);
    }
    int i = fCurrentEntity.ch[fCurrentEntity.position];
    if (isExternal) {
      return i != 13 ? i : 10;
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
    if ((j == 10) || ((j == 13) && (isExternal)))
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
      if ((j == 13) && (isExternal))
      {
        if (fCurrentEntity.ch[(fCurrentEntity.position++)] != '\n') {
          fCurrentEntity.position -= 1;
        }
        j = 10;
      }
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
    int j = 0;
    for (;;)
    {
      int k = fCurrentEntity.ch[fCurrentEntity.position];
      boolean bool;
      if (k < 127) {
        j = VALID_NAMES[k];
      } else {
        bool = XMLChar.isName(k);
      }
      if (!bool) {
        break;
      }
      if (++fCurrentEntity.position == fCurrentEntity.count)
      {
        m = fCurrentEntity.position - i;
        invokeListeners(m);
        if (m == fCurrentEntity.fBufferSize)
        {
          localObject = new char[fCurrentEntity.fBufferSize * 2];
          System.arraycopy(fCurrentEntity.ch, i, localObject, 0, m);
          fCurrentEntity.ch = ((char[])localObject);
          fCurrentEntity.fBufferSize *= 2;
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
    int m = fCurrentEntity.position - i;
    fCurrentEntity.columnNumber += m;
    Object localObject = null;
    if (m > 0) {
      localObject = fSymbolTable.addSymbol(fCurrentEntity.ch, i, m);
    }
    return (String)localObject;
  }
  
  protected String scanName(XMLScanner.NameType paramNameType)
    throws IOException
  {
    if (fCurrentEntity.position == fCurrentEntity.count) {
      load(0, true, true);
    }
    int i = fCurrentEntity.position;
    if (XMLChar.isNameStart(fCurrentEntity.ch[i]))
    {
      if (++fCurrentEntity.position == fCurrentEntity.count)
      {
        invokeListeners(1);
        fCurrentEntity.ch[0] = fCurrentEntity.ch[i];
        i = 0;
        if (load(1, false, false))
        {
          fCurrentEntity.columnNumber += 1;
          String str1 = fSymbolTable.addSymbol(fCurrentEntity.ch, 0, 1);
          return str1;
        }
      }
      int k = 0;
      for (;;)
      {
        int m = fCurrentEntity.ch[fCurrentEntity.position];
        boolean bool;
        if (m < 127) {
          k = VALID_NAMES[m];
        } else {
          bool = XMLChar.isName(m);
        }
        if (!bool) {
          break;
        }
        if ((j = checkBeforeLoad(fCurrentEntity, i, i)) > 0)
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
    String str2;
    if (j > 0)
    {
      checkLimit(XMLSecurityManager.Limit.MAX_NAME_LIMIT, fCurrentEntity, i, j);
      checkEntityLimit(paramNameType, fCurrentEntity, i, j);
      str2 = fSymbolTable.addSymbol(fCurrentEntity.ch, i, j);
    }
    else
    {
      str2 = null;
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
    if (XMLChar.isNameStart(fCurrentEntity.ch[i]))
    {
      if (++fCurrentEntity.position == fCurrentEntity.count)
      {
        invokeListeners(1);
        fCurrentEntity.ch[0] = fCurrentEntity.ch[i];
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
      int j = -1;
      int k = 0;
      for (;;)
      {
        int n = fCurrentEntity.ch[fCurrentEntity.position];
        boolean bool;
        if (n < 127) {
          k = VALID_NAMES[n];
        } else {
          bool = XMLChar.isName(n);
        }
        if (!bool) {
          break;
        }
        if (n == 58)
        {
          if (j == -1)
          {
            j = fCurrentEntity.position;
            checkLimit(XMLSecurityManager.Limit.MAX_NAME_LIMIT, fCurrentEntity, i, j - i);
          }
        }
        else if ((m = checkBeforeLoad(fCurrentEntity, i, j)) > 0)
        {
          if (j != -1) {
            j -= i;
          }
          i = 0;
          if (load(m, false, false)) {
            break;
          }
        }
      }
      int m = fCurrentEntity.position - i;
      fCurrentEntity.columnNumber += m;
      if (m > 0)
      {
        String str2 = null;
        Object localObject = null;
        String str3 = fSymbolTable.addSymbol(fCurrentEntity.ch, i, m);
        if (j != -1)
        {
          int i1 = j - i;
          checkLimit(XMLSecurityManager.Limit.MAX_NAME_LIMIT, fCurrentEntity, i, i1);
          str2 = fSymbolTable.addSymbol(fCurrentEntity.ch, i, i1);
          int i2 = m - i1 - 1;
          checkLimit(XMLSecurityManager.Limit.MAX_NAME_LIMIT, fCurrentEntity, j + 1, i2);
          localObject = fSymbolTable.addSymbol(fCurrentEntity.ch, j + 1, i2);
        }
        else
        {
          localObject = str3;
          checkLimit(XMLSecurityManager.Limit.MAX_NAME_LIMIT, fCurrentEntity, i, m);
        }
        paramQName.setValues(str2, (String)localObject, str3, null);
        checkEntityLimit(paramNameType, fCurrentEntity, i, m);
        return true;
      }
    }
    return false;
  }
  
  protected int checkBeforeLoad(Entity.ScannedEntity paramScannedEntity, int paramInt1, int paramInt2)
    throws IOException
  {
    int i = 0;
    if (++position == count)
    {
      i = position - paramInt1;
      int j = i;
      if (paramInt2 != -1)
      {
        paramInt2 -= paramInt1;
        j = i - paramInt2;
      }
      else
      {
        paramInt2 = paramInt1;
      }
      checkLimit(XMLSecurityManager.Limit.MAX_NAME_LIMIT, paramScannedEntity, paramInt2, j);
      invokeListeners(i);
      if (i == ch.length)
      {
        char[] arrayOfChar = new char[fBufferSize * 2];
        System.arraycopy(ch, paramInt1, arrayOfChar, 0, i);
        ch = arrayOfChar;
        fBufferSize *= 2;
      }
      else
      {
        System.arraycopy(ch, paramInt1, ch, 0, i);
      }
    }
    return i;
  }
  
  protected void checkEntityLimit(XMLScanner.NameType paramNameType, Entity.ScannedEntity paramScannedEntity, int paramInt1, int paramInt2)
  {
    if ((paramScannedEntity == null) || (!isGE)) {
      return;
    }
    if (paramNameType != XMLScanner.NameType.REFERENCE) {
      checkLimit(XMLSecurityManager.Limit.GENERAL_ENTITY_SIZE_LIMIT, paramScannedEntity, paramInt1, paramInt2);
    }
    if ((paramNameType == XMLScanner.NameType.ELEMENTSTART) || (paramNameType == XMLScanner.NameType.ATTRIBUTENAME)) {
      checkNodeCount(paramScannedEntity);
    }
  }
  
  protected void checkNodeCount(Entity.ScannedEntity paramScannedEntity)
  {
    if ((paramScannedEntity != null) && (isGE)) {
      checkLimit(XMLSecurityManager.Limit.ENTITY_REPLACEMENT_LIMIT, paramScannedEntity, 0, 1);
    }
  }
  
  protected void checkLimit(XMLSecurityManager.Limit paramLimit, Entity.ScannedEntity paramScannedEntity, int paramInt1, int paramInt2)
  {
    fLimitAnalyzer.addValue(paramLimit, name, paramInt2);
    if (fSecurityManager.isOverLimit(paramLimit, fLimitAnalyzer))
    {
      fSecurityManager.debugPrint(fLimitAnalyzer);
      Object[] arrayOfObject = { name, Integer.valueOf(fLimitAnalyzer.getValue(paramLimit)), Integer.valueOf(fSecurityManager.getLimit(paramLimit)), paramLimit == XMLSecurityManager.Limit.ENTITY_REPLACEMENT_LIMIT ? new Object[] { Integer.valueOf(fLimitAnalyzer.getValue(paramLimit)), Integer.valueOf(fSecurityManager.getLimit(paramLimit)), fSecurityManager.getStateLiteral(paramLimit) } : fSecurityManager.getStateLiteral(paramLimit) };
      fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", paramLimit.key(), arrayOfObject, (short)2);
    }
    if (fSecurityManager.isOverLimit(XMLSecurityManager.Limit.TOTAL_ENTITY_SIZE_LIMIT, fLimitAnalyzer))
    {
      fSecurityManager.debugPrint(fLimitAnalyzer);
      fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "TotalEntitySizeLimit", new Object[] { Integer.valueOf(fLimitAnalyzer.getTotalValue(XMLSecurityManager.Limit.TOTAL_ENTITY_SIZE_LIMIT)), Integer.valueOf(fSecurityManager.getLimit(XMLSecurityManager.Limit.TOTAL_ENTITY_SIZE_LIMIT)), fSecurityManager.getStateLiteral(XMLSecurityManager.Limit.TOTAL_ENTITY_SIZE_LIMIT) }, (short)2);
    }
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
    }
    int i = fCurrentEntity.position;
    int j = fCurrentEntity.ch[i];
    int k = 0;
    int m = 0;
    if ((j == 10) || ((j == 13) && (isExternal)))
    {
      do
      {
        j = fCurrentEntity.ch[(fCurrentEntity.position++)];
        if ((j == 13) && (isExternal))
        {
          k++;
          fCurrentEntity.lineNumber += 1;
          fCurrentEntity.columnNumber = 1;
          if (fCurrentEntity.position == fCurrentEntity.count)
          {
            checkEntityLimit(null, fCurrentEntity, i, k);
            i = 0;
            fCurrentEntity.position = k;
            if (load(k, false, true))
            {
              m = 1;
              break;
            }
          }
          if (fCurrentEntity.ch[fCurrentEntity.position] == '\n')
          {
            fCurrentEntity.position += 1;
            i++;
          }
          else
          {
            k++;
          }
        }
        else if (j == 10)
        {
          k++;
          fCurrentEntity.lineNumber += 1;
          fCurrentEntity.columnNumber = 1;
          if (fCurrentEntity.position == fCurrentEntity.count)
          {
            checkEntityLimit(null, fCurrentEntity, i, k);
            i = 0;
            fCurrentEntity.position = k;
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
    while (fCurrentEntity.position < fCurrentEntity.count)
    {
      j = fCurrentEntity.ch[(fCurrentEntity.position++)];
      if (!XMLChar.isContent(j)) {
        fCurrentEntity.position -= 1;
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
      if ((j == 13) && (isExternal)) {
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
      fCurrentEntity.position = 0;
    }
    int i = fCurrentEntity.position;
    int j = fCurrentEntity.ch[i];
    int k = 0;
    if (whiteSpaceInfoNeeded) {
      whiteSpaceLen = 0;
    }
    if ((j == 10) || ((j == 13) && (isExternal)))
    {
      do
      {
        j = fCurrentEntity.ch[(fCurrentEntity.position++)];
        if ((j == 13) && (isExternal))
        {
          k++;
          fCurrentEntity.lineNumber += 1;
          fCurrentEntity.columnNumber = 1;
          if (fCurrentEntity.position == fCurrentEntity.count)
          {
            i = 0;
            fCurrentEntity.position = k;
            if (load(k, false, true)) {
              break;
            }
          }
          if (fCurrentEntity.ch[fCurrentEntity.position] == '\n')
          {
            fCurrentEntity.position += 1;
            i++;
          }
          else
          {
            k++;
          }
        }
        else if (j == 10)
        {
          k++;
          fCurrentEntity.lineNumber += 1;
          fCurrentEntity.columnNumber = 1;
          if (fCurrentEntity.position == fCurrentEntity.count)
          {
            i = 0;
            fCurrentEntity.position = k;
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
      m = 0;
      for (m = i; m < fCurrentEntity.position; m++)
      {
        fCurrentEntity.ch[m] = '\n';
        storeWhiteSpace(m);
      }
      int n = fCurrentEntity.position - i;
      if (fCurrentEntity.position == fCurrentEntity.count - 1)
      {
        paramXMLString.setValues(fCurrentEntity.ch, i, n);
        return -1;
      }
    }
    while (fCurrentEntity.position < fCurrentEntity.count)
    {
      j = fCurrentEntity.ch[fCurrentEntity.position];
      if (((j == paramInt) && ((!fCurrentEntity.literal) || (isExternal))) || (j == 37) || (!XMLChar.isContent(j))) {
        break;
      }
      if ((whiteSpaceInfoNeeded) && (j == 9)) {
        storeWhiteSpace(fCurrentEntity.position);
      }
      fCurrentEntity.position += 1;
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
  
  private void storeWhiteSpace(int paramInt)
  {
    if (whiteSpaceLen >= whiteSpaceLookup.length)
    {
      int[] arrayOfInt = new int[whiteSpaceLookup.length + 100];
      System.arraycopy(whiteSpaceLookup, 0, arrayOfInt, 0, whiteSpaceLookup.length);
      whiteSpaceLookup = arrayOfInt;
    }
    whiteSpaceLookup[(whiteSpaceLen++)] = paramInt;
  }
  
  protected boolean scanData(String paramString, XMLStringBuffer paramXMLStringBuffer)
    throws IOException
  {
    int i = 0;
    int j = paramString.length();
    int k = paramString.charAt(0);
    label1024:
    do
    {
      if (fCurrentEntity.position == fCurrentEntity.count) {
        load(0, true, false);
      }
      boolean bool = false;
      while ((fCurrentEntity.position > fCurrentEntity.count - j) && (!bool))
      {
        System.arraycopy(fCurrentEntity.ch, fCurrentEntity.position, fCurrentEntity.ch, 0, fCurrentEntity.count - fCurrentEntity.position);
        bool = load(fCurrentEntity.count - fCurrentEntity.position, false, false);
        fCurrentEntity.position = 0;
        fCurrentEntity.startPosition = 0;
      }
      if (fCurrentEntity.position > fCurrentEntity.count - j)
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
      if ((n == 10) || ((n == 13) && (isExternal)))
      {
        do
        {
          n = fCurrentEntity.ch[(fCurrentEntity.position++)];
          if ((n == 13) && (isExternal))
          {
            i1++;
            fCurrentEntity.lineNumber += 1;
            fCurrentEntity.columnNumber = 1;
            if (fCurrentEntity.position == fCurrentEntity.count)
            {
              m = 0;
              fCurrentEntity.position = i1;
              if (load(i1, false, true)) {
                break;
              }
            }
            if (fCurrentEntity.ch[fCurrentEntity.position] == '\n')
            {
              fCurrentEntity.position += 1;
              m++;
            }
            else
            {
              i1++;
            }
          }
          else if (n == 10)
          {
            i1++;
            fCurrentEntity.lineNumber += 1;
            fCurrentEntity.columnNumber = 1;
            if (fCurrentEntity.position == fCurrentEntity.count)
            {
              m = 0;
              fCurrentEntity.position = i1;
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
      while (fCurrentEntity.position < fCurrentEntity.count)
      {
        n = fCurrentEntity.ch[(fCurrentEntity.position++)];
        if (n == k)
        {
          i2 = fCurrentEntity.position - 1;
          for (int i3 = 1; i3 < j; i3++)
          {
            if (fCurrentEntity.position == fCurrentEntity.count)
            {
              fCurrentEntity.position -= i3;
              break label1024;
            }
            n = fCurrentEntity.ch[(fCurrentEntity.position++)];
            if (paramString.charAt(i3) != n)
            {
              fCurrentEntity.position -= i3;
              break;
            }
          }
          if (fCurrentEntity.position == i2 + j) {
            i = 1;
          }
        }
        else if ((n == 10) || ((isExternal) && (n == 13)))
        {
          fCurrentEntity.position -= 1;
        }
        else if (XMLChar.isInvalid(n))
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
    if ((paramInt == 10) && (j == 13) && (isExternal))
    {
      if (fCurrentEntity.position == fCurrentEntity.count)
      {
        invokeListeners(1);
        fCurrentEntity.ch[0] = ((char)j);
        load(1, false, false);
      }
      fCurrentEntity.position += 1;
      if (fCurrentEntity.ch[fCurrentEntity.position] == '\n') {
        fCurrentEntity.position += 1;
      }
      fCurrentEntity.lineNumber += 1;
      fCurrentEntity.columnNumber = 1;
      checkEntityLimit(paramNameType, fCurrentEntity, i, fCurrentEntity.position - i);
      return true;
    }
    return false;
  }
  
  public boolean isSpace(char paramChar)
  {
    return (paramChar == ' ') || (paramChar == '\n') || (paramChar == '\t') || (paramChar == '\r');
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
    if (XMLChar.isSpace(i))
    {
      do
      {
        boolean bool = false;
        if ((i == 10) || ((isExternal) && (i == 13)))
        {
          fCurrentEntity.lineNumber += 1;
          fCurrentEntity.columnNumber = 1;
          if (fCurrentEntity.position == fCurrentEntity.count - 1)
          {
            invokeListeners(1);
            fCurrentEntity.ch[0] = ((char)i);
            bool = load(1, true, false);
            if (!bool) {
              fCurrentEntity.position = 0;
            } else if (fCurrentEntity == null) {
              return true;
            }
          }
          if ((i == 13) && (isExternal)) {
            if (fCurrentEntity.ch[(++fCurrentEntity.position)] != '\n') {
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
      } while (XMLChar.isSpace(i = fCurrentEntity.ch[fCurrentEntity.position]));
      return true;
    }
    return false;
  }
  
  public boolean arrangeCapacity(int paramInt)
    throws IOException
  {
    return arrangeCapacity(paramInt, false);
  }
  
  public boolean arrangeCapacity(int paramInt, boolean paramBoolean)
    throws IOException
  {
    if (fCurrentEntity.count - fCurrentEntity.position >= paramInt) {
      return true;
    }
    boolean bool = false;
    while (fCurrentEntity.count - fCurrentEntity.position < paramInt)
    {
      if (fCurrentEntity.ch.length - fCurrentEntity.position < paramInt)
      {
        invokeListeners(0);
        System.arraycopy(fCurrentEntity.ch, fCurrentEntity.position, fCurrentEntity.ch, 0, fCurrentEntity.count - fCurrentEntity.position);
        fCurrentEntity.count -= fCurrentEntity.position;
        fCurrentEntity.position = 0;
      }
      if (fCurrentEntity.count - fCurrentEntity.position < paramInt)
      {
        int i = fCurrentEntity.position;
        invokeListeners(i);
        bool = load(fCurrentEntity.count, paramBoolean, false);
        fCurrentEntity.position = i;
        if (bool) {
          break;
        }
      }
    }
    return fCurrentEntity.count - fCurrentEntity.position >= paramInt;
  }
  
  protected boolean skipString(String paramString)
    throws IOException
  {
    int i = paramString.length();
    if (arrangeCapacity(i, false))
    {
      int j = fCurrentEntity.position;
      int k = fCurrentEntity.position + i - 1;
      int m = i - 1;
      while (paramString.charAt(m--) == fCurrentEntity.ch[k]) {
        if (k-- == j)
        {
          fCurrentEntity.position += i;
          fCurrentEntity.columnNumber += i;
          if (!detectingVersion) {
            checkEntityLimit(null, fCurrentEntity, j, i);
          }
          return true;
        }
      }
    }
    return false;
  }
  
  protected boolean skipString(char[] paramArrayOfChar)
    throws IOException
  {
    int i = paramArrayOfChar.length;
    if (arrangeCapacity(i, false))
    {
      int j = fCurrentEntity.position;
      for (int k = 0; k < i; k++) {
        if (fCurrentEntity.ch[(j++)] != paramArrayOfChar[k]) {
          return false;
        }
      }
      fCurrentEntity.position += i;
      fCurrentEntity.columnNumber += i;
      if (!detectingVersion) {
        checkEntityLimit(null, fCurrentEntity, j, i);
      }
      return true;
    }
    return false;
  }
  
  final boolean load(int paramInt, boolean paramBoolean1, boolean paramBoolean2)
    throws IOException
  {
    if (paramBoolean2) {
      invokeListeners(paramInt);
    }
    fCurrentEntity.fTotalCountTillLastLoad += fCurrentEntity.fLastCount;
    int i = fCurrentEntity.ch.length - paramInt;
    if ((!fCurrentEntity.mayReadChunks) && (i > 64)) {
      i = 64;
    }
    int j = fCurrentEntity.reader.read(fCurrentEntity.ch, paramInt, i);
    boolean bool = false;
    if (j != -1)
    {
      if (j != 0)
      {
        fCurrentEntity.fLastCount = j;
        fCurrentEntity.count = (j + paramInt);
        fCurrentEntity.position = paramInt;
      }
    }
    else
    {
      fCurrentEntity.count = paramInt;
      fCurrentEntity.position = paramInt;
      bool = true;
      if (paramBoolean1)
      {
        fEntityManager.endEntity();
        if (fCurrentEntity == null) {
          throw END_OF_DOCUMENT_ENTITY;
        }
        if (fCurrentEntity.position == fCurrentEntity.count) {
          load(0, true, false);
        }
      }
    }
    return bool;
  }
  
  protected Reader createReader(InputStream paramInputStream, String paramString, Boolean paramBoolean)
    throws IOException
  {
    if (paramString == null) {
      paramString = "UTF-8";
    }
    String str1 = paramString.toUpperCase(Locale.ENGLISH);
    if (str1.equals("UTF-8")) {
      return new UTF8Reader(paramInputStream, fCurrentEntity.fBufferSize, fErrorReporter.getMessageFormatter("http://www.w3.org/TR/1998/REC-xml-19980210"), fErrorReporter.getLocale());
    }
    if (str1.equals("US-ASCII")) {
      return new ASCIIReader(paramInputStream, fCurrentEntity.fBufferSize, fErrorReporter.getMessageFormatter("http://www.w3.org/TR/1998/REC-xml-19980210"), fErrorReporter.getLocale());
    }
    if (str1.equals("ISO-10646-UCS-4"))
    {
      if (paramBoolean != null)
      {
        bool1 = paramBoolean.booleanValue();
        if (bool1) {
          return new UCSReader(paramInputStream, (short)8);
        }
        return new UCSReader(paramInputStream, (short)4);
      }
      fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "EncodingByteOrderUnsupported", new Object[] { paramString }, (short)2);
    }
    if (str1.equals("ISO-10646-UCS-2"))
    {
      if (paramBoolean != null)
      {
        bool1 = paramBoolean.booleanValue();
        if (bool1) {
          return new UCSReader(paramInputStream, (short)2);
        }
        return new UCSReader(paramInputStream, (short)1);
      }
      fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "EncodingByteOrderUnsupported", new Object[] { paramString }, (short)2);
    }
    boolean bool1 = XMLChar.isValidIANAEncoding(paramString);
    boolean bool2 = XMLChar.isValidJavaEncoding(paramString);
    if ((!bool1) || ((fAllowJavaEncodings) && (!bool2)))
    {
      fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "EncodingDeclInvalid", new Object[] { paramString }, (short)2);
      paramString = "ISO-8859-1";
    }
    String str2 = EncodingMap.getIANA2JavaMapping(str1);
    if (str2 == null)
    {
      if (fAllowJavaEncodings)
      {
        str2 = paramString;
      }
      else
      {
        fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "EncodingDeclInvalid", new Object[] { paramString }, (short)2);
        str2 = "ISO8859_1";
      }
    }
    else if (str2.equals("ASCII")) {
      return new ASCIIReader(paramInputStream, fBufferSize, fErrorReporter.getMessageFormatter("http://www.w3.org/TR/1998/REC-xml-19980210"), fErrorReporter.getLocale());
    }
    return new InputStreamReader(paramInputStream, str2);
  }
  
  protected Object[] getEncodingName(byte[] paramArrayOfByte, int paramInt)
  {
    if (paramInt < 2) {
      return new Object[] { "UTF-8", null };
    }
    int i = paramArrayOfByte[0] & 0xFF;
    int j = paramArrayOfByte[1] & 0xFF;
    if ((i == 254) && (j == 255)) {
      return new Object[] { "UTF-16BE", new Boolean(true) };
    }
    if ((i == 255) && (j == 254)) {
      return new Object[] { "UTF-16LE", new Boolean(false) };
    }
    if (paramInt < 3) {
      return new Object[] { "UTF-8", null };
    }
    int k = paramArrayOfByte[2] & 0xFF;
    if ((i == 239) && (j == 187) && (k == 191)) {
      return new Object[] { "UTF-8", null };
    }
    if (paramInt < 4) {
      return new Object[] { "UTF-8", null };
    }
    int m = paramArrayOfByte[3] & 0xFF;
    if ((i == 0) && (j == 0) && (k == 0) && (m == 60)) {
      return new Object[] { "ISO-10646-UCS-4", new Boolean(true) };
    }
    if ((i == 60) && (j == 0) && (k == 0) && (m == 0)) {
      return new Object[] { "ISO-10646-UCS-4", new Boolean(false) };
    }
    if ((i == 0) && (j == 0) && (k == 60) && (m == 0)) {
      return new Object[] { "ISO-10646-UCS-4", null };
    }
    if ((i == 0) && (j == 60) && (k == 0) && (m == 0)) {
      return new Object[] { "ISO-10646-UCS-4", null };
    }
    if ((i == 0) && (j == 60) && (k == 0) && (m == 63)) {
      return new Object[] { "UTF-16BE", new Boolean(true) };
    }
    if ((i == 60) && (j == 0) && (k == 63) && (m == 0)) {
      return new Object[] { "UTF-16LE", new Boolean(false) };
    }
    if ((i == 76) && (j == 111) && (k == 167) && (m == 148)) {
      return new Object[] { "CP037", null };
    }
    return new Object[] { "UTF-8", null };
  }
  
  final void print() {}
  
  public void registerListener(XMLBufferListener paramXMLBufferListener)
  {
    if (!listeners.contains(paramXMLBufferListener)) {
      listeners.add(paramXMLBufferListener);
    }
  }
  
  public void invokeListeners(int paramInt)
  {
    for (int i = 0; i < listeners.size(); i++) {
      ((XMLBufferListener)listeners.get(i)).refresh(paramInt);
    }
  }
  
  protected final boolean skipDeclSpaces()
    throws IOException
  {
    if (fCurrentEntity.position == fCurrentEntity.count) {
      load(0, true, false);
    }
    int i = fCurrentEntity.ch[fCurrentEntity.position];
    if (XMLChar.isSpace(i))
    {
      boolean bool1 = fCurrentEntity.isExternal();
      do
      {
        boolean bool2 = false;
        if ((i == 10) || ((bool1) && (i == 13)))
        {
          fCurrentEntity.lineNumber += 1;
          fCurrentEntity.columnNumber = 1;
          if (fCurrentEntity.position == fCurrentEntity.count - 1)
          {
            fCurrentEntity.ch[0] = ((char)i);
            bool2 = load(1, true, false);
            if (!bool2) {
              fCurrentEntity.position = 0;
            }
          }
          if ((i == 13) && (bool1)) {
            if (fCurrentEntity.ch[(++fCurrentEntity.position)] != '\n') {
              fCurrentEntity.position -= 1;
            }
          }
        }
        else
        {
          fCurrentEntity.columnNumber += 1;
        }
        if (!bool2) {
          fCurrentEntity.position += 1;
        }
        if (fCurrentEntity.position == fCurrentEntity.count) {
          load(0, true, false);
        }
      } while (XMLChar.isSpace(i = fCurrentEntity.ch[fCurrentEntity.position]));
      return true;
    }
    return false;
  }
  
  static
  {
    for (int i = 65; i <= 90; i++) {
      VALID_NAMES[i] = true;
    }
    for (i = 97; i <= 122; i++) {
      VALID_NAMES[i] = true;
    }
    for (i = 48; i <= 57; i++) {
      VALID_NAMES[i] = true;
    }
    VALID_NAMES[45] = true;
    VALID_NAMES[46] = true;
    VALID_NAMES[58] = true;
    VALID_NAMES[95] = true;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\impl\XMLEntityScanner.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */