package foo.hz.app;

import com.atomikos.icatch.config.UserTransactionServiceImp;

import foo.hz.domain.BusinessObject;
import foo.hz.service.InstanceService;
import foo.hz.service.MyTransactionService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class PeekApplication {

    private static final Logger LOGGER = LoggerFactory.getLogger(PeekApplication.class);

    public static void main(String[] args) throws Throwable {
        System.setProperty(UserTransactionServiceImp.FILE_PATH_PROPERTY_NAME, "jta_peek.properties");

        try (ConfigurableApplicationContext context = new ClassPathXmlApplicationContext("classpath:spring-all.xml")) {
            LOGGER.warn("Spring context is ready. Waiting for other application");
            MyTransactionService transactionService = context.getBean(MyTransactionService.class);
            InstanceService instanceService = context.getBean(InstanceService.class);
            instanceService.waitForStart();
            LOGGER.warn("All application is up. Starts test");

            while (instanceService.getCount() > 1) {
                LOGGER.warn("Peek of queue: [{}]", transactionService.peek());
                Thread.sleep(1000);
            }

            Thread.sleep(3000);

            BusinessObject businessObject = transactionService.peek();
            if (null == businessObject) {
                LOGGER.error("Object is null. Transaction is broken");
                throw new RuntimeException("Transaction is broken");
            } else {
                LOGGER.warn("Received object [{}] after destroying JVM. Transaction is OK", businessObject);
            }
        }
    }
}
