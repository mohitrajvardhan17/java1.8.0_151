package com.sun.org.apache.xalan.internal.xsltc.compiler.util;

import com.sun.org.apache.bcel.internal.classfile.Method;
import com.sun.org.apache.bcel.internal.generic.ALOAD;
import com.sun.org.apache.bcel.internal.generic.ClassGen;
import com.sun.org.apache.bcel.internal.generic.Instruction;
import com.sun.org.apache.xalan.internal.xsltc.compiler.Parser;
import com.sun.org.apache.xalan.internal.xsltc.compiler.Stylesheet;

public class ClassGenerator
  extends ClassGen
{
  protected static final int TRANSLET_INDEX = 0;
  protected static int INVALID_INDEX = -1;
  private Stylesheet _stylesheet;
  private final Parser _parser;
  private final Instruction _aloadTranslet;
  private final String _domClass;
  private final String _domClassSig;
  private final String _applyTemplatesSig;
  private final String _applyTemplatesSigForImport;
  
  public ClassGenerator(String paramString1, String paramString2, String paramString3, int paramInt, String[] paramArrayOfString, Stylesheet paramStylesheet)
  {
    super(paramString1, paramString2, paramString3, paramInt, paramArrayOfString);
    _stylesheet = paramStylesheet;
    _parser = paramStylesheet.getParser();
    _aloadTranslet = new ALOAD(0);
    if (paramStylesheet.isMultiDocument())
    {
      _domClass = "com.sun.org.apache.xalan.internal.xsltc.dom.MultiDOM";
      _domClassSig = "Lcom/sun/org/apache/xalan/internal/xsltc/dom/MultiDOM;";
    }
    else
    {
      _domClass = "com.sun.org.apache.xalan.internal.xsltc.dom.DOMAdapter";
      _domClassSig = "Lcom/sun/org/apache/xalan/internal/xsltc/dom/DOMAdapter;";
    }
    _applyTemplatesSig = "(Lcom/sun/org/apache/xalan/internal/xsltc/DOM;Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;Lcom/sun/org/apache/xml/internal/serializer/SerializationHandler;)V";
    _applyTemplatesSigForImport = "(Lcom/sun/org/apache/xalan/internal/xsltc/DOM;Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;Lcom/sun/org/apache/xml/internal/serializer/SerializationHandler;I)V";
  }
  
  public final Parser getParser()
  {
    return _parser;
  }
  
  public final Stylesheet getStylesheet()
  {
    return _stylesheet;
  }
  
  public final String getClassName()
  {
    return _stylesheet.getClassName();
  }
  
  public Instruction loadTranslet()
  {
    return _aloadTranslet;
  }
  
  public final String getDOMClass()
  {
    return _domClass;
  }
  
  public final String getDOMClassSig()
  {
    return _domClassSig;
  }
  
  public final String getApplyTemplatesSig()
  {
    return _applyTemplatesSig;
  }
  
  public final String getApplyTemplatesSigForImport()
  {
    return _applyTemplatesSigForImport;
  }
  
  public boolean isExternal()
  {
    return false;
  }
  
  public void addMethod(MethodGenerator paramMethodGenerator)
  {
    Method[] arrayOfMethod = paramMethodGenerator.getGeneratedMethods(this);
    for (int i = 0; i < arrayOfMethod.length; i++) {
      addMethod(arrayOfMethod[i]);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\util\ClassGenerator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */