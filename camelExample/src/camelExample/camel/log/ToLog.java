package camelExample.camel.log;

import java.util.Date;

import org.apache.camel.ProducerTemplate;
import org.apache.camel.spring.Main;
import org.apache.log4j.PropertyConfigurator;

public class ToLog {
	public static void main(String[] args) throws Exception {

		String ctxPath = "camelExample/camel/log/ToLog.xml";
		// log4j.properties
		PropertyConfigurator.configure(
				"C:\\DevelopTools\\Eclipse\\Workspace\\GIT\\nettySample\\camelExample\\src\\camelExample\\log4j.properties");

		Main main = new Main();
		main.setApplicationContextUri(ctxPath);

		// Camel 컨텍스트 실행
		main.start();

		// Camel 생산자(발신자) 객체 획득
		ProducerTemplate producer = main.getCamelTemplate();

		// 메시지 발신
		producer.requestBody("direct:start", "Hello World! at " + new Date());

		// Camel 컨텍스트 종료
		main.stop();

	}
}
