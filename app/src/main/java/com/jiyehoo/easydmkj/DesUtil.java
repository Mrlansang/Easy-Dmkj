package com.jiyehoo.easydmkj;



import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @ClassName DesUtil
 * @Decription
 * @Author
 * @Date 20.9.9 10:59
 **/
public class DesUtil {
    private final static String key ="51434574";
    /**
     * 进行MD5加密
     *
     * @param s 要进行MD5转换的字符串
     * @return 该字符串的MD5值的8-24位
     */
    public static String getMD5(String s){
        char hexDigits[]={'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};

        try {
            byte[] btInput = s.getBytes();
            // 获得MD5摘要算法的 MessageDigest 对象
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            // 使用指定的字节更新摘要
            mdInst.update(btInput);
            // 获得密文
            byte[] md = mdInst.digest();
            // 把密文转换成十六进制的字符串形式
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str).substring(8,24);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    /**
     * key  不足8位补位
     * @param string
     */
    public static byte[] getKey(String keyRule) {
        byte[] keyByte = keyRule.getBytes();
        // 创建一个空的八位数组,默认情况下为0
        byte[] byteTemp = new byte[8];
        // 将用户指定的规则转换成八位数组
        for (int i = 0; i < byteTemp.length && i < keyByte.length; i++) {
            byteTemp[i] = keyByte[i];
        }
        return byteTemp;
    }
    /**
     * DES加密
     *
     * @param plaintext 明文
     * @param Key 密钥
     * @param EncryptMode AES加密模式，CBC或ECB
     * @return 该字符串的AES密文值
     */
    public static String DES_Encrypt(String plaintext, String Key,String EncryptMode) {
        String PlainText=null;
        try {
            PlainText=plaintext;
            if (Key == null) {
                return null;
            }
            //Key = getMD5(Key);
           // byte[] raw = Key.getBytes("utf-8");
            SecretKeySpec skeySpec = new SecretKeySpec(getKey(Key), "DES");
            Cipher cipher = Cipher.getInstance("DES/"+EncryptMode+"/PKCS5Padding");
            if(EncryptMode=="ECB") {
                cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
            }else {
                IvParameterSpec iv = new IvParameterSpec(Key.getBytes("utf-8"));//使用CBC模式，需要一个向量iv，可增加加密算法的强度
                cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
            }
            byte[] encrypted = cipher.doFinal(PlainText.getBytes("utf-8"));
            return bytesToHex(encrypted);
            //return new String(encrypted);//此处使用BASE64做转码功能，同时能起到2次加密的作用。
        } catch (Exception ex) {
            System.out.println(ex.toString());
            return null;
        }
    }


    /**
     * 字节数组转16进制
     * @param bytes 需要转换的byte数组
     * @return  转换后的Hex字符串
     */
    public static String bytesToHex(byte[] bytes) {
        StringBuffer sb = new StringBuffer();
        for(int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(bytes[i] & 0xFF);
            if(hex.length() < 2){
                sb.append(0);
            }
            sb.append(hex);
        }
        return sb.toString().toUpperCase();
    }
    public static String DES_EncryptECB(String plaintext){
        return DES_Encrypt(plaintext,key,"ECB");
    }

    public static byte[] DES_CBC_Encrypt(byte[] content, byte[] keyBytes) {
        try {
            DESKeySpec keySpec = new DESKeySpec(keyBytes);
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey key = keyFactory.generateSecret(keySpec);

            Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(keySpec.getKey()));
            byte[] result = cipher.doFinal(content);
            return result;
        } catch (Exception e) {
            System.out.println("exception:" + e.toString());
        }
        return null;
    }

    private static byte[] DES_CBC_Decrypt(byte[] content, byte[] keyBytes) {
        try {
            DESKeySpec keySpec = new DESKeySpec(keyBytes);
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey key = keyFactory.generateSecret(keySpec);

            Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(keyBytes));
            byte[] result = cipher.doFinal(content);
            return result;
        } catch (Exception e) {
            System.out.println("exception:" + e.toString());
        }
        return null;
    }

    private static String byteToHexString(byte[] bytes) {
        StringBuffer sb = new StringBuffer(bytes.length);
        String sTemp;
        for (int i = 0; i < bytes.length; i++) {
            sTemp = Integer.toHexString(0xFF & bytes[i]);
            if (sTemp.length() < 2)
                sb.append(0);
            sb.append(sTemp.toUpperCase());
        }
        return sb.toString();
    }

    public static String encrypt(String code){
        if(Objects.isNull(code)){
            return code;
        }
        return new String(DES_CBC_Encrypt(code.getBytes(),key.getBytes()));
    }


    public static String signtoken(String str){
        String strResult;
        try
        {
            // SHA 加密开始
            // 创建加密对象 并傳入加密類型
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-512");
            // 传入要加密的字符串
            messageDigest.update(str.getBytes());
            // 得到 byte 類型结果
            byte byteBuffer[] = messageDigest.digest();

            // 遍歷 byte buffer
            StringBuilder stringBuilder=new StringBuilder();
            for (int i = 0; i < byteBuffer.length; i++)
            {
                String hex = Integer.toHexString(0xff & byteBuffer[i]);
                if (hex.length() == 1)
                {
                    stringBuilder.append("0"+hex);
                   // System.out.print("0"+hex);
                    continue;
                }
                stringBuilder.append(hex);
             //   System.out.print(hex);
            }
            String update=stringBuilder.toString().substring(0,64);
          //  System.out.println();
           // System.out.println(update);
            StringBuilder result=new StringBuilder();
            for (int i = 1; i < update.length();i+=2 )
            {
                result.append(update.charAt(i));
            }
            // 得到返回結果
            strResult = result.toString();

        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
            return str;
        }
        return strResult;
    }
    public static String getHash3(String source, String hashType) {
        char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        StringBuilder sb = new StringBuilder();
        MessageDigest md5;
        try {
            md5 = MessageDigest.getInstance(hashType);
            md5.update(source.getBytes());
            byte[] encryptStr = md5.digest();
            for (int i = 0; i < encryptStr.length; i++) {
                int iRet = encryptStr[i];
                if (iRet < 0) {
                    iRet += 256;
                }
                int iD1 = iRet / 16;
                int iD2 = iRet % 16;
                sb.append(hexDigits[iD1] + "" + hexDigits[iD2]);
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }



    public static String encrypt_oracle(String key,String text){
       String iv = "9618913120112010";
        try {
            return encryptAES(text,key,iv);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    private static String add_to_16(String value){
        while (value.length() % 16 != 0){
            value += '\0';

        }
        return value;
    }
    private static String add_to_r(int block_size,String value){
        int i = block_size - value.length() % block_size;
        char c = (char) (block_size - value.length() % block_size);
        StringBuilder stringBuilder=new StringBuilder(value);
        for (int j = 0; j < i ; j++) {
            stringBuilder.append(c);
        }
        System.out.println("bb:");
        String string = stringBuilder.toString();
        System.out.println("aa:"+value);
        System.out.println("aa:"+string);
        return string;
    }

    /**
     * AES PKCS5Padding
     * @param sstr
     * @param skey
     * @param siv
     * @return
     * @throws Exception
     */
    public static String encryptAES(String sstr,String skey,String siv) throws Exception {
        if (skey==null){
            return null;
        }
        if (skey.length()!=16){
            System.out.println("key is not 16 of length");
        }
        byte[] keyByte = skey.getBytes("utf-8");
        SecretKeySpec keySpec = new SecretKeySpec(keyByte,"AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");//算法/模式/补码方式
        byte[] ivByte = siv.getBytes("utf-8");
        IvParameterSpec iv = new IvParameterSpec(ivByte);   //使用CBC模式，需要一个向量iv，可增加加密算法的强度
        cipher.init(Cipher.ENCRYPT_MODE,keySpec,iv);

        byte[] encrype = cipher.doFinal(add_to_r(16,sstr).getBytes());
        return Base64.getMimeEncoder().encodeToString(encrype);
    }
    /**
     * 解码 Unicode \\uXXXX
     * @param str
     * @return
     */
    public static String decodeUnicode(String str) {
        Charset set = Charset.forName("UTF-16");
        Pattern p = Pattern.compile("\\\\u([0-9a-fA-F]{4})");
        Matcher m = p.matcher( str );
        int start = 0 ;
        int start2 = 0 ;
        StringBuffer sb = new StringBuffer();
        while( m.find( start ) ) {
            start2 = m.start() ;
            if( start2 > start ){
                String seg = str.substring(start, start2) ;
                sb.append( seg );
            }
            String code = m.group( 1 );
            int i = Integer.valueOf( code , 16 );
            byte[] bb = new byte[ 4 ] ;
            bb[ 0 ] = (byte) ((i >> 8) & 0xFF );
            bb[ 1 ] = (byte) ( i & 0xFF ) ;
            ByteBuffer b = ByteBuffer.wrap(bb);
            sb.append( String.valueOf( set.decode(b) ).trim() );
            start = m.end() ;
        }
        start2 = str.length() ;
        if( start2 > start ){
            String seg = str.substring(start, start2) ;
            sb.append( seg );
        }
        return sb.toString() ;
    }

    public static String convertStringToHex(String str){

        char[] chars = str.toCharArray();

        StringBuffer hex = new StringBuffer();

        for(int i = 0; i < chars.length; i++){

            hex.append(Integer.toHexString((int)chars[i]));

        }

        return hex.toString();

    }
    public static void main(String[] args) {
        System.out.println(DES_EncryptECB("yy2020510"));
    }

}
