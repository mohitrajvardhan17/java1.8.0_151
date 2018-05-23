package sun.net.www.protocol.file;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilePermission;
import java.io.IOException;
import java.io.InputStream;
import java.net.FileNameMap;
import java.net.URL;
import java.security.Permission;
import java.text.Collator;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import sun.net.ProgressMonitor;
import sun.net.ProgressSource;
import sun.net.www.MessageHeader;
import sun.net.www.MeteredStream;
import sun.net.www.ParseUtil;

public class FileURLConnection
  extends sun.net.www.URLConnection
{
  static String CONTENT_LENGTH = "content-length";
  static String CONTENT_TYPE = "content-type";
  static String TEXT_PLAIN = "text/plain";
  static String LAST_MODIFIED = "last-modified";
  String contentType;
  InputStream is;
  File file;
  String filename;
  boolean isDirectory = false;
  boolean exists = false;
  List<String> files;
  long length = -1L;
  long lastModified = 0L;
  private boolean initializedHeaders = false;
  Permission permission;
  
  protected FileURLConnection(URL paramURL, File paramFile)
  {
    super(paramURL);
    file = paramFile;
  }
  
  public void connect()
    throws IOException
  {
    if (!connected)
    {
      try
      {
        filename = file.toString();
        isDirectory = file.isDirectory();
        if (isDirectory)
        {
          String[] arrayOfString = file.list();
          if (arrayOfString == null) {
            throw new FileNotFoundException(filename + " exists, but is not accessible");
          }
          files = Arrays.asList(arrayOfString);
        }
        else
        {
          is = new BufferedInputStream(new FileInputStream(filename));
          boolean bool = ProgressMonitor.getDefault().shouldMeterInput(url, "GET");
          if (bool)
          {
            ProgressSource localProgressSource = new ProgressSource(url, "GET", file.length());
            is = new MeteredStream(is, localProgressSource, file.length());
          }
        }
      }
      catch (IOException localIOException)
      {
        throw localIOException;
      }
      connected = true;
    }
  }
  
  private void initializeHeaders()
  {
    try
    {
      connect();
      exists = file.exists();
    }
    catch (IOException localIOException) {}
    if ((!initializedHeaders) || (!exists))
    {
      length = file.length();
      lastModified = file.lastModified();
      if (!isDirectory)
      {
        FileNameMap localFileNameMap = java.net.URLConnection.getFileNameMap();
        contentType = localFileNameMap.getContentTypeFor(filename);
        if (contentType != null) {
          properties.add(CONTENT_TYPE, contentType);
        }
        properties.add(CONTENT_LENGTH, String.valueOf(length));
        if (lastModified != 0L)
        {
          Date localDate = new Date(lastModified);
          SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss 'GMT'", Locale.US);
          localSimpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
          properties.add(LAST_MODIFIED, localSimpleDateFormat.format(localDate));
        }
      }
      else
      {
        properties.add(CONTENT_TYPE, TEXT_PLAIN);
      }
      initializedHeaders = true;
    }
  }
  
  public String getHeaderField(String paramString)
  {
    initializeHeaders();
    return super.getHeaderField(paramString);
  }
  
  public String getHeaderField(int paramInt)
  {
    initializeHeaders();
    return super.getHeaderField(paramInt);
  }
  
  public int getContentLength()
  {
    initializeHeaders();
    if (length > 2147483647L) {
      return -1;
    }
    return (int)length;
  }
  
  public long getContentLengthLong()
  {
    initializeHeaders();
    return length;
  }
  
  public String getHeaderFieldKey(int paramInt)
  {
    initializeHeaders();
    return super.getHeaderFieldKey(paramInt);
  }
  
  public MessageHeader getProperties()
  {
    initializeHeaders();
    return super.getProperties();
  }
  
  public long getLastModified()
  {
    initializeHeaders();
    return lastModified;
  }
  
  public synchronized InputStream getInputStream()
    throws IOException
  {
    connect();
    if (is == null) {
      if (isDirectory)
      {
        FileNameMap localFileNameMap = java.net.URLConnection.getFileNameMap();
        StringBuffer localStringBuffer = new StringBuffer();
        if (files == null) {
          throw new FileNotFoundException(filename);
        }
        Collections.sort(files, Collator.getInstance());
        for (int i = 0; i < files.size(); i++)
        {
          String str = (String)files.get(i);
          localStringBuffer.append(str);
          localStringBuffer.append("\n");
        }
        is = new ByteArrayInputStream(localStringBuffer.toString().getBytes());
      }
      else
      {
        throw new FileNotFoundException(filename);
      }
    }
    return is;
  }
  
  public Permission getPermission()
    throws IOException
  {
    if (permission == null)
    {
      String str = ParseUtil.decode(url.getPath());
      if (File.separatorChar == '/') {
        permission = new FilePermission(str, "read");
      } else {
        permission = new FilePermission(str.replace('/', File.separatorChar), "read");
      }
    }
    return permission;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\net\www\protocol\file\FileURLConnection.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */