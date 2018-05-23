package com.sun.org.apache.xml.internal.security.transforms.implementations;

import com.sun.org.apache.xml.internal.security.c14n.CanonicalizationException;
import com.sun.org.apache.xml.internal.security.signature.XMLSignatureInput;
import com.sun.org.apache.xml.internal.security.transforms.Transform;
import com.sun.org.apache.xml.internal.security.transforms.TransformSpi;
import com.sun.org.apache.xml.internal.security.transforms.TransformationException;
import java.io.IOException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

public class TransformBase64Decode
  extends TransformSpi
{
  public static final String implementedTransformURI = "http://www.w3.org/2000/09/xmldsig#base64";
  
  public TransformBase64Decode() {}
  
  protected String engineGetURI()
  {
    return "http://www.w3.org/2000/09/xmldsig#base64";
  }
  
  protected XMLSignatureInput enginePerformTransform(XMLSignatureInput paramXMLSignatureInput, Transform paramTransform)
    throws IOException, CanonicalizationException, TransformationException
  {
    return enginePerformTransform(paramXMLSignatureInput, null, paramTransform);
  }
  
  /* Error */
  protected XMLSignatureInput enginePerformTransform(XMLSignatureInput paramXMLSignatureInput, java.io.OutputStream paramOutputStream, Transform paramTransform)
    throws IOException, CanonicalizationException, TransformationException
  {
    // Byte code:
    //   0: aload_1
    //   1: invokevirtual 147	com/sun/org/apache/xml/internal/security/signature/XMLSignatureInput:isElement	()Z
    //   4: ifeq +106 -> 110
    //   7: aload_1
    //   8: invokevirtual 155	com/sun/org/apache/xml/internal/security/signature/XMLSignatureInput:getSubNode	()Lorg/w3c/dom/Node;
    //   11: astore 4
    //   13: aload_1
    //   14: invokevirtual 155	com/sun/org/apache/xml/internal/security/signature/XMLSignatureInput:getSubNode	()Lorg/w3c/dom/Node;
    //   17: invokeinterface 176 1 0
    //   22: iconst_3
    //   23: if_icmpne +12 -> 35
    //   26: aload 4
    //   28: invokeinterface 178 1 0
    //   33: astore 4
    //   35: new 84	java/lang/StringBuilder
    //   38: dup
    //   39: invokespecial 167	java/lang/StringBuilder:<init>	()V
    //   42: astore 5
    //   44: aload_0
    //   45: aload 4
    //   47: checkcast 89	org/w3c/dom/Element
    //   50: aload 5
    //   52: invokevirtual 158	com/sun/org/apache/xml/internal/security/transforms/implementations/TransformBase64Decode:traverseElement	(Lorg/w3c/dom/Element;Ljava/lang/StringBuilder;)V
    //   55: aload_2
    //   56: ifnonnull +23 -> 79
    //   59: aload 5
    //   61: invokevirtual 168	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   64: invokestatic 162	com/sun/org/apache/xml/internal/security/utils/Base64:decode	(Ljava/lang/String;)[B
    //   67: astore 6
    //   69: new 76	com/sun/org/apache/xml/internal/security/signature/XMLSignatureInput
    //   72: dup
    //   73: aload 6
    //   75: invokespecial 151	com/sun/org/apache/xml/internal/security/signature/XMLSignatureInput:<init>	([B)V
    //   78: areturn
    //   79: aload 5
    //   81: invokevirtual 168	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   84: aload_2
    //   85: invokestatic 164	com/sun/org/apache/xml/internal/security/utils/Base64:decode	(Ljava/lang/String;Ljava/io/OutputStream;)V
    //   88: new 76	com/sun/org/apache/xml/internal/security/signature/XMLSignatureInput
    //   91: dup
    //   92: aconst_null
    //   93: checkcast 73	[B
    //   96: invokespecial 151	com/sun/org/apache/xml/internal/security/signature/XMLSignatureInput:<init>	([B)V
    //   99: astore 6
    //   101: aload 6
    //   103: aload_2
    //   104: invokevirtual 154	com/sun/org/apache/xml/internal/security/signature/XMLSignatureInput:setOutputStream	(Ljava/io/OutputStream;)V
    //   107: aload 6
    //   109: areturn
    //   110: aload_1
    //   111: invokevirtual 149	com/sun/org/apache/xml/internal/security/signature/XMLSignatureInput:isOctetStream	()Z
    //   114: ifne +10 -> 124
    //   117: aload_1
    //   118: invokevirtual 148	com/sun/org/apache/xml/internal/security/signature/XMLSignatureInput:isNodeSet	()Z
    //   121: ifeq +92 -> 213
    //   124: aload_2
    //   125: ifnonnull +26 -> 151
    //   128: aload_1
    //   129: invokevirtual 150	com/sun/org/apache/xml/internal/security/signature/XMLSignatureInput:getBytes	()[B
    //   132: astore 4
    //   134: aload 4
    //   136: invokestatic 160	com/sun/org/apache/xml/internal/security/utils/Base64:decode	([B)[B
    //   139: astore 5
    //   141: new 76	com/sun/org/apache/xml/internal/security/signature/XMLSignatureInput
    //   144: dup
    //   145: aload 5
    //   147: invokespecial 151	com/sun/org/apache/xml/internal/security/signature/XMLSignatureInput:<init>	([B)V
    //   150: areturn
    //   151: aload_1
    //   152: invokevirtual 146	com/sun/org/apache/xml/internal/security/signature/XMLSignatureInput:isByteArray	()Z
    //   155: ifne +10 -> 165
    //   158: aload_1
    //   159: invokevirtual 148	com/sun/org/apache/xml/internal/security/signature/XMLSignatureInput:isNodeSet	()Z
    //   162: ifeq +14 -> 176
    //   165: aload_1
    //   166: invokevirtual 150	com/sun/org/apache/xml/internal/security/signature/XMLSignatureInput:getBytes	()[B
    //   169: aload_2
    //   170: invokestatic 161	com/sun/org/apache/xml/internal/security/utils/Base64:decode	([BLjava/io/OutputStream;)V
    //   173: goto +18 -> 191
    //   176: new 81	java/io/BufferedInputStream
    //   179: dup
    //   180: aload_1
    //   181: invokevirtual 153	com/sun/org/apache/xml/internal/security/signature/XMLSignatureInput:getOctetStreamReal	()Ljava/io/InputStream;
    //   184: invokespecial 165	java/io/BufferedInputStream:<init>	(Ljava/io/InputStream;)V
    //   187: aload_2
    //   188: invokestatic 163	com/sun/org/apache/xml/internal/security/utils/Base64:decode	(Ljava/io/InputStream;Ljava/io/OutputStream;)V
    //   191: new 76	com/sun/org/apache/xml/internal/security/signature/XMLSignatureInput
    //   194: dup
    //   195: aconst_null
    //   196: checkcast 73	[B
    //   199: invokespecial 151	com/sun/org/apache/xml/internal/security/signature/XMLSignatureInput:<init>	([B)V
    //   202: astore 4
    //   204: aload 4
    //   206: aload_2
    //   207: invokevirtual 154	com/sun/org/apache/xml/internal/security/signature/XMLSignatureInput:setOutputStream	(Ljava/io/OutputStream;)V
    //   210: aload 4
    //   212: areturn
    //   213: invokestatic 173	javax/xml/parsers/DocumentBuilderFactory:newInstance	()Ljavax/xml/parsers/DocumentBuilderFactory;
    //   216: astore 4
    //   218: aload 4
    //   220: ldc 4
    //   222: getstatic 145	java/lang/Boolean:TRUE	Ljava/lang/Boolean;
    //   225: invokevirtual 166	java/lang/Boolean:booleanValue	()Z
    //   228: invokevirtual 171	javax/xml/parsers/DocumentBuilderFactory:setFeature	(Ljava/lang/String;Z)V
    //   231: aload 4
    //   233: invokevirtual 172	javax/xml/parsers/DocumentBuilderFactory:newDocumentBuilder	()Ljavax/xml/parsers/DocumentBuilder;
    //   236: aload_1
    //   237: invokevirtual 152	com/sun/org/apache/xml/internal/security/signature/XMLSignatureInput:getOctetStream	()Ljava/io/InputStream;
    //   240: invokevirtual 170	javax/xml/parsers/DocumentBuilder:parse	(Ljava/io/InputStream;)Lorg/w3c/dom/Document;
    //   243: astore 5
    //   245: aload 5
    //   247: invokeinterface 174 1 0
    //   252: astore 6
    //   254: new 84	java/lang/StringBuilder
    //   257: dup
    //   258: invokespecial 167	java/lang/StringBuilder:<init>	()V
    //   261: astore 7
    //   263: aload_0
    //   264: aload 6
    //   266: aload 7
    //   268: invokevirtual 158	com/sun/org/apache/xml/internal/security/transforms/implementations/TransformBase64Decode:traverseElement	(Lorg/w3c/dom/Element;Ljava/lang/StringBuilder;)V
    //   271: aload 7
    //   273: invokevirtual 168	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   276: invokestatic 162	com/sun/org/apache/xml/internal/security/utils/Base64:decode	(Ljava/lang/String;)[B
    //   279: astore 8
    //   281: new 76	com/sun/org/apache/xml/internal/security/signature/XMLSignatureInput
    //   284: dup
    //   285: aload 8
    //   287: invokespecial 151	com/sun/org/apache/xml/internal/security/signature/XMLSignatureInput:<init>	([B)V
    //   290: areturn
    //   291: astore 4
    //   293: new 78	com/sun/org/apache/xml/internal/security/transforms/TransformationException
    //   296: dup
    //   297: ldc 3
    //   299: aload 4
    //   301: invokespecial 157	com/sun/org/apache/xml/internal/security/transforms/TransformationException:<init>	(Ljava/lang/String;Ljava/lang/Exception;)V
    //   304: athrow
    //   305: astore 4
    //   307: new 78	com/sun/org/apache/xml/internal/security/transforms/TransformationException
    //   310: dup
    //   311: ldc 2
    //   313: aload 4
    //   315: invokespecial 157	com/sun/org/apache/xml/internal/security/transforms/TransformationException:<init>	(Ljava/lang/String;Ljava/lang/Exception;)V
    //   318: athrow
    //   319: astore 4
    //   321: new 78	com/sun/org/apache/xml/internal/security/transforms/TransformationException
    //   324: dup
    //   325: ldc 1
    //   327: aload 4
    //   329: invokespecial 157	com/sun/org/apache/xml/internal/security/transforms/TransformationException:<init>	(Ljava/lang/String;Ljava/lang/Exception;)V
    //   332: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	333	0	this	TransformBase64Decode
    //   0	333	1	paramXMLSignatureInput	XMLSignatureInput
    //   0	333	2	paramOutputStream	java.io.OutputStream
    //   0	333	3	paramTransform	Transform
    //   11	221	4	localObject1	Object
    //   291	9	4	localParserConfigurationException	javax.xml.parsers.ParserConfigurationException
    //   305	9	4	localSAXException	org.xml.sax.SAXException
    //   319	9	4	localBase64DecodingException	com.sun.org.apache.xml.internal.security.exceptions.Base64DecodingException
    //   42	204	5	localObject2	Object
    //   67	198	6	localObject3	Object
    //   261	11	7	localStringBuilder	StringBuilder
    //   279	7	8	arrayOfByte	byte[]
    // Exception table:
    //   from	to	target	type
    //   213	290	291	javax/xml/parsers/ParserConfigurationException
    //   213	290	305	org/xml/sax/SAXException
    //   0	78	319	com/sun/org/apache/xml/internal/security/exceptions/Base64DecodingException
    //   79	109	319	com/sun/org/apache/xml/internal/security/exceptions/Base64DecodingException
    //   110	150	319	com/sun/org/apache/xml/internal/security/exceptions/Base64DecodingException
    //   151	212	319	com/sun/org/apache/xml/internal/security/exceptions/Base64DecodingException
    //   213	290	319	com/sun/org/apache/xml/internal/security/exceptions/Base64DecodingException
    //   291	319	319	com/sun/org/apache/xml/internal/security/exceptions/Base64DecodingException
  }
  
  void traverseElement(Element paramElement, StringBuilder paramStringBuilder)
  {
    for (Node localNode = paramElement.getFirstChild(); localNode != null; localNode = localNode.getNextSibling()) {
      switch (localNode.getNodeType())
      {
      case 1: 
        traverseElement((Element)localNode, paramStringBuilder);
        break;
      case 3: 
        paramStringBuilder.append(((Text)localNode).getData());
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\security\transforms\implementations\TransformBase64Decode.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */