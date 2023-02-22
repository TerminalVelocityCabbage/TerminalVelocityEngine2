package com.terminalvelocitycabbage.engine.debug;

import com.terminalvelocitycabbage.engine.client.ClientBase;
import com.terminalvelocitycabbage.engine.server.ServerBase;

public class Log {

	static LoggerSource source;

	private static boolean testAndFallback(String toPrint) {
		boolean cont = true;
		var clientBase = ClientBase.getInstance();
		var serverBase = ServerBase.getInstance();
		if (clientBase == null && serverBase == null) {
			System.out.println("[Logger] Printing with system out instead of with logger since sided entrypoint instance is null");
			cont = false;
		}
		source = clientBase == null ? serverBase : clientBase;
		if (cont && source.getLogger() == null) {
			System.out.println("[Logger] Printing with system out instead of with logger since logger instance is null");
			cont = false;
		}
		if (!cont) {
			System.out.println(toPrint);
		}
		return cont;
	}

	public static void info(Object message) {
		if (testAndFallback(LogLevel.INFO.prefix + " " + message)) {
			source.getLogger().queueAndPrint(LogLevel.INFO, String.valueOf(message));
		}
	}

	public static void info(Object message, String additionalInfo) {
		if (testAndFallback(LogLevel.INFO.prefix + " " + message + " " + additionalInfo)) {
			source.getLogger().queueAndPrint(LogLevel.INFO, String.valueOf(message), additionalInfo);
		}
	}

	public static void warn(Object message) {
		if (testAndFallback(LogLevel.WARN.prefix + " " + message)) {
			source.getLogger().queueAndPrint(LogLevel.WARN, String.valueOf(message));
		}
	}

	public static void warn(Object message, String additionalInfo) {
		if (testAndFallback(LogLevel.WARN.prefix + " " + message + " " + additionalInfo)) {
			source.getLogger().queueAndPrint(LogLevel.WARN, String.valueOf(message), additionalInfo);
		}
	}

	public static void error(Object message) {
		if (testAndFallback(LogLevel.ERROR.prefix + " " + message)) {
			source.getLogger().queueAndPrint(LogLevel.ERROR, String.valueOf(message));
		}
	}

	public static void error(Object message, String additionalInfo) {
		if (testAndFallback(LogLevel.ERROR.prefix + " " + message + " " + additionalInfo)) {
			source.getLogger().queueAndPrint(LogLevel.ERROR, String.valueOf(message), additionalInfo);
		}
	}

	public static void debug(Object message) {
		if (testAndFallback(LogLevel.DEBUG.prefix + " " + message)) {
			source.getLogger().queueAndPrint(LogLevel.DEBUG, String.valueOf(message));
		}
	}

	public static void debug(Object message, String additionalInfo) {
		if (testAndFallback(LogLevel.DEBUG.prefix + " " + message + " " + additionalInfo)) {
			source.getLogger().queueAndPrint(LogLevel.DEBUG, String.valueOf(message), additionalInfo);
		}
	}

	public static void crash(Object message, Throwable throwable) {
		crash(throwable.getMessage(), message, throwable);
	}

	public static void crash(String title, Object message, Throwable throwable) {
		if (testAndFallback(LogLevel.DEBUG.prefix + " " + title + " " + message)) {
			source.getLogger().queueAndPrint(LogLevel.CRASH, title, throwable);
			source.getLogger().createLog(true);
		}
		throw new RuntimeException(String.valueOf(message), throwable);
	}

	public static void crash(String title, Object message, String additionalInfo, Throwable throwable) {
		if (testAndFallback(LogLevel.DEBUG.prefix + " " + title + " " + message + " " + additionalInfo)) {
			source.getLogger().queueAndPrint(LogLevel.CRASH, title, additionalInfo, throwable);
			source.getLogger().createLog(true);
		}
		throw new RuntimeException(String.valueOf(message), throwable);
	}

	public static void close() {
		source.getLogger().createLog(false);
	}
}
