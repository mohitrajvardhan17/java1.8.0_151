package com.sun.org.apache.bcel.internal.util;

import com.sun.org.apache.bcel.internal.Constants;
import com.sun.org.apache.bcel.internal.classfile.Attribute;
import com.sun.org.apache.bcel.internal.classfile.Code;
import com.sun.org.apache.bcel.internal.classfile.ConstantValue;
import com.sun.org.apache.bcel.internal.classfile.ExceptionTable;
import com.sun.org.apache.bcel.internal.classfile.Field;
import com.sun.org.apache.bcel.internal.classfile.Method;
import com.sun.org.apache.bcel.internal.classfile.Utility;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

final class MethodHTML
  implements Constants
{
  private String class_name;
  private PrintWriter file;
  private ConstantHTML constant_html;
  private AttributeHTML attribute_html;
  
  MethodHTML(String paramString1, String paramString2, Method[] paramArrayOfMethod, Field[] paramArrayOfField, ConstantHTML paramConstantHTML, AttributeHTML paramAttributeHTML)
    throws IOException
  {
    class_name = paramString2;
    attribute_html = paramAttributeHTML;
    constant_html = paramConstantHTML;
    file = new PrintWriter(new FileOutputStream(paramString1 + paramString2 + "_methods.html"));
    file.println("<HTML><BODY BGCOLOR=\"#C0C0C0\"><TABLE BORDER=0>");
    file.println("<TR><TH ALIGN=LEFT>Access&nbsp;flags</TH><TH ALIGN=LEFT>Type</TH><TH ALIGN=LEFT>Field&nbsp;name</TH></TR>");
    for (int i = 0; i < paramArrayOfField.length; i++) {
      writeField(paramArrayOfField[i]);
    }
    file.println("</TABLE>");
    file.println("<TABLE BORDER=0><TR><TH ALIGN=LEFT>Access&nbsp;flags</TH><TH ALIGN=LEFT>Return&nbsp;type</TH><TH ALIGN=LEFT>Method&nbsp;name</TH><TH ALIGN=LEFT>Arguments</TH></TR>");
    for (i = 0; i < paramArrayOfMethod.length; i++) {
      writeMethod(paramArrayOfMethod[i], i);
    }
    file.println("</TABLE></BODY></HTML>");
    file.close();
  }
  
  private void writeField(Field paramField)
    throws IOException
  {
    String str1 = Utility.signatureToString(paramField.getSignature());
    String str2 = paramField.getName();
    String str3 = Utility.accessToString(paramField.getAccessFlags());
    str3 = Utility.replace(str3, " ", "&nbsp;");
    file.print("<TR><TD><FONT COLOR=\"#FF0000\">" + str3 + "</FONT></TD>\n<TD>" + Class2HTML.referenceType(str1) + "</TD><TD><A NAME=\"field" + str2 + "\">" + str2 + "</A></TD>");
    Attribute[] arrayOfAttribute = paramField.getAttributes();
    for (int i = 0; i < arrayOfAttribute.length; i++) {
      attribute_html.writeAttribute(arrayOfAttribute[i], str2 + "@" + i);
    }
    for (i = 0; i < arrayOfAttribute.length; i++) {
      if (arrayOfAttribute[i].getTag() == 1)
      {
        String str4 = ((ConstantValue)arrayOfAttribute[i]).toString();
        file.print("<TD>= <A HREF=\"" + class_name + "_attributes.html#" + str2 + "@" + i + "\" TARGET=\"Attributes\">" + str4 + "</TD>\n");
        break;
      }
    }
    file.println("</TR>");
  }
  
  private final void writeMethod(Method paramMethod, int paramInt)
    throws IOException
  {
    String str1 = paramMethod.getSignature();
    String[] arrayOfString = Utility.methodSignatureArgumentTypes(str1, false);
    String str2 = Utility.methodSignatureReturnType(str1, false);
    String str3 = paramMethod.getName();
    String str5 = Utility.accessToString(paramMethod.getAccessFlags());
    Attribute[] arrayOfAttribute = paramMethod.getAttributes();
    str5 = Utility.replace(str5, " ", "&nbsp;");
    String str4 = Class2HTML.toHTML(str3);
    file.print("<TR VALIGN=TOP><TD><FONT COLOR=\"#FF0000\"><A NAME=method" + paramInt + ">" + str5 + "</A></FONT></TD>");
    file.print("<TD>" + Class2HTML.referenceType(str2) + "</TD><TD><A HREF=" + class_name + "_code.html#method" + paramInt + " TARGET=Code>" + str4 + "</A></TD>\n<TD>(");
    for (int i = 0; i < arrayOfString.length; i++)
    {
      file.print(Class2HTML.referenceType(arrayOfString[i]));
      if (i < arrayOfString.length - 1) {
        file.print(", ");
      }
    }
    file.print(")</TD></TR>");
    for (i = 0; i < arrayOfAttribute.length; i++)
    {
      attribute_html.writeAttribute(arrayOfAttribute[i], "method" + paramInt + "@" + i, paramInt);
      int j = arrayOfAttribute[i].getTag();
      Object localObject;
      int k;
      if (j == 3)
      {
        file.print("<TR VALIGN=TOP><TD COLSPAN=2></TD><TH ALIGN=LEFT>throws</TH><TD>");
        localObject = ((ExceptionTable)arrayOfAttribute[i]).getExceptionIndexTable();
        for (k = 0; k < localObject.length; k++)
        {
          file.print(constant_html.referenceConstant(localObject[k]));
          if (k < localObject.length - 1) {
            file.print(", ");
          }
        }
        file.println("</TD></TR>");
      }
      else if (j == 2)
      {
        localObject = ((Code)arrayOfAttribute[i]).getAttributes();
        for (k = 0; k < localObject.length; k++) {
          attribute_html.writeAttribute(localObject[k], "method" + paramInt + "@" + i + "@" + k, paramInt);
        }
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\bcel\internal\util\MethodHTML.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */