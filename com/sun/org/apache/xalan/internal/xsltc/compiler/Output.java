package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.INVOKEVIRTUAL;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.PUSH;
import com.sun.org.apache.bcel.internal.generic.PUTFIELD;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMsg;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Util;
import com.sun.org.apache.xml.internal.serializer.Encodings;
import com.sun.org.apache.xml.internal.utils.XML11Char;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.Properties;
import java.util.StringTokenizer;

final class Output
  extends TopLevelElement
{
  private String _version;
  private String _method;
  private String _encoding;
  private boolean _omitHeader = false;
  private String _standalone;
  private String _doctypePublic;
  private String _doctypeSystem;
  private String _cdata;
  private boolean _indent = false;
  private String _mediaType;
  private String _indentamount;
  private boolean _disabled = false;
  private static final String STRING_SIG = "Ljava/lang/String;";
  private static final String XML_VERSION = "1.0";
  private static final String HTML_VERSION = "4.0";
  
  Output() {}
  
  public void display(int paramInt)
  {
    indent(paramInt);
    Util.println("Output " + _method);
  }
  
  public void disable()
  {
    _disabled = true;
  }
  
  public boolean enabled()
  {
    return !_disabled;
  }
  
  public String getCdata()
  {
    return _cdata;
  }
  
  public String getOutputMethod()
  {
    return _method;
  }
  
  private void transferAttribute(Output paramOutput, String paramString)
  {
    if ((!hasAttribute(paramString)) && (paramOutput.hasAttribute(paramString))) {
      addAttribute(paramString, paramOutput.getAttribute(paramString));
    }
  }
  
  public void mergeOutput(Output paramOutput)
  {
    transferAttribute(paramOutput, "version");
    transferAttribute(paramOutput, "method");
    transferAttribute(paramOutput, "encoding");
    transferAttribute(paramOutput, "doctype-system");
    transferAttribute(paramOutput, "doctype-public");
    transferAttribute(paramOutput, "media-type");
    transferAttribute(paramOutput, "indent");
    transferAttribute(paramOutput, "omit-xml-declaration");
    transferAttribute(paramOutput, "standalone");
    if (paramOutput.hasAttribute("cdata-section-elements")) {
      addAttribute("cdata-section-elements", paramOutput.getAttribute("cdata-section-elements") + ' ' + getAttribute("cdata-section-elements"));
    }
    String str = lookupPrefix("http://xml.apache.org/xalan");
    if (str != null) {
      transferAttribute(paramOutput, str + ':' + "indent-amount");
    }
    str = lookupPrefix("http://xml.apache.org/xslt");
    if (str != null) {
      transferAttribute(paramOutput, str + ':' + "indent-amount");
    }
  }
  
  public void parseContents(Parser paramParser)
  {
    Properties localProperties = new Properties();
    paramParser.setOutput(this);
    if (_disabled) {
      return;
    }
    String str1 = null;
    _version = getAttribute("version");
    if (_version.equals("")) {
      _version = null;
    } else {
      localProperties.setProperty("version", _version);
    }
    _method = getAttribute("method");
    if (_method.equals("")) {
      _method = null;
    }
    if (_method != null)
    {
      _method = _method.toLowerCase();
      if ((_method.equals("xml")) || (_method.equals("html")) || (_method.equals("text")) || ((XML11Char.isXML11ValidQName(_method)) && (_method.indexOf(":") > 0))) {
        localProperties.setProperty("method", _method);
      } else {
        reportError(this, paramParser, "INVALID_METHOD_IN_OUTPUT", _method);
      }
    }
    _encoding = getAttribute("encoding");
    Object localObject;
    if (_encoding.equals(""))
    {
      _encoding = null;
    }
    else
    {
      try
      {
        String str2 = Encodings.convertMime2JavaEncoding(_encoding);
        localObject = new OutputStreamWriter(System.out, str2);
      }
      catch (UnsupportedEncodingException localUnsupportedEncodingException)
      {
        localObject = new ErrorMsg("UNSUPPORTED_ENCODING", _encoding, this);
        paramParser.reportError(4, (ErrorMsg)localObject);
      }
      localProperties.setProperty("encoding", _encoding);
    }
    str1 = getAttribute("omit-xml-declaration");
    if (!str1.equals(""))
    {
      if (str1.equals("yes")) {
        _omitHeader = true;
      }
      localProperties.setProperty("omit-xml-declaration", str1);
    }
    _standalone = getAttribute("standalone");
    if (_standalone.equals("")) {
      _standalone = null;
    } else {
      localProperties.setProperty("standalone", _standalone);
    }
    _doctypeSystem = getAttribute("doctype-system");
    if (_doctypeSystem.equals("")) {
      _doctypeSystem = null;
    } else {
      localProperties.setProperty("doctype-system", _doctypeSystem);
    }
    _doctypePublic = getAttribute("doctype-public");
    if (_doctypePublic.equals("")) {
      _doctypePublic = null;
    } else {
      localProperties.setProperty("doctype-public", _doctypePublic);
    }
    _cdata = getAttribute("cdata-section-elements");
    if (_cdata.equals(""))
    {
      _cdata = null;
    }
    else
    {
      StringBuffer localStringBuffer = new StringBuffer();
      localObject = new StringTokenizer(_cdata);
      while (((StringTokenizer)localObject).hasMoreTokens())
      {
        String str3 = ((StringTokenizer)localObject).nextToken();
        if (!XML11Char.isXML11ValidQName(str3))
        {
          ErrorMsg localErrorMsg = new ErrorMsg("INVALID_QNAME_ERR", str3, this);
          paramParser.reportError(3, localErrorMsg);
        }
        localStringBuffer.append(paramParser.getQName(str3).toString()).append(' ');
      }
      _cdata = localStringBuffer.toString();
      localProperties.setProperty("cdata-section-elements", _cdata);
    }
    str1 = getAttribute("indent");
    if (!str1.equals(""))
    {
      if (str1.equals("yes")) {
        _indent = true;
      }
      localProperties.setProperty("indent", str1);
    }
    else if ((_method != null) && (_method.equals("html")))
    {
      _indent = true;
    }
    _indentamount = getAttribute(lookupPrefix("http://xml.apache.org/xalan"), "indent-amount");
    if (_indentamount.equals("")) {
      _indentamount = getAttribute(lookupPrefix("http://xml.apache.org/xslt"), "indent-amount");
    }
    if (!_indentamount.equals("")) {
      localProperties.setProperty("indent_amount", _indentamount);
    }
    _mediaType = getAttribute("media-type");
    if (_mediaType.equals("")) {
      _mediaType = null;
    } else {
      localProperties.setProperty("media-type", _mediaType);
    }
    if (_method != null) {
      if (_method.equals("html"))
      {
        if (_version == null) {
          _version = "4.0";
        }
        if (_mediaType == null) {
          _mediaType = "text/html";
        }
      }
      else if ((_method.equals("text")) && (_mediaType == null))
      {
        _mediaType = "text/plain";
      }
    }
    paramParser.getCurrentStylesheet().setOutputProperties(localProperties);
  }
  
  public void translate(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator)
  {
    if (_disabled) {
      return;
    }
    ConstantPoolGen localConstantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList localInstructionList = paramMethodGenerator.getInstructionList();
    int i = 0;
    localInstructionList.append(paramClassGenerator.loadTranslet());
    if ((_version != null) && (!_version.equals("1.0")))
    {
      i = localConstantPoolGen.addFieldref("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet", "_version", "Ljava/lang/String;");
      localInstructionList.append(DUP);
      localInstructionList.append(new PUSH(localConstantPoolGen, _version));
      localInstructionList.append(new PUTFIELD(i));
    }
    if (_method != null)
    {
      i = localConstantPoolGen.addFieldref("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet", "_method", "Ljava/lang/String;");
      localInstructionList.append(DUP);
      localInstructionList.append(new PUSH(localConstantPoolGen, _method));
      localInstructionList.append(new PUTFIELD(i));
    }
    if (_encoding != null)
    {
      i = localConstantPoolGen.addFieldref("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet", "_encoding", "Ljava/lang/String;");
      localInstructionList.append(DUP);
      localInstructionList.append(new PUSH(localConstantPoolGen, _encoding));
      localInstructionList.append(new PUTFIELD(i));
    }
    if (_omitHeader)
    {
      i = localConstantPoolGen.addFieldref("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet", "_omitHeader", "Z");
      localInstructionList.append(DUP);
      localInstructionList.append(new PUSH(localConstantPoolGen, _omitHeader));
      localInstructionList.append(new PUTFIELD(i));
    }
    if (_standalone != null)
    {
      i = localConstantPoolGen.addFieldref("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet", "_standalone", "Ljava/lang/String;");
      localInstructionList.append(DUP);
      localInstructionList.append(new PUSH(localConstantPoolGen, _standalone));
      localInstructionList.append(new PUTFIELD(i));
    }
    i = localConstantPoolGen.addFieldref("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet", "_doctypeSystem", "Ljava/lang/String;");
    localInstructionList.append(DUP);
    localInstructionList.append(new PUSH(localConstantPoolGen, _doctypeSystem));
    localInstructionList.append(new PUTFIELD(i));
    i = localConstantPoolGen.addFieldref("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet", "_doctypePublic", "Ljava/lang/String;");
    localInstructionList.append(DUP);
    localInstructionList.append(new PUSH(localConstantPoolGen, _doctypePublic));
    localInstructionList.append(new PUTFIELD(i));
    if (_mediaType != null)
    {
      i = localConstantPoolGen.addFieldref("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet", "_mediaType", "Ljava/lang/String;");
      localInstructionList.append(DUP);
      localInstructionList.append(new PUSH(localConstantPoolGen, _mediaType));
      localInstructionList.append(new PUTFIELD(i));
    }
    if (_indent)
    {
      i = localConstantPoolGen.addFieldref("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet", "_indent", "Z");
      localInstructionList.append(DUP);
      localInstructionList.append(new PUSH(localConstantPoolGen, _indent));
      localInstructionList.append(new PUTFIELD(i));
    }
    if ((_indentamount != null) && (!_indentamount.equals("")))
    {
      i = localConstantPoolGen.addFieldref("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet", "_indentamount", "I");
      localInstructionList.append(DUP);
      localInstructionList.append(new PUSH(localConstantPoolGen, Integer.parseInt(_indentamount)));
      localInstructionList.append(new PUTFIELD(i));
    }
    if (_cdata != null)
    {
      int j = localConstantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet", "addCdataElement", "(Ljava/lang/String;)V");
      StringTokenizer localStringTokenizer = new StringTokenizer(_cdata);
      while (localStringTokenizer.hasMoreTokens())
      {
        localInstructionList.append(DUP);
        localInstructionList.append(new PUSH(localConstantPoolGen, localStringTokenizer.nextToken()));
        localInstructionList.append(new INVOKEVIRTUAL(j));
      }
    }
    localInstructionList.append(POP);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\Output.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */