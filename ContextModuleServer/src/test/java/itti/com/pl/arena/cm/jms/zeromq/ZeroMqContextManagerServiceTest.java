package itti.com.pl.arena.cm.jms.zeromq;

import itti.com.pl.arena.cm.service.jms.ContextModuleZeroMqService;

import org.junit.Test;

public class ZeroMqContextManagerServiceTest {

	@Test
	public void connectTest(){
		ContextModuleZeroMqService service = new ContextModuleZeroMqService();
		service.setBrokerUrl("127.0.0.1");
		service.setConnectionPort("44554");
		service.init();
		service.shutdown();
	}
}
