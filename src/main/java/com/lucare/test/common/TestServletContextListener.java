package com.lucare.test.common;

import com.lucare.invoke.InvokeManager;
import com.lucare.jedis.RedisProxyFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class TestServletContextListener implements ServletContextListener {

	private static final Logger logger = LoggerFactory.getLogger(TestServletContextListener.class);

	public void contextInitialized(ServletContextEvent event) {
		try {
			long begin = System.currentTimeMillis();
			long now = 0, current = 0;
			
			logger.info("====>[UserServer] contextInitialized begining.<===");

			// 1. InvokeManager启动
			now = begin;
			InvokeManager.start();
			current = System.currentTimeMillis();
			logger.info("[LOAD(InvokeManager)] cost={}ms.", current-now);
			
			// 2. 开启连接
			now = begin;
//			MongoProxyFactory.start();
			RedisProxyFactory.start();
			current = System.currentTimeMillis();
			logger.info("[LOAD(ConnectionResources)] cost={}ms.", current-now);

			// 3. 模版管理器启动
			now = begin;
			TemplateManager.start();
			current = System.currentTimeMillis();
			logger.info("[LOAD(TemplateManager)] cost={}ms.", current-now);

			// 4. 定时任务 先注释掉，软件没装
			now = current;
			//TaskContainer.start();
			current = System.currentTimeMillis();
			logger.info("[LOAD(TaskContainer)] cost={}ms.", current - now);
			
		} catch (Throwable e) {
			e.printStackTrace(); 
			logger.error("[UserServer] contextInitialized with:", e);
			System.exit(0);
		}
	}

	/**
	 * 服务器停止时，释放资源
	 */
	public void contextDestroyed(ServletContextEvent event) {
		logger.info("====>[UserServer] contextDestroyed beginning...");
		RedisProxyFactory.releaseProxy();
//		MongoProxyFactory.releaseProxy();
		logger.info("====>[UserServer] contextDestroyed end.<====");
	}

}
