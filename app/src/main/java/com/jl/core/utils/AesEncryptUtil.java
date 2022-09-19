package com.jl.core.utils;

import android.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * 工程名称： bd
 * 说明：支付参数单独加密的工具类
 * packageName： bd.common.utlis
 * className： AesEncryptUtil
 *
 * @author bd-design
 * @version 1.0
 * @date 2019/8/3 17:19
 * @since 1.8
 */
public class AesEncryptUtil {
	//使用AES-128-CBC加密模式，key需要为16位,key和iv可以相同！
	private static String KEY = "help_store_pay02";
	private static String IV = "_dosIs$2514_112G";

	public static String getKEY() {
		return KEY;
	}

	public static void setKEY(String KEY) {
		AesEncryptUtil.KEY = KEY;
	}

	public static String getIV() {
		return IV;
	}

	public static void setIV(String IV) {
		AesEncryptUtil.IV = IV;
	}

	/**
	 * 加密方法
	 * @param data  要加密的数据
	 * @param key 加密key
	 * @param iv 加密iv
	 * @return 加密的结果
	 * @throws Exception
	 */
	public static String encrypt(String data, String key, String iv) throws Exception {
		try {

			Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");//"算法/模式/补码方式"NoPadding PkcsPadding
			int blockSize = cipher.getBlockSize();

			byte[] dataBytes = data.getBytes();
			int plaintextLength = dataBytes.length;
			if (plaintextLength % blockSize != 0) {
				plaintextLength = plaintextLength + (blockSize - (plaintextLength % blockSize));
			}

			byte[] plaintext = new byte[plaintextLength];
			System.arraycopy(dataBytes, 0, plaintext, 0, dataBytes.length);

			SecretKeySpec keyspec = new SecretKeySpec(key.getBytes(), "AES");
			IvParameterSpec ivspec = new IvParameterSpec(iv.getBytes());

			cipher.init(Cipher.ENCRYPT_MODE, keyspec, ivspec);
			byte[] encrypted = cipher.doFinal(plaintext);

			return Base64.encodeToString(encrypted, Base64.DEFAULT);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 使用默认的key和iv加密
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public static String encrypt(String data) throws Exception {
		return encrypt(data, KEY, IV);
	}
}
