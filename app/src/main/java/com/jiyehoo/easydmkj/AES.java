package com.jiyehoo.easydmkj;

/**
 * @ClassName AES
 * @Decription
 * @Author
 * @Date 20.9.9 22:59
 **/

import android.os.Build;

import androidx.annotation.RequiresApi;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.util.Arrays;
import java.util.Base64;

/**
 *
 * @author
 * AES128 算法
 *
 * CBC 模式
 *
 * PKCS7Padding 填充模式
 *
 * CBC模式需要添加一个参数iv
 *
 * 介于java 不支持PKCS7Padding，只支持PKCS5Padding 但是PKCS7Padding 和 PKCS5Padding 没有什么区别
 * 要实现在java端用PKCS7Padding填充，需要用到bouncycastle组件来实现
 */
public class AES {
    // 算法名称
     final String KEY_ALGORITHM = "AES";
    // 加解密算法/模式/填充方式
     final String algorithmStr = "AES/CBC/PKCS7Padding";
    //
    private Key key;
    private Cipher cipher;
    boolean isInited = false;

   // byte[] iv = { 0x30, 0x31, 0x30, 0x32, 0x30, 0x33, 0x30, 0x34, 0x30, 0x35, 0x30, 0x36, 0x30, 0x37, 0x30, 0x38 };
    byte[] iv = "加密矢量".getBytes();
    public void init(byte[] keyBytes) {

        // 如果密钥不足16位，那么就补足.  这个if 中的内容很重要
        int base = 16;
        if (keyBytes.length % base != 0) {
            int groups = keyBytes.length / base + (keyBytes.length % base != 0 ? 1 : 0);
            byte[] temp = new byte[groups * base];
            Arrays.fill(temp, (byte) 0);
            System.arraycopy(keyBytes, 0, temp, 0, keyBytes.length);
            keyBytes = temp;
        }
        // 初始化
        Security.addProvider(new BouncyCastleProvider());
        // 转化成JAVA的密钥格式
        key = new SecretKeySpec(keyBytes, KEY_ALGORITHM);
        try {
            // 初始化cipher
            cipher = Cipher.getInstance(algorithmStr, "BC");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        }
    }
    /**
     * 加密方法
     *
     * @param content
     *            要加密的字符串
     * @param keyBytes
     *            加密密钥
     * @return
     */
    public byte[] encrypt(byte[] content, byte[] keyBytes) {
        byte[] encryptedText = null;
        init(keyBytes);
        try {
            cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(iv));
            encryptedText = cipher.doFinal(content);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return encryptedText;
    }

    public byte[] encrypt(String src,String keysrc,String iv){
        byte[] encryptedText = null;
        init(keysrc.getBytes());
       // System.out.println("IV：" + iv);
        try {
            cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(iv.getBytes()));
            encryptedText = cipher.doFinal(src.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return encryptedText;
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public String urlBase64encrypt(String src, String keysrc, String iv){
        byte[] encrypt = encrypt(src, keysrc, iv);
        String string = Base64.getMimeEncoder().encodeToString(encrypt);
        String result;
        try {
            result = URLEncoder.encode(string,"utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return string;
        }
        return result;
    }

    /**
     * 解密方法
     *
     * @param encryptedData
     *            要解密的字符串
     * @param keyBytes
     *            解密密钥
     * @return
     */
    public byte[] decrypt(byte[] encryptedData, byte[] keyBytes) {
        byte[] encryptedText = null;
        init(keyBytes);
       // System.out.println("IV：" + new String(iv));
        try {
            cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));
            encryptedText = cipher.doFinal(encryptedData);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return encryptedText;
    }
    public static void main(String[] args) {
        AES aes=new AES();
//   加解密 密钥
        byte[] keybytes = "加密的key".getBytes();
        String content = "{\"catalogId\":\"\",\"catalogId2\":\"\",\"endTime\":\"\",\"joinEndTime\":\"\",\"joinFlag\":\"\",\"joinStartTime\":\"\",\"keyword\":\"\",\"level\":\"\",\"page\":\"1\",\"signToken\":\"39a9cae0eb8833a61e87c393f68a6b17\",\"sort\":\"\",\"specialFlag\":\"\",\"startTime\":\"\",\"status\":\"\",\"token\":\"7129F15F304C25967CAF5A894F4444AE\",\"uid\":35069158,\"version\":\"4.2.6\"}";
        // 加密字符串
     //   System.out.println("加密前的：" + content);
      //  System.out.println("加密密钥：" + new String(keybytes));
        // 加密方法
        byte[] enc = aes.encrypt(content.getBytes(), keybytes);
       // System.out.println("加密后的内容：" + Base64.getMimeEncoder().encodeToString(enc));
      //  System.out.println(aes.urlBase64encrypt(content,"4T1JbdlgSM6h1urT","9618913120112010"));
        // 解密方法
        byte[] dec = aes.decrypt(enc, keybytes);
       // System.out.println("解密后的内容：" + new String(dec));
    }
}
