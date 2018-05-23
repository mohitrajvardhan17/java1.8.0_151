package com.sun.security.sasl.digest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.security.auth.callback.CallbackHandler;
import javax.security.sasl.SaslException;
import javax.security.sasl.SaslServer;

final class DigestMD5Server
  extends DigestMD5Base
  implements SaslServer
{
  private static final String MY_CLASS_NAME = DigestMD5Server.class.getName();
  private static final String UTF8_DIRECTIVE = "charset=utf-8,";
  private static final String ALGORITHM_DIRECTIVE = "algorithm=md5-sess";
  private static final int NONCE_COUNT_VALUE = 1;
  private static final String UTF8_PROPERTY = "com.sun.security.sasl.digest.utf8";
  private static final String REALM_PROPERTY = "com.sun.security.sasl.digest.realm";
  private static final String[] DIRECTIVE_KEY = { "username", "realm", "nonce", "cnonce", "nonce-count", "qop", "digest-uri", "response", "maxbuf", "charset", "cipher", "authzid", "auth-param" };
  private static final int USERNAME = 0;
  private static final int REALM = 1;
  private static final int NONCE = 2;
  private static final int CNONCE = 3;
  private static final int NONCE_COUNT = 4;
  private static final int QOP = 5;
  private static final int DIGEST_URI = 6;
  private static final int RESPONSE = 7;
  private static final int MAXBUF = 8;
  private static final int CHARSET = 9;
  private static final int CIPHER = 10;
  private static final int AUTHZID = 11;
  private static final int AUTH_PARAM = 12;
  private String specifiedQops;
  private byte[] myCiphers;
  private List<String> serverRealms = new ArrayList();
  
  DigestMD5Server(String paramString1, String paramString2, Map<String, ?> paramMap, CallbackHandler paramCallbackHandler)
    throws SaslException
  {
    super(paramMap, MY_CLASS_NAME, 1, paramString1 + "/" + (paramString2 == null ? "*" : paramString2), paramCallbackHandler);
    useUTF8 = true;
    if (paramMap != null)
    {
      specifiedQops = ((String)paramMap.get("javax.security.sasl.qop"));
      if ("false".equals((String)paramMap.get("com.sun.security.sasl.digest.utf8")))
      {
        useUTF8 = false;
        logger.log(Level.FINE, "DIGEST80:Server supports ISO-Latin-1");
      }
      String str1 = (String)paramMap.get("com.sun.security.sasl.digest.realm");
      if (str1 != null)
      {
        StringTokenizer localStringTokenizer = new StringTokenizer(str1, ", \t\n");
        int i = localStringTokenizer.countTokens();
        String str2 = null;
        for (int j = 0; j < i; j++)
        {
          str2 = localStringTokenizer.nextToken();
          logger.log(Level.FINE, "DIGEST81:Server supports realm {0}", str2);
          serverRealms.add(str2);
        }
      }
    }
    encoding = (useUTF8 ? "UTF8" : "8859_1");
    if (serverRealms.isEmpty())
    {
      if (paramString2 == null) {
        throw new SaslException("A realm must be provided in props or serverName");
      }
      serverRealms.add(paramString2);
    }
  }
  
  public byte[] evaluateResponse(byte[] paramArrayOfByte)
    throws SaslException
  {
    if (paramArrayOfByte.length > 4096) {
      throw new SaslException("DIGEST-MD5: Invalid digest response length. Got:  " + paramArrayOfByte.length + " Expected < " + 4096);
    }
    byte[] arrayOfByte;
    switch (step)
    {
    case 1: 
      if (paramArrayOfByte.length != 0) {
        throw new SaslException("DIGEST-MD5 must not have an initial response");
      }
      String str = null;
      if ((allQop & 0x4) != 0)
      {
        myCiphers = getPlatformCiphers();
        StringBuffer localStringBuffer = new StringBuffer();
        for (int i = 0; i < CIPHER_TOKENS.length; i++) {
          if (myCiphers[i] != 0)
          {
            if (localStringBuffer.length() > 0) {
              localStringBuffer.append(',');
            }
            localStringBuffer.append(CIPHER_TOKENS[i]);
          }
        }
        str = localStringBuffer.toString();
      }
      try
      {
        arrayOfByte = generateChallenge(serverRealms, specifiedQops, str);
        step = 3;
        return arrayOfByte;
      }
      catch (UnsupportedEncodingException localUnsupportedEncodingException1)
      {
        throw new SaslException("DIGEST-MD5: Error encoding challenge", localUnsupportedEncodingException1);
      }
      catch (IOException localIOException)
      {
        throw new SaslException("DIGEST-MD5: Error generating challenge", localIOException);
      }
    case 3: 
      try
      {
        byte[][] arrayOfByte1 = parseDirectives(paramArrayOfByte, DIRECTIVE_KEY, null, 1);
        arrayOfByte = validateClientResponse(arrayOfByte1);
      }
      catch (SaslException localSaslException)
      {
        throw localSaslException;
      }
      catch (UnsupportedEncodingException localUnsupportedEncodingException2)
      {
        throw new SaslException("DIGEST-MD5: Error validating client response", localUnsupportedEncodingException2);
      }
      finally
      {
        step = 0;
      }
      completed = true;
      if ((integrity) && (privacy)) {
        secCtx = new DigestMD5Base.DigestPrivacy(this, false);
      } else if (integrity) {
        secCtx = new DigestMD5Base.DigestIntegrity(this, false);
      }
      return arrayOfByte;
    }
    throw new SaslException("DIGEST-MD5: Server at illegal state");
  }
  
  private byte[] generateChallenge(List<String> paramList, String paramString1, String paramString2)
    throws UnsupportedEncodingException, IOException
  {
    ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
    for (int i = 0; (paramList != null) && (i < paramList.size()); i++)
    {
      localByteArrayOutputStream.write("realm=\"".getBytes(encoding));
      writeQuotedStringValue(localByteArrayOutputStream, ((String)paramList.get(i)).getBytes(encoding));
      localByteArrayOutputStream.write(34);
      localByteArrayOutputStream.write(44);
    }
    localByteArrayOutputStream.write("nonce=\"".getBytes(encoding));
    nonce = generateNonce();
    writeQuotedStringValue(localByteArrayOutputStream, nonce);
    localByteArrayOutputStream.write(34);
    localByteArrayOutputStream.write(44);
    if (paramString1 != null)
    {
      localByteArrayOutputStream.write("qop=\"".getBytes(encoding));
      writeQuotedStringValue(localByteArrayOutputStream, paramString1.getBytes(encoding));
      localByteArrayOutputStream.write(34);
      localByteArrayOutputStream.write(44);
    }
    if (recvMaxBufSize != 65536) {
      localByteArrayOutputStream.write(("maxbuf=\"" + recvMaxBufSize + "\",").getBytes(encoding));
    }
    if (useUTF8) {
      localByteArrayOutputStream.write("charset=utf-8,".getBytes(encoding));
    }
    if (paramString2 != null)
    {
      localByteArrayOutputStream.write("cipher=\"".getBytes(encoding));
      writeQuotedStringValue(localByteArrayOutputStream, paramString2.getBytes(encoding));
      localByteArrayOutputStream.write(34);
      localByteArrayOutputStream.write(44);
    }
    localByteArrayOutputStream.write("algorithm=md5-sess".getBytes(encoding));
    return localByteArrayOutputStream.toByteArray();
  }
  
  /* Error */
  private byte[] validateClientResponse(byte[][] paramArrayOfByte)
    throws SaslException, UnsupportedEncodingException
  {
    // Byte code:
    //   0: aload_1
    //   1: bipush 9
    //   3: aaload
    //   4: ifnull +70 -> 74
    //   7: aload_0
    //   8: getfield 497	com/sun/security/sasl/digest/DigestMD5Server:useUTF8	Z
    //   11: ifeq +26 -> 37
    //   14: ldc 88
    //   16: new 356	java/lang/String
    //   19: dup
    //   20: aload_1
    //   21: bipush 9
    //   23: aaload
    //   24: aload_0
    //   25: getfield 505	com/sun/security/sasl/digest/DigestMD5Server:encoding	Ljava/lang/String;
    //   28: invokespecial 546	java/lang/String:<init>	([BLjava/lang/String;)V
    //   31: invokevirtual 541	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   34: ifne +40 -> 74
    //   37: new 376	javax/security/sasl/SaslException
    //   40: dup
    //   41: new 358	java/lang/StringBuilder
    //   44: dup
    //   45: invokespecial 552	java/lang/StringBuilder:<init>	()V
    //   48: ldc 32
    //   50: invokevirtual 555	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   53: new 356	java/lang/String
    //   56: dup
    //   57: aload_1
    //   58: bipush 9
    //   60: aaload
    //   61: invokespecial 540	java/lang/String:<init>	([B)V
    //   64: invokevirtual 555	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   67: invokevirtual 553	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   70: invokespecial 572	javax/security/sasl/SaslException:<init>	(Ljava/lang/String;)V
    //   73: athrow
    //   74: aload_1
    //   75: bipush 8
    //   77: aaload
    //   78: ifnonnull +8 -> 86
    //   81: ldc 1
    //   83: goto +21 -> 104
    //   86: new 356	java/lang/String
    //   89: dup
    //   90: aload_1
    //   91: bipush 8
    //   93: aaload
    //   94: aload_0
    //   95: getfield 505	com/sun/security/sasl/digest/DigestMD5Server:encoding	Ljava/lang/String;
    //   98: invokespecial 546	java/lang/String:<init>	([BLjava/lang/String;)V
    //   101: invokestatic 535	java/lang/Integer:parseInt	(Ljava/lang/String;)I
    //   104: istore_2
    //   105: aload_0
    //   106: aload_0
    //   107: getfield 492	com/sun/security/sasl/digest/DigestMD5Server:sendMaxBufSize	I
    //   110: ifne +7 -> 117
    //   113: iload_2
    //   114: goto +11 -> 125
    //   117: aload_0
    //   118: getfield 492	com/sun/security/sasl/digest/DigestMD5Server:sendMaxBufSize	I
    //   121: iload_2
    //   122: invokestatic 537	java/lang/Math:min	(II)I
    //   125: putfield 492	com/sun/security/sasl/digest/DigestMD5Server:sendMaxBufSize	I
    //   128: aload_1
    //   129: iconst_0
    //   130: aaload
    //   131: ifnull +33 -> 164
    //   134: new 356	java/lang/String
    //   137: dup
    //   138: aload_1
    //   139: iconst_0
    //   140: aaload
    //   141: aload_0
    //   142: getfield 505	com/sun/security/sasl/digest/DigestMD5Server:encoding	Ljava/lang/String;
    //   145: invokespecial 546	java/lang/String:<init>	([BLjava/lang/String;)V
    //   148: astore_3
    //   149: getstatic 514	com/sun/security/sasl/digest/DigestMD5Server:logger	Ljava/util/logging/Logger;
    //   152: getstatic 516	java/util/logging/Level:FINE	Ljava/util/logging/Level;
    //   155: ldc 49
    //   157: aload_3
    //   158: invokevirtual 563	java/util/logging/Logger:log	(Ljava/util/logging/Level;Ljava/lang/String;Ljava/lang/Object;)V
    //   161: goto +13 -> 174
    //   164: new 376	javax/security/sasl/SaslException
    //   167: dup
    //   168: ldc 39
    //   170: invokespecial 572	javax/security/sasl/SaslException:<init>	(Ljava/lang/String;)V
    //   173: athrow
    //   174: aload_0
    //   175: aload_1
    //   176: iconst_1
    //   177: aaload
    //   178: ifnull +20 -> 198
    //   181: new 356	java/lang/String
    //   184: dup
    //   185: aload_1
    //   186: iconst_1
    //   187: aaload
    //   188: aload_0
    //   189: getfield 505	com/sun/security/sasl/digest/DigestMD5Server:encoding	Ljava/lang/String;
    //   192: invokespecial 546	java/lang/String:<init>	([BLjava/lang/String;)V
    //   195: goto +5 -> 200
    //   198: ldc 2
    //   200: putfield 508	com/sun/security/sasl/digest/DigestMD5Server:negotiatedRealm	Ljava/lang/String;
    //   203: getstatic 514	com/sun/security/sasl/digest/DigestMD5Server:logger	Ljava/util/logging/Logger;
    //   206: getstatic 516	java/util/logging/Level:FINE	Ljava/util/logging/Level;
    //   209: ldc 50
    //   211: aload_0
    //   212: getfield 508	com/sun/security/sasl/digest/DigestMD5Server:negotiatedRealm	Ljava/lang/String;
    //   215: invokevirtual 563	java/util/logging/Logger:log	(Ljava/util/logging/Level;Ljava/lang/String;Ljava/lang/Object;)V
    //   218: aload_0
    //   219: getfield 513	com/sun/security/sasl/digest/DigestMD5Server:serverRealms	Ljava/util/List;
    //   222: aload_0
    //   223: getfield 508	com/sun/security/sasl/digest/DigestMD5Server:negotiatedRealm	Ljava/lang/String;
    //   226: invokeinterface 578 2 0
    //   231: ifne +33 -> 264
    //   234: new 376	javax/security/sasl/SaslException
    //   237: dup
    //   238: new 358	java/lang/StringBuilder
    //   241: dup
    //   242: invokespecial 552	java/lang/StringBuilder:<init>	()V
    //   245: ldc 42
    //   247: invokevirtual 555	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   250: aload_0
    //   251: getfield 508	com/sun/security/sasl/digest/DigestMD5Server:negotiatedRealm	Ljava/lang/String;
    //   254: invokevirtual 555	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   257: invokevirtual 553	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   260: invokespecial 572	javax/security/sasl/SaslException:<init>	(Ljava/lang/String;)V
    //   263: athrow
    //   264: aload_1
    //   265: iconst_2
    //   266: aaload
    //   267: ifnonnull +13 -> 280
    //   270: new 376	javax/security/sasl/SaslException
    //   273: dup
    //   274: ldc 38
    //   276: invokespecial 572	javax/security/sasl/SaslException:<init>	(Ljava/lang/String;)V
    //   279: athrow
    //   280: aload_1
    //   281: iconst_2
    //   282: aaload
    //   283: astore 4
    //   285: aload 4
    //   287: aload_0
    //   288: getfield 500	com/sun/security/sasl/digest/DigestMD5Server:nonce	[B
    //   291: invokestatic 558	java/util/Arrays:equals	([B[B)Z
    //   294: ifne +13 -> 307
    //   297: new 376	javax/security/sasl/SaslException
    //   300: dup
    //   301: ldc 35
    //   303: invokespecial 572	javax/security/sasl/SaslException:<init>	(Ljava/lang/String;)V
    //   306: athrow
    //   307: aload_1
    //   308: iconst_3
    //   309: aaload
    //   310: ifnonnull +13 -> 323
    //   313: new 376	javax/security/sasl/SaslException
    //   316: dup
    //   317: ldc 37
    //   319: invokespecial 572	javax/security/sasl/SaslException:<init>	(Ljava/lang/String;)V
    //   322: athrow
    //   323: aload_1
    //   324: iconst_3
    //   325: aaload
    //   326: astore 5
    //   328: aload_1
    //   329: iconst_4
    //   330: aaload
    //   331: ifnull +62 -> 393
    //   334: iconst_1
    //   335: new 356	java/lang/String
    //   338: dup
    //   339: aload_1
    //   340: iconst_4
    //   341: aaload
    //   342: aload_0
    //   343: getfield 505	com/sun/security/sasl/digest/DigestMD5Server:encoding	Ljava/lang/String;
    //   346: invokespecial 546	java/lang/String:<init>	([BLjava/lang/String;)V
    //   349: bipush 16
    //   351: invokestatic 536	java/lang/Integer:parseInt	(Ljava/lang/String;I)I
    //   354: if_icmpeq +39 -> 393
    //   357: new 376	javax/security/sasl/SaslException
    //   360: dup
    //   361: new 358	java/lang/StringBuilder
    //   364: dup
    //   365: invokespecial 552	java/lang/StringBuilder:<init>	()V
    //   368: ldc 41
    //   370: invokevirtual 555	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   373: new 356	java/lang/String
    //   376: dup
    //   377: aload_1
    //   378: iconst_4
    //   379: aaload
    //   380: invokespecial 540	java/lang/String:<init>	([B)V
    //   383: invokevirtual 555	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   386: invokevirtual 553	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   389: invokespecial 572	javax/security/sasl/SaslException:<init>	(Ljava/lang/String;)V
    //   392: athrow
    //   393: aload_0
    //   394: aload_1
    //   395: iconst_5
    //   396: aaload
    //   397: ifnull +20 -> 417
    //   400: new 356	java/lang/String
    //   403: dup
    //   404: aload_1
    //   405: iconst_5
    //   406: aaload
    //   407: aload_0
    //   408: getfield 505	com/sun/security/sasl/digest/DigestMD5Server:encoding	Ljava/lang/String;
    //   411: invokespecial 546	java/lang/String:<init>	([BLjava/lang/String;)V
    //   414: goto +5 -> 419
    //   417: ldc 58
    //   419: putfield 507	com/sun/security/sasl/digest/DigestMD5Server:negotiatedQop	Ljava/lang/String;
    //   422: getstatic 514	com/sun/security/sasl/digest/DigestMD5Server:logger	Ljava/util/logging/Logger;
    //   425: getstatic 516	java/util/logging/Level:FINE	Ljava/util/logging/Level;
    //   428: ldc 51
    //   430: aload_0
    //   431: getfield 507	com/sun/security/sasl/digest/DigestMD5Server:negotiatedQop	Ljava/lang/String;
    //   434: invokevirtual 563	java/util/logging/Logger:log	(Ljava/util/logging/Level;Ljava/lang/String;Ljava/lang/Object;)V
    //   437: aload_0
    //   438: getfield 507	com/sun/security/sasl/digest/DigestMD5Server:negotiatedQop	Ljava/lang/String;
    //   441: astore 7
    //   443: iconst_m1
    //   444: istore 8
    //   446: aload 7
    //   448: invokevirtual 538	java/lang/String:hashCode	()I
    //   451: lookupswitch	default:+78->529, 3005864:+33->484, 1414216745:+65->516, 1431098954:+49->500
    //   484: aload 7
    //   486: ldc 58
    //   488: invokevirtual 541	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   491: ifeq +38 -> 529
    //   494: iconst_0
    //   495: istore 8
    //   497: goto +32 -> 529
    //   500: aload 7
    //   502: ldc 60
    //   504: invokevirtual 541	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   507: ifeq +22 -> 529
    //   510: iconst_1
    //   511: istore 8
    //   513: goto +16 -> 529
    //   516: aload 7
    //   518: ldc 59
    //   520: invokevirtual 541	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   523: ifeq +6 -> 529
    //   526: iconst_2
    //   527: istore 8
    //   529: iload 8
    //   531: tableswitch	default:+80->611, 0:+25->556, 1:+31->562, 2:+53->584
    //   556: iconst_1
    //   557: istore 6
    //   559: goto +82 -> 641
    //   562: iconst_2
    //   563: istore 6
    //   565: aload_0
    //   566: iconst_1
    //   567: putfield 495	com/sun/security/sasl/digest/DigestMD5Server:integrity	Z
    //   570: aload_0
    //   571: aload_0
    //   572: getfield 492	com/sun/security/sasl/digest/DigestMD5Server:sendMaxBufSize	I
    //   575: bipush 16
    //   577: isub
    //   578: putfield 490	com/sun/security/sasl/digest/DigestMD5Server:rawSendSize	I
    //   581: goto +60 -> 641
    //   584: iconst_4
    //   585: istore 6
    //   587: aload_0
    //   588: aload_0
    //   589: iconst_1
    //   590: dup_x1
    //   591: putfield 496	com/sun/security/sasl/digest/DigestMD5Server:privacy	Z
    //   594: putfield 495	com/sun/security/sasl/digest/DigestMD5Server:integrity	Z
    //   597: aload_0
    //   598: aload_0
    //   599: getfield 492	com/sun/security/sasl/digest/DigestMD5Server:sendMaxBufSize	I
    //   602: bipush 26
    //   604: isub
    //   605: putfield 490	com/sun/security/sasl/digest/DigestMD5Server:rawSendSize	I
    //   608: goto +33 -> 641
    //   611: new 376	javax/security/sasl/SaslException
    //   614: dup
    //   615: new 358	java/lang/StringBuilder
    //   618: dup
    //   619: invokespecial 552	java/lang/StringBuilder:<init>	()V
    //   622: ldc 33
    //   624: invokevirtual 555	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   627: aload_0
    //   628: getfield 507	com/sun/security/sasl/digest/DigestMD5Server:negotiatedQop	Ljava/lang/String;
    //   631: invokevirtual 555	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   634: invokevirtual 553	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   637: invokespecial 572	javax/security/sasl/SaslException:<init>	(Ljava/lang/String;)V
    //   640: athrow
    //   641: iload 6
    //   643: aload_0
    //   644: getfield 489	com/sun/security/sasl/digest/DigestMD5Server:allQop	B
    //   647: iand
    //   648: ifne +33 -> 681
    //   651: new 376	javax/security/sasl/SaslException
    //   654: dup
    //   655: new 358	java/lang/StringBuilder
    //   658: dup
    //   659: invokespecial 552	java/lang/StringBuilder:<init>	()V
    //   662: ldc 45
    //   664: invokevirtual 555	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   667: aload_0
    //   668: getfield 507	com/sun/security/sasl/digest/DigestMD5Server:negotiatedQop	Ljava/lang/String;
    //   671: invokevirtual 555	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   674: invokevirtual 553	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   677: invokespecial 572	javax/security/sasl/SaslException:<init>	(Ljava/lang/String;)V
    //   680: athrow
    //   681: aload_0
    //   682: getfield 496	com/sun/security/sasl/digest/DigestMD5Server:privacy	Z
    //   685: ifeq +216 -> 901
    //   688: aload_0
    //   689: aload_1
    //   690: bipush 10
    //   692: aaload
    //   693: ifnull +21 -> 714
    //   696: new 356	java/lang/String
    //   699: dup
    //   700: aload_1
    //   701: bipush 10
    //   703: aaload
    //   704: aload_0
    //   705: getfield 505	com/sun/security/sasl/digest/DigestMD5Server:encoding	Ljava/lang/String;
    //   708: invokespecial 546	java/lang/String:<init>	([BLjava/lang/String;)V
    //   711: goto +4 -> 715
    //   714: aconst_null
    //   715: putfield 506	com/sun/security/sasl/digest/DigestMD5Server:negotiatedCipher	Ljava/lang/String;
    //   718: aload_0
    //   719: getfield 506	com/sun/security/sasl/digest/DigestMD5Server:negotiatedCipher	Ljava/lang/String;
    //   722: ifnonnull +13 -> 735
    //   725: new 376	javax/security/sasl/SaslException
    //   728: dup
    //   729: ldc 40
    //   731: invokespecial 572	javax/security/sasl/SaslException:<init>	(Ljava/lang/String;)V
    //   734: athrow
    //   735: iconst_m1
    //   736: istore 7
    //   738: getstatic 514	com/sun/security/sasl/digest/DigestMD5Server:logger	Ljava/util/logging/Logger;
    //   741: getstatic 516	java/util/logging/Level:FINE	Ljava/util/logging/Level;
    //   744: ldc 52
    //   746: aload_0
    //   747: getfield 506	com/sun/security/sasl/digest/DigestMD5Server:negotiatedCipher	Ljava/lang/String;
    //   750: invokevirtual 563	java/util/logging/Logger:log	(Ljava/util/logging/Level;Ljava/lang/String;Ljava/lang/Object;)V
    //   753: iconst_0
    //   754: istore 8
    //   756: iload 8
    //   758: getstatic 511	com/sun/security/sasl/digest/DigestMD5Server:CIPHER_TOKENS	[Ljava/lang/String;
    //   761: arraylength
    //   762: if_icmpge +42 -> 804
    //   765: aload_0
    //   766: getfield 506	com/sun/security/sasl/digest/DigestMD5Server:negotiatedCipher	Ljava/lang/String;
    //   769: getstatic 511	com/sun/security/sasl/digest/DigestMD5Server:CIPHER_TOKENS	[Ljava/lang/String;
    //   772: iload 8
    //   774: aaload
    //   775: invokevirtual 541	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   778: ifeq +20 -> 798
    //   781: aload_0
    //   782: getfield 499	com/sun/security/sasl/digest/DigestMD5Server:myCiphers	[B
    //   785: iload 8
    //   787: baload
    //   788: ifeq +10 -> 798
    //   791: iload 8
    //   793: istore 7
    //   795: goto +9 -> 804
    //   798: iinc 8 1
    //   801: goto -45 -> 756
    //   804: iload 7
    //   806: iconst_m1
    //   807: if_icmpne +33 -> 840
    //   810: new 376	javax/security/sasl/SaslException
    //   813: dup
    //   814: new 358	java/lang/StringBuilder
    //   817: dup
    //   818: invokespecial 552	java/lang/StringBuilder:<init>	()V
    //   821: ldc 46
    //   823: invokevirtual 555	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   826: aload_0
    //   827: getfield 506	com/sun/security/sasl/digest/DigestMD5Server:negotiatedCipher	Ljava/lang/String;
    //   830: invokevirtual 555	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   833: invokevirtual 553	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   836: invokespecial 572	javax/security/sasl/SaslException:<init>	(Ljava/lang/String;)V
    //   839: athrow
    //   840: getstatic 498	com/sun/security/sasl/digest/DigestMD5Server:CIPHER_MASKS	[B
    //   843: iload 7
    //   845: baload
    //   846: iconst_4
    //   847: iand
    //   848: ifeq +12 -> 860
    //   851: aload_0
    //   852: ldc 72
    //   854: putfield 509	com/sun/security/sasl/digest/DigestMD5Server:negotiatedStrength	Ljava/lang/String;
    //   857: goto +29 -> 886
    //   860: getstatic 498	com/sun/security/sasl/digest/DigestMD5Server:CIPHER_MASKS	[B
    //   863: iload 7
    //   865: baload
    //   866: iconst_2
    //   867: iand
    //   868: ifeq +12 -> 880
    //   871: aload_0
    //   872: ldc 77
    //   874: putfield 509	com/sun/security/sasl/digest/DigestMD5Server:negotiatedStrength	Ljava/lang/String;
    //   877: goto +9 -> 886
    //   880: aload_0
    //   881: ldc 74
    //   883: putfield 509	com/sun/security/sasl/digest/DigestMD5Server:negotiatedStrength	Ljava/lang/String;
    //   886: getstatic 514	com/sun/security/sasl/digest/DigestMD5Server:logger	Ljava/util/logging/Logger;
    //   889: getstatic 516	java/util/logging/Level:FINE	Ljava/util/logging/Level;
    //   892: ldc 53
    //   894: aload_0
    //   895: getfield 509	com/sun/security/sasl/digest/DigestMD5Server:negotiatedStrength	Ljava/lang/String;
    //   898: invokevirtual 563	java/util/logging/Logger:log	(Ljava/util/logging/Level;Ljava/lang/String;Ljava/lang/Object;)V
    //   901: aload_1
    //   902: bipush 6
    //   904: aaload
    //   905: ifnull +21 -> 926
    //   908: new 356	java/lang/String
    //   911: dup
    //   912: aload_1
    //   913: bipush 6
    //   915: aaload
    //   916: aload_0
    //   917: getfield 505	com/sun/security/sasl/digest/DigestMD5Server:encoding	Ljava/lang/String;
    //   920: invokespecial 546	java/lang/String:<init>	([BLjava/lang/String;)V
    //   923: goto +4 -> 927
    //   926: aconst_null
    //   927: astore 7
    //   929: aload 7
    //   931: ifnull +16 -> 947
    //   934: getstatic 514	com/sun/security/sasl/digest/DigestMD5Server:logger	Ljava/util/logging/Logger;
    //   937: getstatic 516	java/util/logging/Level:FINE	Ljava/util/logging/Level;
    //   940: ldc 54
    //   942: aload 7
    //   944: invokevirtual 563	java/util/logging/Logger:log	(Ljava/util/logging/Level;Ljava/lang/String;Ljava/lang/Object;)V
    //   947: aload_0
    //   948: getfield 504	com/sun/security/sasl/digest/DigestMD5Server:digestUri	Ljava/lang/String;
    //   951: aload 7
    //   953: invokestatic 525	com/sun/security/sasl/digest/DigestMD5Server:uriMatches	(Ljava/lang/String;Ljava/lang/String;)Z
    //   956: ifeq +12 -> 968
    //   959: aload_0
    //   960: aload 7
    //   962: putfield 504	com/sun/security/sasl/digest/DigestMD5Server:digestUri	Ljava/lang/String;
    //   965: goto +43 -> 1008
    //   968: new 376	javax/security/sasl/SaslException
    //   971: dup
    //   972: new 358	java/lang/StringBuilder
    //   975: dup
    //   976: invokespecial 552	java/lang/StringBuilder:<init>	()V
    //   979: ldc 34
    //   981: invokevirtual 555	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   984: aload 7
    //   986: invokevirtual 555	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   989: ldc 12
    //   991: invokevirtual 555	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   994: aload_0
    //   995: getfield 504	com/sun/security/sasl/digest/DigestMD5Server:digestUri	Ljava/lang/String;
    //   998: invokevirtual 555	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1001: invokevirtual 553	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   1004: invokespecial 572	javax/security/sasl/SaslException:<init>	(Ljava/lang/String;)V
    //   1007: athrow
    //   1008: aload_1
    //   1009: bipush 7
    //   1011: aaload
    //   1012: astore 8
    //   1014: aload 8
    //   1016: ifnonnull +13 -> 1029
    //   1019: new 376	javax/security/sasl/SaslException
    //   1022: dup
    //   1023: ldc 31
    //   1025: invokespecial 572	javax/security/sasl/SaslException:<init>	(Ljava/lang/String;)V
    //   1028: athrow
    //   1029: aload_1
    //   1030: bipush 11
    //   1032: aaload
    //   1033: dup
    //   1034: astore 9
    //   1036: ifnull +19 -> 1055
    //   1039: new 356	java/lang/String
    //   1042: dup
    //   1043: aload 9
    //   1045: aload_0
    //   1046: getfield 505	com/sun/security/sasl/digest/DigestMD5Server:encoding	Ljava/lang/String;
    //   1049: invokespecial 546	java/lang/String:<init>	([BLjava/lang/String;)V
    //   1052: goto +4 -> 1056
    //   1055: aload_3
    //   1056: astore 10
    //   1058: aload 9
    //   1060: ifnull +23 -> 1083
    //   1063: getstatic 514	com/sun/security/sasl/digest/DigestMD5Server:logger	Ljava/util/logging/Logger;
    //   1066: getstatic 516	java/util/logging/Level:FINE	Ljava/util/logging/Level;
    //   1069: ldc 55
    //   1071: new 356	java/lang/String
    //   1074: dup
    //   1075: aload 9
    //   1077: invokespecial 540	java/lang/String:<init>	([B)V
    //   1080: invokevirtual 563	java/util/logging/Logger:log	(Ljava/util/logging/Level;Ljava/lang/String;Ljava/lang/Object;)V
    //   1083: new 375	javax/security/sasl/RealmCallback
    //   1086: dup
    //   1087: ldc 18
    //   1089: aload_0
    //   1090: getfield 508	com/sun/security/sasl/digest/DigestMD5Server:negotiatedRealm	Ljava/lang/String;
    //   1093: invokespecial 571	javax/security/sasl/RealmCallback:<init>	(Ljava/lang/String;Ljava/lang/String;)V
    //   1096: astore 12
    //   1098: new 371	javax/security/auth/callback/NameCallback
    //   1101: dup
    //   1102: ldc 15
    //   1104: aload_3
    //   1105: invokespecial 564	javax/security/auth/callback/NameCallback:<init>	(Ljava/lang/String;Ljava/lang/String;)V
    //   1108: astore 13
    //   1110: new 372	javax/security/auth/callback/PasswordCallback
    //   1113: dup
    //   1114: ldc 17
    //   1116: iconst_0
    //   1117: invokespecial 567	javax/security/auth/callback/PasswordCallback:<init>	(Ljava/lang/String;Z)V
    //   1120: astore 14
    //   1122: aload_0
    //   1123: getfield 515	com/sun/security/sasl/digest/DigestMD5Server:cbh	Ljavax/security/auth/callback/CallbackHandler;
    //   1126: iconst_3
    //   1127: anewarray 369	javax/security/auth/callback/Callback
    //   1130: dup
    //   1131: iconst_0
    //   1132: aload 12
    //   1134: aastore
    //   1135: dup
    //   1136: iconst_1
    //   1137: aload 13
    //   1139: aastore
    //   1140: dup
    //   1141: iconst_2
    //   1142: aload 14
    //   1144: aastore
    //   1145: invokeinterface 580 2 0
    //   1150: aload 14
    //   1152: invokevirtual 566	javax/security/auth/callback/PasswordCallback:getPassword	()[C
    //   1155: astore 11
    //   1157: aload 14
    //   1159: invokevirtual 565	javax/security/auth/callback/PasswordCallback:clearPassword	()V
    //   1162: goto +31 -> 1193
    //   1165: astore 12
    //   1167: new 376	javax/security/sasl/SaslException
    //   1170: dup
    //   1171: ldc 21
    //   1173: aload 12
    //   1175: invokespecial 573	javax/security/sasl/SaslException:<init>	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   1178: athrow
    //   1179: astore 12
    //   1181: new 376	javax/security/sasl/SaslException
    //   1184: dup
    //   1185: ldc 26
    //   1187: aload 12
    //   1189: invokespecial 573	javax/security/sasl/SaslException:<init>	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   1192: athrow
    //   1193: aload 11
    //   1195: ifnonnull +42 -> 1237
    //   1198: new 376	javax/security/sasl/SaslException
    //   1201: dup
    //   1202: new 358	java/lang/StringBuilder
    //   1205: dup
    //   1206: invokespecial 552	java/lang/StringBuilder:<init>	()V
    //   1209: ldc 30
    //   1211: invokevirtual 555	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1214: aload_3
    //   1215: invokevirtual 555	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1218: ldc 4
    //   1220: invokevirtual 555	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1223: aload_0
    //   1224: getfield 508	com/sun/security/sasl/digest/DigestMD5Server:negotiatedRealm	Ljava/lang/String;
    //   1227: invokevirtual 555	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1230: invokevirtual 553	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   1233: invokespecial 572	javax/security/sasl/SaslException:<init>	(Ljava/lang/String;)V
    //   1236: athrow
    //   1237: aload_0
    //   1238: ldc 14
    //   1240: aload_0
    //   1241: getfield 504	com/sun/security/sasl/digest/DigestMD5Server:digestUri	Ljava/lang/String;
    //   1244: aload_0
    //   1245: getfield 507	com/sun/security/sasl/digest/DigestMD5Server:negotiatedQop	Ljava/lang/String;
    //   1248: aload_3
    //   1249: aload_0
    //   1250: getfield 508	com/sun/security/sasl/digest/DigestMD5Server:negotiatedRealm	Ljava/lang/String;
    //   1253: aload 11
    //   1255: aload_0
    //   1256: getfield 500	com/sun/security/sasl/digest/DigestMD5Server:nonce	[B
    //   1259: aload 5
    //   1261: iconst_1
    //   1262: aload 9
    //   1264: invokevirtual 528	com/sun/security/sasl/digest/DigestMD5Server:generateResponseValue	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;[C[B[BI[B)[B
    //   1267: astore 12
    //   1269: goto +31 -> 1300
    //   1272: astore 13
    //   1274: new 376	javax/security/sasl/SaslException
    //   1277: dup
    //   1278: ldc 43
    //   1280: aload 13
    //   1282: invokespecial 573	javax/security/sasl/SaslException:<init>	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   1285: athrow
    //   1286: astore 13
    //   1288: new 376	javax/security/sasl/SaslException
    //   1291: dup
    //   1292: ldc 43
    //   1294: aload 13
    //   1296: invokespecial 573	javax/security/sasl/SaslException:<init>	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   1299: athrow
    //   1300: aload 8
    //   1302: aload 12
    //   1304: invokestatic 558	java/util/Arrays:equals	([B[B)Z
    //   1307: ifne +13 -> 1320
    //   1310: new 376	javax/security/sasl/SaslException
    //   1313: dup
    //   1314: ldc 36
    //   1316: invokespecial 572	javax/security/sasl/SaslException:<init>	(Ljava/lang/String;)V
    //   1319: athrow
    //   1320: new 374	javax/security/sasl/AuthorizeCallback
    //   1323: dup
    //   1324: aload_3
    //   1325: aload 10
    //   1327: invokespecial 570	javax/security/sasl/AuthorizeCallback:<init>	(Ljava/lang/String;Ljava/lang/String;)V
    //   1330: astore 13
    //   1332: aload_0
    //   1333: getfield 515	com/sun/security/sasl/digest/DigestMD5Server:cbh	Ljavax/security/auth/callback/CallbackHandler;
    //   1336: iconst_1
    //   1337: anewarray 369	javax/security/auth/callback/Callback
    //   1340: dup
    //   1341: iconst_0
    //   1342: aload 13
    //   1344: aastore
    //   1345: invokeinterface 580 2 0
    //   1350: aload 13
    //   1352: invokevirtual 568	javax/security/sasl/AuthorizeCallback:isAuthorized	()Z
    //   1355: ifeq +15 -> 1370
    //   1358: aload_0
    //   1359: aload 13
    //   1361: invokevirtual 569	javax/security/sasl/AuthorizeCallback:getAuthorizedID	()Ljava/lang/String;
    //   1364: putfield 503	com/sun/security/sasl/digest/DigestMD5Server:authzid	Ljava/lang/String;
    //   1367: goto +40 -> 1407
    //   1370: new 376	javax/security/sasl/SaslException
    //   1373: dup
    //   1374: new 358	java/lang/StringBuilder
    //   1377: dup
    //   1378: invokespecial 552	java/lang/StringBuilder:<init>	()V
    //   1381: ldc 20
    //   1383: invokevirtual 555	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1386: aload_3
    //   1387: invokevirtual 555	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1390: ldc 5
    //   1392: invokevirtual 555	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1395: aload 10
    //   1397: invokevirtual 555	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1400: invokevirtual 553	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   1403: invokespecial 572	javax/security/sasl/SaslException:<init>	(Ljava/lang/String;)V
    //   1406: athrow
    //   1407: goto +36 -> 1443
    //   1410: astore 13
    //   1412: aload 13
    //   1414: athrow
    //   1415: astore 13
    //   1417: new 376	javax/security/sasl/SaslException
    //   1420: dup
    //   1421: ldc 22
    //   1423: aload 13
    //   1425: invokespecial 573	javax/security/sasl/SaslException:<init>	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   1428: athrow
    //   1429: astore 13
    //   1431: new 376	javax/security/sasl/SaslException
    //   1434: dup
    //   1435: ldc 27
    //   1437: aload 13
    //   1439: invokespecial 573	javax/security/sasl/SaslException:<init>	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   1442: athrow
    //   1443: aload_0
    //   1444: aload_3
    //   1445: aload 11
    //   1447: aload 5
    //   1449: iconst_1
    //   1450: aload 9
    //   1452: invokespecial 524	com/sun/security/sasl/digest/DigestMD5Server:generateResponseAuth	(Ljava/lang/String;[C[BI[B)[B
    //   1455: astore 13
    //   1457: iconst_0
    //   1458: istore 14
    //   1460: iload 14
    //   1462: aload 11
    //   1464: arraylength
    //   1465: if_icmpge +15 -> 1480
    //   1468: aload 11
    //   1470: iload 14
    //   1472: iconst_0
    //   1473: castore
    //   1474: iinc 14 1
    //   1477: goto -17 -> 1460
    //   1480: aload 13
    //   1482: areturn
    //   1483: astore 15
    //   1485: iconst_0
    //   1486: istore 16
    //   1488: iload 16
    //   1490: aload 11
    //   1492: arraylength
    //   1493: if_icmpge +15 -> 1508
    //   1496: aload 11
    //   1498: iload 16
    //   1500: iconst_0
    //   1501: castore
    //   1502: iinc 16 1
    //   1505: goto -17 -> 1488
    //   1508: aload 15
    //   1510: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	1511	0	this	DigestMD5Server
    //   0	1511	1	paramArrayOfByte	byte[][]
    //   104	18	2	i	int
    //   148	1297	3	str1	String
    //   283	3	4	arrayOfByte1	byte[]
    //   326	1122	5	arrayOfByte2	byte[]
    //   557	91	6	j	int
    //   441	76	7	str2	String
    //   736	128	7	k	int
    //   927	58	7	localObject1	Object
    //   444	355	8	m	int
    //   1012	289	8	arrayOfByte3	byte[]
    //   1034	417	9	arrayOfByte4	byte[]
    //   1056	340	10	str3	String
    //   1155	342	11	arrayOfChar	char[]
    //   1096	37	12	localRealmCallback	javax.security.sasl.RealmCallback
    //   1165	9	12	localUnsupportedCallbackException1	javax.security.auth.callback.UnsupportedCallbackException
    //   1179	9	12	localIOException1	IOException
    //   1267	36	12	arrayOfByte5	byte[]
    //   1108	30	13	localNameCallback	javax.security.auth.callback.NameCallback
    //   1272	9	13	localNoSuchAlgorithmException	NoSuchAlgorithmException
    //   1286	9	13	localIOException2	IOException
    //   1330	30	13	localAuthorizeCallback	javax.security.sasl.AuthorizeCallback
    //   1410	3	13	localSaslException	SaslException
    //   1415	9	13	localUnsupportedCallbackException2	javax.security.auth.callback.UnsupportedCallbackException
    //   1429	9	13	localIOException3	IOException
    //   1455	26	13	arrayOfByte6	byte[]
    //   1120	38	14	localPasswordCallback	javax.security.auth.callback.PasswordCallback
    //   1458	17	14	n	int
    //   1483	26	15	localObject2	Object
    //   1486	17	16	i1	int
    // Exception table:
    //   from	to	target	type
    //   1083	1162	1165	javax/security/auth/callback/UnsupportedCallbackException
    //   1083	1162	1179	java/io/IOException
    //   1237	1269	1272	java/security/NoSuchAlgorithmException
    //   1237	1269	1286	java/io/IOException
    //   1320	1407	1410	javax/security/sasl/SaslException
    //   1320	1407	1415	javax/security/auth/callback/UnsupportedCallbackException
    //   1320	1407	1429	java/io/IOException
    //   1237	1457	1483	finally
    //   1483	1485	1483	finally
  }
  
  private static boolean uriMatches(String paramString1, String paramString2)
  {
    if (paramString1.equalsIgnoreCase(paramString2)) {
      return true;
    }
    if (paramString1.endsWith("/*"))
    {
      int i = paramString1.length() - 1;
      String str1 = paramString1.substring(0, i);
      String str2 = paramString2.substring(0, i);
      return str1.equalsIgnoreCase(str2);
    }
    return false;
  }
  
  private byte[] generateResponseAuth(String paramString, char[] paramArrayOfChar, byte[] paramArrayOfByte1, int paramInt, byte[] paramArrayOfByte2)
    throws SaslException
  {
    try
    {
      byte[] arrayOfByte1 = generateResponseValue("", digestUri, negotiatedQop, paramString, negotiatedRealm, paramArrayOfChar, nonce, paramArrayOfByte1, paramInt, paramArrayOfByte2);
      byte[] arrayOfByte2 = new byte[arrayOfByte1.length + 8];
      System.arraycopy("rspauth=".getBytes(encoding), 0, arrayOfByte2, 0, 8);
      System.arraycopy(arrayOfByte1, 0, arrayOfByte2, 8, arrayOfByte1.length);
      return arrayOfByte2;
    }
    catch (NoSuchAlgorithmException localNoSuchAlgorithmException)
    {
      throw new SaslException("DIGEST-MD5: problem generating response", localNoSuchAlgorithmException);
    }
    catch (IOException localIOException)
    {
      throw new SaslException("DIGEST-MD5: problem generating response", localIOException);
    }
  }
  
  public String getAuthorizationID()
  {
    if (completed) {
      return authzid;
    }
    throw new IllegalStateException("DIGEST-MD5 server negotiation not complete");
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\security\sasl\digest\DigestMD5Server.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */