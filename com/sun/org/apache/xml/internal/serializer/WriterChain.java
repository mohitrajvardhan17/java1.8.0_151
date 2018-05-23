package com.sun.org.apache.xml.internal.serializer;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

abstract interface WriterChain
{
  public abstract void write(int paramInt)
    throws IOException;
  
  public abstract void write(char[] paramArrayOfChar)
    throws IOException;
  
  public abstract void write(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws IOException;
  
  public abstract void write(String paramString)
    throws IOException;
  
  public abstract void write(String paramString, int paramInt1, int paramInt2)
    throws IOException;
  
  public abstract void flush()
    throws IOException;
  
  public abstract void close()
    throws IOException;
  
  public abstract Writer getWriter();
  
  public abstract OutputStream getOutputStream();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\serializer\WriterChain.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */