package Server.ORM;

import com.google.gson.Gson;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

abstract class AbstractManagerORM<T> implements InterfaceORM<T> {
    private Connection connection;
    private String nameTable;
    private List<String> columns;
    private List<Field> fields;
    private String primaryKey;
    private Field fieldPrimaryKey;
    private Map<Field, String> fieldsWithType;
    private Gson gson;
    private Class<?> aClass;


    public AbstractManagerORM(Class<?> tClass, String url, String login, String password) {
        try {
            connection = DriverManager.getConnection(url, login, password);
            this.aClass = tClass;
            FieldCreator fieldCreator = new FieldCreator(tClass);
            this.nameTable = fieldCreator.getNameTable();
            this.fields = fieldCreator.getFields();
            this.columns = fieldCreator.getNameColumns();
            this.primaryKey = fieldCreator.getPrimaryKey();
            this.fieldPrimaryKey = fieldCreator.getFieldPrimaryKey();
            this.fieldsWithType = new HashMap<>();
            this.gson = new Gson();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setAutoCommit(Boolean b){
        try {
            connection.setAutoCommit(b);
        }catch (Exception e ){e.printStackTrace();}
    }

    public void commit(){
        try{
        connection.commit();
        }catch (Exception e ){e.printStackTrace();}
    }

    public List<String> getColumns() {
        return columns;
    }

    public List<Field> getFields() {
        return fields;
    }

    public String getNameTable() {
        return nameTable;
    }

    public Connection getConnection() {
        return connection;
    }

    public String getPrimaryKey() {
        return primaryKey;
    }

    public Field getFieldPrimaryKey() {
        return fieldPrimaryKey;
    }

    public Map<Field, String> getFieldsWithType() {
        return fieldsWithType;
    }

    public Gson getGson() {
        return gson;
    }

    public Class<?> getaClass() {
        return aClass;
    }
}
