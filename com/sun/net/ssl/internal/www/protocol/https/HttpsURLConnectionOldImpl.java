package com.sun.net.ssl.internal.www.protocol.https;

import com.sun.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.Proxy;
import java.net.URL;
import java.security.Permission;
import java.security.cert.Certificate;
import java.util.List;
import java.util.Map;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.security.cert.X509Certificate;

public class HttpsURLConnectionOldImpl
  extends HttpsURLConnection
{
  private DelegateHttpsURLConnection delegate = new DelegateHttpsURLConnection(url, paramProxy, paramHandler, this);
  
  HttpsURLConnectionOldImpl(URL paramURL, Handler paramHandler)
    throws IOException
  {
    this(paramURL, null, paramHandler);
  }
  
  static URL checkURL(URL paramURL)
    throws IOException
  {
    if ((paramURL != null) && (paramURL.toExternalForm().indexOf('\n') > -1)) {
      throw new MalformedURLException("Illegal character in URL");
    }
    return paramURL;
  }
  
  HttpsURLConnectionOldImpl(URL paramURL, Proxy paramProxy, Handler paramHandler)
    throws IOException
  {
    super(checkURL(paramURL));
  }
  
  protected void setNewClient(URL paramURL)
    throws IOException
  {
    delegate.setNewClient(paramURL, false);
  }
  
  protected void setNewClient(URL paramURL, boolean paramBoolean)
    throws IOException
  {
    delegate.setNewClient(paramURL, paramBoolean);
  }
  
  protected void setProxiedClient(URL paramURL, String paramString, int paramInt)
    throws IOException
  {
    delegate.setProxiedClient(paramURL, paramString, paramInt);
  }
  
  protected void setProxiedClient(URL paramURL, String paramString, int paramInt, boolean paramBoolean)
    throws IOException
  {
    delegate.setProxiedClient(paramURL, paramString, paramInt, paramBoolean);
  }
  
  public void connect()
    throws IOException
  {
    delegate.connect();
  }
  
  protected boolean isConnected()
  {
    return delegate.isConnected();
  }
  
  protected void setConnected(boolean paramBoolean)
  {
    delegate.setConnected(paramBoolean);
  }
  
  public String getCipherSuite()
  {
    return delegate.getCipherSuite();
  }
  
  public Certificate[] getLocalCertificates()
  {
    return delegate.getLocalCertificates();
  }
  
  public Certificate[] getServerCertificates()
    throws SSLPeerUnverifiedException
  {
    return delegate.getServerCertificates();
  }
  
  public X509Certificate[] getServerCertificateChain()
  {
    try
    {
      return delegate.getServerCertificateChain();
    }
    catch (SSLPeerUnverifiedException localSSLPeerUnverifiedException) {}
    return null;
  }
  
  public synchronized OutputStream getOutputStream()
    throws IOException
  {
    return delegate.getOutputStream();
  }
  
  public synchronized InputStream getInputStream()
    throws IOException
  {
    return delegate.getInputStream();
  }
  
  public InputStream getErrorStream()
  {
    return delegate.getErrorStream();
  }
  
  public void disconnect()
  {
    delegate.disconnect();
  }
  
  public boolean usingProxy()
  {
    return delegate.usingProxy();
  }
  
  public Map<String, List<String>> getHeaderFields()
  {
    return delegate.getHeaderFields();
  }
  
  public String getHeaderField(String paramString)
  {
    return delegate.getHeaderField(paramString);
  }
  
  public String getHeaderField(int paramInt)
  {
    return delegate.getHeaderField(paramInt);
  }
  
  public String getHeaderFieldKey(int paramInt)
  {
    return delegate.getHeaderFieldKey(paramInt);
  }
  
  public void setRequestProperty(String paramString1, String paramString2)
  {
    delegate.setRequestProperty(paramString1, paramString2);
  }
  
  public void addRequestProperty(String paramString1, String paramString2)
  {
    delegate.addRequestProperty(paramString1, paramString2);
  }
  
  public int getResponseCode()
    throws IOException
  {
    return delegate.getResponseCode();
  }
  
  public String getRequestProperty(String paramString)
  {
    return delegate.getRequestProperty(paramString);
  }
  
  public Map<String, List<String>> getRequestProperties()
  {
    return delegate.getRequestProperties();
  }
  
  public void setInstanceFollowRedirects(boolean paramBoolean)
  {
    delegate.setInstanceFollowRedirects(paramBoolean);
  }
  
  public boolean getInstanceFollowRedirects()
  {
    return delegate.getInstanceFollowRedirects();
  }
  
  public void setRequestMethod(String paramString)
    throws ProtocolException
  {
    delegate.setRequestMethod(paramString);
  }
  
  public String getRequestMethod()
  {
    return delegate.getRequestMethod();
  }
  
  public String getResponseMessage()
    throws IOException
  {
    return delegate.getResponseMessage();
  }
  
  public long getHeaderFieldDate(String paramString, long paramLong)
  {
    return delegate.getHeaderFieldDate(paramString, paramLong);
  }
  
  public Permission getPermission()
    throws IOException
  {
    return delegate.getPermission();
  }
  
  public URL getURL()
  {
    return delegate.getURL();
  }
  
  public int getContentLength()
  {
    return delegate.getContentLength();
  }
  
  public long getContentLengthLong()
  {
    return delegate.getContentLengthLong();
  }
  
  public String getContentType()
  {
    return delegate.getContentType();
  }
  
  public String getContentEncoding()
  {
    return delegate.getContentEncoding();
  }
  
  public long getExpiration()
  {
    return delegate.getExpiration();
  }
  
  public long getDate()
  {
    return delegate.getDate();
  }
  
  public long getLastModified()
  {
    return delegate.getLastModified();
  }
  
  public int getHeaderFieldInt(String paramString, int paramInt)
  {
    return delegate.getHeaderFieldInt(paramString, paramInt);
  }
  
  public long getHeaderFieldLong(String paramString, long paramLong)
  {
    return delegate.getHeaderFieldLong(paramString, paramLong);
  }
  
  public Object getContent()
    throws IOException
  {
    return delegate.getContent();
  }
  
  public Object getContent(Class[] paramArrayOfClass)
    throws IOException
  {
    return delegate.getContent(paramArrayOfClass);
  }
  
  public String toString()
  {
    return delegate.toString();
  }
  
  public void setDoInput(boolean paramBoolean)
  {
    delegate.setDoInput(paramBoolean);
  }
  
  public boolean getDoInput()
  {
    return delegate.getDoInput();
  }
  
  public void setDoOutput(boolean paramBoolean)
  {
    delegate.setDoOutput(paramBoolean);
  }
  
  public boolean getDoOutput()
  {
    return delegate.getDoOutput();
  }
  
  public void setAllowUserInteraction(boolean paramBoolean)
  {
    delegate.setAllowUserInteraction(paramBoolean);
  }
  
  public boolean getAllowUserInteraction()
  {
    return delegate.getAllowUserInteraction();
  }
  
  public void setUseCaches(boolean paramBoolean)
  {
    delegate.setUseCaches(paramBoolean);
  }
  
  public boolean getUseCaches()
  {
    return delegate.getUseCaches();
  }
  
  public void setIfModifiedSince(long paramLong)
  {
    delegate.setIfModifiedSince(paramLong);
  }
  
  public long getIfModifiedSince()
  {
    return delegate.getIfModifiedSince();
  }
  
  public boolean getDefaultUseCaches()
  {
    return delegate.getDefaultUseCaches();
  }
  
  public void setDefaultUseCaches(boolean paramBoolean)
  {
    delegate.setDefaultUseCaches(paramBoolean);
  }
  
  protected void finalize()
    throws Throwable
  {
    delegate.dispose();
  }
  
  public boolean equals(Object paramObject)
  {
    return delegate.equals(paramObject);
  }
  
  public int hashCode()
  {
    return delegate.hashCode();
  }
  
  public void setConnectTimeout(int paramInt)
  {
    delegate.setConnectTimeout(paramInt);
  }
  
  public int getConnectTimeout()
  {
    return delegate.getConnectTimeout();
  }
  
  public void setReadTimeout(int paramInt)
  {
    delegate.setReadTimeout(paramInt);
  }
  
  public int getReadTimeout()
  {
    return delegate.getReadTimeout();
  }
  
  public void setFixedLengthStreamingMode(int paramInt)
  {
    delegate.setFixedLengthStreamingMode(paramInt);
  }
  
  public void setFixedLengthStreamingMode(long paramLong)
  {
    delegate.setFixedLengthStreamingMode(paramLong);
  }
  
  public void setChunkedStreamingMode(int paramInt)
  {
    delegate.setChunkedStreamingMode(paramInt);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\net\ssl\internal\www\protocol\https\HttpsURLConnectionOldImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */