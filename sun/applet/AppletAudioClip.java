package sun.applet;

import com.sun.media.sound.JavaSoundAudioClip;
import java.applet.AudioClip;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.URL;
import java.net.URLConnection;

public class AppletAudioClip
  implements AudioClip
{
  private URL url = null;
  private AudioClip audioClip = null;
  boolean DEBUG = false;
  
  public AppletAudioClip(URL paramURL)
  {
    url = paramURL;
    try
    {
      InputStream localInputStream = paramURL.openStream();
      createAppletAudioClip(localInputStream);
    }
    catch (IOException localIOException)
    {
      if (DEBUG) {
        System.err.println("IOException creating AppletAudioClip" + localIOException);
      }
    }
  }
  
  public AppletAudioClip(URLConnection paramURLConnection)
  {
    try
    {
      createAppletAudioClip(paramURLConnection.getInputStream());
    }
    catch (IOException localIOException)
    {
      if (DEBUG) {
        System.err.println("IOException creating AppletAudioClip" + localIOException);
      }
    }
  }
  
  public AppletAudioClip(byte[] paramArrayOfByte)
  {
    try
    {
      ByteArrayInputStream localByteArrayInputStream = new ByteArrayInputStream(paramArrayOfByte);
      createAppletAudioClip(localByteArrayInputStream);
    }
    catch (IOException localIOException)
    {
      if (DEBUG) {
        System.err.println("IOException creating AppletAudioClip " + localIOException);
      }
    }
  }
  
  void createAppletAudioClip(InputStream paramInputStream)
    throws IOException
  {
    try
    {
      audioClip = new JavaSoundAudioClip(paramInputStream);
    }
    catch (Exception localException)
    {
      throw new IOException("Failed to construct the AudioClip: " + localException);
    }
  }
  
  public synchronized void play()
  {
    if (audioClip != null) {
      audioClip.play();
    }
  }
  
  public synchronized void loop()
  {
    if (audioClip != null) {
      audioClip.loop();
    }
  }
  
  public synchronized void stop()
  {
    if (audioClip != null) {
      audioClip.stop();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\applet\AppletAudioClip.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */