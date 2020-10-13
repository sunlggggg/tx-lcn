package com.codingapi.txlcn.protocol.util;

import cn.hyperchain.sdk.account.*;
import cn.hyperchain.sdk.common.utils.ByteUtil;
import cn.hyperchain.sdk.crypto.HashUtil;
import cn.hyperchain.sdk.crypto.ecdsa.ECKey;
import cn.hyperchain.sdk.crypto.sm.sm2.SM2Util;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.engines.SM2Engine;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.math.ec.ECPoint;
import org.springframework.util.Base64Utils;
import org.springframework.util.StringUtils;

import java.math.BigInteger;
import java.security.SecureRandom;

/**
 * @author sunligang
 */
@Slf4j
public class AccountUtil {

    public static String randomPassword(int len) {
        StringBuilder password = new StringBuilder();
        while (len-- > 0) {
            password.append((char) (int) (Math.random() * 26 + 65));
        }
        return password.toString();
    }


    public static Account genAccount(Algo algo) {
        return genAccount(algo, "");
    }


    public static Account genAccount(Algo algo, String password) {
        byte[] address;
        byte[] publicKey;
        byte[] privateKey;
        if (algo.isSM()) {
            AsymmetricCipherKeyPair keyPair = SM2Util.generateKeyPair();
            ECPrivateKeyParameters ecPriv = (ECPrivateKeyParameters) keyPair.getPrivate();
            ECPublicKeyParameters ecPub = (ECPublicKeyParameters) keyPair.getPublic();
            BigInteger privateKeyBI = ecPriv.getD();
            publicKey = ecPub.getQ().getEncoded(false);
            privateKey = Account.encodePrivateKey(ByteUtil.biConvert32Bytes(privateKeyBI), algo, password);
            address = HashUtil.sha3omit12(publicKey);
            return new SMAccount(ByteUtil.toHex(address), ByteUtil.toHex(publicKey), ByteUtil.toHex(privateKey), Version.V4, algo, keyPair);
        } else {
            ECKey ecKey = new ECKey(new SecureRandom());
            address = ecKey.getAddress();
            publicKey = ecKey.getPubKey();
            privateKey = Account.encodePrivateKey(ecKey.getPrivKeyBytes(), algo, "");
            return new ECAccount(ByteUtil.toHex(address), ByteUtil.toHex(publicKey), ByteUtil.toHex(privateKey), Version.V4, algo, ecKey);
        }
    }

    private static byte[] hexToByte(String hex) {
        int m, n;
        int byteLen = hex.length() / 2;
        byte[] ret = new byte[byteLen];
        for (int i = 0; i < byteLen; i++) {
            m = i * 2 + 1;
            n = m + 1;
            int intVal = Integer.decode("0x" + hex.substring(i * 2, m) + hex.substring(m, n));
            ret[i] = (byte) intVal;
        }
        return ret;
    }

    public static String getAddress(String publicKey) {
        return ByteUtil.toHex(HashUtil.sha3omit12(ByteUtil.fromHex(publicKey)));
    }

    /**
     * 倍点运算(SM2 国密算法)
     *
     * @param alicePri A私钥
     * @param alicePsw A私钥的密码
     * @param bobPub   B公钥
     * @return 双方协商密钥
     */
    public static String pointMultiply(String alicePri, String alicePsw, String bobPub) {
        BigInteger priBigInteger = getPriBigInteger(alicePri, Algo.SMRAW, alicePsw);
        ECPoint ecPoint = getECPoint(bobPub, Algo.SMRAW);
        return ByteUtil.toHex(ecPoint.multiply(priBigInteger).getEncoded(false));
    }

    private static BigInteger getPriBigInteger(String privateKey, Algo algo, String password) {
        byte[] bb = Account.decodePrivateKey(ByteUtil.fromHex(privateKey), algo, password);
        return ByteUtil.bytesToBigInteger(bb);
    }

    private static ECPoint getECPoint(String publicKey, Algo algo) {
        byte[] pubEncode = ByteUtil.fromHex(publicKey);
        if (algo.isSM()) {
            return SM2Util.CURVE.decodePoint(pubEncode);
        } else {
            return ECKey.CURVE.getCurve().decodePoint(pubEncode);
        }
    }

    public static byte[] encrypt(String publicKey, byte[] data) {
        ECPublicKeyParameters publicKeyParameters = new ECPublicKeyParameters(getECPoint(publicKey, Algo.SMRAW), SM2Util.DOMAIN_PARAMS);

        SM2Engine sm2Engine = new SM2Engine();
        sm2Engine.init(true, new ParametersWithRandom(publicKeyParameters, new SecureRandom()));

        try {
            return sm2Engine.processBlock(data, 0, data.length);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException("sm2加密异常", e);
        }
    }


    public static byte[] decrypt(String privateKey, String psw, byte[] dataByte) {
        BigInteger priBigInteger = getPriBigInteger(privateKey, Algo.SMRAW, psw);
        ECPrivateKeyParameters privateKeyParameters = new ECPrivateKeyParameters(priBigInteger, SM2Util.DOMAIN_PARAMS);

        SM2Engine sm2Engine = new SM2Engine();
        sm2Engine.init(false, privateKeyParameters);

        try {
            return sm2Engine.processBlock(dataByte, 0, dataByte.length);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException("sm2解密异常", e);
        }
    }

    public static String sign(String publicKey, String privateKey, String psw, byte[] source) throws CryptoException {
        ECPublicKeyParameters publicKeyParameters = new ECPublicKeyParameters(getECPoint(publicKey, Algo.SMRAW), SM2Util.DOMAIN_PARAMS);
        BigInteger priBigInteger = getPriBigInteger(privateKey, Algo.SMRAW, psw);
        ECPrivateKeyParameters privateKeyParameters = new ECPrivateKeyParameters(priBigInteger, SM2Util.DOMAIN_PARAMS);
        AsymmetricCipherKeyPair pair = new AsymmetricCipherKeyPair(publicKeyParameters, privateKeyParameters);
        byte[] crypt = SM2Util.sign(pair, source);
        byte[] result = new byte[3 + source.length + crypt.length];
        result[0] = (byte) (source.length >> 16 & 0xFF);
        result[1] = (byte) (source.length >> 8 & 0xFF);
        result[2] = (byte) (source.length & 0xFF);
        System.arraycopy(source, 0, result, 3, source.length);
        System.arraycopy(crypt, 0, result, 3 + source.length, crypt.length);
        return Base64Utils.encodeToString(result);
    }

    public static boolean verify(String publicKey, String signStr) {
        if (StringUtils.isEmpty(signStr)) {
            return false;
        }
        byte[] bytes = Base64Utils.decodeFromString(signStr);
        int len = (bytes[0] << 16 & 0xFF0000) | (bytes[1] << 8 & 0xFF00) | (bytes[2] & 0xFF);
        byte[] source = new byte[len];
        byte[] crypt = new byte[bytes.length - 3 - source.length];
        System.arraycopy(bytes, 3, source, 0, source.length);
        System.arraycopy(bytes, 3 + source.length, crypt, 0, crypt.length);
        ECPublicKeyParameters publicKeyParameters = new ECPublicKeyParameters(getECPoint(publicKey, Algo.SMRAW), SM2Util.DOMAIN_PARAMS);
        return SM2Util.verify(source, crypt, publicKeyParameters);
    }

    public void testEnDECrypt() throws CryptoException {
        Account account = AccountUtil.genAccount(Algo.SMRAW, "123");
        String base64 = sign(account.getPublicKey(), account.getPrivateKey(), "123", "hello".getBytes());
        System.out.println(verify(account.getPublicKey(), base64));

//        Account account = AccountUtil.genAccount(Algo.SMRAW, "123");
//        SM2Util.sign()

//        byte[] sign = sign(account.getPrivateKey(), "123", "hello".getBytes());
//
//        String plain = new String(verifySign(account.getPublicKey(), sign));
//        System.out.println(plain);
    }
}
