class MyMessageAppender<E> extends RollingFileAppender<E> {
 private int lastRolloverMesc = 0;
 private int FIVE_MINUTE = 5 * 60 * 1000;
   @Override
   public void rollover() {
     int mesc = Calendar.getInstance().getTimeInMillis();
     
     if(lastRolloverMesc == 0) {
       lastRolloverMesc = mesc;
     }
     
     if(mesc - lastRolloverMesc >= FIVE_MINUTE) {
      super.rollover();
      lastRolloverMesc = mesc;
     }
   }
}

scan("60 seconds")

def LOG_HOME = System.getProperty("log.home");
def CONF_PATH = System.getProperty("conf.path");

//println("serverId=${serverId}");

appender("stdout", ConsoleAppender) {
	encoder(PatternLayoutEncoder) {
		pattern = "%d{yyyyMMdd-HH:mm:ss} %-5p [%t] %class:%line %m%n"
	}
}

appender("fileLog", RollingFileAppender) {
	append = true
	threshold = ERROR
	file = "${LOG_HOME}/log/info.log"
	encoding = "UTF-8"
	encoder(PatternLayoutEncoder) {
		pattern = "%d{yyyyMMdd-HH:mm:ss} %-5p [%t] %class{5}:%line %m%n"
	}
	rollingPolicy(FixedWindowRollingPolicy) {
		fileNamePattern = "${LOG_HOME}/log/info.%i.log.zip"
		minIndex = 1
		maxIndex = 50
	}
	triggeringPolicy(SizeBasedTriggeringPolicy) {
		maxFileSize = "100MB"
	}
}

appender("charLog", RollingFileAppender) {
	append = true
	threshold = ERROR
	file = "${LOG_HOME}/log/char.log"
	encoding = "UTF-8"
	encoder(PatternLayoutEncoder) {
		pattern = "%d{yyyyMMdd-HH:mm:ss} %-5p [%t] %class{5}:%line %m%n"
	}
	rollingPolicy(FixedWindowRollingPolicy) {
		fileNamePattern = "${LOG_HOME}/log/char.%i.log.zip"
		minIndex = 1
		maxIndex = 50
	}
	triggeringPolicy(SizeBasedTriggeringPolicy) {
		maxFileSize = "100MB"
	}
}

appender("statdatalog", MyMessageAppender) {
	append = true
	file = "${LOG_HOME}/statdata/stat_"
	encoding = "UTF-8"
	encoder(PatternLayoutEncoder) { 
		pattern = "%m%n" 
	}
	
	rollingPolicy(TimeBasedRollingPolicy) { 
		fileNamePattern = "${LOG_HOME}/statdata/${serverId}_stat_%d{yyyyMMdd_HHmm'.log'}" 
	}
}

appender("gamelog", MyMessageAppender) {
	append = true
	file = "${LOG_HOME}/gamelog/game_"
	encoding = "UTF-8"
	encoder(PatternLayoutEncoder) {
		pattern = "%m%n"
	}
	
	rollingPolicy(TimeBasedRollingPolicy) {
		fileNamePattern = "${LOG_HOME}/gamelog/${serverId}_game_%d{yyyyMMdd_HHmm'.log'}"
	}
}

root(ERROR, ["stdout",  "fileLog"])
