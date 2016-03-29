package com.lucare.test.common;

import com.lucare.common.utils.CommonUtils;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import java.io.InputStream;
import java.io.StringWriter;
import java.util.Properties;

public class TemplateManager {

	public static final String serverDomain = "http://www.zhongbin.org";

	public static void main(String[] args) throws Exception {
		TemplateManager.start();
		VelocityContext context = new VelocityContext();
		context.put("name", "张三");
		String s = TemplateManager.mergeTemplate("demo.vm", context);
		System.out.println(s);
	}

	private static boolean started = false;

	public static synchronized void start() {
		try {
			Properties prop = new Properties();
			InputStream in = CommonUtils.getInputStreamFromClassPath("velocity.properties");
			prop.load(in);
			Velocity.init(prop);
			started = true;
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

	public static boolean isRunning() {
		return started;
	}

	/**
	 * 返回模板加载后的字符串
	 * 
	 * @param fileName - 如 index.vm
	 * @param context
	 * @return
	 */
	public static String mergeTemplate(String fileName, VelocityContext context) {
		StringWriter writer = new StringWriter();
		Velocity.getTemplate(fileName, "UTF-8").merge(context, writer);
		return writer.toString();
	}
}
