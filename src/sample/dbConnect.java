package sample;
import java.sql.*;

public class dbConnect {
    private String url = "localhost";
    private String port = "3306";
    private String database = "javaproject";
    private String user = "root";
    private String password = "";
    private String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    Connection connection = null;

    public Connection connect2DB(){
        String DB_URL = "jdbc:mysql://"+url+":"+port+"/"+database;
        try{
            connection = DriverManager.getConnection(DB_URL,user,password);
            System.out.println("Connected");
            return connection;
        }catch (SQLException e){
            e.printStackTrace();
            return null;
        }
    }

}
