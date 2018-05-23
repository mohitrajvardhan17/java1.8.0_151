package java.lang;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class Throwable
  implements Serializable
{
  private static final long serialVersionUID = -3042686055658047285L;
  private transient Object backtrace;
  private String detailMessage;
  private static final StackTraceElement[] UNASSIGNED_STACK = new StackTraceElement[0];
  private Throwable cause = this;
  private StackTraceElement[] stackTrace = UNASSIGNED_STACK;
  private static final List<Throwable> SUPPRESSED_SENTINEL = Collections.unmodifiableList(new ArrayList(0));
  private List<Throwable> suppressedExceptions = SUPPRESSED_SENTINEL;
  private static final String NULL_CAUSE_MESSAGE = "Cannot suppress a null exception.";
  private static final String SELF_SUPPRESSION_MESSAGE = "Self-suppression not permitted";
  private static final String CAUSE_CAPTION = "Caused by: ";
  private static final String SUPPRESSED_CAPTION = "Suppressed: ";
  private static final Throwable[] EMPTY_THROWABLE_ARRAY = new Throwable[0];
  
  public Throwable()
  {
    fillInStackTrace();
  }
  
  public Throwable(String paramString)
  {
    fillInStackTrace();
    detailMessage = paramString;
  }
  
  public Throwable(String paramString, Throwable paramThrowable)
  {
    fillInStackTrace();
    detailMessage = paramString;
    cause = paramThrowable;
  }
  
  public Throwable(Throwable paramThrowable)
  {
    fillInStackTrace();
    detailMessage = (paramThrowable == null ? null : paramThrowable.toString());
    cause = paramThrowable;
  }
  
  protected Throwable(String paramString, Throwable paramThrowable, boolean paramBoolean1, boolean paramBoolean2)
  {
    if (paramBoolean2) {
      fillInStackTrace();
    } else {
      stackTrace = null;
    }
    detailMessage = paramString;
    cause = paramThrowable;
    if (!paramBoolean1) {
      suppressedExceptions = null;
    }
  }
  
  public String getMessage()
  {
    return detailMessage;
  }
  
  public String getLocalizedMessage()
  {
    return getMessage();
  }
  
  public synchronized Throwable getCause()
  {
    return cause == this ? null : cause;
  }
  
  public synchronized Throwable initCause(Throwable paramThrowable)
  {
    if (cause != this) {
      throw new IllegalStateException("Can't overwrite cause with " + Objects.toString(paramThrowable, "a null"), this);
    }
    if (paramThrowable == this) {
      throw new IllegalArgumentException("Self-causation not permitted", this);
    }
    cause = paramThrowable;
    return this;
  }
  
  public String toString()
  {
    String str1 = getClass().getName();
    String str2 = getLocalizedMessage();
    return str2 != null ? str1 + ": " + str2 : str1;
  }
  
  public void printStackTrace()
  {
    printStackTrace(System.err);
  }
  
  public void printStackTrace(PrintStream paramPrintStream)
  {
    printStackTrace(new WrappedPrintStream(paramPrintStream));
  }
  
  private void printStackTrace(PrintStreamOrWriter paramPrintStreamOrWriter)
  {
    Set localSet = Collections.newSetFromMap(new IdentityHashMap());
    localSet.add(this);
    synchronized (paramPrintStreamOrWriter.lock())
    {
      paramPrintStreamOrWriter.println(this);
      StackTraceElement[] arrayOfStackTraceElement = getOurStackTrace();
      Object localObject2;
      for (localObject2 : arrayOfStackTraceElement) {
        paramPrintStreamOrWriter.println("\tat " + localObject2);
      }
      for (localObject2 : getSuppressed()) {
        ((Throwable)localObject2).printEnclosedStackTrace(paramPrintStreamOrWriter, arrayOfStackTraceElement, "Suppressed: ", "\t", localSet);
      }
      ??? = getCause();
      if (??? != null) {
        ((Throwable)???).printEnclosedStackTrace(paramPrintStreamOrWriter, arrayOfStackTraceElement, "Caused by: ", "", localSet);
      }
    }
  }
  
  private void printEnclosedStackTrace(PrintStreamOrWriter paramPrintStreamOrWriter, StackTraceElement[] paramArrayOfStackTraceElement, String paramString1, String paramString2, Set<Throwable> paramSet)
  {
    assert (Thread.holdsLock(paramPrintStreamOrWriter.lock()));
    if (paramSet.contains(this))
    {
      paramPrintStreamOrWriter.println("\t[CIRCULAR REFERENCE:" + this + "]");
    }
    else
    {
      paramSet.add(this);
      StackTraceElement[] arrayOfStackTraceElement = getOurStackTrace();
      int i = arrayOfStackTraceElement.length - 1;
      for (int j = paramArrayOfStackTraceElement.length - 1; (i >= 0) && (j >= 0) && (arrayOfStackTraceElement[i].equals(paramArrayOfStackTraceElement[j])); j--) {
        i--;
      }
      int k = arrayOfStackTraceElement.length - 1 - i;
      paramPrintStreamOrWriter.println(paramString2 + paramString1 + this);
      for (int m = 0; m <= i; m++) {
        paramPrintStreamOrWriter.println(paramString2 + "\tat " + arrayOfStackTraceElement[m]);
      }
      if (k != 0) {
        paramPrintStreamOrWriter.println(paramString2 + "\t... " + k + " more");
      }
      for (Object localObject2 : getSuppressed()) {
        ((Throwable)localObject2).printEnclosedStackTrace(paramPrintStreamOrWriter, arrayOfStackTraceElement, "Suppressed: ", paramString2 + "\t", paramSet);
      }
      ??? = getCause();
      if (??? != null) {
        ((Throwable)???).printEnclosedStackTrace(paramPrintStreamOrWriter, arrayOfStackTraceElement, "Caused by: ", paramString2, paramSet);
      }
    }
  }
  
  public void printStackTrace(PrintWriter paramPrintWriter)
  {
    printStackTrace(new WrappedPrintWriter(paramPrintWriter));
  }
  
  public synchronized Throwable fillInStackTrace()
  {
    if ((stackTrace != null) || (backtrace != null))
    {
      fillInStackTrace(0);
      stackTrace = UNASSIGNED_STACK;
    }
    return this;
  }
  
  private native Throwable fillInStackTrace(int paramInt);
  
  public StackTraceElement[] getStackTrace()
  {
    return (StackTraceElement[])getOurStackTrace().clone();
  }
  
  private synchronized StackTraceElement[] getOurStackTrace()
  {
    if ((stackTrace == UNASSIGNED_STACK) || ((stackTrace == null) && (backtrace != null)))
    {
      int i = getStackTraceDepth();
      stackTrace = new StackTraceElement[i];
      for (int j = 0; j < i; j++) {
        stackTrace[j] = getStackTraceElement(j);
      }
    }
    else if (stackTrace == null)
    {
      return UNASSIGNED_STACK;
    }
    return stackTrace;
  }
  
  public void setStackTrace(StackTraceElement[] paramArrayOfStackTraceElement)
  {
    StackTraceElement[] arrayOfStackTraceElement = (StackTraceElement[])paramArrayOfStackTraceElement.clone();
    for (int i = 0; i < arrayOfStackTraceElement.length; i++) {
      if (arrayOfStackTraceElement[i] == null) {
        throw new NullPointerException("stackTrace[" + i + "]");
      }
    }
    synchronized (this)
    {
      if ((stackTrace == null) && (backtrace == null)) {
        return;
      }
      stackTrace = arrayOfStackTraceElement;
    }
  }
  
  native int getStackTraceDepth();
  
  native StackTraceElement getStackTraceElement(int paramInt);
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    paramObjectInputStream.defaultReadObject();
    Object localObject1;
    if (suppressedExceptions != null)
    {
      localObject1 = null;
      if (suppressedExceptions.isEmpty())
      {
        localObject1 = SUPPRESSED_SENTINEL;
      }
      else
      {
        localObject1 = new ArrayList(1);
        Iterator localIterator = suppressedExceptions.iterator();
        while (localIterator.hasNext())
        {
          Throwable localThrowable = (Throwable)localIterator.next();
          if (localThrowable == null) {
            throw new NullPointerException("Cannot suppress a null exception.");
          }
          if (localThrowable == this) {
            throw new IllegalArgumentException("Self-suppression not permitted");
          }
          ((List)localObject1).add(localThrowable);
        }
      }
      suppressedExceptions = ((List)localObject1);
    }
    if (stackTrace != null)
    {
      if (stackTrace.length == 0) {
        stackTrace = ((StackTraceElement[])UNASSIGNED_STACK.clone());
      } else if ((stackTrace.length == 1) && (SentinelHolder.STACK_TRACE_ELEMENT_SENTINEL.equals(stackTrace[0]))) {
        stackTrace = null;
      } else {
        for (Object localObject2 : stackTrace) {
          if (localObject2 == null) {
            throw new NullPointerException("null StackTraceElement in serial stream. ");
          }
        }
      }
    }
    else {
      stackTrace = ((StackTraceElement[])UNASSIGNED_STACK.clone());
    }
  }
  
  /* Error */
  private synchronized void writeObject(java.io.ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    // Byte code:
    //   0: aload_0
    //   1: invokespecial 292	java/lang/Throwable:getOurStackTrace	()[Ljava/lang/StackTraceElement;
    //   4: pop
    //   5: aload_0
    //   6: getfield 263	java/lang/Throwable:stackTrace	[Ljava/lang/StackTraceElement;
    //   9: astore_2
    //   10: aload_0
    //   11: getfield 263	java/lang/Throwable:stackTrace	[Ljava/lang/StackTraceElement;
    //   14: ifnonnull +10 -> 24
    //   17: aload_0
    //   18: getstatic 270	java/lang/Throwable$SentinelHolder:STACK_TRACE_SENTINEL	[Ljava/lang/StackTraceElement;
    //   21: putfield 263	java/lang/Throwable:stackTrace	[Ljava/lang/StackTraceElement;
    //   24: aload_1
    //   25: invokevirtual 273	java/io/ObjectOutputStream:defaultWriteObject	()V
    //   28: aload_0
    //   29: aload_2
    //   30: putfield 263	java/lang/Throwable:stackTrace	[Ljava/lang/StackTraceElement;
    //   33: goto +11 -> 44
    //   36: astore_3
    //   37: aload_0
    //   38: aload_2
    //   39: putfield 263	java/lang/Throwable:stackTrace	[Ljava/lang/StackTraceElement;
    //   42: aload_3
    //   43: athrow
    //   44: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	45	0	this	Throwable
    //   0	45	1	paramObjectOutputStream	java.io.ObjectOutputStream
    //   9	30	2	arrayOfStackTraceElement	StackTraceElement[]
    //   36	7	3	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   10	28	36	finally
  }
  
  public final synchronized void addSuppressed(Throwable paramThrowable)
  {
    if (paramThrowable == this) {
      throw new IllegalArgumentException("Self-suppression not permitted", paramThrowable);
    }
    if (paramThrowable == null) {
      throw new NullPointerException("Cannot suppress a null exception.");
    }
    if (suppressedExceptions == null) {
      return;
    }
    if (suppressedExceptions == SUPPRESSED_SENTINEL) {
      suppressedExceptions = new ArrayList(1);
    }
    suppressedExceptions.add(paramThrowable);
  }
  
  public final synchronized Throwable[] getSuppressed()
  {
    if ((suppressedExceptions == SUPPRESSED_SENTINEL) || (suppressedExceptions == null)) {
      return EMPTY_THROWABLE_ARRAY;
    }
    return (Throwable[])suppressedExceptions.toArray(EMPTY_THROWABLE_ARRAY);
  }
  
  private static abstract class PrintStreamOrWriter
  {
    private PrintStreamOrWriter() {}
    
    abstract Object lock();
    
    abstract void println(Object paramObject);
  }
  
  private static class SentinelHolder
  {
    public static final StackTraceElement STACK_TRACE_ELEMENT_SENTINEL = new StackTraceElement("", "", null, Integer.MIN_VALUE);
    public static final StackTraceElement[] STACK_TRACE_SENTINEL = { STACK_TRACE_ELEMENT_SENTINEL };
    
    private SentinelHolder() {}
  }
  
  private static class WrappedPrintStream
    extends Throwable.PrintStreamOrWriter
  {
    private final PrintStream printStream;
    
    WrappedPrintStream(PrintStream paramPrintStream)
    {
      super();
      printStream = paramPrintStream;
    }
    
    Object lock()
    {
      return printStream;
    }
    
    void println(Object paramObject)
    {
      printStream.println(paramObject);
    }
  }
  
  private static class WrappedPrintWriter
    extends Throwable.PrintStreamOrWriter
  {
    private final PrintWriter printWriter;
    
    WrappedPrintWriter(PrintWriter paramPrintWriter)
    {
      super();
      printWriter = paramPrintWriter;
    }
    
    Object lock()
    {
      return printWriter;
    }
    
    void println(Object paramObject)
    {
      printWriter.println(paramObject);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\lang\Throwable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */