package com.lynchj.utils.security.rsa;

import com.lynchj.enums.ErrorStatusEnum;
import com.lynchj.exception.LynchjException;

import javax.crypto.Cipher;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class RSA2SecurityUtils {

    private static final String ALGORITHM_RSA = "RSA";

    /** RSA-charset */
    private static final String RSA_CHARSET = "UTF-8";

    /** 签名类型 */
    private static final String SIGN_TYPE = "SHA256withRSA";

    private static final Integer KEY_LENGTH = 2048;

    private RSA2SecurityUtils() {
        throw new LynchjException(ErrorStatusEnum.CREATE_UTILS_ENTITY_ERROR);
    }

    /**
     * 生成2048公钥、私钥
     *
     * @return 返回公钥私钥的Map集合
     */
    public static Map<String, byte[]> generateKeyBytes() {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM_RSA);
            keyPairGenerator.initialize(KEY_LENGTH);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
            RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
            Map<String, byte[]> keyMap = new HashMap<>();
            keyMap.put("publicKey", publicKey.getEncoded());
            keyMap.put("privateKey", privateKey.getEncoded());
            return keyMap;
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 将字符串通过RSA算法公钥加密
     *
     * @param content 需要加密的内容
     * @param pubKey 公钥
     */
    private static String EncryptByRSAPubKey(String content, String pubKey) throws Exception {
        try {
            PublicKey publicKey = RSA2SecurityUtils.getRSAPubKey(pubKey);
            Cipher cipher = Cipher.getInstance(publicKey.getAlgorithm());
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            cipher.update(content.getBytes(RSA2SecurityUtils.RSA_CHARSET));
            return RSA2SecurityUtils.encodeBase64(cipher.doFinal());
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception();
        }
    }

    /**
     * 将字符串通过RSA算法公钥解密
     *
     * @param content 需要解密的内容
     * @param pubKey 公钥
     * @return 解密后字符串
     */
    private static String DecryptByRSAPubKey(String content, String pubKey) throws Exception {
        try {
            PublicKey publicKey = RSA2SecurityUtils.getRSAPubKey(pubKey);
            Cipher cipher = Cipher.getInstance(publicKey.getAlgorithm());
            cipher.init(Cipher.DECRYPT_MODE, publicKey);
            cipher.update(RSA2SecurityUtils.decodeBase64(content));
            return new String(cipher.doFinal(), RSA_CHARSET);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception();
        }
    }

    /**
     * 将字符串通过RSA算法私钥加密
     *
     * @param content 需要加密的内容
     * @param priKey 私钥
     * @return 加密后字符串
     */
    public static String EncryptByRSAPriKey(String content, String priKey) throws Exception {
        try {
            PrivateKey privateKey = RSA2SecurityUtils.getRSAPriKey(priKey);
            Cipher cipher = Cipher.getInstance(privateKey.getAlgorithm());
            cipher.init(Cipher.ENCRYPT_MODE, privateKey);
            cipher.update(content.getBytes(RSA2SecurityUtils.RSA_CHARSET));
            return RSA2SecurityUtils.encodeBase64(cipher.doFinal());
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception();
        }
    }

    /**
     * 将字符串通过RSA算法私钥解密
     *
     * @param content 需要解密的内容
     * @param priKey 私钥
     * @return 解密后字符串
     */
    public static String DecryptByRSAPriKey(String content, String priKey) throws Exception {
        try {
            PrivateKey privateKey = RSA2SecurityUtils.getRSAPriKey(priKey);
            Cipher cipher = Cipher.getInstance(privateKey.getAlgorithm());
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            cipher.update(RSA2SecurityUtils.decodeBase64(content));
            return new String(cipher.doFinal(), RSA2SecurityUtils.RSA_CHARSET);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception();
        }
    }

    /**
     * 使用私钥对数据进行加密签名
     *
     * @param data 数据
     * @param privateKeyString 私钥
     * @return 加密后的签名
     */
    public static String sign(String data, String privateKeyString) throws Exception {
        KeyFactory keyf = KeyFactory.getInstance(ALGORITHM_RSA);
        PrivateKey privateKey = keyf.generatePrivate(new PKCS8EncodedKeySpec(decodeBase64(privateKeyString)));
        Signature signet = Signature.getInstance(SIGN_TYPE);
        signet.initSign(privateKey);
        signet.update(data.getBytes(RSA_CHARSET));
        byte[] signed = signet.sign();
        return encodeBase64(signed);
    }

    /**
     * 使用公钥判断签名是否与数据匹配
     *
     * @param data 数据
     * @param sign 签名
     * @param publicKeyString 公钥
     * @return 验证签名是否通过
     */
    public static boolean verify(String data, String sign, String publicKeyString) throws Exception {
        KeyFactory keyf = KeyFactory.getInstance(ALGORITHM_RSA);
        PublicKey publicKey = keyf.generatePublic(new X509EncodedKeySpec(decodeBase64(publicKeyString)));
        Signature signet = Signature.getInstance(SIGN_TYPE);
        signet.initVerify(publicKey);
        signet.update(data.getBytes(RSA_CHARSET));
        return signet.verify(decodeBase64(sign));
    }

    /**
     * 获取RSA公钥
     *
     * @param pubKey 公钥
     */
    private static PublicKey getRSAPubKey(String pubKey) throws Exception {
        try {
            X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(RSA2SecurityUtils.decodeBase64(pubKey));
            KeyFactory keyFactory = KeyFactory.getInstance(RSA2SecurityUtils.ALGORITHM_RSA);
            return keyFactory.generatePublic(publicKeySpec);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception();
        }
    }

    /**
     * 获取RSA私钥
     *
     * @param priKey 私钥
     */
    private static PrivateKey getRSAPriKey(String priKey) throws Exception {
        try {
            PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(RSA2SecurityUtils.decodeBase64(priKey));
            KeyFactory keyFactory = KeyFactory.getInstance(RSA2SecurityUtils.ALGORITHM_RSA);
            return keyFactory.generatePrivate(privateKeySpec);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception();
        }
    }

    /**
     * base64编码
     */
    public static String encodeBase64(byte[] source) throws Exception {
        Base64.Encoder encoder = Base64.getEncoder();
        return new String(encoder.encode(source), RSA_CHARSET);
    }

    /**
     * Base64解码
     */
    public static byte[] decodeBase64(String target) throws Exception {
        Base64.Decoder decoder = Base64.getDecoder();
        return decoder.decode(target.getBytes(RSA_CHARSET));
    }

    public static void main(String[] args) throws Exception {
        /*String pubKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAjiSU84l+zIatmoapB04fosxWmB+acQQnvk19o3r9dguDviNMGp9N0p6db4NY/hIH/MH2GsEsVnqU4nOBpuXC5yBbiJPevDRnqGBQ2ObyZprptfz6TR52xGLoq8kEEMdlVX1kk43115A7xqZb/0PL2o0dQf3vP0k3EalvBKZaspfEUp7mlnBj1433kf5Qr0I3O3abzbNVCwsTkSCWjE/sbEISYMMrjiMuMXWknG0lndsn55uKs1URm+b38aGxKB5stbpAZtVn2sMrRyMpx/nZM07ErJjsR6Gb2Hb2dc+PoHiAry/Uewc8RFc/u/0mOQEfqEiItRwhkuJB8GGU+CecLQIDAQAB";
        String priKey = "MIIEvwIBADANBgkqhkiG9w0BAQEFAASCBKkwggSlAgEAAoIBAQCOJJTziX7Mhq2ahqkHTh+izFaYH5pxBCe+TX2jev12C4O+I0wan03Snp1vg1j+Egf8wfYawSxWepTic4Gm5cLnIFuIk968NGeoYFDY5vJmmum1/PpNHnbEYuiryQQQx2VVfWSTjfXXkDvGplv/Q8vajR1B/e8/STcRqW8Eplqyl8RSnuaWcGPXjfeR/lCvQjc7dpvNs1ULCxORIJaMT+xsQhJgwyuOIy4xdaScbSWd2yfnm4qzVRGb5vfxobEoHmy1ukBm1WfawytHIynH+dkzTsSsmOxHoZvYdvZ1z4+geICvL9R7BzxEVz+7/SY5AR+oSIi1HCGS4kHwYZT4J5wtAgMBAAECggEAfr5lxqZSVwK7566tJ2Nk2CAGEs0TRDTlT03/encfbFOmHDOTF6X2mZhvX1CqWJoxXxKAZKjc5RuWiDlgYTY8TSTQGX9ouz6rMzr2vAFqIU1+mZTCh2NjmYdsWkj6hA+X7tvJg7yweo8UjfQm0f1c1zq+3OoVm8OoA+qloGrJMra8UpWS5sgRXuqcCrk/96Kb0eJCqtcDhcQqjvzWWlOZOgigWYbWlZ/drMSb0hkeSdpiFp7UBqJkVnb6sN4Hb6jFlSAP0qMs+y3lUOOjbTzv7Z0ib+YVHogOyHl/C4W2iidZtDESSNf/q8iSgahmJEvP57Fzpqldq12J7NMwxXfsrQKBgQD0fdzPEXKG0qlw/BrcSBgfBgfA0ssK9kXyVB82VZTNpeXj4jRsWNyVDze5cToOZ2pmB99fG2LAEos3lDyadvDJvymE3FIlBHwbfFKRWteECJXgX16C1HW2WBzoEgXfZxEkrNwd6689jg1PY/kglNnubhqD5Mo0reO4wbuvkP8FVwKBgQCU1WlPCSAD6orWi+mDwhXKwReWdJXJ8JGOy/UbrzdGDfkzUSMEvOhGL2em3EVua8zEaRGUY4CZCTbyoFJFlvwRIbD+L/HCYu16ERHXae4fKrnxZmt3xtkchHAVvH22TitNyET5XOgQSep2dMjlO8bU5mVKKB6iOIa0+s9HXhrUGwKBgQCodGwGVAwn7tAGY/2R1y6feYUwC00QwXySKfnTcwXy56JM8FeUa2mYXoLTh7srn3+Ey1nRasoQcvZvFfjnqJnhN5BA1ZjXno5wVamCMygY9d1mWe+BjyeHupA4KerX8Wizb1K+XtxAOjbbg5NsKoM5r60fQS09PK6RZxKNNgcwQwKBgQCGtdsJVGML6GAd7KlBPoxm5fqpjYB+NSYqL2T818yZtdAWqJufkDRUQlf46WpWlW/TXp4wxxcAbRy04nNU1WH1R59hkpepr8zuLUEOzqxevVdrrZ4b3XlpnF2u7tqCBaKgVmJHqdXJ2H1FFlM3WLGQxbBsPH/tqGgUquat2/lAfQKBgQDbSsiSwcduSMwZk2MxGtX8da5iIkzMYY6mv45f243c1TKnMBzG2JKHjhahK8Ux0aUFevdkFLoJE1tog0LC/a/84mnR+jSRWoJFkqOpur2nCA6w7H7Giw3UrEewprLHzmkF30EOwdUuo0upcW3gjEcSzCeSeTk8Mkn7fInJ/jmz/g==";
        String content = "a=123";

        String s = EncryptByRSAPubKey(content, pubKey);
        System.out.println("公钥加密后：" + s);
        System.out.println("私钥解密后：" + decryptByRSAPriKey(s, priKey));

        String sign = sign(content, priKey);
        System.err.println("私钥签名后：" + sign);
        System.err.println("私钥验证结果：" + verify(content, sign, pubKey));*/


        /*try {
            KeyPairGenerator gen = KeyPairGenerator.getInstance(ALGORITHM_RSA);
            gen.initialize(KEY_LENGTH);
            KeyPair pair = gen.generateKeyPair();
            //rsa生成一对公私钥
            PublicKey publicKey  = getRSAPubKey(pubKey);
            PrivateKey privateKey  = getRSAPriKey(priKey);
            //SHA1withRSA算法进行签名
            Signature sign = Signature.getInstance(SIGN_TYPE);
            sign.initSign(privateKey);
            byte[] data = "a=123".getBytes(RSA_CHARSET);
            //更新用于签名的数据
            sign.update(data);
            byte[] signature = sign.sign();
            System.err.println("签名的信息：" + encodeBase64(signature));
            Signature verifySign = Signature.getInstance(SIGN_TYPE);
            verifySign.initVerify(publicKey);
            //用于验签的数据
            verifySign.update(data);
            boolean flag = verifySign.verify(signature);
            System.out.println(flag);

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }*/

        Map<String, byte[]> keyMap = generateKeyBytes();
        for (Map.Entry<String, byte[]> entry : keyMap.entrySet()) {
            System.err.println(entry.getKey() + "=======" + encodeBase64(entry.getValue()));
        }

    }

}
