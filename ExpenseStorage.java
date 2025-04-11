import java.io.*;
import java.util.ArrayList;

public class ExpenseStorage {
    private static final String FILE_NAME = "myexpenses.txt";

    public static void storeExpense(ArrayList<Expense> list) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(list); 
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public static ArrayList<Expense> readExpense() {
        ArrayList<Expense> list = new ArrayList<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
            Object obj = ois.readObject();
            if (obj instanceof ArrayList<?>) {
                list = (ArrayList<Expense>) obj;
            }
        } catch (Exception e) {
            // File might not exist yet, or deserialization failed — safe to ignore in this context
        }
        return list;
    }
}
