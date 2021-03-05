package cn.itcast.freemarker;

import freemarker.template.Configuration;
import freemarker.template.Template;
import org.junit.Test;

import java.io.FileWriter;
import java.util.*;

public class FreemarkerTest {

    /**
     * 原理：
     * 模版+数据=输出
     */
    @Test
    public void test() throws Exception {
        //创建一个freemarker配置对象
        Configuration configuration = new Configuration(Configuration.getVersion());
        //设置模版路径;参数1：模版类路径，参数2：具体的路径
        configuration.setClassForTemplateLoading(FreemarkerTest.class, "/ftl");
        //设置生成后文件内容编码
        configuration.setDefaultEncoding("utf-8");

        //获取模版
        Template template = configuration.getTemplate("test.ftl");

        //获取数据
        Map<String, Object> dataModel = new HashMap<>();
        dataModel.put("name", "黑马");
        dataModel.put("msg", "欢迎使用Freeemarker");

        List<Map<String, Object>> goodsList = new ArrayList<>();
        Map<String, Object> map1 = new HashMap<>();
        map1.put("name", "苹果");
        map1.put("price", "4.5");
        goodsList.add(map1);

        Map<String, Object> map2 = new HashMap<>();
        map2.put("name", "柚子");
        map2.put("price", "6");
        goodsList.add(map2);

        dataModel.put("goodsList", goodsList);

        dataModel.put("today", new Date());

        dataModel.put("number", 123456789L);


        //创建输出的对象
        FileWriter fileWriter = new FileWriter("D:\\itcast\\test\\test.html");
        //输出
        template.process(dataModel, fileWriter);

        fileWriter.close();

    }
}
