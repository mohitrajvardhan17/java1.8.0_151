package com.sun.java.util.jar.pack;

import java.io.IOException;
import java.util.Arrays;

class Instruction
{
  protected byte[] bytes;
  protected int pc;
  protected int bc;
  protected int w;
  protected int length;
  protected boolean special;
  private static final byte[][] BC_LENGTH;
  private static final byte[][] BC_INDEX;
  private static final byte[][] BC_TAG;
  private static final byte[][] BC_BRANCH;
  private static final byte[][] BC_SLOT;
  private static final byte[][] BC_CON;
  private static final String[] BC_NAME;
  private static final String[][] BC_FORMAT;
  private static int BW = 4;
  
  protected Instruction(byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    reset(paramArrayOfByte, paramInt1, paramInt2, paramInt3, paramInt4);
  }
  
  private void reset(byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    bytes = paramArrayOfByte;
    pc = paramInt1;
    bc = paramInt2;
    w = paramInt3;
    length = paramInt4;
  }
  
  public int getBC()
  {
    return bc;
  }
  
  public boolean isWide()
  {
    return w != 0;
  }
  
  public byte[] getBytes()
  {
    return bytes;
  }
  
  public int getPC()
  {
    return pc;
  }
  
  public int getLength()
  {
    return length;
  }
  
  public int getNextPC()
  {
    return pc + length;
  }
  
  public Instruction next()
  {
    int i = pc + length;
    if (i == bytes.length) {
      return null;
    }
    return at(bytes, i, this);
  }
  
  public boolean isNonstandard()
  {
    return isNonstandard(bc);
  }
  
  public void setNonstandardLength(int paramInt)
  {
    assert (isNonstandard());
    length = paramInt;
  }
  
  public Instruction forceNextPC(int paramInt)
  {
    int i = paramInt - pc;
    return new Instruction(bytes, pc, -1, -1, i);
  }
  
  public static Instruction at(byte[] paramArrayOfByte, int paramInt)
  {
    return at(paramArrayOfByte, paramInt, null);
  }
  
  public static Instruction at(byte[] paramArrayOfByte, int paramInt, Instruction paramInstruction)
  {
    int i = getByte(paramArrayOfByte, paramInt);
    int j = -1;
    int k = 0;
    int m = BC_LENGTH[k][i];
    if (m == 0) {
      switch (i)
      {
      case 196: 
        i = getByte(paramArrayOfByte, paramInt + 1);
        k = 1;
        m = BC_LENGTH[k][i];
        if (m == 0) {
          m = 1;
        }
        break;
      case 170: 
        return new TableSwitch(paramArrayOfByte, paramInt);
      case 171: 
        return new LookupSwitch(paramArrayOfByte, paramInt);
      default: 
        m = 1;
      }
    }
    assert (m > 0);
    assert (paramInt + m <= paramArrayOfByte.length);
    if ((paramInstruction != null) && (!special))
    {
      paramInstruction.reset(paramArrayOfByte, paramInt, i, k, m);
      return paramInstruction;
    }
    return new Instruction(paramArrayOfByte, paramInt, i, k, m);
  }
  
  public byte getCPTag()
  {
    return BC_TAG[w][bc];
  }
  
  public int getCPIndex()
  {
    int i = BC_INDEX[w][bc];
    if (i == 0) {
      return -1;
    }
    assert (w == 0);
    if (length == 2) {
      return getByte(bytes, pc + i);
    }
    return getShort(bytes, pc + i);
  }
  
  public void setCPIndex(int paramInt)
  {
    int i = BC_INDEX[w][bc];
    assert (i != 0);
    if (length == 2) {
      setByte(bytes, pc + i, paramInt);
    } else {
      setShort(bytes, pc + i, paramInt);
    }
    assert (getCPIndex() == paramInt);
  }
  
  public ConstantPool.Entry getCPRef(ConstantPool.Entry[] paramArrayOfEntry)
  {
    int i = getCPIndex();
    return i < 0 ? null : paramArrayOfEntry[i];
  }
  
  public int getLocalSlot()
  {
    int i = BC_SLOT[w][bc];
    if (i == 0) {
      return -1;
    }
    if (w == 0) {
      return getByte(bytes, pc + i);
    }
    return getShort(bytes, pc + i);
  }
  
  public int getBranchLabel()
  {
    int i = BC_BRANCH[w][bc];
    if (i == 0) {
      return -1;
    }
    assert (w == 0);
    assert ((length == 3) || (length == 5));
    int j;
    if (length == 3) {
      j = (short)getShort(bytes, pc + i);
    } else {
      j = getInt(bytes, pc + i);
    }
    assert (j + pc >= 0);
    assert (j + pc <= bytes.length);
    return j + pc;
  }
  
  public void setBranchLabel(int paramInt)
  {
    int i = BC_BRANCH[w][bc];
    assert (i != 0);
    if (length == 3) {
      setShort(bytes, pc + i, paramInt - pc);
    } else {
      setInt(bytes, pc + i, paramInt - pc);
    }
    assert (paramInt == getBranchLabel());
  }
  
  public int getConstant()
  {
    int i = BC_CON[w][bc];
    if (i == 0) {
      return 0;
    }
    switch (length - i)
    {
    case 1: 
      return (byte)getByte(bytes, pc + i);
    case 2: 
      return (short)getShort(bytes, pc + i);
    }
    if (!$assertionsDisabled) {
      throw new AssertionError();
    }
    return 0;
  }
  
  public void setConstant(int paramInt)
  {
    int i = BC_CON[w][bc];
    assert (i != 0);
    switch (length - i)
    {
    case 1: 
      setByte(bytes, pc + i, paramInt);
      break;
    case 2: 
      setShort(bytes, pc + i, paramInt);
    }
    assert (paramInt == getConstant());
  }
  
  public boolean equals(Object paramObject)
  {
    return (paramObject != null) && (paramObject.getClass() == Instruction.class) && (equals((Instruction)paramObject));
  }
  
  public int hashCode()
  {
    int i = 3;
    i = 11 * i + Arrays.hashCode(bytes);
    i = 11 * i + pc;
    i = 11 * i + bc;
    i = 11 * i + w;
    i = 11 * i + length;
    return i;
  }
  
  public boolean equals(Instruction paramInstruction)
  {
    if (pc != pc) {
      return false;
    }
    if (bc != bc) {
      return false;
    }
    if (w != w) {
      return false;
    }
    if (length != length) {
      return false;
    }
    for (int i = 1; i < length; i++) {
      if (bytes[(pc + i)] != bytes[(pc + i)]) {
        return false;
      }
    }
    return true;
  }
  
  static String labstr(int paramInt)
  {
    if ((paramInt >= 0) && (paramInt < 100000)) {
      return (100000 + paramInt + "").substring(1);
    }
    return paramInt + "";
  }
  
  public String toString()
  {
    return toString(null);
  }
  
  public String toString(ConstantPool.Entry[] paramArrayOfEntry)
  {
    String str1 = labstr(pc) + ": ";
    if (bc >= 202)
    {
      str1 = str1 + Integer.toHexString(bc);
      return str1;
    }
    if (w == 1) {
      str1 = str1 + "wide ";
    }
    String str2 = bc < BC_NAME.length ? BC_NAME[bc] : null;
    if (str2 == null) {
      return str1 + "opcode#" + bc;
    }
    str1 = str1 + str2;
    int i = getCPTag();
    if (i != 0) {
      str1 = str1 + " " + ConstantPool.tagName(i) + ":";
    }
    int j = getCPIndex();
    if (j >= 0) {
      str1 = str1 + (paramArrayOfEntry == null ? "" + j : new StringBuilder().append("=").append(paramArrayOfEntry[j].stringValue()).toString());
    }
    int k = getLocalSlot();
    if (k >= 0) {
      str1 = str1 + " Local:" + k;
    }
    int m = getBranchLabel();
    if (m >= 0) {
      str1 = str1 + " To:" + labstr(m);
    }
    int n = getConstant();
    if (n != 0) {
      str1 = str1 + " Con:" + n;
    }
    return str1;
  }
  
  public int getIntAt(int paramInt)
  {
    return getInt(bytes, pc + paramInt);
  }
  
  public int getShortAt(int paramInt)
  {
    return getShort(bytes, pc + paramInt);
  }
  
  public int getByteAt(int paramInt)
  {
    return getByte(bytes, pc + paramInt);
  }
  
  public static int getInt(byte[] paramArrayOfByte, int paramInt)
  {
    return (getShort(paramArrayOfByte, paramInt + 0) << 16) + (getShort(paramArrayOfByte, paramInt + 2) << 0);
  }
  
  public static int getShort(byte[] paramArrayOfByte, int paramInt)
  {
    return (getByte(paramArrayOfByte, paramInt + 0) << 8) + (getByte(paramArrayOfByte, paramInt + 1) << 0);
  }
  
  public static int getByte(byte[] paramArrayOfByte, int paramInt)
  {
    return paramArrayOfByte[paramInt] & 0xFF;
  }
  
  public static void setInt(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    setShort(paramArrayOfByte, paramInt1 + 0, paramInt2 >> 16);
    setShort(paramArrayOfByte, paramInt1 + 2, paramInt2 >> 0);
  }
  
  public static void setShort(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    setByte(paramArrayOfByte, paramInt1 + 0, paramInt2 >> 8);
    setByte(paramArrayOfByte, paramInt1 + 1, paramInt2 >> 0);
  }
  
  public static void setByte(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    paramArrayOfByte[paramInt1] = ((byte)paramInt2);
  }
  
  public static boolean isNonstandard(int paramInt)
  {
    return BC_LENGTH[0][paramInt] < 0;
  }
  
  public static int opLength(int paramInt)
  {
    int i = BC_LENGTH[0][paramInt];
    assert (i > 0);
    return i;
  }
  
  public static int opWideLength(int paramInt)
  {
    int i = BC_LENGTH[1][paramInt];
    assert (i > 0);
    return i;
  }
  
  public static boolean isLocalSlotOp(int paramInt)
  {
    return (paramInt < BC_SLOT[0].length) && (BC_SLOT[0][paramInt] > 0);
  }
  
  public static boolean isBranchOp(int paramInt)
  {
    return (paramInt < BC_BRANCH[0].length) && (BC_BRANCH[0][paramInt] > 0);
  }
  
  public static boolean isCPRefOp(int paramInt)
  {
    if ((paramInt < BC_INDEX[0].length) && (BC_INDEX[0][paramInt] > 0)) {
      return true;
    }
    if ((paramInt >= 233) && (paramInt < 242)) {
      return true;
    }
    return (paramInt == 242) || (paramInt == 243);
  }
  
  public static byte getCPRefOpTag(int paramInt)
  {
    if ((paramInt < BC_INDEX[0].length) && (BC_INDEX[0][paramInt] > 0)) {
      return BC_TAG[0][paramInt];
    }
    if ((paramInt >= 233) && (paramInt < 242)) {
      return 51;
    }
    if ((paramInt == 243) || (paramInt == 242)) {
      return 11;
    }
    return 0;
  }
  
  public static boolean isFieldOp(int paramInt)
  {
    return (paramInt >= 178) && (paramInt <= 181);
  }
  
  public static boolean isInvokeInitOp(int paramInt)
  {
    return (paramInt >= 230) && (paramInt < 233);
  }
  
  public static boolean isSelfLinkerOp(int paramInt)
  {
    return (paramInt >= 202) && (paramInt < 230);
  }
  
  public static String byteName(int paramInt)
  {
    String str;
    if ((paramInt < BC_NAME.length) && (BC_NAME[paramInt] != null))
    {
      str = BC_NAME[paramInt];
    }
    else
    {
      int i;
      if (isSelfLinkerOp(paramInt))
      {
        i = paramInt - 202;
        int j = i >= 14 ? 1 : 0;
        if (j != 0) {
          i -= 14;
        }
        int k = i >= 7 ? 1 : 0;
        if (k != 0) {
          i -= 7;
        }
        int m = 178 + i;
        assert ((m >= 178) && (m <= 184));
        str = BC_NAME[m];
        str = str + (j != 0 ? "_super" : "_this");
        if (k != 0) {
          str = "aload_0&" + str;
        }
        str = "*" + str;
      }
      else if (isInvokeInitOp(paramInt))
      {
        i = paramInt - 230;
        switch (i)
        {
        case 0: 
          str = "*invokespecial_init_this";
          break;
        case 1: 
          str = "*invokespecial_init_super";
          break;
        default: 
          assert (i == 2);
          str = "*invokespecial_init_new";
        }
      }
      else
      {
        switch (paramInt)
        {
        case 234: 
          str = "*ildc";
          break;
        case 235: 
          str = "*fldc";
          break;
        case 237: 
          str = "*ildc_w";
          break;
        case 238: 
          str = "*fldc_w";
          break;
        case 239: 
          str = "*dldc2_w";
          break;
        case 233: 
          str = "*cldc";
          break;
        case 236: 
          str = "*cldc_w";
          break;
        case 240: 
          str = "*qldc";
          break;
        case 241: 
          str = "*qldc_w";
          break;
        case 254: 
          str = "*byte_escape";
          break;
        case 253: 
          str = "*ref_escape";
          break;
        case 255: 
          str = "*end";
          break;
        case 242: 
        case 243: 
        case 244: 
        case 245: 
        case 246: 
        case 247: 
        case 248: 
        case 249: 
        case 250: 
        case 251: 
        case 252: 
        default: 
          str = "*bc#" + paramInt;
        }
      }
    }
    return str;
  }
  
  private static void def(String paramString, int paramInt)
  {
    def(paramString, paramInt, paramInt);
  }
  
  private static void def(String paramString, int paramInt1, int paramInt2)
  {
    String[] arrayOfString = { paramString, null };
    if (paramString.indexOf('w') > 0)
    {
      arrayOfString[1] = paramString.substring(paramString.indexOf(119));
      arrayOfString[0] = paramString.substring(0, paramString.indexOf(119));
    }
    for (int i = 0; i <= 1; i++)
    {
      paramString = arrayOfString[i];
      if (paramString != null)
      {
        int j = paramString.length();
        int k = Math.max(0, paramString.indexOf('k'));
        int m = 0;
        int n = Math.max(0, paramString.indexOf('o'));
        int i1 = Math.max(0, paramString.indexOf('l'));
        int i2 = Math.max(0, paramString.indexOf('x'));
        if ((k > 0) && (k + 1 < j))
        {
          switch (paramString.charAt(k + 1))
          {
          case 'c': 
            m = 7;
            break;
          case 'k': 
            m = 51;
            break;
          case 'f': 
            m = 9;
            break;
          case 'm': 
            m = 10;
            break;
          case 'i': 
            m = 11;
            break;
          case 'y': 
            m = 18;
          }
          if ((!$assertionsDisabled) && (m == 0)) {
            throw new AssertionError();
          }
        }
        else if ((k > 0) && (j == 2))
        {
          assert (paramInt1 == 18);
          m = 51;
        }
        for (int i3 = paramInt1; i3 <= paramInt2; i3++)
        {
          BC_FORMAT[i][i3] = paramString;
          assert (BC_LENGTH[i][i3] == -1);
          BC_LENGTH[i][i3] = ((byte)j);
          BC_INDEX[i][i3] = ((byte)k);
          BC_TAG[i][i3] = ((byte)m);
          assert ((k != 0) || (m == 0));
          BC_BRANCH[i][i3] = ((byte)n);
          BC_SLOT[i][i3] = ((byte)i1);
          assert ((n == 0) || (i1 == 0));
          assert ((n == 0) || (k == 0));
          assert ((i1 == 0) || (k == 0));
          BC_CON[i][i3] = ((byte)i2);
        }
      }
    }
  }
  
  public static void opcodeChecker(byte[] paramArrayOfByte, ConstantPool.Entry[] paramArrayOfEntry, Package.Version paramVersion)
    throws Instruction.FormatException
  {
    for (Instruction localInstruction = at(paramArrayOfByte, 0); localInstruction != null; localInstruction = localInstruction.next())
    {
      int i = localInstruction.getBC();
      if ((i < 0) || (i > 201))
      {
        localObject = "illegal opcode: " + i + " " + localInstruction;
        throw new FormatException((String)localObject);
      }
      Object localObject = localInstruction.getCPRef(paramArrayOfEntry);
      if (localObject != null)
      {
        int j = localInstruction.getCPTag();
        boolean bool = ((ConstantPool.Entry)localObject).tagMatches(j);
        if ((!bool) && ((bc == 183) || (bc == 184)) && (((ConstantPool.Entry)localObject).tagMatches(11)) && (paramVersion.greaterThan(Constants.JAVA7_MAX_CLASS_VERSION))) {
          bool = true;
        }
        if (!bool)
        {
          String str = "illegal reference, expected type=" + ConstantPool.tagName(j) + ": " + localInstruction.toString(paramArrayOfEntry);
          throw new FormatException(str);
        }
      }
    }
  }
  
  static
  {
    BC_LENGTH = new byte[2]['Ā'];
    BC_INDEX = new byte[2]['Ā'];
    BC_TAG = new byte[2]['Ā'];
    BC_BRANCH = new byte[2]['Ā'];
    BC_SLOT = new byte[2]['Ā'];
    BC_CON = new byte[2]['Ā'];
    BC_NAME = new String['Ā'];
    BC_FORMAT = new String[2]['Ê'];
    for (int i = 0; i < 202; i++)
    {
      BC_LENGTH[0][i] = -1;
      BC_LENGTH[1][i] = -1;
    }
    def("b", 0, 15);
    def("bx", 16);
    def("bxx", 17);
    def("bk", 18);
    def("bkk", 19, 20);
    def("blwbll", 21, 25);
    def("b", 26, 53);
    def("blwbll", 54, 58);
    def("b", 59, 131);
    def("blxwbllxx", 132);
    def("b", 133, 152);
    def("boo", 153, 168);
    def("blwbll", 169);
    def("", 170, 171);
    def("b", 172, 177);
    def("bkf", 178, 181);
    def("bkm", 182, 184);
    def("bkixx", 185);
    def("bkyxx", 186);
    def("bkc", 187);
    def("bx", 188);
    def("bkc", 189);
    def("b", 190, 191);
    def("bkc", 192, 193);
    def("b", 194, 195);
    def("", 196);
    def("bkcx", 197);
    def("boo", 198, 199);
    def("boooo", 200, 201);
    for (i = 0; i < 202; i++) {
      if ((BC_LENGTH[0][i] != -1) && (BC_LENGTH[1][i] == -1)) {
        BC_LENGTH[1][i] = ((byte)(1 + BC_LENGTH[0][i]));
      }
    }
    String str = "nop aconst_null iconst_m1 iconst_0 iconst_1 iconst_2 iconst_3 iconst_4 iconst_5 lconst_0 lconst_1 fconst_0 fconst_1 fconst_2 dconst_0 dconst_1 bipush sipush ldc ldc_w ldc2_w iload lload fload dload aload iload_0 iload_1 iload_2 iload_3 lload_0 lload_1 lload_2 lload_3 fload_0 fload_1 fload_2 fload_3 dload_0 dload_1 dload_2 dload_3 aload_0 aload_1 aload_2 aload_3 iaload laload faload daload aaload baload caload saload istore lstore fstore dstore astore istore_0 istore_1 istore_2 istore_3 lstore_0 lstore_1 lstore_2 lstore_3 fstore_0 fstore_1 fstore_2 fstore_3 dstore_0 dstore_1 dstore_2 dstore_3 astore_0 astore_1 astore_2 astore_3 iastore lastore fastore dastore aastore bastore castore sastore pop pop2 dup dup_x1 dup_x2 dup2 dup2_x1 dup2_x2 swap iadd ladd fadd dadd isub lsub fsub dsub imul lmul fmul dmul idiv ldiv fdiv ddiv irem lrem frem drem ineg lneg fneg dneg ishl lshl ishr lshr iushr lushr iand land ior lor ixor lxor iinc i2l i2f i2d l2i l2f l2d f2i f2l f2d d2i d2l d2f i2b i2c i2s lcmp fcmpl fcmpg dcmpl dcmpg ifeq ifne iflt ifge ifgt ifle if_icmpeq if_icmpne if_icmplt if_icmpge if_icmpgt if_icmple if_acmpeq if_acmpne goto jsr ret tableswitch lookupswitch ireturn lreturn freturn dreturn areturn return getstatic putstatic getfield putfield invokevirtual invokespecial invokestatic invokeinterface invokedynamic new newarray anewarray arraylength athrow checkcast instanceof monitorenter monitorexit wide multianewarray ifnull ifnonnull goto_w jsr_w ";
    for (int j = 0; str.length() > 0; j++)
    {
      int k = str.indexOf(' ');
      BC_NAME[j] = str.substring(0, k);
      str = str.substring(k + 1);
    }
  }
  
  static class FormatException
    extends IOException
  {
    private static final long serialVersionUID = 3175572275651367015L;
    
    FormatException(String paramString)
    {
      super();
    }
  }
  
  public static class LookupSwitch
    extends Instruction.Switch
  {
    public int getCaseCount()
    {
      return intAt(1);
    }
    
    public int getCaseValue(int paramInt)
    {
      return intAt(2 + paramInt * 2 + 0);
    }
    
    public int getCaseLabel(int paramInt)
    {
      return intAt(2 + paramInt * 2 + 1) + pc;
    }
    
    public void setCaseCount(int paramInt)
    {
      setIntAt(1, paramInt);
      length = getLength(paramInt);
    }
    
    public void setCaseValue(int paramInt1, int paramInt2)
    {
      setIntAt(2 + paramInt1 * 2 + 0, paramInt2);
    }
    
    public void setCaseLabel(int paramInt1, int paramInt2)
    {
      setIntAt(2 + paramInt1 * 2 + 1, paramInt2 - pc);
    }
    
    LookupSwitch(byte[] paramArrayOfByte, int paramInt)
    {
      super(paramInt, 171);
    }
    
    protected int getLength(int paramInt)
    {
      return apc - pc + (2 + paramInt * 2) * 4;
    }
  }
  
  public static abstract class Switch
    extends Instruction
  {
    protected int apc;
    
    public abstract int getCaseCount();
    
    public abstract int getCaseValue(int paramInt);
    
    public abstract int getCaseLabel(int paramInt);
    
    public abstract void setCaseCount(int paramInt);
    
    public abstract void setCaseValue(int paramInt1, int paramInt2);
    
    public abstract void setCaseLabel(int paramInt1, int paramInt2);
    
    protected abstract int getLength(int paramInt);
    
    public int getDefaultLabel()
    {
      return intAt(0) + pc;
    }
    
    public void setDefaultLabel(int paramInt)
    {
      setIntAt(0, paramInt - pc);
    }
    
    protected int intAt(int paramInt)
    {
      return getInt(bytes, apc + paramInt * 4);
    }
    
    protected void setIntAt(int paramInt1, int paramInt2)
    {
      setInt(bytes, apc + paramInt1 * 4, paramInt2);
    }
    
    protected Switch(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    {
      super(paramInt1, paramInt2, 0, 0);
      apc = alignPC(paramInt1 + 1);
      special = true;
      length = getLength(getCaseCount());
    }
    
    public int getAlignedPC()
    {
      return apc;
    }
    
    public String toString()
    {
      String str = super.toString();
      str = str + " Default:" + labstr(getDefaultLabel());
      int i = getCaseCount();
      for (int j = 0; j < i; j++) {
        str = str + "\n\tCase " + getCaseValue(j) + ":" + labstr(getCaseLabel(j));
      }
      return str;
    }
    
    public static int alignPC(int paramInt)
    {
      while (paramInt % 4 != 0) {
        paramInt++;
      }
      return paramInt;
    }
  }
  
  public static class TableSwitch
    extends Instruction.Switch
  {
    public int getLowCase()
    {
      return intAt(1);
    }
    
    public int getHighCase()
    {
      return intAt(2);
    }
    
    public int getCaseCount()
    {
      return intAt(2) - intAt(1) + 1;
    }
    
    public int getCaseValue(int paramInt)
    {
      return getLowCase() + paramInt;
    }
    
    public int getCaseLabel(int paramInt)
    {
      return intAt(3 + paramInt) + pc;
    }
    
    public void setLowCase(int paramInt)
    {
      setIntAt(1, paramInt);
    }
    
    public void setHighCase(int paramInt)
    {
      setIntAt(2, paramInt);
    }
    
    public void setCaseLabel(int paramInt1, int paramInt2)
    {
      setIntAt(3 + paramInt1, paramInt2 - pc);
    }
    
    public void setCaseCount(int paramInt)
    {
      setHighCase(getLowCase() + paramInt - 1);
      length = getLength(paramInt);
    }
    
    public void setCaseValue(int paramInt1, int paramInt2)
    {
      if (paramInt1 != 0) {
        throw new UnsupportedOperationException();
      }
      int i = getCaseCount();
      setLowCase(paramInt2);
      setCaseCount(i);
    }
    
    TableSwitch(byte[] paramArrayOfByte, int paramInt)
    {
      super(paramInt, 170);
    }
    
    protected int getLength(int paramInt)
    {
      return apc - pc + (3 + paramInt) * 4;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\java\util\jar\pack\Instruction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */