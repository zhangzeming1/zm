package week2Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.omg.CORBA.PUBLIC_MEMBER;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.BoundListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.zzm.bean.User;
import com.zzm.utils.StringUtils;
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(value="classpath:applicationContext-redis.xml")
public class UserTest {

	@Autowired
	private RedisTemplate<String, Object> redisTemplate;
	
	List<User> userlist = new ArrayList<>();
	@Before
	public void testData(){
		for (int i = 0; i < 100000; i++) {
			User user = new User();
			user.setId(i+1);
			
			//随机中文汉字
			String randomChinese = StringUtils.getRandomChinese(3);
			user.setName(randomChinese);
			
			//随机的性别
			Random random = new Random();
			String sex = random.nextBoolean()?"男":"女";
			user.setSex(sex);
			
			//随机手机号
			String phone = "13"+StringUtils.getRandomNumber(9);
			user.setPhone(phone);
			
			//随机邮箱
			int random2 = ((int)(Math.random()*20));
			int len = random2<3?random2+3:random2;
			String randomStr = StringUtils.getRandomStr(len);
			String randomEmailSuffex = StringUtils.getRandomEmailSuffex();
			user.setEmail(randomStr+randomEmailSuffex);
			
			//随机生日  18-70
			String randomBirthday = StringUtils.randomBirthday();
			user.setBirthday(randomBirthday);
			
			userlist.add(user);
		}
	}
	
	@Test
	public void test(){
		/*JDK序列化方式*/
		System.out.println("JDK序列化方式");
		long start = System.currentTimeMillis();
		BoundListOperations<String, Object> boundListOps = redisTemplate.boundListOps("JDK");
		Long leftPush = 0L;
		/*for (User user : userlist) {
			leftPush = boundListOps.leftPush(user);
		}*/
		boundListOps.leftPush(userlist);
		long end = System.currentTimeMillis();
//		System.out.println("保存总数="+leftPush);
		System.out.println("耗时="+(end-start)+"毫秒");
		
		
		/*JSON的序列方法*/
		System.out.println("JSON的序列化方法");
		long start2 = System.currentTimeMillis();
		BoundListOperations<String, Object> boundListOps2 = redisTemplate.boundListOps("json");
		long leftPush2 = 0L;
		/*for (User user : userlist) {
			leftPush = boundListOps2.leftPush(user);
		}*/
		boundListOps.leftPush(userlist);
		long end2 = System.currentTimeMillis();
		//System.out.println("保存总数="+leftPush);
		System.out.println("耗时="+(end2-start2)+"毫秒");
		
		
		/*hash的序列化方法*/
		System.out.println("hash的序列化方法");
		long start3 = System.currentTimeMillis();
		BoundHashOperations<String, Object, Object> boundHashOps = redisTemplate.boundHashOps("hash");
		/*for (User user2 : userlist) {
			boundHashOps.put("hash", user2);
		}*/
		boundHashOps.put("hash", userlist);
		long end3 = System.currentTimeMillis();
		//System.out.println("保存总数="+leftPush);
		System.out.println("耗时="+(end3-start3)+"毫秒");
	}
	
	
}
