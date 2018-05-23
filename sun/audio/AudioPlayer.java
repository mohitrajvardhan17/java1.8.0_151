package sun.audio;

import java.io.InputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;

public final class AudioPlayer
  extends Thread
{
  private final AudioDevice devAudio = AudioDevice.device;
  private static final boolean DEBUG = false;
  public static final AudioPlayer player = ;
  
  private static ThreadGroup getAudioThreadGroup()
  {
    for (ThreadGroup localThreadGroup = currentThread().getThreadGroup(); (localThreadGroup.getParent() != null) && (localThreadGroup.getParent().getParent() != null); localThreadGroup = localThreadGroup.getParent()) {}
    return localThreadGroup;
  }
  
  private static AudioPlayer getAudioPlayer()
  {
    PrivilegedAction local1 = new PrivilegedAction()
    {
      public Object run()
      {
        AudioPlayer localAudioPlayer = new AudioPlayer(null);
        localAudioPlayer.setPriority(10);
        localAudioPlayer.setDaemon(true);
        localAudioPlayer.start();
        return localAudioPlayer;
      }
    };
    AudioPlayer localAudioPlayer = (AudioPlayer)AccessController.doPrivileged(local1);
    return localAudioPlayer;
  }
  
  private AudioPlayer()
  {
    super(getAudioThreadGroup(), "Audio Player");
    devAudio.open();
  }
  
  public synchronized void start(InputStream paramInputStream)
  {
    devAudio.openChannel(paramInputStream);
    notify();
  }
  
  public synchronized void stop(InputStream paramInputStream)
  {
    devAudio.closeChannel(paramInputStream);
  }
  
  public void run()
  {
    devAudio.play();
    try
    {
      for (;;)
      {
        Thread.sleep(5000L);
      }
      return;
    }
    catch (Exception localException) {}
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\audio\AudioPlayer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */