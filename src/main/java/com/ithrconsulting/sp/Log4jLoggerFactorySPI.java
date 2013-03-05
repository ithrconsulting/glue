package com.ithrconsulting.sp;

import java.util.Collection;

import org.xydra.log.ILogListener;
import org.xydra.log.ILoggerFactorySPI;
import org.xydra.log.Logger;

public class Log4jLoggerFactorySPI implements ILoggerFactorySPI {

	public Logger getLogger(String name, Collection<ILogListener> logListeners) {
		return new Log4jAdapter(org.apache.log4j.Logger.getLogger(name));
	}

	public Logger getThreadSafeLogger(String name, Collection<ILogListener> arg1) {
		return new Log4jAdapter(org.apache.log4j.Logger.getLogger(name));
	}

	public Logger getThreadSafeWrappedLogger(String name, String arg1) {
        throw new UnsupportedOperationException();
	}

	public Logger getWrappedLogger(String name, String fullyQualifiedNameOfDelegatingLoggerClass) {
        throw new UnsupportedOperationException();
	}

	private static class Log4jAdapter extends Logger {
		
		private org.apache.log4j.Logger log;
		
		public Log4jAdapter(org.apache.log4j.Logger log) {
			super();
			this.log = log;
		}

		@Override
		public boolean isDebugEnabled() {
			return log.isDebugEnabled();
		}

		@Override
		public boolean isErrorEnabled() {
			return log.isEnabledFor(org.apache.log4j.Level.ERROR);
		}

		@Override
		public boolean isInfoEnabled() {
			return log.isInfoEnabled();
		}

		@Override
		public boolean isTraceEnabled() {
			return log.isTraceEnabled();
		}

		@Override
		public boolean isWarnEnabled() {
			return log.isEnabledFor(org.apache.log4j.Level.WARN);
		}

		@Override
		public void trace(String msg) {
			log.trace(parse(msg));
		}

		@Override
		public void trace(String msg, Throwable t) {
			log.trace(parse(msg),t);
		}

		@Override
		public void debug(String msg) {
			log.debug(parse(msg));
		}

		@Override
		public void debug(String msg, Throwable t) {
			log.debug(parse(msg));
		}

		@Override
		public void info(String msg) {
			log.info(parse(msg));
		}

		@Override
		public void info(String msg, Throwable t) {
			log.info(parse(msg),t);
		}

		@Override
		public void warn(String msg) {
			log.warn(parse(msg));
		}

		@Override
		public void warn(String msg, Throwable t) {
			log.warn(parse(msg),t);
		}

		@Override
		public void error(String msg) {
			log.error(parse(msg));
		}

		@Override
		public void error(String msg, Throwable t) {
			log.error(parse(msg));
		}

		private String parse(String msg) {
			msg = msg.replace("<br />", "");
			msg = msg.replace("\n", " ");
			return msg;
		}
	}
}
