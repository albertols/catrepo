package com.parser.utils;

import com.log.LogEnum;
import com.log.Logger;

public class Sleep
{
	public Sleep(long miliseconds, String thName)
	{
		try
		{
			Thread.sleep(miliseconds);
		}
		catch (InterruptedException e)
		{
			Logger.log(LogEnum.ERROR, "Sleeping thread="+thName);
			e.printStackTrace();
		}
	}

}
