package com.sun.org.apache.xerces.internal.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class EncodingMap
{
  protected static final Map<String, String> fIANA2JavaMap;
  protected static final Map<String, String> fJava2IANAMap;
  
  public EncodingMap() {}
  
  public static String getIANA2JavaMapping(String paramString)
  {
    return (String)fIANA2JavaMap.get(paramString);
  }
  
  public static String getJava2IANAMapping(String paramString)
  {
    return (String)fJava2IANAMap.get(paramString);
  }
  
  static
  {
    HashMap localHashMap1 = new HashMap();
    HashMap localHashMap2 = new HashMap();
    localHashMap1.put("BIG5", "Big5");
    localHashMap1.put("CSBIG5", "Big5");
    localHashMap1.put("CP037", "CP037");
    localHashMap1.put("IBM037", "CP037");
    localHashMap1.put("CSIBM037", "CP037");
    localHashMap1.put("EBCDIC-CP-US", "CP037");
    localHashMap1.put("EBCDIC-CP-CA", "CP037");
    localHashMap1.put("EBCDIC-CP-NL", "CP037");
    localHashMap1.put("EBCDIC-CP-WT", "CP037");
    localHashMap1.put("IBM273", "CP273");
    localHashMap1.put("CP273", "CP273");
    localHashMap1.put("CSIBM273", "CP273");
    localHashMap1.put("IBM277", "CP277");
    localHashMap1.put("CP277", "CP277");
    localHashMap1.put("CSIBM277", "CP277");
    localHashMap1.put("EBCDIC-CP-DK", "CP277");
    localHashMap1.put("EBCDIC-CP-NO", "CP277");
    localHashMap1.put("IBM278", "CP278");
    localHashMap1.put("CP278", "CP278");
    localHashMap1.put("CSIBM278", "CP278");
    localHashMap1.put("EBCDIC-CP-FI", "CP278");
    localHashMap1.put("EBCDIC-CP-SE", "CP278");
    localHashMap1.put("IBM280", "CP280");
    localHashMap1.put("CP280", "CP280");
    localHashMap1.put("CSIBM280", "CP280");
    localHashMap1.put("EBCDIC-CP-IT", "CP280");
    localHashMap1.put("IBM284", "CP284");
    localHashMap1.put("CP284", "CP284");
    localHashMap1.put("CSIBM284", "CP284");
    localHashMap1.put("EBCDIC-CP-ES", "CP284");
    localHashMap1.put("EBCDIC-CP-GB", "CP285");
    localHashMap1.put("IBM285", "CP285");
    localHashMap1.put("CP285", "CP285");
    localHashMap1.put("CSIBM285", "CP285");
    localHashMap1.put("EBCDIC-JP-KANA", "CP290");
    localHashMap1.put("IBM290", "CP290");
    localHashMap1.put("CP290", "CP290");
    localHashMap1.put("CSIBM290", "CP290");
    localHashMap1.put("EBCDIC-CP-FR", "CP297");
    localHashMap1.put("IBM297", "CP297");
    localHashMap1.put("CP297", "CP297");
    localHashMap1.put("CSIBM297", "CP297");
    localHashMap1.put("EBCDIC-CP-AR1", "CP420");
    localHashMap1.put("IBM420", "CP420");
    localHashMap1.put("CP420", "CP420");
    localHashMap1.put("CSIBM420", "CP420");
    localHashMap1.put("EBCDIC-CP-HE", "CP424");
    localHashMap1.put("IBM424", "CP424");
    localHashMap1.put("CP424", "CP424");
    localHashMap1.put("CSIBM424", "CP424");
    localHashMap1.put("IBM437", "CP437");
    localHashMap1.put("437", "CP437");
    localHashMap1.put("CP437", "CP437");
    localHashMap1.put("CSPC8CODEPAGE437", "CP437");
    localHashMap1.put("EBCDIC-CP-CH", "CP500");
    localHashMap1.put("IBM500", "CP500");
    localHashMap1.put("CP500", "CP500");
    localHashMap1.put("CSIBM500", "CP500");
    localHashMap1.put("EBCDIC-CP-CH", "CP500");
    localHashMap1.put("EBCDIC-CP-BE", "CP500");
    localHashMap1.put("IBM775", "CP775");
    localHashMap1.put("CP775", "CP775");
    localHashMap1.put("CSPC775BALTIC", "CP775");
    localHashMap1.put("IBM850", "CP850");
    localHashMap1.put("850", "CP850");
    localHashMap1.put("CP850", "CP850");
    localHashMap1.put("CSPC850MULTILINGUAL", "CP850");
    localHashMap1.put("IBM852", "CP852");
    localHashMap1.put("852", "CP852");
    localHashMap1.put("CP852", "CP852");
    localHashMap1.put("CSPCP852", "CP852");
    localHashMap1.put("IBM855", "CP855");
    localHashMap1.put("855", "CP855");
    localHashMap1.put("CP855", "CP855");
    localHashMap1.put("CSIBM855", "CP855");
    localHashMap1.put("IBM857", "CP857");
    localHashMap1.put("857", "CP857");
    localHashMap1.put("CP857", "CP857");
    localHashMap1.put("CSIBM857", "CP857");
    localHashMap1.put("IBM00858", "CP858");
    localHashMap1.put("CP00858", "CP858");
    localHashMap1.put("CCSID00858", "CP858");
    localHashMap1.put("IBM860", "CP860");
    localHashMap1.put("860", "CP860");
    localHashMap1.put("CP860", "CP860");
    localHashMap1.put("CSIBM860", "CP860");
    localHashMap1.put("IBM861", "CP861");
    localHashMap1.put("861", "CP861");
    localHashMap1.put("CP861", "CP861");
    localHashMap1.put("CP-IS", "CP861");
    localHashMap1.put("CSIBM861", "CP861");
    localHashMap1.put("IBM862", "CP862");
    localHashMap1.put("862", "CP862");
    localHashMap1.put("CP862", "CP862");
    localHashMap1.put("CSPC862LATINHEBREW", "CP862");
    localHashMap1.put("IBM863", "CP863");
    localHashMap1.put("863", "CP863");
    localHashMap1.put("CP863", "CP863");
    localHashMap1.put("CSIBM863", "CP863");
    localHashMap1.put("IBM864", "CP864");
    localHashMap1.put("CP864", "CP864");
    localHashMap1.put("CSIBM864", "CP864");
    localHashMap1.put("IBM865", "CP865");
    localHashMap1.put("865", "CP865");
    localHashMap1.put("CP865", "CP865");
    localHashMap1.put("CSIBM865", "CP865");
    localHashMap1.put("IBM866", "CP866");
    localHashMap1.put("866", "CP866");
    localHashMap1.put("CP866", "CP866");
    localHashMap1.put("CSIBM866", "CP866");
    localHashMap1.put("IBM868", "CP868");
    localHashMap1.put("CP868", "CP868");
    localHashMap1.put("CSIBM868", "CP868");
    localHashMap1.put("CP-AR", "CP868");
    localHashMap1.put("IBM869", "CP869");
    localHashMap1.put("CP869", "CP869");
    localHashMap1.put("CSIBM869", "CP869");
    localHashMap1.put("CP-GR", "CP869");
    localHashMap1.put("IBM870", "CP870");
    localHashMap1.put("CP870", "CP870");
    localHashMap1.put("CSIBM870", "CP870");
    localHashMap1.put("EBCDIC-CP-ROECE", "CP870");
    localHashMap1.put("EBCDIC-CP-YU", "CP870");
    localHashMap1.put("IBM871", "CP871");
    localHashMap1.put("CP871", "CP871");
    localHashMap1.put("CSIBM871", "CP871");
    localHashMap1.put("EBCDIC-CP-IS", "CP871");
    localHashMap1.put("IBM918", "CP918");
    localHashMap1.put("CP918", "CP918");
    localHashMap1.put("CSIBM918", "CP918");
    localHashMap1.put("EBCDIC-CP-AR2", "CP918");
    localHashMap1.put("IBM00924", "CP924");
    localHashMap1.put("CP00924", "CP924");
    localHashMap1.put("CCSID00924", "CP924");
    localHashMap1.put("EBCDIC-LATIN9--EURO", "CP924");
    localHashMap1.put("IBM1026", "CP1026");
    localHashMap1.put("CP1026", "CP1026");
    localHashMap1.put("CSIBM1026", "CP1026");
    localHashMap1.put("IBM01140", "Cp1140");
    localHashMap1.put("CP01140", "Cp1140");
    localHashMap1.put("CCSID01140", "Cp1140");
    localHashMap1.put("IBM01141", "Cp1141");
    localHashMap1.put("CP01141", "Cp1141");
    localHashMap1.put("CCSID01141", "Cp1141");
    localHashMap1.put("IBM01142", "Cp1142");
    localHashMap1.put("CP01142", "Cp1142");
    localHashMap1.put("CCSID01142", "Cp1142");
    localHashMap1.put("IBM01143", "Cp1143");
    localHashMap1.put("CP01143", "Cp1143");
    localHashMap1.put("CCSID01143", "Cp1143");
    localHashMap1.put("IBM01144", "Cp1144");
    localHashMap1.put("CP01144", "Cp1144");
    localHashMap1.put("CCSID01144", "Cp1144");
    localHashMap1.put("IBM01145", "Cp1145");
    localHashMap1.put("CP01145", "Cp1145");
    localHashMap1.put("CCSID01145", "Cp1145");
    localHashMap1.put("IBM01146", "Cp1146");
    localHashMap1.put("CP01146", "Cp1146");
    localHashMap1.put("CCSID01146", "Cp1146");
    localHashMap1.put("IBM01147", "Cp1147");
    localHashMap1.put("CP01147", "Cp1147");
    localHashMap1.put("CCSID01147", "Cp1147");
    localHashMap1.put("IBM01148", "Cp1148");
    localHashMap1.put("CP01148", "Cp1148");
    localHashMap1.put("CCSID01148", "Cp1148");
    localHashMap1.put("IBM01149", "Cp1149");
    localHashMap1.put("CP01149", "Cp1149");
    localHashMap1.put("CCSID01149", "Cp1149");
    localHashMap1.put("EUC-JP", "EUCJIS");
    localHashMap1.put("CSEUCPKDFMTJAPANESE", "EUCJIS");
    localHashMap1.put("EXTENDED_UNIX_CODE_PACKED_FORMAT_FOR_JAPANESE", "EUCJIS");
    localHashMap1.put("EUC-KR", "KSC5601");
    localHashMap1.put("CSEUCKR", "KSC5601");
    localHashMap1.put("KS_C_5601-1987", "KS_C_5601-1987");
    localHashMap1.put("ISO-IR-149", "KS_C_5601-1987");
    localHashMap1.put("KS_C_5601-1989", "KS_C_5601-1987");
    localHashMap1.put("KSC_5601", "KS_C_5601-1987");
    localHashMap1.put("KOREAN", "KS_C_5601-1987");
    localHashMap1.put("CSKSC56011987", "KS_C_5601-1987");
    localHashMap1.put("GB2312", "GB2312");
    localHashMap1.put("CSGB2312", "GB2312");
    localHashMap1.put("ISO-2022-JP", "JIS");
    localHashMap1.put("CSISO2022JP", "JIS");
    localHashMap1.put("ISO-2022-KR", "ISO2022KR");
    localHashMap1.put("CSISO2022KR", "ISO2022KR");
    localHashMap1.put("ISO-2022-CN", "ISO2022CN");
    localHashMap1.put("X0201", "JIS0201");
    localHashMap1.put("CSISO13JISC6220JP", "JIS0201");
    localHashMap1.put("X0208", "JIS0208");
    localHashMap1.put("ISO-IR-87", "JIS0208");
    localHashMap1.put("X0208dbiJIS_X0208-1983", "JIS0208");
    localHashMap1.put("CSISO87JISX0208", "JIS0208");
    localHashMap1.put("X0212", "JIS0212");
    localHashMap1.put("ISO-IR-159", "JIS0212");
    localHashMap1.put("CSISO159JISX02121990", "JIS0212");
    localHashMap1.put("GB18030", "GB18030");
    localHashMap1.put("GBK", "GBK");
    localHashMap1.put("CP936", "GBK");
    localHashMap1.put("MS936", "GBK");
    localHashMap1.put("WINDOWS-936", "GBK");
    localHashMap1.put("SHIFT_JIS", "SJIS");
    localHashMap1.put("CSSHIFTJIS", "SJIS");
    localHashMap1.put("MS_KANJI", "SJIS");
    localHashMap1.put("WINDOWS-31J", "MS932");
    localHashMap1.put("CSWINDOWS31J", "MS932");
    localHashMap1.put("WINDOWS-1250", "Cp1250");
    localHashMap1.put("WINDOWS-1251", "Cp1251");
    localHashMap1.put("WINDOWS-1252", "Cp1252");
    localHashMap1.put("WINDOWS-1253", "Cp1253");
    localHashMap1.put("WINDOWS-1254", "Cp1254");
    localHashMap1.put("WINDOWS-1255", "Cp1255");
    localHashMap1.put("WINDOWS-1256", "Cp1256");
    localHashMap1.put("WINDOWS-1257", "Cp1257");
    localHashMap1.put("WINDOWS-1258", "Cp1258");
    localHashMap1.put("TIS-620", "TIS620");
    localHashMap1.put("ISO-8859-1", "ISO8859_1");
    localHashMap1.put("ISO-IR-100", "ISO8859_1");
    localHashMap1.put("ISO_8859-1", "ISO8859_1");
    localHashMap1.put("LATIN1", "ISO8859_1");
    localHashMap1.put("CSISOLATIN1", "ISO8859_1");
    localHashMap1.put("L1", "ISO8859_1");
    localHashMap1.put("IBM819", "ISO8859_1");
    localHashMap1.put("CP819", "ISO8859_1");
    localHashMap1.put("ISO-8859-2", "ISO8859_2");
    localHashMap1.put("ISO-IR-101", "ISO8859_2");
    localHashMap1.put("ISO_8859-2", "ISO8859_2");
    localHashMap1.put("LATIN2", "ISO8859_2");
    localHashMap1.put("CSISOLATIN2", "ISO8859_2");
    localHashMap1.put("L2", "ISO8859_2");
    localHashMap1.put("ISO-8859-3", "ISO8859_3");
    localHashMap1.put("ISO-IR-109", "ISO8859_3");
    localHashMap1.put("ISO_8859-3", "ISO8859_3");
    localHashMap1.put("LATIN3", "ISO8859_3");
    localHashMap1.put("CSISOLATIN3", "ISO8859_3");
    localHashMap1.put("L3", "ISO8859_3");
    localHashMap1.put("ISO-8859-4", "ISO8859_4");
    localHashMap1.put("ISO-IR-110", "ISO8859_4");
    localHashMap1.put("ISO_8859-4", "ISO8859_4");
    localHashMap1.put("LATIN4", "ISO8859_4");
    localHashMap1.put("CSISOLATIN4", "ISO8859_4");
    localHashMap1.put("L4", "ISO8859_4");
    localHashMap1.put("ISO-8859-5", "ISO8859_5");
    localHashMap1.put("ISO-IR-144", "ISO8859_5");
    localHashMap1.put("ISO_8859-5", "ISO8859_5");
    localHashMap1.put("CYRILLIC", "ISO8859_5");
    localHashMap1.put("CSISOLATINCYRILLIC", "ISO8859_5");
    localHashMap1.put("ISO-8859-6", "ISO8859_6");
    localHashMap1.put("ISO-IR-127", "ISO8859_6");
    localHashMap1.put("ISO_8859-6", "ISO8859_6");
    localHashMap1.put("ECMA-114", "ISO8859_6");
    localHashMap1.put("ASMO-708", "ISO8859_6");
    localHashMap1.put("ARABIC", "ISO8859_6");
    localHashMap1.put("CSISOLATINARABIC", "ISO8859_6");
    localHashMap1.put("ISO-8859-7", "ISO8859_7");
    localHashMap1.put("ISO-IR-126", "ISO8859_7");
    localHashMap1.put("ISO_8859-7", "ISO8859_7");
    localHashMap1.put("ELOT_928", "ISO8859_7");
    localHashMap1.put("ECMA-118", "ISO8859_7");
    localHashMap1.put("GREEK", "ISO8859_7");
    localHashMap1.put("CSISOLATINGREEK", "ISO8859_7");
    localHashMap1.put("GREEK8", "ISO8859_7");
    localHashMap1.put("ISO-8859-8", "ISO8859_8");
    localHashMap1.put("ISO-8859-8-I", "ISO8859_8");
    localHashMap1.put("ISO-IR-138", "ISO8859_8");
    localHashMap1.put("ISO_8859-8", "ISO8859_8");
    localHashMap1.put("HEBREW", "ISO8859_8");
    localHashMap1.put("CSISOLATINHEBREW", "ISO8859_8");
    localHashMap1.put("ISO-8859-9", "ISO8859_9");
    localHashMap1.put("ISO-IR-148", "ISO8859_9");
    localHashMap1.put("ISO_8859-9", "ISO8859_9");
    localHashMap1.put("LATIN5", "ISO8859_9");
    localHashMap1.put("CSISOLATIN5", "ISO8859_9");
    localHashMap1.put("L5", "ISO8859_9");
    localHashMap1.put("ISO-8859-13", "ISO8859_13");
    localHashMap1.put("ISO-8859-15", "ISO8859_15_FDIS");
    localHashMap1.put("ISO_8859-15", "ISO8859_15_FDIS");
    localHashMap1.put("LATIN-9", "ISO8859_15_FDIS");
    localHashMap1.put("KOI8-R", "KOI8_R");
    localHashMap1.put("CSKOI8R", "KOI8_R");
    localHashMap1.put("US-ASCII", "ASCII");
    localHashMap1.put("ISO-IR-6", "ASCII");
    localHashMap1.put("ANSI_X3.4-1968", "ASCII");
    localHashMap1.put("ANSI_X3.4-1986", "ASCII");
    localHashMap1.put("ISO_646.IRV:1991", "ASCII");
    localHashMap1.put("ASCII", "ASCII");
    localHashMap1.put("CSASCII", "ASCII");
    localHashMap1.put("ISO646-US", "ASCII");
    localHashMap1.put("US", "ASCII");
    localHashMap1.put("IBM367", "ASCII");
    localHashMap1.put("CP367", "ASCII");
    localHashMap1.put("UTF-8", "UTF8");
    localHashMap1.put("UTF-16", "UTF-16");
    localHashMap1.put("UTF-16BE", "UnicodeBig");
    localHashMap1.put("UTF-16LE", "UnicodeLittle");
    localHashMap1.put("IBM-1047", "Cp1047");
    localHashMap1.put("IBM1047", "Cp1047");
    localHashMap1.put("CP1047", "Cp1047");
    localHashMap1.put("IBM-37", "CP037");
    localHashMap1.put("IBM-273", "CP273");
    localHashMap1.put("IBM-277", "CP277");
    localHashMap1.put("IBM-278", "CP278");
    localHashMap1.put("IBM-280", "CP280");
    localHashMap1.put("IBM-284", "CP284");
    localHashMap1.put("IBM-285", "CP285");
    localHashMap1.put("IBM-290", "CP290");
    localHashMap1.put("IBM-297", "CP297");
    localHashMap1.put("IBM-420", "CP420");
    localHashMap1.put("IBM-424", "CP424");
    localHashMap1.put("IBM-437", "CP437");
    localHashMap1.put("IBM-500", "CP500");
    localHashMap1.put("IBM-775", "CP775");
    localHashMap1.put("IBM-850", "CP850");
    localHashMap1.put("IBM-852", "CP852");
    localHashMap1.put("IBM-855", "CP855");
    localHashMap1.put("IBM-857", "CP857");
    localHashMap1.put("IBM-858", "CP858");
    localHashMap1.put("IBM-860", "CP860");
    localHashMap1.put("IBM-861", "CP861");
    localHashMap1.put("IBM-862", "CP862");
    localHashMap1.put("IBM-863", "CP863");
    localHashMap1.put("IBM-864", "CP864");
    localHashMap1.put("IBM-865", "CP865");
    localHashMap1.put("IBM-866", "CP866");
    localHashMap1.put("IBM-868", "CP868");
    localHashMap1.put("IBM-869", "CP869");
    localHashMap1.put("IBM-870", "CP870");
    localHashMap1.put("IBM-871", "CP871");
    localHashMap1.put("IBM-918", "CP918");
    localHashMap1.put("IBM-924", "CP924");
    localHashMap1.put("IBM-1026", "CP1026");
    localHashMap1.put("IBM-1140", "Cp1140");
    localHashMap1.put("IBM-1141", "Cp1141");
    localHashMap1.put("IBM-1142", "Cp1142");
    localHashMap1.put("IBM-1143", "Cp1143");
    localHashMap1.put("IBM-1144", "Cp1144");
    localHashMap1.put("IBM-1145", "Cp1145");
    localHashMap1.put("IBM-1146", "Cp1146");
    localHashMap1.put("IBM-1147", "Cp1147");
    localHashMap1.put("IBM-1148", "Cp1148");
    localHashMap1.put("IBM-1149", "Cp1149");
    localHashMap1.put("IBM-819", "ISO8859_1");
    localHashMap1.put("IBM-367", "ASCII");
    fIANA2JavaMap = Collections.unmodifiableMap(localHashMap1);
    localHashMap2.put("ISO8859_1", "ISO-8859-1");
    localHashMap2.put("ISO8859_2", "ISO-8859-2");
    localHashMap2.put("ISO8859_3", "ISO-8859-3");
    localHashMap2.put("ISO8859_4", "ISO-8859-4");
    localHashMap2.put("ISO8859_5", "ISO-8859-5");
    localHashMap2.put("ISO8859_6", "ISO-8859-6");
    localHashMap2.put("ISO8859_7", "ISO-8859-7");
    localHashMap2.put("ISO8859_8", "ISO-8859-8");
    localHashMap2.put("ISO8859_9", "ISO-8859-9");
    localHashMap2.put("ISO8859_13", "ISO-8859-13");
    localHashMap2.put("ISO8859_15", "ISO-8859-15");
    localHashMap2.put("ISO8859_15_FDIS", "ISO-8859-15");
    localHashMap2.put("Big5", "BIG5");
    localHashMap2.put("CP037", "EBCDIC-CP-US");
    localHashMap2.put("CP273", "IBM273");
    localHashMap2.put("CP277", "EBCDIC-CP-DK");
    localHashMap2.put("CP278", "EBCDIC-CP-FI");
    localHashMap2.put("CP280", "EBCDIC-CP-IT");
    localHashMap2.put("CP284", "EBCDIC-CP-ES");
    localHashMap2.put("CP285", "EBCDIC-CP-GB");
    localHashMap2.put("CP290", "EBCDIC-JP-KANA");
    localHashMap2.put("CP297", "EBCDIC-CP-FR");
    localHashMap2.put("CP420", "EBCDIC-CP-AR1");
    localHashMap2.put("CP424", "EBCDIC-CP-HE");
    localHashMap2.put("CP437", "IBM437");
    localHashMap2.put("CP500", "EBCDIC-CP-CH");
    localHashMap2.put("CP775", "IBM775");
    localHashMap2.put("CP850", "IBM850");
    localHashMap2.put("CP852", "IBM852");
    localHashMap2.put("CP855", "IBM855");
    localHashMap2.put("CP857", "IBM857");
    localHashMap2.put("CP858", "IBM00858");
    localHashMap2.put("CP860", "IBM860");
    localHashMap2.put("CP861", "IBM861");
    localHashMap2.put("CP862", "IBM862");
    localHashMap2.put("CP863", "IBM863");
    localHashMap2.put("CP864", "IBM864");
    localHashMap2.put("CP865", "IBM865");
    localHashMap2.put("CP866", "IBM866");
    localHashMap2.put("CP868", "IBM868");
    localHashMap2.put("CP869", "IBM869");
    localHashMap2.put("CP870", "EBCDIC-CP-ROECE");
    localHashMap2.put("CP871", "EBCDIC-CP-IS");
    localHashMap2.put("CP918", "EBCDIC-CP-AR2");
    localHashMap2.put("CP924", "IBM00924");
    localHashMap2.put("CP1026", "IBM1026");
    localHashMap2.put("CP1140", "IBM01140");
    localHashMap2.put("CP1141", "IBM01141");
    localHashMap2.put("CP1142", "IBM01142");
    localHashMap2.put("CP1143", "IBM01143");
    localHashMap2.put("CP1144", "IBM01144");
    localHashMap2.put("CP1145", "IBM01145");
    localHashMap2.put("CP1146", "IBM01146");
    localHashMap2.put("CP1147", "IBM01147");
    localHashMap2.put("CP1148", "IBM01148");
    localHashMap2.put("CP1149", "IBM01149");
    localHashMap2.put("EUCJIS", "EUC-JP");
    localHashMap2.put("KS_C_5601-1987", "KS_C_5601-1987");
    localHashMap2.put("GB2312", "GB2312");
    localHashMap2.put("ISO2022KR", "ISO-2022-KR");
    localHashMap2.put("ISO2022CN", "ISO-2022-CN");
    localHashMap2.put("JIS", "ISO-2022-JP");
    localHashMap2.put("KOI8_R", "KOI8-R");
    localHashMap2.put("KSC5601", "EUC-KR");
    localHashMap2.put("GB18030", "GB18030");
    localHashMap2.put("GBK", "GBK");
    localHashMap2.put("SJIS", "SHIFT_JIS");
    localHashMap2.put("MS932", "WINDOWS-31J");
    localHashMap2.put("UTF8", "UTF-8");
    localHashMap2.put("Unicode", "UTF-16");
    localHashMap2.put("UnicodeBig", "UTF-16BE");
    localHashMap2.put("UnicodeLittle", "UTF-16LE");
    localHashMap2.put("JIS0201", "X0201");
    localHashMap2.put("JIS0208", "X0208");
    localHashMap2.put("JIS0212", "ISO-IR-159");
    localHashMap2.put("CP1047", "IBM1047");
    fJava2IANAMap = Collections.unmodifiableMap(localHashMap1);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\util\EncodingMap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */