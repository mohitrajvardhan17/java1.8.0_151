package sun.nio.fs;

class WindowsSecurity
{
  static final long processTokenWithDuplicateAccess = openProcessToken(2);
  static final long processTokenWithQueryAccess = openProcessToken(8);
  
  private WindowsSecurity() {}
  
  private static long openProcessToken(int paramInt)
  {
    try
    {
      return WindowsNativeDispatcher.OpenProcessToken(WindowsNativeDispatcher.GetCurrentProcess(), paramInt);
    }
    catch (WindowsException localWindowsException) {}
    return 0L;
  }
  
  static Privilege enablePrivilege(String paramString)
  {
    final long l1;
    try
    {
      l1 = WindowsNativeDispatcher.LookupPrivilegeValue(paramString);
    }
    catch (WindowsException localWindowsException1)
    {
      throw new AssertionError(localWindowsException1);
    }
    long l2 = 0L;
    boolean bool1 = false;
    boolean bool2 = false;
    try
    {
      l2 = WindowsNativeDispatcher.OpenThreadToken(WindowsNativeDispatcher.GetCurrentThread(), 32, false);
      if ((l2 == 0L) && (processTokenWithDuplicateAccess != 0L))
      {
        l2 = WindowsNativeDispatcher.DuplicateTokenEx(processTokenWithDuplicateAccess, 36);
        WindowsNativeDispatcher.SetThreadToken(0L, l2);
        bool1 = true;
      }
      if (l2 != 0L)
      {
        WindowsNativeDispatcher.AdjustTokenPrivileges(l2, l1, 2);
        bool2 = true;
      }
    }
    catch (WindowsException localWindowsException2) {}
    long l3 = l2;
    boolean bool3 = bool1;
    final boolean bool4 = bool2;
    new Privilege()
    {
      public void drop()
      {
        if (val$token != 0L) {
          try
          {
            if (bool4) {
              WindowsNativeDispatcher.SetThreadToken(0L, 0L);
            } else if (l1) {
              WindowsNativeDispatcher.AdjustTokenPrivileges(val$token, val$pLuid, 0);
            }
          }
          catch (WindowsException localWindowsException)
          {
            throw new AssertionError(localWindowsException);
          }
          finally
          {
            WindowsNativeDispatcher.CloseHandle(val$token);
          }
        }
      }
    };
  }
  
  static boolean checkAccessMask(long paramLong, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
    throws WindowsException
  {
    int i = 8;
    long l = WindowsNativeDispatcher.OpenThreadToken(WindowsNativeDispatcher.GetCurrentThread(), i, false);
    if ((l == 0L) && (processTokenWithDuplicateAccess != 0L)) {
      l = WindowsNativeDispatcher.DuplicateTokenEx(processTokenWithDuplicateAccess, i);
    }
    boolean bool = false;
    if (l != 0L) {
      try
      {
        bool = WindowsNativeDispatcher.AccessCheck(l, paramLong, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5);
      }
      finally
      {
        WindowsNativeDispatcher.CloseHandle(l);
      }
    }
    return bool;
  }
  
  static abstract interface Privilege
  {
    public abstract void drop();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\nio\fs\WindowsSecurity.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */