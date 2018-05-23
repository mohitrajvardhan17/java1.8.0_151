package com.sun.management.jmx;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import javax.management.Notification;
import javax.management.NotificationListener;

@Deprecated
public class TraceListener
  implements NotificationListener
{
  protected PrintStream out;
  protected boolean needTobeClosed = false;
  protected boolean formated = false;
  
  public TraceListener()
  {
    out = System.out;
  }
  
  public TraceListener(PrintStream paramPrintStream)
    throws IllegalArgumentException
  {
    if (paramPrintStream == null) {
      throw new IllegalArgumentException("An PrintStream object should be specified.");
    }
    out = paramPrintStream;
  }
  
  public TraceListener(String paramString)
    throws IOException
  {
    out = new PrintStream(new FileOutputStream(paramString, true));
    needTobeClosed = true;
  }
  
  public void setFormated(boolean paramBoolean)
  {
    formated = paramBoolean;
  }
  
  public void handleNotification(Notification paramNotification, Object paramObject)
  {
    if ((paramNotification instanceof TraceNotification))
    {
      TraceNotification localTraceNotification = (TraceNotification)paramNotification;
      if (formated)
      {
        out.print("\nGlobal sequence number: " + globalSequenceNumber + "     Sequence number: " + sequenceNumber + "\nLevel: " + Trace.getLevel(level) + "     Type: " + Trace.getType(type) + "\nClass  Name: " + new String(className) + "\nMethod Name: " + new String(methodName) + "\n");
        if (exception != null)
        {
          exception.printStackTrace(out);
          out.println();
        }
        if (info != null) {
          out.println("Information: " + info);
        }
      }
      else
      {
        out.print("(" + className + " " + methodName + ") ");
        if (exception != null)
        {
          exception.printStackTrace(out);
          out.println();
        }
        if (info != null) {
          out.println(info);
        }
      }
    }
  }
  
  public void setFile(String paramString)
    throws IOException
  {
    PrintStream localPrintStream = new PrintStream(new FileOutputStream(paramString, true));
    if (needTobeClosed) {
      out.close();
    }
    out = localPrintStream;
    needTobeClosed = true;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\management\jmx\TraceListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */