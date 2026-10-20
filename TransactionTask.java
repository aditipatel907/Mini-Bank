
class TransactionTask implements Runnable {
    private Account account;
    private String type;
    private long amount;

    public TransactionTask(Account account, String type, long amount) {
        this.account = account;
        this.type = type;
        this.amount = amount;
    }

    public void run() {
        if (type.equals("deposit")) {
            account.deposit(amount);
        } else {
            account.withdraw(amount);
        }
    }
}
