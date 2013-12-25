package itti.com.pl.arena.cm.utils.helpers;

import itti.com.pl.arena.cm.utils.helpers.LogHelper;

import org.junit.Test;

public class LogHelperTest {

	private static final String METHOD_NAME = "errorMethod";
	private static final String MESSAGE = "Some error";
	
	@Test
	public void errorTest()
	{
		LogHelper.error(this.getClass(), METHOD_NAME, MESSAGE);
	}

	@Test
	public void warningTest()
	{
		LogHelper.warning(this.getClass(), METHOD_NAME, MESSAGE);
	}

	@Test
	public void infoTest()
	{
		LogHelper.info(this.getClass(), METHOD_NAME, MESSAGE);
	}

	@Test
	public void debugTest()
	{
		LogHelper.debug(this.getClass(), METHOD_NAME, MESSAGE);
	}

	@Test
	public void exceptionTest()
	{
		LogHelper.exception(this.getClass(), METHOD_NAME, MESSAGE, new RuntimeException("exception"));
	}
}
