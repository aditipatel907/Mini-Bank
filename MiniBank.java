import Model.annotation.Id;
import Model.annotation.Positive;

record BankInfo(String name, String branch) {}

record Command(TransactionType type, String accountNumber, long amount) {}

enum MenuOption {
    OPEN_ACCOUNT,
    DEPOSIT,
    WITHDRAW,
    TRANSFER,
    EXIT
}

enum TransactionType {
    DEPOSIT,
    WITHDRAW,
    TRANSFER
}

interface Transactable {
    void deposit(long amount);
    boolean withdraw(long amount);
}

interface InterestBearing {
    double interestRate();

    default double yearlyInterest() {
        return getBalance() * interestRate();
    }

    long getBalance();
}

@FunctionalInterface
interface WithdrawRule {
    boolean allow(Account account, long amount);
}

class CommandParser {
    public static Command parse(String line) {
        String[] parts = line.split(" ");

        TransactionType operation =
                TransactionType.valueOf(parts[0]);

        String accountNumber = parts[1];
        long amount = Long.parseLong(parts[2]);

        return new Command(operation, accountNumber, amount);
    }
}

class StatementFormatter {
    public static String buildStatement(Account account) {
        StringBuilder statement = new StringBuilder();

        statement.append("==== ACCOUNT STATEMENT ====\n");
        statement.append("Account number: ");
        statement.append(account.getAccountNumber());
        statement.append("\n");

        statement.append("Owner: ");
        statement.append(account.getOwnerName());
        statement.append("\n");

        statement.append("Balance: ");
        statement.append(account.getBalance());
        statement.append("\n");

        return statement.toString();
    }
}

class Validator {
    public static boolean isValidEmail(String email) {
        return email.matches(
                "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
        );
    }

    public static boolean isValidMobile(String mobile) {
        return mobile.matches("^[6-9]\\d{9}$");
    }

    public static boolean isValidPan(String pan) {
        return pan.matches("[A-Z]{3}[PCFHAT][A-Z][0-9]{4}[A-Z]$");
    }

    public static boolean isValidIfsc(String ifsc) {
        return ifsc.matches("[A-Z]{4}[0][A-Z0-9]{6}$");
    }
}

class Customer {
    private String name;
    private String email;
    private String mobile;
    private final String customerId;
    private Address address;

    private static long customerCounter = 101;

    private static String generateCustomerId() {
        return "CUST" + customerCounter++;
    }

    public Customer(String name, String email, String mobile) {
        this.name = name;
        this.email = email;
        this.mobile = mobile;
        this.customerId = generateCustomerId();
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getMobile() {
        return mobile;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void Cloneable() {
        Customer clonedCust = new Customer(this.name, this.email, this.mobile);
    }

    public static class Address {
        String line;
        String city;
        String pincode;

        String getLine() {
            return line;
        }

        String getCity() {
            return city;
        }

        String getPincode() {
            return pincode;
        }
    }

    public String getAddress() {
        if (address != null) {
            return address.getLine() + ", "
                    + address.getCity() + " - "
                    + address.getPincode();
        }

        return "Address not set";
    }
}

class Account implements Transactable, InterestBearing {
    @Id
    private final String accNum;

    private String owner;

    @Positive
    private long balance;

    private boolean active;

    private static int accCount = 1;

    private static String generateAccountNumber() {
        return String.format("AC%04d", accCount++);
    }

    public Account(String ownerName, long openingBalance) {
        this.accNum = generateAccountNumber();
        this.owner = ownerName;
        this.balance = openingBalance;
        this.active = true;
    }

    public Account(String ownerName) {
        this(ownerName, 0);
    }

    @Override
    public synchronized void deposit(long amount) {
        balance += amount;
    }

    @Override
    public synchronized boolean withdraw(long amount) {
        if (balance >= amount) {
            balance -= amount;
            return true;
        }
        return false;
    }

    public String getAccountNumber() {
        return accNum;
    }

    public String getOwnerName() {
        return owner;
    }

    @Override
    public long getBalance() {
        return balance;
    }

    @Override
    public double interestRate() {
        return 0.05;
    }

    public boolean isActive() {
        return active;
    }

    @Override
    public String toString() {
        return "Account{" +
                "Account Number ='" + accNum + '\'' +
                ", Owner =" + owner + '\'' +
                ", Balance = " + balance +
                ", Active = " + active +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        Account account = (Account) obj;
        return accNum.equals(account.accNum);
    }

    @Override
    public int hashCode() {
        return accNum.hashCode();
    }
}

public class MiniBank {

    public static void main(String[] args) {
        Account a = new Account("Aditi", 1000);
        Account b = new Account("Test User", 1000);

        // 1. Thread pool
        System.out.println("--- Thread Pool ---");

        TransactionProcessor processor = new TransactionProcessor();
        processor.submit(new TransactionTask(a, "deposit", 100));
        processor.submit(new TransactionTask(a, "withdraw", 50));
        processor.stop();

        System.out.println("Balance: " + a.getBalance());

        // 2. Producer and consumer
        System.out.println("\n--- Producer Consumer ---");

        TransactionBuffer buffer = new TransactionBuffer();

        Thread producer = new Thread(() -> {
            try {
                buffer.put(new TransactionTask(a, "deposit", 10));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        Thread consumer = new Thread(() -> {
            try {
                Runnable task = buffer.take();
                task.run();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        producer.start();
        consumer.start();

        try {
            producer.join();
            consumer.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return;
        }

        System.out.println("Balance: " + a.getBalance());

        // 3. Deadlock prevention
        System.out.println("\n--- Safe Transfers ---");

        Thread t1 = new Thread(() -> TransferDemo.transfer(a, b, 100));
        Thread t2 = new Thread(() -> TransferDemo.transfer(b, a, 50));

        t1.start();
        t2.start();

        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return;
        }

        System.out.println("Account A: " + a.getBalance());
        System.out.println("Account B: " + b.getBalance());
    }

}