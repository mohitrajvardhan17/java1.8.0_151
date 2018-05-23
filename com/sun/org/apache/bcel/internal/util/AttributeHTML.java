package com.sun.org.apache.bcel.internal.util;

import com.sun.org.apache.bcel.internal.Constants;
import com.sun.org.apache.bcel.internal.classfile.Attribute;
import com.sun.org.apache.bcel.internal.classfile.Code;
import com.sun.org.apache.bcel.internal.classfile.CodeException;
import com.sun.org.apache.bcel.internal.classfile.ConstantPool;
import com.sun.org.apache.bcel.internal.classfile.ConstantUtf8;
import com.sun.org.apache.bcel.internal.classfile.ConstantValue;
import com.sun.org.apache.bcel.internal.classfile.ExceptionTable;
import com.sun.org.apache.bcel.internal.classfile.InnerClass;
import com.sun.org.apache.bcel.internal.classfile.InnerClasses;
import com.sun.org.apache.bcel.internal.classfile.LineNumber;
import com.sun.org.apache.bcel.internal.classfile.LineNumberTable;
import com.sun.org.apache.bcel.internal.classfile.LocalVariable;
import com.sun.org.apache.bcel.internal.classfile.LocalVariableTable;
import com.sun.org.apache.bcel.internal.classfile.SourceFile;
import com.sun.org.apache.bcel.internal.classfile.Utility;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

final class AttributeHTML
  implements Constants
{
  private String class_name;
  private PrintWriter file;
  private int attr_count = 0;
  private ConstantHTML constant_html;
  private ConstantPool constant_pool;
  
  AttributeHTML(String paramString1, String paramString2, ConstantPool paramConstantPool, ConstantHTML paramConstantHTML)
    throws IOException
  {
    class_name = paramString2;
    constant_pool = paramConstantPool;
    constant_html = paramConstantHTML;
    file = new PrintWriter(new FileOutputStream(paramString1 + paramString2 + "_attributes.html"));
    file.println("<HTML><BODY BGCOLOR=\"#C0C0C0\"><TABLE BORDER=0>");
  }
  
  private final String codeLink(int paramInt1, int paramInt2)
  {
    return "<A HREF=\"" + class_name + "_code.html#code" + paramInt2 + "@" + paramInt1 + "\" TARGET=Code>" + paramInt1 + "</A>";
  }
  
  final void close()
  {
    file.println("</TABLE></BODY></HTML>");
    file.close();
  }
  
  final void writeAttribute(Attribute paramAttribute, String paramString)
    throws IOException
  {
    writeAttribute(paramAttribute, paramString, 0);
  }
  
  final void writeAttribute(Attribute paramAttribute, String paramString, int paramInt)
    throws IOException
  {
    int i = paramAttribute.getTag();
    if (i == -1) {
      return;
    }
    attr_count += 1;
    if (attr_count % 2 == 0) {
      file.print("<TR BGCOLOR=\"#C0C0C0\"><TD>");
    } else {
      file.print("<TR BGCOLOR=\"#A0A0A0\"><TD>");
    }
    file.println("<H4><A NAME=\"" + paramString + "\">" + attr_count + " " + ATTRIBUTE_NAMES[i] + "</A></H4>");
    int n;
    int j;
    switch (i)
    {
    case 2: 
      Code localCode = (Code)paramAttribute;
      file.print("<UL><LI>Maximum stack size = " + localCode.getMaxStack() + "</LI>\n<LI>Number of local variables = " + localCode.getMaxLocals() + "</LI>\n<LI><A HREF=\"" + class_name + "_code.html#method" + paramInt + "\" TARGET=Code>Byte code</A></LI></UL>\n");
      CodeException[] arrayOfCodeException = localCode.getExceptionTable();
      int k = arrayOfCodeException.length;
      if (k > 0)
      {
        file.print("<P><B>Exceptions handled</B><UL>");
        for (int m = 0; m < k; m++)
        {
          n = arrayOfCodeException[m].getCatchType();
          file.print("<LI>");
          if (n != 0) {
            file.print(constant_html.referenceConstant(n));
          } else {
            file.print("Any Exception");
          }
          file.print("<BR>(Ranging from lines " + codeLink(arrayOfCodeException[m].getStartPC(), paramInt) + " to " + codeLink(arrayOfCodeException[m].getEndPC(), paramInt) + ", handled at line " + codeLink(arrayOfCodeException[m].getHandlerPC(), paramInt) + ")</LI>");
        }
        file.print("</UL>");
      }
      break;
    case 1: 
      j = ((ConstantValue)paramAttribute).getConstantValueIndex();
      file.print("<UL><LI><A HREF=\"" + class_name + "_cp.html#cp" + j + "\" TARGET=\"ConstantPool\">Constant value index(" + j + ")</A></UL>\n");
      break;
    case 0: 
      j = ((SourceFile)paramAttribute).getSourceFileIndex();
      file.print("<UL><LI><A HREF=\"" + class_name + "_cp.html#cp" + j + "\" TARGET=\"ConstantPool\">Source file index(" + j + ")</A></UL>\n");
      break;
    case 3: 
      int[] arrayOfInt = ((ExceptionTable)paramAttribute).getExceptionIndexTable();
      file.print("<UL>");
      for (n = 0; n < arrayOfInt.length; n++) {
        file.print("<LI><A HREF=\"" + class_name + "_cp.html#cp" + arrayOfInt[n] + "\" TARGET=\"ConstantPool\">Exception class index(" + arrayOfInt[n] + ")</A>\n");
      }
      file.print("</UL>\n");
      break;
    case 4: 
      LineNumber[] arrayOfLineNumber = ((LineNumberTable)paramAttribute).getLineNumberTable();
      file.print("<P>");
      for (int i1 = 0; i1 < arrayOfLineNumber.length; i1++)
      {
        file.print("(" + arrayOfLineNumber[i1].getStartPC() + ",&nbsp;" + arrayOfLineNumber[i1].getLineNumber() + ")");
        if (i1 < arrayOfLineNumber.length - 1) {
          file.print(", ");
        }
      }
      break;
    case 5: 
      LocalVariable[] arrayOfLocalVariable = ((LocalVariableTable)paramAttribute).getLocalVariableTable();
      file.print("<UL>");
      for (int i2 = 0; i2 < arrayOfLocalVariable.length; i2++)
      {
        j = arrayOfLocalVariable[i2].getSignatureIndex();
        String str1 = ((ConstantUtf8)constant_pool.getConstant(j, (byte)1)).getBytes();
        str1 = Utility.signatureToString(str1, false);
        int i4 = arrayOfLocalVariable[i2].getStartPC();
        int i5 = i4 + arrayOfLocalVariable[i2].getLength();
        file.println("<LI>" + Class2HTML.referenceType(str1) + "&nbsp;<B>" + arrayOfLocalVariable[i2].getName() + "</B> in slot %" + arrayOfLocalVariable[i2].getIndex() + "<BR>Valid from lines <A HREF=\"" + class_name + "_code.html#code" + paramInt + "@" + i4 + "\" TARGET=Code>" + i4 + "</A> to <A HREF=\"" + class_name + "_code.html#code" + paramInt + "@" + i5 + "\" TARGET=Code>" + i5 + "</A></LI>");
      }
      file.print("</UL>\n");
      break;
    case 6: 
      InnerClass[] arrayOfInnerClass = ((InnerClasses)paramAttribute).getInnerClasses();
      file.print("<UL>");
      for (int i3 = 0; i3 < arrayOfInnerClass.length; i3++)
      {
        j = arrayOfInnerClass[i3].getInnerNameIndex();
        String str2;
        if (j > 0) {
          str2 = ((ConstantUtf8)constant_pool.getConstant(j, (byte)1)).getBytes();
        } else {
          str2 = "&lt;anonymous&gt;";
        }
        String str3 = Utility.accessToString(arrayOfInnerClass[i3].getInnerAccessFlags());
        file.print("<LI><FONT COLOR=\"#FF0000\">" + str3 + "</FONT> " + constant_html.referenceConstant(arrayOfInnerClass[i3].getInnerClassIndex()) + " in&nbsp;class " + constant_html.referenceConstant(arrayOfInnerClass[i3].getOuterClassIndex()) + " named " + str2 + "</LI>\n");
      }
      file.print("</UL>\n");
      break;
    default: 
      file.print("<P>" + paramAttribute.toString());
    }
    file.println("</TD></TR>");
    file.flush();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\bcel\internal\util\AttributeHTML.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */