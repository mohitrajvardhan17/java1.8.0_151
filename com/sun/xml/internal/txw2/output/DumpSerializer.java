package com.sun.xml.internal.txw2.output;

import java.io.PrintStream;

public class DumpSerializer
  implements XmlSerializer
{
  private final PrintStream out;
  
  public DumpSerializer(PrintStream paramPrintStream)
  {
    out = paramPrintStream;
  }
  
  public void beginStartTag(String paramString1, String paramString2, String paramString3)
  {
    out.println('<' + paramString3 + ':' + paramString2);
  }
  
  public void writeAttribute(String paramString1, String paramString2, String paramString3, StringBuilder paramStringBuilder)
  {
    out.println('@' + paramString3 + ':' + paramString2 + '=' + paramStringBuilder);
  }
  
  public void writeXmlns(String paramString1, String paramString2)
  {
    out.println("xmlns:" + paramString1 + '=' + paramString2);
  }
  
  public void endStartTag(String paramString1, String paramString2, String paramString3)
  {
    out.println('>');
  }
  
  public void endTag()
  {
    out.println("</  >");
  }
  
  public void text(StringBuilder paramStringBuilder)
  {
    out.println(paramStringBuilder);
  }
  
  public void cdata(StringBuilder paramStringBuilder)
  {
    out.println("<![CDATA[");
    out.println(paramStringBuilder);
    out.println("]]>");
  }
  
  public void comment(StringBuilder paramStringBuilder)
  {
    out.println("<!--");
    out.println(paramStringBuilder);
    out.println("-->");
  }
  
  public void startDocument()
  {
    out.println("<?xml?>");
  }
  
  public void endDocument()
  {
    out.println("done");
  }
  
  public void flush()
  {
    out.println("flush");
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\txw2\output\DumpSerializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */