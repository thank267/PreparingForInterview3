package counter;

import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.IntStream;

@Slf4j
public class App {

	static final ReentrantLock locker = new ReentrantLock();
	static Integer counter = 0;

	public static void main(String[] args) throws InterruptedException {

		ExecutorService executor = Executors.newFixedThreadPool(4);

		Set<Future> futures = new HashSet<Future>();

		// суммируем 100000 раз
		IntStream.range(1, 100000).forEach(i -> futures.add(executor.submit(new Counter().plus)));
		// вычитаем 100000 раз
		IntStream.range(1, 100000).forEach(i -> futures.add(executor.submit(new Counter().minus)));

		futures.stream().forEach(f -> {
			try {
				f.get();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
		});
		log.info("Final counter is Zero: {}", counter);

		executor.shutdown();

	}

	static class Counter {

		private final Thread plus = new Thread() {

			@Override
			public void run() {

				try {
					locker.lock();
					counter++;
					log.info("Counter is : " + counter);
				} catch (Exception e) {
					log.error(e.getMessage(), e);
				} finally {
					locker.unlock();
				}
			}

		};
		private final Thread minus = new Thread() {

			@Override
			public void run() {

				try {
					locker.lock();
					counter--;
					log.info("Counter is : " + counter);
				} catch (Exception e) {
					log.error(e.getMessage(), e);
				} finally {
					locker.unlock();
				}

			}

		};

	}

}
