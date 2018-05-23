package javax.activation;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.net.URL;

public class DataHandler
  implements Transferable
{
  private DataSource dataSource = null;
  private DataSource objDataSource = null;
  private Object object = null;
  private String objectMimeType = null;
  private CommandMap currentCommandMap = null;
  private static final DataFlavor[] emptyFlavors = new DataFlavor[0];
  private DataFlavor[] transferFlavors = emptyFlavors;
  private DataContentHandler dataContentHandler = null;
  private DataContentHandler factoryDCH = null;
  private static DataContentHandlerFactory factory = null;
  private DataContentHandlerFactory oldFactory = null;
  private String shortType = null;
  
  public DataHandler(DataSource paramDataSource)
  {
    dataSource = paramDataSource;
    oldFactory = factory;
  }
  
  public DataHandler(Object paramObject, String paramString)
  {
    object = paramObject;
    objectMimeType = paramString;
    oldFactory = factory;
  }
  
  public DataHandler(URL paramURL)
  {
    dataSource = new URLDataSource(paramURL);
    oldFactory = factory;
  }
  
  private synchronized CommandMap getCommandMap()
  {
    if (currentCommandMap != null) {
      return currentCommandMap;
    }
    return CommandMap.getDefaultCommandMap();
  }
  
  public DataSource getDataSource()
  {
    if (dataSource == null)
    {
      if (objDataSource == null) {
        objDataSource = new DataHandlerDataSource(this);
      }
      return objDataSource;
    }
    return dataSource;
  }
  
  public String getName()
  {
    if (dataSource != null) {
      return dataSource.getName();
    }
    return null;
  }
  
  public String getContentType()
  {
    if (dataSource != null) {
      return dataSource.getContentType();
    }
    return objectMimeType;
  }
  
  public InputStream getInputStream()
    throws IOException
  {
    Object localObject = null;
    if (dataSource != null)
    {
      localObject = dataSource.getInputStream();
    }
    else
    {
      DataContentHandler localDataContentHandler1 = getDataContentHandler();
      if (localDataContentHandler1 == null) {
        throw new UnsupportedDataTypeException("no DCH for MIME type " + getBaseType());
      }
      if (((localDataContentHandler1 instanceof ObjectDataContentHandler)) && (((ObjectDataContentHandler)localDataContentHandler1).getDCH() == null)) {
        throw new UnsupportedDataTypeException("no object DCH for MIME type " + getBaseType());
      }
      final DataContentHandler localDataContentHandler2 = localDataContentHandler1;
      final PipedOutputStream localPipedOutputStream = new PipedOutputStream();
      PipedInputStream localPipedInputStream = new PipedInputStream(localPipedOutputStream);
      new Thread(new Runnable()
      {
        /* Error */
        public void run()
        {
          // Byte code:
          //   0: aload_0
          //   1: getfield 50	javax/activation/DataHandler$1:val$fdch	Ljavax/activation/DataContentHandler;
          //   4: aload_0
          //   5: getfield 51	javax/activation/DataHandler$1:this$0	Ljavax/activation/DataHandler;
          //   8: invokestatic 54	javax/activation/DataHandler:access$000	(Ljavax/activation/DataHandler;)Ljava/lang/Object;
          //   11: aload_0
          //   12: getfield 51	javax/activation/DataHandler$1:this$0	Ljavax/activation/DataHandler;
          //   15: invokestatic 55	javax/activation/DataHandler:access$100	(Ljavax/activation/DataHandler;)Ljava/lang/String;
          //   18: aload_0
          //   19: getfield 49	javax/activation/DataHandler$1:val$pos	Ljava/io/PipedOutputStream;
          //   22: invokeinterface 56 4 0
          //   27: aload_0
          //   28: getfield 49	javax/activation/DataHandler$1:val$pos	Ljava/io/PipedOutputStream;
          //   31: invokevirtual 52	java/io/PipedOutputStream:close	()V
          //   34: goto +36 -> 70
          //   37: astore_1
          //   38: goto +32 -> 70
          //   41: astore_1
          //   42: aload_0
          //   43: getfield 49	javax/activation/DataHandler$1:val$pos	Ljava/io/PipedOutputStream;
          //   46: invokevirtual 52	java/io/PipedOutputStream:close	()V
          //   49: goto +21 -> 70
          //   52: astore_1
          //   53: goto +17 -> 70
          //   56: astore_2
          //   57: aload_0
          //   58: getfield 49	javax/activation/DataHandler$1:val$pos	Ljava/io/PipedOutputStream;
          //   61: invokevirtual 52	java/io/PipedOutputStream:close	()V
          //   64: goto +4 -> 68
          //   67: astore_3
          //   68: aload_2
          //   69: athrow
          //   70: return
          // Local variable table:
          //   start	length	slot	name	signature
          //   0	71	0	this	1
          //   37	1	1	localIOException1	IOException
          //   41	1	1	localIOException2	IOException
          //   52	1	1	localIOException3	IOException
          //   56	13	2	localObject	Object
          //   67	1	3	localIOException4	IOException
          // Exception table:
          //   from	to	target	type
          //   27	34	37	java/io/IOException
          //   0	27	41	java/io/IOException
          //   42	49	52	java/io/IOException
          //   0	27	56	finally
          //   57	64	67	java/io/IOException
        }
      }, "DataHandler.getInputStream").start();
      localObject = localPipedInputStream;
    }
    return (InputStream)localObject;
  }
  
  public void writeTo(OutputStream paramOutputStream)
    throws IOException
  {
    Object localObject1;
    if (dataSource != null)
    {
      localObject1 = null;
      byte[] arrayOfByte = new byte[' '];
      localObject1 = dataSource.getInputStream();
      try
      {
        int i;
        while ((i = ((InputStream)localObject1).read(arrayOfByte)) > 0) {
          paramOutputStream.write(arrayOfByte, 0, i);
        }
      }
      finally
      {
        ((InputStream)localObject1).close();
        localObject1 = null;
      }
    }
    else
    {
      localObject1 = getDataContentHandler();
      ((DataContentHandler)localObject1).writeTo(object, objectMimeType, paramOutputStream);
    }
  }
  
  public OutputStream getOutputStream()
    throws IOException
  {
    if (dataSource != null) {
      return dataSource.getOutputStream();
    }
    return null;
  }
  
  public synchronized DataFlavor[] getTransferDataFlavors()
  {
    if (factory != oldFactory) {
      transferFlavors = emptyFlavors;
    }
    if (transferFlavors == emptyFlavors) {
      transferFlavors = getDataContentHandler().getTransferDataFlavors();
    }
    if (transferFlavors == emptyFlavors) {
      return transferFlavors;
    }
    return (DataFlavor[])transferFlavors.clone();
  }
  
  public boolean isDataFlavorSupported(DataFlavor paramDataFlavor)
  {
    DataFlavor[] arrayOfDataFlavor = getTransferDataFlavors();
    for (int i = 0; i < arrayOfDataFlavor.length; i++) {
      if (arrayOfDataFlavor[i].equals(paramDataFlavor)) {
        return true;
      }
    }
    return false;
  }
  
  public Object getTransferData(DataFlavor paramDataFlavor)
    throws UnsupportedFlavorException, IOException
  {
    return getDataContentHandler().getTransferData(paramDataFlavor, dataSource);
  }
  
  public synchronized void setCommandMap(CommandMap paramCommandMap)
  {
    if ((paramCommandMap != currentCommandMap) || (paramCommandMap == null))
    {
      transferFlavors = emptyFlavors;
      dataContentHandler = null;
      currentCommandMap = paramCommandMap;
    }
  }
  
  public CommandInfo[] getPreferredCommands()
  {
    if (dataSource != null) {
      return getCommandMap().getPreferredCommands(getBaseType(), dataSource);
    }
    return getCommandMap().getPreferredCommands(getBaseType());
  }
  
  public CommandInfo[] getAllCommands()
  {
    if (dataSource != null) {
      return getCommandMap().getAllCommands(getBaseType(), dataSource);
    }
    return getCommandMap().getAllCommands(getBaseType());
  }
  
  public CommandInfo getCommand(String paramString)
  {
    if (dataSource != null) {
      return getCommandMap().getCommand(getBaseType(), paramString, dataSource);
    }
    return getCommandMap().getCommand(getBaseType(), paramString);
  }
  
  public Object getContent()
    throws IOException
  {
    if (object != null) {
      return object;
    }
    return getDataContentHandler().getContent(getDataSource());
  }
  
  public Object getBean(CommandInfo paramCommandInfo)
  {
    Object localObject = null;
    try
    {
      ClassLoader localClassLoader = null;
      localClassLoader = SecuritySupport.getContextClassLoader();
      if (localClassLoader == null) {
        localClassLoader = getClass().getClassLoader();
      }
      localObject = paramCommandInfo.getCommandObject(this, localClassLoader);
    }
    catch (IOException localIOException) {}catch (ClassNotFoundException localClassNotFoundException) {}
    return localObject;
  }
  
  private synchronized DataContentHandler getDataContentHandler()
  {
    if (factory != oldFactory)
    {
      oldFactory = factory;
      factoryDCH = null;
      dataContentHandler = null;
      transferFlavors = emptyFlavors;
    }
    if (dataContentHandler != null) {
      return dataContentHandler;
    }
    String str = getBaseType();
    if ((factoryDCH == null) && (factory != null)) {
      factoryDCH = factory.createDataContentHandler(str);
    }
    if (factoryDCH != null) {
      dataContentHandler = factoryDCH;
    }
    if (dataContentHandler == null) {
      if (dataSource != null) {
        dataContentHandler = getCommandMap().createDataContentHandler(str, dataSource);
      } else {
        dataContentHandler = getCommandMap().createDataContentHandler(str);
      }
    }
    if (dataSource != null) {
      dataContentHandler = new DataSourceDataContentHandler(dataContentHandler, dataSource);
    } else {
      dataContentHandler = new ObjectDataContentHandler(dataContentHandler, object, objectMimeType);
    }
    return dataContentHandler;
  }
  
  private synchronized String getBaseType()
  {
    if (shortType == null)
    {
      String str = getContentType();
      try
      {
        MimeType localMimeType = new MimeType(str);
        shortType = localMimeType.getBaseType();
      }
      catch (MimeTypeParseException localMimeTypeParseException)
      {
        shortType = str;
      }
    }
    return shortType;
  }
  
  public static synchronized void setDataContentHandlerFactory(DataContentHandlerFactory paramDataContentHandlerFactory)
  {
    if (factory != null) {
      throw new Error("DataContentHandlerFactory already defined");
    }
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      try
      {
        localSecurityManager.checkSetFactory();
      }
      catch (SecurityException localSecurityException)
      {
        if (DataHandler.class.getClassLoader() != paramDataContentHandlerFactory.getClass().getClassLoader()) {
          throw localSecurityException;
        }
      }
    }
    factory = paramDataContentHandlerFactory;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\activation\DataHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */