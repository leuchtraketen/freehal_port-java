package net.freehal.plugin.berkeleydb;

import java.io.File;

import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;

public class Shrink {
	public static void main(String[] args) {
		for (String argument : args) {
			File dir = new File(argument);
			if (dir.exists() && dir.isDirectory()) {
				System.out.println("compressing database: " + dir);
				final EnvironmentConfig envConfig = new EnvironmentConfig();
				envConfig.setTransactional(true);
				envConfig.setAllowCreate(true);
				envConfig.setConfigParam(EnvironmentConfig.CLEANER_MIN_UTILIZATION, "50");
				Environment env = new Environment(dir, envConfig);
				try {
					env.cleanLog();
					env.compress();
				} catch (DatabaseException ex) {
					ex.printStackTrace();
				}
				env.close();

			} else {
				System.out.println("database not found: " + dir);
			}
		}
	}
}
