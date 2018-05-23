package com.sun.org.apache.bcel.internal.util;

import com.sun.org.apache.bcel.internal.Constants;
import com.sun.org.apache.bcel.internal.classfile.Constant;
import com.sun.org.apache.bcel.internal.classfile.ConstantClass;
import com.sun.org.apache.bcel.internal.classfile.ConstantFieldref;
import com.sun.org.apache.bcel.internal.classfile.ConstantInterfaceMethodref;
import com.sun.org.apache.bcel.internal.classfile.ConstantMethodref;
import com.sun.org.apache.bcel.internal.classfile.ConstantNameAndType;
import com.sun.org.apache.bcel.internal.classfile.ConstantPool;
import com.sun.org.apache.bcel.internal.classfile.ConstantString;
import com.sun.org.apache.bcel.internal.classfile.Method;
import com.sun.org.apache.bcel.internal.classfile.Utility;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

final class ConstantHTML
  implements Constants
{
  private String class_name;
  private String class_package;
  private ConstantPool constant_pool;
  private PrintWriter file;
  private String[] constant_ref;
  private Constant[] constants;
  private Method[] methods;
  
  ConstantHTML(String paramString1, String paramString2, String paramString3, Method[] paramArrayOfMethod, ConstantPool paramConstantPool)
    throws IOException
  {
    class_name = paramString2;
    class_package = paramString3;
    constant_pool = paramConstantPool;
    methods = paramArrayOfMethod;
    constants = paramConstantPool.getConstantPool();
    file = new PrintWriter(new FileOutputStream(paramString1 + paramString2 + "_cp.html"));
    constant_ref = new String[constants.length];
    constant_ref[0] = "&lt;unknown&gt;";
    file.println("<HTML><BODY BGCOLOR=\"#C0C0C0\"><TABLE BORDER=0>");
    for (int i = 1; i < constants.length; i++)
    {
      if (i % 2 == 0) {
        file.print("<TR BGCOLOR=\"#C0C0C0\"><TD>");
      } else {
        file.print("<TR BGCOLOR=\"#A0A0A0\"><TD>");
      }
      if (constants[i] != null) {
        writeConstant(i);
      }
      file.print("</TD></TR>\n");
    }
    file.println("</TABLE></BODY></HTML>");
    file.close();
  }
  
  String referenceConstant(int paramInt)
  {
    return constant_ref[paramInt];
  }
  
  private void writeConstant(int paramInt)
  {
    byte b = constants[paramInt].getTag();
    file.println("<H4> <A NAME=cp" + paramInt + ">" + paramInt + "</A> " + CONSTANT_NAMES[b] + "</H4>");
    int i;
    int j;
    String str1;
    switch (b)
    {
    case 10: 
    case 11: 
      if (b == 10)
      {
        localObject = (ConstantMethodref)constant_pool.getConstant(paramInt, (byte)10);
        i = ((ConstantMethodref)localObject).getClassIndex();
        j = ((ConstantMethodref)localObject).getNameAndTypeIndex();
      }
      else
      {
        localObject = (ConstantInterfaceMethodref)constant_pool.getConstant(paramInt, (byte)11);
        i = ((ConstantInterfaceMethodref)localObject).getClassIndex();
        j = ((ConstantInterfaceMethodref)localObject).getNameAndTypeIndex();
      }
      Object localObject = constant_pool.constantToString(j, (byte)12);
      String str2 = Class2HTML.toHTML((String)localObject);
      String str3 = constant_pool.constantToString(i, (byte)7);
      String str4 = Utility.compactClassName(str3);
      str4 = Utility.compactClassName(str3);
      str4 = Utility.compactClassName(str4, class_package + ".", true);
      ConstantNameAndType localConstantNameAndType1 = (ConstantNameAndType)constant_pool.getConstant(j, (byte)12);
      String str5 = constant_pool.constantToString(localConstantNameAndType1.getSignatureIndex(), (byte)1);
      String[] arrayOfString = Utility.methodSignatureArgumentTypes(str5, false);
      String str6 = Utility.methodSignatureReturnType(str5, false);
      String str7 = Class2HTML.referenceType(str6);
      StringBuffer localStringBuffer = new StringBuffer("(");
      for (int k = 0; k < arrayOfString.length; k++)
      {
        localStringBuffer.append(Class2HTML.referenceType(arrayOfString[k]));
        if (k < arrayOfString.length - 1) {
          localStringBuffer.append(",&nbsp;");
        }
      }
      localStringBuffer.append(")");
      String str8 = localStringBuffer.toString();
      if (str3.equals(class_name)) {
        str1 = "<A HREF=\"" + class_name + "_code.html#method" + getMethodNumber(new StringBuilder().append((String)localObject).append(str5).toString()) + "\" TARGET=Code>" + str2 + "</A>";
      } else {
        str1 = "<A HREF=\"" + str3 + ".html\" TARGET=_top>" + str4 + "</A>." + str2;
      }
      constant_ref[paramInt] = (str7 + "&nbsp;<A HREF=\"" + class_name + "_cp.html#cp" + i + "\" TARGET=Constants>" + str4 + "</A>.<A HREF=\"" + class_name + "_cp.html#cp" + paramInt + "\" TARGET=ConstantPool>" + str2 + "</A>&nbsp;" + str8);
      file.println("<P><TT>" + str7 + "&nbsp;" + str1 + str8 + "&nbsp;</TT>\n<UL><LI><A HREF=\"#cp" + i + "\">Class index(" + i + ")</A>\n<LI><A HREF=\"#cp" + j + "\">NameAndType index(" + j + ")</A></UL>");
      break;
    case 9: 
      ConstantFieldref localConstantFieldref = (ConstantFieldref)constant_pool.getConstant(paramInt, (byte)9);
      i = localConstantFieldref.getClassIndex();
      j = localConstantFieldref.getNameAndTypeIndex();
      String str9 = constant_pool.constantToString(i, (byte)7);
      String str10 = Utility.compactClassName(str9);
      str10 = Utility.compactClassName(str10, class_package + ".", true);
      String str11 = constant_pool.constantToString(j, (byte)12);
      if (str9.equals(class_name)) {
        str1 = "<A HREF=\"" + str9 + "_methods.html#field" + str11 + "\" TARGET=Methods>" + str11 + "</A>";
      } else {
        str1 = "<A HREF=\"" + str9 + ".html\" TARGET=_top>" + str10 + "</A>." + str11 + "\n";
      }
      constant_ref[paramInt] = ("<A HREF=\"" + class_name + "_cp.html#cp" + i + "\" TARGET=Constants>" + str10 + "</A>.<A HREF=\"" + class_name + "_cp.html#cp" + paramInt + "\" TARGET=ConstantPool>" + str11 + "</A>");
      file.println("<P><TT>" + str1 + "</TT><BR>\n<UL><LI><A HREF=\"#cp" + i + "\">Class(" + i + ")</A><BR>\n<LI><A HREF=\"#cp" + j + "\">NameAndType(" + j + ")</A></UL>");
      break;
    case 7: 
      ConstantClass localConstantClass = (ConstantClass)constant_pool.getConstant(paramInt, (byte)7);
      j = localConstantClass.getNameIndex();
      String str12 = constant_pool.constantToString(paramInt, b);
      String str13 = Utility.compactClassName(str12);
      str13 = Utility.compactClassName(str13, class_package + ".", true);
      str1 = "<A HREF=\"" + str12 + ".html\" TARGET=_top>" + str13 + "</A>";
      constant_ref[paramInt] = ("<A HREF=\"" + class_name + "_cp.html#cp" + paramInt + "\" TARGET=ConstantPool>" + str13 + "</A>");
      file.println("<P><TT>" + str1 + "</TT><UL><LI><A HREF=\"#cp" + j + "\">Name index(" + j + ")</A></UL>\n");
      break;
    case 8: 
      ConstantString localConstantString = (ConstantString)constant_pool.getConstant(paramInt, (byte)8);
      j = localConstantString.getStringIndex();
      String str14 = Class2HTML.toHTML(constant_pool.constantToString(paramInt, b));
      file.println("<P><TT>" + str14 + "</TT><UL><LI><A HREF=\"#cp" + j + "\">Name index(" + j + ")</A></UL>\n");
      break;
    case 12: 
      ConstantNameAndType localConstantNameAndType2 = (ConstantNameAndType)constant_pool.getConstant(paramInt, (byte)12);
      j = localConstantNameAndType2.getNameIndex();
      int m = localConstantNameAndType2.getSignatureIndex();
      file.println("<P><TT>" + Class2HTML.toHTML(constant_pool.constantToString(paramInt, b)) + "</TT><UL><LI><A HREF=\"#cp" + j + "\">Name index(" + j + ")</A>\n<LI><A HREF=\"#cp" + m + "\">Signature index(" + m + ")</A></UL>\n");
      break;
    default: 
      file.println("<P><TT>" + Class2HTML.toHTML(constant_pool.constantToString(paramInt, b)) + "</TT>\n");
    }
  }
  
  private final int getMethodNumber(String paramString)
  {
    for (int i = 0; i < methods.length; i++)
    {
      String str = methods[i].getName() + methods[i].getSignature();
      if (str.equals(paramString)) {
        return i;
      }
    }
    return -1;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\bcel\internal\util\ConstantHTML.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */