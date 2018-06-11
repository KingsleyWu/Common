package com.smart.common.localfile;

import android.content.Context;
import android.os.StatFs;
import android.util.Log;

import com.smart.common.data.DataFormat;
import com.smart.common.util.DebugUtil;
import com.smart.common.util.Utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.CRC32;

/**
 * 本地文件增删改查工具类，包括创建文件，创建目录，判断文件是否存在，文件大小单位转换，
 * 获取文件根目录，删除文件，删除目录，删除文件或目录，复制文件，复制文件夹，文件过滤，文件名过滤等
 * @author zhujunjun
 * @date 2016年3月31日
 * @project LunznTool
 * @package com.smart.localfile
 * @filename LocalFileCRUDUtils.java
 * @version 1.0
 * @see 
 * @since 通用工具jar
 */
public class LocalFileCRUDUtils {
    
    /**
     * 保存字符串内容至本地文本
     * @param info 字符串内容
     * @param localFilePath 本地保存路径
     * @return 写入成功，返回true，否则返回false
     */
    public static boolean saveInfoToLocal(String info, String localFilePath) {
        boolean writeSuccess = false;
        FileOutputStream fos = null;
        try {
            File localFile = new File(localFilePath);
            if (!localFile.getParentFile().exists()) {
                localFile.getParentFile().mkdirs();
            }
            fos = new FileOutputStream(localFilePath);
            fos.write(info.getBytes());
            fos.flush();
            writeSuccess = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            Utils.close(fos);
        }
        return writeSuccess;
    }
    
    /**
     * 以某种编码格式读取文件
     * @param path 文件路径
     * @param fileCode 编码格式
     * @return 文件内容
     */
    public static String readFileWithCode(String path, String fileCode) {
        StringBuilder content = new StringBuilder();
        try {
            FileInputStream fis = new FileInputStream(path);
            int len;
            byte[] b = new byte[1024];
            while ((len = fis.read(b)) > 0) {
                content.append(new String(b, 0, len, fileCode));
            }
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content.toString();
    }
    
    /**
     * 读取本地文件 
     * @param localFilePath 文件绝对路径
     * @return 返回字符串，如果失败，返回null
     */
    public static String readFile(String localFilePath) {
        BufferedReader br = null;
        StringBuffer sbf = null;
        String result = null;
        File file = new File(localFilePath);
        try {
            sbf = new StringBuffer();
            br = new BufferedReader(new FileReader(file));
            String line = null;
            while ((line = br.readLine()) != null) {
                sbf.append(line);
            }
            result = sbf.toString();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            Utils.close(br);
        }
        return result;
    }
    
    /**
     * 读取文件内容 
     * @param localFilePath 文 文件绝对路径
     * @return 返回字符串，如果失败，返回字符串 ""
     */
    public static String readFileNoNull(String localFilePath) {
        String result = readFile(localFilePath);
        return result == null ? "" : result;
    }
    
    /**
     * 从assets 从Assert中获取编码格式为“UTF-8”的字符串数据
     * @param context 上下文
     * @param name 文件名
     * @return 文件内容，如果读取失败，返回null
     */
    public static String getFromAssertFile(Context context, String name) {
        String result = null;
        InputStream in = null;
        try {
            in = context.getResources().getAssets().open(name);
            // 获取文件的字节数
            int lenght = in.available();
            // 创建byte数组
            byte[] buffer = new byte[lenght];
            // 将文件中的数据读到byte数组中
            in.read(buffer);
            result = new String(buffer, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            Utils.close(in);
        }
        return result;
    }
    
    /**
     * 删除文件或文件夹
     * @param path 文件夹，或者是单个文件的绝对路径
     * @return 删除成功，返回true否则返回false
     */
    public static boolean deleteFile(String path) {
        boolean result = false;
        try {
            File file = new File(path);
            if (file.exists()) {
                if (file.isFile()) {
                    result = deleteSingleFile(path);
                } else if (file.isDirectory()) {
                    result = deleteDirectory(path);
                }
            }
        } catch (Exception e) {
            result = false;
        }
        return result;
    }
    
    /**
     * 拷贝assest目录下的文件至其他文件目录
     * @param context 上下文
     * @param fileName 文件相对于assest目录的文件名称
     * @param targetDir 要拷贝的目标文件夹的路径
     * @return 拷贝成功，返回true，否则返回false
     */
    public static boolean copyAssestFile(Context context, String fileName, String targetDir) {
        boolean result = false;
        InputStream is = null;
        FileOutputStream fos = null;
        try {
            is = context.getAssets().open(fileName);
            fos = new FileOutputStream(targetDir + fileName);
            byte[] byteArr = new byte[1024];
            int length = 0;
            while ((length = is.read(byteArr)) > 0) {
                fos.write(byteArr, 0, length);
            }
            fos.flush();
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            Utils.close(fos);
            Utils.close(is);
        }
        return result;
    }
    
    /**
     * 复制单个文件
     * @param srcPath 要复制的源文件路径
     * @param tagPath 复制目标文件绝对路径，如果只带路径名称，则使用源文件的文件名
     * @return 拷贝成功，返回true，否则返回false
     */
    public static boolean copySingleFile(String srcPath, String tagPath) {
        boolean result = false;
        FileInputStream fis = null;
        FileOutputStream fos = null;
        try {
            File file = new File(srcPath);
            if (file.exists() && file.isFile()) {
                fis = new FileInputStream(srcPath);
                File newFile = new File(tagPath);
                if (newFile.isDirectory()) {
                    newFile.mkdirs();
                    if (!tagPath.endsWith(File.separator)) {
                        tagPath += File.separator;
                    }
                    tagPath += file.getName();
                } else {
                    newFile.getParentFile().mkdirs();
                }
                DebugUtil.i(" newPath  = " + tagPath);
                fos = new FileOutputStream(tagPath);
                byte[] byteArr = new byte[1024];
                int length = 0;
                while ((length = fis.read(byteArr)) > 0) {
                    fos.write(byteArr, 0, length);
                }
                fos.flush();
                result = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            Utils.close(fos);
            Utils.close(fis);
        }
        return result;
    }
    
    /**
     * 复制文件夹
     * @param srcPath 源文件夹路径
     * @param tagPath 目标文件夹路径
     */
    public static void copyFolder(String srcPath, String tagPath) {
        try {
            File file = new File(srcPath);
            if (file.exists() && file.isDirectory()) {
                File targetFile = new File(tagPath);
                if (!targetFile.exists()) {
                    targetFile.mkdirs();
                }
                String[] sourceFileNames = file.list();
                String sourceFilePath = null;
                String targetFilePath = null;
                if (sourceFileNames != null && sourceFileNames.length > 0) {
                    for (String sourceFileName : sourceFileNames) {
                        // 以“/”结尾
                        if (srcPath.endsWith(File.separator)) {
                            sourceFilePath = srcPath + sourceFileName;
                        }
                        // 不以“/”结尾
                        else {
                            sourceFilePath = srcPath + File.separator + sourceFileName;
                        }
                        // 以“/”结尾
                        if (tagPath.endsWith(File.separator)) {
                            targetFilePath = tagPath + sourceFileName;
                        }
                        // 不以“/”结尾
                        else {
                            targetFilePath = tagPath + File.separator + sourceFileName;
                        }
                        File sourceFile = new File(sourceFilePath);
                        // 如果是文件直接拷贝
                        if (sourceFile.isFile()) {
                            copySingleFile(sourceFilePath, targetFilePath);
                        }
                        // 如果是目录，则递归拷贝
                        else if (sourceFile.isDirectory()) {
                            copyFolder(sourceFilePath, targetFilePath);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**  
    * 移动文件  
    * @param srcFileName    源文件完整路径 
    * @param destDirName    目的目录完整路径 
    * @return 文件移动成功返回true，否则返回false  
    */
    public static boolean moveFile(String srcFileName, String destDirName) {
        File srcFile = new File(srcFileName);
        if (!srcFile.exists() || !srcFile.isFile()) {
            return false;
        }
        
        File destDir = new File(destDirName);
        if (!destDir.exists()) {
            destDir.mkdirs();
        }
        return srcFile.renameTo(new File(destDirName + File.separator + srcFile.getName()));
    }
    
    /**  
    * 移动目录  
    * @param srcDirName     源目录完整路径 
    * @param destDirName    目的目录完整路径 
    * @return 目录移动成功返回true，否则返回false  
    */
    public static boolean moveDirectory(String srcDirName, String destDirName) {
        File srcDir = new File(srcDirName);
        if (!srcDir.exists() || !srcDir.isDirectory()) {
            return false;
        }
        File destDir = new File(destDirName);
        if (!destDir.exists()) {
            destDir.mkdirs();
        }
        
        // 如果是文件则移动，否则递归移动文件夹。删除最终的空源文件夹 
        // 注意移动文件夹时保持文件夹的树状结构 
        File[] sourceFiles = srcDir.listFiles();
        for (File sourceFile : sourceFiles) {
            if (sourceFile.isFile()) {
                moveFile(sourceFile.getAbsolutePath(), destDir.getAbsolutePath());
            } else if (sourceFile.isDirectory()) {
                moveDirectory(sourceFile.getAbsolutePath(),
                    destDir.getAbsolutePath() + File.separator + sourceFile.getName());
            }
        }
        return srcDir.delete();
    }
    
    /**
     * 获取指定路径下所有以指定规则字符串结尾的文件集合
     * @param dirPath 指定路径
     * @param reg 指定规则字符串
     * @return 文件集合
     */
    public static ArrayList<File> listFilesByDirPathAndAppointReg(String dirPath, String reg) {
        ArrayList<File> result = new ArrayList<File>();
        try {
            File file = new File(dirPath);
            if (file.exists() && file.isDirectory()) {
                // 获取符合reg规则的文件集合
                File[] fileArr = file.listFiles(getFilterByAppointReg(reg));
                result.addAll(Arrays.asList(fileArr));
                // 符合目录集合
                fileArr = file.listFiles(getFileterByDirector());
                for (File dir : fileArr) {
                    if (dir.isDirectory()) {
                        listFilesByDirPathAndAppointReg(dir.getAbsolutePath(), reg);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
    
    /**
     * 获取指定路径下所有以指定规则字符串结尾的文件名集合
     * 
     * @param dirPath 指定路径
     * @param reg 指定规则字符串
     * @return 文件名集合
     */
    public static ArrayList<String> listFileNamesByDirPathAndAppointReg(String dirPath, String reg) {
        ArrayList<String> result = new ArrayList<String>();
        try {
            File file = new File(dirPath);
            if (file.exists() && file.isDirectory()) {
                // 获取符合reg规则的文件集合
                File[] fileArr = file.listFiles(getFilterByAppointReg(reg));
                for (File item : fileArr) {
                    result.add(item.getName());
                }
                // 符合目录集合
                fileArr = file.listFiles(getFileterByDirector());
                for (File dir : fileArr) {
                    if (dir.isDirectory()) {
                        listFilesByDirPathAndAppointReg(dir.getAbsolutePath(), reg);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
    
    /** 
     * 获取扩展存储路径，TF卡、U盘 
     * @return 路径列表
     */
    public static List<String> getALLMemoryFile() {
        InputStream is = null;
        InputStreamReader isr = null;
        List<String> paths = new ArrayList<String>();
        try {
            Runtime runtime = Runtime.getRuntime();
            Process proc = runtime.exec("mount");
            is = proc.getInputStream();
            isr = new InputStreamReader(is);
            String line;
            BufferedReader br = new BufferedReader(isr);
            while ((line = br.readLine()) != null) {
                if (line.contains("secure")) {
                    continue;
                }
                if (line.contains("asec")) {
                    continue;
                }
                if (line.contains("fat")) {
                    String columns[] = line.split(" ");
                    if (columns != null && columns.length > 1) {
                        paths.add(columns[1]);
                    }
                } else if (line.contains("fuse")) {
                    String columns[] = line.split(" ");
                    if (columns != null && columns.length > 1) {
                        paths.add(columns[1]);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            Utils.close(is);
            Utils.close(isr);
        }
        return paths;
    }
    
    /**
     * 获取指定路径可用空间大小
     * @param path 路径
     * @return 路径大小
     */
    public static long getAvailableSpaceSize(String path) {
        long avail = 0;
        try {
            StatFs statFs = new StatFs(path);
            long blockSize = statFs.getBlockSize();
            long availableBlocks = statFs.getAvailableBlocks();
            avail = availableBlocks * blockSize;
            DebugUtil.i("getAvailableSpaceSize ==" + avail);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return avail;
    }
    
    /**
     * 获取本地文件的crc校验code值
     * @param path 文件的绝对路径
     * @return 返回crc值，错误的话返回-1
     */
    public static long getCrcCodeFromLocal(String path) {
        long crcCode = -1L;
        FileInputStream inStream = null;
        if (!DataFormat.isEmpty(path) && new File(path).exists()) {
            try {
                inStream = new FileInputStream(path);
                CRC32 crc32 = new CRC32();
                byte data[] = new byte[1024];
                int read = 0;
                while ((read = inStream.read(data)) != -1) {
                    crc32.update(data, 0, read);
                }
                crcCode = crc32.getValue();
                Log.i("LocalFIleCRUDUtils", "get file crccode = " + crcCode);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                Utils.close(inStream);
            }
        }
        return crcCode;
    }
    
    /****************************************************************************
     * 
     * 私有方法，不对外公开
     * 
     * ***************************************************************************/
    /**
     * 删除单个文件
     * @param path 文件路径
     * @return 删除成功，返回true否则返回false
     */
    private static boolean deleteSingleFile(String path) {
        boolean result = false;
        try {
            File file = new File(path);
            if (file.exists() && file.isFile()) {
                file.delete();
                result = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            result = false;
        }
        return result;
    }
    
    /**
     * 删除目录
     * @param path 目录路径
     * @return 删除成功，返回true否则返回false
     */
    private static boolean deleteDirectory(String path) {
        boolean result = false;
        try {
            if (!path.endsWith(File.separator)) {
                path += File.separator;
            }
            File dirFile = new File(path);
            if (!dirFile.exists() || !dirFile.isDirectory()) {
                result = false;
            } else {
                File[] fileList = dirFile.listFiles();
                if (fileList != null && fileList.length > 0) {
                    for (File item : fileList) {
                        if (item.isFile()) {
                            result = deleteSingleFile(item.getAbsolutePath());
                            if (!result) {
                                break;
                            }
                        } else if (item.isDirectory()) {
                            result = deleteDirectory(item.getAbsolutePath());
                            if (!result) {
                                break;
                            }
                        }
                    }
                } else {
                    result = true;
                }
                if (result) {
                    dirFile.delete();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            result = false;
        }
        return result;
    }
    
    /**
     * 获取目录过滤器
     * @return 目录过滤器
     */
    private static FileFilter getFileterByDirector() {
        return new FileFilter() {
            @Override
            public boolean accept(File file) {
                return file.isDirectory();
            }
        };
    }
    
    /**
     * 获取以指定文件名以规则字符串结尾的文件名过滤器
     * @param reg 规则字符串
     * @return 文件过滤器
     */
    private static FilenameFilter getFilterByAppointReg(final String reg) {
        return new FilenameFilter() {
            @Override
            public boolean accept(File file, String fileName) {
                return fileName.endsWith(reg);
            }
        };
    }
    
    /**
     * 写文件数据
     * @param file
     * @param data
     * @return
     */
    public static boolean writeFile(File file, String data) {
        boolean result = false;
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            fos.write(data.getBytes());
            fos.flush();
            result = true;
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            Utils.close(fos);
        }
        return result;
    } // End writeFile
    
    /**
     * 读文件数据
     * @param file
     * @return
     */
    public static String readFileData(File file) {
        String result = null;
        FileInputStream fis = null;
        byte[] fd = new byte[(int)file.length()];
        try {
            fis = new FileInputStream(file);
            fis.read(fd);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            Utils.close(fis);
        }
        if (fd.length > 0) {
            result = new String(fd);
        }
        return result;
    } // End readFileData
}
