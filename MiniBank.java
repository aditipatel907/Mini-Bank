import Model.annotation.Id;
import Model.annotation.Positive;
import java.io.Serializable;

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

class Account implements Transactable, InterestBearing, Serializable {
    private static final long serialVersionUID = 1L;

    public int compareTo(Account other) {
        return this.accNum.compareTo(other.accNum);
    }

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
        AccountStore store = new AccountStore();

        Account a1 = new Account("Aditi", 5000);
        Account a2 = new Account("Rahul", 2000);
        Account a3 = new Account("Priya", 8000);

        store.add(a1);
        store.add(a2);
        store.add(a3);

        store.addTransaction("Aditi deposited 500");
        store.addTransaction("Rahul withdrew 200");

        System.out.println("--- Accounts Sorted by Balance ---");

        for (Account account : store.sortedByBalance()) {
            System.out.println(account.getAccountNumber() + " | "
                    + account.getOwnerName() + " | Rs. "
                    + account.getBalance());
        }

        System.out.println("\n--- Transaction History ---");

        for (String transaction : store.getHistory()) {
            System.out.println(transaction);
        }

        System.out.println("\n--- Comparable Test ---");
        System.out.println("First account: " + a1.compareTo(a2));
    }

}