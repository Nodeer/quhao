package com.withiter.models.merchant;

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