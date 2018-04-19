package com.pft.communicate.protocal.util;

import java.util.Random;

public class IDMaker {

	public static String codesrc = "0123456789abcdefghijklmnopqrstuvwxyz";

	public static String numbercodesrc = "0123456789";

	private static final Random random = new Random();

	public static final int DEFAULTNUM = 20;

	/**
	 * 生成长度为20的随即字符串
	 * 
	 * @return
	 */
	public static String makeId() {
		return makeId(20);
	}

	/**
	 * 生成指定长度的随机字符串
	 * 
	 * @param num
	 *            生成随机字符串的长度
	 * @return string
	 */
	public static String makeId(int length) {
		return makeId(length, codesrc);
	}

	public static String makeId(int length, String src) {
		if (length <= 0 || src == null || src.length() < 2) {
			throw new IllegalArgumentException("invalid parameter length: " + length, null);
		}

		StringBuilder id = new StringBuilder();
		for (int i = 0; i < length; i++) {
			id.append(src.charAt(random.nextInt(src.length() - 1)));
		}
		return id.toString();
	}

}
