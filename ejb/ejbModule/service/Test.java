package service;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class Test {

	public Test() {
		System.out.println("mytest");
	}

	public void doit() {
		System.out.println("mytest doit");
	}

}
