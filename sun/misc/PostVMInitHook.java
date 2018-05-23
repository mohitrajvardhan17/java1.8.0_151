package sun.misc;

import sun.usagetracker.UsageTrackerClient;

public class PostVMInitHook
{
  public PostVMInitHook() {}
  
  public static void run() {}
  
  private static void trackJavaUsage()
  {
    UsageTrackerClient localUsageTrackerClient = new UsageTrackerClient();
    localUsageTrackerClient.run("VM start", System.getProperty("sun.java.command"));
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\misc\PostVMInitHook.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */