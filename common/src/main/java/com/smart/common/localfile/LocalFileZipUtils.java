package com.smart.common.localfile;

import android.content.Context;
import com.smart.common.util.DebugUtil;
import com.smart.common.util.Utils;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * 本地文件压缩工具类，包括解压zip文件，解压Assets目录下文件，压缩zip文件等
 * @author zhujunjun
 * @date 2016年3月31日
 * @project LunznTool
 * @package com.smart.localfile
 * @filename LocalFileZipUtils.java
 * @version 1.0
 * @see 
 * @since 通用工具jar
 */
public class LocalFileZipUtils {
    
    /** 压缩文件中其它文件的校验码存储文件 */
    public final static String CRC_CONFIG_FILE_NAME = "configinfo";
    
    /**
     * 解压缩zip文件
     * @param zipFilePath zip源文件路径
     * @param targetDirPath 解压目标文件夹路径
     * @return 解压之后的文件名
     */
    public synchronized static List<String> unZipFile(String zipFilePath, String targetDirPath) {
        boolean result = false;
        boolean isError = false;
        List<String> fileNameList = new ArrayList<String>();
        InputStream inputStream = null;
        FileOutputStream fos = null;
        try {
            File targetDir = new File(targetDirPath);
            if (!targetDir.exists()) {
                targetDir.mkdirs();
            }
            File sourceFile = new File(zipFilePath);
            if (sourceFile.exists() && sourceFile.length() > 0) {
                boolean isEnough = checkZipSpace(sourceFile, targetDirPath);
                ZipFile zipFile = new ZipFile(sourceFile);
                Enumeration enu = zipFile.entries();
                if (isEnough) {
                    while (enu.hasMoreElements()) {
                        ZipEntry entry = (ZipEntry)enu.nextElement();
                        long size = entry.getSize();
                        String zipEntryName = entry.getName();
                        inputStream = zipFile.getInputStream(entry);
                        //优先将解压到缓存目录中
                        //                        String outPath = (targetDirPath + zipEntryName).replaceAll("\\*", "/");
                        String outPath = (targetDirPath + "/" + zipEntryName).replaceAll("\\*", "/");
                        File lastDir = new File(outPath.substring(0, outPath.lastIndexOf("/")));
                        String fileName = outPath.substring(outPath.lastIndexOf("/") + 1);
                        fileNameList.add(fileName);
                        // 判断文件是否是文件夹，如果是文件夹则只创建文件夹，若是文件，则解压
                        if (!lastDir.exists()) {
                            lastDir.mkdirs();
                        }
                        // 如果是文件夹，且之前创建过，则跳过
                        if (new File(outPath).isDirectory()) {
                            continue;
                        }
                        // 文件解压
                        fos = new FileOutputStream(outPath);
                        byte[] buf = new byte[1024];
                        int length = 0;
                        while ((length = inputStream.read(buf)) > 0) {
                            fos.write(buf, 0, length);
                        }
                        if (new File(outPath).length() != size) {
                            result = false;
                            isError = true;
                            break;
                        }
                    }
                    if (!isError) {
                        result = true;
                    }
                } else {
                    DebugUtil.e("space not enough");
                }
            }
        } catch (Exception e) {
            result = false;
            e.printStackTrace();
        } finally {
            Utils.close(fos);
            Utils.close(inputStream);
        }
        if (!result) {
            fileNameList.clear();
        }
        return fileNameList;
    }
    
    /**
     *  解压Assets中的文件
     * @param context 上下文对象
     * @param assetName 压缩包文件名
     * @param targetDirPath 解压目标文件夹路径
     * @return 解压成功，返回true，否则返回false
     */
    
    public static boolean unZipByAsset(Context context, String assetName, String targetDirPath) {
        boolean result = false;
        ZipInputStream zipInputStream = null;
        FileOutputStream fileOutputStream = null;
        try {
            //创建解压目标目录
            File file = new File(targetDirPath);
            //如果目标目录不存在，则创建
            if (!file.exists()) {
                file.mkdirs();
            }
            InputStream inputStream = null;
            //打开压缩文件
            inputStream = context.getAssets().open(assetName);
            zipInputStream = new ZipInputStream(inputStream);
            //读取一个进入点
            ZipEntry zipEntry = zipInputStream.getNextEntry();
            //使用1Mbuffer
            byte[] buffer = new byte[1024 * 1024];
            //解压时字节计数
            int count = 0;
            //如果进入点为空说明已经遍历完所有压缩包中文件和目录
            while (zipEntry != null) {
                //如果是一个目录
                if (zipEntry.isDirectory()) {
                    file =
                        new File(targetDirPath + File.separator
                            + zipEntry.getName().substring(zipEntry.getName().lastIndexOf("/") + 1));
                    file.mkdir();
                } else {
                    //如果是文件
                    file =
                        new File(targetDirPath + File.separator
                            + zipEntry.getName().substring(zipEntry.getName().lastIndexOf("/") + 1));
                    //创建该文件
                    file.createNewFile();
                    fileOutputStream = new FileOutputStream(file);
                    while ((count = zipInputStream.read(buffer)) > 0) {
                        fileOutputStream.write(buffer, 0, count);
                    }
                }
                //定位到下一个文件入口
                zipEntry = zipInputStream.getNextEntry();
            }
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
            result = false;
        } finally {
            Utils.close(fileOutputStream);
            Utils.close(zipInputStream);
        }
        return result;
    }
    
    /**
     * 压缩zip文件，压缩制定路径下面的所有文件
     * @param zipFilePath 压缩文件的绝对路径
     * @param srcPath 需要压缩的文件夹
     * @return 压缩成功，返回true，否则返回false
     */
    public synchronized static boolean zipFile(String zipFilePath, String srcPath) {
        boolean result = false;
        ZipOutputStream zos = null;
        try {
            zos = new ZipOutputStream(new FileOutputStream(zipFilePath));
            File srcFile = new File(srcPath);
            File[] fileList = srcFile.listFiles();
            result = zipFile(zos, srcFile.getName(), fileList);
            Utils.close(zos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            Utils.close(zos);
        }
        return result;
    }
    
    /**
     * 压缩zip文件，压缩列举的所有文件路径的文件
     * @param zipFilePath 要压缩至的zip目标文件路径
     * @param zipInnerPath zip内部子目录路径
     * @param filePathList 要压缩的源文件路径
     * @return 压缩成功，返回true否则返回false
     */
    public synchronized static boolean zipFile(String zipFilePath, String zipInnerPath, List<String> filePathList) {
        boolean result = false;
        try {
            ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFilePath));
            File[] fileList = new File[filePathList.size()];
            for (int i = 0; i < filePathList.size(); i++) {
                fileList[i] = new File(filePathList.get(i));
            }
            result = zipFile(zos, zipInnerPath, fileList);
            Utils.close(zos);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
    
    /**
     * 读取压缩文件中，文件校验码配置文件信息
     * @param unzipDir  压缩文件的解压缓存目录
     * @param crcCodes  存储读取出来的crcCodes
     */
    public static void readZipCrcCodeConfigFile(String unzipDir, Map<String, Long> crcCodes) {
        String configStr = LocalFileCRUDUtils.readFileWithCode(unzipDir + "/" + CRC_CONFIG_FILE_NAME, "gbk");
        String[] codesStr = configStr.split(";");
        for (int i = 0; i < codesStr.length; i++) {
            String[] pairs = codesStr[i].split(":");
            if (pairs.length == 2) {
                crcCodes.put(pairs[0], Long.parseLong(pairs[1]));
            }
        }
    }
    
    /****************************************************************************
     * 
     * 私有方法，不对外公开
     * 
     * ***************************************************************************/
    
    /**
     * 将文件压缩为zip文件
     * 
     * @return
     */
    private static boolean zipFile(ZipOutputStream zos, String zipInnerPath, File[] srcFiles) {
        boolean result = false;
        FileInputStream fis = null;
        try {
            zipInnerPath = zipInnerPath.replaceAll("\\*", "/");
            if (!zipInnerPath.endsWith("/")) {
                zipInnerPath += "/";
            }
            for (File srcFile : srcFiles) {
                if (!srcFile.isDirectory()) {
                    fis = new FileInputStream(srcFile);
                    zos.putNextEntry(new ZipEntry(zipInnerPath + srcFile.getName()));
                    byte[] buf = new byte[1024];
                    int length = 0;
                    while ((length = fis.read(buf)) > 0) {
                        zos.write(buf, 0, length);
                    }
                    zos.closeEntry();
                    System.out.println("==file path==" + zipInnerPath + srcFile.getName());
                } else {
                    File[] fileList = srcFile.listFiles();
                    String srcPath = srcFile.getName();
                    srcPath = srcPath.replaceAll("\\*", "/");
                    if (!srcPath.endsWith("/")) {
                        srcPath += "/";
                    }
                    zos.putNextEntry(new ZipEntry(zipInnerPath + srcPath));
                    System.out.println("==dir path==" + zipInnerPath + srcFile.getName());
                    zipFile(zos, zipInnerPath + srcPath, fileList);
                }
            }
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
            result = false;
        } finally {
            Utils.close(fis);
        }
        return result;
    }
    
    /**
     * 判断目标路径是否有足够的空间解压
     * @param sourceFile
     * @param dirPath
     * @return 可以解压返回true，否则返回false
     */
    private static boolean checkZipSpace(File sourceFile, String dirPath) {
        ZipFile zipFile;
        boolean isEnough = true;
        long totalSize = 0;
        try {
            zipFile = new ZipFile(sourceFile);
            Enumeration enu = zipFile.entries();
            while (enu.hasMoreElements()) {
                ZipEntry entry = (ZipEntry)enu.nextElement();
                long size = entry.getSize();
                totalSize += size;
            }
            long freeSpace = LocalFileCRUDUtils.getAvailableSpaceSize(dirPath);
            if (freeSpace < totalSize) {
                isEnough = false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return isEnough;
    }
}
