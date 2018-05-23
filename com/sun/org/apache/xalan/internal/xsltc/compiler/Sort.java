package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.classfile.Field;
import com.sun.org.apache.bcel.internal.generic.ALOAD;
import com.sun.org.apache.bcel.internal.generic.ANEWARRAY;
import com.sun.org.apache.bcel.internal.generic.ASTORE;
import com.sun.org.apache.bcel.internal.generic.CHECKCAST;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.GETFIELD;
import com.sun.org.apache.bcel.internal.generic.ILOAD;
import com.sun.org.apache.bcel.internal.generic.INVOKEINTERFACE;
import com.sun.org.apache.bcel.internal.generic.INVOKESPECIAL;
import com.sun.org.apache.bcel.internal.generic.InstructionHandle;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.LocalVariableGen;
import com.sun.org.apache.bcel.internal.generic.NEW;
import com.sun.org.apache.bcel.internal.generic.NOP;
import com.sun.org.apache.bcel.internal.generic.PUSH;
import com.sun.org.apache.bcel.internal.generic.PUTFIELD;
import com.sun.org.apache.bcel.internal.generic.TABLESWITCH;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.CompareGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.IntType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.NodeSortRecordFactGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.NodeSortRecordGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.StringType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Util;
import java.util.ArrayList;
import java.util.Vector;

final class Sort
  extends Instruction
  implements Closure
{
  private Expression _select;
  private AttributeValue _order;
  private AttributeValue _caseOrder;
  private AttributeValue _dataType;
  private String _lang;
  private String _className = null;
  private ArrayList<VariableRefBase> _closureVars = null;
  private boolean _needsSortRecordFactory = false;
  
  Sort() {}
  
  public boolean inInnerClass()
  {
    return _className != null;
  }
  
  public Closure getParentClosure()
  {
    return null;
  }
  
  public String getInnerClassName()
  {
    return _className;
  }
  
  public void addVariable(VariableRefBase paramVariableRefBase)
  {
    if (_closureVars == null) {
      _closureVars = new ArrayList();
    }
    if (!_closureVars.contains(paramVariableRefBase))
    {
      _closureVars.add(paramVariableRefBase);
      _needsSortRecordFactory = true;
    }
  }
  
  private void setInnerClassName(String paramString)
  {
    _className = paramString;
  }
  
  public void parseContents(Parser paramParser)
  {
    SyntaxTreeNode localSyntaxTreeNode = getParent();
    if ((!(localSyntaxTreeNode instanceof ApplyTemplates)) && (!(localSyntaxTreeNode instanceof ForEach)))
    {
      reportError(this, paramParser, "STRAY_SORT_ERR", null);
      return;
    }
    _select = paramParser.parseExpression(this, "select", "string(.)");
    String str = getAttribute("order");
    if (str.length() == 0) {
      str = "ascending";
    }
    _order = AttributeValue.create(this, str, paramParser);
    str = getAttribute("data-type");
    if (str.length() == 0) {
      try
      {
        com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type localType = _select.typeCheck(paramParser.getSymbolTable());
        if ((localType instanceof IntType)) {
          str = "number";
        } else {
          str = "text";
        }
      }
      catch (TypeCheckError localTypeCheckError)
      {
        str = "text";
      }
    }
    _dataType = AttributeValue.create(this, str, paramParser);
    _lang = getAttribute("lang");
    str = getAttribute("case-order");
    _caseOrder = AttributeValue.create(this, str, paramParser);
  }
  
  public com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type typeCheck(SymbolTable paramSymbolTable)
    throws TypeCheckError
  {
    com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type localType = _select.typeCheck(paramSymbolTable);
    if (!(localType instanceof StringType)) {
      _select = new CastExpr(_select, com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type.String);
    }
    _order.typeCheck(paramSymbolTable);
    _caseOrder.typeCheck(paramSymbolTable);
    _dataType.typeCheck(paramSymbolTable);
    return com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type.Void;
  }
  
  public void translateSortType(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator)
  {
    _dataType.translate(paramClassGenerator, paramMethodGenerator);
  }
  
  public void translateSortOrder(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator)
  {
    _order.translate(paramClassGenerator, paramMethodGenerator);
  }
  
  public void translateCaseOrder(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator)
  {
    _caseOrder.translate(paramClassGenerator, paramMethodGenerator);
  }
  
  public void translateLang(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator)
  {
    ConstantPoolGen localConstantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList localInstructionList = paramMethodGenerator.getInstructionList();
    localInstructionList.append(new PUSH(localConstantPoolGen, _lang));
  }
  
  public void translateSelect(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator)
  {
    _select.translate(paramClassGenerator, paramMethodGenerator);
  }
  
  public void translate(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator) {}
  
  public static void translateSortIterator(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator, Expression paramExpression, Vector<Sort> paramVector)
  {
    ConstantPoolGen localConstantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList localInstructionList = paramMethodGenerator.getInstructionList();
    int i = localConstantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.dom.SortingIterator", "<init>", "(Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;Lcom/sun/org/apache/xalan/internal/xsltc/dom/NodeSortRecordFactory;)V");
    LocalVariableGen localLocalVariableGen1 = paramMethodGenerator.addLocalVariable("sort_tmp1", Util.getJCRefType("Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;"), null, null);
    LocalVariableGen localLocalVariableGen2 = paramMethodGenerator.addLocalVariable("sort_tmp2", Util.getJCRefType("Lcom/sun/org/apache/xalan/internal/xsltc/dom/NodeSortRecordFactory;"), null, null);
    if (paramExpression == null)
    {
      int j = localConstantPoolGen.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "getAxisIterator", "(I)Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;");
      localInstructionList.append(paramMethodGenerator.loadDOM());
      localInstructionList.append(new PUSH(localConstantPoolGen, 3));
      localInstructionList.append(new INVOKEINTERFACE(j, 2));
    }
    else
    {
      paramExpression.translate(paramClassGenerator, paramMethodGenerator);
    }
    localLocalVariableGen1.setStart(localInstructionList.append(new ASTORE(localLocalVariableGen1.getIndex())));
    compileSortRecordFactory(paramVector, paramClassGenerator, paramMethodGenerator);
    localLocalVariableGen2.setStart(localInstructionList.append(new ASTORE(localLocalVariableGen2.getIndex())));
    localInstructionList.append(new NEW(localConstantPoolGen.addClass("com.sun.org.apache.xalan.internal.xsltc.dom.SortingIterator")));
    localInstructionList.append(DUP);
    localLocalVariableGen1.setEnd(localInstructionList.append(new ALOAD(localLocalVariableGen1.getIndex())));
    localLocalVariableGen2.setEnd(localInstructionList.append(new ALOAD(localLocalVariableGen2.getIndex())));
    localInstructionList.append(new INVOKESPECIAL(i));
  }
  
  public static void compileSortRecordFactory(Vector<Sort> paramVector, ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator)
  {
    String str1 = compileSortRecord(paramVector, paramClassGenerator, paramMethodGenerator);
    boolean bool = false;
    int i = paramVector.size();
    for (int j = 0; j < i; j++)
    {
      localObject = (Sort)paramVector.elementAt(j);
      bool |= _needsSortRecordFactory;
    }
    String str2 = "com/sun/org/apache/xalan/internal/xsltc/dom/NodeSortRecordFactory";
    if (bool) {
      str2 = compileSortRecordFactory(paramVector, paramClassGenerator, paramMethodGenerator, str1);
    }
    Object localObject = paramClassGenerator.getConstantPool();
    InstructionList localInstructionList = paramMethodGenerator.getInstructionList();
    LocalVariableGen localLocalVariableGen1 = paramMethodGenerator.addLocalVariable("sort_order_tmp", Util.getJCRefType("[Ljava/lang/String;"), null, null);
    localInstructionList.append(new PUSH((ConstantPoolGen)localObject, i));
    localInstructionList.append(new ANEWARRAY(((ConstantPoolGen)localObject).addClass("java.lang.String")));
    for (int k = 0; k < i; k++)
    {
      Sort localSort1 = (Sort)paramVector.elementAt(k);
      localInstructionList.append(DUP);
      localInstructionList.append(new PUSH((ConstantPoolGen)localObject, k));
      localSort1.translateSortOrder(paramClassGenerator, paramMethodGenerator);
      localInstructionList.append(AASTORE);
    }
    localLocalVariableGen1.setStart(localInstructionList.append(new ASTORE(localLocalVariableGen1.getIndex())));
    LocalVariableGen localLocalVariableGen2 = paramMethodGenerator.addLocalVariable("sort_type_tmp", Util.getJCRefType("[Ljava/lang/String;"), null, null);
    localInstructionList.append(new PUSH((ConstantPoolGen)localObject, i));
    localInstructionList.append(new ANEWARRAY(((ConstantPoolGen)localObject).addClass("java.lang.String")));
    for (int m = 0; m < i; m++)
    {
      Sort localSort2 = (Sort)paramVector.elementAt(m);
      localInstructionList.append(DUP);
      localInstructionList.append(new PUSH((ConstantPoolGen)localObject, m));
      localSort2.translateSortType(paramClassGenerator, paramMethodGenerator);
      localInstructionList.append(AASTORE);
    }
    localLocalVariableGen2.setStart(localInstructionList.append(new ASTORE(localLocalVariableGen2.getIndex())));
    LocalVariableGen localLocalVariableGen3 = paramMethodGenerator.addLocalVariable("sort_lang_tmp", Util.getJCRefType("[Ljava/lang/String;"), null, null);
    localInstructionList.append(new PUSH((ConstantPoolGen)localObject, i));
    localInstructionList.append(new ANEWARRAY(((ConstantPoolGen)localObject).addClass("java.lang.String")));
    for (int n = 0; n < i; n++)
    {
      Sort localSort3 = (Sort)paramVector.elementAt(n);
      localInstructionList.append(DUP);
      localInstructionList.append(new PUSH((ConstantPoolGen)localObject, n));
      localSort3.translateLang(paramClassGenerator, paramMethodGenerator);
      localInstructionList.append(AASTORE);
    }
    localLocalVariableGen3.setStart(localInstructionList.append(new ASTORE(localLocalVariableGen3.getIndex())));
    LocalVariableGen localLocalVariableGen4 = paramMethodGenerator.addLocalVariable("sort_case_order_tmp", Util.getJCRefType("[Ljava/lang/String;"), null, null);
    localInstructionList.append(new PUSH((ConstantPoolGen)localObject, i));
    localInstructionList.append(new ANEWARRAY(((ConstantPoolGen)localObject).addClass("java.lang.String")));
    for (int i1 = 0; i1 < i; i1++)
    {
      Sort localSort4 = (Sort)paramVector.elementAt(i1);
      localInstructionList.append(DUP);
      localInstructionList.append(new PUSH((ConstantPoolGen)localObject, i1));
      localSort4.translateCaseOrder(paramClassGenerator, paramMethodGenerator);
      localInstructionList.append(AASTORE);
    }
    localLocalVariableGen4.setStart(localInstructionList.append(new ASTORE(localLocalVariableGen4.getIndex())));
    localInstructionList.append(new NEW(((ConstantPoolGen)localObject).addClass(str2)));
    localInstructionList.append(DUP);
    localInstructionList.append(paramMethodGenerator.loadDOM());
    localInstructionList.append(new PUSH((ConstantPoolGen)localObject, str1));
    localInstructionList.append(paramClassGenerator.loadTranslet());
    localLocalVariableGen1.setEnd(localInstructionList.append(new ALOAD(localLocalVariableGen1.getIndex())));
    localLocalVariableGen2.setEnd(localInstructionList.append(new ALOAD(localLocalVariableGen2.getIndex())));
    localLocalVariableGen3.setEnd(localInstructionList.append(new ALOAD(localLocalVariableGen3.getIndex())));
    localLocalVariableGen4.setEnd(localInstructionList.append(new ALOAD(localLocalVariableGen4.getIndex())));
    localInstructionList.append(new INVOKESPECIAL(((ConstantPoolGen)localObject).addMethodref(str2, "<init>", "(Lcom/sun/org/apache/xalan/internal/xsltc/DOM;Ljava/lang/String;Lcom/sun/org/apache/xalan/internal/xsltc/Translet;[Ljava/lang/String;[Ljava/lang/String;[Ljava/lang/String;[Ljava/lang/String;)V")));
    ArrayList localArrayList = new ArrayList();
    for (int i2 = 0; i2 < i; i2++)
    {
      Sort localSort5 = (Sort)paramVector.get(i2);
      int i3 = _closureVars == null ? 0 : _closureVars.size();
      for (int i4 = 0; i4 < i3; i4++)
      {
        VariableRefBase localVariableRefBase = (VariableRefBase)_closureVars.get(i4);
        if (!localArrayList.contains(localVariableRefBase))
        {
          VariableBase localVariableBase = localVariableRefBase.getVariable();
          localInstructionList.append(DUP);
          localInstructionList.append(localVariableBase.loadInstruction());
          localInstructionList.append(new PUTFIELD(((ConstantPoolGen)localObject).addFieldref(str2, localVariableBase.getEscapedName(), localVariableBase.getType().toSignature())));
          localArrayList.add(localVariableRefBase);
        }
      }
    }
  }
  
  public static String compileSortRecordFactory(Vector<Sort> paramVector, ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator, String paramString)
  {
    XSLTC localXSLTC = ((Sort)paramVector.firstElement()).getXSLTC();
    String str = localXSLTC.getHelperClassName();
    NodeSortRecordFactGenerator localNodeSortRecordFactGenerator = new NodeSortRecordFactGenerator(str, "com/sun/org/apache/xalan/internal/xsltc/dom/NodeSortRecordFactory", str + ".java", 49, new String[0], paramClassGenerator.getStylesheet());
    ConstantPoolGen localConstantPoolGen = localNodeSortRecordFactGenerator.getConstantPool();
    int i = paramVector.size();
    ArrayList localArrayList = new ArrayList();
    for (int j = 0; j < i; j++)
    {
      localObject1 = (Sort)paramVector.get(j);
      int k = _closureVars == null ? 0 : _closureVars.size();
      for (int m = 0; m < k; m++)
      {
        localObject2 = (VariableRefBase)_closureVars.get(m);
        if (!localArrayList.contains(localObject2))
        {
          VariableBase localVariableBase1 = ((VariableRefBase)localObject2).getVariable();
          localNodeSortRecordFactGenerator.addField(new Field(1, localConstantPoolGen.addUtf8(localVariableBase1.getEscapedName()), localConstantPoolGen.addUtf8(localVariableBase1.getType().toSignature()), null, localConstantPoolGen.getConstantPool()));
          localArrayList.add(localObject2);
        }
      }
    }
    com.sun.org.apache.bcel.internal.generic.Type[] arrayOfType = new com.sun.org.apache.bcel.internal.generic.Type[7];
    arrayOfType[0] = Util.getJCRefType("Lcom/sun/org/apache/xalan/internal/xsltc/DOM;");
    arrayOfType[1] = Util.getJCRefType("Ljava/lang/String;");
    arrayOfType[2] = Util.getJCRefType("Lcom/sun/org/apache/xalan/internal/xsltc/Translet;");
    arrayOfType[3] = Util.getJCRefType("[Ljava/lang/String;");
    arrayOfType[4] = Util.getJCRefType("[Ljava/lang/String;");
    arrayOfType[5] = Util.getJCRefType("[Ljava/lang/String;");
    arrayOfType[6] = Util.getJCRefType("[Ljava/lang/String;");
    Object localObject1 = new String[7];
    localObject1[0] = "document";
    localObject1[1] = "className";
    localObject1[2] = "translet";
    localObject1[3] = "order";
    localObject1[4] = "type";
    localObject1[5] = "lang";
    localObject1[6] = "case_order";
    InstructionList localInstructionList = new InstructionList();
    MethodGenerator localMethodGenerator = new MethodGenerator(1, com.sun.org.apache.bcel.internal.generic.Type.VOID, arrayOfType, (String[])localObject1, "<init>", str, localInstructionList, localConstantPoolGen);
    localInstructionList.append(ALOAD_0);
    localInstructionList.append(ALOAD_1);
    localInstructionList.append(ALOAD_2);
    localInstructionList.append(new ALOAD(3));
    localInstructionList.append(new ALOAD(4));
    localInstructionList.append(new ALOAD(5));
    localInstructionList.append(new ALOAD(6));
    localInstructionList.append(new ALOAD(7));
    localInstructionList.append(new INVOKESPECIAL(localConstantPoolGen.addMethodref("com/sun/org/apache/xalan/internal/xsltc/dom/NodeSortRecordFactory", "<init>", "(Lcom/sun/org/apache/xalan/internal/xsltc/DOM;Ljava/lang/String;Lcom/sun/org/apache/xalan/internal/xsltc/Translet;[Ljava/lang/String;[Ljava/lang/String;[Ljava/lang/String;[Ljava/lang/String;)V")));
    localInstructionList.append(RETURN);
    localInstructionList = new InstructionList();
    Object localObject2 = new MethodGenerator(1, Util.getJCRefType("Lcom/sun/org/apache/xalan/internal/xsltc/dom/NodeSortRecord;"), new com.sun.org.apache.bcel.internal.generic.Type[] { com.sun.org.apache.bcel.internal.generic.Type.INT, com.sun.org.apache.bcel.internal.generic.Type.INT }, new String[] { "node", "last" }, "makeNodeSortRecord", str, localInstructionList, localConstantPoolGen);
    localInstructionList.append(ALOAD_0);
    localInstructionList.append(ILOAD_1);
    localInstructionList.append(ILOAD_2);
    localInstructionList.append(new INVOKESPECIAL(localConstantPoolGen.addMethodref("com/sun/org/apache/xalan/internal/xsltc/dom/NodeSortRecordFactory", "makeNodeSortRecord", "(II)Lcom/sun/org/apache/xalan/internal/xsltc/dom/NodeSortRecord;")));
    localInstructionList.append(DUP);
    localInstructionList.append(new CHECKCAST(localConstantPoolGen.addClass(paramString)));
    int n = localArrayList.size();
    for (int i1 = 0; i1 < n; i1++)
    {
      VariableRefBase localVariableRefBase = (VariableRefBase)localArrayList.get(i1);
      VariableBase localVariableBase2 = localVariableRefBase.getVariable();
      com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type localType = localVariableBase2.getType();
      localInstructionList.append(DUP);
      localInstructionList.append(ALOAD_0);
      localInstructionList.append(new GETFIELD(localConstantPoolGen.addFieldref(str, localVariableBase2.getEscapedName(), localType.toSignature())));
      localInstructionList.append(new PUTFIELD(localConstantPoolGen.addFieldref(paramString, localVariableBase2.getEscapedName(), localType.toSignature())));
    }
    localInstructionList.append(POP);
    localInstructionList.append(ARETURN);
    localMethodGenerator.setMaxLocals();
    localMethodGenerator.setMaxStack();
    localNodeSortRecordFactGenerator.addMethod(localMethodGenerator);
    ((MethodGenerator)localObject2).setMaxLocals();
    ((MethodGenerator)localObject2).setMaxStack();
    localNodeSortRecordFactGenerator.addMethod((MethodGenerator)localObject2);
    localXSLTC.dumpClass(localNodeSortRecordFactGenerator.getJavaClass());
    return str;
  }
  
  private static String compileSortRecord(Vector<Sort> paramVector, ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator)
  {
    XSLTC localXSLTC = ((Sort)paramVector.firstElement()).getXSLTC();
    String str = localXSLTC.getHelperClassName();
    NodeSortRecordGenerator localNodeSortRecordGenerator = new NodeSortRecordGenerator(str, "com.sun.org.apache.xalan.internal.xsltc.dom.NodeSortRecord", "sort$0.java", 49, new String[0], paramClassGenerator.getStylesheet());
    ConstantPoolGen localConstantPoolGen = localNodeSortRecordGenerator.getConstantPool();
    int i = paramVector.size();
    ArrayList localArrayList = new ArrayList();
    for (int j = 0; j < i; j++)
    {
      localObject = (Sort)paramVector.get(j);
      ((Sort)localObject).setInnerClassName(str);
      int k = _closureVars == null ? 0 : _closureVars.size();
      for (int m = 0; m < k; m++)
      {
        VariableRefBase localVariableRefBase = (VariableRefBase)_closureVars.get(m);
        if (!localArrayList.contains(localVariableRefBase))
        {
          VariableBase localVariableBase = localVariableRefBase.getVariable();
          localNodeSortRecordGenerator.addField(new Field(1, localConstantPoolGen.addUtf8(localVariableBase.getEscapedName()), localConstantPoolGen.addUtf8(localVariableBase.getType().toSignature()), null, localConstantPoolGen.getConstantPool()));
          localArrayList.add(localVariableRefBase);
        }
      }
    }
    MethodGenerator localMethodGenerator = compileInit(localNodeSortRecordGenerator, localConstantPoolGen, str);
    Object localObject = compileExtract(paramVector, localNodeSortRecordGenerator, localConstantPoolGen, str);
    localNodeSortRecordGenerator.addMethod(localMethodGenerator);
    localNodeSortRecordGenerator.addMethod((MethodGenerator)localObject);
    localXSLTC.dumpClass(localNodeSortRecordGenerator.getJavaClass());
    return str;
  }
  
  private static MethodGenerator compileInit(NodeSortRecordGenerator paramNodeSortRecordGenerator, ConstantPoolGen paramConstantPoolGen, String paramString)
  {
    InstructionList localInstructionList = new InstructionList();
    MethodGenerator localMethodGenerator = new MethodGenerator(1, com.sun.org.apache.bcel.internal.generic.Type.VOID, null, null, "<init>", paramString, localInstructionList, paramConstantPoolGen);
    localInstructionList.append(ALOAD_0);
    localInstructionList.append(new INVOKESPECIAL(paramConstantPoolGen.addMethodref("com.sun.org.apache.xalan.internal.xsltc.dom.NodeSortRecord", "<init>", "()V")));
    localInstructionList.append(RETURN);
    return localMethodGenerator;
  }
  
  private static MethodGenerator compileExtract(Vector<Sort> paramVector, NodeSortRecordGenerator paramNodeSortRecordGenerator, ConstantPoolGen paramConstantPoolGen, String paramString)
  {
    InstructionList localInstructionList = new InstructionList();
    CompareGenerator localCompareGenerator = new CompareGenerator(17, com.sun.org.apache.bcel.internal.generic.Type.STRING, new com.sun.org.apache.bcel.internal.generic.Type[] { Util.getJCRefType("Lcom/sun/org/apache/xalan/internal/xsltc/DOM;"), com.sun.org.apache.bcel.internal.generic.Type.INT, com.sun.org.apache.bcel.internal.generic.Type.INT, Util.getJCRefType("Lcom/sun/org/apache/xalan/internal/xsltc/runtime/AbstractTranslet;"), com.sun.org.apache.bcel.internal.generic.Type.INT }, new String[] { "dom", "current", "level", "translet", "last" }, "extractValueFromDOM", paramString, localInstructionList, paramConstantPoolGen);
    int i = paramVector.size();
    int[] arrayOfInt = new int[i];
    InstructionHandle[] arrayOfInstructionHandle = new InstructionHandle[i];
    InstructionHandle localInstructionHandle1 = null;
    if (i > 1)
    {
      localInstructionList.append(new ILOAD(localCompareGenerator.getLocalIndex("level")));
      localInstructionHandle1 = localInstructionList.append(new NOP());
    }
    for (int j = 0; j < i; j++)
    {
      arrayOfInt[j] = j;
      Sort localSort = (Sort)paramVector.elementAt(j);
      arrayOfInstructionHandle[j] = localInstructionList.append(NOP);
      localSort.translateSelect(paramNodeSortRecordGenerator, localCompareGenerator);
      localInstructionList.append(ARETURN);
    }
    if (i > 1)
    {
      InstructionHandle localInstructionHandle2 = localInstructionList.append(new PUSH(paramConstantPoolGen, ""));
      localInstructionList.insert(localInstructionHandle1, new TABLESWITCH(arrayOfInt, arrayOfInstructionHandle, localInstructionHandle2));
      localInstructionList.append(ARETURN);
    }
    return localCompareGenerator;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\Sort.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */