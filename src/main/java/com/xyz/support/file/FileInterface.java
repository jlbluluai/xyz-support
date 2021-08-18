package com.xyz.support.file;

import com.xyz.support.SupportConstant;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

/**
 * 文件处理接口
 *
 * @author xyz
 * @date 2021/8/10
 **/
public interface FileInterface {

    /**
     * 文件的方式上传
     * <p>
     * 文件名取本名，文件路径在当前服务根路径
     *
     * @param file 文件 cannot be null
     * @return 文件名（带扩展名）
     */
    String upload(File file) throws Exception;

    /**
     * 文件的方式上传
     * <p>
     * 文件名取自定义，文件路径在当前服务根路径
     *
     * @param file     文件 cannot be null
     * @param fileName 文件名 若是blank，退化为不指定文件名的模式；
     *                 若指定，若未携带文件扩展名（会自动从file中算出，推荐），若携带扩展名，以携带为准。
     *                 注意文件名请不要自主携带路径，可能发生不必要的问题
     * @return 文件名（带扩展名）
     */
    String upload(File file, String fileName) throws Exception;

    /**
     * 文件的方式上传
     * <p>
     * 文件名取自定义，文件路径也取自定义（相对于当前服务根路径）
     * <p>
     * 注意有些文件存储不支持目录形式（比如七牛云），该方法不做开放，标明{@link Deprecated}，若调用程序会直接报错，请核对好文档
     *
     * @param file     文件 cannot be null
     * @param fileName 文件名 若是blank，退化为不指定文件名的模式；
     *                 若指定，若未携带文件扩展名（会自动从file中算出，推荐），若携带扩展名，以携带为准。
     *                 注意文件名请不要自主携带路径，可能发生不必要的问题
     * @param filePath 文件路径 允许自定义相对于服务根路径的下级路径，不传则退化为默认根路径。
     *                 注意有些文件存储不支持目录形式（比如七牛云），该方法不做开放，标明{@link Deprecated}，若调用程序会直接报错，请核对好文档
     * @return 文件名（带扩展名）
     */
    String upload(File file, String fileName, String filePath) throws Exception;

    /**
     * 网络文件的方式上传
     * <p>
     * 文件名取本名，文件路径在当前服务根路径
     *
     * @param file 网络文件 cannot be null
     * @return 文件名（带扩展名）
     */
    String upload(MultipartFile file) throws Exception;

    /**
     * 网络文件的方式上传
     * <p>
     * 文件名取自定义，文件路径在当前服务根路径
     *
     * @param file     文件 cannot be null
     * @param fileName 文件名 若是blank，退化为不指定文件名的模式；
     *                 若指定，若未携带文件扩展名（会自动从file中算出，推荐），若携带扩展名，以携带为准。
     *                 注意文件名请不要自主携带路径，可能发生不必要的问题
     * @return 文件名（带扩展名）
     */
    String upload(MultipartFile file, String fileName) throws Exception;

    /**
     * 网络文件的方式上传
     * <p>
     * 文件名取自定义，文件路径在当前服务根路径
     * <p>
     * 注意有些文件存储不支持目录形式（比如七牛云），该方法不做开放，标明{@link Deprecated}，若调用程序会直接报错，请核对好文档
     *
     * @param file     文件 cannot be null
     * @param fileName 文件名 若是blank，退化为不指定文件名的模式；
     *                 若指定，若未携带文件扩展名（会自动从file中算出，推荐），若携带扩展名，以携带为准。
     *                 注意文件名请不要自主携带路径，可能发生不必要的问题
     * @param filePath 文件路径 允许自定义相对于服务根路径的下级路径，不传则退化为默认根路径。
     *                 注意有些文件存储不支持目录形式（比如七牛云），该方法不做开放，标明{@link Deprecated}，若调用程序会直接报错，请核对好文档
     * @return 文件名（带扩展名）
     */
    String upload(MultipartFile file, String fileName, String filePath) throws Exception;

    /**
     * 文件下载
     * <p>
     * 下载完后提供文件，若是临时文件只想要内容，确保使用后主动删除文件，避免服务器硬盘资源浪费
     *
     * @param fileName 文件名 cannot be blank 若需要带二级路径，自行拼装携带
     * @param filePath 下载后本地文件路径 若不指定，默认为当前路径
     */
    File download(String fileName, String filePath) throws Exception;

    /**
     * 文件下载
     * <p>
     * 下载完后提供文件内容字节数组
     * <p>
     * 若是文件大小不可预估，不推荐使用，避免撑爆内存
     *
     * @param fileName 文件名 cannot be blank 若需要带二级路径，自行拼装携带
     */
    byte[] download(String fileName) throws Exception;

    /**
     * 获取文件扩展名
     *
     * @param fileName 文件名
     * @return 文件扩展名
     */
    default String getFileExtension(String fileName) {
        if (!StringUtils.hasText(fileName)) {
            return SupportConstant.EMPTY_STR;
        }

        if (fileName.lastIndexOf(SupportConstant.DOT) != -1 && fileName.lastIndexOf(SupportConstant.DOT) != 0) {
            return fileName.substring(fileName.lastIndexOf(SupportConstant.DOT) + 1);
        }

        return SupportConstant.EMPTY_STR;
    }

    /**
     * 判断文件是否存在扩展名
     *
     * @param fileName 文件名
     * @return true/false
     */
    default boolean existFileExtension(String fileName) {
        if (!StringUtils.hasText(fileName)) {
            return false;
        }
        return fileName.lastIndexOf(SupportConstant.DOT) != -1 && fileName.lastIndexOf(SupportConstant.DOT) != 0;
    }
}
