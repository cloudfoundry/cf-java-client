package com.springdeveloper.test;

import java.util.HashMap;
import java.util.UUID;

public class CrashBean {

	private HashMap<Object, Object> bloat = new HashMap<Object, Object>();

	public CrashBean() {
		String crash = System.getenv("crash");
		if ("true".equals(crash)) {
			while (true) {
				bloat.put(UUID.randomUUID(), new byte[999999999]);
			}
		}
	}
}
