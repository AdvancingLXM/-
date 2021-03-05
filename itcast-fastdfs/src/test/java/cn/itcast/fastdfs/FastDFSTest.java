package cn.itcast.fastdfs;

import org.csource.fastdfs.*;
import org.junit.Test;

public class FastDFSTest {

    @Test
    public void test() throws Exception {

        //配置文件的绝对路径
        String conf_filename = ClassLoader.getSystemResource("fastdfs/tracker.conf").getPath();

        //设置全局的配置信息
        ClientGlobal.init(conf_filename);

        //创建trackerClient
        TrackerClient trackerClient = new TrackerClient();

        //创建trackerServer
        TrackerServer trackerServer = trackerClient.getConnection();

        //创建storageServer
        StorageServer storageServer = null;


        //创建StorageClient
        StorageClient storageClient = new StorageClient(trackerServer, storageServer);


        /**
         * 返回的数组：
         * group1
         * M00/00/00/wKgMqFubiN6AKDE2AABw0se6LsY552.jpg
         */
        //上传图片
        String[] upload_file = storageClient.upload_file("D:\\itcast\\pics\\575968fcN2faf4aa4.jpg", "jpg", null);

        if (upload_file != null && upload_file.length > 0) {
            for (String str : upload_file) {
                System.out.println(str);
            }

            //获取存储服务器地址
            ServerInfo[] serverInfos = trackerClient.getFetchStorages(trackerServer, upload_file[0], upload_file[1]);
            for (ServerInfo serverInfo : serverInfos) {
                System.out.println("ip = " + serverInfo.getIpAddr() + ";port=" + serverInfo.getPort());
            }

            String url = "http://" + serverInfos[0].getIpAddr() + "/" + upload_file[0] + "/" + upload_file[1];
            System.out.println(url);
        }

    }
}
