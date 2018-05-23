package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.GETSTATIC;
import com.sun.org.apache.bcel.internal.generic.INVOKESPECIAL;
import com.sun.org.apache.bcel.internal.generic.INVOKEVIRTUAL;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.NEW;
import com.sun.org.apache.bcel.internal.generic.PUSH;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMsg;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xml.internal.utils.XML11Char;
import org.xml.sax.helpers.AttributesImpl;

final class DecimalFormatting
  extends TopLevelElement
{
  private static final String DFS_CLASS = "java.text.DecimalFormatSymbols";
  private static final String DFS_SIG = "Ljava/text/DecimalFormatSymbols;";
  private QName _name = null;
  
  DecimalFormatting() {}
  
  public Type typeCheck(SymbolTable paramSymbolTable)
    throws TypeCheckError
  {
    return Type.Void;
  }
  
  public void parseContents(Parser paramParser)
  {
    String str = getAttribute("name");
    if ((str.length() > 0) && (!XML11Char.isXML11ValidQName(str)))
    {
      localObject = new ErrorMsg("INVALID_QNAME_ERR", str, this);
      paramParser.reportError(3, (ErrorMsg)localObject);
    }
    _name = paramParser.getQNameIgnoreDefaultNs(str);
    if (_name == null) {
      _name = paramParser.getQNameIgnoreDefaultNs("");
    }
    Object localObject = paramParser.getSymbolTable();
    if (((SymbolTable)localObject).getDecimalFormatting(_name) != null) {
      reportWarning(this, paramParser, "SYMBOLS_REDEF_ERR", _name.toString());
    } else {
      ((SymbolTable)localObject).addDecimalFormatting(_name, this);
    }
  }
  
  public void translate(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator)
  {
    ConstantPoolGen localConstantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList localInstructionList = paramMethodGenerator.getInstructionList();
    int i = localConstantPoolGen.addMethodref("java.text.DecimalFormatSymbols", "<init>", "(Ljava/util/Locale;)V");
    localInstructionList.append(paramClassGenerator.loadTranslet());
    localInstructionList.append(new PUSH(localConstantPoolGen, _name.toString()));
    localInstructionList.append(new NEW(localConstantPoolGen.addClass("java.text.DecimalFormatSymbols")));
    localInstructionList.append(DUP);
    localInstructionList.append(new GETSTATIC(localConstantPoolGen.addFieldref("java.util.Locale", "US", "Ljava/util/Locale;")));
    localInstructionList.append(new INVOKESPECIAL(i));
    String str1 = getAttribute("NaN");
    if ((str1 == null) || (str1.equals("")))
    {
      j = localConstantPoolGen.addMethodref("java.text.DecimalFormatSymbols", "setNaN", "(Ljava/lang/String;)V");
      localInstructionList.append(DUP);
      localInstructionList.append(new PUSH(localConstantPoolGen, "NaN"));
      localInstructionList.append(new INVOKEVIRTUAL(j));
    }
    str1 = getAttribute("infinity");
    if ((str1 == null) || (str1.equals("")))
    {
      j = localConstantPoolGen.addMethodref("java.text.DecimalFormatSymbols", "setInfinity", "(Ljava/lang/String;)V");
      localInstructionList.append(DUP);
      localInstructionList.append(new PUSH(localConstantPoolGen, "Infinity"));
      localInstructionList.append(new INVOKEVIRTUAL(j));
    }
    int j = _attributes.getLength();
    for (int k = 0; k < j; k++)
    {
      String str2 = _attributes.getQName(k);
      String str3 = _attributes.getValue(k);
      int m = 1;
      int n = 0;
      if (str2.equals("decimal-separator"))
      {
        n = localConstantPoolGen.addMethodref("java.text.DecimalFormatSymbols", "setDecimalSeparator", "(C)V");
      }
      else if (str2.equals("grouping-separator"))
      {
        n = localConstantPoolGen.addMethodref("java.text.DecimalFormatSymbols", "setGroupingSeparator", "(C)V");
      }
      else if (str2.equals("minus-sign"))
      {
        n = localConstantPoolGen.addMethodref("java.text.DecimalFormatSymbols", "setMinusSign", "(C)V");
      }
      else if (str2.equals("percent"))
      {
        n = localConstantPoolGen.addMethodref("java.text.DecimalFormatSymbols", "setPercent", "(C)V");
      }
      else if (str2.equals("per-mille"))
      {
        n = localConstantPoolGen.addMethodref("java.text.DecimalFormatSymbols", "setPerMill", "(C)V");
      }
      else if (str2.equals("zero-digit"))
      {
        n = localConstantPoolGen.addMethodref("java.text.DecimalFormatSymbols", "setZeroDigit", "(C)V");
      }
      else if (str2.equals("digit"))
      {
        n = localConstantPoolGen.addMethodref("java.text.DecimalFormatSymbols", "setDigit", "(C)V");
      }
      else if (str2.equals("pattern-separator"))
      {
        n = localConstantPoolGen.addMethodref("java.text.DecimalFormatSymbols", "setPatternSeparator", "(C)V");
      }
      else if (str2.equals("NaN"))
      {
        n = localConstantPoolGen.addMethodref("java.text.DecimalFormatSymbols", "setNaN", "(Ljava/lang/String;)V");
        localInstructionList.append(DUP);
        localInstructionList.append(new PUSH(localConstantPoolGen, str3));
        localInstructionList.append(new INVOKEVIRTUAL(n));
        m = 0;
      }
      else if (str2.equals("infinity"))
      {
        n = localConstantPoolGen.addMethodref("java.text.DecimalFormatSymbols", "setInfinity", "(Ljava/lang/String;)V");
        localInstructionList.append(DUP);
        localInstructionList.append(new PUSH(localConstantPoolGen, str3));
        localInstructionList.append(new INVOKEVIRTUAL(n));
        m = 0;
      }
      else
      {
        m = 0;
      }
      if (m != 0)
      {
        localInstructionList.append(DUP);
        localInstructionList.append(new PUSH(localConstantPoolGen, str3.charAt(0)));
        localInstructionList.append(new INVOKEVIRTUAL(n));
      }
    }
    k = localConstantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet", "addDecimalFormat", "(Ljava/lang/String;Ljava/text/DecimalFormatSymbols;)V");
    localInstructionList.append(new INVOKEVIRTUAL(k));
  }
  
  public static void translateDefaultDFS(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator)
  {
    ConstantPoolGen localConstantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList localInstructionList = paramMethodGenerator.getInstructionList();
    int i = localConstantPoolGen.addMethodref("java.text.DecimalFormatSymbols", "<init>", "(Ljava/util/Locale;)V");
    localInstructionList.append(paramClassGenerator.loadTranslet());
    localInstructionList.append(new PUSH(localConstantPoolGen, ""));
    localInstructionList.append(new NEW(localConstantPoolGen.addClass("java.text.DecimalFormatSymbols")));
    localInstructionList.append(DUP);
    localInstructionList.append(new GETSTATIC(localConstantPoolGen.addFieldref("java.util.Locale", "US", "Ljava/util/Locale;")));
    localInstructionList.append(new INVOKESPECIAL(i));
    int j = localConstantPoolGen.addMethodref("java.text.DecimalFormatSymbols", "setNaN", "(Ljava/lang/String;)V");
    localInstructionList.append(DUP);
    localInstructionList.append(new PUSH(localConstantPoolGen, "NaN"));
    localInstructionList.append(new INVOKEVIRTUAL(j));
    int k = localConstantPoolGen.addMethodref("java.text.DecimalFormatSymbols", "setInfinity", "(Ljava/lang/String;)V");
    localInstructionList.append(DUP);
    localInstructionList.append(new PUSH(localConstantPoolGen, "Infinity"));
    localInstructionList.append(new INVOKEVIRTUAL(k));
    int m = localConstantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet", "addDecimalFormat", "(Ljava/lang/String;Ljava/text/DecimalFormatSymbols;)V");
    localInstructionList.append(new INVOKEVIRTUAL(m));
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\DecimalFormatting.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */