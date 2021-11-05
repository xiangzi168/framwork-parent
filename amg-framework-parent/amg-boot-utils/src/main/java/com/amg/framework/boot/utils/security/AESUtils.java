package com.amg.framework.boot.utils.security;

import com.amg.framework.boot.utils.security.Base64Util;
import org.springframework.util.Base64Utils;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;



public class AESUtils{

    private static final String KEY_ALGORITHM = "AES";
    private static final String DEFAULT_CIPHER_ALGORITHM = "AES/ECB/PKCS5Padding";//默认的加密算法

    /**
     * AES 加密操作
     *
     * @param content 待加密内容
     * @param key 加密密钥
     * @return 返回Base64转码后的加密数据
     */
    public static String encrypt(String content, String key) {
        try {
            Cipher cipher = Cipher.getInstance(DEFAULT_CIPHER_ALGORITHM);// 创建密码器

            byte[] byteContent = content.getBytes("utf-8");

            cipher.init(Cipher.ENCRYPT_MODE, getSecretKey(key));// 初始化为加密模式的密码器

            byte[] result = cipher.doFinal(byteContent);// 加密

            return Base64Utils.encodeToString(result);//通过Base64转码返回

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    /**
     * AES 解密操作
     *
     * @param content
     * @param key
     * @return
     */
    public static String decrypt(String content, String key) {

        try {
            //实例化
            Cipher cipher = Cipher.getInstance(DEFAULT_CIPHER_ALGORITHM);

            //使用密钥初始化，设置为解密模式
            cipher.init(Cipher.DECRYPT_MODE, getSecretKey(key));

            //执行操作
            byte[] result = cipher.doFinal(Base64Utils.decodeFromString(content));


            return new String(result, "utf-8");
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    /**
     * 生成加密秘钥
     *
     * @return
     */
    private static SecretKeySpec getSecretKey(final String key) {
        //返回生成指定算法密钥生成器的 KeyGenerator 对象
        KeyGenerator kg = null;

        try {
            kg = KeyGenerator.getInstance(KEY_ALGORITHM);

            //AES 要求密钥长度为 128
            kg.init(128, new SecureRandom(key.getBytes()));

            //生成一个密钥
            SecretKey secretKey = kg.generateKey();

            return new SecretKeySpec(secretKey.getEncoded(), KEY_ALGORITHM);// 转换为AES专用密钥
        } catch (NoSuchAlgorithmException ex) {
            ex.printStackTrace();
        }

        return null;
    }

    public static void main(String[] args) {
        String content = "%C2%AC%C3%AD%00%05sr%00Ccom.amg.fulfillment.cloud.basic.common.filehandle.dto.FileHandleDTO%C3%9D%C2%814%18%C3%AE%C2%A7%C2%80%C3%97%02%00%0CJ%00%08handleIdI%00%0BhandleState%5B%00%04argst%00%13%5BLjava%2Flang%2FObject%3BL%00%06belongt%00%12Ljava%2Flang%2FString%3BL%00%08fileNameq%00%7E%00%02L%00%0EhandleErrorMsgq%00%7E%00%02L%00%0AhandleTypeq%00%7E%00%02L%00%0BinternalUrlq%00%7E%00%02L%00%0AmethodNameq%00%7E%00%02%5B%00%0EparameterTypest%00%12%5BLjava%2Flang%2FClass%3BL%00%09publicUrlq%00%7E%00%02L%00%06targett%00%11Ljava%2Flang%2FClass%3Bxp%13Q%40%18%27%C2%AD%C2%B0%00%00%00%00%00ur%00%13%5BLjava.lang.Object%3B%C2%90%C3%8EX%C2%9F%10s%29l%02%00%00xp%00%00%00%02t%00%06999000sr%009com.amg.fulfillment.cloud.basic.entity.FileHandleResultDO%00%00%00%00%00%00%00%01%02%00%0AL%00%06belongq%00%7E%00%02L%00%0AcreateTimet%00%10Ljava%2Futil%2FDate%3BL%00%0FfileDownloadUrlq%00%7E%00%02L%00%08fileNameq%00%7E%00%02L%00%0EhandleErrorMsgq%00%7E%00%02L%00%0AhandleTypeq%00%7E%00%02L%00%02idt%00%10Ljava%2Flang%2FLong%3BL%00%05statet%00%13Ljava%2Flang%2FInteger%3BL%00%0AupdateTimeq%00%7E%00%0AL%00%08userNameq%00%7E%00%02xpt%00%06stringsr%00%0Ejava.util.Datehj%C2%81%01KYt%19%03%00%00xpw%08%00%00%01yYp%C2%8A%C3%8Dxt%00%06stringt%00%06stringt%00%06stringt%00%06stringsr%00%0Ejava.lang.Long%3B%C2%8B%C3%A4%C2%90%C3%8C%C2%8F%23%C3%9F%02%00%01J%00%05valuexr%00%10java.lang.Number%C2%86%C2%AC%C2%95%1D%0B%C2%94%C3%A0%C2%8B%02%00%00xp%00%00%00%00%00%00%00%00sr%00%11java.lang.Integer%12%C3%A2%C2%A0%C2%A4%C3%B7%C2%81%C2%878%02%00%01I%00%05valuexq%00%7E%00%16%00%00%00%00sq%00%7E%00%0Fw%08%00%00%01yYp%C2%8A%C3%8Dxt%00%06stringt%00%05basict%00%06999000pt%00%08downloadpt%00%05test1ur%00%12%5BLjava.lang.Class%3B%C2%AB%16%C3%97%C2%AE%C3%8B%C3%8DZ%C2%99%02%00%00xp%00%00%00%02vr%00%10java.lang.String%C2%A0%C3%B0%C2%A48z%3B%C2%B3B%02%00%00xpvq%00%7E%00%09pvr%00%3Acom.amg.fulfillment.cloud.basic.controller.BasicController%00%00%00%00%00%00%00%00%00%00%00xp";
        String key = "sde@5f98H*^hsff%dfs$r344&df8543*er";
        System.out.println("content:" + content);
        String s1 = AESUtils.encrypt(content, key);
        System.out.println("s1:" + s1);
        System.out.println("s2:"+AESUtils.decrypt(s1, key));

    }

}