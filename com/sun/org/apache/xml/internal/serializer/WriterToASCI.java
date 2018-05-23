package com.sun.org.apache.xml.internal.serializer;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

class WriterToASCI
  extends Writer
  implements WriterChain
{
  private final OutputStream m_os;
  
  public WriterToASCI(OutputStream paramOutputStream)
  {
    m_os = paramOutputStream;
  }
  
  public void write(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws IOException
  {
    int i = paramInt2 + paramInt1;
    for (int j = paramInt1; j < i; j++) {
      m_os.write(paramArrayOfChar[j]);
    }
  }
  
  public void write(int paramInt)
    throws IOException
  {
    m_os.write(paramInt);
  }
  
  public void write(String paramString)
    throws IOException
  {
    int i = paramString.length();
    for (int j = 0; j < i; j++) {
      m_os.write(paramString.charAt(j));
    }
  }
  
  public void flush()
    throws IOException
  {
    m_os.flush();
  }
  
  public void close()
    throws IOException
  {
    m_os.close();
  }
  
  public OutputStream getOutputStream()
  {
    return m_os;
  }
  
  public Writer getWriter()
  {
    return null;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\serializer\WriterToASCI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */