package io.github.jiangew.ts.util.compress;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipParameters;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import io.github.jiangew.ts.util.compress.bo.ContentBO;

/**
 * 压缩解压/加密解密
 */
public class CompressUtil {
    public static final int DEFAULT_FILE_MODE = 0000644;

    /**
     * 获取文件MD5
     *
     * @param file 目标文件
     * @return MD5值（大写）
     * @throws Exception
     */
    public static String md5Hex(File file) {

        String md5 = null;
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            md5 = DigestUtils.md5Hex(fis);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(fis);
        }
        return md5.toUpperCase();
    }

    /**
     * 如果目录不存在则新建目录
     *
     * @param dirPath 目标路径
     * @return true：成功，false：失败
     */
    private static boolean mkdirsIfNotExit(String dirPath) {
        File dir = new File(dirPath);
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                return false;
            }
        }
        return true;
    }

    /**
     * 解压TAR文件
     *
     * @param tarInput  tar文件
     * @param outputDir 解压根目录
     * @return 解压后文件列表
     * @throws Exception
     */
    public static List<File> unTarToDir(final InputStream tarInput, String outputDir) {
        if (!mkdirsIfNotExit(outputDir)) {
            throw new IllegalStateException(String.format("Couldn't create directory %s.", outputDir));
        }
        final List<File> resultFiles = new LinkedList<File>();
        TarArchiveInputStream tarInputStream = null;
        try {

            FileUtils.cleanDirectory(new File(outputDir));

            tarInputStream = (TarArchiveInputStream) new ArchiveStreamFactory()
                    .createArchiveInputStream(ArchiveStreamFactory.TAR, tarInput);
            TarArchiveEntry entry = null;

            while ((entry = (TarArchiveEntry) tarInputStream.getNextEntry()) != null) {
                final File outputFile = new File(outputDir, entry.getName());
                if (entry.isDirectory()) {
                    if (!mkdirsIfNotExit(outputFile.getParentFile().getAbsolutePath())) {
                        throw new IllegalStateException(
                                String.format("Couldn't create directory %s.", outputFile.getAbsolutePath()));
                    }
                } else {
                    FileUtils.copyToFile(tarInputStream, outputFile);
                }
                resultFiles.add(outputFile);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(tarInputStream);
        }
        return resultFiles;
    }

    /**
     * tar 打包<br>
     * 注意：
     * 由于Tar压缩类导致userId/groupId等值无法直接写为0而是写为了ASCII的0值，从而导致MD5值和线上不一致，但是不影响解压，可以用长度是否相等来判断是否正确
     *
     * @param inputFileList
     * @param outputTarFile
     * @return
     */
    public static File tarToFile(final Collection<File> inputFileList, File outputTarFile) {

        if (!mkdirsIfNotExit(outputTarFile.getParentFile().getAbsolutePath())) {
            throw new IllegalStateException(
                    String.format("Couldn't create directory %s.", outputTarFile.getAbsolutePath()));
        }

        // ArchiveStreamFactory asf = new ArchiveStreamFactory();
        // asf.createArchiveOutputStream(ArchiveStreamFactory.TAR, out);
        ArchiveOutputStream aos = null;
        try {

            final OutputStream out = new FileOutputStream(outputTarFile);
            // 1024 - 非标
            aos = new TarArchiveOutputStream(out, 1024, "UTF8");

            for (File file : inputFileList) {
                TarArchiveEntry entry = new TarArchiveEntry(file.getName());
                // 非标
                entry.setMode(DEFAULT_FILE_MODE);
                entry.setSize(file.length());
                aos.putArchiveEntry(entry);
                IOUtils.copy(new FileInputStream(file), aos);
                aos.closeArchiveEntry();
            }
            // aos.finish();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(aos);
        }
        return outputTarFile;
    }

    public static File tarToFile(List<byte[]> inputByteArrList, List<String> fileNameList, File outputTarFile) throws IOException {

        if (!mkdirsIfNotExit(outputTarFile.getParentFile().getAbsolutePath())) {
            throw new IllegalStateException(
                    String.format("Couldn't create directory %s.", outputTarFile.getAbsolutePath()));
        }

        byte[] bytes = tarToByteArr(inputByteArrList, fileNameList);
        FileUtils.writeByteArrayToFile(outputTarFile, bytes);

        return outputTarFile;
    }

    public static byte[] tarToByteArr(List<byte[]> inputByteArrList, List<String> fileNameList) {

        if (inputByteArrList.size() != fileNameList.size()) {
            throw new IllegalStateException("byte array list size not equal file name list size.");
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ArchiveOutputStream aos = null;
        ByteArrayInputStream in = null;
        try {

            // 1024 - 非标
            aos = new TarArchiveOutputStream(out, 1024, "UTF8");

            for (int i = 0; i < inputByteArrList.size(); i++) {
                byte[] byteArr = inputByteArrList.get(i);

                TarArchiveEntry entry = new TarArchiveEntry(fileNameList.get(i));
                // 非标
                entry.setMode(DEFAULT_FILE_MODE);
                entry.setSize(byteArr.length);
                aos.putArchiveEntry(entry);

                in = new ByteArrayInputStream(byteArr);

                IOUtils.copy(in, aos);
                aos.closeArchiveEntry();

                IOUtils.closeQuietly(in);
            }

            IOUtils.closeQuietly(aos);
            IOUtils.closeQuietly(out);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(aos);
            IOUtils.closeQuietly(out);
            IOUtils.closeQuietly(in);
        }

        return out.toByteArray();
    }

    /**
     * Gzip格式压缩 <br>
     * 参考：http://www.onicos.com/staff/iz/formats/gzip.html
     *
     * @param txt
     * @param outputGzipFile
     * @return
     * @throws IOException
     */
    public static File gzipToFile(String txt, File outputGzipFile) throws IOException {

        if (!mkdirsIfNotExit(outputGzipFile.getParentFile().getAbsolutePath())) {
            throw new IllegalStateException(
                    String.format("Couldn't create directory %s.", outputGzipFile.getAbsolutePath()));
        }

        List<String> strList = new ArrayList<String>(1);
        strList.add(txt);

        List<byte[]> gzipByteArrList = gzipToByteArray(strList);
        FileUtils.writeByteArrayToFile(outputGzipFile, gzipByteArrList.get(0));
        return outputGzipFile;
    }

    /**
     * Gzip压缩到byte数组列表
     *
     * @param contentVOList
     * @return
     */
    public static List<byte[]> gzipToByteArray(List<String> strList) {
        // 1. 将内容分别压缩为Gzip格式
        List<byte[]> gzipByteArrList = new ArrayList<byte[]>(strList.size());
        for (String str : strList) {

            InputStream in = null;
            ByteArrayOutputStream out = null;
            GzipCompressorOutputStream gzipOut = null;
            try {
                in = new ByteArrayInputStream(str.getBytes(StandardCharsets.UTF_8));
                out = new ByteArrayOutputStream();

                GzipParameters gzipParameters = new GzipParameters();
                // 非标参数
                gzipParameters.setOperatingSystem(0);
                gzipOut = new GzipCompressorOutputStream(out, gzipParameters);

                IOUtils.copy(in, gzipOut);

                // 必须先close,后面的out.toByteArray()才能拿到值
                IOUtils.closeQuietly(gzipOut);
                IOUtils.closeQuietly(out);

                gzipByteArrList.add(out.toByteArray());

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                IOUtils.closeQuietly(gzipOut);
                IOUtils.closeQuietly(out);
                IOUtils.closeQuietly(in);
            }
        }

        return gzipByteArrList;
    }

    /**
     * 解密文件（解密后是gzip格式）
     *
     * @param inputEncryptFile  加密文件
     * @param outputDecryptFile 解密文件
     * @return 解密文件
     * @throws IOException
     */
    public static File decryptFile(final File inputEncryptFile, final File outputDecryptFile) throws IOException {
        FileUtils.writeByteArrayToFile(outputDecryptFile,
                EncryptUtil.decrypt(FileUtils.readFileToByteArray(inputEncryptFile)));
        return outputDecryptFile;
    }

    /**
     * 一键打包同步版
     *
     * @param contentVOList
     * @param infoTxt
     * @param outputTarFile
     * @return
     * @throws IOException
     */
    public static File tarEncryptGzipSyncToFile(List<ContentBO> contentVOList, File outputTarFile) throws IOException {

        // 0. 准备工作
        if (!mkdirsIfNotExit(outputTarFile.getParentFile().getAbsolutePath())) {
            throw new IllegalStateException(
                    String.format("Couldn't create directory %s.", outputTarFile.getAbsolutePath()));
        }

        FileUtils.writeByteArrayToFile(outputTarFile, tarEncryptGzipSyncToByteArr(contentVOList));

        return outputTarFile;
    }

    /**
     * 一键打包同步版
     *
     * @param contentBOList
     * @param infoTxt
     * @param outputTarFile
     * @return
     * @throws IOException
     */
    public static byte[] tarEncryptGzipSyncToByteArr(List<ContentBO> contentBOList) throws IOException {

        // 1. 分类筛选
        // 需要Gzip压缩并加密的List
        List<String> needGzipAndEncryptList = new ArrayList<String>(contentBOList.size() - 1);
        // 不需要处理的List
        List<byte[]> notHandleByteArrList = new ArrayList<byte[]>(1);
        // 文件名集合
        List<String> fileNameList = new ArrayList<String>(contentBOList.size());

        for (ContentBO vo : contentBOList) {
            if (ContentBO.TYPE_GZIP_ENCRYPT == vo.getType()) {
                needGzipAndEncryptList.add(vo.getTxt());
            } else if (ContentBO.TYPE_NOT_HANDLE == vo.getType()) {
                notHandleByteArrList.add(vo.getTxt().getBytes(StandardCharsets.UTF_8));
            }

            fileNameList.add(vo.getFileName());
        }

        // 2. 将内容分别压缩为Gzip格式
        List<byte[]> gzipByteArrList = gzipToByteArray(needGzipAndEncryptList);

        // 3. Gzip加密
        List<byte[]> encryptGzipByteArrList = new ArrayList<byte[]>(needGzipAndEncryptList.size());
        for (byte[] gzipByteArr : gzipByteArrList) {
            // FileUtils.writeByteArrayToFile(new File("z:/829193_16_e.gzip"), gzipByteArr);
            encryptGzipByteArrList.add(EncryptUtil.encrypt(gzipByteArr));
        }

        // 4. Gzip加密文件+infoTxt文件使用Tar打包
        List<byte[]> tarByteArrList = new ArrayList<byte[]>(contentBOList.size());
        tarByteArrList.addAll(encryptGzipByteArrList);
        tarByteArrList.addAll(notHandleByteArrList);

        return tarToByteArr(tarByteArrList, fileNameList);
    }
}
