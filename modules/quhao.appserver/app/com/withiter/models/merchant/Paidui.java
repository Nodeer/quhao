package com.withiter.models.merchant;

/**
 * Paidui Object contains below fields: <br>
 * public int currentNumber = 0;<br>
 * public int maxNumber = 0;<br>
 * public int canceled = 0;<br>
 * public int expired = 0;<br>
 * public int finished = 0;<br>
 * public boolean enable = false;
 *
 */

public class Paidui{
	public Paidui(int number, boolean b) {
		this.currentNumber = number;
		this.enable = b;
	}
	
	public Paidui() {
	}
	
	public int currentNumber = 0;
	public int maxNumber = 0;
	public int canceled = 0;
	public int expired = 0;
	public int finished = 0;
	public boolean enable = false;
}