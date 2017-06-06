package edu.cnu.cs.gooey.utils;

public class Debug {
	public  static boolean ALLOWED = true;
	private static long    before  = System.currentTimeMillis();
	private static long    now     = before;
	public static void it(String label) {
		if (ALLOWED) {
			now    = System.currentTimeMillis();
			System.out.printf( "%d {%5d} [%20s][%s]%n", now, (now-before), Thread.currentThread().getName(), label );
			before = now;
		}
	}
}
