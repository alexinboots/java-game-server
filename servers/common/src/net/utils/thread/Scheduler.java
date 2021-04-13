package net.utils.thread;

import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 定时任务管理器
 */
public class Scheduler {

	private static Logger log = LoggerFactory.getLogger(Scheduler.class);

	/**
	 * 所有任务
	 */
	private final static Map<String, Future<?>> tasks = new ConcurrentHashMap<String, Future<?>>();

	/**
	 * 定时任务线程池
	 */
	private final static ScheduledExecutorService executors = Executors
			.newScheduledThreadPool(Runtime.getRuntime().availableProcessors() * 2, new ThreadFactory() {
				AtomicInteger sn = new AtomicInteger();

				public Thread newThread(Runnable r) {
					SecurityManager s = System.getSecurityManager();
					ThreadGroup group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
					Thread t = new Thread(group, r);
					t.setName("taskThread-" + sn.incrementAndGet());
					return t;
				}
			});

	private static Runnable newTask(final String taskId, final Runnable task, final boolean removeAfterExecute) {
		return new Runnable() {
			public void run() {
				try {
					task.run();
				} catch (Throwable e) {
					log.error("SchedulerTask", e);
				}
				if (removeAfterExecute) {
					tasks.remove(taskId);
				}
			}
		};
	}

	public static String submit(final Runnable task) {
		String taskId = UUID.randomUUID().toString();
		submit(task, taskId);
		return taskId;
	}

	public static String submit(final Runnable task, long delay) {
		final String taskId = UUID.randomUUID().toString();
		submit(task, taskId, delay);
		return taskId;
	}

	public static String submit(final Runnable task, Date time) {
		final String taskId = UUID.randomUUID().toString();
		submit(task, taskId, time);
		return taskId;
	}

	public static String submit(final Runnable task, Date time, long interval) {
		long delay = 0;
		Date now = new Date();
		if (now.before(time)) {
			delay = time.getTime() - now.getTime();
		}
		return submit(task, delay, interval);
	}

	public static String submit(final Runnable task, long delay, long interval) {
		final String taskId = UUID.randomUUID().toString();
		submit(task, taskId, delay, interval);
		return taskId;
	}

	public static String submitFixDelay(final Runnable task, long delay, long interval) {
		final String taskId = UUID.randomUUID().toString();
		submitFixDelay(task, taskId, delay, interval);
		return taskId;
	}

	public static void submit(final Runnable task, final String taskId, Date time) {
		long delay = 0;
		Date now = new Date();
		if (now.before(time)) {
			delay = time.getTime() - now.getTime();

		}
		submit(task, taskId, delay);
	}

	public static void submit(final Runnable task, final String taskId, Date time, long interval) {
		long delay = 0;
		Date now = new Date();
		if (now.before(time)) {
			delay = time.getTime() - now.getTime();
		}
		submit(task, taskId, delay, interval);
	}

	public static void submit(final Runnable task, final String taskId) {
		cancel(taskId);
		Future<?> future = executors.submit(newTask(taskId, task, true));
		if (!future.isDone() && !future.isCancelled()) {
			tasks.put(taskId, future);
		}
	}

	public static void submit(final Runnable task, final String taskId, long delay) {
		cancel(taskId);
		ScheduledFuture<?> future = executors.schedule(newTask(taskId, task, true), delay, TimeUnit.MILLISECONDS);
		if (!future.isDone() && !future.isCancelled()) {
			tasks.put(taskId, future);
		}
	}

	public static void submit(final Runnable task, final String taskId, long delay, long interval) {
		cancel(taskId);
		ScheduledFuture<?> future = executors
				.scheduleAtFixedRate(newTask(taskId, task, false), delay, interval, TimeUnit.MILLISECONDS);
		if (!future.isDone() && !future.isCancelled()) {
			tasks.put(taskId, future);
		}
	}

	public static void submitFixDelay(final Runnable task, final String taskId, long delay, long interval) {
		cancel(taskId);
		ScheduledFuture<?> future = executors
				.scheduleWithFixedDelay(newTask(taskId, task, false), delay, interval, TimeUnit.MILLISECONDS);
		if (!future.isDone() && !future.isCancelled()) {
			tasks.put(taskId, future);
		}
	}

	public static void cancel(String taskId) {
		if (taskId != null) {
			Future<?> future = tasks.get(taskId);
			if (future != null) {
				future.cancel(false);
				log.debug("任务 [{}] 已取消 !", taskId);
				tasks.remove(taskId);
			}
		}
	}

	public static long getDelay(String taskId) {
		if (taskId != null) {
			if (tasks.containsKey(taskId)) {
				Future<?> future = tasks.get(taskId);
				if (future instanceof ScheduledFuture<?>) {
					return ((ScheduledFuture<?>) future).getDelay(TimeUnit.MILLISECONDS);
				}
			}
		}
		return -1;
	}

}
