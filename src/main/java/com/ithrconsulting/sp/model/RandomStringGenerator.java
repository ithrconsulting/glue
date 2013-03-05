package com.ithrconsulting.sp.model;

import java.util.Random;

public class RandomStringGenerator {
	private static final Random generator = new Random();

	public String generate(int length) {
		int base = (int) Math.pow(10, length-1);
		int number = generator.nextInt(9 * base) + base;
		String pin = String.valueOf(number);
		return pin;
	}
}
