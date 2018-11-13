package com.iscas.yf.IntelliPipeline.service.util;

import com.google.common.io.Resources;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BufferedHeader;
import org.apache.http.message.HeaderGroup;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.io.SAXReader;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Properties;

public class JenkinsUtils {

    // log4j - 日志输出
    private static Logger logger = Logger.getLogger(JenkinsUtils.class);

    private static String logOffset = "0";

    // 本地存放各种工作制品的目录
    // private static final String REPO = "src/main/webapp/resources/LocalRepo/";

    // // Jenkins暴露的地址
    // private static final String JENKINS_HOST = "localhost:8888";
    //
    // // 使用username跟API_TOKEN获得访问Jenkins的权限
    // private static final String JENKINS_USERNAME = "frank";
    // private static final String JENKINS_USER_TOKEN = "4e40d2cc6a3309427e69754dbf4d381a";
    // private static final String JENKINS_USER_PASSWORD = "111111";
    //
    // // 项目专属的Token, 所有项目都统一设置
    // private static final String JENKINS_JOB_TOKEN = "iscas_yf";

    // Jenkins暴露的地址
    private static final String JENKINS_HOST = initProperties("JENKINS_HOST");

    // 使用username跟API_TOKEN获得访问Jenkins的权限
    private static final String JENKINS_USERNAME = initProperties("JENKINS_USERNAME");
    private static final String JENKINS_USER_TOKEN = initProperties("JENKINS_USER_TOKEN");
    private static final String JENKINS_USER_PASSWORD = initProperties("JENKINS_USER_PASSWORD");

    // 项目专属的Token, 所有项目都统一设置
    private static final String JENKINS_JOB_TOKEN = initProperties("JENKINS_JOB_TOKEN");


    // 每个项目都一样的Pipeline内容
    private static final String PIPELINE = "@Library('IntelliPipeline-Agent') _ \n" +
            "intelliPipelineProxy{}";


    /**
     * 读取配置文件对Jenkins配置项进行初始化
     * */
    public static String initProperties(String key){

        // 生成properties对象
        Properties pps = new Properties();
        try{
            URL url = Resources.getResource("JenkinsProperties.properties");
            InputStream in = new FileInputStream(url.getFile());
            pps.load(in);
            return pps.getProperty(key);

        } catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 驱动Jenkins开始执行构建, 从IntelliPipeline获取Step
     * @Param projectName - 项目名
     * @Param gitURL - git仓库地址
     * @return true
     * */
    public static boolean createProject(String projectName, String gitURL) throws Exception{

        // 新建项目的远程API的格式
        // String remoteUrl = "http://" + JENKINS_USERNAME + ":" + JENKINS_USER_TOKEN
        //         + "@" + JENKINS_HOST + "/createItem/?name=" + projectName;

        // 新建项目的URL格式
        String url = "http://" + JENKINS_HOST + "/createItem/?name=" + projectName;

        URI uri = URI.create(url);

        HttpHost host = new HttpHost(uri.getHost(), uri.getPort(), uri.getScheme());

        CredentialsProvider credsProvider = new BasicCredentialsProvider();

        credsProvider.setCredentials(new AuthScope(uri.getHost(), uri.getPort()),
                new UsernamePasswordCredentials(JENKINS_USERNAME, JENKINS_USER_TOKEN));

        AuthCache authCache = new BasicAuthCache();

        BasicScheme basicAuth = new BasicScheme();

        authCache.put(host, basicAuth);

        CloseableHttpClient httpClient = HttpClients
                .custom()
                .setDefaultCredentialsProvider(credsProvider)
                .build();

        HttpClientContext localContext = HttpClientContext.create();

        localContext.setAuthCache(authCache);


        HttpPost httpPost = new HttpPost(url);

        httpPost.setHeader("Content-Type", "application/xml");

        // 读取预设的config.xml文件, 将其中的Pipeline脚本和gitURL进行替换
        String jenkinsConfig = getCustomConfig(gitURL, projectName);

        HttpEntity entity = new ByteArrayEntity(jenkinsConfig.getBytes("UTF-8"));

        httpPost.setEntity(entity);

        // 带有用户名密码的授权请求
        HttpResponse response = httpClient.execute(host, httpPost, localContext);

        // 获取Jenkins返回的新建项目的操作结果, 200 -> 创建成功返回true, else -> 创建失败返回false
        if(response.getStatusLine().getStatusCode() == 200) return true;
        else return false;

    }

    /**
     * 指定项目名执行远程构建
     * */
    public static boolean executeBuild(String projectName) throws Exception{

        // 进行构建的远程API的格式
        String url = "http://" + JENKINS_HOST + "/job/" + projectName + "/build";

        URI uri = URI.create(url);

        HttpHost host = new HttpHost(uri.getHost(), uri.getPort(), uri.getScheme());

        CredentialsProvider credsProvider = new BasicCredentialsProvider();

        credsProvider.setCredentials(new AuthScope(uri.getHost(), uri.getPort()),
                new UsernamePasswordCredentials(JENKINS_USERNAME, JENKINS_USER_TOKEN));

        AuthCache authCache = new BasicAuthCache();

        BasicScheme basicAuth = new BasicScheme();

        authCache.put(host, basicAuth);

        CloseableHttpClient httpClient = HttpClients
                .custom()
                .setDefaultCredentialsProvider(credsProvider)
                .build();

        HttpClientContext localContext = HttpClientContext.create();

        localContext.setAuthCache(authCache);


        HttpPost httpPost = new HttpPost(url);

        httpPost.setHeader("Content-Type", "application/xml");

        // 带有用户名密码的授权请求
        HttpResponse response = httpClient.execute(host, httpPost, localContext);

        if(response.getStatusLine().getStatusCode() == 201) return true;
        else return false;
    }

    /**
     * 输入gitURL, 从模板配置文件直接获取对应的config content
     * @Param gitURL - 项目GitHub地址
     * @return 新建Jenkins项目需要的config.xml内容
     * */
    public static String getCustomConfig(String gitURL, String projectName) throws Exception{

        // 使用dom4j读取和修改xml内容
        URL url = Resources.getResource("JenkinsConfig.xml");
        File configFile = new File(url.toURI());
        Document doc = new SAXReader().read(configFile);

        // 修改pipeline脚本值
        doc.selectSingleNode("//definition/script").setText(PIPELINE);

        // 修改projectURL的值
        doc.selectSingleNode("//com.coravy.hudson.plugins.github.GithubProjectProperty/projectUrl").setText(gitURL);

        // 修改autoToken的值
        doc.selectSingleNode("//authToken").setText(projectName);

        // 把doc直接转化为String对象
        String jenkinsConfig = doc.asXML();

        return jenkinsConfig;
    }


    // TODO: 删除Jenkins上的项目
    public static boolean deleteProject(String projectName) throws Exception{
        return false;
    }

    /**
     * 获取控制台输出, 需要构建的序号
     * @Param projectName
     * @Param buildNumber
     * @return 状态码200表示成功
     * */
    public static String getConsoleOutput(String projectName, String buildNumber, String rootPath) throws Exception {
        String url = "http://" + JENKINS_HOST + "/job/" + projectName + "/" + buildNumber
                + "/logText/progressiveText?start=" + logOffset;

        URI uri = URI.create(url);

        HttpHost host = new HttpHost(uri.getHost(), uri.getPort(), uri.getScheme());

        CredentialsProvider credsProvider = new BasicCredentialsProvider();

        credsProvider.setCredentials(new AuthScope(uri.getHost(), uri.getPort()),
                new UsernamePasswordCredentials(JENKINS_USERNAME, JENKINS_USER_TOKEN));

        AuthCache authCache = new BasicAuthCache();

        BasicScheme basicAuth = new BasicScheme();

        authCache.put(host, basicAuth);

        CloseableHttpClient httpClient = HttpClients
                .custom()
                .setDefaultCredentialsProvider(credsProvider)
                .build();

        HttpClientContext localContext = HttpClientContext.create();

        localContext.setAuthCache(authCache);

        HttpGet httpGet = new HttpGet(url);

        httpGet.setHeader("Accept", "text/html, application/xhtml+xml, application/xml");

        HttpResponse response = httpClient.execute(host, httpGet, localContext);

        HttpEntity entity = response.getEntity();

        String consoleContent = EntityUtils.toString(entity, "UTF-8");

        // 获得每次开头的地方, 这样每个step的控制台输出就能各自识别了
        String s = response.getHeaders("X-Text-Size")[0].toString();
        String[] s1 = s.split(":");
        logOffset = s1[1].trim();

        File logFile = new File(rootPath + "LocalRepo/" + projectName + "/build-" + buildNumber + ".log");

        // 不存在, 新建文件
        if(!logFile.exists()){
            File dir = new File(logFile.getParent());
            dir.mkdirs();
            logFile.createNewFile();
        }

        FileWriter writer = new FileWriter(logFile, true);

        writer.write(consoleContent);

        writer.close();

        // logFile: /home/workplace/LocalGitHubRepo/$projectName/build-$buildNumber
        // src/main/webapp/resources/LocalRepo/$projectName/build-$buildNumber
        String filePath = logFile.getAbsolutePath();
        logger.info("FileName and Project: " + filePath);
        return filePath.substring(filePath.indexOf("LocalRepo"));
    }

    // public static void main(String[] args) throws Exception{
    //     initProperties("JENKINS_HOST");
    // }
}
