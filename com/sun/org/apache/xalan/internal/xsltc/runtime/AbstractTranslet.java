package com.sun.org.apache.xalan.internal.xsltc.runtime;

import com.sun.org.apache.xalan.internal.utils.FactoryImpl;
import com.sun.org.apache.xalan.internal.xsltc.DOM;
import com.sun.org.apache.xalan.internal.xsltc.DOMCache;
import com.sun.org.apache.xalan.internal.xsltc.DOMEnhancedForDTM;
import com.sun.org.apache.xalan.internal.xsltc.Translet;
import com.sun.org.apache.xalan.internal.xsltc.TransletException;
import com.sun.org.apache.xalan.internal.xsltc.dom.DOMAdapter;
import com.sun.org.apache.xalan.internal.xsltc.dom.KeyIndex;
import com.sun.org.apache.xalan.internal.xsltc.runtime.output.TransletOutputHandlerFactory;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xml.internal.serializer.SerializationHandler;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Vector;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Templates;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;

public abstract class AbstractTranslet
  implements Translet
{
  public String _version = "1.0";
  public String _method = null;
  public String _encoding = "UTF-8";
  public boolean _omitHeader = false;
  public String _standalone = null;
  public boolean _isStandalone = false;
  public String _doctypePublic = null;
  public String _doctypeSystem = null;
  public boolean _indent = false;
  public String _mediaType = null;
  public Vector _cdata = null;
  public int _indentamount = -1;
  public static final int FIRST_TRANSLET_VERSION = 100;
  public static final int VER_SPLIT_NAMES_ARRAY = 101;
  public static final int CURRENT_TRANSLET_VERSION = 101;
  protected int transletVersion = 100;
  protected String[] namesArray;
  protected String[] urisArray;
  protected int[] typesArray;
  protected String[] namespaceArray;
  protected Templates _templates = null;
  protected boolean _hasIdCall = false;
  protected StringValueHandler stringValueHandler = new StringValueHandler();
  private static final String EMPTYSTRING = "";
  private static final String ID_INDEX_NAME = "##id";
  private boolean _useServicesMechanism;
  private String _accessExternalStylesheet = "all";
  protected int pbase = 0;
  protected int pframe = 0;
  protected ArrayList paramsStack = new ArrayList();
  private MessageHandler _msgHandler = null;
  public Map<String, DecimalFormat> _formatSymbols = null;
  private Map<String, KeyIndex> _keyIndexes = null;
  private KeyIndex _emptyKeyIndex = null;
  private int _indexSize = 0;
  private int _currentRootForKeys = 0;
  private DOMCache _domCache = null;
  private Map<String, Class<?>> _auxClasses = null;
  protected DOMImplementation _domImplementation = null;
  
  public AbstractTranslet() {}
  
  public void printInternalState()
  {
    System.out.println("-------------------------------------");
    System.out.println("AbstractTranslet this = " + this);
    System.out.println("pbase = " + pbase);
    System.out.println("vframe = " + pframe);
    System.out.println("paramsStack.size() = " + paramsStack.size());
    System.out.println("namesArray.size = " + namesArray.length);
    System.out.println("namespaceArray.size = " + namespaceArray.length);
    System.out.println("");
    System.out.println("Total memory = " + Runtime.getRuntime().totalMemory());
  }
  
  public final DOMAdapter makeDOMAdapter(DOM paramDOM)
    throws TransletException
  {
    setRootForKeys(paramDOM.getDocument());
    return new DOMAdapter(paramDOM, namesArray, urisArray, typesArray, namespaceArray);
  }
  
  public final void pushParamFrame()
  {
    paramsStack.add(pframe, new Integer(pbase));
    pbase = (++pframe);
  }
  
  public final void popParamFrame()
  {
    if (pbase > 0)
    {
      int i = ((Integer)paramsStack.get(--pbase)).intValue();
      for (int j = pframe - 1; j >= pbase; j--) {
        paramsStack.remove(j);
      }
      pframe = pbase;
      pbase = i;
    }
  }
  
  public final Object addParameter(String paramString, Object paramObject)
  {
    paramString = BasisLibrary.mapQNameToJavaName(paramString);
    return addParameter(paramString, paramObject, false);
  }
  
  public final Object addParameter(String paramString, Object paramObject, boolean paramBoolean)
  {
    for (int i = pframe - 1; i >= pbase; i--)
    {
      Parameter localParameter = (Parameter)paramsStack.get(i);
      if (_name.equals(paramString))
      {
        if ((_isDefault) || (!paramBoolean))
        {
          _value = paramObject;
          _isDefault = paramBoolean;
          return paramObject;
        }
        return _value;
      }
    }
    paramsStack.add(pframe++, new Parameter(paramString, paramObject, paramBoolean));
    return paramObject;
  }
  
  public void clearParameters()
  {
    pbase = (pframe = 0);
    paramsStack.clear();
  }
  
  public final Object getParameter(String paramString)
  {
    paramString = BasisLibrary.mapQNameToJavaName(paramString);
    for (int i = pframe - 1; i >= pbase; i--)
    {
      Parameter localParameter = (Parameter)paramsStack.get(i);
      if (_name.equals(paramString)) {
        return _value;
      }
    }
    return null;
  }
  
  public final void setMessageHandler(MessageHandler paramMessageHandler)
  {
    _msgHandler = paramMessageHandler;
  }
  
  public final void displayMessage(String paramString)
  {
    if (_msgHandler == null) {
      System.err.println(paramString);
    } else {
      _msgHandler.displayMessage(paramString);
    }
  }
  
  public void addDecimalFormat(String paramString, DecimalFormatSymbols paramDecimalFormatSymbols)
  {
    if (_formatSymbols == null) {
      _formatSymbols = new HashMap();
    }
    if (paramString == null) {
      paramString = "";
    }
    DecimalFormat localDecimalFormat = new DecimalFormat();
    if (paramDecimalFormatSymbols != null) {
      localDecimalFormat.setDecimalFormatSymbols(paramDecimalFormatSymbols);
    }
    _formatSymbols.put(paramString, localDecimalFormat);
  }
  
  public final DecimalFormat getDecimalFormat(String paramString)
  {
    if (_formatSymbols != null)
    {
      if (paramString == null) {
        paramString = "";
      }
      DecimalFormat localDecimalFormat = (DecimalFormat)_formatSymbols.get(paramString);
      if (localDecimalFormat == null) {
        localDecimalFormat = (DecimalFormat)_formatSymbols.get("");
      }
      return localDecimalFormat;
    }
    return null;
  }
  
  public final void prepassDocument(DOM paramDOM)
  {
    setIndexSize(paramDOM.getSize());
    buildIDIndex(paramDOM);
  }
  
  private final void buildIDIndex(DOM paramDOM)
  {
    setRootForKeys(paramDOM.getDocument());
    if ((paramDOM instanceof DOMEnhancedForDTM))
    {
      DOMEnhancedForDTM localDOMEnhancedForDTM = (DOMEnhancedForDTM)paramDOM;
      if (localDOMEnhancedForDTM.hasDOMSource())
      {
        buildKeyIndex("##id", paramDOM);
        return;
      }
      Map localMap = localDOMEnhancedForDTM.getElementsWithIDs();
      if (localMap == null) {
        return;
      }
      int i = 0;
      Iterator localIterator = localMap.entrySet().iterator();
      while (localIterator.hasNext())
      {
        Map.Entry localEntry = (Map.Entry)localIterator.next();
        int j = paramDOM.getNodeHandle(((Integer)localEntry.getValue()).intValue());
        buildKeyIndex("##id", j, (String)localEntry.getKey());
        i = 1;
      }
      if (i != 0) {
        setKeyIndexDom("##id", paramDOM);
      }
    }
  }
  
  public final void postInitialization()
  {
    if (transletVersion < 101)
    {
      int i = namesArray.length;
      String[] arrayOfString1 = new String[i];
      String[] arrayOfString2 = new String[i];
      int[] arrayOfInt = new int[i];
      for (int j = 0; j < i; j++)
      {
        String str = namesArray[j];
        int k = str.lastIndexOf(':');
        int m = k + 1;
        if (k > -1) {
          arrayOfString1[j] = str.substring(0, k);
        }
        if (str.charAt(m) == '@')
        {
          m++;
          arrayOfInt[j] = 2;
        }
        else if (str.charAt(m) == '?')
        {
          m++;
          arrayOfInt[j] = 13;
        }
        else
        {
          arrayOfInt[j] = 1;
        }
        arrayOfString2[j] = (m == 0 ? str : str.substring(m));
      }
      namesArray = arrayOfString2;
      urisArray = arrayOfString1;
      typesArray = arrayOfInt;
    }
    if (transletVersion > 101) {
      BasisLibrary.runTimeError("UNKNOWN_TRANSLET_VERSION_ERR", getClass().getName());
    }
  }
  
  public void setIndexSize(int paramInt)
  {
    if (paramInt > _indexSize) {
      _indexSize = paramInt;
    }
  }
  
  public KeyIndex createKeyIndex()
  {
    return new KeyIndex(_indexSize);
  }
  
  public void buildKeyIndex(String paramString1, int paramInt, String paramString2)
  {
    KeyIndex localKeyIndex = buildKeyIndexHelper(paramString1);
    localKeyIndex.add(paramString2, paramInt, _currentRootForKeys);
  }
  
  public void buildKeyIndex(String paramString, DOM paramDOM)
  {
    KeyIndex localKeyIndex = buildKeyIndexHelper(paramString);
    localKeyIndex.setDom(paramDOM, paramDOM.getDocument());
  }
  
  private KeyIndex buildKeyIndexHelper(String paramString)
  {
    if (_keyIndexes == null) {
      _keyIndexes = new HashMap();
    }
    KeyIndex localKeyIndex = (KeyIndex)_keyIndexes.get(paramString);
    if (localKeyIndex == null) {
      _keyIndexes.put(paramString, localKeyIndex = new KeyIndex(_indexSize));
    }
    return localKeyIndex;
  }
  
  public KeyIndex getKeyIndex(String paramString)
  {
    if (_keyIndexes == null) {
      return _emptyKeyIndex != null ? _emptyKeyIndex : (_emptyKeyIndex = new KeyIndex(1));
    }
    KeyIndex localKeyIndex = (KeyIndex)_keyIndexes.get(paramString);
    if (localKeyIndex == null) {
      return _emptyKeyIndex != null ? _emptyKeyIndex : (_emptyKeyIndex = new KeyIndex(1));
    }
    return localKeyIndex;
  }
  
  private void setRootForKeys(int paramInt)
  {
    _currentRootForKeys = paramInt;
  }
  
  public void buildKeys(DOM paramDOM, DTMAxisIterator paramDTMAxisIterator, SerializationHandler paramSerializationHandler, int paramInt)
    throws TransletException
  {}
  
  public void setKeyIndexDom(String paramString, DOM paramDOM)
  {
    getKeyIndex(paramString).setDom(paramDOM, paramDOM.getDocument());
  }
  
  public void setDOMCache(DOMCache paramDOMCache)
  {
    _domCache = paramDOMCache;
  }
  
  public DOMCache getDOMCache()
  {
    return _domCache;
  }
  
  public SerializationHandler openOutputHandler(String paramString, boolean paramBoolean)
    throws TransletException
  {
    try
    {
      TransletOutputHandlerFactory localTransletOutputHandlerFactory = TransletOutputHandlerFactory.newInstance();
      String str = new File(paramString).getParent();
      if ((null != str) && (str.length() > 0))
      {
        localObject = new File(str);
        ((File)localObject).mkdirs();
      }
      localTransletOutputHandlerFactory.setEncoding(_encoding);
      localTransletOutputHandlerFactory.setOutputMethod(_method);
      localTransletOutputHandlerFactory.setOutputStream(new BufferedOutputStream(new FileOutputStream(paramString, paramBoolean)));
      localTransletOutputHandlerFactory.setOutputType(0);
      Object localObject = localTransletOutputHandlerFactory.getSerializationHandler();
      transferOutputSettings((SerializationHandler)localObject);
      ((SerializationHandler)localObject).startDocument();
      return (SerializationHandler)localObject;
    }
    catch (Exception localException)
    {
      throw new TransletException(localException);
    }
  }
  
  public SerializationHandler openOutputHandler(String paramString)
    throws TransletException
  {
    return openOutputHandler(paramString, false);
  }
  
  public void closeOutputHandler(SerializationHandler paramSerializationHandler)
  {
    try
    {
      paramSerializationHandler.endDocument();
      paramSerializationHandler.close();
    }
    catch (Exception localException) {}
  }
  
  public abstract void transform(DOM paramDOM, DTMAxisIterator paramDTMAxisIterator, SerializationHandler paramSerializationHandler)
    throws TransletException;
  
  /* Error */
  public final void transform(DOM paramDOM, SerializationHandler paramSerializationHandler)
    throws TransletException
  {
    // Byte code:
    //   0: aload_0
    //   1: aload_1
    //   2: aload_1
    //   3: invokeinterface 604 1 0
    //   8: aload_2
    //   9: invokevirtual 550	com/sun/org/apache/xalan/internal/xsltc/runtime/AbstractTranslet:transform	(Lcom/sun/org/apache/xalan/internal/xsltc/DOM;Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;Lcom/sun/org/apache/xml/internal/serializer/SerializationHandler;)V
    //   12: aload_0
    //   13: aconst_null
    //   14: putfield 525	com/sun/org/apache/xalan/internal/xsltc/runtime/AbstractTranslet:_keyIndexes	Ljava/util/Map;
    //   17: goto +11 -> 28
    //   20: astore_3
    //   21: aload_0
    //   22: aconst_null
    //   23: putfield 525	com/sun/org/apache/xalan/internal/xsltc/runtime/AbstractTranslet:_keyIndexes	Ljava/util/Map;
    //   26: aload_3
    //   27: athrow
    //   28: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	29	0	this	AbstractTranslet
    //   0	29	1	paramDOM	DOM
    //   0	29	2	paramSerializationHandler	SerializationHandler
    //   20	7	3	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   0	12	20	finally
  }
  
  public final void characters(String paramString, SerializationHandler paramSerializationHandler)
    throws TransletException
  {
    if (paramString != null) {
      try
      {
        paramSerializationHandler.characters(paramString);
      }
      catch (Exception localException)
      {
        throw new TransletException(localException);
      }
    }
  }
  
  public void addCdataElement(String paramString)
  {
    if (_cdata == null) {
      _cdata = new Vector();
    }
    int i = paramString.lastIndexOf(':');
    if (i > 0)
    {
      String str1 = paramString.substring(0, i);
      String str2 = paramString.substring(i + 1);
      _cdata.addElement(str1);
      _cdata.addElement(str2);
    }
    else
    {
      _cdata.addElement(null);
      _cdata.addElement(paramString);
    }
  }
  
  protected void transferOutputSettings(SerializationHandler paramSerializationHandler)
  {
    if (_method != null)
    {
      if (_method.equals("xml"))
      {
        if (_standalone != null) {
          paramSerializationHandler.setStandalone(_standalone);
        }
        if (_omitHeader) {
          paramSerializationHandler.setOmitXMLDeclaration(true);
        }
        paramSerializationHandler.setCdataSectionElements(_cdata);
        if (_version != null) {
          paramSerializationHandler.setVersion(_version);
        }
        paramSerializationHandler.setIndent(_indent);
        paramSerializationHandler.setIndentAmount(_indentamount);
        if (_doctypeSystem != null) {
          paramSerializationHandler.setDoctype(_doctypeSystem, _doctypePublic);
        }
        paramSerializationHandler.setIsStandalone(_isStandalone);
      }
      else if (_method.equals("html"))
      {
        paramSerializationHandler.setIndent(_indent);
        paramSerializationHandler.setDoctype(_doctypeSystem, _doctypePublic);
        if (_mediaType != null) {
          paramSerializationHandler.setMediaType(_mediaType);
        }
      }
    }
    else
    {
      paramSerializationHandler.setCdataSectionElements(_cdata);
      if (_version != null) {
        paramSerializationHandler.setVersion(_version);
      }
      if (_standalone != null) {
        paramSerializationHandler.setStandalone(_standalone);
      }
      if (_omitHeader) {
        paramSerializationHandler.setOmitXMLDeclaration(true);
      }
      paramSerializationHandler.setIndent(_indent);
      paramSerializationHandler.setDoctype(_doctypeSystem, _doctypePublic);
      paramSerializationHandler.setIsStandalone(_isStandalone);
    }
  }
  
  public void addAuxiliaryClass(Class paramClass)
  {
    if (_auxClasses == null) {
      _auxClasses = new HashMap();
    }
    _auxClasses.put(paramClass.getName(), paramClass);
  }
  
  public void setAuxiliaryClasses(Map<String, Class<?>> paramMap)
  {
    _auxClasses = paramMap;
  }
  
  public Class getAuxiliaryClass(String paramString)
  {
    if (_auxClasses == null) {
      return null;
    }
    return (Class)_auxClasses.get(paramString);
  }
  
  public String[] getNamesArray()
  {
    return namesArray;
  }
  
  public String[] getUrisArray()
  {
    return urisArray;
  }
  
  public int[] getTypesArray()
  {
    return typesArray;
  }
  
  public String[] getNamespaceArray()
  {
    return namespaceArray;
  }
  
  public boolean hasIdCall()
  {
    return _hasIdCall;
  }
  
  public Templates getTemplates()
  {
    return _templates;
  }
  
  public void setTemplates(Templates paramTemplates)
  {
    _templates = paramTemplates;
  }
  
  public boolean useServicesMechnism()
  {
    return _useServicesMechanism;
  }
  
  public void setServicesMechnism(boolean paramBoolean)
  {
    _useServicesMechanism = paramBoolean;
  }
  
  public String getAllowedProtocols()
  {
    return _accessExternalStylesheet;
  }
  
  public void setAllowedProtocols(String paramString)
  {
    _accessExternalStylesheet = paramString;
  }
  
  public Document newDocument(String paramString1, String paramString2)
    throws ParserConfigurationException
  {
    if (_domImplementation == null)
    {
      DocumentBuilderFactory localDocumentBuilderFactory = FactoryImpl.getDOMFactory(_useServicesMechanism);
      _domImplementation = localDocumentBuilderFactory.newDocumentBuilder().getDOMImplementation();
    }
    return _domImplementation.createDocument(paramString1, paramString2, null);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\runtime\AbstractTranslet.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */