package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.ALOAD;
import com.sun.org.apache.bcel.internal.generic.BranchHandle;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.IF_ICMPEQ;
import com.sun.org.apache.bcel.internal.generic.ILOAD;
import com.sun.org.apache.bcel.internal.generic.INVOKEINTERFACE;
import com.sun.org.apache.bcel.internal.generic.INVOKEVIRTUAL;
import com.sun.org.apache.bcel.internal.generic.InstructionHandle;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.PUSH;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Util;
import java.util.StringTokenizer;
import java.util.Vector;

final class Whitespace
  extends TopLevelElement
{
  public static final int USE_PREDICATE = 0;
  public static final int STRIP_SPACE = 1;
  public static final int PRESERVE_SPACE = 2;
  public static final int RULE_NONE = 0;
  public static final int RULE_ELEMENT = 1;
  public static final int RULE_NAMESPACE = 2;
  public static final int RULE_ALL = 3;
  private String _elementList;
  private int _action;
  private int _importPrecedence;
  
  Whitespace() {}
  
  public void parseContents(Parser paramParser)
  {
    _action = (_qname.getLocalPart().endsWith("strip-space") ? 1 : 2);
    _importPrecedence = paramParser.getCurrentImportPrecedence();
    _elementList = getAttribute("elements");
    if ((_elementList == null) || (_elementList.length() == 0))
    {
      reportError(this, paramParser, "REQUIRED_ATTR_ERR", "elements");
      return;
    }
    SymbolTable localSymbolTable = paramParser.getSymbolTable();
    StringTokenizer localStringTokenizer = new StringTokenizer(_elementList);
    StringBuffer localStringBuffer = new StringBuffer("");
    while (localStringTokenizer.hasMoreElements())
    {
      String str1 = localStringTokenizer.nextToken();
      int i = str1.indexOf(':');
      if (i != -1)
      {
        String str2 = lookupNamespace(str1.substring(0, i));
        if (str2 != null) {
          localStringBuffer.append(str2).append(':').append(str1.substring(i + 1));
        } else {
          localStringBuffer.append(str1);
        }
      }
      else
      {
        localStringBuffer.append(str1);
      }
      if (localStringTokenizer.hasMoreElements()) {
        localStringBuffer.append(" ");
      }
    }
    _elementList = localStringBuffer.toString();
  }
  
  public Vector getRules()
  {
    Vector localVector = new Vector();
    StringTokenizer localStringTokenizer = new StringTokenizer(_elementList);
    while (localStringTokenizer.hasMoreElements()) {
      localVector.add(new WhitespaceRule(_action, localStringTokenizer.nextToken(), _importPrecedence));
    }
    return localVector;
  }
  
  private static WhitespaceRule findContradictingRule(Vector paramVector, WhitespaceRule paramWhitespaceRule)
  {
    for (int i = 0; i < paramVector.size(); i++)
    {
      WhitespaceRule localWhitespaceRule = (WhitespaceRule)paramVector.elementAt(i);
      if (localWhitespaceRule == paramWhitespaceRule) {
        return null;
      }
      switch (localWhitespaceRule.getStrength())
      {
      case 3: 
        return localWhitespaceRule;
      case 1: 
      case 2: 
        if ((paramWhitespaceRule.getElement().equals(localWhitespaceRule.getElement())) && (paramWhitespaceRule.getNamespace().equals(localWhitespaceRule.getNamespace()))) {
          return localWhitespaceRule;
        }
        break;
      }
    }
    return null;
  }
  
  private static int prioritizeRules(Vector paramVector)
  {
    int i = 2;
    quicksort(paramVector, 0, paramVector.size() - 1);
    int j = 0;
    WhitespaceRule localWhitespaceRule;
    for (int k = 0; k < paramVector.size(); k++)
    {
      localWhitespaceRule = (WhitespaceRule)paramVector.elementAt(k);
      if (localWhitespaceRule.getAction() == 1) {
        j = 1;
      }
    }
    if (j == 0)
    {
      paramVector.removeAllElements();
      return 2;
    }
    k = 0;
    while (k < paramVector.size())
    {
      localWhitespaceRule = (WhitespaceRule)paramVector.elementAt(k);
      if (findContradictingRule(paramVector, localWhitespaceRule) != null)
      {
        paramVector.remove(k);
      }
      else
      {
        if (localWhitespaceRule.getStrength() == 3)
        {
          i = localWhitespaceRule.getAction();
          for (int m = k; m < paramVector.size(); m++) {
            paramVector.removeElementAt(m);
          }
        }
        k++;
      }
    }
    if (paramVector.size() == 0) {
      return i;
    }
    do
    {
      localWhitespaceRule = (WhitespaceRule)paramVector.lastElement();
      if (localWhitespaceRule.getAction() != i) {
        break;
      }
      paramVector.removeElementAt(paramVector.size() - 1);
    } while (paramVector.size() > 0);
    return i;
  }
  
  public static void compileStripSpace(BranchHandle[] paramArrayOfBranchHandle, int paramInt, InstructionList paramInstructionList)
  {
    InstructionHandle localInstructionHandle = paramInstructionList.append(ICONST_1);
    paramInstructionList.append(IRETURN);
    for (int i = 0; i < paramInt; i++) {
      paramArrayOfBranchHandle[i].setTarget(localInstructionHandle);
    }
  }
  
  public static void compilePreserveSpace(BranchHandle[] paramArrayOfBranchHandle, int paramInt, InstructionList paramInstructionList)
  {
    InstructionHandle localInstructionHandle = paramInstructionList.append(ICONST_0);
    paramInstructionList.append(IRETURN);
    for (int i = 0; i < paramInt; i++) {
      paramArrayOfBranchHandle[i].setTarget(localInstructionHandle);
    }
  }
  
  private static void compilePredicate(Vector paramVector, int paramInt, ClassGenerator paramClassGenerator)
  {
    ConstantPoolGen localConstantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList localInstructionList = new InstructionList();
    XSLTC localXSLTC = paramClassGenerator.getParser().getXSLTC();
    MethodGenerator localMethodGenerator = new MethodGenerator(17, com.sun.org.apache.bcel.internal.generic.Type.BOOLEAN, new com.sun.org.apache.bcel.internal.generic.Type[] { Util.getJCRefType("Lcom/sun/org/apache/xalan/internal/xsltc/DOM;"), com.sun.org.apache.bcel.internal.generic.Type.INT, com.sun.org.apache.bcel.internal.generic.Type.INT }, new String[] { "dom", "node", "type" }, "stripSpace", paramClassGenerator.getClassName(), localInstructionList, localConstantPoolGen);
    paramClassGenerator.addInterface("com/sun/org/apache/xalan/internal/xsltc/StripFilter");
    int i = localMethodGenerator.getLocalIndex("dom");
    int j = localMethodGenerator.getLocalIndex("node");
    int k = localMethodGenerator.getLocalIndex("type");
    BranchHandle[] arrayOfBranchHandle1 = new BranchHandle[paramVector.size()];
    BranchHandle[] arrayOfBranchHandle2 = new BranchHandle[paramVector.size()];
    int m = 0;
    int n = 0;
    for (int i1 = 0; i1 < paramVector.size(); i1++)
    {
      WhitespaceRule localWhitespaceRule = (WhitespaceRule)paramVector.elementAt(i1);
      int i2 = localConstantPoolGen.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "getNamespaceName", "(I)Ljava/lang/String;");
      int i3 = localConstantPoolGen.addMethodref("java/lang/String", "compareTo", "(Ljava/lang/String;)I");
      if (localWhitespaceRule.getStrength() == 2)
      {
        localInstructionList.append(new ALOAD(i));
        localInstructionList.append(new ILOAD(j));
        localInstructionList.append(new INVOKEINTERFACE(i2, 2));
        localInstructionList.append(new PUSH(localConstantPoolGen, localWhitespaceRule.getNamespace()));
        localInstructionList.append(new INVOKEVIRTUAL(i3));
        localInstructionList.append(ICONST_0);
        if (localWhitespaceRule.getAction() == 1) {
          arrayOfBranchHandle1[(m++)] = localInstructionList.append(new IF_ICMPEQ(null));
        } else {
          arrayOfBranchHandle2[(n++)] = localInstructionList.append(new IF_ICMPEQ(null));
        }
      }
      else if (localWhitespaceRule.getStrength() == 1)
      {
        Parser localParser = paramClassGenerator.getParser();
        QName localQName;
        if (localWhitespaceRule.getNamespace() != "") {
          localQName = localParser.getQName(localWhitespaceRule.getNamespace(), null, localWhitespaceRule.getElement());
        } else {
          localQName = localParser.getQName(localWhitespaceRule.getElement());
        }
        int i4 = localXSLTC.registerElement(localQName);
        localInstructionList.append(new ILOAD(k));
        localInstructionList.append(new PUSH(localConstantPoolGen, i4));
        if (localWhitespaceRule.getAction() == 1) {
          arrayOfBranchHandle1[(m++)] = localInstructionList.append(new IF_ICMPEQ(null));
        } else {
          arrayOfBranchHandle2[(n++)] = localInstructionList.append(new IF_ICMPEQ(null));
        }
      }
    }
    if (paramInt == 1)
    {
      compileStripSpace(arrayOfBranchHandle1, m, localInstructionList);
      compilePreserveSpace(arrayOfBranchHandle2, n, localInstructionList);
    }
    else
    {
      compilePreserveSpace(arrayOfBranchHandle2, n, localInstructionList);
      compileStripSpace(arrayOfBranchHandle1, m, localInstructionList);
    }
    paramClassGenerator.addMethod(localMethodGenerator);
  }
  
  private static void compileDefault(int paramInt, ClassGenerator paramClassGenerator)
  {
    ConstantPoolGen localConstantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList localInstructionList = new InstructionList();
    XSLTC localXSLTC = paramClassGenerator.getParser().getXSLTC();
    MethodGenerator localMethodGenerator = new MethodGenerator(17, com.sun.org.apache.bcel.internal.generic.Type.BOOLEAN, new com.sun.org.apache.bcel.internal.generic.Type[] { Util.getJCRefType("Lcom/sun/org/apache/xalan/internal/xsltc/DOM;"), com.sun.org.apache.bcel.internal.generic.Type.INT, com.sun.org.apache.bcel.internal.generic.Type.INT }, new String[] { "dom", "node", "type" }, "stripSpace", paramClassGenerator.getClassName(), localInstructionList, localConstantPoolGen);
    paramClassGenerator.addInterface("com/sun/org/apache/xalan/internal/xsltc/StripFilter");
    if (paramInt == 1) {
      localInstructionList.append(ICONST_1);
    } else {
      localInstructionList.append(ICONST_0);
    }
    localInstructionList.append(IRETURN);
    paramClassGenerator.addMethod(localMethodGenerator);
  }
  
  public static int translateRules(Vector paramVector, ClassGenerator paramClassGenerator)
  {
    int i = prioritizeRules(paramVector);
    if (paramVector.size() == 0)
    {
      compileDefault(i, paramClassGenerator);
      return i;
    }
    compilePredicate(paramVector, i, paramClassGenerator);
    return 0;
  }
  
  private static void quicksort(Vector paramVector, int paramInt1, int paramInt2)
  {
    while (paramInt1 < paramInt2)
    {
      int i = partition(paramVector, paramInt1, paramInt2);
      quicksort(paramVector, paramInt1, i);
      paramInt1 = i + 1;
    }
  }
  
  private static int partition(Vector paramVector, int paramInt1, int paramInt2)
  {
    WhitespaceRule localWhitespaceRule1 = (WhitespaceRule)paramVector.elementAt(paramInt1 + paramInt2 >>> 1);
    int i = paramInt1 - 1;
    int j = paramInt2 + 1;
    for (;;)
    {
      if (localWhitespaceRule1.compareTo((WhitespaceRule)paramVector.elementAt(--j)) >= 0)
      {
        while (localWhitespaceRule1.compareTo((WhitespaceRule)paramVector.elementAt(++i)) > 0) {}
        if (i >= j) {
          break;
        }
        WhitespaceRule localWhitespaceRule2 = (WhitespaceRule)paramVector.elementAt(i);
        paramVector.setElementAt(paramVector.elementAt(j), i);
        paramVector.setElementAt(localWhitespaceRule2, j);
      }
    }
    return j;
  }
  
  public com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type typeCheck(SymbolTable paramSymbolTable)
    throws TypeCheckError
  {
    return com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type.Void;
  }
  
  public void translate(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator) {}
  
  private static final class WhitespaceRule
  {
    private final int _action;
    private String _namespace;
    private String _element;
    private int _type;
    private int _priority;
    
    public WhitespaceRule(int paramInt1, String paramString, int paramInt2)
    {
      _action = paramInt1;
      int i = paramString.lastIndexOf(':');
      if (i >= 0)
      {
        _namespace = paramString.substring(0, i);
        _element = paramString.substring(i + 1, paramString.length());
      }
      else
      {
        _namespace = "";
        _element = paramString;
      }
      _priority = (paramInt2 << 2);
      if (_element.equals("*"))
      {
        if (_namespace == "")
        {
          _type = 3;
          _priority += 2;
        }
        else
        {
          _type = 2;
          _priority += 1;
        }
      }
      else {
        _type = 1;
      }
    }
    
    public int compareTo(WhitespaceRule paramWhitespaceRule)
    {
      return _priority > _priority ? 1 : _priority < _priority ? -1 : 0;
    }
    
    public int getAction()
    {
      return _action;
    }
    
    public int getStrength()
    {
      return _type;
    }
    
    public int getPriority()
    {
      return _priority;
    }
    
    public String getElement()
    {
      return _element;
    }
    
    public String getNamespace()
    {
      return _namespace;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\Whitespace.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */