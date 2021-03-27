package app.utils;
import java.util.logging.*;

/**
 * A decorator for the logger
 */
public class AppLogger {
  private Logger logger;
  private Boolean debugMode;
  // hardcoding the Level type for now
  private Level logLevel = Level.INFO;
  
  /**
   * The constructor for the AppLogger
   * @param isDebugMode if true, the logger will print, else will not print
   * @param name the name for the logger - String
   */
  public AppLogger(Boolean isDebugMode, String name) {
    logger = Logger.getLogger(name);
    debugMode = isDebugMode;
  }

  /**
   * The log method will call the logger's log method if we are in debig mode
   * otherwise do nothing
   * @param message the message to be logged
   */
  public void log(String message){
    if(Boolean.TRUE.equals(debugMode)) 
      logger.log(logLevel, message);
  }

  /**
   * Overloaded method for log with two parameters
   * @param message the message to be logged
   */
  public void log(String message, Level customLogLevel){
    if(Boolean.TRUE.equals(debugMode)) 
      logger.log(customLogLevel, message);
  }
}
