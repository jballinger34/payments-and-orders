package me.jamie.paymentspractice.dao.order;

import java.sql.*;

import me.jamie.paymentspractice.dto.LineItemRecord;
import me.jamie.paymentspractice.dto.OrderRecord;
import me.jamie.paymentspractice.exception.OrderNotFoundException;
import me.jamie.paymentspractice.exception.PersistenceException;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Repository
@Primary
public class OracleDbOrderDao implements OrderDao {

    private final DataSource dataSource;

    public OracleDbOrderDao(DataSource dataSource){
        this.dataSource = dataSource;
    }

    private Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }


    @Override
    public void save(OrderRecord order) throws PersistenceException {
        String mergeOrder = """
                MERGE INTO orders o
                USING (SELECT ? AS order_id, ? AS status_ordinal, ? AS payment_id FROM dual) src
                ON (o.order_id = src.order_id)
                WHEN MATCHED THEN
                    UPDATE SET o.status_ordinal = src.status_ordinal,
                                o.payment_id = src.payment_id
                WHEN NOT MATCHED THEN
                    INSERT (order_id, status_ordinal, payment_id)
                    VALUES (src.order_id, src.status_ordinal, src.payment_id)
               """;
        String deleteItems = "DELETE FROM order_items WHERE order_id = ?";
        String insertItem = "INSERT INTO order_items (order_id, product_id, quantity, price) VALUES (?, ?, ?, ?)";

        try(Connection con = getConnection()){
            con.setAutoCommit(false);
            try(
                    PreparedStatement mergeStmt = con.prepareStatement(mergeOrder);
                    PreparedStatement deleteStmt = con.prepareStatement(deleteItems);
                    PreparedStatement itemStmt = con.prepareStatement(insertItem);
            ) {
                mergeStmt.setString(1, order.id());
                mergeStmt.setInt(2, order.statusOrdinal());
                mergeStmt.setString(3, order.paymentId());
                mergeStmt.executeUpdate();

                //NOTE if we decide that order items cant be updated after order created,
                // this doesnt need to happen
                deleteStmt.setString(1, order.id());
                deleteStmt.executeUpdate();

                for(LineItemRecord record : order.items()){
                    itemStmt.setString(1, order.id());
                    itemStmt.setString(2, record.productId());
                    itemStmt.setInt(3, record.quantity());
                    itemStmt.setDouble(4, record.price());
                    itemStmt.addBatch();
                }
                itemStmt.executeBatch();

                con.commit();
            } catch (SQLException e) {
                con.rollback();
                throw new PersistenceException("Error saving order", e);
            }
        } catch (SQLException e){
            throw new PersistenceException("DB error", e);
        }
    }

    @Override
    public OrderRecord findById(String orderId) throws PersistenceException {
        String orderSql = "SELECT * FROM orders WHERE order_id = ?";
        String itemsSql = "SELECT * FROM order_items WHERE order_id = ?";

        try(Connection con = getConnection();
            PreparedStatement orderStmt = con.prepareStatement(orderSql);
            PreparedStatement itemStmt = con.prepareStatement(itemsSql)
        ){
            orderStmt.setString(1, orderId);
            ResultSet orderRs = orderStmt.executeQuery();

            if(!orderRs.next()){
                throw new OrderNotFoundException("No order with Id: " + orderId);
            }

            List<LineItemRecord> items = new ArrayList<>();
            itemStmt.setString(1,orderId);
            ResultSet itemsRs = itemStmt.executeQuery();

            while(itemsRs.next()){
                items.add(new LineItemRecord(
                        itemsRs.getString("product_id"),
                        itemsRs.getInt("quantity"),
                        itemsRs.getDouble("price")
                ));
            }
            return new OrderRecord(
                    orderRs.getString("order_id"),
                    orderRs.getInt("status_ordinal"),
                    orderRs.getString("payment_id"),
                    items
            );

        } catch (SQLException e){
            throw new PersistenceException("Error retrieving order.", e);
        }
    }

    @Override
    public List<OrderRecord> findAll() throws PersistenceException {
        String sql = """
            SELECT o.order_id, o.status_ordinal, o.payment_id,
                   i.product_id, i.quantity, i.price
            FROM orders o
            LEFT JOIN order_items i ON o.order_id = i.order_id
            ORDER BY o.order_id
        """;

        Map<String, OrderRecord> map = new LinkedHashMap<>();

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String orderId = rs.getString("order_id");

                OrderRecord order = map.get(orderId);
                if (order == null) {
                    order = new OrderRecord(
                            orderId,
                            rs.getInt("status_ordinal"),
                            rs.getString("payment_id"),
                            new ArrayList<>()
                    );
                    map.put(orderId, order);
                }

                String productId = rs.getString("product_id");
                if (productId != null) {
                    order.items().add(new LineItemRecord(
                            productId,
                            rs.getInt("quantity"),
                            rs.getDouble("price")
                    ));
                }
            }

        } catch (SQLException e) {
            throw new PersistenceException("Error retrieving orders", e);
        }

        return new ArrayList<>(map.values());
    }
}
