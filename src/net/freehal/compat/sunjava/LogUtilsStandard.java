package net.freehal.compat.sunjava;

import net.freehal.core.util.LogUtilsImpl;

public class LogUtilsStandard implements LogUtilsImpl {

	@Override
	public void e(String e) {
		System.out.println("error: " + e);
	}

	@Override
	public void w(String e) {
		System.out.println("warning: " + e);
	}

	@Override
	public void i(String e) {
		System.out.println("info: " + e);
	}

	@Override
	public void d(String e) {
		System.out.println("debug: " + e);
	}

}
