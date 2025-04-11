import javax.swing.*;
import java.util.HashSet;

public class CategoryManager {
    private static HashSet<String> categories = new HashSet<>();

    public static void manageCategories() {
        String input = JOptionPane.showInputDialog(null, "Enter new category to add:");
        if (input != null && !input.trim().isEmpty()) {
            categories.add(input.trim());
            JOptionPane.showMessageDialog(null, "Category added: " + input);
        }
    }

    public static boolean isValidCategory(String cat) {
        return categories.contains(cat);
    }
}
