package sun.io;

import sun.misc.VM;

public class Win32ErrorMode
{
  private static final long SEM_FAILCRITICALERRORS = 1L;
  private static final long SEM_NOGPFAULTERRORBOX = 2L;
  private static final long SEM_NOALIGNMENTFAULTEXCEPT = 4L;
  private static final long SEM_NOOPENFILEERRORBOX = 32768L;
  
  private Win32ErrorMode() {}
  
  public static void initialize()
  {
    if (!VM.isBooted())
    {
      String str = System.getProperty("sun.io.allowCriticalErrorMessageBox");
      if ((str == null) || (str.equals(Boolean.FALSE.toString())))
      {
        long l = setErrorMode(0L);
        l |= 1L;
        setErrorMode(l);
      }
    }
  }
  
  private static native long setErrorMode(long paramLong);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\io\Win32ErrorMode.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */