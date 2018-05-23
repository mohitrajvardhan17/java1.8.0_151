package com.sun.xml.internal.bind.v2.model.impl;

import com.sun.istack.internal.ByteArrayDataSource;
import com.sun.xml.internal.bind.DatatypeConverterImpl;
import com.sun.xml.internal.bind.WhiteSpaceProcessor;
import com.sun.xml.internal.bind.api.AccessorException;
import com.sun.xml.internal.bind.v2.TODO;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeBuiltinLeafInfo;
import com.sun.xml.internal.bind.v2.runtime.Name;
import com.sun.xml.internal.bind.v2.runtime.NamespaceContext2;
import com.sun.xml.internal.bind.v2.runtime.Transducer;
import com.sun.xml.internal.bind.v2.runtime.XMLSerializer;
import com.sun.xml.internal.bind.v2.runtime.output.Pcdata;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.Base64Data;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.UnmarshallingContext;
import com.sun.xml.internal.bind.v2.util.ByteArrayOutputStreamEx;
import com.sun.xml.internal.bind.v2.util.DataSourceSource;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import javax.xml.bind.MarshalException;
import javax.xml.bind.helpers.ValidationEventImpl;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;
import org.xml.sax.SAXException;

public abstract class RuntimeBuiltinLeafInfoImpl<T>
  extends BuiltinLeafInfoImpl<Type, Class>
  implements RuntimeBuiltinLeafInfo, Transducer<T>
{
  public static final Map<Type, RuntimeBuiltinLeafInfoImpl<?>> LEAVES = new HashMap();
  public static final RuntimeBuiltinLeafInfoImpl<String> STRING;
  private static final String DATE = "date";
  public static final List<RuntimeBuiltinLeafInfoImpl<?>> builtinBeanInfos;
  public static final String MAP_ANYURI_TO_URI = "mapAnyUriToUri";
  private static final Map<QName, String> xmlGregorianCalendarFormatString;
  private static final Map<QName, Integer> xmlGregorianCalendarFieldRef;
  
  private RuntimeBuiltinLeafInfoImpl(Class paramClass, QName... paramVarArgs)
  {
    super(paramClass, paramVarArgs);
    LEAVES.put(paramClass, this);
  }
  
  public final Class getClazz()
  {
    return (Class)getType();
  }
  
  public final Transducer getTransducer()
  {
    return this;
  }
  
  public boolean useNamespace()
  {
    return false;
  }
  
  public final boolean isDefault()
  {
    return true;
  }
  
  public void declareNamespace(T paramT, XMLSerializer paramXMLSerializer)
    throws AccessorException
  {}
  
  public QName getTypeName(T paramT)
  {
    return null;
  }
  
  private static QName createXS(String paramString)
  {
    return new QName("http://www.w3.org/2001/XMLSchema", paramString);
  }
  
  private static byte[] decodeBase64(CharSequence paramCharSequence)
  {
    if ((paramCharSequence instanceof Base64Data))
    {
      Base64Data localBase64Data = (Base64Data)paramCharSequence;
      return localBase64Data.getExact();
    }
    return DatatypeConverterImpl._parseBase64Binary(paramCharSequence.toString());
  }
  
  private static void checkXmlGregorianCalendarFieldRef(QName paramQName, XMLGregorianCalendar paramXMLGregorianCalendar)
    throws MarshalException
  {
    StringBuilder localStringBuilder = new StringBuilder();
    int i = ((Integer)xmlGregorianCalendarFieldRef.get(paramQName)).intValue();
    int j = 1;
    int k = 0;
    while (i != 0)
    {
      int m = i & 0x1;
      i >>>= 4;
      k++;
      if (m == 1) {
        switch (k)
        {
        case 1: 
          if (paramXMLGregorianCalendar.getSecond() == Integer.MIN_VALUE) {
            localStringBuilder.append("  ").append(Messages.XMLGREGORIANCALENDAR_SEC);
          }
          break;
        case 2: 
          if (paramXMLGregorianCalendar.getMinute() == Integer.MIN_VALUE) {
            localStringBuilder.append("  ").append(Messages.XMLGREGORIANCALENDAR_MIN);
          }
          break;
        case 3: 
          if (paramXMLGregorianCalendar.getHour() == Integer.MIN_VALUE) {
            localStringBuilder.append("  ").append(Messages.XMLGREGORIANCALENDAR_HR);
          }
          break;
        case 4: 
          if (paramXMLGregorianCalendar.getDay() == Integer.MIN_VALUE) {
            localStringBuilder.append("  ").append(Messages.XMLGREGORIANCALENDAR_DAY);
          }
          break;
        case 5: 
          if (paramXMLGregorianCalendar.getMonth() == Integer.MIN_VALUE) {
            localStringBuilder.append("  ").append(Messages.XMLGREGORIANCALENDAR_MONTH);
          }
          break;
        case 6: 
          if (paramXMLGregorianCalendar.getYear() == Integer.MIN_VALUE) {
            localStringBuilder.append("  ").append(Messages.XMLGREGORIANCALENDAR_YEAR);
          }
          break;
        }
      }
    }
    if (localStringBuilder.length() > 0) {
      throw new MarshalException(Messages.XMLGREGORIANCALENDAR_INVALID.format(new Object[] { paramQName.getLocalPart() }) + localStringBuilder.toString());
    }
  }
  
  static
  {
    Object localObject = (String)AccessController.doPrivileged(new PrivilegedAction()
    {
      public String run()
      {
        return System.getProperty("mapAnyUriToUri");
      }
    });
    QName[] arrayOfQName = { createXS("string"), createXS("anySimpleType"), createXS("normalizedString"), createXS("token"), createXS("language"), createXS("Name"), createXS("NCName"), createXS("NMTOKEN"), localObject == null ? new QName[] { createXS("string"), createXS("anySimpleType"), createXS("normalizedString"), createXS("anyURI"), createXS("token"), createXS("language"), createXS("Name"), createXS("NCName"), createXS("NMTOKEN"), createXS("ENTITY") } : createXS("ENTITY") };
    STRING = new StringImplImpl(String.class, arrayOfQName);
    ArrayList localArrayList1 = new ArrayList();
    localArrayList1.add(new StringImpl(Character.class, new QName[] { createXS("unsignedShort") })
    {
      public Character parse(CharSequence paramAnonymousCharSequence)
      {
        return Character.valueOf((char)DatatypeConverterImpl._parseInt(paramAnonymousCharSequence));
      }
      
      public String print(Character paramAnonymousCharacter)
      {
        return Integer.toString(paramAnonymousCharacter.charValue());
      }
    });
    localArrayList1.add(new StringImpl(Calendar.class, new QName[] { DatatypeConstants.DATETIME })
    {
      public Calendar parse(CharSequence paramAnonymousCharSequence)
      {
        return DatatypeConverterImpl._parseDateTime(paramAnonymousCharSequence.toString());
      }
      
      public String print(Calendar paramAnonymousCalendar)
      {
        return DatatypeConverterImpl._printDateTime(paramAnonymousCalendar);
      }
    });
    localArrayList1.add(new StringImpl(GregorianCalendar.class, new QName[] { DatatypeConstants.DATETIME })
    {
      public GregorianCalendar parse(CharSequence paramAnonymousCharSequence)
      {
        return DatatypeConverterImpl._parseDateTime(paramAnonymousCharSequence.toString());
      }
      
      public String print(GregorianCalendar paramAnonymousGregorianCalendar)
      {
        return DatatypeConverterImpl._printDateTime(paramAnonymousGregorianCalendar);
      }
    });
    localArrayList1.add(new StringImpl(Date.class, new QName[] { DatatypeConstants.DATETIME })
    {
      public Date parse(CharSequence paramAnonymousCharSequence)
      {
        return DatatypeConverterImpl._parseDateTime(paramAnonymousCharSequence.toString()).getTime();
      }
      
      public String print(Date paramAnonymousDate)
      {
        XMLSerializer localXMLSerializer = XMLSerializer.getInstance();
        QName localQName = localXMLSerializer.getSchemaType();
        GregorianCalendar localGregorianCalendar = new GregorianCalendar(0, 0, 0);
        localGregorianCalendar.setTime(paramAnonymousDate);
        if ((localQName != null) && ("http://www.w3.org/2001/XMLSchema".equals(localQName.getNamespaceURI())) && ("date".equals(localQName.getLocalPart()))) {
          return DatatypeConverterImpl._printDate(localGregorianCalendar);
        }
        return DatatypeConverterImpl._printDateTime(localGregorianCalendar);
      }
    });
    localArrayList1.add(new StringImpl(File.class, new QName[] { createXS("string") })
    {
      public File parse(CharSequence paramAnonymousCharSequence)
      {
        return new File(WhiteSpaceProcessor.trim(paramAnonymousCharSequence).toString());
      }
      
      public String print(File paramAnonymousFile)
      {
        return paramAnonymousFile.getPath();
      }
    });
    localArrayList1.add(new StringImpl(URL.class, new QName[] { createXS("anyURI") })
    {
      public URL parse(CharSequence paramAnonymousCharSequence)
        throws SAXException
      {
        TODO.checkSpec("JSR222 Issue #42");
        try
        {
          return new URL(WhiteSpaceProcessor.trim(paramAnonymousCharSequence).toString());
        }
        catch (MalformedURLException localMalformedURLException)
        {
          UnmarshallingContext.getInstance().handleError(localMalformedURLException);
        }
        return null;
      }
      
      public String print(URL paramAnonymousURL)
      {
        return paramAnonymousURL.toExternalForm();
      }
    });
    if (localObject == null) {
      localArrayList1.add(new StringImpl(URI.class, new QName[] { createXS("string") })
      {
        public URI parse(CharSequence paramAnonymousCharSequence)
          throws SAXException
        {
          try
          {
            return new URI(paramAnonymousCharSequence.toString());
          }
          catch (URISyntaxException localURISyntaxException)
          {
            UnmarshallingContext.getInstance().handleError(localURISyntaxException);
          }
          return null;
        }
        
        public String print(URI paramAnonymousURI)
        {
          return paramAnonymousURI.toString();
        }
      });
    }
    localArrayList1.add(new StringImpl(Class.class, new QName[] { createXS("string") })
    {
      public Class parse(CharSequence paramAnonymousCharSequence)
        throws SAXException
      {
        TODO.checkSpec("JSR222 Issue #42");
        try
        {
          String str = WhiteSpaceProcessor.trim(paramAnonymousCharSequence).toString();
          ClassLoader localClassLoader = getInstanceclassLoader;
          if (localClassLoader == null) {
            localClassLoader = Thread.currentThread().getContextClassLoader();
          }
          if (localClassLoader != null) {
            return localClassLoader.loadClass(str);
          }
          return Class.forName(str);
        }
        catch (ClassNotFoundException localClassNotFoundException)
        {
          UnmarshallingContext.getInstance().handleError(localClassNotFoundException);
        }
        return null;
      }
      
      public String print(Class paramAnonymousClass)
      {
        return paramAnonymousClass.getName();
      }
    });
    localArrayList1.add(new PcdataImpl(Image.class, new QName[] { createXS("base64Binary") })
    {
      public Image parse(CharSequence paramAnonymousCharSequence)
        throws SAXException
      {
        try
        {
          Object localObject1;
          if ((paramAnonymousCharSequence instanceof Base64Data)) {
            localObject1 = ((Base64Data)paramAnonymousCharSequence).getInputStream();
          } else {
            localObject1 = new ByteArrayInputStream(RuntimeBuiltinLeafInfoImpl.decodeBase64(paramAnonymousCharSequence));
          }
          try
          {
            BufferedImage localBufferedImage = ImageIO.read((InputStream)localObject1);
            return localBufferedImage;
          }
          finally
          {
            ((InputStream)localObject1).close();
          }
          return null;
        }
        catch (IOException localIOException)
        {
          UnmarshallingContext.getInstance().handleError(localIOException);
        }
      }
      
      private BufferedImage convertToBufferedImage(Image paramAnonymousImage)
        throws IOException
      {
        if ((paramAnonymousImage instanceof BufferedImage)) {
          return (BufferedImage)paramAnonymousImage;
        }
        MediaTracker localMediaTracker = new MediaTracker(new Component() {});
        localMediaTracker.addImage(paramAnonymousImage, 0);
        try
        {
          localMediaTracker.waitForAll();
        }
        catch (InterruptedException localInterruptedException)
        {
          throw new IOException(localInterruptedException.getMessage());
        }
        BufferedImage localBufferedImage = new BufferedImage(paramAnonymousImage.getWidth(null), paramAnonymousImage.getHeight(null), 2);
        Graphics2D localGraphics2D = localBufferedImage.createGraphics();
        localGraphics2D.drawImage(paramAnonymousImage, 0, 0, null);
        return localBufferedImage;
      }
      
      public Base64Data print(Image paramAnonymousImage)
      {
        ByteArrayOutputStreamEx localByteArrayOutputStreamEx = new ByteArrayOutputStreamEx();
        XMLSerializer localXMLSerializer = XMLSerializer.getInstance();
        String str = localXMLSerializer.getXMIMEContentType();
        if ((str == null) || (str.startsWith("image/*"))) {
          str = "image/png";
        }
        try
        {
          Iterator localIterator = ImageIO.getImageWritersByMIMEType(str);
          if (localIterator.hasNext())
          {
            ImageWriter localImageWriter = (ImageWriter)localIterator.next();
            ImageOutputStream localImageOutputStream = ImageIO.createImageOutputStream(localByteArrayOutputStreamEx);
            localImageWriter.setOutput(localImageOutputStream);
            localImageWriter.write(convertToBufferedImage(paramAnonymousImage));
            localImageOutputStream.close();
            localImageWriter.dispose();
          }
          else
          {
            localXMLSerializer.handleEvent(new ValidationEventImpl(1, Messages.NO_IMAGE_WRITER.format(new Object[] { str }), localXMLSerializer.getCurrentLocation(null)));
            throw new RuntimeException("no encoder for MIME type " + str);
          }
        }
        catch (IOException localIOException)
        {
          localXMLSerializer.handleError(localIOException);
          throw new RuntimeException(localIOException);
        }
        Base64Data localBase64Data = new Base64Data();
        localByteArrayOutputStreamEx.set(localBase64Data, str);
        return localBase64Data;
      }
    });
    localArrayList1.add(new PcdataImpl(DataHandler.class, new QName[] { createXS("base64Binary") })
    {
      public DataHandler parse(CharSequence paramAnonymousCharSequence)
      {
        if ((paramAnonymousCharSequence instanceof Base64Data)) {
          return ((Base64Data)paramAnonymousCharSequence).getDataHandler();
        }
        return new DataHandler(new ByteArrayDataSource(RuntimeBuiltinLeafInfoImpl.decodeBase64(paramAnonymousCharSequence), UnmarshallingContext.getInstance().getXMIMEContentType()));
      }
      
      public Base64Data print(DataHandler paramAnonymousDataHandler)
      {
        Base64Data localBase64Data = new Base64Data();
        localBase64Data.set(paramAnonymousDataHandler);
        return localBase64Data;
      }
    });
    localArrayList1.add(new PcdataImpl(Source.class, new QName[] { createXS("base64Binary") })
    {
      public Source parse(CharSequence paramAnonymousCharSequence)
        throws SAXException
      {
        try
        {
          if ((paramAnonymousCharSequence instanceof Base64Data)) {
            return new DataSourceSource(((Base64Data)paramAnonymousCharSequence).getDataHandler());
          }
          return new DataSourceSource(new ByteArrayDataSource(RuntimeBuiltinLeafInfoImpl.decodeBase64(paramAnonymousCharSequence), UnmarshallingContext.getInstance().getXMIMEContentType()));
        }
        catch (MimeTypeParseException localMimeTypeParseException)
        {
          UnmarshallingContext.getInstance().handleError(localMimeTypeParseException);
        }
        return null;
      }
      
      public Base64Data print(Source paramAnonymousSource)
      {
        XMLSerializer localXMLSerializer = XMLSerializer.getInstance();
        Base64Data localBase64Data = new Base64Data();
        String str1 = localXMLSerializer.getXMIMEContentType();
        MimeType localMimeType = null;
        if (str1 != null) {
          try
          {
            localMimeType = new MimeType(str1);
          }
          catch (MimeTypeParseException localMimeTypeParseException)
          {
            localXMLSerializer.handleError(localMimeTypeParseException);
          }
        }
        Object localObject2;
        if ((paramAnonymousSource instanceof DataSourceSource))
        {
          localObject1 = ((DataSourceSource)paramAnonymousSource).getDataSource();
          localObject2 = ((DataSource)localObject1).getContentType();
          if ((localObject2 != null) && ((str1 == null) || (str1.equals(localObject2))))
          {
            localBase64Data.set(new DataHandler((DataSource)localObject1));
            return localBase64Data;
          }
        }
        Object localObject1 = null;
        if (localMimeType != null) {
          localObject1 = localMimeType.getParameter("charset");
        }
        if (localObject1 == null) {
          localObject1 = "UTF-8";
        }
        try
        {
          localObject2 = new ByteArrayOutputStreamEx();
          Transformer localTransformer = localXMLSerializer.getIdentityTransformer();
          String str2 = localTransformer.getOutputProperty("encoding");
          localTransformer.setOutputProperty("encoding", (String)localObject1);
          localTransformer.transform(paramAnonymousSource, new StreamResult(new OutputStreamWriter((OutputStream)localObject2, (String)localObject1)));
          localTransformer.setOutputProperty("encoding", str2);
          ((ByteArrayOutputStreamEx)localObject2).set(localBase64Data, "application/xml; charset=" + (String)localObject1);
          return localBase64Data;
        }
        catch (TransformerException localTransformerException)
        {
          localXMLSerializer.handleError(localTransformerException);
        }
        catch (UnsupportedEncodingException localUnsupportedEncodingException)
        {
          localXMLSerializer.handleError(localUnsupportedEncodingException);
        }
        localBase64Data.set(new byte[0], "application/xml");
        return localBase64Data;
      }
    });
    localArrayList1.add(new StringImpl(XMLGregorianCalendar.class, new QName[] { createXS("anySimpleType"), DatatypeConstants.DATE, DatatypeConstants.DATETIME, DatatypeConstants.TIME, DatatypeConstants.GMONTH, DatatypeConstants.GDAY, DatatypeConstants.GYEAR, DatatypeConstants.GYEARMONTH, DatatypeConstants.GMONTHDAY })
    {
      public String print(XMLGregorianCalendar paramAnonymousXMLGregorianCalendar)
      {
        XMLSerializer localXMLSerializer = XMLSerializer.getInstance();
        QName localQName = localXMLSerializer.getSchemaType();
        if (localQName != null) {
          try
          {
            RuntimeBuiltinLeafInfoImpl.checkXmlGregorianCalendarFieldRef(localQName, paramAnonymousXMLGregorianCalendar);
            String str = (String)RuntimeBuiltinLeafInfoImpl.xmlGregorianCalendarFormatString.get(localQName);
            if (str != null) {
              return format(str, paramAnonymousXMLGregorianCalendar);
            }
          }
          catch (MarshalException localMarshalException)
          {
            localXMLSerializer.handleEvent(new ValidationEventImpl(0, localMarshalException.getMessage(), localXMLSerializer.getCurrentLocation(null)));
            return "";
          }
        }
        return paramAnonymousXMLGregorianCalendar.toXMLFormat();
      }
      
      public XMLGregorianCalendar parse(CharSequence paramAnonymousCharSequence)
        throws SAXException
      {
        try
        {
          return DatatypeConverterImpl.getDatatypeFactory().newXMLGregorianCalendar(paramAnonymousCharSequence.toString().trim());
        }
        catch (Exception localException)
        {
          UnmarshallingContext.getInstance().handleError(localException);
        }
        return null;
      }
      
      private String format(String paramAnonymousString, XMLGregorianCalendar paramAnonymousXMLGregorianCalendar)
      {
        StringBuilder localStringBuilder = new StringBuilder();
        int i = 0;
        int j = paramAnonymousString.length();
        while (i < j)
        {
          char c = paramAnonymousString.charAt(i++);
          if (c != '%') {
            localStringBuilder.append(c);
          } else {
            switch (paramAnonymousString.charAt(i++))
            {
            case 'Y': 
              printNumber(localStringBuilder, paramAnonymousXMLGregorianCalendar.getEonAndYear(), 4);
              break;
            case 'M': 
              printNumber(localStringBuilder, paramAnonymousXMLGregorianCalendar.getMonth(), 2);
              break;
            case 'D': 
              printNumber(localStringBuilder, paramAnonymousXMLGregorianCalendar.getDay(), 2);
              break;
            case 'h': 
              printNumber(localStringBuilder, paramAnonymousXMLGregorianCalendar.getHour(), 2);
              break;
            case 'm': 
              printNumber(localStringBuilder, paramAnonymousXMLGregorianCalendar.getMinute(), 2);
              break;
            case 's': 
              printNumber(localStringBuilder, paramAnonymousXMLGregorianCalendar.getSecond(), 2);
              if (paramAnonymousXMLGregorianCalendar.getFractionalSecond() != null)
              {
                String str = paramAnonymousXMLGregorianCalendar.getFractionalSecond().toPlainString();
                localStringBuilder.append(str.substring(1, str.length()));
              }
              break;
            case 'z': 
              int k = paramAnonymousXMLGregorianCalendar.getTimezone();
              if (k == 0)
              {
                localStringBuilder.append('Z');
              }
              else if (k != Integer.MIN_VALUE)
              {
                if (k < 0)
                {
                  localStringBuilder.append('-');
                  k *= -1;
                }
                else
                {
                  localStringBuilder.append('+');
                }
                printNumber(localStringBuilder, k / 60, 2);
                localStringBuilder.append(':');
                printNumber(localStringBuilder, k % 60, 2);
              }
              break;
            default: 
              throw new InternalError();
            }
          }
        }
        return localStringBuilder.toString();
      }
      
      private void printNumber(StringBuilder paramAnonymousStringBuilder, BigInteger paramAnonymousBigInteger, int paramAnonymousInt)
      {
        String str = paramAnonymousBigInteger.toString();
        for (int i = str.length(); i < paramAnonymousInt; i++) {
          paramAnonymousStringBuilder.append('0');
        }
        paramAnonymousStringBuilder.append(str);
      }
      
      private void printNumber(StringBuilder paramAnonymousStringBuilder, int paramAnonymousInt1, int paramAnonymousInt2)
      {
        String str = String.valueOf(paramAnonymousInt1);
        for (int i = str.length(); i < paramAnonymousInt2; i++) {
          paramAnonymousStringBuilder.append('0');
        }
        paramAnonymousStringBuilder.append(str);
      }
      
      public QName getTypeName(XMLGregorianCalendar paramAnonymousXMLGregorianCalendar)
      {
        return paramAnonymousXMLGregorianCalendar.getXMLSchemaType();
      }
    });
    ArrayList localArrayList2 = new ArrayList();
    localArrayList2.add(STRING);
    localArrayList2.add(new StringImpl(Boolean.class, new QName[] { createXS("boolean") })
    {
      public Boolean parse(CharSequence paramAnonymousCharSequence)
      {
        return DatatypeConverterImpl._parseBoolean(paramAnonymousCharSequence);
      }
      
      public String print(Boolean paramAnonymousBoolean)
      {
        return paramAnonymousBoolean.toString();
      }
    });
    localArrayList2.add(new PcdataImpl(byte[].class, new QName[] { createXS("base64Binary"), createXS("hexBinary") })
    {
      public byte[] parse(CharSequence paramAnonymousCharSequence)
      {
        return RuntimeBuiltinLeafInfoImpl.decodeBase64(paramAnonymousCharSequence);
      }
      
      public Base64Data print(byte[] paramAnonymousArrayOfByte)
      {
        XMLSerializer localXMLSerializer = XMLSerializer.getInstance();
        Base64Data localBase64Data = new Base64Data();
        String str = localXMLSerializer.getXMIMEContentType();
        localBase64Data.set(paramAnonymousArrayOfByte, str);
        return localBase64Data;
      }
    });
    localArrayList2.add(new StringImpl(Byte.class, new QName[] { createXS("byte") })
    {
      public Byte parse(CharSequence paramAnonymousCharSequence)
      {
        return Byte.valueOf(DatatypeConverterImpl._parseByte(paramAnonymousCharSequence));
      }
      
      public String print(Byte paramAnonymousByte)
      {
        return DatatypeConverterImpl._printByte(paramAnonymousByte.byteValue());
      }
    });
    localArrayList2.add(new StringImpl(Short.class, new QName[] { createXS("short"), createXS("unsignedByte") })
    {
      public Short parse(CharSequence paramAnonymousCharSequence)
      {
        return Short.valueOf(DatatypeConverterImpl._parseShort(paramAnonymousCharSequence));
      }
      
      public String print(Short paramAnonymousShort)
      {
        return DatatypeConverterImpl._printShort(paramAnonymousShort.shortValue());
      }
    });
    localArrayList2.add(new StringImpl(Integer.class, new QName[] { createXS("int"), createXS("unsignedShort") })
    {
      public Integer parse(CharSequence paramAnonymousCharSequence)
      {
        return Integer.valueOf(DatatypeConverterImpl._parseInt(paramAnonymousCharSequence));
      }
      
      public String print(Integer paramAnonymousInteger)
      {
        return DatatypeConverterImpl._printInt(paramAnonymousInteger.intValue());
      }
    });
    localArrayList2.add(new StringImpl(Long.class, new QName[] { createXS("long"), createXS("unsignedInt") })
    {
      public Long parse(CharSequence paramAnonymousCharSequence)
      {
        return Long.valueOf(DatatypeConverterImpl._parseLong(paramAnonymousCharSequence));
      }
      
      public String print(Long paramAnonymousLong)
      {
        return DatatypeConverterImpl._printLong(paramAnonymousLong.longValue());
      }
    });
    localArrayList2.add(new StringImpl(Float.class, new QName[] { createXS("float") })
    {
      public Float parse(CharSequence paramAnonymousCharSequence)
      {
        return Float.valueOf(DatatypeConverterImpl._parseFloat(paramAnonymousCharSequence.toString()));
      }
      
      public String print(Float paramAnonymousFloat)
      {
        return DatatypeConverterImpl._printFloat(paramAnonymousFloat.floatValue());
      }
    });
    localArrayList2.add(new StringImpl(Double.class, new QName[] { createXS("double") })
    {
      public Double parse(CharSequence paramAnonymousCharSequence)
      {
        return Double.valueOf(DatatypeConverterImpl._parseDouble(paramAnonymousCharSequence));
      }
      
      public String print(Double paramAnonymousDouble)
      {
        return DatatypeConverterImpl._printDouble(paramAnonymousDouble.doubleValue());
      }
    });
    localArrayList2.add(new StringImpl(BigInteger.class, new QName[] { createXS("integer"), createXS("positiveInteger"), createXS("negativeInteger"), createXS("nonPositiveInteger"), createXS("nonNegativeInteger"), createXS("unsignedLong") })
    {
      public BigInteger parse(CharSequence paramAnonymousCharSequence)
      {
        return DatatypeConverterImpl._parseInteger(paramAnonymousCharSequence);
      }
      
      public String print(BigInteger paramAnonymousBigInteger)
      {
        return DatatypeConverterImpl._printInteger(paramAnonymousBigInteger);
      }
    });
    localArrayList2.add(new StringImpl(BigDecimal.class, new QName[] { createXS("decimal") })
    {
      public BigDecimal parse(CharSequence paramAnonymousCharSequence)
      {
        return DatatypeConverterImpl._parseDecimal(paramAnonymousCharSequence.toString());
      }
      
      public String print(BigDecimal paramAnonymousBigDecimal)
      {
        return DatatypeConverterImpl._printDecimal(paramAnonymousBigDecimal);
      }
    });
    localArrayList2.add(new StringImpl(QName.class, new QName[] { createXS("QName") })
    {
      public QName parse(CharSequence paramAnonymousCharSequence)
        throws SAXException
      {
        try
        {
          return DatatypeConverterImpl._parseQName(paramAnonymousCharSequence.toString(), UnmarshallingContext.getInstance());
        }
        catch (IllegalArgumentException localIllegalArgumentException)
        {
          UnmarshallingContext.getInstance().handleError(localIllegalArgumentException);
        }
        return null;
      }
      
      public String print(QName paramAnonymousQName)
      {
        return DatatypeConverterImpl._printQName(paramAnonymousQName, XMLSerializer.getInstance().getNamespaceContext());
      }
      
      public boolean useNamespace()
      {
        return true;
      }
      
      public void declareNamespace(QName paramAnonymousQName, XMLSerializer paramAnonymousXMLSerializer)
      {
        paramAnonymousXMLSerializer.getNamespaceContext().declareNamespace(paramAnonymousQName.getNamespaceURI(), paramAnonymousQName.getPrefix(), false);
      }
    });
    if (localObject != null) {
      localArrayList2.add(new StringImpl(URI.class, new QName[] { createXS("anyURI") })
      {
        public URI parse(CharSequence paramAnonymousCharSequence)
          throws SAXException
        {
          try
          {
            return new URI(paramAnonymousCharSequence.toString());
          }
          catch (URISyntaxException localURISyntaxException)
          {
            UnmarshallingContext.getInstance().handleError(localURISyntaxException);
          }
          return null;
        }
        
        public String print(URI paramAnonymousURI)
        {
          return paramAnonymousURI.toString();
        }
      });
    }
    localArrayList2.add(new StringImpl(Duration.class, new QName[] { createXS("duration") })
    {
      public String print(Duration paramAnonymousDuration)
      {
        return paramAnonymousDuration.toString();
      }
      
      public Duration parse(CharSequence paramAnonymousCharSequence)
      {
        TODO.checkSpec("JSR222 Issue #42");
        return DatatypeConverterImpl.getDatatypeFactory().newDuration(paramAnonymousCharSequence.toString());
      }
    });
    localArrayList2.add(new StringImpl(Void.class, new QName[0])
    {
      public String print(Void paramAnonymousVoid)
      {
        return "";
      }
      
      public Void parse(CharSequence paramAnonymousCharSequence)
      {
        return null;
      }
    });
    ArrayList localArrayList3 = new ArrayList(localArrayList1.size() + localArrayList2.size() + 1);
    localArrayList3.addAll(localArrayList1);
    try
    {
      localArrayList3.add(new UUIDImpl());
    }
    catch (LinkageError localLinkageError) {}
    localArrayList3.addAll(localArrayList2);
    builtinBeanInfos = Collections.unmodifiableList(localArrayList3);
    xmlGregorianCalendarFormatString = new HashMap();
    localObject = xmlGregorianCalendarFormatString;
    ((Map)localObject).put(DatatypeConstants.DATETIME, "%Y-%M-%DT%h:%m:%s%z");
    ((Map)localObject).put(DatatypeConstants.DATE, "%Y-%M-%D%z");
    ((Map)localObject).put(DatatypeConstants.TIME, "%h:%m:%s%z");
    ((Map)localObject).put(DatatypeConstants.GMONTH, "--%M--%z");
    ((Map)localObject).put(DatatypeConstants.GDAY, "---%D%z");
    ((Map)localObject).put(DatatypeConstants.GYEAR, "%Y%z");
    ((Map)localObject).put(DatatypeConstants.GYEARMONTH, "%Y-%M%z");
    ((Map)localObject).put(DatatypeConstants.GMONTHDAY, "--%M-%D%z");
    xmlGregorianCalendarFieldRef = new HashMap();
    localObject = xmlGregorianCalendarFieldRef;
    ((Map)localObject).put(DatatypeConstants.DATETIME, Integer.valueOf(17895697));
    ((Map)localObject).put(DatatypeConstants.DATE, Integer.valueOf(17895424));
    ((Map)localObject).put(DatatypeConstants.TIME, Integer.valueOf(16777489));
    ((Map)localObject).put(DatatypeConstants.GDAY, Integer.valueOf(16781312));
    ((Map)localObject).put(DatatypeConstants.GMONTH, Integer.valueOf(16842752));
    ((Map)localObject).put(DatatypeConstants.GYEAR, Integer.valueOf(17825792));
    ((Map)localObject).put(DatatypeConstants.GYEARMONTH, Integer.valueOf(17891328));
    ((Map)localObject).put(DatatypeConstants.GMONTHDAY, Integer.valueOf(16846848));
  }
  
  private static abstract class PcdataImpl<T>
    extends RuntimeBuiltinLeafInfoImpl<T>
  {
    protected PcdataImpl(Class paramClass, QName... paramVarArgs)
    {
      super(paramVarArgs, null);
    }
    
    public abstract Pcdata print(T paramT)
      throws AccessorException;
    
    public final void writeText(XMLSerializer paramXMLSerializer, T paramT, String paramString)
      throws IOException, SAXException, XMLStreamException, AccessorException
    {
      paramXMLSerializer.text(print(paramT), paramString);
    }
    
    public final void writeLeafElement(XMLSerializer paramXMLSerializer, Name paramName, T paramT, String paramString)
      throws IOException, SAXException, XMLStreamException, AccessorException
    {
      paramXMLSerializer.leafElement(paramName, print(paramT), paramString);
    }
  }
  
  private static abstract class StringImpl<T>
    extends RuntimeBuiltinLeafInfoImpl<T>
  {
    protected StringImpl(Class paramClass, QName... paramVarArgs)
    {
      super(paramVarArgs, null);
    }
    
    public abstract String print(T paramT)
      throws AccessorException;
    
    public void writeText(XMLSerializer paramXMLSerializer, T paramT, String paramString)
      throws IOException, SAXException, XMLStreamException, AccessorException
    {
      paramXMLSerializer.text(print(paramT), paramString);
    }
    
    public void writeLeafElement(XMLSerializer paramXMLSerializer, Name paramName, T paramT, String paramString)
      throws IOException, SAXException, XMLStreamException, AccessorException
    {
      paramXMLSerializer.leafElement(paramName, print(paramT), paramString);
    }
  }
  
  private static class StringImplImpl
    extends RuntimeBuiltinLeafInfoImpl.StringImpl<String>
  {
    public StringImplImpl(Class paramClass, QName[] paramArrayOfQName)
    {
      super(paramArrayOfQName);
    }
    
    public String parse(CharSequence paramCharSequence)
    {
      return paramCharSequence.toString();
    }
    
    public String print(String paramString)
    {
      return paramString;
    }
    
    public final void writeText(XMLSerializer paramXMLSerializer, String paramString1, String paramString2)
      throws IOException, SAXException, XMLStreamException
    {
      paramXMLSerializer.text(paramString1, paramString2);
    }
    
    public final void writeLeafElement(XMLSerializer paramXMLSerializer, Name paramName, String paramString1, String paramString2)
      throws IOException, SAXException, XMLStreamException
    {
      paramXMLSerializer.leafElement(paramName, paramString1, paramString2);
    }
  }
  
  private static class UUIDImpl
    extends RuntimeBuiltinLeafInfoImpl.StringImpl<UUID>
  {
    public UUIDImpl()
    {
      super(new QName[] { RuntimeBuiltinLeafInfoImpl.createXS("string") });
    }
    
    public UUID parse(CharSequence paramCharSequence)
      throws SAXException
    {
      TODO.checkSpec("JSR222 Issue #42");
      try
      {
        return UUID.fromString(WhiteSpaceProcessor.trim(paramCharSequence).toString());
      }
      catch (IllegalArgumentException localIllegalArgumentException)
      {
        UnmarshallingContext.getInstance().handleError(localIllegalArgumentException);
      }
      return null;
    }
    
    public String print(UUID paramUUID)
    {
      return paramUUID.toString();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\model\impl\RuntimeBuiltinLeafInfoImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */