package dao;

import me.jamie.paymentspractice.dao.order.OracleDbOrderDao;
import me.jamie.paymentspractice.dao.order.OrderDao;
import me.jamie.paymentspractice.dto.LineItemRecord;
import me.jamie.paymentspractice.dto.OrderRecord;
import me.jamie.paymentspractice.exception.PersistenceException;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class OracleDbOrderDaoTest {

    private OrderDao dao;

    private static final String URL = System.getenv("DB_URL");
    private static final String USER = System.getenv("DB_TEST_USER");
    private static final String PASS = System.getenv("DB_TEST_PASSWORD");

    @BeforeAll
    static void createTables() throws Exception {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
             Statement stmt = conn.createStatement()
        ) {
            stmt.execute("""
            CREATE TABLE orders (
                order_id VARCHAR2(36) PRIMARY KEY,
                status_ordinal NUMBER,
                payment_id VARCHAR2(36)
            )
        """);

            stmt.execute("""
            CREATE TABLE order_items (
                order_id VARCHAR2(36),
                product_id VARCHAR2(50),
                quantity NUMBER,
                price NUMBER
            )
        """);
        } catch (SQLException e) {
            // ignore "table already exists" errors for simplicity
        }
    }
    private void cleanTables() {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
             Statement stmt = conn.createStatement()) {

            stmt.execute("DELETE FROM order_items");
            stmt.execute("DELETE FROM orders");

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    void setUp() throws Exception {
        dao = new OracleDbOrderDao(URL, USER, PASS);
        cleanTables();
    }
    @AfterAll
    static void tearDownSchema() throws Exception {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
             Statement stmt = conn.createStatement()) {

            stmt.execute("DROP TABLE order_items");
            stmt.execute("DROP TABLE orders");

        } catch (SQLException e) {
            // ignore cleanup errors
        }
    }

    @Test
    void testSaveFindOrder(){
        try {
            String id = UUID.randomUUID().toString();
            OrderRecord order = new OrderRecord(
                    id,
                    1,
                    "PAY1",
                    List.of(
                            new LineItemRecord("P1", 2, 10.0),
                            new LineItemRecord("P2", 1, 5.0)
                    )
            );

            dao.save(order);

            OrderRecord result = dao.findById(id);

            assertEquals(id, result.id());
            assertEquals(1, result.statusOrdinal());
            assertEquals("PAY1", result.paymentId());
            assertEquals(2, result.items().size());
        } catch (PersistenceException e){
            fail("Error saving/retrieving order");
        }
    }

    @Test
    void testFindAllOrders() {
        try{
            String id1 = UUID.randomUUID().toString();
            String id2 = UUID.randomUUID().toString();

            dao.save(new OrderRecord(
                    id1, 1, "P1",
                    List.of(new LineItemRecord("A", 1, 1.0))
            ));
            dao.save(new OrderRecord(
                    id2, 1, "P2",
                    List.of(new LineItemRecord("B", 2, 2.0))
            ));

            List<OrderRecord> all = dao.findAll();

            assertEquals(2, all.size());

        } catch (PersistenceException e){
            fail("Error saving/retrieving order");
        }
    }

    @Test
    void testOrderNotFound() {
        assertThrows(RuntimeException.class, () -> {
            dao.findById("does-not-exist");
        });
    }
}