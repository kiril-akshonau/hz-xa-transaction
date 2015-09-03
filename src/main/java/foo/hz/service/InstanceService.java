package foo.hz.service;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IAtomicLong;
import com.hazelcast.core.MembershipAdapter;
import com.hazelcast.core.MembershipEvent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;

@Service
public class InstanceService extends MembershipAdapter {

    private final IAtomicLong count;

    @Autowired
    public InstanceService(HazelcastInstance hz) {
        hz.getCluster().addMembershipListener(this);
        this.count = hz.getAtomicLong("COUNT");
    }

    @Override
    public void memberRemoved(MembershipEvent membershipEvent) {
        count.decrementAndGet();
    }

    public void waitForStart() throws InterruptedException {
        count.incrementAndGet();
        while (count.get() != 2) {
            Thread.sleep(1000);
        }
    }

    public long getCount() {
        return count.get();
    }
}
