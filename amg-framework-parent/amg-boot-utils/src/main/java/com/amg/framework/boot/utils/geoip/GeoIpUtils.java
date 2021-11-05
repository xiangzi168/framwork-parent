package com.amg.framework.boot.utils.geoip;

import java.io.File;
import java.io.InputStream;
import java.net.InetAddress;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.model.CountryResponse;
import com.maxmind.geoip2.record.Country;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

/**
 * @author zhengzhouyang
 * @date 2020/7/16 10:51
 * @describe
 */
public class GeoIpUtils {

    private final static Logger logger = LoggerFactory.getLogger(GeoIpUtils.class);

    private static DatabaseReader reader;

    private static DatabaseReader getReader(){
        try{
            if(reader == null){
                logger.warn("打开ip数据库");
                ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
                Resource[] resources = resolver.getResources("GeoLite2-Country.mmdb");
                Resource resource = resources[0];
                //ClassPathResource classPathResource = new ClassPathResource("GeoLite2-Country.mmdb");
                //获得文件流，因为在jar文件中，不能直接通过文件资源路径拿到文件，但是可以在jar包中拿到文件流
                InputStream stream = resource.getInputStream();
                String targetFilePath ="geoIp/"+resource.getFilename();
                if (logger.isInfoEnabled()) {
                    logger.info("放置位置  [" + targetFilePath + "]");
                }
                File database = new File(targetFilePath);
                FileUtils.copyInputStreamToFile(stream, database);
                //File database =  resource.getFile(); // 附件下载百度云地址https://pan.baidu.com/s/1ENqTeCoMIWJMbh88nYU5gg
                reader = new DatabaseReader.Builder(database).build();
            }
            return reader;
        }catch(Exception e){
            logger.warn("打开ip数据库错误");
            return reader;
        }

    }

    /**
     * 根据ip获取国家对象,不存在则返回null
     * @param ip
     * @return
     */
    public static Country getCountry(String ip){
        try{
            InetAddress ipAddress = InetAddress.getByName(ip);
            CountryResponse response = getReader().country(ipAddress);
            Country country = response.getCountry();
            return country;
        }catch(Exception e){
            return null;
        }
    }

    /**
     * 根据ip获取国家代码,不存在则返回null
     * @param ip
     * @return
     */
    public static String getCountryCode(String ip){
        if (ip.contains("192.168") || ip.contains("127.0.0.1") || ip.contains("0:0:0:0:0:0:0:1")){
            return "CN";
        }
        Country country = getCountry(ip);
        return country != null ? country.getIsoCode() : null;
    }

    /**
     * 根据ip获取国家名称,不存在则返回null
     * @param ip
     * @return
     */
    public static String getCountryName(String ip){
        Country country = getCountry(ip);
        return country != null ? country.getName() : null;
    }
}
