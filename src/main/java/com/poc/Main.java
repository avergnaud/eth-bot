package com.poc;

import org.wildfly.swarm.Swarm;

public class Main {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		// Instantiate the container
        Swarm swarm = new Swarm();

        // Create one or more deployments
//        JAXRSArchive deployment = ShrinkWrap.create(JAXRSArchive.class);
//        ...

        // Add resource to deployment
//        deployment.addClass(MyResource.class);

        System.out.println("########################$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
        
        swarm.start().deploy();
//        swarm.deploy(deployment);
	}

}