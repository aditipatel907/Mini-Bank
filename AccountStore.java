
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

class AccountStore {
    private HashMap<String, Account> accounts = new HashMap<>();
    private ArrayList<String> history = new ArrayList<>();

    public void add(Account account) {
        accounts.put(account.getAccountNumber(), account);
    }

    public Account get(String accountNumber) {
        return accounts.get(accountNumber);
    }

    public ArrayList<Account> getAll() {
        return new ArrayList<>(accounts.values());
    }

    public void addTransaction(String transaction) {
        history.add(transaction);
    }

    public ArrayList<String> getHistory() {
        return new ArrayList<>(history);
    }

    public ArrayList<Account> sortedByBalance() {
        ArrayList<Account> sorted = getAll();

        sorted.sort(Comparator.comparingLong(Account::getBalance));

        return sorted;
    }
}
