package dao;

import me.jamie.paymentspractice.dao.order.OrderDao;
import me.jamie.paymentspractice.dto.LineItemRecord;
import me.jamie.paymentspractice.dto.OrderRecord;
import me.jamie.paymentspractice.exception.PersistenceException;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class OracleDbOrderDaoTest {

    @Autowired
    private OrderDao dao;
    @Autowired
    private DataSource dataSource;

    @BeforeAll
    void createTables() throws Exception {
        try (Connection conn = dataSource.getConnection();
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

    @BeforeEach
    public void cleanTables() {
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute("DELETE FROM order_items");
            stmt.execute("DELETE FROM orders");

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @AfterAll
    void tearDownSchema() throws Exception {
        try (Connection conn = dataSource.getConnection();
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