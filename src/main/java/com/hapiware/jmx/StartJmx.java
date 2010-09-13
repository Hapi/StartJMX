package com.hapiware.jmx;

import java.io.File;
import java.io.IOException;

import com.sun.tools.attach.AgentInitializationException;
import com.sun.tools.attach.AgentLoadException;
import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;


/**
 * Starts a JMX agent of the target JVM on the run.
 * </p>
 * <b>NOTICE!</b> Works only on Java 1.6 or later.
 * 
 * @author hapi
 *
 */
public class StartJmx
{
	private static final String LOCAL_CONNECTOR_ADDRES =
		"com.sun.management.jmxremote.localConnectorAddress"; 


	public static void main(String[] args)
	{
		if(args.length == 0 || args.length > 2)
			usageAndExit(0);
		
		if(
			args[0].equalsIgnoreCase("-?") ||
			args[0].equalsIgnoreCase("-h") ||
			args[0].equalsIgnoreCase("-help") ||
			args[0].equalsIgnoreCase("--help")
		)
			usageAndExit(0);
		
		try {
			Integer pid = Integer.valueOf(args[0]);
			VirtualMachine vm = VirtualMachine.attach(pid.toString());
			String connectionAddress = vm.getAgentProperties().getProperty(LOCAL_CONNECTOR_ADDRES);
			if(connectionAddress == null)
				vm.loadAgent(
					args.length == 2 ?
						args[1]
						: vm.getSystemProperties().getProperty("java.home")
							+ File.separator + "lib" + File.separator + "management-agent.jar"
				);
			vm.detach();
			System.out.println("JMX agent for " + pid + " started succesfully.");
		}
		catch(NumberFormatException ex) {
			System.out.println(args[0] + " was not recognized as PID.");
			usageAndExit(-1);
		}
		catch(AttachNotSupportedException e) {
			e.printStackTrace();
		}
		catch(IOException e) {
			e.printStackTrace();
		}
		catch(AgentLoadException e) {
			e.printStackTrace();
		}
		catch(AgentInitializationException e) {
			e.printStackTrace();
		}
	}
	
	
	private static void usageAndExit(int status)
	{
		final String startjmx = "java -jar startjmx.jar";
		System.out.println("Description: Starts a JMX agent of the target JVM on the run. Works only on Java 1.6 or later.");
		System.out.println();
		System.out.println("Usage: " + startjmx + " [-? | -h | -help | --help]");
		System.out.println("       " + startjmx + " PID [MANAGEMENTJAR]");
		System.out.println();
		System.out.println("       PID");
		System.out.println("          PID for the JVM which JMX agent is to be started.");
		System.out.println();
		System.out.println("       MANAGEMENTJAR");
		System.out.println("          Optional. A path to management-agent.jar.");
		System.out.println("          Default is $JAVA_HOME/lib/management-agent.jar.");
		System.out.println();
		System.out.println("Examples:");
		System.out.println("    " + startjmx + " -?");
		System.out.println("    " + startjmx + " 50001");
		System.out.println("    " + startjmx + " 50001 /usr/lib/jvm/jre-1.6.0-openjdk/lib/management-agent.jar");
		System.out.println();
		System.exit(status);
	}
}
