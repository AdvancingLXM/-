package cn.itcast.redis;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/applicationContext-redis.xml")
public class RedisTest {

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 字符串测试
     */
    @Test
    public void testString() {
        redisTemplate.boundValueOps("str").set("i_am_jbl传智播客");
        Object obj = redisTemplate.boundValueOps("str").get();
        System.out.println(obj);
    }
    /**
     * 散列测试
     */
    @Test
    public void testHash() {
        redisTemplate.boundHashOps("h_key").put("f_1", "v_1");
        redisTemplate.boundHashOps("h_key").put("f_2", "v_2");
        //获取所有域对应的值
        Object obj = redisTemplate.boundHashOps("h_key").values();
        System.out.println(obj);
    }
    /**
     * 列表测试
     */
    @Test
    public void testList() {
        redisTemplate.delete("l_key");
        redisTemplate.boundListOps("l_key").leftPush("b");
        redisTemplate.boundListOps("l_key").leftPush("a");
        redisTemplate.boundListOps("l_key").rightPush("c");
        //起始、结束（-1表示最后一个元素）
        Object obj = redisTemplate.boundListOps("l_key").range(0, -1);
        System.out.println(obj);
    }
    /**
     * 集合测试
     */
    @Test
    public void testSet() {
        redisTemplate.delete("set_key");
        redisTemplate.boundSetOps("set_key").add("a", "c", 1, "b");

        Object obj = redisTemplate.boundSetOps("set_key").members();
        System.out.println(obj);
    }
    /**
     * 有序集合测试
     * 每个元素都是有一个分数的，集合的顺序按照分数大小升序排序
     */
    @Test
    public void testSortedSet() {
        redisTemplate.delete("z_key");
        redisTemplate.boundZSetOps("z_key").add("b", 20);
        redisTemplate.boundZSetOps("z_key").add("a", 30);
        redisTemplate.boundZSetOps("z_key").add("c", 10);
        Object obj = redisTemplate.boundZSetOps("z_key").range(0, -1);
        System.out.println(obj);
    }
}
