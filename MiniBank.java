import Model.annotation.Id;
import Model.annotation.Positive;
import java.util.Scanner;

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
    public void deposit(long amount) {
        balance += amount;
    }

    @Override
    public boolean withdraw(long amount) {
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
    Scanner input = new Scanner(System.in);

    BankInfo bank = new BankInfo("MiniBank", "Vadodara");

    System.out.println("==================================");
    System.out.println("Welcome to " + bank.name());
    System.out.println("Branch : " + bank.branch());
    System.out.println("==================================");

    // 1. Anonymous class
    Account account = new Account("Aditi", 10000);

    WithdrawRule rule1 = new WithdrawRule() {
        @Override
        public boolean allow(Account account, long amount) {
            return account.getBalance() >= amount;
        }
    };

    System.out.println("\n--- Anonymous Class ---");
    System.out.println("Withdraw 5000: " + rule1.allow(account, 5000));

    WithdrawRule rule2 =
            (account1, amount) -> account1.getBalance() >= amount;

    System.out.println("\n--- Lambda Expression ---");
    System.out.println("Withdraw 12000: " + rule2.allow(account, 12000));

    System.out.println("\n--- Interest Calculation ---");
    System.out.println("Yearly interest: " + account.yearlyInterest());

    System.out.println("\n--- Annotation Validation ---");
    Account invalidAccount = new Account("Test User", -500);

    String[] errors = AnnotationValidator.validate(invalidAccount);

    if (errors.length == 0) {
        System.out.println("No validation errors.");
    } else {
        for (String error : errors) {
            System.out.println(error);
        }
    }

    // 5. Input validation
    System.out.println("\n--- Input Validation Tests ---");

    System.out.println("Mobile: "
            + Validator.isValidMobile("9876543210")
            + " / "
            + Validator.isValidMobile("12345"));

    System.out.println("Email: "
            + Validator.isValidEmail("aditi@gmail.com")
            + " / "
            + Validator.isValidEmail("aditi@gmail"));

    System.out.println("PAN: "
            + Validator.isValidPan("ABCPD1234E")
            + " / "
            + Validator.isValidPan("ABC123"));

    System.out.println("IFSC: "
            + Validator.isValidIfsc("SBIN0123456")
            + " / "
            + Validator.isValidIfsc("ABC123"));

    // 6. Menu
    int choice = 0;

    while (choice != 5) {
        System.out.println("\nMENU");
        System.out.println("1. Open Account");
        System.out.println("2. Deposit");
        System.out.println("3. Withdraw");
        System.out.println("4. Transfer");
        System.out.println("5. Exit");

        System.out.print("Enter your choice: ");
        choice = input.nextInt();

        switch (choice) {
            case 1 -> System.out.println(
                    "OPEN_ACCOUNT - To be implemented in a later lab.");
            case 2 -> System.out.println(
                    "DEPOSIT - To be implemented in a later lab.");
            case 3 -> System.out.println(
                    "WITHDRAW - To be implemented in a later lab.");
            case 4 -> System.out.println(
                    "TRANSFER - To be implemented in a later lab.");
            case 5 -> System.out.println(
                    "Thank you for using " + bank.name() + "!");
            default -> System.out.println(
                    "Invalid choice! Please try again.");
        }
    }

    input.close();
}
}