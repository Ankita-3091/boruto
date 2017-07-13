package io.github.jiangew.ts.util.compress.bo;

/**
 * 需要加密打包的内容
 */
public class ContentBO {
    // == 处理类型 ======================================
    // Gzip压缩并加密
    public final static int TYPE_GZIP_ENCRYPT = 0;
    // 不处理
    public final static int TYPE_NOT_HANDLE = -1;
    // =================================================

    // 文本内容
    private String txt;
    // 文件名
    private String fileName;
    // 处理类型
    private int type = TYPE_GZIP_ENCRYPT;

    public ContentBO(String txt, String fileName) {
        this.txt = txt;
        this.fileName = fileName;
    }

    public ContentBO(String txt, String fileName, int type) {
        this.txt = txt;
        this.fileName = fileName;
        this.type = type;
    }

    public String getTxt() {
        return txt;
    }

    public void setTxt(String txt) {
        this.txt = txt;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }


    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "ContentVO [txt=" + txt + ", fileName=" + fileName + ", type=" + type + "]";
    }
}
