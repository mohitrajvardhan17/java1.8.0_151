package jdk.internal.org.objectweb.asm;

class MethodWriter
  extends MethodVisitor
{
  static final int ACC_CONSTRUCTOR = 524288;
  static final int SAME_FRAME = 0;
  static final int SAME_LOCALS_1_STACK_ITEM_FRAME = 64;
  static final int RESERVED = 128;
  static final int SAME_LOCALS_1_STACK_ITEM_FRAME_EXTENDED = 247;
  static final int CHOP_FRAME = 248;
  static final int SAME_FRAME_EXTENDED = 251;
  static final int APPEND_FRAME = 252;
  static final int FULL_FRAME = 255;
  private static final int FRAMES = 0;
  private static final int MAXS = 1;
  private static final int NOTHING = 2;
  final ClassWriter cw;
  private int access;
  private final int name;
  private final int desc;
  private final String descriptor;
  String signature;
  int classReaderOffset;
  int classReaderLength;
  int exceptionCount;
  int[] exceptions;
  private ByteVector annd;
  private AnnotationWriter anns;
  private AnnotationWriter ianns;
  private AnnotationWriter tanns;
  private AnnotationWriter itanns;
  private AnnotationWriter[] panns;
  private AnnotationWriter[] ipanns;
  private int synthetics;
  private Attribute attrs;
  private ByteVector code = new ByteVector();
  private int maxStack;
  private int maxLocals;
  private int currentLocals;
  private int frameCount;
  private ByteVector stackMap;
  private int previousFrameOffset;
  private int[] previousFrame;
  private int[] frame;
  private int handlerCount;
  private Handler firstHandler;
  private Handler lastHandler;
  private int methodParametersCount;
  private ByteVector methodParameters;
  private int localVarCount;
  private ByteVector localVar;
  private int localVarTypeCount;
  private ByteVector localVarType;
  private int lineNumberCount;
  private ByteVector lineNumber;
  private int lastCodeOffset;
  private AnnotationWriter ctanns;
  private AnnotationWriter ictanns;
  private Attribute cattrs;
  private boolean resize;
  private int subroutines;
  private final int compute;
  private Label labels;
  private Label previousBlock;
  private Label currentBlock;
  private int stackSize;
  private int maxStackSize;
  
  MethodWriter(ClassWriter paramClassWriter, int paramInt, String paramString1, String paramString2, String paramString3, String[] paramArrayOfString, boolean paramBoolean1, boolean paramBoolean2)
  {
    super(327680);
    if (firstMethod == null) {
      firstMethod = this;
    } else {
      lastMethod.mv = this;
    }
    lastMethod = this;
    cw = paramClassWriter;
    access = paramInt;
    if ("<init>".equals(paramString1)) {
      access |= 0x80000;
    }
    name = paramClassWriter.newUTF8(paramString1);
    desc = paramClassWriter.newUTF8(paramString2);
    descriptor = paramString2;
    signature = paramString3;
    int i;
    if ((paramArrayOfString != null) && (paramArrayOfString.length > 0))
    {
      exceptionCount = paramArrayOfString.length;
      exceptions = new int[exceptionCount];
      for (i = 0; i < exceptionCount; i++) {
        exceptions[i] = paramClassWriter.newClass(paramArrayOfString[i]);
      }
    }
    compute = (paramBoolean1 ? 1 : paramBoolean2 ? 0 : 2);
    if ((paramBoolean1) || (paramBoolean2))
    {
      i = Type.getArgumentsAndReturnSizes(descriptor) >> 2;
      if ((paramInt & 0x8) != 0) {
        i--;
      }
      maxLocals = i;
      currentLocals = i;
      labels = new Label();
      labels.status |= 0x8;
      visitLabel(labels);
    }
  }
  
  public void visitParameter(String paramString, int paramInt)
  {
    if (methodParameters == null) {
      methodParameters = new ByteVector();
    }
    methodParametersCount += 1;
    methodParameters.putShort(paramString == null ? 0 : cw.newUTF8(paramString)).putShort(paramInt);
  }
  
  public AnnotationVisitor visitAnnotationDefault()
  {
    annd = new ByteVector();
    return new AnnotationWriter(cw, false, annd, null, 0);
  }
  
  public AnnotationVisitor visitAnnotation(String paramString, boolean paramBoolean)
  {
    ByteVector localByteVector = new ByteVector();
    localByteVector.putShort(cw.newUTF8(paramString)).putShort(0);
    AnnotationWriter localAnnotationWriter = new AnnotationWriter(cw, true, localByteVector, localByteVector, 2);
    if (paramBoolean)
    {
      next = anns;
      anns = localAnnotationWriter;
    }
    else
    {
      next = ianns;
      ianns = localAnnotationWriter;
    }
    return localAnnotationWriter;
  }
  
  public AnnotationVisitor visitTypeAnnotation(int paramInt, TypePath paramTypePath, String paramString, boolean paramBoolean)
  {
    ByteVector localByteVector = new ByteVector();
    AnnotationWriter.putTarget(paramInt, paramTypePath, localByteVector);
    localByteVector.putShort(cw.newUTF8(paramString)).putShort(0);
    AnnotationWriter localAnnotationWriter = new AnnotationWriter(cw, true, localByteVector, localByteVector, length - 2);
    if (paramBoolean)
    {
      next = tanns;
      tanns = localAnnotationWriter;
    }
    else
    {
      next = itanns;
      itanns = localAnnotationWriter;
    }
    return localAnnotationWriter;
  }
  
  public AnnotationVisitor visitParameterAnnotation(int paramInt, String paramString, boolean paramBoolean)
  {
    ByteVector localByteVector = new ByteVector();
    if ("Ljava/lang/Synthetic;".equals(paramString))
    {
      synthetics = Math.max(synthetics, paramInt + 1);
      return new AnnotationWriter(cw, false, localByteVector, null, 0);
    }
    localByteVector.putShort(cw.newUTF8(paramString)).putShort(0);
    AnnotationWriter localAnnotationWriter = new AnnotationWriter(cw, true, localByteVector, localByteVector, 2);
    if (paramBoolean)
    {
      if (panns == null) {
        panns = new AnnotationWriter[Type.getArgumentTypes(descriptor).length];
      }
      next = panns[paramInt];
      panns[paramInt] = localAnnotationWriter;
    }
    else
    {
      if (ipanns == null) {
        ipanns = new AnnotationWriter[Type.getArgumentTypes(descriptor).length];
      }
      next = ipanns[paramInt];
      ipanns[paramInt] = localAnnotationWriter;
    }
    return localAnnotationWriter;
  }
  
  public void visitAttribute(Attribute paramAttribute)
  {
    if (paramAttribute.isCodeAttribute())
    {
      next = cattrs;
      cattrs = paramAttribute;
    }
    else
    {
      next = attrs;
      attrs = paramAttribute;
    }
  }
  
  public void visitCode() {}
  
  public void visitFrame(int paramInt1, int paramInt2, Object[] paramArrayOfObject1, int paramInt3, Object[] paramArrayOfObject2)
  {
    if (compute == 0) {
      return;
    }
    int i;
    int j;
    if (paramInt1 == -1)
    {
      if (previousFrame == null) {
        visitImplicitFirstFrame();
      }
      currentLocals = paramInt2;
      i = startFrame(code.length, paramInt2, paramInt3);
      for (j = 0; j < paramInt2; j++) {
        if ((paramArrayOfObject1[j] instanceof String)) {
          frame[(i++)] = (0x1700000 | cw.addType((String)paramArrayOfObject1[j]));
        } else if ((paramArrayOfObject1[j] instanceof Integer)) {
          frame[(i++)] = ((Integer)paramArrayOfObject1[j]).intValue();
        } else {
          frame[(i++)] = (0x1800000 | cw.addUninitializedType("", position));
        }
      }
      for (j = 0; j < paramInt3; j++) {
        if ((paramArrayOfObject2[j] instanceof String)) {
          frame[(i++)] = (0x1700000 | cw.addType((String)paramArrayOfObject2[j]));
        } else if ((paramArrayOfObject2[j] instanceof Integer)) {
          frame[(i++)] = ((Integer)paramArrayOfObject2[j]).intValue();
        } else {
          frame[(i++)] = (0x1800000 | cw.addUninitializedType("", position));
        }
      }
      endFrame();
    }
    else
    {
      if (stackMap == null)
      {
        stackMap = new ByteVector();
        i = code.length;
      }
      else
      {
        i = code.length - previousFrameOffset - 1;
        if (i < 0)
        {
          if (paramInt1 == 3) {
            return;
          }
          throw new IllegalStateException();
        }
      }
      switch (paramInt1)
      {
      case 0: 
        currentLocals = paramInt2;
        stackMap.putByte(255).putShort(i).putShort(paramInt2);
        for (j = 0; j < paramInt2; j++) {
          writeFrameType(paramArrayOfObject1[j]);
        }
        stackMap.putShort(paramInt3);
        for (j = 0; j < paramInt3; j++) {
          writeFrameType(paramArrayOfObject2[j]);
        }
        break;
      case 1: 
        currentLocals += paramInt2;
        stackMap.putByte(251 + paramInt2).putShort(i);
        for (j = 0; j < paramInt2; j++) {
          writeFrameType(paramArrayOfObject1[j]);
        }
        break;
      case 2: 
        currentLocals -= paramInt2;
        stackMap.putByte(251 - paramInt2).putShort(i);
        break;
      case 3: 
        if (i < 64) {
          stackMap.putByte(i);
        } else {
          stackMap.putByte(251).putShort(i);
        }
        break;
      case 4: 
        if (i < 64) {
          stackMap.putByte(64 + i);
        } else {
          stackMap.putByte(247).putShort(i);
        }
        writeFrameType(paramArrayOfObject2[0]);
      }
      previousFrameOffset = code.length;
      frameCount += 1;
    }
    maxStack = Math.max(maxStack, paramInt3);
    maxLocals = Math.max(maxLocals, currentLocals);
  }
  
  public void visitInsn(int paramInt)
  {
    lastCodeOffset = code.length;
    code.putByte(paramInt);
    if (currentBlock != null)
    {
      if (compute == 0)
      {
        currentBlock.frame.execute(paramInt, 0, null, null);
      }
      else
      {
        int i = stackSize + Frame.SIZE[paramInt];
        if (i > maxStackSize) {
          maxStackSize = i;
        }
        stackSize = i;
      }
      if (((paramInt >= 172) && (paramInt <= 177)) || (paramInt == 191)) {
        noSuccessor();
      }
    }
  }
  
  public void visitIntInsn(int paramInt1, int paramInt2)
  {
    lastCodeOffset = code.length;
    if (currentBlock != null) {
      if (compute == 0)
      {
        currentBlock.frame.execute(paramInt1, paramInt2, null, null);
      }
      else if (paramInt1 != 188)
      {
        int i = stackSize + 1;
        if (i > maxStackSize) {
          maxStackSize = i;
        }
        stackSize = i;
      }
    }
    if (paramInt1 == 17) {
      code.put12(paramInt1, paramInt2);
    } else {
      code.put11(paramInt1, paramInt2);
    }
  }
  
  public void visitVarInsn(int paramInt1, int paramInt2)
  {
    lastCodeOffset = code.length;
    int i;
    if (currentBlock != null) {
      if (compute == 0)
      {
        currentBlock.frame.execute(paramInt1, paramInt2, null, null);
      }
      else if (paramInt1 == 169)
      {
        currentBlock.status |= 0x100;
        currentBlock.inputStackTop = stackSize;
        noSuccessor();
      }
      else
      {
        i = stackSize + Frame.SIZE[paramInt1];
        if (i > maxStackSize) {
          maxStackSize = i;
        }
        stackSize = i;
      }
    }
    if (compute != 2)
    {
      if ((paramInt1 == 22) || (paramInt1 == 24) || (paramInt1 == 55) || (paramInt1 == 57)) {
        i = paramInt2 + 2;
      } else {
        i = paramInt2 + 1;
      }
      if (i > maxLocals) {
        maxLocals = i;
      }
    }
    if ((paramInt2 < 4) && (paramInt1 != 169))
    {
      if (paramInt1 < 54) {
        i = 26 + (paramInt1 - 21 << 2) + paramInt2;
      } else {
        i = 59 + (paramInt1 - 54 << 2) + paramInt2;
      }
      code.putByte(i);
    }
    else if (paramInt2 >= 256)
    {
      code.putByte(196).put12(paramInt1, paramInt2);
    }
    else
    {
      code.put11(paramInt1, paramInt2);
    }
    if ((paramInt1 >= 54) && (compute == 0) && (handlerCount > 0)) {
      visitLabel(new Label());
    }
  }
  
  public void visitTypeInsn(int paramInt, String paramString)
  {
    lastCodeOffset = code.length;
    Item localItem = cw.newClassItem(paramString);
    if (currentBlock != null) {
      if (compute == 0)
      {
        currentBlock.frame.execute(paramInt, code.length, cw, localItem);
      }
      else if (paramInt == 187)
      {
        int i = stackSize + 1;
        if (i > maxStackSize) {
          maxStackSize = i;
        }
        stackSize = i;
      }
    }
    code.put12(paramInt, index);
  }
  
  public void visitFieldInsn(int paramInt, String paramString1, String paramString2, String paramString3)
  {
    lastCodeOffset = code.length;
    Item localItem = cw.newFieldItem(paramString1, paramString2, paramString3);
    if (currentBlock != null) {
      if (compute == 0)
      {
        currentBlock.frame.execute(paramInt, 0, cw, localItem);
      }
      else
      {
        int j = paramString3.charAt(0);
        int i;
        switch (paramInt)
        {
        case 178: 
          i = stackSize + ((j == 68) || (j == 74) ? 2 : 1);
          break;
        case 179: 
          i = stackSize + ((j == 68) || (j == 74) ? -2 : -1);
          break;
        case 180: 
          i = stackSize + ((j == 68) || (j == 74) ? 1 : 0);
          break;
        default: 
          i = stackSize + ((j == 68) || (j == 74) ? -3 : -2);
        }
        if (i > maxStackSize) {
          maxStackSize = i;
        }
        stackSize = i;
      }
    }
    code.put12(paramInt, index);
  }
  
  public void visitMethodInsn(int paramInt, String paramString1, String paramString2, String paramString3, boolean paramBoolean)
  {
    lastCodeOffset = code.length;
    Item localItem = cw.newMethodItem(paramString1, paramString2, paramString3, paramBoolean);
    int i = intVal;
    if (currentBlock != null) {
      if (compute == 0)
      {
        currentBlock.frame.execute(paramInt, 0, cw, localItem);
      }
      else
      {
        if (i == 0)
        {
          i = Type.getArgumentsAndReturnSizes(paramString3);
          intVal = i;
        }
        int j;
        if (paramInt == 184) {
          j = stackSize - (i >> 2) + (i & 0x3) + 1;
        } else {
          j = stackSize - (i >> 2) + (i & 0x3);
        }
        if (j > maxStackSize) {
          maxStackSize = j;
        }
        stackSize = j;
      }
    }
    if (paramInt == 185)
    {
      if (i == 0)
      {
        i = Type.getArgumentsAndReturnSizes(paramString3);
        intVal = i;
      }
      code.put12(185, index).put11(i >> 2, 0);
    }
    else
    {
      code.put12(paramInt, index);
    }
  }
  
  public void visitInvokeDynamicInsn(String paramString1, String paramString2, Handle paramHandle, Object... paramVarArgs)
  {
    lastCodeOffset = code.length;
    Item localItem = cw.newInvokeDynamicItem(paramString1, paramString2, paramHandle, paramVarArgs);
    int i = intVal;
    if (currentBlock != null) {
      if (compute == 0)
      {
        currentBlock.frame.execute(186, 0, cw, localItem);
      }
      else
      {
        if (i == 0)
        {
          i = Type.getArgumentsAndReturnSizes(paramString2);
          intVal = i;
        }
        int j = stackSize - (i >> 2) + (i & 0x3) + 1;
        if (j > maxStackSize) {
          maxStackSize = j;
        }
        stackSize = j;
      }
    }
    code.put12(186, index);
    code.putShort(0);
  }
  
  public void visitJumpInsn(int paramInt, Label paramLabel)
  {
    lastCodeOffset = code.length;
    Label localLabel = null;
    if (currentBlock != null) {
      if (compute == 0)
      {
        currentBlock.frame.execute(paramInt, 0, null, null);
        getFirststatus |= 0x10;
        addSuccessor(0, paramLabel);
        if (paramInt != 167) {
          localLabel = new Label();
        }
      }
      else if (paramInt == 168)
      {
        if ((status & 0x200) == 0)
        {
          status |= 0x200;
          subroutines += 1;
        }
        currentBlock.status |= 0x80;
        addSuccessor(stackSize + 1, paramLabel);
        localLabel = new Label();
      }
      else
      {
        stackSize += Frame.SIZE[paramInt];
        addSuccessor(stackSize, paramLabel);
      }
    }
    if (((status & 0x2) != 0) && (position - code.length < 32768))
    {
      if (paramInt == 167)
      {
        code.putByte(200);
      }
      else if (paramInt == 168)
      {
        code.putByte(201);
      }
      else
      {
        if (localLabel != null) {
          status |= 0x10;
        }
        code.putByte(paramInt <= 166 ? (paramInt + 1 ^ 0x1) - 1 : paramInt ^ 0x1);
        code.putShort(8);
        code.putByte(200);
      }
      paramLabel.put(this, code, code.length - 1, true);
    }
    else
    {
      code.putByte(paramInt);
      paramLabel.put(this, code, code.length - 1, false);
    }
    if (currentBlock != null)
    {
      if (localLabel != null) {
        visitLabel(localLabel);
      }
      if (paramInt == 167) {
        noSuccessor();
      }
    }
  }
  
  public void visitLabel(Label paramLabel)
  {
    resize |= paramLabel.resolve(this, code.length, code.data);
    if ((status & 0x1) != 0) {
      return;
    }
    if (compute == 0)
    {
      if (currentBlock != null)
      {
        if (position == currentBlock.position)
        {
          currentBlock.status |= status & 0x10;
          frame = currentBlock.frame;
          return;
        }
        addSuccessor(0, paramLabel);
      }
      currentBlock = paramLabel;
      if (frame == null)
      {
        frame = new Frame();
        frame.owner = paramLabel;
      }
      if (previousBlock != null)
      {
        if (position == previousBlock.position)
        {
          previousBlock.status |= status & 0x10;
          frame = previousBlock.frame;
          currentBlock = previousBlock;
          return;
        }
        previousBlock.successor = paramLabel;
      }
      previousBlock = paramLabel;
    }
    else if (compute == 1)
    {
      if (currentBlock != null)
      {
        currentBlock.outputStackMax = maxStackSize;
        addSuccessor(stackSize, paramLabel);
      }
      currentBlock = paramLabel;
      stackSize = 0;
      maxStackSize = 0;
      if (previousBlock != null) {
        previousBlock.successor = paramLabel;
      }
      previousBlock = paramLabel;
    }
  }
  
  public void visitLdcInsn(Object paramObject)
  {
    lastCodeOffset = code.length;
    Item localItem = cw.newConstItem(paramObject);
    if (currentBlock != null) {
      if (compute == 0)
      {
        currentBlock.frame.execute(18, 0, cw, localItem);
      }
      else
      {
        if ((type == 5) || (type == 6)) {
          i = stackSize + 2;
        } else {
          i = stackSize + 1;
        }
        if (i > maxStackSize) {
          maxStackSize = i;
        }
        stackSize = i;
      }
    }
    int i = index;
    if ((type == 5) || (type == 6)) {
      code.put12(20, i);
    } else if (i >= 256) {
      code.put12(19, i);
    } else {
      code.put11(18, i);
    }
  }
  
  public void visitIincInsn(int paramInt1, int paramInt2)
  {
    lastCodeOffset = code.length;
    if ((currentBlock != null) && (compute == 0)) {
      currentBlock.frame.execute(132, paramInt1, null, null);
    }
    if (compute != 2)
    {
      int i = paramInt1 + 1;
      if (i > maxLocals) {
        maxLocals = i;
      }
    }
    if ((paramInt1 > 255) || (paramInt2 > 127) || (paramInt2 < -128)) {
      code.putByte(196).put12(132, paramInt1).putShort(paramInt2);
    } else {
      code.putByte(132).put11(paramInt1, paramInt2);
    }
  }
  
  public void visitTableSwitchInsn(int paramInt1, int paramInt2, Label paramLabel, Label... paramVarArgs)
  {
    lastCodeOffset = code.length;
    int i = code.length;
    code.putByte(170);
    code.putByteArray(null, 0, (4 - code.length % 4) % 4);
    paramLabel.put(this, code, i, true);
    code.putInt(paramInt1).putInt(paramInt2);
    for (int j = 0; j < paramVarArgs.length; j++) {
      paramVarArgs[j].put(this, code, i, true);
    }
    visitSwitchInsn(paramLabel, paramVarArgs);
  }
  
  public void visitLookupSwitchInsn(Label paramLabel, int[] paramArrayOfInt, Label[] paramArrayOfLabel)
  {
    lastCodeOffset = code.length;
    int i = code.length;
    code.putByte(171);
    code.putByteArray(null, 0, (4 - code.length % 4) % 4);
    paramLabel.put(this, code, i, true);
    code.putInt(paramArrayOfLabel.length);
    for (int j = 0; j < paramArrayOfLabel.length; j++)
    {
      code.putInt(paramArrayOfInt[j]);
      paramArrayOfLabel[j].put(this, code, i, true);
    }
    visitSwitchInsn(paramLabel, paramArrayOfLabel);
  }
  
  private void visitSwitchInsn(Label paramLabel, Label[] paramArrayOfLabel)
  {
    if (currentBlock != null)
    {
      int i;
      if (compute == 0)
      {
        currentBlock.frame.execute(171, 0, null, null);
        addSuccessor(0, paramLabel);
        getFirststatus |= 0x10;
        for (i = 0; i < paramArrayOfLabel.length; i++)
        {
          addSuccessor(0, paramArrayOfLabel[i]);
          getFirststatus |= 0x10;
        }
      }
      else
      {
        stackSize -= 1;
        addSuccessor(stackSize, paramLabel);
        for (i = 0; i < paramArrayOfLabel.length; i++) {
          addSuccessor(stackSize, paramArrayOfLabel[i]);
        }
      }
      noSuccessor();
    }
  }
  
  public void visitMultiANewArrayInsn(String paramString, int paramInt)
  {
    lastCodeOffset = code.length;
    Item localItem = cw.newClassItem(paramString);
    if (currentBlock != null) {
      if (compute == 0) {
        currentBlock.frame.execute(197, paramInt, cw, localItem);
      } else {
        stackSize += 1 - paramInt;
      }
    }
    code.put12(197, index).putByte(paramInt);
  }
  
  public AnnotationVisitor visitInsnAnnotation(int paramInt, TypePath paramTypePath, String paramString, boolean paramBoolean)
  {
    ByteVector localByteVector = new ByteVector();
    paramInt = paramInt & 0xFF0000FF | lastCodeOffset << 8;
    AnnotationWriter.putTarget(paramInt, paramTypePath, localByteVector);
    localByteVector.putShort(cw.newUTF8(paramString)).putShort(0);
    AnnotationWriter localAnnotationWriter = new AnnotationWriter(cw, true, localByteVector, localByteVector, length - 2);
    if (paramBoolean)
    {
      next = ctanns;
      ctanns = localAnnotationWriter;
    }
    else
    {
      next = ictanns;
      ictanns = localAnnotationWriter;
    }
    return localAnnotationWriter;
  }
  
  public void visitTryCatchBlock(Label paramLabel1, Label paramLabel2, Label paramLabel3, String paramString)
  {
    handlerCount += 1;
    Handler localHandler = new Handler();
    start = paramLabel1;
    end = paramLabel2;
    handler = paramLabel3;
    desc = paramString;
    type = (paramString != null ? cw.newClass(paramString) : 0);
    if (lastHandler == null) {
      firstHandler = localHandler;
    } else {
      lastHandler.next = localHandler;
    }
    lastHandler = localHandler;
  }
  
  public AnnotationVisitor visitTryCatchAnnotation(int paramInt, TypePath paramTypePath, String paramString, boolean paramBoolean)
  {
    ByteVector localByteVector = new ByteVector();
    AnnotationWriter.putTarget(paramInt, paramTypePath, localByteVector);
    localByteVector.putShort(cw.newUTF8(paramString)).putShort(0);
    AnnotationWriter localAnnotationWriter = new AnnotationWriter(cw, true, localByteVector, localByteVector, length - 2);
    if (paramBoolean)
    {
      next = ctanns;
      ctanns = localAnnotationWriter;
    }
    else
    {
      next = ictanns;
      ictanns = localAnnotationWriter;
    }
    return localAnnotationWriter;
  }
  
  public void visitLocalVariable(String paramString1, String paramString2, String paramString3, Label paramLabel1, Label paramLabel2, int paramInt)
  {
    if (paramString3 != null)
    {
      if (localVarType == null) {
        localVarType = new ByteVector();
      }
      localVarTypeCount += 1;
      localVarType.putShort(position).putShort(position - position).putShort(cw.newUTF8(paramString1)).putShort(cw.newUTF8(paramString3)).putShort(paramInt);
    }
    if (localVar == null) {
      localVar = new ByteVector();
    }
    localVarCount += 1;
    localVar.putShort(position).putShort(position - position).putShort(cw.newUTF8(paramString1)).putShort(cw.newUTF8(paramString2)).putShort(paramInt);
    if (compute != 2)
    {
      int i = paramString2.charAt(0);
      int j = paramInt + ((i == 74) || (i == 68) ? 2 : 1);
      if (j > maxLocals) {
        maxLocals = j;
      }
    }
  }
  
  public AnnotationVisitor visitLocalVariableAnnotation(int paramInt, TypePath paramTypePath, Label[] paramArrayOfLabel1, Label[] paramArrayOfLabel2, int[] paramArrayOfInt, String paramString, boolean paramBoolean)
  {
    ByteVector localByteVector = new ByteVector();
    localByteVector.putByte(paramInt >>> 24).putShort(paramArrayOfLabel1.length);
    for (int i = 0; i < paramArrayOfLabel1.length; i++) {
      localByteVector.putShort(position).putShort(position - position).putShort(paramArrayOfInt[i]);
    }
    if (paramTypePath == null)
    {
      localByteVector.putByte(0);
    }
    else
    {
      i = b[offset] * 2 + 1;
      localByteVector.putByteArray(b, offset, i);
    }
    localByteVector.putShort(cw.newUTF8(paramString)).putShort(0);
    AnnotationWriter localAnnotationWriter = new AnnotationWriter(cw, true, localByteVector, localByteVector, length - 2);
    if (paramBoolean)
    {
      next = ctanns;
      ctanns = localAnnotationWriter;
    }
    else
    {
      next = ictanns;
      ictanns = localAnnotationWriter;
    }
    return localAnnotationWriter;
  }
  
  public void visitLineNumber(int paramInt, Label paramLabel)
  {
    if (lineNumber == null) {
      lineNumber = new ByteVector();
    }
    lineNumberCount += 1;
    lineNumber.putShort(position);
    lineNumber.putShort(paramInt);
  }
  
  public void visitMaxs(int paramInt1, int paramInt2)
  {
    if (resize) {
      resizeInstructions();
    }
    Handler localHandler;
    Object localObject1;
    Object localObject2;
    Object localObject4;
    Object localObject6;
    if (compute == 0)
    {
      for (localHandler = firstHandler; localHandler != null; localHandler = next)
      {
        localObject1 = start.getFirst();
        localObject2 = handler.getFirst();
        Label localLabel1 = end.getFirst();
        localObject4 = desc == null ? "java/lang/Throwable" : desc;
        int m = 0x1700000 | cw.addType((String)localObject4);
        status |= 0x10;
        while (localObject1 != localLabel1)
        {
          Edge localEdge1 = new Edge();
          info = m;
          successor = ((Label)localObject2);
          next = successors;
          successors = localEdge1;
          localObject1 = successor;
        }
      }
      localObject1 = labels.frame;
      localObject2 = Type.getArgumentTypes(descriptor);
      ((Frame)localObject1).initInputFrame(cw, access, (Type[])localObject2, maxLocals);
      visitFrame((Frame)localObject1);
      int j = 0;
      localObject4 = labels;
      int i4;
      while (localObject4 != null)
      {
        localObject5 = localObject4;
        localObject4 = next;
        next = null;
        localObject1 = frame;
        if ((status & 0x10) != 0) {
          status |= 0x20;
        }
        status |= 0x40;
        int i1 = inputStack.length + outputStackMax;
        if (i1 > j) {
          j = i1;
        }
        for (Edge localEdge2 = successors; localEdge2 != null; localEdge2 = next)
        {
          Label localLabel2 = successor.getFirst();
          i4 = ((Frame)localObject1).merge(cw, frame, info);
          if ((i4 != 0) && (next == null))
          {
            next = ((Label)localObject4);
            localObject4 = localLabel2;
          }
        }
      }
      for (Object localObject5 = labels; localObject5 != null; localObject5 = successor)
      {
        localObject1 = frame;
        if ((status & 0x20) != 0) {
          visitFrame((Frame)localObject1);
        }
        if ((status & 0x40) == 0)
        {
          localObject6 = successor;
          int i2 = position;
          int i3 = (localObject6 == null ? code.length : position) - 1;
          if (i3 >= i2)
          {
            j = Math.max(j, 1);
            for (i4 = i2; i4 < i3; i4++) {
              code.data[i4] = 0;
            }
            code.data[i3] = -65;
            int i5 = startFrame(i2, 0, 1);
            frame[i5] = (0x1700000 | cw.addType("java/lang/Throwable"));
            endFrame();
            firstHandler = Handler.remove(firstHandler, (Label)localObject5, (Label)localObject6);
          }
        }
      }
      localHandler = firstHandler;
      handlerCount = 0;
      while (localHandler != null)
      {
        handlerCount += 1;
        localHandler = next;
      }
      maxStack = j;
    }
    else if (compute == 1)
    {
      Object localObject3;
      for (localHandler = firstHandler; localHandler != null; localHandler = next)
      {
        localObject1 = start;
        localObject2 = handler;
        localObject3 = end;
        while (localObject1 != localObject3)
        {
          localObject4 = new Edge();
          info = Integer.MAX_VALUE;
          successor = ((Label)localObject2);
          if ((status & 0x80) == 0)
          {
            next = successors;
            successors = ((Edge)localObject4);
          }
          else
          {
            next = successors.next.next;
            successors.next.next = ((Edge)localObject4);
          }
          localObject1 = successor;
        }
      }
      if (subroutines > 0)
      {
        i = 0;
        labels.visitSubroutine(null, 1L, subroutines);
        for (localObject2 = labels; localObject2 != null; localObject2 = successor) {
          if ((status & 0x80) != 0)
          {
            localObject3 = successors.next.successor;
            if ((status & 0x400) == 0)
            {
              i++;
              ((Label)localObject3).visitSubroutine(null, i / 32L << 32 | 1L << i % 32, subroutines);
            }
          }
        }
        for (localObject2 = labels; localObject2 != null; localObject2 = successor) {
          if ((status & 0x80) != 0)
          {
            for (localObject3 = labels; localObject3 != null; localObject3 = successor) {
              status &= 0xF7FF;
            }
            localObject4 = successors.next.successor;
            ((Label)localObject4).visitSubroutine((Label)localObject2, 0L, subroutines);
          }
        }
      }
      int i = 0;
      localObject2 = labels;
      while (localObject2 != null)
      {
        localObject3 = localObject2;
        localObject2 = next;
        int k = inputStackTop;
        int n = k + outputStackMax;
        if (n > i) {
          i = n;
        }
        localObject6 = successors;
        if ((status & 0x80) != 0) {}
        for (localObject6 = next; localObject6 != null; localObject6 = next)
        {
          localObject3 = successor;
          if ((status & 0x8) == 0)
          {
            inputStackTop = (info == Integer.MAX_VALUE ? 1 : k + info);
            status |= 0x8;
            next = ((Label)localObject2);
            localObject2 = localObject3;
          }
        }
      }
      maxStack = Math.max(paramInt1, i);
    }
    else
    {
      maxStack = paramInt1;
      maxLocals = paramInt2;
    }
  }
  
  public void visitEnd() {}
  
  private void addSuccessor(int paramInt, Label paramLabel)
  {
    Edge localEdge = new Edge();
    info = paramInt;
    successor = paramLabel;
    next = currentBlock.successors;
    currentBlock.successors = localEdge;
  }
  
  private void noSuccessor()
  {
    if (compute == 0)
    {
      Label localLabel = new Label();
      frame = new Frame();
      frame.owner = localLabel;
      localLabel.resolve(this, code.length, code.data);
      previousBlock.successor = localLabel;
      previousBlock = localLabel;
    }
    else
    {
      currentBlock.outputStackMax = maxStackSize;
    }
    currentBlock = null;
  }
  
  private void visitFrame(Frame paramFrame)
  {
    int k = 0;
    int m = 0;
    int n = 0;
    int[] arrayOfInt1 = inputLocals;
    int[] arrayOfInt2 = inputStack;
    int j;
    for (int i = 0; i < arrayOfInt1.length; i++)
    {
      j = arrayOfInt1[i];
      if (j == 16777216)
      {
        k++;
      }
      else
      {
        m += k + 1;
        k = 0;
      }
      if ((j == 16777220) || (j == 16777219)) {
        i++;
      }
    }
    for (i = 0; i < arrayOfInt2.length; i++)
    {
      j = arrayOfInt2[i];
      n++;
      if ((j == 16777220) || (j == 16777219)) {
        i++;
      }
    }
    int i1 = startFrame(owner.position, m, n);
    i = 0;
    while (m > 0)
    {
      j = arrayOfInt1[i];
      frame[(i1++)] = j;
      if ((j == 16777220) || (j == 16777219)) {
        i++;
      }
      i++;
      m--;
    }
    for (i = 0; i < arrayOfInt2.length; i++)
    {
      j = arrayOfInt2[i];
      frame[(i1++)] = j;
      if ((j == 16777220) || (j == 16777219)) {
        i++;
      }
    }
    endFrame();
  }
  
  private void visitImplicitFirstFrame()
  {
    int i = startFrame(0, descriptor.length() + 1, 0);
    if ((access & 0x8) == 0) {
      if ((access & 0x80000) == 0) {
        frame[(i++)] = (0x1700000 | cw.addType(cw.thisName));
      } else {
        frame[(i++)] = 6;
      }
    }
    int j = 1;
    for (;;)
    {
      int k = j;
      switch (descriptor.charAt(j++))
      {
      case 'B': 
      case 'C': 
      case 'I': 
      case 'S': 
      case 'Z': 
        frame[(i++)] = 1;
        break;
      case 'F': 
        frame[(i++)] = 2;
        break;
      case 'J': 
        frame[(i++)] = 4;
        break;
      case 'D': 
        frame[(i++)] = 3;
        break;
      case '[': 
        while (descriptor.charAt(j) == '[') {
          j++;
        }
        if (descriptor.charAt(j) == 'L')
        {
          j++;
          while (descriptor.charAt(j) != ';') {
            j++;
          }
        }
        frame[(i++)] = (0x1700000 | cw.addType(descriptor.substring(k, ++j)));
        break;
      case 'L': 
        while (descriptor.charAt(j) != ';') {
          j++;
        }
        frame[(i++)] = (0x1700000 | cw.addType(descriptor.substring(k + 1, j++)));
        break;
      case 'E': 
      case 'G': 
      case 'H': 
      case 'K': 
      case 'M': 
      case 'N': 
      case 'O': 
      case 'P': 
      case 'Q': 
      case 'R': 
      case 'T': 
      case 'U': 
      case 'V': 
      case 'W': 
      case 'X': 
      case 'Y': 
      default: 
        break label409;
      }
    }
    label409:
    frame[1] = (i - 3);
    endFrame();
  }
  
  private int startFrame(int paramInt1, int paramInt2, int paramInt3)
  {
    int i = 3 + paramInt2 + paramInt3;
    if ((frame == null) || (frame.length < i)) {
      frame = new int[i];
    }
    frame[0] = paramInt1;
    frame[1] = paramInt2;
    frame[2] = paramInt3;
    return 3;
  }
  
  private void endFrame()
  {
    if (previousFrame != null)
    {
      if (stackMap == null) {
        stackMap = new ByteVector();
      }
      writeFrame();
      frameCount += 1;
    }
    previousFrame = frame;
    frame = null;
  }
  
  private void writeFrame()
  {
    int i = frame[1];
    int j = frame[2];
    if ((cw.version & 0xFFFF) < 50)
    {
      stackMap.putShort(frame[0]).putShort(i);
      writeFrameTypes(3, 3 + i);
      stackMap.putShort(j);
      writeFrameTypes(3 + i, 3 + i + j);
      return;
    }
    int k = previousFrame[1];
    int m = 255;
    int n = 0;
    int i1;
    if (frameCount == 0) {
      i1 = frame[0];
    } else {
      i1 = frame[0] - previousFrame[0] - 1;
    }
    if (j == 0)
    {
      n = i - k;
      switch (n)
      {
      case -3: 
      case -2: 
      case -1: 
        m = 248;
        k = i;
        break;
      case 0: 
        m = i1 < 64 ? 0 : 251;
        break;
      case 1: 
      case 2: 
      case 3: 
        m = 252;
      }
    }
    else if ((i == k) && (j == 1))
    {
      m = i1 < 63 ? 64 : 247;
    }
    if (m != 255)
    {
      int i2 = 3;
      for (int i3 = 0; i3 < k; i3++)
      {
        if (frame[i2] != previousFrame[i2])
        {
          m = 255;
          break;
        }
        i2++;
      }
    }
    switch (m)
    {
    case 0: 
      stackMap.putByte(i1);
      break;
    case 64: 
      stackMap.putByte(64 + i1);
      writeFrameTypes(3 + i, 4 + i);
      break;
    case 247: 
      stackMap.putByte(247).putShort(i1);
      writeFrameTypes(3 + i, 4 + i);
      break;
    case 251: 
      stackMap.putByte(251).putShort(i1);
      break;
    case 248: 
      stackMap.putByte(251 + n).putShort(i1);
      break;
    case 252: 
      stackMap.putByte(251 + n).putShort(i1);
      writeFrameTypes(3 + k, 3 + i);
      break;
    default: 
      stackMap.putByte(255).putShort(i1).putShort(i);
      writeFrameTypes(3, 3 + i);
      stackMap.putShort(j);
      writeFrameTypes(3 + i, 3 + i + j);
    }
  }
  
  private void writeFrameTypes(int paramInt1, int paramInt2)
  {
    for (int i = paramInt1; i < paramInt2; i++)
    {
      int j = frame[i];
      int k = j & 0xF0000000;
      if (k == 0)
      {
        int m = j & 0xFFFFF;
        switch (j & 0xFF00000)
        {
        case 24117248: 
          stackMap.putByte(7).putShort(cw.newClass(cw.typeTable[m].strVal1));
          break;
        case 25165824: 
          stackMap.putByte(8).putShort(cw.typeTable[m].intVal);
          break;
        default: 
          stackMap.putByte(m);
        }
      }
      else
      {
        StringBuilder localStringBuilder = new StringBuilder();
        k >>= 28;
        while (k-- > 0) {
          localStringBuilder.append('[');
        }
        if ((j & 0xFF00000) == 24117248)
        {
          localStringBuilder.append('L');
          localStringBuilder.append(cw.typeTable[(j & 0xFFFFF)].strVal1);
          localStringBuilder.append(';');
        }
        else
        {
          switch (j & 0xF)
          {
          case 1: 
            localStringBuilder.append('I');
            break;
          case 2: 
            localStringBuilder.append('F');
            break;
          case 3: 
            localStringBuilder.append('D');
            break;
          case 9: 
            localStringBuilder.append('Z');
            break;
          case 10: 
            localStringBuilder.append('B');
            break;
          case 11: 
            localStringBuilder.append('C');
            break;
          case 12: 
            localStringBuilder.append('S');
            break;
          case 4: 
          case 5: 
          case 6: 
          case 7: 
          case 8: 
          default: 
            localStringBuilder.append('J');
          }
        }
        stackMap.putByte(7).putShort(cw.newClass(localStringBuilder.toString()));
      }
    }
  }
  
  private void writeFrameType(Object paramObject)
  {
    if ((paramObject instanceof String)) {
      stackMap.putByte(7).putShort(cw.newClass((String)paramObject));
    } else if ((paramObject instanceof Integer)) {
      stackMap.putByte(((Integer)paramObject).intValue());
    } else {
      stackMap.putByte(8).putShort(position);
    }
  }
  
  final int getSize()
  {
    if (classReaderOffset != 0) {
      return 6 + classReaderLength;
    }
    int i = 8;
    int j;
    if (code.length > 0)
    {
      if (code.length > 65536) {
        throw new RuntimeException("Method code too large!");
      }
      cw.newUTF8("Code");
      i += 18 + code.length + 8 * handlerCount;
      if (localVar != null)
      {
        cw.newUTF8("LocalVariableTable");
        i += 8 + localVar.length;
      }
      if (localVarType != null)
      {
        cw.newUTF8("LocalVariableTypeTable");
        i += 8 + localVarType.length;
      }
      if (lineNumber != null)
      {
        cw.newUTF8("LineNumberTable");
        i += 8 + lineNumber.length;
      }
      if (stackMap != null)
      {
        j = (cw.version & 0xFFFF) >= 50 ? 1 : 0;
        cw.newUTF8(j != 0 ? "StackMapTable" : "StackMap");
        i += 8 + stackMap.length;
      }
      if (ctanns != null)
      {
        cw.newUTF8("RuntimeVisibleTypeAnnotations");
        i += 8 + ctanns.getSize();
      }
      if (ictanns != null)
      {
        cw.newUTF8("RuntimeInvisibleTypeAnnotations");
        i += 8 + ictanns.getSize();
      }
      if (cattrs != null) {
        i += cattrs.getSize(cw, code.data, code.length, maxStack, maxLocals);
      }
    }
    if (exceptionCount > 0)
    {
      cw.newUTF8("Exceptions");
      i += 8 + 2 * exceptionCount;
    }
    if (((access & 0x1000) != 0) && (((cw.version & 0xFFFF) < 49) || ((access & 0x40000) != 0)))
    {
      cw.newUTF8("Synthetic");
      i += 6;
    }
    if ((access & 0x20000) != 0)
    {
      cw.newUTF8("Deprecated");
      i += 6;
    }
    if (signature != null)
    {
      cw.newUTF8("Signature");
      cw.newUTF8(signature);
      i += 8;
    }
    if (methodParameters != null)
    {
      cw.newUTF8("MethodParameters");
      i += 7 + methodParameters.length;
    }
    if (annd != null)
    {
      cw.newUTF8("AnnotationDefault");
      i += 6 + annd.length;
    }
    if (anns != null)
    {
      cw.newUTF8("RuntimeVisibleAnnotations");
      i += 8 + anns.getSize();
    }
    if (ianns != null)
    {
      cw.newUTF8("RuntimeInvisibleAnnotations");
      i += 8 + ianns.getSize();
    }
    if (tanns != null)
    {
      cw.newUTF8("RuntimeVisibleTypeAnnotations");
      i += 8 + tanns.getSize();
    }
    if (itanns != null)
    {
      cw.newUTF8("RuntimeInvisibleTypeAnnotations");
      i += 8 + itanns.getSize();
    }
    if (panns != null)
    {
      cw.newUTF8("RuntimeVisibleParameterAnnotations");
      i += 7 + 2 * (panns.length - synthetics);
      for (j = panns.length - 1; j >= synthetics; j--) {
        i += (panns[j] == null ? 0 : panns[j].getSize());
      }
    }
    if (ipanns != null)
    {
      cw.newUTF8("RuntimeInvisibleParameterAnnotations");
      i += 7 + 2 * (ipanns.length - synthetics);
      for (j = ipanns.length - 1; j >= synthetics; j--) {
        i += (ipanns[j] == null ? 0 : ipanns[j].getSize());
      }
    }
    if (attrs != null) {
      i += attrs.getSize(cw, null, 0, -1, -1);
    }
    return i;
  }
  
  final void put(ByteVector paramByteVector)
  {
    int i = 0xE0000 | (access & 0x40000) / 64;
    paramByteVector.putShort(access & (i ^ 0xFFFFFFFF)).putShort(name).putShort(desc);
    if (classReaderOffset != 0)
    {
      paramByteVector.putByteArray(cw.cr.b, classReaderOffset, classReaderLength);
      return;
    }
    int j = 0;
    if (code.length > 0) {
      j++;
    }
    if (exceptionCount > 0) {
      j++;
    }
    if (((access & 0x1000) != 0) && (((cw.version & 0xFFFF) < 49) || ((access & 0x40000) != 0))) {
      j++;
    }
    if ((access & 0x20000) != 0) {
      j++;
    }
    if (signature != null) {
      j++;
    }
    if (methodParameters != null) {
      j++;
    }
    if (annd != null) {
      j++;
    }
    if (anns != null) {
      j++;
    }
    if (ianns != null) {
      j++;
    }
    if (tanns != null) {
      j++;
    }
    if (itanns != null) {
      j++;
    }
    if (panns != null) {
      j++;
    }
    if (ipanns != null) {
      j++;
    }
    if (attrs != null) {
      j += attrs.getCount();
    }
    paramByteVector.putShort(j);
    int k;
    if (code.length > 0)
    {
      k = 12 + code.length + 8 * handlerCount;
      if (localVar != null) {
        k += 8 + localVar.length;
      }
      if (localVarType != null) {
        k += 8 + localVarType.length;
      }
      if (lineNumber != null) {
        k += 8 + lineNumber.length;
      }
      if (stackMap != null) {
        k += 8 + stackMap.length;
      }
      if (ctanns != null) {
        k += 8 + ctanns.getSize();
      }
      if (ictanns != null) {
        k += 8 + ictanns.getSize();
      }
      if (cattrs != null) {
        k += cattrs.getSize(cw, code.data, code.length, maxStack, maxLocals);
      }
      paramByteVector.putShort(cw.newUTF8("Code")).putInt(k);
      paramByteVector.putShort(maxStack).putShort(maxLocals);
      paramByteVector.putInt(code.length).putByteArray(code.data, 0, code.length);
      paramByteVector.putShort(handlerCount);
      if (handlerCount > 0) {
        for (Handler localHandler = firstHandler; localHandler != null; localHandler = next) {
          paramByteVector.putShort(start.position).putShort(end.position).putShort(handler.position).putShort(type);
        }
      }
      j = 0;
      if (localVar != null) {
        j++;
      }
      if (localVarType != null) {
        j++;
      }
      if (lineNumber != null) {
        j++;
      }
      if (stackMap != null) {
        j++;
      }
      if (ctanns != null) {
        j++;
      }
      if (ictanns != null) {
        j++;
      }
      if (cattrs != null) {
        j += cattrs.getCount();
      }
      paramByteVector.putShort(j);
      if (localVar != null)
      {
        paramByteVector.putShort(cw.newUTF8("LocalVariableTable"));
        paramByteVector.putInt(localVar.length + 2).putShort(localVarCount);
        paramByteVector.putByteArray(localVar.data, 0, localVar.length);
      }
      if (localVarType != null)
      {
        paramByteVector.putShort(cw.newUTF8("LocalVariableTypeTable"));
        paramByteVector.putInt(localVarType.length + 2).putShort(localVarTypeCount);
        paramByteVector.putByteArray(localVarType.data, 0, localVarType.length);
      }
      if (lineNumber != null)
      {
        paramByteVector.putShort(cw.newUTF8("LineNumberTable"));
        paramByteVector.putInt(lineNumber.length + 2).putShort(lineNumberCount);
        paramByteVector.putByteArray(lineNumber.data, 0, lineNumber.length);
      }
      if (stackMap != null)
      {
        int m = (cw.version & 0xFFFF) >= 50 ? 1 : 0;
        paramByteVector.putShort(cw.newUTF8(m != 0 ? "StackMapTable" : "StackMap"));
        paramByteVector.putInt(stackMap.length + 2).putShort(frameCount);
        paramByteVector.putByteArray(stackMap.data, 0, stackMap.length);
      }
      if (ctanns != null)
      {
        paramByteVector.putShort(cw.newUTF8("RuntimeVisibleTypeAnnotations"));
        ctanns.put(paramByteVector);
      }
      if (ictanns != null)
      {
        paramByteVector.putShort(cw.newUTF8("RuntimeInvisibleTypeAnnotations"));
        ictanns.put(paramByteVector);
      }
      if (cattrs != null) {
        cattrs.put(cw, code.data, code.length, maxLocals, maxStack, paramByteVector);
      }
    }
    if (exceptionCount > 0)
    {
      paramByteVector.putShort(cw.newUTF8("Exceptions")).putInt(2 * exceptionCount + 2);
      paramByteVector.putShort(exceptionCount);
      for (k = 0; k < exceptionCount; k++) {
        paramByteVector.putShort(exceptions[k]);
      }
    }
    if (((access & 0x1000) != 0) && (((cw.version & 0xFFFF) < 49) || ((access & 0x40000) != 0))) {
      paramByteVector.putShort(cw.newUTF8("Synthetic")).putInt(0);
    }
    if ((access & 0x20000) != 0) {
      paramByteVector.putShort(cw.newUTF8("Deprecated")).putInt(0);
    }
    if (signature != null) {
      paramByteVector.putShort(cw.newUTF8("Signature")).putInt(2).putShort(cw.newUTF8(signature));
    }
    if (methodParameters != null)
    {
      paramByteVector.putShort(cw.newUTF8("MethodParameters"));
      paramByteVector.putInt(methodParameters.length + 1).putByte(methodParametersCount);
      paramByteVector.putByteArray(methodParameters.data, 0, methodParameters.length);
    }
    if (annd != null)
    {
      paramByteVector.putShort(cw.newUTF8("AnnotationDefault"));
      paramByteVector.putInt(annd.length);
      paramByteVector.putByteArray(annd.data, 0, annd.length);
    }
    if (anns != null)
    {
      paramByteVector.putShort(cw.newUTF8("RuntimeVisibleAnnotations"));
      anns.put(paramByteVector);
    }
    if (ianns != null)
    {
      paramByteVector.putShort(cw.newUTF8("RuntimeInvisibleAnnotations"));
      ianns.put(paramByteVector);
    }
    if (tanns != null)
    {
      paramByteVector.putShort(cw.newUTF8("RuntimeVisibleTypeAnnotations"));
      tanns.put(paramByteVector);
    }
    if (itanns != null)
    {
      paramByteVector.putShort(cw.newUTF8("RuntimeInvisibleTypeAnnotations"));
      itanns.put(paramByteVector);
    }
    if (panns != null)
    {
      paramByteVector.putShort(cw.newUTF8("RuntimeVisibleParameterAnnotations"));
      AnnotationWriter.put(panns, synthetics, paramByteVector);
    }
    if (ipanns != null)
    {
      paramByteVector.putShort(cw.newUTF8("RuntimeInvisibleParameterAnnotations"));
      AnnotationWriter.put(ipanns, synthetics, paramByteVector);
    }
    if (attrs != null) {
      attrs.put(cw, null, 0, -1, -1, paramByteVector);
    }
  }
  
  private void resizeInstructions()
  {
    byte[] arrayOfByte = code.data;
    Object localObject1 = new int[0];
    Object localObject2 = new int[0];
    boolean[] arrayOfBoolean = new boolean[code.length];
    int i2 = 3;
    int i4;
    int k;
    int i1;
    Object localObject5;
    do
    {
      if (i2 == 3) {
        i2 = 2;
      }
      i = 0;
      while (i < arrayOfByte.length)
      {
        int i3 = arrayOfByte[i] & 0xFF;
        i4 = 0;
        switch (ClassWriter.TYPE[i3])
        {
        case 0: 
        case 4: 
          i++;
          break;
        case 9: 
          if (i3 > 201)
          {
            i3 = i3 < 218 ? i3 - 49 : i3 - 20;
            k = i + readUnsignedShort(arrayOfByte, i + 1);
          }
          else
          {
            k = i + readShort(arrayOfByte, i + 1);
          }
          i1 = getNewOffset((int[])localObject1, (int[])localObject2, i, k);
          if (((i1 < 32768) || (i1 > 32767)) && (arrayOfBoolean[i] == 0))
          {
            if ((i3 == 167) || (i3 == 168)) {
              i4 = 2;
            } else {
              i4 = 5;
            }
            arrayOfBoolean[i] = true;
          }
          i += 3;
          break;
        case 10: 
          i += 5;
          break;
        case 14: 
          if (i2 == 1)
          {
            i1 = getNewOffset((int[])localObject1, (int[])localObject2, 0, i);
            i4 = -(i1 & 0x3);
          }
          else if (arrayOfBoolean[i] == 0)
          {
            i4 = i & 0x3;
            arrayOfBoolean[i] = true;
          }
          i = i + 4 - (i & 0x3);
          i += 4 * (readInt(arrayOfByte, i + 8) - readInt(arrayOfByte, i + 4) + 1) + 12;
          break;
        case 15: 
          if (i2 == 1)
          {
            i1 = getNewOffset((int[])localObject1, (int[])localObject2, 0, i);
            i4 = -(i1 & 0x3);
          }
          else if (arrayOfBoolean[i] == 0)
          {
            i4 = i & 0x3;
            arrayOfBoolean[i] = true;
          }
          i = i + 4 - (i & 0x3);
          i += 8 * readInt(arrayOfByte, i + 4) + 8;
          break;
        case 17: 
          i3 = arrayOfByte[(i + 1)] & 0xFF;
          if (i3 == 132) {
            i += 6;
          } else {
            i += 4;
          }
          break;
        case 1: 
        case 3: 
        case 11: 
          i += 2;
          break;
        case 2: 
        case 5: 
        case 6: 
        case 12: 
        case 13: 
          i += 3;
          break;
        case 7: 
        case 8: 
          i += 5;
          break;
        case 16: 
        default: 
          i += 4;
        }
        if (i4 != 0)
        {
          localObject4 = new int[localObject1.length + 1];
          localObject5 = new int[localObject2.length + 1];
          System.arraycopy(localObject1, 0, localObject4, 0, localObject1.length);
          System.arraycopy(localObject2, 0, localObject5, 0, localObject2.length);
          localObject4[localObject1.length] = i;
          localObject5[localObject2.length] = i4;
          localObject1 = localObject4;
          localObject2 = localObject5;
          if (i4 > 0) {
            i2 = 3;
          }
        }
      }
      if (i2 < 3) {
        i2--;
      }
    } while (i2 != 0);
    ByteVector localByteVector = new ByteVector(code.length);
    int i = 0;
    while (i < code.length)
    {
      i4 = arrayOfByte[i] & 0xFF;
      int j;
      int n;
      switch (ClassWriter.TYPE[i4])
      {
      case 0: 
      case 4: 
        localByteVector.putByte(i4);
        i++;
        break;
      case 9: 
        if (i4 > 201)
        {
          i4 = i4 < 218 ? i4 - 49 : i4 - 20;
          k = i + readUnsignedShort(arrayOfByte, i + 1);
        }
        else
        {
          k = i + readShort(arrayOfByte, i + 1);
        }
        i1 = getNewOffset((int[])localObject1, (int[])localObject2, i, k);
        if (arrayOfBoolean[i] != 0)
        {
          if (i4 == 167)
          {
            localByteVector.putByte(200);
          }
          else if (i4 == 168)
          {
            localByteVector.putByte(201);
          }
          else
          {
            localByteVector.putByte(i4 <= 166 ? (i4 + 1 ^ 0x1) - 1 : i4 ^ 0x1);
            localByteVector.putShort(8);
            localByteVector.putByte(200);
            i1 -= 3;
          }
          localByteVector.putInt(i1);
        }
        else
        {
          localByteVector.putByte(i4);
          localByteVector.putShort(i1);
        }
        i += 3;
        break;
      case 10: 
        k = i + readInt(arrayOfByte, i + 1);
        i1 = getNewOffset((int[])localObject1, (int[])localObject2, i, k);
        localByteVector.putByte(i4);
        localByteVector.putInt(i1);
        i += 5;
        break;
      case 14: 
        j = i;
        i = i + 4 - (j & 0x3);
        localByteVector.putByte(170);
        localByteVector.putByteArray(null, 0, (4 - length % 4) % 4);
        k = j + readInt(arrayOfByte, i);
        i += 4;
        i1 = getNewOffset((int[])localObject1, (int[])localObject2, j, k);
        localByteVector.putInt(i1);
        n = readInt(arrayOfByte, i);
        i += 4;
        localByteVector.putInt(n);
        n = readInt(arrayOfByte, i) - n + 1;
        i += 4;
        localByteVector.putInt(readInt(arrayOfByte, i - 4));
      case 15: 
      case 17: 
      case 1: 
      case 3: 
      case 11: 
      case 2: 
      case 5: 
      case 6: 
      case 12: 
      case 13: 
      case 7: 
      case 8: 
      case 16: 
      default: 
        while (n > 0)
        {
          k = j + readInt(arrayOfByte, i);
          i += 4;
          i1 = getNewOffset((int[])localObject1, (int[])localObject2, j, k);
          localByteVector.putInt(i1);
          n--;
          continue;
          j = i;
          i = i + 4 - (j & 0x3);
          localByteVector.putByte(171);
          localByteVector.putByteArray(null, 0, (4 - length % 4) % 4);
          k = j + readInt(arrayOfByte, i);
          i += 4;
          i1 = getNewOffset((int[])localObject1, (int[])localObject2, j, k);
          localByteVector.putInt(i1);
          n = readInt(arrayOfByte, i);
          i += 4;
          localByteVector.putInt(n);
          while (n > 0)
          {
            localByteVector.putInt(readInt(arrayOfByte, i));
            i += 4;
            k = j + readInt(arrayOfByte, i);
            i += 4;
            i1 = getNewOffset((int[])localObject1, (int[])localObject2, j, k);
            localByteVector.putInt(i1);
            n--;
            continue;
            i4 = arrayOfByte[(i + 1)] & 0xFF;
            if (i4 == 132)
            {
              localByteVector.putByteArray(arrayOfByte, i, 6);
              i += 6;
            }
            else
            {
              localByteVector.putByteArray(arrayOfByte, i, 4);
              i += 4;
              break;
              localByteVector.putByteArray(arrayOfByte, i, 2);
              i += 2;
              break;
              localByteVector.putByteArray(arrayOfByte, i, 3);
              i += 3;
              break;
              localByteVector.putByteArray(arrayOfByte, i, 5);
              i += 5;
              break;
              localByteVector.putByteArray(arrayOfByte, i, 4);
              i += 4;
            }
          }
        }
      }
    }
    if (compute == 0)
    {
      for (localObject3 = labels; localObject3 != null; localObject3 = successor)
      {
        i = position - 3;
        if ((i >= 0) && (arrayOfBoolean[i] != 0)) {
          status |= 0x10;
        }
        getNewOffset((int[])localObject1, (int[])localObject2, (Label)localObject3);
      }
      for (m = 0; m < cw.typeTable.length; m++)
      {
        localObject4 = cw.typeTable[m];
        if ((localObject4 != null) && (type == 31)) {
          intVal = getNewOffset((int[])localObject1, (int[])localObject2, 0, intVal);
        }
      }
    }
    else if (frameCount > 0)
    {
      cw.invalidFrames = true;
    }
    for (Object localObject3 = firstHandler; localObject3 != null; localObject3 = next)
    {
      getNewOffset((int[])localObject1, (int[])localObject2, start);
      getNewOffset((int[])localObject1, (int[])localObject2, end);
      getNewOffset((int[])localObject1, (int[])localObject2, handler);
    }
    for (int m = 0; m < 2; m++)
    {
      localObject4 = m == 0 ? localVar : localVarType;
      if (localObject4 != null)
      {
        arrayOfByte = data;
        for (i = 0; i < length; i += 10)
        {
          k = readUnsignedShort(arrayOfByte, i);
          i1 = getNewOffset((int[])localObject1, (int[])localObject2, 0, k);
          writeShort(arrayOfByte, i, i1);
          k += readUnsignedShort(arrayOfByte, i + 2);
          i1 = getNewOffset((int[])localObject1, (int[])localObject2, 0, k) - i1;
          writeShort(arrayOfByte, i + 2, i1);
        }
      }
    }
    if (lineNumber != null)
    {
      arrayOfByte = lineNumber.data;
      for (i = 0; i < lineNumber.length; i += 4) {
        writeShort(arrayOfByte, i, getNewOffset((int[])localObject1, (int[])localObject2, 0, readUnsignedShort(arrayOfByte, i)));
      }
    }
    for (Object localObject4 = cattrs; localObject4 != null; localObject4 = next)
    {
      localObject5 = ((Attribute)localObject4).getLabels();
      if (localObject5 != null) {
        for (m = localObject5.length - 1; m >= 0; m--) {
          getNewOffset((int[])localObject1, (int[])localObject2, localObject5[m]);
        }
      }
    }
    code = localByteVector;
  }
  
  static int readUnsignedShort(byte[] paramArrayOfByte, int paramInt)
  {
    return (paramArrayOfByte[paramInt] & 0xFF) << 8 | paramArrayOfByte[(paramInt + 1)] & 0xFF;
  }
  
  static short readShort(byte[] paramArrayOfByte, int paramInt)
  {
    return (short)((paramArrayOfByte[paramInt] & 0xFF) << 8 | paramArrayOfByte[(paramInt + 1)] & 0xFF);
  }
  
  static int readInt(byte[] paramArrayOfByte, int paramInt)
  {
    return (paramArrayOfByte[paramInt] & 0xFF) << 24 | (paramArrayOfByte[(paramInt + 1)] & 0xFF) << 16 | (paramArrayOfByte[(paramInt + 2)] & 0xFF) << 8 | paramArrayOfByte[(paramInt + 3)] & 0xFF;
  }
  
  static void writeShort(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    paramArrayOfByte[paramInt1] = ((byte)(paramInt2 >>> 8));
    paramArrayOfByte[(paramInt1 + 1)] = ((byte)paramInt2);
  }
  
  static int getNewOffset(int[] paramArrayOfInt1, int[] paramArrayOfInt2, int paramInt1, int paramInt2)
  {
    int i = paramInt2 - paramInt1;
    for (int j = 0; j < paramArrayOfInt1.length; j++) {
      if ((paramInt1 < paramArrayOfInt1[j]) && (paramArrayOfInt1[j] <= paramInt2)) {
        i += paramArrayOfInt2[j];
      } else if ((paramInt2 < paramArrayOfInt1[j]) && (paramArrayOfInt1[j] <= paramInt1)) {
        i -= paramArrayOfInt2[j];
      }
    }
    return i;
  }
  
  static void getNewOffset(int[] paramArrayOfInt1, int[] paramArrayOfInt2, Label paramLabel)
  {
    if ((status & 0x4) == 0)
    {
      position = getNewOffset(paramArrayOfInt1, paramArrayOfInt2, 0, position);
      status |= 0x4;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\jdk\internal\org\objectweb\asm\MethodWriter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */