package io.rainbow.billiards.springboot;

import java.io.File;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {

	public static void main(String[] args) throws Exception {
		//SpringApplication.run(Application.class, args);
		//System.out.println("Hello world");
		
		Mp4Test mp4Test = new Mp4Test();
        File source = new File("E:\\movie\\NiceMiss1.mp4");        
        
        long startTime = System.currentTimeMillis();
        mp4Test.getThumbnail(source);
        long endTime = System.currentTimeMillis();
        System.out.println((endTime-startTime) + "(ms)");
	}
}
