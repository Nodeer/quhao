package com.withiter.test;

public class SychronizedStaticMethodTest {

	public synchronized static void method(String threadId) {
		for (int i = 0; i < 100; i++) {
			System.out.println(threadId + "_" + i);
		}
	}

	public static void main(String[] args) {
		for (int i = 0; i < 5; i++) {
			new Thread() {
				public void run() {
					SychronizedStaticMethodTest.method(this.getName());
				}
			}.start();
		}
	}
}
