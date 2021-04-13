package net.db.dao;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.db.BasePo;
import net.utils.OrderedThreadPoolExecutor;

/**
 * 异步数据操作（更新和删除的异步，取数据等操作需要同步返回）
 * 采用netty的OrderedMemoryAwareThreadPoolExecutor的设计通过高效的forkjoin线程池实现异步，
 * 不保证用户数据按提交顺序执行，只保证同一条数据按顺序执行，支持状态合并
 *
 * @author ckf
 */
public final class AsyncData extends SyncData {

	private final static Logger logger = LoggerFactory.getLogger(AsyncData.class);

	private final Map<String, BasePo> map = new ConcurrentHashMap<>();
	private final OrderedThreadPoolExecutor executor = OrderedThreadPoolExecutor
			.newFixesOrderedThreadPool(Runtime.getRuntime().availableProcessors());

	@Override public <T extends BasePo> int delete(int dsId, String className, String data) {
		Dao<T> dao = Dao.getDao(dsId, className);
		final T t = dao.decode(data);
		if (t == null) {
			return 0;
		}

		if (dao instanceof CachedDao) {
			final CachedDao<T> d = (CachedDao<T>) dao;

			String markKey = d.getMarkKey(t);
			try {
				int res = d.asyncDelete(t);

				map.remove(markKey);
				executor.execute(new OrderedThreadPoolExecutor.OrderedRunable((long) markKey.hashCode()) {
					@Override public void run() {
						d.asyncDeleteDb(t);
					}
				});
				return res;
			} catch (RuntimeException e) {
				logger.error("cache error", e);
				return d.asyncDeleteDb(t);
			}
		} else {
			return super.delete(dsId, className, data);
		}
	}

	@Override public <T extends BasePo> boolean update(int dsId, String className, String data) {
		Dao<T> dao = Dao.getDao(dsId, className);
		final T t = dao.decode(data);
		if (t == null) {
			return false;
		}

		if (dao instanceof CachedDao) {
			final CachedDao<T> d = (CachedDao<T>) dao;
			final String markKey = d.getMarkKey(t);
			try {
				boolean res = d.asyncUpdate(data);

				map.put(markKey, t);
				executor.execute(new OrderedThreadPoolExecutor.OrderedRunable((long) markKey.hashCode()) {
					@Override public void run() {
						Object o = map.remove(markKey);
						if (o != null) {
							d.asyncUpdateDb((T) o);
						}
					}
				});
				return res;
			} catch (RuntimeException e) {
				logger.error("cache error", e);
				return d.asyncUpdateDb(t);
			}
		} else {
			return super.update(dsId, className, data);
		}
	}

	public void shutdown() {
		this.executor.shutdown();
	}
}
