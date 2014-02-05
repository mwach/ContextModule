package itti.com.pl.arena.cm.jms.zeromq;

import itti.com.pl.arena.cm.service.jms.ContextModuleJmsService;

import org.junit.Assert;
import org.junit.Test;

public class ZeroMqContextManagerServiceTest {

    @Test
    public void connectTest() {

        int MAX_WAITING_TIME = 10 * 1000;

        Runnable jmsTestThread = new Runnable() {

            @Override
            public void run() {
                ContextModuleJmsService service = new ContextModuleJmsService();
                service.setBrokerUrl("127.0.0.1");
                service.setConnectionPort("44554");
                try{
                    service.init();
                    service.shutdown();
                }catch(RuntimeException exc){
                    exc.printStackTrace();
                }
            }

        };
        Thread t = new Thread(jmsTestThread);
        t.start();
        int totalSleep = 0;
        
        while(t.isAlive() && totalSleep <= MAX_WAITING_TIME){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }
            totalSleep += 1000;
        }
        Assert.assertFalse(t.isAlive());
    }
}
