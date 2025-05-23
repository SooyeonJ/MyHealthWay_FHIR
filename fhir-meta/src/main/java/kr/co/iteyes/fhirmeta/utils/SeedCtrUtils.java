package kr.co.iteyes.fhirmeta.utils;

import kr.re.nsr.seed.KISA_SEED_CTR;

import java.security.SecureRandom;
import java.util.Base64;

public class SeedCtrUtils {

	private static final int KEY_BIT_LENGTH = 256; // 키 길이 : 256 비트
	private static final int KEY_BYTE_LENGTH = KEY_BIT_LENGTH / 8;
	private static final byte[] bszCTR;

	// 카운터
	static {
		bszCTR = new byte[KEY_BYTE_LENGTH];
		bszCTR[KEY_BYTE_LENGTH - 1] = (byte) 0x0FE;
	}

	/**
	 * 의료 마이데이터 시스템에 맞게 수정
	 *
	 * @param key SEED 암호화키
	 * @param plainText 평문
	 * @return 암호화된 데이터
	 */
	public static String SEED_CTR_Encrypt(String key, String plainText) {

		byte[] pbszUserKey = Base64.getDecoder().decode(key.getBytes());
		byte[] pbszPlainText = plainText.getBytes();
		int nPlainTextLen = pbszPlainText.length;

		byte[] defaultCipherText = KISA_SEED_CTR.SEED_CTR_Encrypt(pbszUserKey, bszCTR, pbszPlainText, 0, nPlainTextLen);
		String defaultCipherString = Base64.getEncoder().encodeToString(defaultCipherText);

		return defaultCipherString;
	}

	/**
	 * 의료 마이데이터 시스템에 맞게 수정
	 *
	 * @param key SEED 암호화키
	 * @param pbszPlainText 평문을 byte형으로
	 * @return 암호화된 데이터
	 */
	public static String SEED_CTR_Encrypt(String key, byte[] pbszPlainText) {

		byte[] pbszUserKey = Base64.getDecoder().decode(key.getBytes());
		int nPlainTextLen = pbszPlainText.length;

		byte[] defaultCipherText = KISA_SEED_CTR.SEED_CTR_Encrypt(pbszUserKey, bszCTR, pbszPlainText, 0, nPlainTextLen);
		String defaultCipherString = Base64.getEncoder().encodeToString(defaultCipherText);

		return defaultCipherString;
	}

	/**
	 * 의료 마이데이터 시스템에 맞게 수정
	 *
	 * @param key SEED 암호화키
	 * @param pbszPlainText 평문을 byte형으로
	 * @return 암호화된 데이터(byte)
	 */
	public static byte[] SEED_CTR_Encrypt_byte(String key, byte[] pbszPlainText) {

		byte[] pbszUserKey = Base64.getDecoder().decode(key.getBytes());
		int nPlainTextLen = pbszPlainText.length;

		return KISA_SEED_CTR.SEED_CTR_Encrypt(pbszUserKey, bszCTR, pbszPlainText, 0, nPlainTextLen);
	}

	/**
	 * 의료 마이데이터 시스템에 맞게 수정
	 *
	 * @param key SEED 암호화키
	 * @param pbszCipherText 암호화된 데이터(byte)
	 * @return 복호화 데이터(byte)
	 */
	public static byte[] SEED_CTR_Decrypt_byte(String key, byte[] pbszCipherText) {

		byte[] pbszUserKey = Base64.getDecoder().decode(key.getBytes());
		int nCipherTextLen = pbszCipherText.length;

		return KISA_SEED_CTR.SEED_CTR_Decrypt(pbszUserKey, bszCTR, pbszCipherText, 0, nCipherTextLen);
	}

	/**
	 * 의료 마이데이터 시스템에 맞게 수정
	 *
	 * @param key SEED 암호화키
	 * @param encrypedData 암호화 데이터
	 * @return 복호화 데이터(byte)
	 */
	public static byte[] SEED_CTR_Decrypt_byte(String key, String encrypedData) {

		byte[] pbszUserKey = Base64.getDecoder().decode(key.getBytes());
		byte[] pbszCipherText = Base64.getDecoder().decode(encrypedData.getBytes());
		int nCipherTextLen = pbszCipherText.length;

		return KISA_SEED_CTR.SEED_CTR_Decrypt(pbszUserKey, bszCTR, pbszCipherText, 0, nCipherTextLen);
	}

	/**
	 * 의료 마이데이터 시스템에 맞게 수정
	 *
	 * @param key SEED 암호화키
	 * @param encrypedData 암호화 데이터
	 * @return 복호화 데이터(String)
	 */
	public static String SEED_CTR_Decrypt(String key, String encrypedData) {

		byte[] pbszUserKey = Base64.getDecoder().decode(key.getBytes());
		byte[] pbszCipherText = Base64.getDecoder().decode(encrypedData.getBytes());
		int nCipherTextLen = pbszCipherText.length;

		return new String(KISA_SEED_CTR.SEED_CTR_Decrypt(pbszUserKey, bszCTR, pbszCipherText, 0, nCipherTextLen));
	}

	public static String SEED_CTR_create() {
		byte[] arr = new byte[8];
		new SecureRandom().nextBytes(arr);

		String bytesToHex = convertBytesToHex(arr);

		return Base64.getEncoder().encodeToString(bytesToHex.getBytes());
	}

	private static String convertBytesToHex(byte[] bytes) {
		StringBuilder result = new StringBuilder();
		for (byte temp : bytes) {
			result.append(String.format("%02x", temp));
		}
		return result.toString();
	}
}
