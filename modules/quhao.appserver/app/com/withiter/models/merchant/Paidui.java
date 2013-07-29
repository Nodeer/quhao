package com.withiter.models.merchant;

public class Paidui{
	public Paidui(int number, boolean b) {
		this.currentWait = number;
		this.enable = b;
	}
	
	public Paidui() {
		
	}
	
	public int currentWait = 0;
	public boolean enable = false;
}