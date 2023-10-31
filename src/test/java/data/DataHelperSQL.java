package data;

import lombok.SneakyThrows;
import lombok.val;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import java.sql.DriverManager;

public class DataHelperSQL {
    private static String url = System.getProperty("db.url");
    private static String user = System.getProperty("db.user");
    private static String password = System.getProperty("db.password");

    @SneakyThrows
    public static void clearTables() { //очистить БД
        val deletePaymentEntity = "DELETE FROM payment_entity";
        val deleteCreditEntity = "DELETE FROM credit_request_entity";
        val deleteOrderEntity = "DELETE FROM order_entity";
        val runner = new QueryRunner();
        try (val conn = DriverManager.getConnection(
                url, user, password)
        ) {
            runner.update(conn, deletePaymentEntity);
            runner.update(conn, deleteCreditEntity);
            runner.update(conn, deleteOrderEntity);
        }
    }

    @SneakyThrows
    public static String getPaymentStatus() { //статус дебетовой карты
        String status = "SELECT status FROM payment_entity";
        return getStatus(status);
    }

    @SneakyThrows
    public static String getCreditStatus() { // статус кредитной карты
        String status = "SELECT status FROM credit_request_entity";
        return getStatus(status);
    }

    @SneakyThrows
    public static String getStatus(String status) {
        String result = "";
        val runner = new QueryRunner();
        try (val conn = DriverManager.getConnection(
                url, user, password)
        ) {
            result = runner.query(conn, status, new ScalarHandler<String>());
            System.out.println(result);
            return result;
        }
    }
}
