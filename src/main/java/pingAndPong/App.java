package pingAndPong;

import lombok.extern.slf4j.Slf4j;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.SynchronousQueue;

@Slf4j
public class App {

	static final Random random = new Random();

	static final SynchronousQueue<Integer> monitorQueue = new SynchronousQueue<Integer>();

	public static void main(String[] args) throws InterruptedException {

		ExecutorService executor = Executors.newFixedThreadPool(2);

		executor.submit(new PingPongImpl().ping);
		executor.submit(new PingPongImpl().pong);

	}

	static class PingPongImpl {

		private final Thread ping = new Thread() {

			@Override
			public void run() {
				while (true) {
					try {
						int sleepTime = random.nextInt(1000);
						log.info("Ping " + sleepTime);
						monitorQueue.put(1);
						Thread.sleep(sleepTime);
					} catch (Exception e) {
						log.error(e.getMessage(), e);
					}
				}
			}

		};

		private final Thread pong = new Thread() {

			@Override
			public void run() {
				while (true) {
					try {
						int sleepTime = random.nextInt(1000);
						monitorQueue.take();
						log.info("Pong " + sleepTime);
						Thread.sleep(sleepTime);
					} catch (Exception e) {
						log.error(e.getMessage(), e);
					}

				}

			}

		};

	}

}
