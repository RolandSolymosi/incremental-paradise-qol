package com.incrementalqol.common.utils;

import com.incrementalqol.config.Config;
import org.slf4j.Logger;

public class ConfiguredLogger {

    public static void LogInfo(Logger logger, String message){
        if (Config.HANDLER.instance().getLoggingEnabled()){
            logger.info(message);
        }
    }

    public static void LogError(Logger logger, String message){
        logger.error(message);
    }
}
