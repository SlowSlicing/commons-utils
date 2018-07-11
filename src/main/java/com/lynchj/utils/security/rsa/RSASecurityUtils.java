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

public class RSASecurityUtils {

    private static final String ALGORITHM_RSA = "RSA";

    /** RSA-charset */
    private static final String RSA_CHARSET = "UTF-8";

    /** 签名类型 */
    private static final String SIGN_TYPE = "SHA1withRSA";

    private static final Integer KEY_LENGTH = 1024;

    private RSASecurityUtils() {
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
    private static String encryptByRSAPubKey(String content, String pubKey) throws Exception {
        try {
            PublicKey publicKey = RSASecurityUtils.getRSAPubKey(pubKey);
            Cipher cipher = Cipher.getInstance(publicKey.getAlgorithm());
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            cipher.update(content.getBytes(RSASecurityUtils.RSA_CHARSET));
            return RSASecurityUtils.encodeBase64(cipher.doFinal());
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
            PublicKey publicKey = RSASecurityUtils.getRSAPubKey(pubKey);
            Cipher cipher = Cipher.getInstance(publicKey.getAlgorithm());
            cipher.init(Cipher.DECRYPT_MODE, publicKey);
            cipher.update(RSASecurityUtils.decodeBase64(content));
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
            PrivateKey privateKey = RSASecurityUtils.getRSAPriKey(priKey);
            Cipher cipher = Cipher.getInstance(privateKey.getAlgorithm());
            cipher.init(Cipher.ENCRYPT_MODE, privateKey);
            cipher.update(content.getBytes(RSASecurityUtils.RSA_CHARSET));
            return RSASecurityUtils.encodeBase64(cipher.doFinal());
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
    public static String decryptByRSAPriKey(String content, String priKey) throws Exception {
        try {
            PrivateKey privateKey = RSASecurityUtils.getRSAPriKey(priKey);
            Cipher cipher = Cipher.getInstance(privateKey.getAlgorithm());
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            cipher.update(RSASecurityUtils.decodeBase64(content));
            return new String(cipher.doFinal(), RSASecurityUtils.RSA_CHARSET);
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
            X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(RSASecurityUtils.decodeBase64(pubKey));
            KeyFactory keyFactory = KeyFactory.getInstance(RSASecurityUtils.ALGORITHM_RSA);
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
            PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(RSASecurityUtils.decodeBase64(priKey));
            KeyFactory keyFactory = KeyFactory.getInstance(RSASecurityUtils.ALGORITHM_RSA);
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
        /*String pubKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAg9vtCITGfC6IcIWTE2EhF07kTqsy/JNdHJAU8RjS/a8TmLWWGH1vHpX+VVZKyUVS/iVZI5EUZyrZSjCeJ28wgPxCfCBzE8e6LpEE0EDg8Yb2PdiU72msykDK8FLHcjQmxAUpkZkoB3Ap+H3xAxNoTqTIrQZ59sGCiGz7sw7HJXicM5qvjID5gbjRA2fLb+ClermvOPdlrnWjGpjLaYft0pcSA4KRNvyHeLacWLd9IDIb523msImvkMhwG+Z7wof3Z/FFi3XcJQ7kG3+CsjU/oGnea7w92UjOjJlv0E6Z7DGwW6jeA7X4M4am0Q+3UATEsbv6uLtdV9+9oq86+SxyVQIDAQAB";
        String priKey = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCD2+0IhMZ8LohwhZMTYSEXTuROqzL8k10ckBTxGNL9rxOYtZYYfW8elf5VVkrJRVL+JVkjkRRnKtlKMJ4nbzCA/EJ8IHMTx7oukQTQQODxhvY92JTvaazKQMrwUsdyNCbEBSmRmSgHcCn4ffEDE2hOpMitBnn2wYKIbPuzDscleJwzmq+MgPmBuNEDZ8tv4KV6ua8492WudaMamMtph+3SlxIDgpE2/Id4tpxYt30gMhvnbeawia+QyHAb5nvCh/dn8UWLddwlDuQbf4KyNT+gad5rvD3ZSM6MmW/QTpnsMbBbqN4DtfgzhqbRD7dQBMSxu/q4u11X372irzr5LHJVAgMBAAECggEAOQaJCZEaoiQhv8AU7mGoRQNKe7dQkqlMsIijde2NekcYS4fkxOyifPDTkKaEK21+ygju9xHMaB3A4XQLQfS1XmM6gaIrApfzLiZrlYEph2sd3XtoVe422zWQTsUOGpbxWP7+jkhVZnocvKoC7JidGL3tR98wKgZgZI31gOfYOpzAeTEkbzYL2Cf5XmxKhaBkQ3N3iTxQZ6MdLnyvGqCOmYma0zK2MngpSPt0KLrBmC3j1sI5uMf7qyawjzi9UCd3aZ8aIvukCI1GDCCz0eeHBns0T2y7HKg34i5SQCbecv3cWbc34ZESkfWtlMtqtdBhMySHrGAIqqSvGbojG7Ez9QKBgQDaBHPUEnz6crxXaEZbU4wOjUzFYPryyQAtrhJgmozDZUnVbjI0DcgRrrOm+YEofk2B5tHUEZ4FH8rsSCGluH72ZevTmVt5bXuRrwv2dy6be8DdD9zeFbeVdZT4mOwr68IHJq7mmhkL8BoMtMwpUp7KSTSftSen234/RzoakJ4tvwKBgQCa1NL4MiRXFav1Bw9Br5MHvb8wIENkHGyXN4+3Dj7sX/NDdWOk5KWuXZdbQO2QbR0GkE/Dpu9PjItZs6gb2/IexvtpmWQEHKlGG+5zFqQcAnZPM6t4WurFw1UHdN2dVz3xz8rd+tMD/Z784eXSpD/l8hUuVR0IyKnqipgw7gyM6wKBgQDUdkBWrInFHCk1WTYva/TyfXsSYxdLoNweqCPapuKEzOGuMAyWs9OEf+ct0rqutp9b95AGhgCj576+kvDPakO4ZczzUeFWLX6dk6Qp1S3Dck+aXwoUF7/n1EWQGp8VklRs2aLasdO00ZDhTxQjNRPv+HVjKoxxxbJ7gjM6jjLISwKBgEWq99boMQPiY4KYj5vd1cnI7mtISSqVw6LzRD55MYUI05wKCTEcFRT1VfKgr4SqJEJc0xdVWR8xPU66kS0f9b01idEFTUSwZNaAIPY2PNfBn9yZglFgj303HF6DjmiK5fuVpVHm1+ZWy6A8QT5b28iq+i7j63GVYSeA/2Qc6RbLAoGAVESjyL2+0H14M/p05aAVCtDBBbNTf5Ikx4KEDmE2Ke+uFIjdHuPQOYSitHWIXFSE4cE6ZjCS32FehIpwgrIH1iV/MdR1hD4T8DAf64FDq8brrpAlihDFK/BTnV4jVhnBIaOM9bKahEeDpV1pWJNlhsKHDbVq0U0Eb1QNTKyhaQw=";
        String content = "a=123";

        String s = encryptByRSAPubKey(content, pubKey);
        System.out.println("公钥加密后：" + s);
        System.out.println("私钥解密后：" + decryptByRSAPriKey(s, priKey));

        String sign = sign(content, priKey);
        System.err.println("私钥签名后：" + sign);
        System.err.println("私钥验证结果：" + verify(content, sign, pubKey));*/

        /*
        try {
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

        /*Map<String, byte[]> keyMap = generateKeyBytes();
        for (Map.Entry<String, byte[]> entry : keyMap.entrySet()) {
            System.err.println(entry.getKey() + "=======" + encodeBase64(entry.getValue()));
        }*/

        String pubKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAg9vtCITGfC6IcIWTE2EhF07kTqsy/JNdHJAU8RjS/a8TmLWWGH1vHpX+VVZKyUVS/iVZI5EUZyrZSjCeJ28wgPxCfCBzE8e6LpEE0EDg8Yb2PdiU72msykDK8FLHcjQmxAUpkZkoB3Ap+H3xAxNoTqTIrQZ59sGCiGz7sw7HJXicM5qvjID5gbjRA2fLb+ClermvOPdlrnWjGpjLaYft0pcSA4KRNvyHeLacWLd9IDIb523msImvkMhwG+Z7wof3Z/FFi3XcJQ7kG3+CsjU/oGnea7w92UjOjJlv0E6Z7DGwW6jeA7X4M4am0Q+3UATEsbv6uLtdV9+9oq86+SxyVQIDAQAB";
        String priKey = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCD2+0IhMZ8LohwhZMTYSEXTuROqzL8k10ckBTxGNL9rxOYtZYYfW8elf5VVkrJRVL+JVkjkRRnKtlKMJ4nbzCA/EJ8IHMTx7oukQTQQODxhvY92JTvaazKQMrwUsdyNCbEBSmRmSgHcCn4ffEDE2hOpMitBnn2wYKIbPuzDscleJwzmq+MgPmBuNEDZ8tv4KV6ua8492WudaMamMtph+3SlxIDgpE2/Id4tpxYt30gMhvnbeawia+QyHAb5nvCh/dn8UWLddwlDuQbf4KyNT+gad5rvD3ZSM6MmW/QTpnsMbBbqN4DtfgzhqbRD7dQBMSxu/q4u11X372irzr5LHJVAgMBAAECggEAOQaJCZEaoiQhv8AU7mGoRQNKe7dQkqlMsIijde2NekcYS4fkxOyifPDTkKaEK21+ygju9xHMaB3A4XQLQfS1XmM6gaIrApfzLiZrlYEph2sd3XtoVe422zWQTsUOGpbxWP7+jkhVZnocvKoC7JidGL3tR98wKgZgZI31gOfYOpzAeTEkbzYL2Cf5XmxKhaBkQ3N3iTxQZ6MdLnyvGqCOmYma0zK2MngpSPt0KLrBmC3j1sI5uMf7qyawjzi9UCd3aZ8aIvukCI1GDCCz0eeHBns0T2y7HKg34i5SQCbecv3cWbc34ZESkfWtlMtqtdBhMySHrGAIqqSvGbojG7Ez9QKBgQDaBHPUEnz6crxXaEZbU4wOjUzFYPryyQAtrhJgmozDZUnVbjI0DcgRrrOm+YEofk2B5tHUEZ4FH8rsSCGluH72ZevTmVt5bXuRrwv2dy6be8DdD9zeFbeVdZT4mOwr68IHJq7mmhkL8BoMtMwpUp7KSTSftSen234/RzoakJ4tvwKBgQCa1NL4MiRXFav1Bw9Br5MHvb8wIENkHGyXN4+3Dj7sX/NDdWOk5KWuXZdbQO2QbR0GkE/Dpu9PjItZs6gb2/IexvtpmWQEHKlGG+5zFqQcAnZPM6t4WurFw1UHdN2dVz3xz8rd+tMD/Z784eXSpD/l8hUuVR0IyKnqipgw7gyM6wKBgQDUdkBWrInFHCk1WTYva/TyfXsSYxdLoNweqCPapuKEzOGuMAyWs9OEf+ct0rqutp9b95AGhgCj576+kvDPakO4ZczzUeFWLX6dk6Qp1S3Dck+aXwoUF7/n1EWQGp8VklRs2aLasdO00ZDhTxQjNRPv+HVjKoxxxbJ7gjM6jjLISwKBgEWq99boMQPiY4KYj5vd1cnI7mtISSqVw6LzRD55MYUI05wKCTEcFRT1VfKgr4SqJEJc0xdVWR8xPU66kS0f9b01idEFTUSwZNaAIPY2PNfBn9yZglFgj303HF6DjmiK5fuVpVHm1+ZWy6A8QT5b28iq+i7j63GVYSeA/2Qc6RbLAoGAVESjyL2+0H14M/p05aAVCtDBBbNTf5Ikx4KEDmE2Ke+uFIjdHuPQOYSitHWIXFSE4cE6ZjCS32FehIpwgrIH1iV/MdR1hD4T8DAf64FDq8brrpAlihDFK/BTnV4jVhnBIaOM9bKahEeDpV1pWJNlhsKHDbVq0U0Eb1QNTKyhaQw=";

        System.err.println(pubKey.getBytes("UTF-8").length);
        System.err.println(priKey.getBytes("UTF-8").length);

        String pubKey_2048 = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCjShKGLIj4YJcIDph/5PY7aeW30F3Aov1YPHnjEZYbvpStr855gYY7JOzyJUNiCi8G7dNseeU/+qocomiOOjFpHGN1aoRr4J9m28m7KlbrZPioXPDLuiqgHwft8GdBGQKP/RpPMKcU+JftIGWZ24UTC0aPsLyu8RU+ZlQOJaOIlwIDAQAB";
        String priKey_2048 = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAKNKEoYsiPhglwgOmH/k9jtp5bfQXcCi/Vg8eeMRlhu+lK2vznmBhjsk7PIlQ2IKLwbt02x55T/6qhyiaI46MWkcY3VqhGvgn2bbybsqVutk+Khc8Mu6KqAfB+3wZ0EZAo/9Gk8wpxT4l+0gZZnbhRMLRo+wvK7xFT5mVA4lo4iXAgMBAAECgYAf8HYtHnrwMlx4a/pyUcPEHVOcn7om2vq5DqM8wgzdgmqsio3VgHtUCEX8m0NR4vIN5ekL98/astohXbcb0tAlEgFP/dcaumOQuNHSMMIJHc9UQIxDzuS3H5EaKuYDogfA91oEsTghdciACupczA+SpADWUdwuxL7LoJB1vAq1WQJBANvjuBzwhQNNug3SMCEwL1tEhdJ909KhBZQeVa2L1dO21urNeq7Erm0Cx0Ms/HKqUzg+UtkXZWdSOxGcExiH7LUCQQC+GtlrEnFPJOtS9W0RkwQsheb93g/yEkfMwA/Nv09kJGvdLeUoa7QDEOhd+18dFF6oNMj6+6+6VqBe4cCwE7ubAkBIr94MsvVFQkxehTyju+nroZsbGb0Lw260p9Jqq+7jLW2d8I69dwaxwllcO2K4BNW9odyBJtq+bNBZ4d3uHgCBAkEArkOWglxJmi9RuJ5Z+P43mUi10om5PEIdp0d4NTbl4/qvFfV4V3FDdnwNRfmj2thNXQvmIIyPLdUy9Uckh0PPeQJAWcjOfapjBT4zBwi0NASdpk6WCMTYUSCln27Y01QbeP3rCuoWhBwZB4Zs4vlvxci6qhBs3eyMwqcNX8aBU078og==";

        System.err.println(pubKey_2048.getBytes("UTF-8").length);
        System.err.println(priKey_2048.getBytes("UTF-8").length);
    }

}
