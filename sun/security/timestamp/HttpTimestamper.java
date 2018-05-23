package sun.security.timestamp;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import sun.misc.IOUtils;
import sun.security.util.Debug;

public class HttpTimestamper
  implements Timestamper
{
  private static final int CONNECT_TIMEOUT = 15000;
  private static final String TS_QUERY_MIME_TYPE = "application/timestamp-query";
  private static final String TS_REPLY_MIME_TYPE = "application/timestamp-reply";
  private static final Debug debug = Debug.getInstance("ts");
  private URI tsaURI = null;
  
  public HttpTimestamper(URI paramURI)
  {
    if ((!paramURI.getScheme().equalsIgnoreCase("http")) && (!paramURI.getScheme().equalsIgnoreCase("https"))) {
      throw new IllegalArgumentException("TSA must be an HTTP or HTTPS URI");
    }
    tsaURI = paramURI;
  }
  
  public TSResponse generateTimestamp(TSRequest paramTSRequest)
    throws IOException
  {
    HttpURLConnection localHttpURLConnection = (HttpURLConnection)tsaURI.toURL().openConnection();
    localHttpURLConnection.setDoOutput(true);
    localHttpURLConnection.setUseCaches(false);
    localHttpURLConnection.setRequestProperty("Content-Type", "application/timestamp-query");
    localHttpURLConnection.setRequestMethod("POST");
    localHttpURLConnection.setConnectTimeout(15000);
    if (debug != null)
    {
      localObject1 = localHttpURLConnection.getRequestProperties().entrySet();
      debug.println(localHttpURLConnection.getRequestMethod() + " " + tsaURI + " HTTP/1.1");
      localObject2 = ((Set)localObject1).iterator();
      while (((Iterator)localObject2).hasNext())
      {
        localObject3 = (Map.Entry)((Iterator)localObject2).next();
        debug.println("  " + localObject3);
      }
      debug.println();
    }
    localHttpURLConnection.connect();
    Object localObject1 = null;
    try
    {
      localObject1 = new DataOutputStream(localHttpURLConnection.getOutputStream());
      localObject2 = paramTSRequest.encode();
      ((DataOutputStream)localObject1).write((byte[])localObject2, 0, localObject2.length);
      ((DataOutputStream)localObject1).flush();
      if (debug != null) {
        debug.println("sent timestamp query (length=" + localObject2.length + ")");
      }
    }
    finally
    {
      if (localObject1 != null) {
        ((DataOutputStream)localObject1).close();
      }
    }
    Object localObject2 = null;
    Object localObject3 = null;
    try
    {
      localObject2 = new BufferedInputStream(localHttpURLConnection.getInputStream());
      if (debug != null)
      {
        String str1 = localHttpURLConnection.getHeaderField(0);
        debug.println(str1);
        for (int j = 1; (str1 = localHttpURLConnection.getHeaderField(j)) != null; j++)
        {
          String str2 = localHttpURLConnection.getHeaderFieldKey(j);
          debug.println("  " + (str2 == null ? "" : new StringBuilder().append(str2).append(": ").toString()) + str1);
        }
        debug.println();
      }
      verifyMimeType(localHttpURLConnection.getContentType());
      int i = localHttpURLConnection.getContentLength();
      localObject3 = IOUtils.readFully((InputStream)localObject2, i, false);
      if (debug != null) {
        debug.println("received timestamp response (length=" + localObject3.length + ")");
      }
    }
    finally
    {
      if (localObject2 != null) {
        ((BufferedInputStream)localObject2).close();
      }
    }
    return new TSResponse((byte[])localObject3);
  }
  
  private static void verifyMimeType(String paramString)
    throws IOException
  {
    if (!"application/timestamp-reply".equalsIgnoreCase(paramString)) {
      throw new IOException("MIME Content-Type is not application/timestamp-reply");
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\timestamp\HttpTimestamper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */