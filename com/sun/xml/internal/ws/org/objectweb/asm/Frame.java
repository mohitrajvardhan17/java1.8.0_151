package com.sun.xml.internal.ws.org.objectweb.asm;

final class Frame
{
  static final int DIM = -268435456;
  static final int ARRAY_OF = 268435456;
  static final int ELEMENT_OF = -268435456;
  static final int KIND = 251658240;
  static final int VALUE = 16777215;
  static final int BASE_KIND = 267386880;
  static final int BASE_VALUE = 1048575;
  static final int BASE = 16777216;
  static final int OBJECT = 24117248;
  static final int UNINITIALIZED = 25165824;
  private static final int LOCAL = 33554432;
  private static final int STACK = 50331648;
  static final int TOP = 16777216;
  static final int BOOLEAN = 16777225;
  static final int BYTE = 16777226;
  static final int CHAR = 16777227;
  static final int SHORT = 16777228;
  static final int INTEGER = 16777217;
  static final int FLOAT = 16777218;
  static final int DOUBLE = 16777219;
  static final int LONG = 16777220;
  static final int NULL = 16777221;
  static final int UNINITIALIZED_THIS = 16777222;
  static final int[] SIZE;
  Label owner;
  int[] inputLocals;
  int[] inputStack;
  private int[] outputLocals;
  private int[] outputStack;
  private int outputStackTop;
  private int initializationCount;
  private int[] initializations;
  
  Frame() {}
  
  private int get(int paramInt)
  {
    if ((outputLocals == null) || (paramInt >= outputLocals.length)) {
      return 0x2000000 | paramInt;
    }
    int i = outputLocals[paramInt];
    if (i == 0) {
      i = outputLocals[paramInt] = 0x2000000 | paramInt;
    }
    return i;
  }
  
  private void set(int paramInt1, int paramInt2)
  {
    if (outputLocals == null) {
      outputLocals = new int[10];
    }
    int i = outputLocals.length;
    if (paramInt1 >= i)
    {
      int[] arrayOfInt = new int[Math.max(paramInt1 + 1, 2 * i)];
      System.arraycopy(outputLocals, 0, arrayOfInt, 0, i);
      outputLocals = arrayOfInt;
    }
    outputLocals[paramInt1] = paramInt2;
  }
  
  private void push(int paramInt)
  {
    if (outputStack == null) {
      outputStack = new int[10];
    }
    int i = outputStack.length;
    if (outputStackTop >= i)
    {
      int[] arrayOfInt = new int[Math.max(outputStackTop + 1, 2 * i)];
      System.arraycopy(outputStack, 0, arrayOfInt, 0, i);
      outputStack = arrayOfInt;
    }
    outputStack[(outputStackTop++)] = paramInt;
    int j = owner.inputStackTop + outputStackTop;
    if (j > owner.outputStackMax) {
      owner.outputStackMax = j;
    }
  }
  
  private void push(ClassWriter paramClassWriter, String paramString)
  {
    int i = type(paramClassWriter, paramString);
    if (i != 0)
    {
      push(i);
      if ((i == 16777220) || (i == 16777219)) {
        push(16777216);
      }
    }
  }
  
  private static int type(ClassWriter paramClassWriter, String paramString)
  {
    int i = paramString.charAt(0) == '(' ? paramString.indexOf(')') + 1 : 0;
    String str;
    switch (paramString.charAt(i))
    {
    case 'V': 
      return 0;
    case 'B': 
    case 'C': 
    case 'I': 
    case 'S': 
    case 'Z': 
      return 16777217;
    case 'F': 
      return 16777218;
    case 'J': 
      return 16777220;
    case 'D': 
      return 16777219;
    case 'L': 
      str = paramString.substring(i + 1, paramString.length() - 1);
      return 0x1700000 | paramClassWriter.addType(str);
    }
    for (int k = i + 1; paramString.charAt(k) == '['; k++) {}
    int j;
    switch (paramString.charAt(k))
    {
    case 'Z': 
      j = 16777225;
      break;
    case 'C': 
      j = 16777227;
      break;
    case 'B': 
      j = 16777226;
      break;
    case 'S': 
      j = 16777228;
      break;
    case 'I': 
      j = 16777217;
      break;
    case 'F': 
      j = 16777218;
      break;
    case 'J': 
      j = 16777220;
      break;
    case 'D': 
      j = 16777219;
      break;
    case 'E': 
    case 'G': 
    case 'H': 
    case 'K': 
    case 'L': 
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
      str = paramString.substring(k + 1, paramString.length() - 1);
      j = 0x1700000 | paramClassWriter.addType(str);
    }
    return k - i << 28 | j;
  }
  
  private int pop()
  {
    if (outputStackTop > 0) {
      return outputStack[(--outputStackTop)];
    }
    return 0x3000000 | ---owner.inputStackTop;
  }
  
  private void pop(int paramInt)
  {
    if (outputStackTop >= paramInt)
    {
      outputStackTop -= paramInt;
    }
    else
    {
      owner.inputStackTop -= paramInt - outputStackTop;
      outputStackTop = 0;
    }
  }
  
  private void pop(String paramString)
  {
    int i = paramString.charAt(0);
    if (i == 40) {
      pop((MethodWriter.getArgumentsAndReturnSizes(paramString) >> 2) - 1);
    } else if ((i == 74) || (i == 68)) {
      pop(2);
    } else {
      pop(1);
    }
  }
  
  private void init(int paramInt)
  {
    if (initializations == null) {
      initializations = new int[2];
    }
    int i = initializations.length;
    if (initializationCount >= i)
    {
      int[] arrayOfInt = new int[Math.max(initializationCount + 1, 2 * i)];
      System.arraycopy(initializations, 0, arrayOfInt, 0, i);
      initializations = arrayOfInt;
    }
    initializations[(initializationCount++)] = paramInt;
  }
  
  private int init(ClassWriter paramClassWriter, int paramInt)
  {
    int i;
    if (paramInt == 16777222)
    {
      i = 0x1700000 | paramClassWriter.addType(thisName);
    }
    else if ((paramInt & 0xFFF00000) == 25165824)
    {
      String str = typeTable[(paramInt & 0xFFFFF)].strVal1;
      i = 0x1700000 | paramClassWriter.addType(str);
    }
    else
    {
      return paramInt;
    }
    for (int j = 0; j < initializationCount; j++)
    {
      int k = initializations[j];
      int m = k & 0xF0000000;
      int n = k & 0xF000000;
      if (n == 33554432) {
        k = m + inputLocals[(k & 0xFFFFFF)];
      } else if (n == 50331648) {
        k = m + inputStack[(inputStack.length - (k & 0xFFFFFF))];
      }
      if (paramInt == k) {
        return i;
      }
    }
    return paramInt;
  }
  
  void initInputFrame(ClassWriter paramClassWriter, int paramInt1, Type[] paramArrayOfType, int paramInt2)
  {
    inputLocals = new int[paramInt2];
    inputStack = new int[0];
    int i = 0;
    if ((paramInt1 & 0x8) == 0) {
      if ((paramInt1 & 0x40000) == 0) {
        inputLocals[(i++)] = (0x1700000 | paramClassWriter.addType(thisName));
      } else {
        inputLocals[(i++)] = 16777222;
      }
    }
    for (int j = 0; j < paramArrayOfType.length; j++)
    {
      int k = type(paramClassWriter, paramArrayOfType[j].getDescriptor());
      inputLocals[(i++)] = k;
      if ((k == 16777220) || (k == 16777219)) {
        inputLocals[(i++)] = 16777216;
      }
    }
    while (i < paramInt2) {
      inputLocals[(i++)] = 16777216;
    }
  }
  
  void execute(int paramInt1, int paramInt2, ClassWriter paramClassWriter, Item paramItem)
  {
    int i;
    int j;
    int k;
    String str;
    switch (paramInt1)
    {
    case 0: 
    case 116: 
    case 117: 
    case 118: 
    case 119: 
    case 145: 
    case 146: 
    case 147: 
    case 167: 
    case 177: 
      break;
    case 1: 
      push(16777221);
      break;
    case 2: 
    case 3: 
    case 4: 
    case 5: 
    case 6: 
    case 7: 
    case 8: 
    case 16: 
    case 17: 
    case 21: 
      push(16777217);
      break;
    case 9: 
    case 10: 
    case 22: 
      push(16777220);
      push(16777216);
      break;
    case 11: 
    case 12: 
    case 13: 
    case 23: 
      push(16777218);
      break;
    case 14: 
    case 15: 
    case 24: 
      push(16777219);
      push(16777216);
      break;
    case 18: 
      switch (type)
      {
      case 3: 
        push(16777217);
        break;
      case 5: 
        push(16777220);
        push(16777216);
        break;
      case 4: 
        push(16777218);
        break;
      case 6: 
        push(16777219);
        push(16777216);
        break;
      case 7: 
        push(0x1700000 | paramClassWriter.addType("java/lang/Class"));
        break;
      default: 
        push(0x1700000 | paramClassWriter.addType("java/lang/String"));
      }
      break;
    case 25: 
      push(get(paramInt2));
      break;
    case 46: 
    case 51: 
    case 52: 
    case 53: 
      pop(2);
      push(16777217);
      break;
    case 47: 
    case 143: 
      pop(2);
      push(16777220);
      push(16777216);
      break;
    case 48: 
      pop(2);
      push(16777218);
      break;
    case 49: 
    case 138: 
      pop(2);
      push(16777219);
      push(16777216);
      break;
    case 50: 
      pop(1);
      i = pop();
      push(-268435456 + i);
      break;
    case 54: 
    case 56: 
    case 58: 
      i = pop();
      set(paramInt2, i);
      if (paramInt2 > 0)
      {
        j = get(paramInt2 - 1);
        if ((j == 16777220) || (j == 16777219)) {
          set(paramInt2 - 1, 16777216);
        }
      }
      break;
    case 55: 
    case 57: 
      pop(1);
      i = pop();
      set(paramInt2, i);
      set(paramInt2 + 1, 16777216);
      if (paramInt2 > 0)
      {
        j = get(paramInt2 - 1);
        if ((j == 16777220) || (j == 16777219)) {
          set(paramInt2 - 1, 16777216);
        }
      }
      break;
    case 79: 
    case 81: 
    case 83: 
    case 84: 
    case 85: 
    case 86: 
      pop(3);
      break;
    case 80: 
    case 82: 
      pop(4);
      break;
    case 87: 
    case 153: 
    case 154: 
    case 155: 
    case 156: 
    case 157: 
    case 158: 
    case 170: 
    case 171: 
    case 172: 
    case 174: 
    case 176: 
    case 191: 
    case 194: 
    case 195: 
    case 198: 
    case 199: 
      pop(1);
      break;
    case 88: 
    case 159: 
    case 160: 
    case 161: 
    case 162: 
    case 163: 
    case 164: 
    case 165: 
    case 166: 
    case 173: 
    case 175: 
      pop(2);
      break;
    case 89: 
      i = pop();
      push(i);
      push(i);
      break;
    case 90: 
      i = pop();
      j = pop();
      push(i);
      push(j);
      push(i);
      break;
    case 91: 
      i = pop();
      j = pop();
      k = pop();
      push(i);
      push(k);
      push(j);
      push(i);
      break;
    case 92: 
      i = pop();
      j = pop();
      push(j);
      push(i);
      push(j);
      push(i);
      break;
    case 93: 
      i = pop();
      j = pop();
      k = pop();
      push(j);
      push(i);
      push(k);
      push(j);
      push(i);
      break;
    case 94: 
      i = pop();
      j = pop();
      k = pop();
      int m = pop();
      push(j);
      push(i);
      push(m);
      push(k);
      push(j);
      push(i);
      break;
    case 95: 
      i = pop();
      j = pop();
      push(i);
      push(j);
      break;
    case 96: 
    case 100: 
    case 104: 
    case 108: 
    case 112: 
    case 120: 
    case 122: 
    case 124: 
    case 126: 
    case 128: 
    case 130: 
    case 136: 
    case 142: 
    case 149: 
    case 150: 
      pop(2);
      push(16777217);
      break;
    case 97: 
    case 101: 
    case 105: 
    case 109: 
    case 113: 
    case 127: 
    case 129: 
    case 131: 
      pop(4);
      push(16777220);
      push(16777216);
      break;
    case 98: 
    case 102: 
    case 106: 
    case 110: 
    case 114: 
    case 137: 
    case 144: 
      pop(2);
      push(16777218);
      break;
    case 99: 
    case 103: 
    case 107: 
    case 111: 
    case 115: 
      pop(4);
      push(16777219);
      push(16777216);
      break;
    case 121: 
    case 123: 
    case 125: 
      pop(3);
      push(16777220);
      push(16777216);
      break;
    case 132: 
      set(paramInt2, 16777217);
      break;
    case 133: 
    case 140: 
      pop(1);
      push(16777220);
      push(16777216);
      break;
    case 134: 
      pop(1);
      push(16777218);
      break;
    case 135: 
    case 141: 
      pop(1);
      push(16777219);
      push(16777216);
      break;
    case 139: 
    case 190: 
    case 193: 
      pop(1);
      push(16777217);
      break;
    case 148: 
    case 151: 
    case 152: 
      pop(4);
      push(16777217);
      break;
    case 168: 
    case 169: 
      throw new RuntimeException("JSR/RET are not supported with computeFrames option");
    case 178: 
      push(paramClassWriter, strVal3);
      break;
    case 179: 
      pop(strVal3);
      break;
    case 180: 
      pop(1);
      push(paramClassWriter, strVal3);
      break;
    case 181: 
      pop(strVal3);
      pop();
      break;
    case 182: 
    case 183: 
    case 184: 
    case 185: 
      pop(strVal3);
      if (paramInt1 != 184)
      {
        i = pop();
        if ((paramInt1 == 183) && (strVal2.charAt(0) == '<')) {
          init(i);
        }
      }
      push(paramClassWriter, strVal3);
      break;
    case 187: 
      push(0x1800000 | paramClassWriter.addUninitializedType(strVal1, paramInt2));
      break;
    case 188: 
      pop();
      switch (paramInt2)
      {
      case 4: 
        push(285212681);
        break;
      case 5: 
        push(285212683);
        break;
      case 8: 
        push(285212682);
        break;
      case 9: 
        push(285212684);
        break;
      case 10: 
        push(285212673);
        break;
      case 6: 
        push(285212674);
        break;
      case 7: 
        push(285212675);
        break;
      default: 
        push(285212676);
      }
      break;
    case 189: 
      str = strVal1;
      pop();
      if (str.charAt(0) == '[') {
        push(paramClassWriter, '[' + str);
      } else {
        push(0x11700000 | paramClassWriter.addType(str));
      }
      break;
    case 192: 
      str = strVal1;
      pop();
      if (str.charAt(0) == '[') {
        push(paramClassWriter, str);
      } else {
        push(0x1700000 | paramClassWriter.addType(str));
      }
      break;
    case 19: 
    case 20: 
    case 26: 
    case 27: 
    case 28: 
    case 29: 
    case 30: 
    case 31: 
    case 32: 
    case 33: 
    case 34: 
    case 35: 
    case 36: 
    case 37: 
    case 38: 
    case 39: 
    case 40: 
    case 41: 
    case 42: 
    case 43: 
    case 44: 
    case 45: 
    case 59: 
    case 60: 
    case 61: 
    case 62: 
    case 63: 
    case 64: 
    case 65: 
    case 66: 
    case 67: 
    case 68: 
    case 69: 
    case 70: 
    case 71: 
    case 72: 
    case 73: 
    case 74: 
    case 75: 
    case 76: 
    case 77: 
    case 78: 
    case 186: 
    case 196: 
    case 197: 
    default: 
      pop(paramInt2);
      push(paramClassWriter, strVal1);
    }
  }
  
  boolean merge(ClassWriter paramClassWriter, Frame paramFrame, int paramInt)
  {
    boolean bool = false;
    int i1 = inputLocals.length;
    int i2 = inputStack.length;
    if (inputLocals == null)
    {
      inputLocals = new int[i1];
      bool = true;
    }
    int j;
    int n;
    int k;
    int m;
    for (int i = 0; i < i1; i++)
    {
      if ((outputLocals != null) && (i < outputLocals.length))
      {
        j = outputLocals[i];
        if (j == 0)
        {
          n = inputLocals[i];
        }
        else
        {
          k = j & 0xF0000000;
          m = j & 0xF000000;
          if (m == 33554432) {
            n = k + inputLocals[(j & 0xFFFFFF)];
          } else if (m == 50331648) {
            n = k + inputStack[(i2 - (j & 0xFFFFFF))];
          } else {
            n = j;
          }
        }
      }
      else
      {
        n = inputLocals[i];
      }
      if (initializations != null) {
        n = init(paramClassWriter, n);
      }
      bool |= merge(paramClassWriter, n, inputLocals, i);
    }
    if (paramInt > 0)
    {
      for (i = 0; i < i1; i++)
      {
        n = inputLocals[i];
        bool |= merge(paramClassWriter, n, inputLocals, i);
      }
      if (inputStack == null)
      {
        inputStack = new int[1];
        bool = true;
      }
      bool |= merge(paramClassWriter, paramInt, inputStack, 0);
      return bool;
    }
    int i3 = inputStack.length + owner.inputStackTop;
    if (inputStack == null)
    {
      inputStack = new int[i3 + outputStackTop];
      bool = true;
    }
    for (i = 0; i < i3; i++)
    {
      n = inputStack[i];
      if (initializations != null) {
        n = init(paramClassWriter, n);
      }
      bool |= merge(paramClassWriter, n, inputStack, i);
    }
    for (i = 0; i < outputStackTop; i++)
    {
      j = outputStack[i];
      k = j & 0xF0000000;
      m = j & 0xF000000;
      if (m == 33554432) {
        n = k + inputLocals[(j & 0xFFFFFF)];
      } else if (m == 50331648) {
        n = k + inputStack[(i2 - (j & 0xFFFFFF))];
      } else {
        n = j;
      }
      if (initializations != null) {
        n = init(paramClassWriter, n);
      }
      bool |= merge(paramClassWriter, n, inputStack, i3 + i);
    }
    return bool;
  }
  
  private static boolean merge(ClassWriter paramClassWriter, int paramInt1, int[] paramArrayOfInt, int paramInt2)
  {
    int i = paramArrayOfInt[paramInt2];
    if (i == paramInt1) {
      return false;
    }
    if ((paramInt1 & 0xFFFFFFF) == 16777221)
    {
      if (i == 16777221) {
        return false;
      }
      paramInt1 = 16777221;
    }
    if (i == 0)
    {
      paramArrayOfInt[paramInt2] = paramInt1;
      return true;
    }
    int j;
    if (((i & 0xFF00000) == 24117248) || ((i & 0xF0000000) != 0))
    {
      if (paramInt1 == 16777221) {
        return false;
      }
      if ((paramInt1 & 0xFFF00000) == (i & 0xFFF00000))
      {
        if ((i & 0xFF00000) == 24117248) {
          j = paramInt1 & 0xF0000000 | 0x1700000 | paramClassWriter.getMergedType(paramInt1 & 0xFFFFF, i & 0xFFFFF);
        } else {
          j = 0x1700000 | paramClassWriter.addType("java/lang/Object");
        }
      }
      else if (((paramInt1 & 0xFF00000) == 24117248) || ((paramInt1 & 0xF0000000) != 0)) {
        j = 0x1700000 | paramClassWriter.addType("java/lang/Object");
      } else {
        j = 16777216;
      }
    }
    else if (i == 16777221)
    {
      j = ((paramInt1 & 0xFF00000) == 24117248) || ((paramInt1 & 0xF0000000) != 0) ? paramInt1 : 16777216;
    }
    else
    {
      j = 16777216;
    }
    if (i != j)
    {
      paramArrayOfInt[paramInt2] = j;
      return true;
    }
    return false;
  }
  
  static
  {
    int[] arrayOfInt = new int['Ê'];
    String str = "EFFFFFFFFGGFFFGGFFFEEFGFGFEEEEEEEEEEEEEEEEEEEEDEDEDDDDDCDCDEEEEEEEEEEEEEEEEEEEEBABABBBBDCFFFGGGEDCDCDCDCDCDCDCDCDCDCEEEEDDDDDDDCDCDCEFEFDDEEFFDEDEEEBDDBBDDDDDDCCCCCCCCEFEDDDCDCDEEEEEEEEEEFEEEEEEDDEEDDEE";
    for (int i = 0; i < arrayOfInt.length; i++) {
      arrayOfInt[i] = (str.charAt(i) - 'E');
    }
    SIZE = arrayOfInt;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\org\objectweb\asm\Frame.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */