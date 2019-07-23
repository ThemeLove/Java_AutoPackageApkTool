package com.themelove.tool.vas.thread;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 *  线程池管理类
 *	@author:qingshanliao
 *  @date  :2019年1月24日
 */
public class ThreadPoolManager {
//	private final String TAG = ThreadPoolManager.class.getSimpleName();
	private static ThreadPoolManager instance = null;
	
	private ThreadPoolExecutor executor;
	
    private  final int CPU_COUNT         = Runtime.getRuntime().availableProcessors(); //根据cpu的数量动态的配置核心线程数和最大线程数
    private  final int CORE_POOL_SIZE    = CPU_COUNT + 1; //核心线程数 = CPU核心数 + 1
    private  final int MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1; //线程池最大线程数 = CPU核心数 * 2 + 1
    private  final int KEEP_ALIVE        = 10;  //非核心线程闲置时超时10s

	private ThreadPoolManager(){
		 executor = new ThreadPoolExecutor(CORE_POOL_SIZE, 
				 							MAXIMUM_POOL_SIZE,
				 							KEEP_ALIVE,
				 							TimeUnit.SECONDS,
				 							new ArrayBlockingQueue<Runnable>(20),
				 							Executors.defaultThreadFactory(),
				 							new ThreadPoolExecutor.AbortPolicy());
	}
	
	public static ThreadPoolManager getInstance(){
		if (instance==null) {
			synchronized (ThreadPoolManager.class) {
				if (instance == null) {
					instance = new ThreadPoolManager();
				}
			}
		}
		return instance;
	}
	
	/**
	 * 在子线程中执行一个Runnable任务
	 * @param task
	 */
	public void execute(Runnable task){
		executor.execute(task);
	}
	
	/**
	 * 移除一个Runnable任务
	 * @param task
	 * @return
	 */
	public boolean cancel(Runnable task){
		return executor.getQueue().remove(task);
	}

	
	/**
	 * 执行一个带返回值的Callable任务
	 * @param task
	 * @return
	 */
	public <T> Future<T>  submit(Callable<T> task){
		return executor.submit(task);
	}
	
	/**
	 * 取消一个Callback的Future任务
	 * @param future 	submit方法的返回值
	 * @param mayInterruptIfRunning		true：中断线程并停止任务	false:让任务继续执行完毕，并返回false
	 * @return
	 */
	public <T> boolean cancelFuture(Future<T> future, boolean mayInterruptIfRunning){
		return future.cancel(mayInterruptIfRunning);
	}
}
