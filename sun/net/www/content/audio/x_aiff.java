package sun.net.www.content.audio;

import java.io.IOException;
import java.net.ContentHandler;
import java.net.URLConnection;
import sun.applet.AppletAudioClip;

public class x_aiff
  extends ContentHandler
{
  public x_aiff() {}
  
  public Object getContent(URLConnection paramURLConnection)
    throws IOException
  {
    return new AppletAudioClip(paramURLConnection);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\net\www\content\audio\x_aiff.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */