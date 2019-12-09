package com.zzm.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
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
public class test {
	
	@Autowired
	private RedisTemplate<String, Object> redisTemplate;
	
	List<User> userList = new ArrayList<>();

	@Before
	public void testDate(){
		for (int i = 0; i < 100000; i++) {
			User user = new User();
			user.setId(i+1);
			
			//随机姓名
			String randomChinese = StringUtils.getRandomChinese(3);
			user.setName(randomChinese);
			
			//随机性别
			Random random = new Random();
			String sex = random.nextBoolean()?"男":"女";
			user.setSex(sex);
			
			//随机手机号
			String phone = "13"+StringUtils.getRandomNumber(9);
			user.setPhone(phone);
			
			//随机邮箱
			int email = (int)(Math.random()*20);
			int len = email<3?email+3:email;
			String randomStr = StringUtils.getRandomStr(len);
			String randomEmailSuffex = StringUtils.getRandomEmailSuffex();
			user.setEmail(randomStr+randomEmailSuffex);
			
			//随机生日
			String randomBirthday = StringUtils.randomBirthday();
			user.setBirthday(randomBirthday);
			
			userList.add(user);
		}
	}
	
	
	@Test
	public void test(){
		System.out.println("JDK的序列化方法.......");
		long start = System.currentTimeMillis();
		BoundListOperations<String, Object> boundListOps = redisTemplate.boundListOps("JDk");
		long leftPush = 0L;
		for (User user : userList) {
			leftPush = boundListOps.leftPush(user);
		}
		long end = System.currentTimeMillis();
		System.out.println("保存数量:"+leftPush);
		System.out.println("所耗时间:"+(end-start)+"毫秒");
		
		
		System.out.println("JSON的序列化方法.......");
		long start2 = System.currentTimeMillis();
		BoundListOperations<String, Object> boundListOps2 = redisTemplate.boundListOps("JSON");
		long leftPush2 = 0L;
		for (User user : userList) {
			leftPush2 = boundListOps2.leftPush(user);
		}
		long end2 = System.currentTimeMillis();
		System.out.println("保存数量:"+leftPush2);
		System.out.println("所耗时间:"+(end2-start2)+"毫秒");
		
		
		System.out.println("Hash的序列化方法.......");
		long start3 = System.currentTimeMillis();
		BoundHashOperations<String, Object, Object> boundHashOps = redisTemplate.boundHashOps("Hash");
//		long leftPush3 = 0L;
		for (User user : userList) {
			boundHashOps.put("user",userList.size());			
		}
//		boundHashOps.put("user", userList);
		long end3 = System.currentTimeMillis();
		System.out.println("保存数量:100000");
		System.out.println("所耗时间:"+(end3-start3)+"毫秒");
	}
	
}
