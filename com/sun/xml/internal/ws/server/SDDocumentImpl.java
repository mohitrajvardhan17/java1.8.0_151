package com.sun.xml.internal.ws.server;

import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.server.DocumentAddressResolver;
import com.sun.xml.internal.ws.api.server.PortAddressResolver;
import com.sun.xml.internal.ws.api.server.SDDocument;
import com.sun.xml.internal.ws.api.server.SDDocument.Schema;
import com.sun.xml.internal.ws.api.server.SDDocument.WSDL;
import com.sun.xml.internal.ws.api.server.SDDocumentFilter;
import com.sun.xml.internal.ws.api.server.SDDocumentSource;
import com.sun.xml.internal.ws.api.streaming.XMLStreamWriterFactory;
import com.sun.xml.internal.ws.util.RuntimeVersion;
import com.sun.xml.internal.ws.util.xml.XMLStreamReaderToXMLStreamWriter;
import com.sun.xml.internal.ws.wsdl.SDDocumentResolver;
import com.sun.xml.internal.ws.wsdl.writer.DocumentLocationResolver;
import com.sun.xml.internal.ws.wsdl.writer.WSDLPatcher;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

public class SDDocumentImpl
  extends SDDocumentSource
  implements SDDocument
{
  private static final String NS_XSD = "http://www.w3.org/2001/XMLSchema";
  private static final QName SCHEMA_INCLUDE_QNAME = new QName("http://www.w3.org/2001/XMLSchema", "include");
  private static final QName SCHEMA_IMPORT_QNAME = new QName("http://www.w3.org/2001/XMLSchema", "import");
  private static final QName SCHEMA_REDEFINE_QNAME = new QName("http://www.w3.org/2001/XMLSchema", "redefine");
  private static final String VERSION_COMMENT = " Published by JAX-WS RI (http://jax-ws.java.net). RI's version is " + RuntimeVersion.VERSION + ". ";
  private final QName rootName;
  private final SDDocumentSource source;
  @Nullable
  List<SDDocumentFilter> filters;
  @Nullable
  SDDocumentResolver sddocResolver;
  private final URL url;
  private final Set<String> imports;
  
  /* Error */
  public static SDDocumentImpl create(SDDocumentSource paramSDDocumentSource, QName paramQName1, QName paramQName2)
  {
    // Byte code:
    //   0: aload_0
    //   1: invokevirtual 279	com/sun/xml/internal/ws/api/server/SDDocumentSource:getSystemId	()Ljava/net/URL;
    //   4: astore_3
    //   5: aload_0
    //   6: invokevirtual 280	com/sun/xml/internal/ws/api/server/SDDocumentSource:read	()Ljavax/xml/stream/XMLStreamReader;
    //   9: astore 4
    //   11: aload 4
    //   13: invokestatic 291	com/sun/xml/internal/ws/streaming/XMLStreamReaderUtil:nextElementContent	(Ljavax/xml/stream/XMLStreamReader;)I
    //   16: pop
    //   17: aload 4
    //   19: invokeinterface 320 1 0
    //   24: astore 5
    //   26: aload 5
    //   28: getstatic 276	com/sun/xml/internal/ws/wsdl/parser/WSDLConstants:QNAME_SCHEMA	Ljavax/xml/namespace/QName;
    //   31: invokevirtual 308	javax/xml/namespace/QName:equals	(Ljava/lang/Object;)Z
    //   34: ifeq +158 -> 192
    //   37: aload 4
    //   39: ldc 14
    //   41: invokestatic 294	com/sun/xml/internal/ws/wsdl/parser/ParserUtil:getMandatoryNonEmptyAttribute	(Ljavax/xml/stream/XMLStreamReader;Ljava/lang/String;)Ljava/lang/String;
    //   44: astore 6
    //   46: new 155	java/util/HashSet
    //   49: dup
    //   50: invokespecial 307	java/util/HashSet:<init>	()V
    //   53: astore 7
    //   55: aload 4
    //   57: invokestatic 290	com/sun/xml/internal/ws/streaming/XMLStreamReaderUtil:nextContent	(Ljavax/xml/stream/XMLStreamReader;)I
    //   60: bipush 8
    //   62: if_icmpeq +103 -> 165
    //   65: aload 4
    //   67: invokeinterface 318 1 0
    //   72: iconst_1
    //   73: if_icmpeq +6 -> 79
    //   76: goto -21 -> 55
    //   79: aload 4
    //   81: invokeinterface 320 1 0
    //   86: astore 8
    //   88: getstatic 269	com/sun/xml/internal/ws/server/SDDocumentImpl:SCHEMA_INCLUDE_QNAME	Ljavax/xml/namespace/QName;
    //   91: aload 8
    //   93: invokevirtual 308	javax/xml/namespace/QName:equals	(Ljava/lang/Object;)Z
    //   96: ifne +25 -> 121
    //   99: getstatic 268	com/sun/xml/internal/ws/server/SDDocumentImpl:SCHEMA_IMPORT_QNAME	Ljavax/xml/namespace/QName;
    //   102: aload 8
    //   104: invokevirtual 308	javax/xml/namespace/QName:equals	(Ljava/lang/Object;)Z
    //   107: ifne +14 -> 121
    //   110: getstatic 270	com/sun/xml/internal/ws/server/SDDocumentImpl:SCHEMA_REDEFINE_QNAME	Ljavax/xml/namespace/QName;
    //   113: aload 8
    //   115: invokevirtual 308	javax/xml/namespace/QName:equals	(Ljava/lang/Object;)Z
    //   118: ifeq +44 -> 162
    //   121: aload 4
    //   123: aconst_null
    //   124: ldc 13
    //   126: invokeinterface 321 3 0
    //   131: astore 9
    //   133: aload 9
    //   135: ifnull +27 -> 162
    //   138: aload 7
    //   140: new 154	java/net/URL
    //   143: dup
    //   144: aload_0
    //   145: invokevirtual 279	com/sun/xml/internal/ws/api/server/SDDocumentSource:getSystemId	()Ljava/net/URL;
    //   148: aload 9
    //   150: invokespecial 306	java/net/URL:<init>	(Ljava/net/URL;Ljava/lang/String;)V
    //   153: invokevirtual 305	java/net/URL:toString	()Ljava/lang/String;
    //   156: invokeinterface 317 2 0
    //   161: pop
    //   162: goto -107 -> 55
    //   165: new 139	com/sun/xml/internal/ws/server/SDDocumentImpl$SchemaImpl
    //   168: dup
    //   169: aload 5
    //   171: aload_3
    //   172: aload_0
    //   173: aload 6
    //   175: aload 7
    //   177: invokespecial 287	com/sun/xml/internal/ws/server/SDDocumentImpl$SchemaImpl:<init>	(Ljavax/xml/namespace/QName;Ljava/net/URL;Lcom/sun/xml/internal/ws/api/server/SDDocumentSource;Ljava/lang/String;Ljava/util/Set;)V
    //   180: astore 8
    //   182: aload 4
    //   184: invokeinterface 319 1 0
    //   189: aload 8
    //   191: areturn
    //   192: aload 5
    //   194: getstatic 273	com/sun/xml/internal/ws/wsdl/parser/WSDLConstants:QNAME_DEFINITIONS	Ljavax/xml/namespace/QName;
    //   197: invokevirtual 308	javax/xml/namespace/QName:equals	(Ljava/lang/Object;)Z
    //   200: ifeq +346 -> 546
    //   203: aload 4
    //   205: ldc 14
    //   207: invokestatic 294	com/sun/xml/internal/ws/wsdl/parser/ParserUtil:getMandatoryNonEmptyAttribute	(Ljavax/xml/stream/XMLStreamReader;Ljava/lang/String;)Ljava/lang/String;
    //   210: astore 6
    //   212: iconst_0
    //   213: istore 7
    //   215: iconst_0
    //   216: istore 8
    //   218: new 155	java/util/HashSet
    //   221: dup
    //   222: invokespecial 307	java/util/HashSet:<init>	()V
    //   225: astore 9
    //   227: new 155	java/util/HashSet
    //   230: dup
    //   231: invokespecial 307	java/util/HashSet:<init>	()V
    //   234: astore 10
    //   236: aload 4
    //   238: invokestatic 290	com/sun/xml/internal/ws/streaming/XMLStreamReaderUtil:nextContent	(Ljavax/xml/stream/XMLStreamReader;)I
    //   241: bipush 8
    //   243: if_icmpeq +270 -> 513
    //   246: aload 4
    //   248: invokeinterface 318 1 0
    //   253: iconst_1
    //   254: if_icmpeq +6 -> 260
    //   257: goto -21 -> 236
    //   260: aload 4
    //   262: invokeinterface 320 1 0
    //   267: astore 11
    //   269: getstatic 275	com/sun/xml/internal/ws/wsdl/parser/WSDLConstants:QNAME_PORT_TYPE	Ljavax/xml/namespace/QName;
    //   272: aload 11
    //   274: invokevirtual 308	javax/xml/namespace/QName:equals	(Ljava/lang/Object;)Z
    //   277: ifeq +46 -> 323
    //   280: aload 4
    //   282: ldc 10
    //   284: invokestatic 294	com/sun/xml/internal/ws/wsdl/parser/ParserUtil:getMandatoryNonEmptyAttribute	(Ljavax/xml/stream/XMLStreamReader;Ljava/lang/String;)Ljava/lang/String;
    //   287: astore 12
    //   289: aload_2
    //   290: ifnull +30 -> 320
    //   293: aload_2
    //   294: invokevirtual 309	javax/xml/namespace/QName:getLocalPart	()Ljava/lang/String;
    //   297: aload 12
    //   299: invokevirtual 300	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   302: ifeq +18 -> 320
    //   305: aload_2
    //   306: invokevirtual 310	javax/xml/namespace/QName:getNamespaceURI	()Ljava/lang/String;
    //   309: aload 6
    //   311: invokevirtual 300	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   314: ifeq +6 -> 320
    //   317: iconst_1
    //   318: istore 7
    //   320: goto +190 -> 510
    //   323: getstatic 277	com/sun/xml/internal/ws/wsdl/parser/WSDLConstants:QNAME_SERVICE	Ljavax/xml/namespace/QName;
    //   326: aload 11
    //   328: invokevirtual 308	javax/xml/namespace/QName:equals	(Ljava/lang/Object;)Z
    //   331: ifeq +50 -> 381
    //   334: aload 4
    //   336: ldc 10
    //   338: invokestatic 294	com/sun/xml/internal/ws/wsdl/parser/ParserUtil:getMandatoryNonEmptyAttribute	(Ljavax/xml/stream/XMLStreamReader;Ljava/lang/String;)Ljava/lang/String;
    //   341: astore 12
    //   343: new 159	javax/xml/namespace/QName
    //   346: dup
    //   347: aload 6
    //   349: aload 12
    //   351: invokespecial 311	javax/xml/namespace/QName:<init>	(Ljava/lang/String;Ljava/lang/String;)V
    //   354: astore 13
    //   356: aload 10
    //   358: aload 13
    //   360: invokeinterface 317 2 0
    //   365: pop
    //   366: aload_1
    //   367: aload 13
    //   369: invokevirtual 308	javax/xml/namespace/QName:equals	(Ljava/lang/Object;)Z
    //   372: ifeq +6 -> 378
    //   375: iconst_1
    //   376: istore 8
    //   378: goto +132 -> 510
    //   381: getstatic 274	com/sun/xml/internal/ws/wsdl/parser/WSDLConstants:QNAME_IMPORT	Ljavax/xml/namespace/QName;
    //   384: aload 11
    //   386: invokevirtual 308	javax/xml/namespace/QName:equals	(Ljava/lang/Object;)Z
    //   389: ifeq +47 -> 436
    //   392: aload 4
    //   394: aconst_null
    //   395: ldc 9
    //   397: invokeinterface 321 3 0
    //   402: astore 12
    //   404: aload 12
    //   406: ifnull +27 -> 433
    //   409: aload 9
    //   411: new 154	java/net/URL
    //   414: dup
    //   415: aload_0
    //   416: invokevirtual 279	com/sun/xml/internal/ws/api/server/SDDocumentSource:getSystemId	()Ljava/net/URL;
    //   419: aload 12
    //   421: invokespecial 306	java/net/URL:<init>	(Ljava/net/URL;Ljava/lang/String;)V
    //   424: invokevirtual 305	java/net/URL:toString	()Ljava/lang/String;
    //   427: invokeinterface 317 2 0
    //   432: pop
    //   433: goto +77 -> 510
    //   436: getstatic 269	com/sun/xml/internal/ws/server/SDDocumentImpl:SCHEMA_INCLUDE_QNAME	Ljavax/xml/namespace/QName;
    //   439: aload 11
    //   441: invokevirtual 308	javax/xml/namespace/QName:equals	(Ljava/lang/Object;)Z
    //   444: ifne +25 -> 469
    //   447: getstatic 268	com/sun/xml/internal/ws/server/SDDocumentImpl:SCHEMA_IMPORT_QNAME	Ljavax/xml/namespace/QName;
    //   450: aload 11
    //   452: invokevirtual 308	javax/xml/namespace/QName:equals	(Ljava/lang/Object;)Z
    //   455: ifne +14 -> 469
    //   458: getstatic 270	com/sun/xml/internal/ws/server/SDDocumentImpl:SCHEMA_REDEFINE_QNAME	Ljavax/xml/namespace/QName;
    //   461: aload 11
    //   463: invokevirtual 308	javax/xml/namespace/QName:equals	(Ljava/lang/Object;)Z
    //   466: ifeq +44 -> 510
    //   469: aload 4
    //   471: aconst_null
    //   472: ldc 13
    //   474: invokeinterface 321 3 0
    //   479: astore 12
    //   481: aload 12
    //   483: ifnull +27 -> 510
    //   486: aload 9
    //   488: new 154	java/net/URL
    //   491: dup
    //   492: aload_0
    //   493: invokevirtual 279	com/sun/xml/internal/ws/api/server/SDDocumentSource:getSystemId	()Ljava/net/URL;
    //   496: aload 12
    //   498: invokespecial 306	java/net/URL:<init>	(Ljava/net/URL;Ljava/lang/String;)V
    //   501: invokevirtual 305	java/net/URL:toString	()Ljava/lang/String;
    //   504: invokeinterface 317 2 0
    //   509: pop
    //   510: goto -274 -> 236
    //   513: new 140	com/sun/xml/internal/ws/server/SDDocumentImpl$WSDLImpl
    //   516: dup
    //   517: aload 5
    //   519: aload_3
    //   520: aload_0
    //   521: aload 6
    //   523: iload 7
    //   525: iload 8
    //   527: aload 9
    //   529: aload 10
    //   531: invokespecial 288	com/sun/xml/internal/ws/server/SDDocumentImpl$WSDLImpl:<init>	(Ljavax/xml/namespace/QName;Ljava/net/URL;Lcom/sun/xml/internal/ws/api/server/SDDocumentSource;Ljava/lang/String;ZZLjava/util/Set;Ljava/util/Set;)V
    //   534: astore 11
    //   536: aload 4
    //   538: invokeinterface 319 1 0
    //   543: aload 11
    //   545: areturn
    //   546: new 137	com/sun/xml/internal/ws/server/SDDocumentImpl
    //   549: dup
    //   550: aload 5
    //   552: aload_3
    //   553: aload_0
    //   554: invokespecial 283	com/sun/xml/internal/ws/server/SDDocumentImpl:<init>	(Ljavax/xml/namespace/QName;Ljava/net/URL;Lcom/sun/xml/internal/ws/api/server/SDDocumentSource;)V
    //   557: astore 6
    //   559: aload 4
    //   561: invokeinterface 319 1 0
    //   566: aload 6
    //   568: areturn
    //   569: astore 14
    //   571: aload 4
    //   573: invokeinterface 319 1 0
    //   578: aload 14
    //   580: athrow
    //   581: astore 4
    //   583: new 141	com/sun/xml/internal/ws/server/ServerRtException
    //   586: dup
    //   587: ldc 12
    //   589: iconst_2
    //   590: anewarray 150	java/lang/Object
    //   593: dup
    //   594: iconst_0
    //   595: aload_3
    //   596: aastore
    //   597: dup
    //   598: iconst_1
    //   599: aload 4
    //   601: aastore
    //   602: invokespecial 289	com/sun/xml/internal/ws/server/ServerRtException:<init>	(Ljava/lang/String;[Ljava/lang/Object;)V
    //   605: athrow
    //   606: astore 4
    //   608: new 141	com/sun/xml/internal/ws/server/ServerRtException
    //   611: dup
    //   612: ldc 12
    //   614: iconst_2
    //   615: anewarray 150	java/lang/Object
    //   618: dup
    //   619: iconst_0
    //   620: aload_3
    //   621: aastore
    //   622: dup
    //   623: iconst_1
    //   624: aload 4
    //   626: aastore
    //   627: invokespecial 289	com/sun/xml/internal/ws/server/ServerRtException:<init>	(Ljava/lang/String;[Ljava/lang/Object;)V
    //   630: athrow
    //   631: astore 4
    //   633: new 141	com/sun/xml/internal/ws/server/ServerRtException
    //   636: dup
    //   637: ldc 12
    //   639: iconst_2
    //   640: anewarray 150	java/lang/Object
    //   643: dup
    //   644: iconst_0
    //   645: aload_3
    //   646: aastore
    //   647: dup
    //   648: iconst_1
    //   649: aload 4
    //   651: aastore
    //   652: invokespecial 289	com/sun/xml/internal/ws/server/ServerRtException:<init>	(Ljava/lang/String;[Ljava/lang/Object;)V
    //   655: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	656	0	paramSDDocumentSource	SDDocumentSource
    //   0	656	1	paramQName1	QName
    //   0	656	2	paramQName2	QName
    //   4	642	3	localURL	URL
    //   9	563	4	localXMLStreamReader	XMLStreamReader
    //   581	19	4	localWebServiceException	javax.xml.ws.WebServiceException
    //   606	19	4	localIOException	IOException
    //   631	19	4	localXMLStreamException	XMLStreamException
    //   24	527	5	localQName1	QName
    //   44	523	6	localObject1	Object
    //   53	123	7	localHashSet1	HashSet
    //   213	311	7	bool1	boolean
    //   86	104	8	localObject2	Object
    //   216	310	8	bool2	boolean
    //   131	397	9	localObject3	Object
    //   234	296	10	localHashSet2	HashSet
    //   267	277	11	localObject4	Object
    //   287	210	12	str	String
    //   354	14	13	localQName2	QName
    //   569	10	14	localObject5	Object
    // Exception table:
    //   from	to	target	type
    //   11	182	569	finally
    //   192	536	569	finally
    //   546	559	569	finally
    //   569	571	569	finally
    //   5	189	581	javax/xml/ws/WebServiceException
    //   192	543	581	javax/xml/ws/WebServiceException
    //   546	566	581	javax/xml/ws/WebServiceException
    //   569	581	581	javax/xml/ws/WebServiceException
    //   5	189	606	java/io/IOException
    //   192	543	606	java/io/IOException
    //   546	566	606	java/io/IOException
    //   569	581	606	java/io/IOException
    //   5	189	631	javax/xml/stream/XMLStreamException
    //   192	543	631	javax/xml/stream/XMLStreamException
    //   546	566	631	javax/xml/stream/XMLStreamException
    //   569	581	631	javax/xml/stream/XMLStreamException
  }
  
  protected SDDocumentImpl(QName paramQName, URL paramURL, SDDocumentSource paramSDDocumentSource)
  {
    this(paramQName, paramURL, paramSDDocumentSource, new HashSet());
  }
  
  protected SDDocumentImpl(QName paramQName, URL paramURL, SDDocumentSource paramSDDocumentSource, Set<String> paramSet)
  {
    if (paramURL == null) {
      throw new IllegalArgumentException("Cannot construct SDDocument with null URL.");
    }
    rootName = paramQName;
    source = paramSDDocumentSource;
    url = paramURL;
    imports = paramSet;
  }
  
  void setFilters(List<SDDocumentFilter> paramList)
  {
    filters = paramList;
  }
  
  void setResolver(SDDocumentResolver paramSDDocumentResolver)
  {
    sddocResolver = paramSDDocumentResolver;
  }
  
  public QName getRootName()
  {
    return rootName;
  }
  
  public boolean isWSDL()
  {
    return false;
  }
  
  public boolean isSchema()
  {
    return false;
  }
  
  public URL getURL()
  {
    return url;
  }
  
  public XMLStreamReader read(XMLInputFactory paramXMLInputFactory)
    throws IOException, XMLStreamException
  {
    return source.read(paramXMLInputFactory);
  }
  
  public XMLStreamReader read()
    throws IOException, XMLStreamException
  {
    return source.read();
  }
  
  public URL getSystemId()
  {
    return url;
  }
  
  public Set<String> getImports()
  {
    return imports;
  }
  
  public void writeTo(OutputStream paramOutputStream)
    throws IOException
  {
    XMLStreamWriter localXMLStreamWriter = null;
    try
    {
      localXMLStreamWriter = XMLStreamWriterFactory.create(paramOutputStream, "UTF-8");
      localXMLStreamWriter.writeStartDocument("UTF-8", "1.0");
      new XMLStreamReaderToXMLStreamWriter().bridge(source.read(), localXMLStreamWriter);
      localXMLStreamWriter.writeEndDocument();
      IOException localIOException1;
      IOException localIOException2;
      return;
    }
    catch (XMLStreamException localXMLStreamException2)
    {
      localIOException1 = new IOException(localXMLStreamException2.getMessage());
      localIOException1.initCause(localXMLStreamException2);
      throw localIOException1;
    }
    finally
    {
      try
      {
        if (localXMLStreamWriter != null) {
          localXMLStreamWriter.close();
        }
      }
      catch (XMLStreamException localXMLStreamException3)
      {
        localIOException2 = new IOException(localXMLStreamException3.getMessage());
        localIOException2.initCause(localXMLStreamException3);
        throw localIOException2;
      }
    }
  }
  
  public void writeTo(PortAddressResolver paramPortAddressResolver, DocumentAddressResolver paramDocumentAddressResolver, OutputStream paramOutputStream)
    throws IOException
  {
    XMLStreamWriter localXMLStreamWriter = null;
    try
    {
      localXMLStreamWriter = XMLStreamWriterFactory.create(paramOutputStream, "UTF-8");
      localXMLStreamWriter.writeStartDocument("UTF-8", "1.0");
      writeTo(paramPortAddressResolver, paramDocumentAddressResolver, localXMLStreamWriter);
      localXMLStreamWriter.writeEndDocument();
      IOException localIOException1;
      IOException localIOException2;
      return;
    }
    catch (XMLStreamException localXMLStreamException2)
    {
      localIOException1 = new IOException(localXMLStreamException2.getMessage());
      localIOException1.initCause(localXMLStreamException2);
      throw localIOException1;
    }
    finally
    {
      try
      {
        if (localXMLStreamWriter != null) {
          localXMLStreamWriter.close();
        }
      }
      catch (XMLStreamException localXMLStreamException3)
      {
        localIOException2 = new IOException(localXMLStreamException3.getMessage());
        localIOException2.initCause(localXMLStreamException3);
        throw localIOException2;
      }
    }
  }
  
  public void writeTo(PortAddressResolver paramPortAddressResolver, DocumentAddressResolver paramDocumentAddressResolver, XMLStreamWriter paramXMLStreamWriter)
    throws XMLStreamException, IOException
  {
    if (filters != null)
    {
      localObject1 = filters.iterator();
      while (((Iterator)localObject1).hasNext())
      {
        SDDocumentFilter localSDDocumentFilter = (SDDocumentFilter)((Iterator)localObject1).next();
        paramXMLStreamWriter = localSDDocumentFilter.filter(this, paramXMLStreamWriter);
      }
    }
    Object localObject1 = source.read();
    try
    {
      paramXMLStreamWriter.writeComment(VERSION_COMMENT);
      new WSDLPatcher(paramPortAddressResolver, new DocumentLocationResolverImpl(paramDocumentAddressResolver)).bridge((XMLStreamReader)localObject1, paramXMLStreamWriter);
    }
    finally
    {
      ((XMLStreamReader)localObject1).close();
    }
  }
  
  private class DocumentLocationResolverImpl
    implements DocumentLocationResolver
  {
    private DocumentAddressResolver delegate;
    
    DocumentLocationResolverImpl(DocumentAddressResolver paramDocumentAddressResolver)
    {
      delegate = paramDocumentAddressResolver;
    }
    
    public String getLocationFor(String paramString1, String paramString2)
    {
      if (sddocResolver == null) {
        return paramString2;
      }
      try
      {
        URL localURL = new URL(getURL(), paramString2);
        SDDocument localSDDocument = sddocResolver.resolve(localURL.toExternalForm());
        if (localSDDocument == null) {
          return paramString2;
        }
        return delegate.getRelativeAddressFor(SDDocumentImpl.this, localSDDocument);
      }
      catch (MalformedURLException localMalformedURLException) {}
      return null;
    }
  }
  
  private static final class SchemaImpl
    extends SDDocumentImpl
    implements SDDocument.Schema
  {
    private final String targetNamespace;
    
    public SchemaImpl(QName paramQName, URL paramURL, SDDocumentSource paramSDDocumentSource, String paramString, Set<String> paramSet)
    {
      super(paramURL, paramSDDocumentSource, paramSet);
      targetNamespace = paramString;
    }
    
    public String getTargetNamespace()
    {
      return targetNamespace;
    }
    
    public boolean isSchema()
    {
      return true;
    }
  }
  
  private static final class WSDLImpl
    extends SDDocumentImpl
    implements SDDocument.WSDL
  {
    private final String targetNamespace;
    private final boolean hasPortType;
    private final boolean hasService;
    private final Set<QName> allServices;
    
    public WSDLImpl(QName paramQName, URL paramURL, SDDocumentSource paramSDDocumentSource, String paramString, boolean paramBoolean1, boolean paramBoolean2, Set<String> paramSet, Set<QName> paramSet1)
    {
      super(paramURL, paramSDDocumentSource, paramSet);
      targetNamespace = paramString;
      hasPortType = paramBoolean1;
      hasService = paramBoolean2;
      allServices = paramSet1;
    }
    
    public String getTargetNamespace()
    {
      return targetNamespace;
    }
    
    public boolean hasPortType()
    {
      return hasPortType;
    }
    
    public boolean hasService()
    {
      return hasService;
    }
    
    public Set<QName> getAllServices()
    {
      return allServices;
    }
    
    public boolean isWSDL()
    {
      return true;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\server\SDDocumentImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */