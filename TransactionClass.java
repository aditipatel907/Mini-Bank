class TransactionTask implements Runnable {
    private Account account;
    private String type;
    private long amount;

    public TransactionTask(Account account, String type, long amount) {
        this.account = account;
        this.type = type;
        this.amount = amount;
    }

    @Override
    public void run() {
        if (type.equals("deposit")) {
            account.deposit(amount);
            System.out.println(Thread.currentThread().getName()
                    + " deposited " + amount);
        } else if (type.equals("withdraw")) {
            boolean result = account.withdraw(amount);

            if (result) {
                System.out.println(Thread.currentThread().getName()
                        + " withdrew " + amount);
            } else {
                System.out.println(Thread.currentThread().getName()
                        + " withdrawal failed");
            }
        }
    }
}