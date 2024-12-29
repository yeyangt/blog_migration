package com.august.blog.util;

import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 * 目录管理
 */
public abstract class DirUtil {

    /**
     * 文章存储位置
     */
    public static String getArticleDir(){

        String path=getListDir()+ File.separator+"content";
        // 创建文件夹，如果不存在
        File folder = new File(path);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        return path;
    }

    /**
     * 未成功解析的文章存储位置
     */
    public static String getErrorArticleDir(){

        String path=getListDir()+ File.separator+"error";
        // 创建文件夹，如果不存在
        File folder = new File(path);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        return path;
    }

    /**
     * 文章列表
     */
    public static String getListDir(){
        String userDir = System.getProperty("user.dir");
        File parentFolder=null;
        List<String> modules= Arrays.asList("sina_download","jianshu_upload","jianshu_publish","blog_base");
        for (String module : modules) {
            if(userDir.endsWith(module)){
                parentFolder=new File(userDir).getParentFile();
                break;
            }
        }

        if(parentFolder==null){
            parentFolder=new File(userDir);
        }

        String moduleRootPath=parentFolder.getAbsolutePath()+File.separator +"blog_base";

        String path=moduleRootPath + File.separator + "src" + File.separator + "main" + File.separator + "resources" + File.separator + "article_storage";
        // 创建文件夹，如果不存在
        File folder = new File(path);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        return path;
    }


    /**
     * 图片存储位置
     * @return
     */
    public static String getImgDir(){
        String path=getListDir()+ File.separator + "img";
        // 创建文件夹，如果不存在
        File folder = new File(path);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        return path;
    }

    /**
     * 一张用于测试简书上传是否成功的图片
     */
    public static String getTetImgPath(){

        String path=getListDir();
        File folder = new File(path);
        return folder.getParentFile()+File.separator + "test"+File.separator+"upload.png";
    }

}
