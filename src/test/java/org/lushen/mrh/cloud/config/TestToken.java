package org.lushen.mrh.cloud.config;

import java.util.StringTokenizer;

public class TestToken {

	public static void main(String[] args) {

		StringTokenizer tokenizer = new StringTokenizer("service-user-dev", "-", false);
		while(tokenizer.hasMoreTokens()) {
			System.out.println(tokenizer.nextToken());
		}

	}

}
