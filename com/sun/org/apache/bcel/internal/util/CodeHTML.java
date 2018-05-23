package com.sun.org.apache.bcel.internal.util;

import com.sun.org.apache.bcel.internal.Constants;
import com.sun.org.apache.bcel.internal.classfile.Attribute;
import com.sun.org.apache.bcel.internal.classfile.Code;
import com.sun.org.apache.bcel.internal.classfile.CodeException;
import com.sun.org.apache.bcel.internal.classfile.Constant;
import com.sun.org.apache.bcel.internal.classfile.ConstantFieldref;
import com.sun.org.apache.bcel.internal.classfile.ConstantInterfaceMethodref;
import com.sun.org.apache.bcel.internal.classfile.ConstantMethodref;
import com.sun.org.apache.bcel.internal.classfile.ConstantNameAndType;
import com.sun.org.apache.bcel.internal.classfile.ConstantPool;
import com.sun.org.apache.bcel.internal.classfile.LocalVariable;
import com.sun.org.apache.bcel.internal.classfile.LocalVariableTable;
import com.sun.org.apache.bcel.internal.classfile.Method;
import com.sun.org.apache.bcel.internal.classfile.Utility;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.BitSet;

final class CodeHTML
  implements Constants
{
  private String class_name;
  private Method[] methods;
  private PrintWriter file;
  private BitSet goto_set;
  private ConstantPool constant_pool;
  private ConstantHTML constant_html;
  private static boolean wide = false;
  
  CodeHTML(String paramString1, String paramString2, Method[] paramArrayOfMethod, ConstantPool paramConstantPool, ConstantHTML paramConstantHTML)
    throws IOException
  {
    class_name = paramString2;
    methods = paramArrayOfMethod;
    constant_pool = paramConstantPool;
    constant_html = paramConstantHTML;
    file = new PrintWriter(new FileOutputStream(paramString1 + paramString2 + "_code.html"));
    file.println("<HTML><BODY BGCOLOR=\"#C0C0C0\">");
    for (int i = 0; i < paramArrayOfMethod.length; i++) {
      writeMethod(paramArrayOfMethod[i], i);
    }
    file.println("</BODY></HTML>");
    file.close();
  }
  
  private final String codeToHTML(ByteSequence paramByteSequence, int paramInt)
    throws IOException
  {
    int i = (short)paramByteSequence.readUnsignedByte();
    int j = 0;
    int i4 = 0;
    StringBuffer localStringBuffer = new StringBuffer("<TT>" + OPCODE_NAMES[i] + "</TT></TD><TD>");
    int i6;
    int i7;
    if ((i == 170) || (i == 171))
    {
      i6 = paramByteSequence.getIndex() % 4;
      i4 = i6 == 0 ? 0 : 4 - i6;
      for (i7 = 0; i7 < i4; i7++) {
        paramByteSequence.readByte();
      }
      j = paramByteSequence.readInt();
    }
    int i5;
    int[] arrayOfInt;
    int n;
    int i2;
    int i1;
    String str1;
    int i12;
    switch (i)
    {
    case 170: 
      int k = paramByteSequence.readInt();
      int m = paramByteSequence.readInt();
      i5 = paramByteSequence.getIndex() - 12 - i4 - 1;
      j += i5;
      localStringBuffer.append("<TABLE BORDER=1><TR>");
      arrayOfInt = new int[m - k + 1];
      for (i6 = 0; i6 < arrayOfInt.length; i6++)
      {
        arrayOfInt[i6] = (i5 + paramByteSequence.readInt());
        localStringBuffer.append("<TH>" + (k + i6) + "</TH>");
      }
      localStringBuffer.append("<TH>default</TH></TR>\n<TR>");
      for (i6 = 0; i6 < arrayOfInt.length; i6++) {
        localStringBuffer.append("<TD><A HREF=\"#code" + paramInt + "@" + arrayOfInt[i6] + "\">" + arrayOfInt[i6] + "</A></TD>");
      }
      localStringBuffer.append("<TD><A HREF=\"#code" + paramInt + "@" + j + "\">" + j + "</A></TD></TR>\n</TABLE>\n");
      break;
    case 171: 
      i6 = paramByteSequence.readInt();
      i5 = paramByteSequence.getIndex() - 8 - i4 - 1;
      arrayOfInt = new int[i6];
      j += i5;
      localStringBuffer.append("<TABLE BORDER=1><TR>");
      for (i7 = 0; i7 < i6; i7++)
      {
        int i8 = paramByteSequence.readInt();
        arrayOfInt[i7] = (i5 + paramByteSequence.readInt());
        localStringBuffer.append("<TH>" + i8 + "</TH>");
      }
      localStringBuffer.append("<TH>default</TH></TR>\n<TR>");
      for (i7 = 0; i7 < i6; i7++) {
        localStringBuffer.append("<TD><A HREF=\"#code" + paramInt + "@" + arrayOfInt[i7] + "\">" + arrayOfInt[i7] + "</A></TD>");
      }
      localStringBuffer.append("<TD><A HREF=\"#code" + paramInt + "@" + j + "\">" + j + "</A></TD></TR>\n</TABLE>\n");
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
      n = paramByteSequence.getIndex() + paramByteSequence.readShort() - 1;
      localStringBuffer.append("<A HREF=\"#code" + paramInt + "@" + n + "\">" + n + "</A>");
      break;
    case 200: 
    case 201: 
      i7 = paramByteSequence.getIndex() + paramByteSequence.readInt() - 1;
      localStringBuffer.append("<A HREF=\"#code" + paramInt + "@" + i7 + "\">" + i7 + "</A>");
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
        i2 = paramByteSequence.readShort();
        wide = false;
      }
      else
      {
        i2 = paramByteSequence.readUnsignedByte();
      }
      localStringBuffer.append("%" + i2);
      break;
    case 196: 
      wide = true;
      localStringBuffer.append("(wide)");
      break;
    case 188: 
      localStringBuffer.append("<FONT COLOR=\"#00FF00\">" + TYPE_NAMES[paramByteSequence.readByte()] + "</FONT>");
      break;
    case 178: 
    case 179: 
    case 180: 
    case 181: 
      n = paramByteSequence.readShort();
      ConstantFieldref localConstantFieldref = (ConstantFieldref)constant_pool.getConstant(n, (byte)9);
      i1 = localConstantFieldref.getClassIndex();
      str1 = constant_pool.getConstantString(i1, (byte)7);
      str1 = Utility.compactClassName(str1, false);
      n = localConstantFieldref.getNameAndTypeIndex();
      String str3 = constant_pool.constantToString(n, (byte)12);
      if (str1.equals(class_name)) {
        localStringBuffer.append("<A HREF=\"" + class_name + "_methods.html#field" + str3 + "\" TARGET=Methods>" + str3 + "</A>\n");
      } else {
        localStringBuffer.append(constant_html.referenceConstant(i1) + "." + str3);
      }
      break;
    case 187: 
    case 192: 
    case 193: 
      n = paramByteSequence.readShort();
      localStringBuffer.append(constant_html.referenceConstant(n));
      break;
    case 182: 
    case 183: 
    case 184: 
    case 185: 
      int i9 = paramByteSequence.readShort();
      if (i == 185)
      {
        int i10 = paramByteSequence.readUnsignedByte();
        int i11 = paramByteSequence.readUnsignedByte();
        localObject2 = (ConstantInterfaceMethodref)constant_pool.getConstant(i9, (byte)11);
        i1 = ((ConstantInterfaceMethodref)localObject2).getClassIndex();
        str4 = constant_pool.constantToString((Constant)localObject2);
        n = ((ConstantInterfaceMethodref)localObject2).getNameAndTypeIndex();
      }
      else
      {
        localObject1 = (ConstantMethodref)constant_pool.getConstant(i9, (byte)10);
        i1 = ((ConstantMethodref)localObject1).getClassIndex();
        str4 = constant_pool.constantToString((Constant)localObject1);
        n = ((ConstantMethodref)localObject1).getNameAndTypeIndex();
      }
      str1 = Class2HTML.referenceClass(i1);
      String str4 = Class2HTML.toHTML(constant_pool.constantToString(constant_pool.getConstant(n, (byte)12)));
      Object localObject1 = (ConstantNameAndType)constant_pool.getConstant(n, (byte)12);
      String str2 = constant_pool.constantToString(((ConstantNameAndType)localObject1).getSignatureIndex(), (byte)1);
      String[] arrayOfString = Utility.methodSignatureArgumentTypes(str2, false);
      Object localObject2 = Utility.methodSignatureReturnType(str2, false);
      localStringBuffer.append(str1 + ".<A HREF=\"" + class_name + "_cp.html#cp" + i9 + "\" TARGET=ConstantPool>" + str4 + "</A>(");
      for (i12 = 0; i12 < arrayOfString.length; i12++)
      {
        localStringBuffer.append(Class2HTML.referenceType(arrayOfString[i12]));
        if (i12 < arrayOfString.length - 1) {
          localStringBuffer.append(", ");
        }
      }
      localStringBuffer.append("):" + Class2HTML.referenceType((String)localObject2));
      break;
    case 19: 
    case 20: 
      n = paramByteSequence.readShort();
      localStringBuffer.append("<A HREF=\"" + class_name + "_cp.html#cp" + n + "\" TARGET=\"ConstantPool\">" + Class2HTML.toHTML(constant_pool.constantToString(n, constant_pool.getConstant(n).getTag())) + "</a>");
      break;
    case 18: 
      n = paramByteSequence.readUnsignedByte();
      localStringBuffer.append("<A HREF=\"" + class_name + "_cp.html#cp" + n + "\" TARGET=\"ConstantPool\">" + Class2HTML.toHTML(constant_pool.constantToString(n, constant_pool.getConstant(n).getTag())) + "</a>");
      break;
    case 189: 
      n = paramByteSequence.readShort();
      localStringBuffer.append(constant_html.referenceConstant(n));
      break;
    case 197: 
      n = paramByteSequence.readShort();
      i12 = paramByteSequence.readByte();
      localStringBuffer.append(constant_html.referenceConstant(n) + ":" + i12 + "-dimensional");
      break;
    case 132: 
      int i3;
      if (wide)
      {
        i2 = paramByteSequence.readShort();
        i3 = paramByteSequence.readShort();
        wide = false;
      }
      else
      {
        i2 = paramByteSequence.readUnsignedByte();
        i3 = paramByteSequence.readByte();
      }
      localStringBuffer.append("%" + i2 + " " + i3);
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
      if (NO_OF_OPERANDS[i] > 0) {
        for (int i13 = 0; i13 < TYPE_OF_OPERANDS[i].length; i13++)
        {
          switch (TYPE_OF_OPERANDS[i][i13])
          {
          case 8: 
            localStringBuffer.append(paramByteSequence.readUnsignedByte());
            break;
          case 9: 
            localStringBuffer.append(paramByteSequence.readShort());
            break;
          case 10: 
            localStringBuffer.append(paramByteSequence.readInt());
            break;
          default: 
            System.err.println("Unreachable default case reached!");
            System.exit(-1);
          }
          localStringBuffer.append("&nbsp;");
        }
      }
      break;
    }
    localStringBuffer.append("</TD>");
    return localStringBuffer.toString();
  }
  
  private final void findGotos(ByteSequence paramByteSequence, Method paramMethod, Code paramCode)
    throws IOException
  {
    goto_set = new BitSet(paramByteSequence.available());
    int m;
    int i2;
    int i4;
    int i5;
    int i6;
    if (paramCode != null)
    {
      CodeException[] arrayOfCodeException = paramCode.getExceptionTable();
      m = arrayOfCodeException.length;
      for (int n = 0; n < m; n++)
      {
        goto_set.set(arrayOfCodeException[n].getStartPC());
        goto_set.set(arrayOfCodeException[n].getEndPC());
        goto_set.set(arrayOfCodeException[n].getHandlerPC());
      }
      Attribute[] arrayOfAttribute = paramCode.getAttributes();
      for (i2 = 0; i2 < arrayOfAttribute.length; i2++) {
        if (arrayOfAttribute[i2].getTag() == 5)
        {
          LocalVariable[] arrayOfLocalVariable = ((LocalVariableTable)arrayOfAttribute[i2]).getLocalVariableTable();
          for (i4 = 0; i4 < arrayOfLocalVariable.length; i4++)
          {
            i5 = arrayOfLocalVariable[i4].getStartPC();
            i6 = i5 + arrayOfLocalVariable[i4].getLength();
            goto_set.set(i5);
            goto_set.set(i6);
          }
          break;
        }
      }
    }
    for (int k = 0; paramByteSequence.available() > 0; k++)
    {
      int j = paramByteSequence.readUnsignedByte();
      int i;
      switch (j)
      {
      case 170: 
      case 171: 
        m = paramByteSequence.getIndex() % 4;
        int i1 = m == 0 ? 0 : 4 - m;
        for (i4 = 0; i4 < i1; i4++) {
          paramByteSequence.readByte();
        }
        i2 = paramByteSequence.readInt();
        int i3;
        if (j == 170)
        {
          i4 = paramByteSequence.readInt();
          i5 = paramByteSequence.readInt();
          i3 = paramByteSequence.getIndex() - 12 - i1 - 1;
          i2 += i3;
          goto_set.set(i2);
          for (i6 = 0; i6 < i5 - i4 + 1; i6++)
          {
            i = i3 + paramByteSequence.readInt();
            goto_set.set(i);
          }
        }
        else
        {
          i4 = paramByteSequence.readInt();
          i3 = paramByteSequence.getIndex() - 8 - i1 - 1;
          i2 += i3;
          goto_set.set(i2);
          for (i5 = 0; i5 < i4; i5++)
          {
            i6 = paramByteSequence.readInt();
            i = i3 + paramByteSequence.readInt();
            goto_set.set(i);
          }
        }
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
        i = paramByteSequence.getIndex() + paramByteSequence.readShort() - 1;
        goto_set.set(i);
        break;
      case 200: 
      case 201: 
        i = paramByteSequence.getIndex() + paramByteSequence.readInt() - 1;
        goto_set.set(i);
        break;
      case 169: 
      case 172: 
      case 173: 
      case 174: 
      case 175: 
      case 176: 
      case 177: 
      case 178: 
      case 179: 
      case 180: 
      case 181: 
      case 182: 
      case 183: 
      case 184: 
      case 185: 
      case 186: 
      case 187: 
      case 188: 
      case 189: 
      case 190: 
      case 191: 
      case 192: 
      case 193: 
      case 194: 
      case 195: 
      case 196: 
      case 197: 
      default: 
        paramByteSequence.unreadByte();
        codeToHTML(paramByteSequence, 0);
      }
    }
  }
  
  private void writeMethod(Method paramMethod, int paramInt)
    throws IOException
  {
    String str1 = paramMethod.getSignature();
    String[] arrayOfString = Utility.methodSignatureArgumentTypes(str1, false);
    String str2 = Utility.methodSignatureReturnType(str1, false);
    String str3 = paramMethod.getName();
    String str4 = Class2HTML.toHTML(str3);
    String str5 = Utility.accessToString(paramMethod.getAccessFlags());
    str5 = Utility.replace(str5, " ", "&nbsp;");
    Attribute[] arrayOfAttribute1 = paramMethod.getAttributes();
    file.print("<P><B><FONT COLOR=\"#FF0000\">" + str5 + "</FONT>&nbsp;<A NAME=method" + paramInt + ">" + Class2HTML.referenceType(str2) + "</A>&nbsp<A HREF=\"" + class_name + "_methods.html#method" + paramInt + "\" TARGET=Methods>" + str4 + "</A>(");
    for (int i = 0; i < arrayOfString.length; i++)
    {
      file.print(Class2HTML.referenceType(arrayOfString[i]));
      if (i < arrayOfString.length - 1) {
        file.print(",&nbsp;");
      }
    }
    file.println(")</B></P>");
    Code localCode = null;
    byte[] arrayOfByte = null;
    int k;
    if (arrayOfAttribute1.length > 0)
    {
      file.print("<H4>Attributes</H4><UL>\n");
      for (int j = 0; j < arrayOfAttribute1.length; j++)
      {
        k = arrayOfAttribute1[j].getTag();
        if (k != -1) {
          file.print("<LI><A HREF=\"" + class_name + "_attributes.html#method" + paramInt + "@" + j + "\" TARGET=Attributes>" + ATTRIBUTE_NAMES[k] + "</A></LI>\n");
        } else {
          file.print("<LI>" + arrayOfAttribute1[j] + "</LI>");
        }
        if (k == 2)
        {
          localCode = (Code)arrayOfAttribute1[j];
          Attribute[] arrayOfAttribute2 = localCode.getAttributes();
          arrayOfByte = localCode.getCode();
          file.print("<UL>");
          for (int n = 0; n < arrayOfAttribute2.length; n++)
          {
            k = arrayOfAttribute2[n].getTag();
            file.print("<LI><A HREF=\"" + class_name + "_attributes.html#method" + paramInt + "@" + j + "@" + n + "\" TARGET=Attributes>" + ATTRIBUTE_NAMES[k] + "</A></LI>\n");
          }
          file.print("</UL>");
        }
      }
      file.println("</UL>");
    }
    if (arrayOfByte != null)
    {
      ByteSequence localByteSequence = new ByteSequence(arrayOfByte);
      localByteSequence.mark(localByteSequence.available());
      findGotos(localByteSequence, paramMethod, localCode);
      localByteSequence.reset();
      file.println("<TABLE BORDER=0><TR><TH ALIGN=LEFT>Byte<BR>offset</TH><TH ALIGN=LEFT>Instruction</TH><TH ALIGN=LEFT>Argument</TH>");
      for (k = 0; localByteSequence.available() > 0; k++)
      {
        int m = localByteSequence.getIndex();
        String str6 = codeToHTML(localByteSequence, paramInt);
        String str7 = "";
        if (goto_set.get(m)) {
          str7 = "<A NAME=code" + paramInt + "@" + m + "></A>";
        }
        String str8;
        if (localByteSequence.getIndex() == arrayOfByte.length) {
          str8 = "<A NAME=code" + paramInt + "@" + arrayOfByte.length + ">" + m + "</A>";
        } else {
          str8 = "" + m;
        }
        file.println("<TR VALIGN=TOP><TD>" + str8 + "</TD><TD>" + str7 + str6 + "</TR>");
      }
      file.println("<TR><TD> </A></TD></TR>");
      file.println("</TABLE>");
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\bcel\internal\util\CodeHTML.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */