import java.util.concurrent.atomic.AtomicInteger;

public class LogoutUserHandler implements Runnable {
    AtomicInteger timer;

    public LogoutUserHandler(AtomicInteger timer) {
        this.timer = timer;
    }   

    @Override
    public void run() {
        try {
            while (true) {
                Thread.sleep(1000);
                if (timer.decrementAndGet() == 0) {
                    System.out.println("\nTEMPO SCADUTO");
                    System.exit(0);
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}