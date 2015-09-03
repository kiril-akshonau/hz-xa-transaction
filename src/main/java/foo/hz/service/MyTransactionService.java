package foo.hz.service;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.TransactionalQueue;
import com.hazelcast.transaction.TransactionContext;
import com.hazelcast.transaction.TransactionOptions;
import com.hazelcast.transaction.TransactionOptions.TransactionType;

import foo.hz.domain.BusinessObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;
import javax.transaction.xa.XAResource;

@Service
public class MyTransactionService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MyTransactionService.class);
    private static final TransactionOptions OPTIONS = new TransactionOptions()
            .setTransactionType(TransactionType.TWO_PHASE);

    @Resource(name = "jtaUserTransactionManager")
    private TransactionManager tm;
    @Resource(name = "hzInstanceZone")
    private HazelcastInstance hz;

    public BusinessObject peek() throws Throwable {
        BusinessObject businessObject = null;

        tm.begin();

        TransactionContext txContext = hz.newTransactionContext(OPTIONS);
        XAResource xaResource = txContext.getXaResource();

        Transaction transaction = tm.getTransaction();
        transaction.enlistResource(xaResource);

        try {
            TransactionalQueue<BusinessObject> queue = txContext.getQueue("businessObjectQueue");

            businessObject = queue.peek();

            transaction.delistResource(xaResource, XAResource.TMSUCCESS);
            tm.commit();
        } catch (Exception e) {
            LOGGER.error("Error", e);
            tm.rollback();
        }
        return businessObject;
    }

    public void offer(BusinessObject businessObject) throws Throwable {
        tm.begin();

        TransactionContext txContext = hz.newTransactionContext(OPTIONS);
        XAResource xaResource = txContext.getXaResource();

        Transaction transaction = tm.getTransaction();
        transaction.enlistResource(xaResource);

        try {
            TransactionalQueue<BusinessObject> queue = txContext.getQueue("businessObjectQueue");

            queue.offer(businessObject);

            transaction.delistResource(xaResource, XAResource.TMSUCCESS);
            tm.commit();
        } catch (Exception e) {
            LOGGER.error("Error", e);
            tm.rollback();
        }
    }

    public void pollAndExit() throws Throwable {
        tm.begin();

        TransactionContext txContext = hz.newTransactionContext(OPTIONS);
        XAResource xaResource = txContext.getXaResource();

        Transaction transaction = tm.getTransaction();
        transaction.enlistResource(xaResource);

        try {
            TransactionalQueue<BusinessObject> queue = txContext.getQueue("businessObjectQueue");

            queue.poll();
            Thread.sleep(3000);

            System.exit(-1);
        } catch (Exception e) {
            LOGGER.error("Error", e);
            tm.rollback();
        }
    }
}
