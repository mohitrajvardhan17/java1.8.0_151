package sun.rmi.server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.security.AccessController;
import java.util.Date;
import sun.rmi.runtime.NewThreadAction;
import sun.security.action.GetPropertyAction;

class PipeWriter
  implements Runnable
{
  private ByteArrayOutputStream bufOut;
  private int cLast;
  private byte[] currSep;
  private PrintWriter out;
  private InputStream in;
  private String pipeString;
  private String execString;
  private static String lineSeparator = (String)AccessController.doPrivileged(new GetPropertyAction("line.separator"));
  private static int lineSeparatorLength = lineSeparator.length();
  private static int numExecs = 0;
  
  private PipeWriter(InputStream paramInputStream, OutputStream paramOutputStream, String paramString, int paramInt)
  {
    in = paramInputStream;
    out = new PrintWriter(paramOutputStream);
    bufOut = new ByteArrayOutputStream();
    currSep = new byte[lineSeparatorLength];
    execString = (":ExecGroup-" + Integer.toString(paramInt) + ':' + paramString + ':');
  }
  
  public void run()
  {
    byte[] arrayOfByte = new byte['Ā'];
    try
    {
      int i;
      while ((i = in.read(arrayOfByte)) != -1) {
        write(arrayOfByte, 0, i);
      }
      String str = bufOut.toString();
      bufOut.reset();
      if (str.length() > 0)
      {
        out.println(createAnnotation() + str);
        out.flush();
      }
    }
    catch (IOException localIOException) {}
  }
  
  private void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    if (paramInt2 < 0) {
      throw new ArrayIndexOutOfBoundsException(paramInt2);
    }
    for (int i = 0; i < paramInt2; i++) {
      write(paramArrayOfByte[(paramInt1 + i)]);
    }
  }
  
  private void write(byte paramByte)
    throws IOException
  {
    int i = 0;
    for (i = 1; i < currSep.length; i++) {
      currSep[(i - 1)] = currSep[i];
    }
    currSep[(i - 1)] = paramByte;
    bufOut.write(paramByte);
    if ((cLast >= lineSeparatorLength - 1) && (lineSeparator.equals(new String(currSep))))
    {
      cLast = 0;
      out.print(createAnnotation() + bufOut.toString());
      out.flush();
      bufOut.reset();
      if (out.checkError()) {
        throw new IOException("PipeWriter: IO Exception when writing to output stream.");
      }
    }
    else
    {
      cLast += 1;
    }
  }
  
  private String createAnnotation()
  {
    return new Date().toString() + execString;
  }
  
  static void plugTogetherPair(InputStream paramInputStream1, OutputStream paramOutputStream1, InputStream paramInputStream2, OutputStream paramOutputStream2)
  {
    Thread localThread1 = null;
    Thread localThread2 = null;
    int i = getNumExec();
    localThread1 = (Thread)AccessController.doPrivileged(new NewThreadAction(new PipeWriter(paramInputStream1, paramOutputStream1, "out", i), "out", true));
    localThread2 = (Thread)AccessController.doPrivileged(new NewThreadAction(new PipeWriter(paramInputStream2, paramOutputStream2, "err", i), "err", true));
    localThread1.start();
    localThread2.start();
  }
  
  private static synchronized int getNumExec()
  {
    return numExecs++;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\rmi\server\PipeWriter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */