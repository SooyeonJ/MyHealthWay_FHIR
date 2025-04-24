package kr.co.iteyes.fhirmeta.utils;

import lombok.extern.slf4j.Slf4j;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;

@Slf4j
public class RsaUtils {
    private static final String ALGORITHM = "RSA";
    private static final String ENCRYPTION_ALGORITHM = "RSA/ECB/OAEPWithSHA1AndMGF1Padding";
    private static final String DECRYPTION_ALGORITHM = "RSA/ECB/OAEPPadding";
    private static final int KEY_SIZE = 2048;
    private static final int CIPER_BLOCK_SIZE = KEY_SIZE / 8;
    private static final int ENCRYPTION_BLOCK_SIZE = 214;
    private static final int DECRYPTION_BLOCK_SIZE = CIPER_BLOCK_SIZE;

    /**
     * RSA 기반 알고리즘의 공개키와 개인키를 생성하여 문자열로 변환
     *
     * @return 문자열의 공개키 개인키 Pair
     */
    public static HashMap<String, String> createKeyPair() {
        HashMap<String, String> keyPairMap = new HashMap<String, String>();
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM);
            keyPairGenerator.initialize(KEY_SIZE, new SecureRandom());

            KeyPair keyPair = keyPairGenerator.genKeyPair();
            PublicKey publicKey = keyPair.getPublic();
            PrivateKey privateKey = keyPair.getPrivate();

            String stringPubicKey = Base64.getEncoder().encodeToString(publicKey.getEncoded());
            String stringPrivateKey = Base64.getEncoder().encodeToString(privateKey.getEncoded());

            keyPairMap.put("publicKey", stringPubicKey);
            keyPairMap.put("privateKey", stringPrivateKey);
        } catch (NoSuchAlgorithmException ne) {
            log.error(
                    "의료 마이데이터 예외발생: [NoSuchAlgorithmException]Exception while processing MYMD createKeyPair : encode NoSuchAlgorithmException Occured : {}",
                    ne.getMessage());
        }
        return keyPairMap;
    }

    /**
     * 공개키를 이용하여 평문을 암호화
     *
     * @param plainData 암호화할 평문
     * @param stringPublicKey 공개키 문자열
     * @return encryptedData 암호화된 평문
     */
    public static String encrypt(final String plainData, final String stringPublicKey) {
        byte[] encryptedData = encrypt(plainData.getBytes(), stringPublicKey);

        return encryptedData != null ? Base64.getEncoder().encodeToString(encryptedData) : null;
    }

    /**
     * 공개키를 이용하여 평문 byte 배열을 암호화
     *
     * @param bytePlainData 암호화할 평문 byte 배열
     * @param stringPublicKey 공개키 문자열
     * @return encryptedData 암호화된 byte 배열
     */
    public static byte[] encrypt(final byte[] bytePlainData, final String stringPublicKey) {
        try {
            // 암호화
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
            byte[] bytePublicKey = Base64.getDecoder().decode(stringPublicKey.getBytes());

            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(bytePublicKey);
            PublicKey publicKey = keyFactory.generatePublic(keySpec);

            Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);

            int length = bytePlainData.length;
            final int encryptedLength = calculateLength(length, DECRYPTION_BLOCK_SIZE);
            ByteArrayOutputStream byteEncrytedStream = new ByteArrayOutputStream(encryptedLength);

            for (int offset = 0; length > 0; offset += ENCRYPTION_BLOCK_SIZE, length -= ENCRYPTION_BLOCK_SIZE) {
                int len = length > ENCRYPTION_BLOCK_SIZE ? ENCRYPTION_BLOCK_SIZE : length;
                byte[] encryted = cipher.doFinal(bytePlainData, offset, len);

                byteEncrytedStream.write(encryted);
            }
            byteEncrytedStream.flush();
            byteEncrytedStream.close();

            return byteEncrytedStream.toByteArray();
        } catch (NoSuchAlgorithmException e) {
            log.error(
                    "의료 마이데이터 예외발생: [NoSuchAlgorithmException]Exception while processing MYMD encryption : encode NoSuchAlgorithmException Occured : {}",
                    e.getMessage());
        } catch (InvalidKeySpecException e) {
            log.error(
                    "의료 마이데이터 예외발생: [InvalidKeySpecException]Exception while processing MYMD encryption : encode InvalidKeySpecException Occured : {}",
                    e.getMessage());
        } catch (NoSuchPaddingException e) {
            log.error(
                    "의료 마이데이터 예외발생: [NoSuchPaddingException]Exception while processing MYMD encryption : encode NoSuchPaddingException Occured : {}",
                    e.getMessage());
        } catch (IllegalBlockSizeException e) {
            log.error(
                    "의료 마이데이터 예외발생: [IllegalBlockSizeException]Exception while processing MYMD encryption : encode IllegalBlockSizeException Occured : {}",
                    e.getMessage());
        } catch (BadPaddingException e) {
            log.error(
                    "의료 마이데이터 예외발생: [BadPaddingException]Exception while processing MYMD encryption : encode BadPaddingException Occured : {}",
                    e.getMessage());
        } catch (InvalidKeyException e) {
            log.error(
                    "의료 마이데이터 예외발생: [InvalidKeyException]Exception while processing MYMD encryption : encode InvalidKeyException Occured : {}",
                    e.getMessage());
        } catch (IOException e) {
            log.error(
                    "의료 마이데이터 예외발생: [IOException]Exception while processing MYMD encryption : encode IOException Occured : {}",
                    e.getMessage());
        }

        return null;
    }

    /**
     * 암호화한 문자열을 개인키를 이용하여 평문으로 복호화(Base64 출력)
     *
     * @param encryptedData 암호화된 문자열
     * @param stringPrivateKey 개인키 문자열
     * @return 복호화된 평문 데이터(Base64 인코딩)
     */
    public static String decrypt(final String encryptedData, final String stringPrivateKey) {
        byte[] byteEncryptedData = Base64.getDecoder().decode(encryptedData.getBytes());
        byte[] decryptedData = decrypt(byteEncryptedData, stringPrivateKey);
        return decryptedData != null ? Base64.getEncoder().encodeToString(decryptedData) : null;
    }

    /**
     * 암호화한 문자열을 개인키를 이용하여 평문으로 복호화
     *
     * @param encryptedData 암호화된 문자열
     * @param stringPrivateKey 개인키 문자열
     * @param charset 출력 캐릭터셋
     * @return 복호화된 평문 데이터
     */
    public static String decrypt(final String encryptedData, final String stringPrivateKey, Charset charset) {
        byte[] byteEncryptedData = Base64.getDecoder().decode(encryptedData.getBytes());
        byte[] decryptedData = decrypt(byteEncryptedData, stringPrivateKey);
        return decryptedData != null ? new String(decryptedData, (charset == null ? StandardCharsets.UTF_8 : charset)) : null;
    }

    /**
     * 암호화한 byte 배열을 개인키를 이용하여 평문으로 복호화
     *
     * @param byteEncryptedData 암호화된 byte 배열
     * @param stringPrivateKey 개인키 문자열
     * @return 복호화된 평문 byte 배열
     */
    public static byte[] decrypt(final byte[] byteEncryptedData, final String stringPrivateKey) {
        try {
            // 문자열로 전달받은 개인키를 개인키 객체화
            byte[] bytePrivateKey = Base64.getDecoder().decode(stringPrivateKey.getBytes());
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(bytePrivateKey);

            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
            PrivateKey privateKey = keyFactory.generatePrivate(keySpec);

            // 만들어진 개인키 객체를 기반으로 암호화모드 설정
            Cipher cipher = Cipher.getInstance(DECRYPTION_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);

            // 암호화문을 평문으로 복호화
            int length = byteEncryptedData.length;
            final int decryptedLength = calculateLength(length, ENCRYPTION_BLOCK_SIZE);
            ByteArrayOutputStream byteDecrytedStream = new ByteArrayOutputStream(decryptedLength);

            for (int offset = 0; length > 0; offset += DECRYPTION_BLOCK_SIZE, length -= DECRYPTION_BLOCK_SIZE) {
                int len = length > DECRYPTION_BLOCK_SIZE ? DECRYPTION_BLOCK_SIZE : length;
                byte[] decrypted = cipher.doFinal(byteEncryptedData, offset, len);

                byteDecrytedStream.write(decrypted);
            }
            byteDecrytedStream.flush();
            byteDecrytedStream.close();

            return byteDecrytedStream.toByteArray();
        } catch (NoSuchAlgorithmException e) {
            log.error(
                    "의료 마이데이터 예외발생: [NoSuchAlgorithmException]Exception while processing MYMD encryption : decode NoSuchAlgorithmException Occured : {}",
                    e.getMessage());
        } catch (InvalidKeySpecException e) {
            log.error(
                    "의료 마이데이터 예외발생: [InvalidKeySpecException]Exception while processing MYMD encryption : decode InvalidKeySpecException Occured : {}",
                    e.getMessage());
        } catch (NoSuchPaddingException e) {
            log.error(
                    "의료 마이데이터 예외발생: [NoSuchPaddingException]Exception while processing MYMD encryption : decode NoSuchPaddingException Occured : {}",
                    e.getMessage());
        } catch (InvalidKeyException e) {
            log.error(
                    "의료 마이데이터 예외발생: [InvalidKeyException]Exception while processing MYMD encryption : decode InvalidKeyException Occured : {}",
                    e.getMessage());
        } catch (IllegalBlockSizeException e) {
            log.error(
                    "의료 마이데이터 예외발생: [IllegalBlockSizeException]Exception while processing MYMD encryption : decode IllegalBlockSizeException Occured : {}",
                    e.getMessage());
        } catch (BadPaddingException e) {
            log.error(
                    "의료 마이데이터 예외발생: [BadPaddingException]Exception while processing MYMD encryption : decode BadPaddingException Occured : {}",
                    e.getMessage());
        } catch (IOException e) {
            log.error(
                    "의료 마이데이터 예외발생: [IOException]Exception while processing MYMD encryption : decode IOException Occured : {}",
                    e.getMessage());
        }

        return null;
    }

    /**
     * 전문 사이즈 계산
     *
     * @param length
     * @param blockSize
     * @return
     */
    private static int calculateLength(int length, int blockSize) {
        return ((length + CIPER_BLOCK_SIZE - 1) / CIPER_BLOCK_SIZE) * blockSize;
    }

}
