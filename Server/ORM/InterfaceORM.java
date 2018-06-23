package Server.ORM;

import java.sql.ResultSet;

interface InterfaceORM<T>{

    ResultSet executeQuery(String query);

    void create();

    boolean insert(T object);

    boolean update(T object);

    boolean delete(T object);

    void dropTable();

}
