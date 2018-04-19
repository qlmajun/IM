package com.pft.communicate.protocal.parser;

public class BytesUtil {

	public static byte[] arraycat(byte[] buf1, byte[] buf2) {

		byte[] bufret = null;
		int len1 = 0;
		int len2 = 0;
		if (buf1 != null)
			len1 = buf1.length;
		if (buf2 != null)
			len2 = buf2.length;
		if (len1 + len2 > 0)
			bufret = new byte[len1 + len2];
		if (len1 > 0)
			System.arraycopy(buf1, 0, bufret, 0, len1);
		if (len2 > 0)
			System.arraycopy(buf2, 0, bufret, len1, len2);
		return bufret;
	}

	/**
	 * 比较两个byte数组的前多少个字节相同
	 *
	 * @param buf1
	 * @param buf2
	 * @param length
	 * @return
	 */
	public static boolean arrayPartEquals(byte[] buf1, byte[] buf2, int length) {

		if (length <= 0) {
			length = Math.min(buf1.length, buf2.length);
		} else if (buf1.length < length || buf2.length < length) {
			return false;
		}

		for (int i = 0; i < length; i++) {
			if (buf1[i] != buf2[i]) {
				return false;
			}
		}

		return true;
	}

	/**
	 * 将字节数组中连续的4个字节转换为int
	 *
	 * @param bytes
	 * @param startIndex 开始的索引，如果为3,则表示第3,4,5,6个字节转为int
	 * @return
	 */
	public static int bytesArrayToInt(final byte[] bytes, int startIndex) {
		int value = 0;
		if (bytes != null && bytes.length > 0) {
			int endIndex = (4 + startIndex) > bytes.length ? bytes.length : 4 + startIndex;
			for (int j = startIndex; j < endIndex; j++) {
				value = (value << 8) | (bytes[j] & 0xFF);
			}
		}
		return value;
	}

	/**
	 * int值转成4字节的byte数组
	 *
	 * @param num
	 * @return
	 */
	public static byte[] intTobyteArray(int num) {
		byte[] result = new byte[4];
		result[0] = (byte) (num >>> 24);// 取最高8位放到0下标
		result[1] = (byte) (num >>> 16);// 取次高8为放到1下标
		result[2] = (byte) (num >>> 8); // 取次低8位放到2下标
		result[3] = (byte) (num); // 取最低8位放到3下标
		return result;
	}

	/**
	 *
	 * @param target 被填充的数组
	 * @param from 开始被填充的数组的下标
	 * @param to 被填充的数组的结束下标, 如果下标无效, 如to<from, fill的大小小于to和from的差值，则根据fill的大小计算from
	 * @param fill 填充数组
	 */
	public static void fillBytesWithArray(final byte[] target, int from, int to, byte[] fill) {
		if (target == null || fill == null) {
			throw new IllegalArgumentException("invalid byte array");
		}
		if (from > to || to - from + 1 > fill.length) {
			to = from + fill.length - 1;
		}

		for (int i = from; i <= to; i++) {
			target[i] = fill[i - from];
		}
	}

}
