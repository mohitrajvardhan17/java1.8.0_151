package sun.net.www.protocol.jar;

import java.io.IOException;
import java.net.URL;
import java.util.jar.JarFile;

public abstract interface URLJarFileCallBack
{
  public abstract JarFile retrieve(URL paramURL)
    throws IOException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\net\www\protocol\jar\URLJarFileCallBack.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */