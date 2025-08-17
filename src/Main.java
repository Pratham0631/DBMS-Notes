import java.sql.*;
import java.util.Scanner;

public class Main {
    // For connection
    private static final String url = "jdbc:mysql://127.0.0.1:3306/lenden";

    private static final String username = "root";

    private static final String password = "root123";

    public static void main(String[] args) {
        // Driver Loaded
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
        }
        catch(ClassNotFoundException e){
            System.out.println(e.getMessage());
        }

        // Create Connection to Database
        try{
            Connection connection = DriverManager.getConnection(url, username, password);
            connection.setAutoCommit(false);
            String debit_query = "UPDATE accounts SET balance = balance - ? WHERE account_number = ?";
            String credit_query = "UPDATE accounts SET balance = balance + ? WHERE account_number = ?";
            PreparedStatement debitPreparedStatement = connection.prepareStatement(debit_query);
            PreparedStatement creditPreparedStatement = connection.prepareStatement(credit_query);

            Scanner sc = new Scanner(System.in);
            System.out.println("Enter Account number (Debit): ");
            int account_number = sc.nextInt();
            System.out.println("Enter Amount: ");
            double amount = sc.nextDouble();
            System.out.println("Enter Account number (Credit): ");
            int account_number2 = sc.nextInt();

            debitPreparedStatement.setDouble(1, amount);
            debitPreparedStatement.setInt(2, account_number);
            creditPreparedStatement.setDouble(1, amount);
            creditPreparedStatement.setInt(2, account_number2);

            debitPreparedStatement.executeUpdate();
            creditPreparedStatement.executeUpdate();

            if(isSufficient(connection, account_number, amount)){
                connection.commit();
                System.out.println("Transaction Successful!!");
            }
            else{
                connection.rollback();
                System.out.println("Transaction Failed!!");
            }
        }
        catch(SQLException e){
            System.out.println(e.getMessage());
        }

    }

    static boolean isSufficient(Connection connection, int account_number, double amount){
        try{
            String query = "SELECT balance FROM accounts WHERE account_number = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, account_number);
            ResultSet resultSet = preparedStatement.executeQuery();

            if(resultSet.next()){
                double current_balance = resultSet.getDouble("balance");
                if(amount > current_balance){
                    return false;
                }
            }
        }
        catch(SQLException e){
            System.out.println(e.getMessage());
        }
        return true;
    }
}