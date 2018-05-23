package com.sun.org.apache.xalan.internal.xsltc.compiler.util;

import com.sun.org.apache.bcel.internal.classfile.Field;
import com.sun.org.apache.bcel.internal.classfile.Method;
import com.sun.org.apache.bcel.internal.generic.ALOAD;
import com.sun.org.apache.bcel.internal.generic.ASTORE;
import com.sun.org.apache.bcel.internal.generic.BranchHandle;
import com.sun.org.apache.bcel.internal.generic.BranchInstruction;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.DLOAD;
import com.sun.org.apache.bcel.internal.generic.DSTORE;
import com.sun.org.apache.bcel.internal.generic.FLOAD;
import com.sun.org.apache.bcel.internal.generic.FSTORE;
import com.sun.org.apache.bcel.internal.generic.GETFIELD;
import com.sun.org.apache.bcel.internal.generic.GOTO;
import com.sun.org.apache.bcel.internal.generic.ICONST;
import com.sun.org.apache.bcel.internal.generic.ILOAD;
import com.sun.org.apache.bcel.internal.generic.INVOKEINTERFACE;
import com.sun.org.apache.bcel.internal.generic.INVOKESPECIAL;
import com.sun.org.apache.bcel.internal.generic.INVOKESTATIC;
import com.sun.org.apache.bcel.internal.generic.INVOKEVIRTUAL;
import com.sun.org.apache.bcel.internal.generic.ISTORE;
import com.sun.org.apache.bcel.internal.generic.IfInstruction;
import com.sun.org.apache.bcel.internal.generic.IndexedInstruction;
import com.sun.org.apache.bcel.internal.generic.Instruction;
import com.sun.org.apache.bcel.internal.generic.InstructionConstants;
import com.sun.org.apache.bcel.internal.generic.InstructionHandle;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.InstructionTargeter;
import com.sun.org.apache.bcel.internal.generic.LLOAD;
import com.sun.org.apache.bcel.internal.generic.LSTORE;
import com.sun.org.apache.bcel.internal.generic.LocalVariableGen;
import com.sun.org.apache.bcel.internal.generic.LocalVariableInstruction;
import com.sun.org.apache.bcel.internal.generic.MethodGen;
import com.sun.org.apache.bcel.internal.generic.NEW;
import com.sun.org.apache.bcel.internal.generic.PUTFIELD;
import com.sun.org.apache.bcel.internal.generic.RET;
import com.sun.org.apache.bcel.internal.generic.Select;
import com.sun.org.apache.bcel.internal.generic.TargetLostException;
import com.sun.org.apache.bcel.internal.generic.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.Constants;
import com.sun.org.apache.xalan.internal.xsltc.compiler.Parser;
import com.sun.org.apache.xalan.internal.xsltc.compiler.Pattern;
import com.sun.org.apache.xalan.internal.xsltc.compiler.Stylesheet;
import com.sun.org.apache.xalan.internal.xsltc.compiler.XSLTC;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Stack;

public class MethodGenerator
  extends MethodGen
  implements Constants
{
  protected static final int INVALID_INDEX = -1;
  private static final String START_ELEMENT_SIG = "(Ljava/lang/String;)V";
  private static final String END_ELEMENT_SIG = "(Ljava/lang/String;)V";
  private InstructionList _mapTypeSub;
  private static final int DOM_INDEX = 1;
  private static final int ITERATOR_INDEX = 2;
  private static final int HANDLER_INDEX = 3;
  private static final int MAX_METHOD_SIZE = 65535;
  private static final int MAX_BRANCH_TARGET_OFFSET = 32767;
  private static final int MIN_BRANCH_TARGET_OFFSET = -32768;
  private static final int TARGET_METHOD_SIZE = 60000;
  private static final int MINIMUM_OUTLINEABLE_CHUNK_SIZE = 1000;
  private Instruction _iloadCurrent;
  private Instruction _istoreCurrent;
  private final Instruction _astoreHandler = new ASTORE(3);
  private final Instruction _aloadHandler = new ALOAD(3);
  private final Instruction _astoreIterator = new ASTORE(2);
  private final Instruction _aloadIterator = new ALOAD(2);
  private final Instruction _aloadDom = new ALOAD(1);
  private final Instruction _astoreDom = new ASTORE(1);
  private final Instruction _startElement;
  private final Instruction _endElement;
  private final Instruction _startDocument;
  private final Instruction _endDocument;
  private final Instruction _attribute;
  private final Instruction _uniqueAttribute;
  private final Instruction _namespace;
  private final Instruction _setStartNode;
  private final Instruction _reset;
  private final Instruction _nextNode;
  private SlotAllocator _slotAllocator;
  private boolean _allocatorInit = false;
  private LocalVariableRegistry _localVariableRegistry;
  private Map<Pattern, InstructionList> _preCompiled = new HashMap();
  private int m_totalChunks = 0;
  private int m_openChunks = 0;
  
  public MethodGenerator(int paramInt, Type paramType, Type[] paramArrayOfType, String[] paramArrayOfString, String paramString1, String paramString2, InstructionList paramInstructionList, ConstantPoolGen paramConstantPoolGen)
  {
    super(paramInt, paramType, paramArrayOfType, paramArrayOfString, paramString1, paramString2, paramInstructionList, paramConstantPoolGen);
    int i = paramConstantPoolGen.addInterfaceMethodref("com.sun.org.apache.xml.internal.serializer.SerializationHandler", "startElement", "(Ljava/lang/String;)V");
    _startElement = new INVOKEINTERFACE(i, 2);
    int j = paramConstantPoolGen.addInterfaceMethodref("com.sun.org.apache.xml.internal.serializer.SerializationHandler", "endElement", "(Ljava/lang/String;)V");
    _endElement = new INVOKEINTERFACE(j, 2);
    int k = paramConstantPoolGen.addInterfaceMethodref("com.sun.org.apache.xml.internal.serializer.SerializationHandler", "addAttribute", "(Ljava/lang/String;Ljava/lang/String;)V");
    _attribute = new INVOKEINTERFACE(k, 3);
    int m = paramConstantPoolGen.addInterfaceMethodref("com.sun.org.apache.xml.internal.serializer.SerializationHandler", "addUniqueAttribute", "(Ljava/lang/String;Ljava/lang/String;I)V");
    _uniqueAttribute = new INVOKEINTERFACE(m, 4);
    int n = paramConstantPoolGen.addInterfaceMethodref("com.sun.org.apache.xml.internal.serializer.SerializationHandler", "namespaceAfterStartElement", "(Ljava/lang/String;Ljava/lang/String;)V");
    _namespace = new INVOKEINTERFACE(n, 3);
    int i1 = paramConstantPoolGen.addInterfaceMethodref("com.sun.org.apache.xml.internal.serializer.SerializationHandler", "startDocument", "()V");
    _startDocument = new INVOKEINTERFACE(i1, 1);
    i1 = paramConstantPoolGen.addInterfaceMethodref("com.sun.org.apache.xml.internal.serializer.SerializationHandler", "endDocument", "()V");
    _endDocument = new INVOKEINTERFACE(i1, 1);
    i1 = paramConstantPoolGen.addInterfaceMethodref("com.sun.org.apache.xml.internal.dtm.DTMAxisIterator", "setStartNode", "(I)Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;");
    _setStartNode = new INVOKEINTERFACE(i1, 2);
    i1 = paramConstantPoolGen.addInterfaceMethodref("com.sun.org.apache.xml.internal.dtm.DTMAxisIterator", "reset", "()Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;");
    _reset = new INVOKEINTERFACE(i1, 1);
    i1 = paramConstantPoolGen.addInterfaceMethodref("com.sun.org.apache.xml.internal.dtm.DTMAxisIterator", "next", "()I");
    _nextNode = new INVOKEINTERFACE(i1, 1);
    _slotAllocator = new SlotAllocator();
    _slotAllocator.initialize(getLocalVariableRegistry().getLocals(false));
    _allocatorInit = true;
  }
  
  public LocalVariableGen addLocalVariable(String paramString, Type paramType, InstructionHandle paramInstructionHandle1, InstructionHandle paramInstructionHandle2)
  {
    LocalVariableGen localLocalVariableGen;
    if (_allocatorInit)
    {
      localLocalVariableGen = addLocalVariable2(paramString, paramType, paramInstructionHandle1);
    }
    else
    {
      localLocalVariableGen = super.addLocalVariable(paramString, paramType, paramInstructionHandle1, paramInstructionHandle2);
      getLocalVariableRegistry().registerLocalVariable(localLocalVariableGen);
    }
    return localLocalVariableGen;
  }
  
  public LocalVariableGen addLocalVariable2(String paramString, Type paramType, InstructionHandle paramInstructionHandle)
  {
    LocalVariableGen localLocalVariableGen = super.addLocalVariable(paramString, paramType, _slotAllocator.allocateSlot(paramType), paramInstructionHandle, null);
    getLocalVariableRegistry().registerLocalVariable(localLocalVariableGen);
    return localLocalVariableGen;
  }
  
  private LocalVariableRegistry getLocalVariableRegistry()
  {
    if (_localVariableRegistry == null) {
      _localVariableRegistry = new LocalVariableRegistry();
    }
    return _localVariableRegistry;
  }
  
  boolean offsetInLocalVariableGenRange(LocalVariableGen paramLocalVariableGen, int paramInt)
  {
    InstructionHandle localInstructionHandle1 = paramLocalVariableGen.getStart();
    InstructionHandle localInstructionHandle2 = paramLocalVariableGen.getEnd();
    if (localInstructionHandle1 == null) {
      localInstructionHandle1 = getInstructionList().getStart();
    }
    if (localInstructionHandle2 == null) {
      localInstructionHandle2 = getInstructionList().getEnd();
    }
    return (localInstructionHandle1.getPosition() <= paramInt) && (localInstructionHandle2.getPosition() + localInstructionHandle2.getInstruction().getLength() >= paramInt);
  }
  
  public void removeLocalVariable(LocalVariableGen paramLocalVariableGen)
  {
    _slotAllocator.releaseSlot(paramLocalVariableGen);
    getLocalVariableRegistry().removeByNameTracking(paramLocalVariableGen);
    super.removeLocalVariable(paramLocalVariableGen);
  }
  
  public Instruction loadDOM()
  {
    return _aloadDom;
  }
  
  public Instruction storeDOM()
  {
    return _astoreDom;
  }
  
  public Instruction storeHandler()
  {
    return _astoreHandler;
  }
  
  public Instruction loadHandler()
  {
    return _aloadHandler;
  }
  
  public Instruction storeIterator()
  {
    return _astoreIterator;
  }
  
  public Instruction loadIterator()
  {
    return _aloadIterator;
  }
  
  public final Instruction setStartNode()
  {
    return _setStartNode;
  }
  
  public final Instruction reset()
  {
    return _reset;
  }
  
  public final Instruction nextNode()
  {
    return _nextNode;
  }
  
  public final Instruction startElement()
  {
    return _startElement;
  }
  
  public final Instruction endElement()
  {
    return _endElement;
  }
  
  public final Instruction startDocument()
  {
    return _startDocument;
  }
  
  public final Instruction endDocument()
  {
    return _endDocument;
  }
  
  public final Instruction attribute()
  {
    return _attribute;
  }
  
  public final Instruction uniqueAttribute()
  {
    return _uniqueAttribute;
  }
  
  public final Instruction namespace()
  {
    return _namespace;
  }
  
  public Instruction loadCurrentNode()
  {
    if (_iloadCurrent == null)
    {
      int i = getLocalIndex("current");
      if (i > 0) {
        _iloadCurrent = new ILOAD(i);
      } else {
        _iloadCurrent = new ICONST(0);
      }
    }
    return _iloadCurrent;
  }
  
  public Instruction storeCurrentNode()
  {
    return _istoreCurrent != null ? _istoreCurrent : (_istoreCurrent = new ISTORE(getLocalIndex("current")));
  }
  
  public Instruction loadContextNode()
  {
    return loadCurrentNode();
  }
  
  public Instruction storeContextNode()
  {
    return storeCurrentNode();
  }
  
  public int getLocalIndex(String paramString)
  {
    return getLocalVariable(paramString).getIndex();
  }
  
  public LocalVariableGen getLocalVariable(String paramString)
  {
    return getLocalVariableRegistry().lookUpByName(paramString);
  }
  
  public void setMaxLocals()
  {
    int i = super.getMaxLocals();
    int j = i;
    LocalVariableGen[] arrayOfLocalVariableGen = super.getLocalVariables();
    if ((arrayOfLocalVariableGen != null) && (arrayOfLocalVariableGen.length > i)) {
      i = arrayOfLocalVariableGen.length;
    }
    if (i < 5) {
      i = 5;
    }
    super.setMaxLocals(i);
  }
  
  public void addInstructionList(Pattern paramPattern, InstructionList paramInstructionList)
  {
    _preCompiled.put(paramPattern, paramInstructionList);
  }
  
  public InstructionList getInstructionList(Pattern paramPattern)
  {
    return (InstructionList)_preCompiled.get(paramPattern);
  }
  
  private ArrayList getCandidateChunks(ClassGenerator paramClassGenerator, int paramInt)
  {
    Iterator localIterator = getInstructionList().iterator();
    ArrayList localArrayList1 = new ArrayList();
    ArrayList localArrayList2 = new ArrayList();
    Stack localStack = new Stack();
    int i = 0;
    int j = 1;
    String str;
    if (m_openChunks != 0)
    {
      str = new ErrorMsg("OUTLINE_ERR_UNBALANCED_MARKERS").toString();
      throw new InternalError(str);
    }
    Object localObject1;
    do
    {
      localObject1 = localIterator.hasNext() ? (InstructionHandle)localIterator.next() : null;
      str = localObject1 != null ? ((InstructionHandle)localObject1).getInstruction() : null;
      if (j != 0)
      {
        i = 1;
        localArrayList2.add(localObject1);
        j = 0;
      }
      if ((str instanceof OutlineableChunkStart))
      {
        if (i != 0)
        {
          localStack.push(localArrayList2);
          localArrayList2 = new ArrayList();
        }
        i = 1;
        localArrayList2.add(localObject1);
      }
      else if ((localObject1 == null) || ((str instanceof OutlineableChunkEnd)))
      {
        ArrayList localArrayList3 = null;
        if (i == 0)
        {
          localArrayList3 = localArrayList2;
          localArrayList2 = (ArrayList)localStack.pop();
        }
        InstructionHandle localInstructionHandle1 = (InstructionHandle)localArrayList2.get(localArrayList2.size() - 1);
        int k = localObject1 != null ? ((InstructionHandle)localObject1).getPosition() : paramInt;
        int m = k - localInstructionHandle1.getPosition();
        if (m <= 60000)
        {
          localArrayList2.add(localObject1);
        }
        else
        {
          if (i == 0)
          {
            int n = localArrayList3.size() / 2;
            if (n > 0)
            {
              Chunk[] arrayOfChunk = new Chunk[n];
              Object localObject2;
              for (int i1 = 0; i1 < n; i1++)
              {
                InstructionHandle localInstructionHandle2 = (InstructionHandle)localArrayList3.get(i1 * 2);
                localObject2 = (InstructionHandle)localArrayList3.get(i1 * 2 + 1);
                arrayOfChunk[i1] = new Chunk(localInstructionHandle2, (InstructionHandle)localObject2);
              }
              ArrayList localArrayList4 = mergeAdjacentChunks(arrayOfChunk);
              for (int i2 = 0; i2 < localArrayList4.size(); i2++)
              {
                localObject2 = (Chunk)localArrayList4.get(i2);
                int i3 = ((Chunk)localObject2).getChunkSize();
                if ((i3 >= 1000) && (i3 <= 60000)) {
                  localArrayList1.add(localObject2);
                }
              }
            }
          }
          localArrayList2.remove(localArrayList2.size() - 1);
        }
        i = (localArrayList2.size() & 0x1) == 1 ? 1 : 0;
      }
    } while (localObject1 != null);
    return localArrayList1;
  }
  
  private ArrayList mergeAdjacentChunks(Chunk[] paramArrayOfChunk)
  {
    int[] arrayOfInt1 = new int[paramArrayOfChunk.length];
    int[] arrayOfInt2 = new int[paramArrayOfChunk.length];
    boolean[] arrayOfBoolean = new boolean[paramArrayOfChunk.length];
    int i = 0;
    int k = 0;
    ArrayList localArrayList = new ArrayList();
    int j = 0;
    int n;
    for (int m = 1; m < paramArrayOfChunk.length; m++) {
      if (!paramArrayOfChunk[(m - 1)].isAdjacentTo(paramArrayOfChunk[m]))
      {
        n = m - j;
        if (i < n) {
          i = n;
        }
        if (n > 1)
        {
          arrayOfInt2[k] = n;
          arrayOfInt1[k] = j;
          k++;
        }
        j = m;
      }
    }
    if (paramArrayOfChunk.length - j > 1)
    {
      m = paramArrayOfChunk.length - j;
      if (i < m) {
        i = m;
      }
      arrayOfInt2[k] = (paramArrayOfChunk.length - j);
      arrayOfInt1[k] = j;
      k++;
    }
    for (m = i; m > 1; m--) {
      for (n = 0; n < k; n++)
      {
        int i1 = arrayOfInt1[n];
        int i2 = i1 + arrayOfInt2[n] - 1;
        int i3 = 0;
        for (int i4 = i1; (i4 + m - 1 <= i2) && (i3 == 0); i4++)
        {
          int i5 = i4 + m - 1;
          int i6 = 0;
          for (int i7 = i4; i7 <= i5; i7++) {
            i6 += paramArrayOfChunk[i7].getChunkSize();
          }
          if (i6 <= 60000)
          {
            i3 = 1;
            for (i7 = i4; i7 <= i5; i7++) {
              arrayOfBoolean[i7] = true;
            }
            localArrayList.add(new Chunk(paramArrayOfChunk[i4].getChunkStart(), paramArrayOfChunk[i5].getChunkEnd()));
            arrayOfInt1[n] -= i4;
            i7 = i2 - i5;
            if (i7 >= 2)
            {
              arrayOfInt1[k] = (i5 + 1);
              arrayOfInt2[k] = i7;
              k++;
            }
          }
        }
      }
    }
    for (m = 0; m < paramArrayOfChunk.length; m++) {
      if (arrayOfBoolean[m] == 0) {
        localArrayList.add(paramArrayOfChunk[m]);
      }
    }
    return localArrayList;
  }
  
  public Method[] outlineChunks(ClassGenerator paramClassGenerator, int paramInt)
  {
    ArrayList localArrayList = new ArrayList();
    int i = paramInt;
    int j = 0;
    String str = getName();
    if (str.equals("<init>")) {
      str = "$lt$init$gt$";
    } else if (str.equals("<clinit>")) {
      str = "$lt$clinit$gt$";
    }
    int k;
    do
    {
      localObject = getCandidateChunks(paramClassGenerator, i);
      Collections.sort((List)localObject);
      k = 0;
      for (int m = ((ArrayList)localObject).size() - 1; (m >= 0) && (i > 60000); m--)
      {
        Chunk localChunk = (Chunk)((ArrayList)localObject).get(m);
        localArrayList.add(outline(localChunk.getChunkStart(), localChunk.getChunkEnd(), str + "$outline$" + j, paramClassGenerator));
        j++;
        k = 1;
        InstructionList localInstructionList = getInstructionList();
        InstructionHandle localInstructionHandle = localInstructionList.getEnd();
        localInstructionList.setPositions();
        i = localInstructionHandle.getPosition() + localInstructionHandle.getInstruction().getLength();
      }
    } while ((k != 0) && (i > 60000));
    if (i > 65535)
    {
      localObject = new ErrorMsg("OUTLINE_ERR_METHOD_TOO_BIG").toString();
      throw new InternalError((String)localObject);
    }
    Object localObject = new Method[localArrayList.size() + 1];
    localArrayList.toArray((Object[])localObject);
    localObject[localArrayList.size()] = getThisMethod();
    return (Method[])localObject;
  }
  
  private Method outline(InstructionHandle paramInstructionHandle1, InstructionHandle paramInstructionHandle2, String paramString, ClassGenerator paramClassGenerator)
  {
    if (getExceptionHandlers().length != 0)
    {
      String str1 = new ErrorMsg("OUTLINE_ERR_TRY_CATCH").toString();
      throw new InternalError(str1);
    }
    int i = paramInstructionHandle1.getPosition();
    int j = paramInstructionHandle2.getPosition() + paramInstructionHandle2.getInstruction().getLength();
    ConstantPoolGen localConstantPoolGen1 = getConstantPool();
    InstructionList localInstructionList1 = new InstructionList();
    XSLTC localXSLTC = paramClassGenerator.getParser().getXSLTC();
    String str2 = localXSLTC.getHelperClassName();
    Type[] arrayOfType = { new ObjectType(str2).toJCType() };
    String str3 = "copyLocals";
    String[] arrayOfString1 = { "copyLocals" };
    int k = 18;
    int m = (getAccessFlags() & 0x8) != 0 ? 1 : 0;
    if (m != 0) {
      k |= 0x8;
    }
    MethodGenerator localMethodGenerator = new MethodGenerator(k, Type.VOID, arrayOfType, arrayOfString1, paramString, getClassName(), localInstructionList1, localConstantPoolGen1);
    ClassGenerator local1 = new ClassGenerator(str2, "java.lang.Object", str2 + ".java", 49, null, paramClassGenerator.getStylesheet())
    {
      public boolean isExternal()
      {
        return true;
      }
    };
    ConstantPoolGen localConstantPoolGen2 = local1.getConstantPool();
    local1.addEmptyConstructor(1);
    int n = 0;
    InstructionHandle localInstructionHandle1 = paramInstructionHandle2.getNext();
    InstructionList localInstructionList2 = new InstructionList();
    InstructionList localInstructionList3 = new InstructionList();
    InstructionList localInstructionList4 = new InstructionList();
    InstructionList localInstructionList5 = new InstructionList();
    InstructionHandle localInstructionHandle2 = localInstructionList2.append(new NEW(localConstantPoolGen1.addClass(str2)));
    localInstructionList2.append(InstructionConstants.DUP);
    localInstructionList2.append(InstructionConstants.DUP);
    localInstructionList2.append(new INVOKESPECIAL(localConstantPoolGen1.addMethodref(str2, "<init>", "()V")));
    InstructionHandle localInstructionHandle3;
    if (m != 0)
    {
      localInstructionHandle3 = localInstructionList3.append(new INVOKESTATIC(localConstantPoolGen1.addMethodref(paramClassGenerator.getClassName(), paramString, localMethodGenerator.getSignature())));
    }
    else
    {
      localInstructionList3.append(InstructionConstants.THIS);
      localInstructionList3.append(InstructionConstants.SWAP);
      localInstructionHandle3 = localInstructionList3.append(new INVOKEVIRTUAL(localConstantPoolGen1.addMethodref(paramClassGenerator.getClassName(), paramString, localMethodGenerator.getSignature())));
    }
    int i1 = 0;
    Object localObject1 = null;
    Object localObject2 = null;
    HashMap localHashMap1 = new HashMap();
    HashMap localHashMap2 = new HashMap();
    HashMap localHashMap3 = new HashMap();
    HashMap localHashMap4 = new HashMap();
    Object localObject7;
    Object localObject9;
    Object localObject11;
    for (InstructionHandle localInstructionHandle4 = paramInstructionHandle1; localInstructionHandle4 != localInstructionHandle1; localInstructionHandle4 = localInstructionHandle4.getNext())
    {
      localObject3 = localInstructionHandle4.getInstruction();
      if ((localObject3 instanceof MarkerInstruction))
      {
        if (localInstructionHandle4.hasTargeters()) {
          if ((localObject3 instanceof OutlineableChunkEnd))
          {
            localHashMap1.put(localInstructionHandle4, localObject2);
          }
          else if (i1 == 0)
          {
            i1 = 1;
            localObject1 = localInstructionHandle4;
          }
        }
      }
      else
      {
        localObject4 = ((Instruction)localObject3).copy();
        if ((localObject4 instanceof BranchInstruction)) {
          localObject2 = localInstructionList1.append((BranchInstruction)localObject4);
        } else {
          localObject2 = localInstructionList1.append((Instruction)localObject4);
        }
        if (((localObject4 instanceof LocalVariableInstruction)) || ((localObject4 instanceof RET)))
        {
          localObject5 = (IndexedInstruction)localObject4;
          int i2 = ((IndexedInstruction)localObject5).getIndex();
          localObject7 = getLocalVariableRegistry().lookupRegisteredLocalVariable(i2, localInstructionHandle4.getPosition());
          localObject9 = (LocalVariableGen)localHashMap2.get(localObject7);
          if (localHashMap2.get(localObject7) == null)
          {
            boolean bool1 = offsetInLocalVariableGenRange((LocalVariableGen)localObject7, i != 0 ? i - 1 : 0);
            boolean bool2 = offsetInLocalVariableGenRange((LocalVariableGen)localObject7, j + 1);
            if ((bool1) || (bool2))
            {
              localObject11 = ((LocalVariableGen)localObject7).getName();
              Type localType1 = ((LocalVariableGen)localObject7).getType();
              localObject9 = localMethodGenerator.addLocalVariable((String)localObject11, localType1, null, null);
              int i9 = ((LocalVariableGen)localObject9).getIndex();
              String str4 = localType1.getSignature();
              localHashMap2.put(localObject7, localObject9);
              n++;
              String str5 = "field" + n;
              local1.addField(new Field(1, localConstantPoolGen2.addUtf8(str5), localConstantPoolGen2.addUtf8(str4), null, localConstantPoolGen2.getConstantPool()));
              int i10 = localConstantPoolGen1.addFieldref(str2, str5, str4);
              InstructionHandle localInstructionHandle6;
              if (bool1)
              {
                localInstructionList2.append(InstructionConstants.DUP);
                localInstructionHandle6 = localInstructionList2.append(loadLocal(i2, localType1));
                localInstructionList2.append(new PUTFIELD(i10));
                if (!bool2) {
                  localHashMap4.put(localObject7, localInstructionHandle6);
                }
                localInstructionList4.append(InstructionConstants.ALOAD_1);
                localInstructionList4.append(new GETFIELD(i10));
                localInstructionList4.append(storeLocal(i9, localType1));
              }
              if (bool2)
              {
                localInstructionList5.append(InstructionConstants.ALOAD_1);
                localInstructionList5.append(loadLocal(i9, localType1));
                localInstructionList5.append(new PUTFIELD(i10));
                localInstructionList3.append(InstructionConstants.DUP);
                localInstructionList3.append(new GETFIELD(i10));
                localInstructionHandle6 = localInstructionList3.append(storeLocal(i2, localType1));
                if (!bool1) {
                  localHashMap3.put(localObject7, localInstructionHandle6);
                }
              }
            }
          }
        }
        if (localInstructionHandle4.hasTargeters()) {
          localHashMap1.put(localInstructionHandle4, localObject2);
        }
        if (i1 != 0)
        {
          do
          {
            localHashMap1.put(localObject1, localObject2);
            localObject1 = ((InstructionHandle)localObject1).getNext();
          } while (localObject1 != localInstructionHandle4);
          i1 = 0;
        }
      }
    }
    localInstructionHandle4 = paramInstructionHandle1;
    Object localObject3 = localInstructionList1.getStart();
    while (localObject3 != null)
    {
      localObject4 = localInstructionHandle4.getInstruction();
      localObject5 = ((InstructionHandle)localObject3).getInstruction();
      Object localObject10;
      int i3;
      if ((localObject4 instanceof BranchInstruction))
      {
        localObject6 = (BranchInstruction)localObject5;
        localObject7 = (BranchInstruction)localObject4;
        localObject9 = ((BranchInstruction)localObject7).getTarget();
        localObject10 = (InstructionHandle)localHashMap1.get(localObject9);
        ((BranchInstruction)localObject6).setTarget((InstructionHandle)localObject10);
        if ((localObject7 instanceof Select))
        {
          InstructionHandle[] arrayOfInstructionHandle = ((Select)localObject7).getTargets();
          localObject11 = ((Select)localObject6).getTargets();
          for (int i7 = 0; i7 < arrayOfInstructionHandle.length; i7++) {
            localObject11[i7] = ((InstructionHandle)localHashMap1.get(arrayOfInstructionHandle[i7]));
          }
        }
      }
      else if (((localObject4 instanceof LocalVariableInstruction)) || ((localObject4 instanceof RET)))
      {
        localObject6 = (IndexedInstruction)localObject5;
        i3 = ((IndexedInstruction)localObject6).getIndex();
        localObject9 = getLocalVariableRegistry().lookupRegisteredLocalVariable(i3, localInstructionHandle4.getPosition());
        localObject10 = (LocalVariableGen)localHashMap2.get(localObject9);
        int i6;
        if (localObject10 == null)
        {
          localObject11 = ((LocalVariableGen)localObject9).getName();
          Type localType2 = ((LocalVariableGen)localObject9).getType();
          localObject10 = localMethodGenerator.addLocalVariable((String)localObject11, localType2, null, null);
          i6 = ((LocalVariableGen)localObject10).getIndex();
          localHashMap2.put(localObject9, localObject10);
          localHashMap3.put(localObject9, localInstructionHandle3);
          localHashMap4.put(localObject9, localInstructionHandle3);
        }
        else
        {
          i6 = ((LocalVariableGen)localObject10).getIndex();
        }
        ((IndexedInstruction)localObject6).setIndex(i6);
      }
      if (localInstructionHandle4.hasTargeters())
      {
        localObject6 = localInstructionHandle4.getTargeters();
        for (i3 = 0; i3 < localObject6.length; i3++)
        {
          localObject9 = localObject6[i3];
          if (((localObject9 instanceof LocalVariableGen)) && (((LocalVariableGen)localObject9).getEnd() == localInstructionHandle4))
          {
            localObject10 = localHashMap2.get(localObject9);
            if (localObject10 != null) {
              localMethodGenerator.removeLocalVariable((LocalVariableGen)localObject10);
            }
          }
        }
      }
      if (!(localObject4 instanceof MarkerInstruction)) {
        localObject3 = ((InstructionHandle)localObject3).getNext();
      }
      localInstructionHandle4 = localInstructionHandle4.getNext();
    }
    localInstructionList3.append(InstructionConstants.POP);
    Object localObject4 = localHashMap3.entrySet().iterator();
    Object localObject8;
    while (((Iterator)localObject4).hasNext())
    {
      localObject5 = (Map.Entry)((Iterator)localObject4).next();
      localObject6 = (LocalVariableGen)((Map.Entry)localObject5).getKey();
      localObject8 = (InstructionHandle)((Map.Entry)localObject5).getValue();
      ((LocalVariableGen)localObject6).setStart((InstructionHandle)localObject8);
    }
    Object localObject5 = localHashMap4.entrySet().iterator();
    while (((Iterator)localObject5).hasNext())
    {
      localObject6 = (Map.Entry)((Iterator)localObject5).next();
      localObject8 = (LocalVariableGen)((Map.Entry)localObject6).getKey();
      localObject9 = (InstructionHandle)((Map.Entry)localObject6).getValue();
      ((LocalVariableGen)localObject8).setEnd((InstructionHandle)localObject9);
    }
    localXSLTC.dumpClass(local1.getJavaClass());
    Object localObject6 = getInstructionList();
    ((InstructionList)localObject6).insert(paramInstructionHandle1, localInstructionList2);
    ((InstructionList)localObject6).insert(paramInstructionHandle1, localInstructionList3);
    localInstructionList1.insert(localInstructionList4);
    localInstructionList1.append(localInstructionList5);
    localInstructionList1.append(InstructionConstants.RETURN);
    int i5;
    try
    {
      ((InstructionList)localObject6).delete(paramInstructionHandle1, paramInstructionHandle2);
    }
    catch (TargetLostException localTargetLostException)
    {
      localObject9 = localTargetLostException.getTargets();
      i5 = 0;
    }
    while (i5 < localObject9.length)
    {
      InstructionHandle localInstructionHandle5 = localObject9[i5];
      localObject11 = localInstructionHandle5.getTargeters();
      for (int i8 = 0; i8 < localObject11.length; i8++) {
        if ((localObject11[i8] instanceof LocalVariableGen))
        {
          LocalVariableGen localLocalVariableGen = (LocalVariableGen)localObject11[i8];
          if (localLocalVariableGen.getStart() == localInstructionHandle5) {
            localLocalVariableGen.setStart(localInstructionHandle3);
          }
          if (localLocalVariableGen.getEnd() == localInstructionHandle5) {
            localLocalVariableGen.setEnd(localInstructionHandle3);
          }
        }
        else
        {
          localObject11[i8].updateTarget(localInstructionHandle5, localInstructionHandle2);
        }
      }
      i5++;
    }
    String[] arrayOfString2 = getExceptions();
    for (int i4 = 0; i4 < arrayOfString2.length; i4++) {
      localMethodGenerator.addException(arrayOfString2[i4]);
    }
    return localMethodGenerator.getThisMethod();
  }
  
  private static Instruction loadLocal(int paramInt, Type paramType)
  {
    if (paramType == Type.BOOLEAN) {
      return new ILOAD(paramInt);
    }
    if (paramType == Type.INT) {
      return new ILOAD(paramInt);
    }
    if (paramType == Type.SHORT) {
      return new ILOAD(paramInt);
    }
    if (paramType == Type.LONG) {
      return new LLOAD(paramInt);
    }
    if (paramType == Type.BYTE) {
      return new ILOAD(paramInt);
    }
    if (paramType == Type.CHAR) {
      return new ILOAD(paramInt);
    }
    if (paramType == Type.FLOAT) {
      return new FLOAD(paramInt);
    }
    if (paramType == Type.DOUBLE) {
      return new DLOAD(paramInt);
    }
    return new ALOAD(paramInt);
  }
  
  private static Instruction storeLocal(int paramInt, Type paramType)
  {
    if (paramType == Type.BOOLEAN) {
      return new ISTORE(paramInt);
    }
    if (paramType == Type.INT) {
      return new ISTORE(paramInt);
    }
    if (paramType == Type.SHORT) {
      return new ISTORE(paramInt);
    }
    if (paramType == Type.LONG) {
      return new LSTORE(paramInt);
    }
    if (paramType == Type.BYTE) {
      return new ISTORE(paramInt);
    }
    if (paramType == Type.CHAR) {
      return new ISTORE(paramInt);
    }
    if (paramType == Type.FLOAT) {
      return new FSTORE(paramInt);
    }
    if (paramType == Type.DOUBLE) {
      return new DSTORE(paramInt);
    }
    return new ASTORE(paramInt);
  }
  
  public void markChunkStart()
  {
    getInstructionList().append(OutlineableChunkStart.OUTLINEABLECHUNKSTART);
    m_totalChunks += 1;
    m_openChunks += 1;
  }
  
  public void markChunkEnd()
  {
    getInstructionList().append(OutlineableChunkEnd.OUTLINEABLECHUNKEND);
    m_openChunks -= 1;
    if (m_openChunks < 0)
    {
      String str = new ErrorMsg("OUTLINE_ERR_UNBALANCED_MARKERS").toString();
      throw new InternalError(str);
    }
  }
  
  Method[] getGeneratedMethods(ClassGenerator paramClassGenerator)
  {
    InstructionList localInstructionList = getInstructionList();
    InstructionHandle localInstructionHandle = localInstructionList.getEnd();
    localInstructionList.setPositions();
    int i = localInstructionHandle.getPosition() + localInstructionHandle.getInstruction().getLength();
    if (i > 32767)
    {
      boolean bool = widenConditionalBranchTargetOffsets();
      if (bool)
      {
        localInstructionList.setPositions();
        localInstructionHandle = localInstructionList.getEnd();
        i = localInstructionHandle.getPosition() + localInstructionHandle.getInstruction().getLength();
      }
    }
    Method[] arrayOfMethod;
    if (i > 65535) {
      arrayOfMethod = outlineChunks(paramClassGenerator, i);
    } else {
      arrayOfMethod = new Method[] { getThisMethod() };
    }
    return arrayOfMethod;
  }
  
  protected Method getThisMethod()
  {
    stripAttributes(true);
    setMaxLocals();
    setMaxStack();
    removeNOPs();
    return getMethod();
  }
  
  boolean widenConditionalBranchTargetOffsets()
  {
    boolean bool = false;
    int i = 0;
    InstructionList localInstructionList = getInstructionList();
    Instruction localInstruction;
    for (Object localObject = localInstructionList.getStart(); localObject != null; localObject = ((InstructionHandle)localObject).getNext())
    {
      localInstruction = ((InstructionHandle)localObject).getInstruction();
      switch (localInstruction.getOpcode())
      {
      case 167: 
      case 168: 
        i += 2;
        break;
      case 170: 
      case 171: 
        i += 3;
        break;
      case 153: 
      case 154: 
      case 155: 
      case 156: 
      case 157: 
      case 158: 
      case 159: 
      case 160: 
      case 161: 
      case 162: 
      case 163: 
      case 164: 
      case 165: 
      case 166: 
      case 198: 
      case 199: 
        i += 5;
      }
    }
    for (localObject = localInstructionList.getStart(); localObject != null; localObject = ((InstructionHandle)localObject).getNext())
    {
      localInstruction = ((InstructionHandle)localObject).getInstruction();
      if ((localInstruction instanceof IfInstruction))
      {
        IfInstruction localIfInstruction1 = (IfInstruction)localInstruction;
        BranchHandle localBranchHandle1 = (BranchHandle)localObject;
        InstructionHandle localInstructionHandle1 = localIfInstruction1.getTarget();
        int j = localInstructionHandle1.getPosition() - localBranchHandle1.getPosition();
        if ((j - i < 32768) || (j + i > 32767))
        {
          InstructionHandle localInstructionHandle2 = localBranchHandle1.getNext();
          IfInstruction localIfInstruction2 = localIfInstruction1.negate();
          BranchHandle localBranchHandle2 = localInstructionList.append(localBranchHandle1, localIfInstruction2);
          BranchHandle localBranchHandle3 = localInstructionList.append(localBranchHandle2, new GOTO(localInstructionHandle1));
          if (localInstructionHandle2 == null) {
            localInstructionHandle2 = localInstructionList.append(localBranchHandle3, NOP);
          }
          localBranchHandle2.updateTarget(localInstructionHandle1, localInstructionHandle2);
          if (localBranchHandle1.hasTargeters())
          {
            InstructionTargeter[] arrayOfInstructionTargeter = localBranchHandle1.getTargeters();
            for (int k = 0; k < arrayOfInstructionTargeter.length; k++)
            {
              InstructionTargeter localInstructionTargeter = arrayOfInstructionTargeter[k];
              if ((localInstructionTargeter instanceof LocalVariableGen))
              {
                LocalVariableGen localLocalVariableGen = (LocalVariableGen)localInstructionTargeter;
                if (localLocalVariableGen.getStart() == localBranchHandle1) {
                  localLocalVariableGen.setStart(localBranchHandle2);
                } else if (localLocalVariableGen.getEnd() == localBranchHandle1) {
                  localLocalVariableGen.setEnd(localBranchHandle3);
                }
              }
              else
              {
                localInstructionTargeter.updateTarget(localBranchHandle1, localBranchHandle2);
              }
            }
          }
          try
          {
            localInstructionList.delete(localBranchHandle1);
          }
          catch (TargetLostException localTargetLostException)
          {
            String str = new ErrorMsg("OUTLINE_ERR_DELETED_TARGET", localTargetLostException.getMessage()).toString();
            throw new InternalError(str);
          }
          localObject = localBranchHandle3;
          bool = true;
        }
      }
    }
    return bool;
  }
  
  private class Chunk
    implements Comparable
  {
    private InstructionHandle m_start;
    private InstructionHandle m_end;
    private int m_size;
    
    Chunk(InstructionHandle paramInstructionHandle1, InstructionHandle paramInstructionHandle2)
    {
      m_start = paramInstructionHandle1;
      m_end = paramInstructionHandle2;
      m_size = (paramInstructionHandle2.getPosition() - paramInstructionHandle1.getPosition());
    }
    
    boolean isAdjacentTo(Chunk paramChunk)
    {
      return getChunkEnd().getNext() == paramChunk.getChunkStart();
    }
    
    InstructionHandle getChunkStart()
    {
      return m_start;
    }
    
    InstructionHandle getChunkEnd()
    {
      return m_end;
    }
    
    int getChunkSize()
    {
      return m_size;
    }
    
    public int compareTo(Object paramObject)
    {
      return getChunkSize() - ((Chunk)paramObject).getChunkSize();
    }
  }
  
  protected class LocalVariableRegistry
  {
    protected ArrayList _variables = new ArrayList();
    protected HashMap _nameToLVGMap = new HashMap();
    
    protected LocalVariableRegistry() {}
    
    protected void registerLocalVariable(LocalVariableGen paramLocalVariableGen)
    {
      int i = paramLocalVariableGen.getIndex();
      int j = _variables.size();
      if (i >= j)
      {
        for (int k = j; k < i; k++) {
          _variables.add(null);
        }
        _variables.add(paramLocalVariableGen);
      }
      else
      {
        Object localObject = _variables.get(i);
        if (localObject != null)
        {
          if ((localObject instanceof LocalVariableGen))
          {
            ArrayList localArrayList = new ArrayList();
            localArrayList.add(localObject);
            localArrayList.add(paramLocalVariableGen);
            _variables.set(i, localArrayList);
          }
          else
          {
            ((ArrayList)localObject).add(paramLocalVariableGen);
          }
        }
        else {
          _variables.set(i, paramLocalVariableGen);
        }
      }
      registerByName(paramLocalVariableGen);
    }
    
    protected LocalVariableGen lookupRegisteredLocalVariable(int paramInt1, int paramInt2)
    {
      Object localObject1 = _variables != null ? _variables.get(paramInt1) : null;
      if (localObject1 != null)
      {
        Object localObject2;
        if ((localObject1 instanceof LocalVariableGen))
        {
          localObject2 = (LocalVariableGen)localObject1;
          if (offsetInLocalVariableGenRange((LocalVariableGen)localObject2, paramInt2)) {
            return (LocalVariableGen)localObject2;
          }
        }
        else
        {
          localObject2 = (ArrayList)localObject1;
          int i = ((ArrayList)localObject2).size();
          for (int j = 0; j < i; j++)
          {
            LocalVariableGen localLocalVariableGen = (LocalVariableGen)((ArrayList)localObject2).get(j);
            if (offsetInLocalVariableGenRange(localLocalVariableGen, paramInt2)) {
              return localLocalVariableGen;
            }
          }
        }
      }
      return null;
    }
    
    protected void registerByName(LocalVariableGen paramLocalVariableGen)
    {
      Object localObject = _nameToLVGMap.get(paramLocalVariableGen.getName());
      if (localObject == null)
      {
        _nameToLVGMap.put(paramLocalVariableGen.getName(), paramLocalVariableGen);
      }
      else
      {
        ArrayList localArrayList;
        if ((localObject instanceof ArrayList))
        {
          localArrayList = (ArrayList)localObject;
          localArrayList.add(paramLocalVariableGen);
        }
        else
        {
          localArrayList = new ArrayList();
          localArrayList.add(localObject);
          localArrayList.add(paramLocalVariableGen);
        }
        _nameToLVGMap.put(paramLocalVariableGen.getName(), localArrayList);
      }
    }
    
    protected void removeByNameTracking(LocalVariableGen paramLocalVariableGen)
    {
      Object localObject = _nameToLVGMap.get(paramLocalVariableGen.getName());
      if ((localObject instanceof ArrayList))
      {
        ArrayList localArrayList = (ArrayList)localObject;
        for (int i = 0; i < localArrayList.size(); i++) {
          if (localArrayList.get(i) == paramLocalVariableGen)
          {
            localArrayList.remove(i);
            break;
          }
        }
      }
      else
      {
        _nameToLVGMap.remove(paramLocalVariableGen);
      }
    }
    
    protected LocalVariableGen lookUpByName(String paramString)
    {
      LocalVariableGen localLocalVariableGen = null;
      Object localObject = _nameToLVGMap.get(paramString);
      if ((localObject instanceof ArrayList))
      {
        ArrayList localArrayList = (ArrayList)localObject;
        for (int i = 0; i < localArrayList.size(); i++)
        {
          localLocalVariableGen = (LocalVariableGen)localArrayList.get(i);
          if (localLocalVariableGen.getName() == paramString) {
            break;
          }
        }
      }
      else
      {
        localLocalVariableGen = (LocalVariableGen)localObject;
      }
      return localLocalVariableGen;
    }
    
    protected LocalVariableGen[] getLocals(boolean paramBoolean)
    {
      LocalVariableGen[] arrayOfLocalVariableGen = null;
      ArrayList localArrayList1 = new ArrayList();
      Object localObject;
      ArrayList localArrayList2;
      int k;
      if (paramBoolean)
      {
        int i = localArrayList1.size();
        for (int j = 0; j < i; j++)
        {
          localObject = _variables.get(j);
          if (localObject != null) {
            if ((localObject instanceof ArrayList))
            {
              localArrayList2 = (ArrayList)localObject;
              for (k = 0; k < localArrayList2.size(); k++) {
                localArrayList1.add(localArrayList2.get(j));
              }
            }
            else
            {
              localArrayList1.add(localObject);
            }
          }
        }
      }
      else
      {
        Iterator localIterator = _nameToLVGMap.entrySet().iterator();
        while (localIterator.hasNext())
        {
          Map.Entry localEntry = (Map.Entry)localIterator.next();
          localObject = localEntry.getValue();
          if (localObject != null) {
            if ((localObject instanceof ArrayList))
            {
              localArrayList2 = (ArrayList)localObject;
              for (k = 0; k < localArrayList2.size(); k++) {
                localArrayList1.add(localArrayList2.get(k));
              }
            }
            else
            {
              localArrayList1.add(localObject);
            }
          }
        }
      }
      arrayOfLocalVariableGen = new LocalVariableGen[localArrayList1.size()];
      localArrayList1.toArray(arrayOfLocalVariableGen);
      return arrayOfLocalVariableGen;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\util\MethodGenerator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */