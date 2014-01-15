package itti.com.pl.arena.cm.jms.zeromq;

import itti.com.pl.arena.cm.service.jms.ContextModuleJmsService;

import org.junit.Test;

public class ZeroMqContextManagerServiceTest {

    @Test
    public void connectTest() {
        ContextModuleJmsService service = new ContextModuleJmsService();
        service.setBrokerUrl("127.0.0.1");
        service.setConnectionPort("44554");
        service.init();
        service.shutdown();
    }
}
