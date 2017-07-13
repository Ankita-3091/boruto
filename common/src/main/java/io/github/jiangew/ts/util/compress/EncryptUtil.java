package io.github.jiangew.ts.util.compress;

/**
 * 加解密<br>
 * 优化版
 */
public class EncryptUtil {
    // 密钥
    private static final String KEY = "Q9*11q^REaDer%Bs1&#@[";
    // 密钥长度
    private static final int KEY_LENGTH = KEY.length();

    /**
     * 解密
     *
     * @param input
     * @return
     */
    public static byte[] encrypt(final byte[] input) {
        final int length = input.length;
        byte[] output = new byte[length];
        byte j = 0;
        for (int i = 0; i < length; i++) {
            j = j < KEY_LENGTH ? j : 0;
            byte cKey = (byte) KEY.charAt(j++);
            byte highAndLow = (byte) (input[i] ^ cKey);
            output[i] = (byte) (((highAndLow & 0x0f) << 4) | ((highAndLow & 0xf0) >> 4));
        }
        return output;
    }

    /**
     * 加密
     *
     * @param szInput
     * @return
     */
    public static byte[] decrypt(byte[] szInput) {
        int nInputLen = szInput.length;
        byte[] cOutput = new byte[nInputLen];
        byte j = 0;
        for (int i = 0; i < nInputLen; i++) {
            j = j < KEY_LENGTH ? j : 0;
            byte highAndLow = szInput[i];
            byte cString = (byte) (((highAndLow & 0x0f) << 4) | ((highAndLow & 0xf0) >> 4));
            byte cKey = (byte) KEY.charAt(j++);
            cOutput[i] = (byte) (cString ^ cKey);
        }
        return cOutput;
    }
}
