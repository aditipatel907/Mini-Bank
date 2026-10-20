
class TransferDemo {
    static void transfer(Account a, Account b, long amount) {
        Account first = a;
        Account second = b;

        if (a.getAccountNumber().compareTo(b.getAccountNumber()) > 0) {
            first = b;
            second = a;
        }

        synchronized (first) {
            synchronized (second) {
                if (a.withdraw(amount)) {
                    b.deposit(amount);
                    System.out.println("Transfer completed");
                } else {
                    System.out.println("Not enough balance");
                }
            }
        }
    }
}
