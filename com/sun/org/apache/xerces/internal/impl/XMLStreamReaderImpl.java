package com.sun.org.apache.xerces.internal.impl;

import com.sun.org.apache.xerces.internal.util.NamespaceContextWrapper;
import com.sun.org.apache.xerces.internal.util.NamespaceSupport;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.util.XMLAttributesImpl;
import com.sun.org.apache.xerces.internal.util.XMLAttributesIteratorImpl;
import com.sun.org.apache.xerces.internal.util.XMLChar;
import com.sun.org.apache.xerces.internal.util.XMLStringBuffer;
import com.sun.org.apache.xerces.internal.xni.XMLResourceIdentifier;
import com.sun.org.apache.xerces.internal.xni.XMLString;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
import com.sun.xml.internal.stream.Entity;
import com.sun.xml.internal.stream.Entity.ExternalEntity;
import com.sun.xml.internal.stream.Entity.InternalEntity;
import com.sun.xml.internal.stream.StaxErrorReporter;
import com.sun.xml.internal.stream.XMLEntityStorage;
import com.sun.xml.internal.stream.dtd.nonvalidating.DTDGrammar;
import com.sun.xml.internal.stream.dtd.nonvalidating.XMLNotationDecl;
import com.sun.xml.internal.stream.events.EntityDeclarationImpl;
import com.sun.xml.internal.stream.events.NotationDeclarationImpl;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

public class XMLStreamReaderImpl
  implements XMLStreamReader
{
  protected static final String ENTITY_MANAGER = "http://apache.org/xml/properties/internal/entity-manager";
  protected static final String ERROR_REPORTER = "http://apache.org/xml/properties/internal/error-reporter";
  protected static final String SYMBOL_TABLE = "http://apache.org/xml/properties/internal/symbol-table";
  protected static final String READER_IN_DEFINED_STATE = "http://java.sun.com/xml/stream/properties/reader-in-defined-state";
  private SymbolTable fSymbolTable = new SymbolTable();
  protected XMLDocumentScannerImpl fScanner = new XMLNSDocumentScannerImpl();
  protected NamespaceContextWrapper fNamespaceContextWrapper = new NamespaceContextWrapper((NamespaceSupport)fScanner.getNamespaceContext());
  protected XMLEntityManager fEntityManager = new XMLEntityManager();
  protected StaxErrorReporter fErrorReporter = new StaxErrorReporter();
  protected XMLEntityScanner fEntityScanner = null;
  protected XMLInputSource fInputSource = null;
  protected PropertyManager fPropertyManager = null;
  private int fEventType;
  static final boolean DEBUG = false;
  private boolean fReuse = true;
  private boolean fReaderInDefinedState = true;
  private boolean fBindNamespaces = true;
  private String fDTDDecl = null;
  private String versionStr = null;
  
  public XMLStreamReaderImpl(InputStream paramInputStream, PropertyManager paramPropertyManager)
    throws XMLStreamException
  {
    init(paramPropertyManager);
    XMLInputSource localXMLInputSource = new XMLInputSource(null, null, null, paramInputStream, null);
    setInputSource(localXMLInputSource);
  }
  
  public XMLDocumentScannerImpl getScanner()
  {
    System.out.println("returning scanner");
    return fScanner;
  }
  
  public XMLStreamReaderImpl(String paramString, PropertyManager paramPropertyManager)
    throws XMLStreamException
  {
    init(paramPropertyManager);
    XMLInputSource localXMLInputSource = new XMLInputSource(null, paramString, null);
    setInputSource(localXMLInputSource);
  }
  
  public XMLStreamReaderImpl(InputStream paramInputStream, String paramString, PropertyManager paramPropertyManager)
    throws XMLStreamException
  {
    init(paramPropertyManager);
    XMLInputSource localXMLInputSource = new XMLInputSource(null, null, null, new BufferedInputStream(paramInputStream), paramString);
    setInputSource(localXMLInputSource);
  }
  
  public XMLStreamReaderImpl(Reader paramReader, PropertyManager paramPropertyManager)
    throws XMLStreamException
  {
    init(paramPropertyManager);
    XMLInputSource localXMLInputSource = new XMLInputSource(null, null, null, new BufferedReader(paramReader), null);
    setInputSource(localXMLInputSource);
  }
  
  public XMLStreamReaderImpl(XMLInputSource paramXMLInputSource, PropertyManager paramPropertyManager)
    throws XMLStreamException
  {
    init(paramPropertyManager);
    setInputSource(paramXMLInputSource);
  }
  
  public void setInputSource(XMLInputSource paramXMLInputSource)
    throws XMLStreamException
  {
    fReuse = false;
    try
    {
      fScanner.setInputSource(paramXMLInputSource);
      if (fReaderInDefinedState)
      {
        fEventType = fScanner.next();
        if (versionStr == null) {
          versionStr = getVersion();
        }
        if ((fEventType == 7) && (versionStr != null) && (versionStr.equals("1.1"))) {
          switchToXML11Scanner();
        }
      }
    }
    catch (IOException localIOException)
    {
      throw new XMLStreamException(localIOException);
    }
    catch (XNIException localXNIException)
    {
      throw new XMLStreamException(localXNIException.getMessage(), getLocation(), localXNIException.getException());
    }
  }
  
  void init(PropertyManager paramPropertyManager)
    throws XMLStreamException
  {
    fPropertyManager = paramPropertyManager;
    paramPropertyManager.setProperty("http://apache.org/xml/properties/internal/symbol-table", fSymbolTable);
    paramPropertyManager.setProperty("http://apache.org/xml/properties/internal/error-reporter", fErrorReporter);
    paramPropertyManager.setProperty("http://apache.org/xml/properties/internal/entity-manager", fEntityManager);
    reset();
  }
  
  public boolean canReuse()
  {
    return fReuse;
  }
  
  public void reset()
  {
    fReuse = true;
    fEventType = 0;
    fEntityManager.reset(fPropertyManager);
    fScanner.reset(fPropertyManager);
    fDTDDecl = null;
    fEntityScanner = fEntityManager.getEntityScanner();
    fReaderInDefinedState = ((Boolean)fPropertyManager.getProperty("http://java.sun.com/xml/stream/properties/reader-in-defined-state")).booleanValue();
    fBindNamespaces = ((Boolean)fPropertyManager.getProperty("javax.xml.stream.isNamespaceAware")).booleanValue();
    versionStr = null;
  }
  
  public void close()
    throws XMLStreamException
  {
    fReuse = true;
  }
  
  public String getCharacterEncodingScheme()
  {
    return fScanner.getCharacterEncodingScheme();
  }
  
  public int getColumnNumber()
  {
    return fEntityScanner.getColumnNumber();
  }
  
  public String getEncoding()
  {
    return fEntityScanner.getEncoding();
  }
  
  public int getEventType()
  {
    return fEventType;
  }
  
  public int getLineNumber()
  {
    return fEntityScanner.getLineNumber();
  }
  
  public String getLocalName()
  {
    if ((fEventType == 1) || (fEventType == 2)) {
      return fScanner.getElementQName().localpart;
    }
    if (fEventType == 9) {
      return fScanner.getEntityName();
    }
    throw new IllegalStateException("Method getLocalName() cannot be called for " + getEventTypeString(fEventType) + " event.");
  }
  
  public String getNamespaceURI()
  {
    if ((fEventType == 1) || (fEventType == 2)) {
      return fScanner.getElementQName().uri;
    }
    return null;
  }
  
  public String getPIData()
  {
    if (fEventType == 3) {
      return fScanner.getPIData().toString();
    }
    throw new IllegalStateException("Current state of the parser is " + getEventTypeString(fEventType) + " But Expected state is " + 3);
  }
  
  public String getPITarget()
  {
    if (fEventType == 3) {
      return fScanner.getPITarget();
    }
    throw new IllegalStateException("Current state of the parser is " + getEventTypeString(fEventType) + " But Expected state is " + 3);
  }
  
  public String getPrefix()
  {
    if ((fEventType == 1) || (fEventType == 2))
    {
      String str = fScanner.getElementQName().prefix;
      return str == null ? "" : str;
    }
    return null;
  }
  
  public char[] getTextCharacters()
  {
    if ((fEventType == 4) || (fEventType == 5) || (fEventType == 12) || (fEventType == 6)) {
      return fScanner.getCharacterData().ch;
    }
    throw new IllegalStateException("Current state = " + getEventTypeString(fEventType) + " is not among the states " + getEventTypeString(4) + " , " + getEventTypeString(5) + " , " + getEventTypeString(12) + " , " + getEventTypeString(6) + " valid for getTextCharacters() ");
  }
  
  public int getTextLength()
  {
    if ((fEventType == 4) || (fEventType == 5) || (fEventType == 12) || (fEventType == 6)) {
      return fScanner.getCharacterData().length;
    }
    throw new IllegalStateException("Current state = " + getEventTypeString(fEventType) + " is not among the states " + getEventTypeString(4) + " , " + getEventTypeString(5) + " , " + getEventTypeString(12) + " , " + getEventTypeString(6) + " valid for getTextLength() ");
  }
  
  public int getTextStart()
  {
    if ((fEventType == 4) || (fEventType == 5) || (fEventType == 12) || (fEventType == 6)) {
      return fScanner.getCharacterData().offset;
    }
    throw new IllegalStateException("Current state = " + getEventTypeString(fEventType) + " is not among the states " + getEventTypeString(4) + " , " + getEventTypeString(5) + " , " + getEventTypeString(12) + " , " + getEventTypeString(6) + " valid for getTextStart() ");
  }
  
  public String getValue()
  {
    if (fEventType == 3) {
      return fScanner.getPIData().toString();
    }
    if (fEventType == 5) {
      return fScanner.getComment();
    }
    if ((fEventType == 1) || (fEventType == 2)) {
      return fScanner.getElementQName().localpart;
    }
    if (fEventType == 4) {
      return fScanner.getCharacterData().toString();
    }
    return null;
  }
  
  public String getVersion()
  {
    String str = fEntityScanner.getXMLVersion();
    return ("1.0".equals(str)) && (!fEntityScanner.xmlVersionSetExplicitly) ? null : str;
  }
  
  public boolean hasAttributes()
  {
    return fScanner.getAttributeIterator().getLength() > 0;
  }
  
  public boolean hasName()
  {
    return (fEventType == 1) || (fEventType == 2);
  }
  
  public boolean hasNext()
    throws XMLStreamException
  {
    if (fEventType == -1) {
      return false;
    }
    return fEventType != 8;
  }
  
  public boolean hasValue()
  {
    return (fEventType == 1) || (fEventType == 2) || (fEventType == 9) || (fEventType == 3) || (fEventType == 5) || (fEventType == 4);
  }
  
  public boolean isEndElement()
  {
    return fEventType == 2;
  }
  
  public boolean isStandalone()
  {
    return fScanner.isStandAlone();
  }
  
  public boolean isStartElement()
  {
    return fEventType == 1;
  }
  
  public boolean isWhiteSpace()
  {
    if ((isCharacters()) || (fEventType == 12))
    {
      char[] arrayOfChar = getTextCharacters();
      int i = getTextStart();
      int j = i + getTextLength();
      for (int k = i; k < j; k++) {
        if (!XMLChar.isSpace(arrayOfChar[k])) {
          return false;
        }
      }
      return true;
    }
    return false;
  }
  
  public int next()
    throws XMLStreamException
  {
    if (!hasNext())
    {
      if (fEventType != -1) {
        throw new NoSuchElementException("END_DOCUMENT reached: no more elements on the stream.");
      }
      throw new XMLStreamException("Error processing input source. The input stream is not complete.");
    }
    try
    {
      fEventType = fScanner.next();
      if (versionStr == null) {
        versionStr = getVersion();
      }
      if ((fEventType == 7) && (versionStr != null) && (versionStr.equals("1.1"))) {
        switchToXML11Scanner();
      }
      if ((fEventType == 4) || (fEventType == 9) || (fEventType == 3) || (fEventType == 5) || (fEventType == 12)) {
        fEntityScanner.checkNodeCount(fEntityScanner.fCurrentEntity);
      }
      return fEventType;
    }
    catch (IOException localIOException)
    {
      if (fScanner.fScannerState == 46)
      {
        Boolean localBoolean = (Boolean)fPropertyManager.getProperty("javax.xml.stream.isValidating");
        if ((localBoolean != null) && (!localBoolean.booleanValue()))
        {
          fEventType = 11;
          fScanner.setScannerState(43);
          fScanner.setDriver(fScanner.fPrologDriver);
          if ((fDTDDecl == null) || (fDTDDecl.length() == 0)) {
            fDTDDecl = "<!-- Exception scanning External DTD Subset.  True contents of DTD cannot be determined.  Processing will continue as XMLInputFactory.IS_VALIDATING == false. -->";
          }
          return 11;
        }
      }
      throw new XMLStreamException(localIOException.getMessage(), getLocation(), localIOException);
    }
    catch (XNIException localXNIException)
    {
      throw new XMLStreamException(localXNIException.getMessage(), getLocation(), localXNIException.getException());
    }
  }
  
  private void switchToXML11Scanner()
    throws IOException
  {
    int i = fScanner.fEntityDepth;
    com.sun.org.apache.xerces.internal.xni.NamespaceContext localNamespaceContext = fScanner.fNamespaceContext;
    fScanner = new XML11NSDocumentScannerImpl();
    fScanner.reset(fPropertyManager);
    fScanner.setPropertyManager(fPropertyManager);
    fEntityScanner = fEntityManager.getEntityScanner();
    fEntityManager.fCurrentEntity.mayReadChunks = true;
    fScanner.setScannerState(7);
    fScanner.fEntityDepth = i;
    fScanner.fNamespaceContext = localNamespaceContext;
    fEventType = fScanner.next();
  }
  
  static final String getEventTypeString(int paramInt)
  {
    switch (paramInt)
    {
    case 1: 
      return "START_ELEMENT";
    case 2: 
      return "END_ELEMENT";
    case 3: 
      return "PROCESSING_INSTRUCTION";
    case 4: 
      return "CHARACTERS";
    case 5: 
      return "COMMENT";
    case 7: 
      return "START_DOCUMENT";
    case 8: 
      return "END_DOCUMENT";
    case 9: 
      return "ENTITY_REFERENCE";
    case 10: 
      return "ATTRIBUTE";
    case 11: 
      return "DTD";
    case 12: 
      return "CDATA";
    case 6: 
      return "SPACE";
    }
    return "UNKNOWN_EVENT_TYPE, " + String.valueOf(paramInt);
  }
  
  public int getAttributeCount()
  {
    if ((fEventType == 1) || (fEventType == 10)) {
      return fScanner.getAttributeIterator().getLength();
    }
    throw new IllegalStateException("Current state is not among the states " + getEventTypeString(1) + " , " + getEventTypeString(10) + "valid for getAttributeCount()");
  }
  
  public javax.xml.namespace.QName getAttributeName(int paramInt)
  {
    if ((fEventType == 1) || (fEventType == 10)) {
      return convertXNIQNametoJavaxQName(fScanner.getAttributeIterator().getQualifiedName(paramInt));
    }
    throw new IllegalStateException("Current state is not among the states " + getEventTypeString(1) + " , " + getEventTypeString(10) + "valid for getAttributeName()");
  }
  
  public String getAttributeLocalName(int paramInt)
  {
    if ((fEventType == 1) || (fEventType == 10)) {
      return fScanner.getAttributeIterator().getLocalName(paramInt);
    }
    throw new IllegalStateException();
  }
  
  public String getAttributeNamespace(int paramInt)
  {
    if ((fEventType == 1) || (fEventType == 10)) {
      return fScanner.getAttributeIterator().getURI(paramInt);
    }
    throw new IllegalStateException("Current state is not among the states " + getEventTypeString(1) + " , " + getEventTypeString(10) + "valid for getAttributeNamespace()");
  }
  
  public String getAttributePrefix(int paramInt)
  {
    if ((fEventType == 1) || (fEventType == 10)) {
      return fScanner.getAttributeIterator().getPrefix(paramInt);
    }
    throw new IllegalStateException("Current state is not among the states " + getEventTypeString(1) + " , " + getEventTypeString(10) + "valid for getAttributePrefix()");
  }
  
  public javax.xml.namespace.QName getAttributeQName(int paramInt)
  {
    if ((fEventType == 1) || (fEventType == 10))
    {
      String str1 = fScanner.getAttributeIterator().getLocalName(paramInt);
      String str2 = fScanner.getAttributeIterator().getURI(paramInt);
      return new javax.xml.namespace.QName(str2, str1);
    }
    throw new IllegalStateException("Current state is not among the states " + getEventTypeString(1) + " , " + getEventTypeString(10) + "valid for getAttributeQName()");
  }
  
  public String getAttributeType(int paramInt)
  {
    if ((fEventType == 1) || (fEventType == 10)) {
      return fScanner.getAttributeIterator().getType(paramInt);
    }
    throw new IllegalStateException("Current state is not among the states " + getEventTypeString(1) + " , " + getEventTypeString(10) + "valid for getAttributeType()");
  }
  
  public String getAttributeValue(int paramInt)
  {
    if ((fEventType == 1) || (fEventType == 10)) {
      return fScanner.getAttributeIterator().getValue(paramInt);
    }
    throw new IllegalStateException("Current state is not among the states " + getEventTypeString(1) + " , " + getEventTypeString(10) + "valid for getAttributeValue()");
  }
  
  public String getAttributeValue(String paramString1, String paramString2)
  {
    if ((fEventType == 1) || (fEventType == 10))
    {
      XMLAttributesIteratorImpl localXMLAttributesIteratorImpl = fScanner.getAttributeIterator();
      if (paramString1 == null) {
        return localXMLAttributesIteratorImpl.getValue(localXMLAttributesIteratorImpl.getIndexByLocalName(paramString2));
      }
      return fScanner.getAttributeIterator().getValue(paramString1.length() == 0 ? null : paramString1, paramString2);
    }
    throw new IllegalStateException("Current state is not among the states " + getEventTypeString(1) + " , " + getEventTypeString(10) + "valid for getAttributeValue()");
  }
  
  public String getElementText()
    throws XMLStreamException
  {
    if (getEventType() != 1) {
      throw new XMLStreamException("parser must be on START_ELEMENT to read next text", getLocation());
    }
    int i = next();
    StringBuffer localStringBuffer = new StringBuffer();
    while (i != 2)
    {
      if ((i == 4) || (i == 12) || (i == 6) || (i == 9))
      {
        localStringBuffer.append(getText());
      }
      else if ((i != 3) && (i != 5))
      {
        if (i == 8) {
          throw new XMLStreamException("unexpected end of document when reading element text content");
        }
        if (i == 1) {
          throw new XMLStreamException("elementGetText() function expects text only elment but START_ELEMENT was encountered.", getLocation());
        }
        throw new XMLStreamException("Unexpected event type " + i, getLocation());
      }
      i = next();
    }
    return localStringBuffer.toString();
  }
  
  public Location getLocation()
  {
    new Location()
    {
      String _systemId = fEntityScanner.getExpandedSystemId();
      String _publicId = fEntityScanner.getPublicId();
      int _offset = fEntityScanner.getCharacterOffset();
      int _columnNumber = fEntityScanner.getColumnNumber();
      int _lineNumber = fEntityScanner.getLineNumber();
      
      public String getLocationURI()
      {
        return _systemId;
      }
      
      public int getCharacterOffset()
      {
        return _offset;
      }
      
      public int getColumnNumber()
      {
        return _columnNumber;
      }
      
      public int getLineNumber()
      {
        return _lineNumber;
      }
      
      public String getPublicId()
      {
        return _publicId;
      }
      
      public String getSystemId()
      {
        return _systemId;
      }
      
      public String toString()
      {
        StringBuffer localStringBuffer = new StringBuffer();
        localStringBuffer.append("Line number = " + getLineNumber());
        localStringBuffer.append("\n");
        localStringBuffer.append("Column number = " + getColumnNumber());
        localStringBuffer.append("\n");
        localStringBuffer.append("System Id = " + getSystemId());
        localStringBuffer.append("\n");
        localStringBuffer.append("Public Id = " + getPublicId());
        localStringBuffer.append("\n");
        localStringBuffer.append("Location Uri= " + getLocationURI());
        localStringBuffer.append("\n");
        localStringBuffer.append("CharacterOffset = " + getCharacterOffset());
        localStringBuffer.append("\n");
        return localStringBuffer.toString();
      }
    };
  }
  
  public javax.xml.namespace.QName getName()
  {
    if ((fEventType == 1) || (fEventType == 2)) {
      return convertXNIQNametoJavaxQName(fScanner.getElementQName());
    }
    throw new IllegalStateException("Illegal to call getName() when event type is " + getEventTypeString(fEventType) + ". Valid states are " + getEventTypeString(1) + ", " + getEventTypeString(2));
  }
  
  public javax.xml.namespace.NamespaceContext getNamespaceContext()
  {
    return fNamespaceContextWrapper;
  }
  
  public int getNamespaceCount()
  {
    if ((fEventType == 1) || (fEventType == 2) || (fEventType == 13)) {
      return fScanner.getNamespaceContext().getDeclaredPrefixCount();
    }
    throw new IllegalStateException("Current event state is " + getEventTypeString(fEventType) + " is not among the states " + getEventTypeString(1) + ", " + getEventTypeString(2) + ", " + getEventTypeString(13) + " valid for getNamespaceCount().");
  }
  
  public String getNamespacePrefix(int paramInt)
  {
    if ((fEventType == 1) || (fEventType == 2) || (fEventType == 13))
    {
      String str = fScanner.getNamespaceContext().getDeclaredPrefixAt(paramInt);
      return str.equals("") ? null : str;
    }
    throw new IllegalStateException("Current state " + getEventTypeString(fEventType) + " is not among the states " + getEventTypeString(1) + ", " + getEventTypeString(2) + ", " + getEventTypeString(13) + " valid for getNamespacePrefix().");
  }
  
  public String getNamespaceURI(int paramInt)
  {
    if ((fEventType == 1) || (fEventType == 2) || (fEventType == 13)) {
      return fScanner.getNamespaceContext().getURI(fScanner.getNamespaceContext().getDeclaredPrefixAt(paramInt));
    }
    throw new IllegalStateException("Current state " + getEventTypeString(fEventType) + " is not among the states " + getEventTypeString(1) + ", " + getEventTypeString(2) + ", " + getEventTypeString(13) + " valid for getNamespaceURI().");
  }
  
  public Object getProperty(String paramString)
    throws IllegalArgumentException
  {
    if (paramString == null) {
      throw new IllegalArgumentException();
    }
    if (fPropertyManager != null)
    {
      if (paramString.equals("javax.xml.stream.notations")) {
        return getNotationDecls();
      }
      if (paramString.equals("javax.xml.stream.entities")) {
        return getEntityDecls();
      }
      return fPropertyManager.getProperty(paramString);
    }
    return null;
  }
  
  public String getText()
  {
    if ((fEventType == 4) || (fEventType == 5) || (fEventType == 12) || (fEventType == 6)) {
      return fScanner.getCharacterData().toString();
    }
    Object localObject;
    if (fEventType == 9)
    {
      localObject = fScanner.getEntityName();
      if (localObject != null)
      {
        if (fScanner.foundBuiltInRefs) {
          return fScanner.getCharacterData().toString();
        }
        XMLEntityStorage localXMLEntityStorage = fEntityManager.getEntityStore();
        Entity localEntity = localXMLEntityStorage.getEntity((String)localObject);
        if (localEntity == null) {
          return null;
        }
        if (localEntity.isExternal()) {
          return entityLocation.getExpandedSystemId();
        }
        return text;
      }
      return null;
    }
    if (fEventType == 11)
    {
      if (fDTDDecl != null) {
        return fDTDDecl;
      }
      localObject = fScanner.getDTDDecl();
      fDTDDecl = ((XMLStringBuffer)localObject).toString();
      return fDTDDecl;
    }
    throw new IllegalStateException("Current state " + getEventTypeString(fEventType) + " is not among the states" + getEventTypeString(4) + ", " + getEventTypeString(5) + ", " + getEventTypeString(12) + ", " + getEventTypeString(6) + ", " + getEventTypeString(9) + ", " + getEventTypeString(11) + " valid for getText() ");
  }
  
  public void require(int paramInt, String paramString1, String paramString2)
    throws XMLStreamException
  {
    if (paramInt != fEventType) {
      throw new XMLStreamException("Event type " + getEventTypeString(paramInt) + " specified did not match with current parser event " + getEventTypeString(fEventType));
    }
    if ((paramString1 != null) && (!paramString1.equals(getNamespaceURI()))) {
      throw new XMLStreamException("Namespace URI " + paramString1 + " specified did not match with current namespace URI");
    }
    if ((paramString2 != null) && (!paramString2.equals(getLocalName()))) {
      throw new XMLStreamException("LocalName " + paramString2 + " specified did not match with current local name");
    }
  }
  
  public int getTextCharacters(int paramInt1, char[] paramArrayOfChar, int paramInt2, int paramInt3)
    throws XMLStreamException
  {
    if (paramArrayOfChar == null) {
      throw new NullPointerException("target char array can't be null");
    }
    if ((paramInt2 < 0) || (paramInt3 < 0) || (paramInt1 < 0) || (paramInt2 >= paramArrayOfChar.length) || (paramInt2 + paramInt3 > paramArrayOfChar.length)) {
      throw new IndexOutOfBoundsException();
    }
    int i = 0;
    int j = getTextLength() - paramInt1;
    if (j < 0) {
      throw new IndexOutOfBoundsException("sourceStart is greater thannumber of characters associated with this event");
    }
    if (j < paramInt3) {
      i = j;
    } else {
      i = paramInt3;
    }
    System.arraycopy(getTextCharacters(), getTextStart() + paramInt1, paramArrayOfChar, paramInt2, i);
    return i;
  }
  
  public boolean hasText()
  {
    if ((fEventType == 4) || (fEventType == 5) || (fEventType == 12)) {
      return fScanner.getCharacterData().length > 0;
    }
    if (fEventType == 9)
    {
      String str = fScanner.getEntityName();
      if (str != null)
      {
        if (fScanner.foundBuiltInRefs) {
          return true;
        }
        XMLEntityStorage localXMLEntityStorage = fEntityManager.getEntityStore();
        Entity localEntity = localXMLEntityStorage.getEntity(str);
        if (localEntity == null) {
          return false;
        }
        if (localEntity.isExternal()) {
          return entityLocation.getExpandedSystemId() != null;
        }
        return text != null;
      }
      return false;
    }
    if (fEventType == 11) {
      return fScanner.fSeenDoctypeDecl;
    }
    return false;
  }
  
  public boolean isAttributeSpecified(int paramInt)
  {
    if ((fEventType == 1) || (fEventType == 10)) {
      return fScanner.getAttributeIterator().isSpecified(paramInt);
    }
    throw new IllegalStateException("Current state is not among the states " + getEventTypeString(1) + " , " + getEventTypeString(10) + "valid for isAttributeSpecified()");
  }
  
  public boolean isCharacters()
  {
    return fEventType == 4;
  }
  
  public int nextTag()
    throws XMLStreamException
  {
    for (int i = next(); ((i == 4) && (isWhiteSpace())) || ((i == 12) && (isWhiteSpace())) || (i == 6) || (i == 3) || (i == 5); i = next()) {}
    if ((i != 1) && (i != 2)) {
      throw new XMLStreamException("found: " + getEventTypeString(i) + ", expected " + getEventTypeString(1) + " or " + getEventTypeString(2), getLocation());
    }
    return i;
  }
  
  public boolean standaloneSet()
  {
    return fScanner.standaloneSet();
  }
  
  public javax.xml.namespace.QName convertXNIQNametoJavaxQName(com.sun.org.apache.xerces.internal.xni.QName paramQName)
  {
    if (paramQName == null) {
      return null;
    }
    if (prefix == null) {
      return new javax.xml.namespace.QName(uri, localpart);
    }
    return new javax.xml.namespace.QName(uri, localpart, prefix);
  }
  
  public String getNamespaceURI(String paramString)
  {
    if (paramString == null) {
      throw new IllegalArgumentException("prefix cannot be null.");
    }
    return fScanner.getNamespaceContext().getURI(fSymbolTable.addSymbol(paramString));
  }
  
  protected void setPropertyManager(PropertyManager paramPropertyManager)
  {
    fPropertyManager = paramPropertyManager;
    fScanner.setProperty("stax-properties", paramPropertyManager);
    fScanner.setPropertyManager(paramPropertyManager);
  }
  
  protected PropertyManager getPropertyManager()
  {
    return fPropertyManager;
  }
  
  static void pr(String paramString)
  {
    System.out.println(paramString);
  }
  
  protected List getEntityDecls()
  {
    if (fEventType == 11)
    {
      XMLEntityStorage localXMLEntityStorage = fEntityManager.getEntityStore();
      ArrayList localArrayList = null;
      if (localXMLEntityStorage.hasEntities())
      {
        EntityDeclarationImpl localEntityDeclarationImpl = null;
        localArrayList = new ArrayList(localXMLEntityStorage.getEntitySize());
        Enumeration localEnumeration = localXMLEntityStorage.getEntityKeys();
        while (localEnumeration.hasMoreElements())
        {
          String str = (String)localEnumeration.nextElement();
          Entity localEntity = localXMLEntityStorage.getEntity(str);
          localEntityDeclarationImpl = new EntityDeclarationImpl();
          localEntityDeclarationImpl.setEntityName(str);
          if (localEntity.isExternal())
          {
            localEntityDeclarationImpl.setXMLResourceIdentifier(entityLocation);
            localEntityDeclarationImpl.setNotationName(notation);
          }
          else
          {
            localEntityDeclarationImpl.setEntityReplacementText(text);
          }
          localArrayList.add(localEntityDeclarationImpl);
        }
      }
      return localArrayList;
    }
    return null;
  }
  
  protected List getNotationDecls()
  {
    if (fEventType == 11)
    {
      if (fScanner.fDTDScanner == null) {
        return null;
      }
      DTDGrammar localDTDGrammar = ((XMLDTDScannerImpl)fScanner.fDTDScanner).getGrammar();
      if (localDTDGrammar == null) {
        return null;
      }
      List localList = localDTDGrammar.getNotationDecls();
      Iterator localIterator = localList.iterator();
      ArrayList localArrayList = new ArrayList();
      while (localIterator.hasNext())
      {
        XMLNotationDecl localXMLNotationDecl = (XMLNotationDecl)localIterator.next();
        if (localXMLNotationDecl != null) {
          localArrayList.add(new NotationDeclarationImpl(localXMLNotationDecl));
        }
      }
      return localArrayList;
    }
    return null;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\impl\XMLStreamReaderImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */