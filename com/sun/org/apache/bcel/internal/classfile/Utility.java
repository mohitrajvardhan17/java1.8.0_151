package com.sun.org.apache.bcel.internal.classfile;

import com.sun.org.apache.bcel.internal.Constants;
import com.sun.org.apache.bcel.internal.util.ByteSequence;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.CharArrayReader;
import java.io.CharArrayWriter;
import java.io.FilterReader;
import java.io.FilterWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public abstract class Utility
{
  private static int consumed_chars;
  private static boolean wide = false;
  private static final int FREE_CHARS = 48;
  private static int[] CHAR_MAP = new int[48];
  private static int[] MAP_CHAR = new int['Ā'];
  private static final char ESCAPE_CHAR = '$';
  
  public Utility() {}
  
  public static final String accessToString(int paramInt)
  {
    return accessToString(paramInt, false);
  }
  
  public static final String accessToString(int paramInt, boolean paramBoolean)
  {
    StringBuffer localStringBuffer = new StringBuffer();
    int i = 0;
    for (int j = 0; i < 2048; j++)
    {
      i = pow2(j);
      if (((paramInt & i) != 0) && ((!paramBoolean) || ((i != 32) && (i != 512)))) {
        localStringBuffer.append(Constants.ACCESS_NAMES[j] + " ");
      }
    }
    return localStringBuffer.toString().trim();
  }
  
  public static final String classOrInterface(int paramInt)
  {
    return (paramInt & 0x200) != 0 ? "interface" : "class";
  }
  
  public static final String codeToString(byte[] paramArrayOfByte, ConstantPool paramConstantPool, int paramInt1, int paramInt2, boolean paramBoolean)
  {
    StringBuffer localStringBuffer = new StringBuffer(paramArrayOfByte.length * 20);
    ByteSequence localByteSequence = new ByteSequence(paramArrayOfByte);
    try
    {
      for (int i = 0; i < paramInt1; i++) {
        codeToString(localByteSequence, paramConstantPool, paramBoolean);
      }
      for (i = 0; localByteSequence.available() > 0; i++) {
        if ((paramInt2 < 0) || (i < paramInt2))
        {
          String str = fillup(localByteSequence.getIndex() + ":", 6, true, ' ');
          localStringBuffer.append(str + codeToString(localByteSequence, paramConstantPool, paramBoolean) + '\n');
        }
      }
    }
    catch (IOException localIOException)
    {
      System.out.println(localStringBuffer.toString());
      localIOException.printStackTrace();
      throw new ClassFormatException("Byte code error: " + localIOException);
    }
    return localStringBuffer.toString();
  }
  
  public static final String codeToString(byte[] paramArrayOfByte, ConstantPool paramConstantPool, int paramInt1, int paramInt2)
  {
    return codeToString(paramArrayOfByte, paramConstantPool, paramInt1, paramInt2, true);
  }
  
  public static final String codeToString(ByteSequence paramByteSequence, ConstantPool paramConstantPool, boolean paramBoolean)
    throws IOException
  {
    int i = (short)paramByteSequence.readUnsignedByte();
    int j = 0;
    int i4 = 0;
    StringBuffer localStringBuffer = new StringBuffer(Constants.OPCODE_NAMES[i]);
    int i6;
    int i7;
    if ((i == 170) || (i == 171))
    {
      i6 = paramByteSequence.getIndex() % 4;
      i4 = i6 == 0 ? 0 : 4 - i6;
      for (i7 = 0; i7 < i4; i7++)
      {
        int i8;
        if ((i8 = paramByteSequence.readByte()) != 0) {
          System.err.println("Warning: Padding byte != 0 in " + Constants.OPCODE_NAMES[i] + ":" + i8);
        }
      }
      j = paramByteSequence.readInt();
    }
    int i5;
    int[] arrayOfInt2;
    int i2;
    int i1;
    switch (i)
    {
    case 170: 
      int k = paramByteSequence.readInt();
      int m = paramByteSequence.readInt();
      i5 = paramByteSequence.getIndex() - 12 - i4 - 1;
      j += i5;
      localStringBuffer.append("\tdefault = " + j + ", low = " + k + ", high = " + m + "(");
      arrayOfInt2 = new int[m - k + 1];
      for (i6 = 0; i6 < arrayOfInt2.length; i6++)
      {
        arrayOfInt2[i6] = (i5 + paramByteSequence.readInt());
        localStringBuffer.append(arrayOfInt2[i6]);
        if (i6 < arrayOfInt2.length - 1) {
          localStringBuffer.append(", ");
        }
      }
      localStringBuffer.append(")");
      break;
    case 171: 
      int n = paramByteSequence.readInt();
      i5 = paramByteSequence.getIndex() - 8 - i4 - 1;
      int[] arrayOfInt1 = new int[n];
      arrayOfInt2 = new int[n];
      j += i5;
      localStringBuffer.append("\tdefault = " + j + ", npairs = " + n + " (");
      for (i6 = 0; i6 < n; i6++)
      {
        arrayOfInt1[i6] = paramByteSequence.readInt();
        arrayOfInt2[i6] = (i5 + paramByteSequence.readInt());
        localStringBuffer.append("(" + arrayOfInt1[i6] + ", " + arrayOfInt2[i6] + ")");
        if (i6 < n - 1) {
          localStringBuffer.append(", ");
        }
      }
      localStringBuffer.append(")");
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
    case 167: 
    case 168: 
    case 198: 
    case 199: 
      localStringBuffer.append("\t\t#" + (paramByteSequence.getIndex() - 1 + paramByteSequence.readShort()));
      break;
    case 200: 
    case 201: 
      localStringBuffer.append("\t\t#" + (paramByteSequence.getIndex() - 1 + paramByteSequence.readInt()));
      break;
    case 21: 
    case 22: 
    case 23: 
    case 24: 
    case 25: 
    case 54: 
    case 55: 
    case 56: 
    case 57: 
    case 58: 
    case 169: 
      if (wide)
      {
        i2 = paramByteSequence.readUnsignedShort();
        wide = false;
      }
      else
      {
        i2 = paramByteSequence.readUnsignedByte();
      }
      localStringBuffer.append("\t\t%" + i2);
      break;
    case 196: 
      wide = true;
      localStringBuffer.append("\t(wide)");
      break;
    case 188: 
      localStringBuffer.append("\t\t<" + Constants.TYPE_NAMES[paramByteSequence.readByte()] + ">");
      break;
    case 178: 
    case 179: 
    case 180: 
    case 181: 
      i1 = paramByteSequence.readUnsignedShort();
      localStringBuffer.append("\t\t" + paramConstantPool.constantToString(i1, (byte)9) + (paramBoolean ? " (" + i1 + ")" : ""));
      break;
    case 187: 
    case 192: 
      localStringBuffer.append("\t");
    case 193: 
      i1 = paramByteSequence.readUnsignedShort();
      localStringBuffer.append("\t<" + paramConstantPool.constantToString(i1, (byte)7) + ">" + (paramBoolean ? " (" + i1 + ")" : ""));
      break;
    case 182: 
    case 183: 
    case 184: 
      i1 = paramByteSequence.readUnsignedShort();
      localStringBuffer.append("\t" + paramConstantPool.constantToString(i1, (byte)10) + (paramBoolean ? " (" + i1 + ")" : ""));
      break;
    case 185: 
      i1 = paramByteSequence.readUnsignedShort();
      i6 = paramByteSequence.readUnsignedByte();
      localStringBuffer.append("\t" + paramConstantPool.constantToString(i1, (byte)11) + (paramBoolean ? " (" + i1 + ")\t" : "") + i6 + "\t" + paramByteSequence.readUnsignedByte());
      break;
    case 19: 
    case 20: 
      i1 = paramByteSequence.readUnsignedShort();
      localStringBuffer.append("\t\t" + paramConstantPool.constantToString(i1, paramConstantPool.getConstant(i1).getTag()) + (paramBoolean ? " (" + i1 + ")" : ""));
      break;
    case 18: 
      i1 = paramByteSequence.readUnsignedByte();
      localStringBuffer.append("\t\t" + paramConstantPool.constantToString(i1, paramConstantPool.getConstant(i1).getTag()) + (paramBoolean ? " (" + i1 + ")" : ""));
      break;
    case 189: 
      i1 = paramByteSequence.readUnsignedShort();
      localStringBuffer.append("\t\t<" + compactClassName(paramConstantPool.getConstantString(i1, (byte)7), false) + ">" + (paramBoolean ? " (" + i1 + ")" : ""));
      break;
    case 197: 
      i1 = paramByteSequence.readUnsignedShort();
      i7 = paramByteSequence.readUnsignedByte();
      localStringBuffer.append("\t<" + compactClassName(paramConstantPool.getConstantString(i1, (byte)7), false) + ">\t" + i7 + (paramBoolean ? " (" + i1 + ")" : ""));
      break;
    case 132: 
      int i3;
      if (wide)
      {
        i2 = paramByteSequence.readUnsignedShort();
        i3 = paramByteSequence.readShort();
        wide = false;
      }
      else
      {
        i2 = paramByteSequence.readUnsignedByte();
        i3 = paramByteSequence.readByte();
      }
      localStringBuffer.append("\t\t%" + i2 + "\t" + i3);
      break;
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
    case 46: 
    case 47: 
    case 48: 
    case 49: 
    case 50: 
    case 51: 
    case 52: 
    case 53: 
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
    case 79: 
    case 80: 
    case 81: 
    case 82: 
    case 83: 
    case 84: 
    case 85: 
    case 86: 
    case 87: 
    case 88: 
    case 89: 
    case 90: 
    case 91: 
    case 92: 
    case 93: 
    case 94: 
    case 95: 
    case 96: 
    case 97: 
    case 98: 
    case 99: 
    case 100: 
    case 101: 
    case 102: 
    case 103: 
    case 104: 
    case 105: 
    case 106: 
    case 107: 
    case 108: 
    case 109: 
    case 110: 
    case 111: 
    case 112: 
    case 113: 
    case 114: 
    case 115: 
    case 116: 
    case 117: 
    case 118: 
    case 119: 
    case 120: 
    case 121: 
    case 122: 
    case 123: 
    case 124: 
    case 125: 
    case 126: 
    case 127: 
    case 128: 
    case 129: 
    case 130: 
    case 131: 
    case 133: 
    case 134: 
    case 135: 
    case 136: 
    case 137: 
    case 138: 
    case 139: 
    case 140: 
    case 141: 
    case 142: 
    case 143: 
    case 144: 
    case 145: 
    case 146: 
    case 147: 
    case 148: 
    case 149: 
    case 150: 
    case 151: 
    case 152: 
    case 172: 
    case 173: 
    case 174: 
    case 175: 
    case 176: 
    case 177: 
    case 186: 
    case 190: 
    case 191: 
    case 194: 
    case 195: 
    default: 
      if (Constants.NO_OF_OPERANDS[i] > 0) {
        for (i7 = 0; i7 < Constants.TYPE_OF_OPERANDS[i].length; i7++)
        {
          localStringBuffer.append("\t\t");
          switch (Constants.TYPE_OF_OPERANDS[i][i7])
          {
          case 8: 
            localStringBuffer.append(paramByteSequence.readByte());
            break;
          case 9: 
            localStringBuffer.append(paramByteSequence.readShort());
            break;
          case 10: 
            localStringBuffer.append(paramByteSequence.readInt());
            break;
          default: 
            System.err.println("Unreachable default case reached!");
            localStringBuffer.setLength(0);
          }
        }
      }
      break;
    }
    return localStringBuffer.toString();
  }
  
  public static final String codeToString(ByteSequence paramByteSequence, ConstantPool paramConstantPool)
    throws IOException
  {
    return codeToString(paramByteSequence, paramConstantPool, true);
  }
  
  public static final String compactClassName(String paramString)
  {
    return compactClassName(paramString, true);
  }
  
  public static final String compactClassName(String paramString1, String paramString2, boolean paramBoolean)
  {
    int i = paramString2.length();
    paramString1 = paramString1.replace('/', '.');
    if ((paramBoolean) && (paramString1.startsWith(paramString2)) && (paramString1.substring(i).indexOf('.') == -1)) {
      paramString1 = paramString1.substring(i);
    }
    return paramString1;
  }
  
  public static final String compactClassName(String paramString, boolean paramBoolean)
  {
    return compactClassName(paramString, "java.lang.", paramBoolean);
  }
  
  private static final boolean is_digit(char paramChar)
  {
    return (paramChar >= '0') && (paramChar <= '9');
  }
  
  private static final boolean is_space(char paramChar)
  {
    return (paramChar == ' ') || (paramChar == '\t') || (paramChar == '\r') || (paramChar == '\n');
  }
  
  public static final int setBit(int paramInt1, int paramInt2)
  {
    return paramInt1 | pow2(paramInt2);
  }
  
  public static final int clearBit(int paramInt1, int paramInt2)
  {
    int i = pow2(paramInt2);
    return (paramInt1 & i) == 0 ? paramInt1 : paramInt1 ^ i;
  }
  
  public static final boolean isSet(int paramInt1, int paramInt2)
  {
    return (paramInt1 & pow2(paramInt2)) != 0;
  }
  
  public static final String methodTypeToSignature(String paramString, String[] paramArrayOfString)
    throws ClassFormatException
  {
    StringBuffer localStringBuffer = new StringBuffer("(");
    if (paramArrayOfString != null) {
      for (int i = 0; i < paramArrayOfString.length; i++)
      {
        str = getSignature(paramArrayOfString[i]);
        if (str.endsWith("V")) {
          throw new ClassFormatException("Invalid type: " + paramArrayOfString[i]);
        }
        localStringBuffer.append(str);
      }
    }
    String str = getSignature(paramString);
    localStringBuffer.append(")" + str);
    return localStringBuffer.toString();
  }
  
  public static final String[] methodSignatureArgumentTypes(String paramString)
    throws ClassFormatException
  {
    return methodSignatureArgumentTypes(paramString, true);
  }
  
  public static final String[] methodSignatureArgumentTypes(String paramString, boolean paramBoolean)
    throws ClassFormatException
  {
    ArrayList localArrayList = new ArrayList();
    try
    {
      if (paramString.charAt(0) != '(') {
        throw new ClassFormatException("Invalid method signature: " + paramString);
      }
      int i = 1;
      while (paramString.charAt(i) != ')')
      {
        localArrayList.add(signatureToString(paramString.substring(i), paramBoolean));
        i += consumed_chars;
      }
    }
    catch (StringIndexOutOfBoundsException localStringIndexOutOfBoundsException)
    {
      throw new ClassFormatException("Invalid method signature: " + paramString);
    }
    String[] arrayOfString = new String[localArrayList.size()];
    localArrayList.toArray(arrayOfString);
    return arrayOfString;
  }
  
  public static final String methodSignatureReturnType(String paramString)
    throws ClassFormatException
  {
    return methodSignatureReturnType(paramString, true);
  }
  
  public static final String methodSignatureReturnType(String paramString, boolean paramBoolean)
    throws ClassFormatException
  {
    String str;
    try
    {
      int i = paramString.lastIndexOf(')') + 1;
      str = signatureToString(paramString.substring(i), paramBoolean);
    }
    catch (StringIndexOutOfBoundsException localStringIndexOutOfBoundsException)
    {
      throw new ClassFormatException("Invalid method signature: " + paramString);
    }
    return str;
  }
  
  public static final String methodSignatureToString(String paramString1, String paramString2, String paramString3)
  {
    return methodSignatureToString(paramString1, paramString2, paramString3, true);
  }
  
  public static final String methodSignatureToString(String paramString1, String paramString2, String paramString3, boolean paramBoolean)
  {
    return methodSignatureToString(paramString1, paramString2, paramString3, paramBoolean, null);
  }
  
  public static final String methodSignatureToString(String paramString1, String paramString2, String paramString3, boolean paramBoolean, LocalVariableTable paramLocalVariableTable)
    throws ClassFormatException
  {
    StringBuffer localStringBuffer = new StringBuffer("(");
    int j = paramString3.indexOf("static") >= 0 ? 0 : 1;
    String str1;
    try
    {
      if (paramString1.charAt(0) != '(') {
        throw new ClassFormatException("Invalid method signature: " + paramString1);
      }
      int i = 1;
      while (paramString1.charAt(i) != ')')
      {
        String str2 = signatureToString(paramString1.substring(i), paramBoolean);
        localStringBuffer.append(str2);
        if (paramLocalVariableTable != null)
        {
          LocalVariable localLocalVariable = paramLocalVariableTable.getLocalVariable(j);
          if (localLocalVariable != null) {
            localStringBuffer.append(" " + localLocalVariable.getName());
          }
        }
        else
        {
          localStringBuffer.append(" arg" + j);
        }
        if (("double".equals(str2)) || ("long".equals(str2))) {
          j += 2;
        } else {
          j++;
        }
        localStringBuffer.append(", ");
        i += consumed_chars;
      }
      i++;
      str1 = signatureToString(paramString1.substring(i), paramBoolean);
    }
    catch (StringIndexOutOfBoundsException localStringIndexOutOfBoundsException)
    {
      throw new ClassFormatException("Invalid method signature: " + paramString1);
    }
    if (localStringBuffer.length() > 1) {
      localStringBuffer.setLength(localStringBuffer.length() - 2);
    }
    localStringBuffer.append(")");
    return paramString3 + (paramString3.length() > 0 ? " " : "") + str1 + " " + paramString2 + localStringBuffer.toString();
  }
  
  private static final int pow2(int paramInt)
  {
    return 1 << paramInt;
  }
  
  public static final String replace(String paramString1, String paramString2, String paramString3)
  {
    StringBuffer localStringBuffer = new StringBuffer();
    try
    {
      int i;
      if ((i = paramString1.indexOf(paramString2)) != -1)
      {
        for (int j = 0; (i = paramString1.indexOf(paramString2, j)) != -1; j = i + paramString2.length())
        {
          localStringBuffer.append(paramString1.substring(j, i));
          localStringBuffer.append(paramString3);
        }
        localStringBuffer.append(paramString1.substring(j));
        paramString1 = localStringBuffer.toString();
      }
    }
    catch (StringIndexOutOfBoundsException localStringIndexOutOfBoundsException)
    {
      System.err.println(localStringIndexOutOfBoundsException);
    }
    return paramString1;
  }
  
  public static final String signatureToString(String paramString)
  {
    return signatureToString(paramString, true);
  }
  
  public static final String signatureToString(String paramString, boolean paramBoolean)
  {
    consumed_chars = 1;
    try
    {
      int i;
      switch (paramString.charAt(0))
      {
      case 'B': 
        return "byte";
      case 'C': 
        return "char";
      case 'D': 
        return "double";
      case 'F': 
        return "float";
      case 'I': 
        return "int";
      case 'J': 
        return "long";
      case 'L': 
        i = paramString.indexOf(';');
        if (i < 0) {
          throw new ClassFormatException("Invalid signature: " + paramString);
        }
        consumed_chars = i + 1;
        return compactClassName(paramString.substring(1, i), paramBoolean);
      case 'S': 
        return "short";
      case 'Z': 
        return "boolean";
      case '[': 
        StringBuffer localStringBuffer = new StringBuffer();
        for (i = 0; paramString.charAt(i) == '['; i++) {
          localStringBuffer.append("[]");
        }
        int j = i;
        String str = signatureToString(paramString.substring(i), paramBoolean);
        consumed_chars += j;
        return str + localStringBuffer.toString();
      case 'V': 
        return "void";
      }
      throw new ClassFormatException("Invalid signature: `" + paramString + "'");
    }
    catch (StringIndexOutOfBoundsException localStringIndexOutOfBoundsException)
    {
      throw new ClassFormatException("Invalid signature: " + localStringIndexOutOfBoundsException + ":" + paramString);
    }
  }
  
  public static String getSignature(String paramString)
  {
    StringBuffer localStringBuffer = new StringBuffer();
    char[] arrayOfChar = paramString.toCharArray();
    int i = 0;
    int j = 0;
    int k = -1;
    for (int m = 0; m < arrayOfChar.length; m++) {
      switch (arrayOfChar[m])
      {
      case '\t': 
      case '\n': 
      case '\f': 
      case '\r': 
      case ' ': 
        if (i != 0) {
          j = 1;
        }
        break;
      case '[': 
        if (i == 0) {
          throw new RuntimeException("Illegal type: " + paramString);
        }
        k = m;
        break;
      default: 
        i = 1;
        if (j == 0) {
          localStringBuffer.append(arrayOfChar[m]);
        }
        break;
      }
    }
    m = 0;
    if (k > 0) {
      m = countBrackets(paramString.substring(k));
    }
    paramString = localStringBuffer.toString();
    localStringBuffer.setLength(0);
    for (int n = 0; n < m; n++) {
      localStringBuffer.append('[');
    }
    n = 0;
    for (int i1 = 4; (i1 <= 12) && (n == 0); i1++) {
      if (Constants.TYPE_NAMES[i1].equals(paramString))
      {
        n = 1;
        localStringBuffer.append(Constants.SHORT_TYPE_NAMES[i1]);
      }
    }
    if (n == 0) {
      localStringBuffer.append('L' + paramString.replace('.', '/') + ';');
    }
    return localStringBuffer.toString();
  }
  
  private static int countBrackets(String paramString)
  {
    char[] arrayOfChar = paramString.toCharArray();
    int i = 0;
    int j = 0;
    for (int k = 0; k < arrayOfChar.length; k++) {
      switch (arrayOfChar[k])
      {
      case '[': 
        if (j != 0) {
          throw new RuntimeException("Illegally nested brackets:" + paramString);
        }
        j = 1;
        break;
      case ']': 
        if (j == 0) {
          throw new RuntimeException("Illegally nested brackets:" + paramString);
        }
        j = 0;
        i++;
      }
    }
    if (j != 0) {
      throw new RuntimeException("Illegally nested brackets:" + paramString);
    }
    return i;
  }
  
  public static final byte typeOfMethodSignature(String paramString)
    throws ClassFormatException
  {
    try
    {
      if (paramString.charAt(0) != '(') {
        throw new ClassFormatException("Invalid method signature: " + paramString);
      }
      int i = paramString.lastIndexOf(')') + 1;
      return typeOfSignature(paramString.substring(i));
    }
    catch (StringIndexOutOfBoundsException localStringIndexOutOfBoundsException)
    {
      throw new ClassFormatException("Invalid method signature: " + paramString);
    }
  }
  
  public static final byte typeOfSignature(String paramString)
    throws ClassFormatException
  {
    try
    {
      switch (paramString.charAt(0))
      {
      case 'B': 
        return 8;
      case 'C': 
        return 5;
      case 'D': 
        return 7;
      case 'F': 
        return 6;
      case 'I': 
        return 10;
      case 'J': 
        return 11;
      case 'L': 
        return 14;
      case '[': 
        return 13;
      case 'V': 
        return 12;
      case 'Z': 
        return 4;
      case 'S': 
        return 9;
      }
      throw new ClassFormatException("Invalid method signature: " + paramString);
    }
    catch (StringIndexOutOfBoundsException localStringIndexOutOfBoundsException)
    {
      throw new ClassFormatException("Invalid method signature: " + paramString);
    }
  }
  
  public static short searchOpcode(String paramString)
  {
    paramString = paramString.toLowerCase();
    int j;
    for (int i = 0; i < Constants.OPCODE_NAMES.length; j = (short)(i + 1)) {
      if (Constants.OPCODE_NAMES[i].equals(paramString)) {
        return i;
      }
    }
    return -1;
  }
  
  private static final short byteToShort(byte paramByte)
  {
    return paramByte < 0 ? (short)(256 + paramByte) : (short)paramByte;
  }
  
  public static final String toHexString(byte[] paramArrayOfByte)
  {
    StringBuffer localStringBuffer = new StringBuffer();
    for (int i = 0; i < paramArrayOfByte.length; i++)
    {
      int j = byteToShort(paramArrayOfByte[i]);
      String str = Integer.toString(j, 16);
      if (j < 16) {
        localStringBuffer.append('0');
      }
      localStringBuffer.append(str);
      if (i < paramArrayOfByte.length - 1) {
        localStringBuffer.append(' ');
      }
    }
    return localStringBuffer.toString();
  }
  
  public static final String format(int paramInt1, int paramInt2, boolean paramBoolean, char paramChar)
  {
    return fillup(Integer.toString(paramInt1), paramInt2, paramBoolean, paramChar);
  }
  
  public static final String fillup(String paramString, int paramInt, boolean paramBoolean, char paramChar)
  {
    int i = paramInt - paramString.length();
    char[] arrayOfChar = new char[i < 0 ? 0 : i];
    for (int j = 0; j < arrayOfChar.length; j++) {
      arrayOfChar[j] = paramChar;
    }
    if (paramBoolean) {
      return paramString + new String(arrayOfChar);
    }
    return new String(arrayOfChar) + paramString;
  }
  
  static final boolean equals(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2)
  {
    int i;
    if ((i = paramArrayOfByte1.length) != paramArrayOfByte2.length) {
      return false;
    }
    for (int j = 0; j < i; j++) {
      if (paramArrayOfByte1[j] != paramArrayOfByte2[j]) {
        return false;
      }
    }
    return true;
  }
  
  public static final void printArray(PrintStream paramPrintStream, Object[] paramArrayOfObject)
  {
    paramPrintStream.println(printArray(paramArrayOfObject, true));
  }
  
  public static final void printArray(PrintWriter paramPrintWriter, Object[] paramArrayOfObject)
  {
    paramPrintWriter.println(printArray(paramArrayOfObject, true));
  }
  
  public static final String printArray(Object[] paramArrayOfObject)
  {
    return printArray(paramArrayOfObject, true);
  }
  
  public static final String printArray(Object[] paramArrayOfObject, boolean paramBoolean)
  {
    return printArray(paramArrayOfObject, paramBoolean, false);
  }
  
  public static final String printArray(Object[] paramArrayOfObject, boolean paramBoolean1, boolean paramBoolean2)
  {
    if (paramArrayOfObject == null) {
      return null;
    }
    StringBuffer localStringBuffer = new StringBuffer();
    if (paramBoolean1) {
      localStringBuffer.append('{');
    }
    for (int i = 0; i < paramArrayOfObject.length; i++)
    {
      if (paramArrayOfObject[i] != null) {
        localStringBuffer.append((paramBoolean2 ? "\"" : "") + paramArrayOfObject[i].toString() + (paramBoolean2 ? "\"" : ""));
      } else {
        localStringBuffer.append("null");
      }
      if (i < paramArrayOfObject.length - 1) {
        localStringBuffer.append(", ");
      }
    }
    if (paramBoolean1) {
      localStringBuffer.append('}');
    }
    return localStringBuffer.toString();
  }
  
  public static boolean isJavaIdentifierPart(char paramChar)
  {
    return ((paramChar >= 'a') && (paramChar <= 'z')) || ((paramChar >= 'A') && (paramChar <= 'Z')) || ((paramChar >= '0') && (paramChar <= '9')) || (paramChar == '_');
  }
  
  public static String encode(byte[] paramArrayOfByte, boolean paramBoolean)
    throws IOException
  {
    if (paramBoolean)
    {
      localObject1 = new ByteArrayOutputStream();
      localObject2 = new GZIPOutputStream((OutputStream)localObject1);
      ((GZIPOutputStream)localObject2).write(paramArrayOfByte, 0, paramArrayOfByte.length);
      ((GZIPOutputStream)localObject2).close();
      ((ByteArrayOutputStream)localObject1).close();
      paramArrayOfByte = ((ByteArrayOutputStream)localObject1).toByteArray();
    }
    Object localObject1 = new CharArrayWriter();
    Object localObject2 = new JavaWriter((Writer)localObject1);
    for (int i = 0; i < paramArrayOfByte.length; i++)
    {
      int j = paramArrayOfByte[i] & 0xFF;
      ((JavaWriter)localObject2).write(j);
    }
    return ((CharArrayWriter)localObject1).toString();
  }
  
  public static byte[] decode(String paramString, boolean paramBoolean)
    throws IOException
  {
    char[] arrayOfChar = paramString.toCharArray();
    CharArrayReader localCharArrayReader = new CharArrayReader(arrayOfChar);
    JavaReader localJavaReader = new JavaReader(localCharArrayReader);
    ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
    int i;
    while ((i = localJavaReader.read()) >= 0) {
      localByteArrayOutputStream.write(i);
    }
    localByteArrayOutputStream.close();
    localCharArrayReader.close();
    localJavaReader.close();
    byte[] arrayOfByte1 = localByteArrayOutputStream.toByteArray();
    if (paramBoolean)
    {
      GZIPInputStream localGZIPInputStream = new GZIPInputStream(new ByteArrayInputStream(arrayOfByte1));
      byte[] arrayOfByte2 = new byte[arrayOfByte1.length * 3];
      int j = 0;
      int k;
      while ((k = localGZIPInputStream.read()) >= 0) {
        arrayOfByte2[(j++)] = ((byte)k);
      }
      arrayOfByte1 = new byte[j];
      System.arraycopy(arrayOfByte2, 0, arrayOfByte1, 0, j);
    }
    return arrayOfByte1;
  }
  
  public static final String convertString(String paramString)
  {
    char[] arrayOfChar = paramString.toCharArray();
    StringBuffer localStringBuffer = new StringBuffer();
    for (int i = 0; i < arrayOfChar.length; i++) {
      switch (arrayOfChar[i])
      {
      case '\n': 
        localStringBuffer.append("\\n");
        break;
      case '\r': 
        localStringBuffer.append("\\r");
        break;
      case '"': 
        localStringBuffer.append("\\\"");
        break;
      case '\'': 
        localStringBuffer.append("\\'");
        break;
      case '\\': 
        localStringBuffer.append("\\\\");
        break;
      default: 
        localStringBuffer.append(arrayOfChar[i]);
      }
    }
    return localStringBuffer.toString();
  }
  
  static
  {
    int i = 0;
    int j = 0;
    for (int k = 65; k <= 90; k++)
    {
      CHAR_MAP[i] = k;
      MAP_CHAR[k] = i;
      i++;
    }
    for (k = 103; k <= 122; k++)
    {
      CHAR_MAP[i] = k;
      MAP_CHAR[k] = i;
      i++;
    }
    CHAR_MAP[i] = 36;
    MAP_CHAR[36] = i;
    i++;
    CHAR_MAP[i] = 95;
    MAP_CHAR[95] = i;
  }
  
  private static class JavaReader
    extends FilterReader
  {
    public JavaReader(Reader paramReader)
    {
      super();
    }
    
    public int read()
      throws IOException
    {
      int i = in.read();
      if (i != 36) {
        return i;
      }
      int j = in.read();
      if (j < 0) {
        return -1;
      }
      if (((j >= 48) && (j <= 57)) || ((j >= 97) && (j <= 102)))
      {
        int k = in.read();
        if (k < 0) {
          return -1;
        }
        char[] arrayOfChar = { (char)j, (char)k };
        int m = Integer.parseInt(new String(arrayOfChar), 16);
        return m;
      }
      return Utility.MAP_CHAR[j];
    }
    
    public int read(char[] paramArrayOfChar, int paramInt1, int paramInt2)
      throws IOException
    {
      for (int i = 0; i < paramInt2; i++) {
        paramArrayOfChar[(paramInt1 + i)] = ((char)read());
      }
      return paramInt2;
    }
  }
  
  private static class JavaWriter
    extends FilterWriter
  {
    public JavaWriter(Writer paramWriter)
    {
      super();
    }
    
    public void write(int paramInt)
      throws IOException
    {
      if ((Utility.isJavaIdentifierPart((char)paramInt)) && (paramInt != 36))
      {
        out.write(paramInt);
      }
      else
      {
        out.write(36);
        if ((paramInt >= 0) && (paramInt < 48))
        {
          out.write(Utility.CHAR_MAP[paramInt]);
        }
        else
        {
          char[] arrayOfChar = Integer.toHexString(paramInt).toCharArray();
          if (arrayOfChar.length == 1)
          {
            out.write(48);
            out.write(arrayOfChar[0]);
          }
          else
          {
            out.write(arrayOfChar[0]);
            out.write(arrayOfChar[1]);
          }
        }
      }
    }
    
    public void write(char[] paramArrayOfChar, int paramInt1, int paramInt2)
      throws IOException
    {
      for (int i = 0; i < paramInt2; i++) {
        write(paramArrayOfChar[(paramInt1 + i)]);
      }
    }
    
    public void write(String paramString, int paramInt1, int paramInt2)
      throws IOException
    {
      write(paramString.toCharArray(), paramInt1, paramInt2);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\bcel\internal\classfile\Utility.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */