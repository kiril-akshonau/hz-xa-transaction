package foo.hz.app;

import com.atomikos.icatch.config.UserTransactionServiceImp;

import foo.hz.domain.BusinessObject;
import foo.hz.service.InstanceService;
import foo.hz.service.MyTransactionService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class OfferAndPollApplication {

    private static final Logger LOGGER = LoggerFactory.getLogger(OfferAndPollApplication.class);

    public static void main(String[] args) throws Throwable {
        System.setProperty(UserTransactionServiceImp.FILE_PATH_PROPERTY_NAME, "jta_offer.properties");

        try (ConfigurableApplicationContext context = new ClassPathXmlApplicationContext("classpath:spring-all.xml")) {
            LOGGER.warn("Spring context is ready. Waiting for other application");
            context.getBean(InstanceService.class).waitForStart();
            LOGGER.warn("All application is up. Starts test");

            MyTransactionService transactionService = context.getBean(MyTransactionService.class);

            Thread.sleep(3000);
            BusinessObject businessObject = new BusinessObject("me");
            LOGGER.warn("Save new [{}] into queue", businessObject);
            transactionService.offer(businessObject);
            LOGGER.warn("Saved");

            Thread.sleep(3000);

            LOGGER.warn("Poll and System.exit(1)");
            transactionService.pollAndExit();
        }
    }
}
