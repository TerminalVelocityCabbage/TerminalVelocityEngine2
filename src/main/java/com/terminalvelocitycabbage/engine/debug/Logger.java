package com.terminalvelocitycabbage.engine.debug;

import org.fusesource.jansi.AnsiConsole;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.zip.DeflaterOutputStream;

public class Logger {

    private final String namespace;
    private ArrayList<LogMessage> messageQueue;

    private Date date;

    private boolean showTimestamp = false;
    private boolean showAdditionalInfo = true;
    private boolean debugMode = false;
    private boolean showNamespace = false;

    public Logger(String namespace) {
        this.namespace = namespace;
        this.messageQueue = new ArrayList<>();
        this.date = new Date(System.currentTimeMillis());
    }

    protected void queueMessage(LogLevel logLevel, String message) {
        messageQueue.add(new LogMessage(logLevel, message, showNamespace));
    }

    protected void queueAndPrint(LogLevel level, String message, boolean showLevel) {
        if (level == LogLevel.DEBUG && !debugMode) return;
        queueMessage(level, message);
        date.setTime(System.currentTimeMillis());
        messageQueue.get(messageQueue.size() - 1).printToConsole(date, namespace, showLevel, showTimestamp);
    }

    protected void queueAndPrint(LogLevel level, String message) {
        queueAndPrint(level, message, true);
    }

    protected void queueAndPrint(LogLevel level, String message, String additionalInfo) {
        queueAndPrint(level, message, true);
        if (showAdditionalInfo || debugMode) {
            queueAndPrint(level, additionalInfo, true);
        }
    }

    private void queueAndPrintThrowable(LogLevel level, String message, Throwable throwable) {
        queueMessage(level, message);
        Arrays.stream(throwable.getStackTrace()).sequential().forEach(stackTraceElement -> queueMessage(level, stackTraceElement.toString()));
        throwable.printStackTrace(AnsiConsole.err());
    }

    protected void queueAndPrint(LogLevel level, String message, Throwable throwable) {
        queueAndPrintThrowable(level, message, throwable);
        date.setTime(System.currentTimeMillis());
        messageQueue.get(messageQueue.size() - 1).errorToConsole(date, namespace, false, showNamespace);
    }

    protected void queueAndPrint(LogLevel level, String message, String additionalInfo, Throwable throwable) {
        queueAndPrintThrowable(level, message, throwable);
        date.setTime(System.currentTimeMillis());
        messageQueue.get(messageQueue.size() - 1).errorToConsole(date, namespace, false, showNamespace);
        if (showAdditionalInfo || debugMode) {
            queueAndPrint(level, additionalInfo, true);
        }
    }

    public Logger timestamp(boolean showTimestamp) {
        this.showTimestamp = showTimestamp;
        return this;
    }

    public Logger additionalInfo(boolean showAdditionalInfo) {
        this.showAdditionalInfo = showAdditionalInfo;
        return this;
    }

    public Logger debugMode(boolean debugMode) {
        this.debugMode = debugMode;
        return this;
    }

    public Logger showNamespace(boolean showNamespace) {
        this.showNamespace = showNamespace;
        return this;
    }

    public void createLog(boolean crash) {

        //The location of the latest log file
        Path latest = Paths.get("logs" + File.separator + "latest.log");

        //If this is a crash log we want to create a new file in a different directory
        if (crash) {
            Log.error("oops something went wrong.");

            //Print the system information
            Log.info(SystemInfo.getAvailableProcessors() + " logical processors");
            Log.info(Math.round(SystemInfo.getFreeMemory()/256.0/102.4) / 10d + "GB free of "
                    + Math.round(SystemInfo.getMaxMemory()/256.0/102.4) /10d + "GB system memory. limit: "
                    + SystemInfo.getAllocatedMemory() + "GB");
            Log.info(SystemInfo.getArchitecture());
            Log.info(SystemInfo.getOSName() + " version "
                    + SystemInfo.getOSVersion());
            Log.info(SystemInfo.getGpuVendor() + " model "
                    + SystemInfo.getGpuModel() + " version");
            Log.info(SystemInfo.getGpuVersion());
        }

        //Check to see if there already exists a latest.log and if so back it pu
        if (Files.exists(latest)) {

            //The old latest log
            File oldLog = latest.toFile();
            //Destination for the backpu log
            File backedUpLog = new File("logs" + File.separator + System.currentTimeMillis() + ".logbackup");

            try {
                FileInputStream fileToCompress = new FileInputStream(oldLog);
                FileOutputStream fileOutputStream = new FileOutputStream(backedUpLog);

                //The file deflater (to compress the file)
                DeflaterOutputStream deflater = new DeflaterOutputStream(fileOutputStream);

                //Write the old log data to the backup file
                int data;
                while ((data = fileToCompress.read()) != -1) {
                    deflater.write(data);
                }
                fileToCompress.close();
                deflater.close();

                //Delete the old latest.log file
                latest.toFile().delete();
            } catch (FileNotFoundException e) {
                Log.crash("Could not create compressed backup file", e);
            } catch (IOException e) {
                Log.crash("Could not read original file", e);
            }
        }

        //Create a new latest log file
        File logFile = Paths.get("logs" + File.separator + "latest.log").toFile();
        try {
            //Make sure that the logs directory exists
            logFile.getParentFile().mkdirs();
            //Create a new file
            boolean createSuccess = logFile.createNewFile();
            if (!createSuccess) {
                Log.error("Could not create log file");
                return;
            }
            //Write the new file nicely
            FileWriter fileWriter = new FileWriter(logFile);
            for (LogMessage message : messageQueue) {
                fileWriter.write(message.toString() + System.lineSeparator());
            }
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            Log.error("Could not write latest log file" + e);
        }
    }
}
