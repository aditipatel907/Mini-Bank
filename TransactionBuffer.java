class TransactionBuffer {
    private Runnable[] buffer = new Runnable[5];
    private int count = 0;
    private int in = 0;
    private int out = 0;

    public synchronized void put(Runnable task)
            throws InterruptedException {

        while (count == buffer.length) {
            wait();
        }

        buffer[in] = task;
        in = (in + 1) % buffer.length;
        count++;

        notify();
    }

    public synchronized Runnable take()
            throws InterruptedException {

        while (count == 0) {
            wait();
        }

        Runnable task = buffer[out];
        out = (out + 1) % buffer.length;
        count--;

        notify();
        return task;
    }
}