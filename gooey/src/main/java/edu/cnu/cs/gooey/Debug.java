package edu.cnu.cs.gooey;

public class Debug {
	public  static boolean Me     = true;
	private static long    before = System.currentTimeMillis();
	private static long    now    = before;
	public static void Me(String label) {
		if (Me) {
			now = System.currentTimeMillis();
//			System.out.printf( "[%s][%s] %d {%4d}%n", label, Thread.currentThread().getName(), now, (now-before) );
			System.out.printf( "%d {%4d} [%33s][%s]%n", now, (now-before), Thread.currentThread().getName(), label );
			before = now;
		}
	}
}
