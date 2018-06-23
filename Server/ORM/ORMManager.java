package Server.ORM;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;


public class ORMManager<T> extends AbstractManagerORM<T> {

    public ORMManager(Class<?> tClass, String url, String login, String password){
        super(tClass, url, login, password);
    }



    @Override
    public void create(){
        String query = "create table "+getNameTable()+"(\n";

        for(int i=0; i<getFields().size(); i++){

            String type = getType(getFields().get(i));
            String nameTableReferences = getFieldsWithType().get(getFields().get(i));

            if(nameTableReferences!=null) {

                query += getColumns().get(i) + " " + type + " references "+nameTableReferences+" on delete cascade,\n";


            }else {
                query += getColumns().get(i) + " " + type + " ,\n";
            }

        }

        if(getPrimaryKey()!=null) {
            query += "primary key (" + getPrimaryKey() + "))";
        }else {
            query=query.substring(0,query.length()-2)+")";
        }
        //System.out.println(query);
        try {
            getConnection().createStatement().execute(query);
        }catch (SQLException e){
            //e.printStackTrace();
            System.out.println("Таблица "+getNameTable()+" уже создана");
        }
    }

    @Override
    public ResultSet executeQuery(String query) {
        try{
            return getConnection().createStatement().executeQuery(query);
        }catch (SQLException e){
            //e.printStackTrace();
        }
        return null;
    }



    @Override
    public boolean insert(T object) {
        StringBuilder result = new StringBuilder();
        result.append("insert into "+getNameTable()+" values(");

        getFields().stream().forEach(
                e-> {
                    e.setAccessible(true);
                    result.append(getValue(e, object)+",");
                }
        );

        result.deleteCharAt(result.length()-1);
        result.append(")");
        try {
            //System.out.println(result.toString());
            getConnection().createStatement().executeUpdate(result.toString());
            return true;
        }catch (SQLException e){
            //e.printStackTrace();
            System.out.println("Объект в таблице "+getNameTable()+" уже существует");
        }
        return false;
    }


    @Override
    public boolean update(T object) {
        return true;
    }


    @Override
    public boolean delete(T object) {
        StringBuilder result = new StringBuilder();

        result.append("delete from "+getNameTable()+" where "+getPrimaryKey()+" = "+getValue(getFieldPrimaryKey(), object));

        try {
            getConnection().createStatement().executeUpdate(result.toString());

            return true;
        }catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }



    @Override
    public void dropTable() {
        try {
            getConnection().createStatement().executeUpdate("drop table "+getNameTable());
        }catch (SQLException e){
            e.printStackTrace();
        }
    }



    public String getType(Field field){
        Class type = field.getType();

        if(type==String.class){
            return "varchar(30)";
        }else if (type==int.class){
            return "integer";
        }else if (type==double.class){
            return "double precision";
        }else{
            FieldCreator fieldCreator = new FieldCreator(type);

            if(fieldCreator.getNameTable()!=null){
                ORMManager ORMManager = new ORMManager(type, DBConnectionConfig.url, DBConnectionConfig.login, DBConnectionConfig.password);

                getFieldsWithType().put(field, ORMManager.getNameTable());
                if(getaClass() != type) {
                    ORMManager.create();
                }
                return ORMManager.getType(fieldCreator.getFieldPrimaryKey());
            }else {
                return "jsonb";
            }

        }
    }



    public String getValue(Field field, T object){
        Object result =null;

        try {
            result = field.get(object);
        }catch (IllegalAccessException e){
            e.printStackTrace();
            System.out.println("Нулевое значение");
        }

        if(result instanceof String ){
            return "'"+result+"'";
        }else if (Integer.class.isInstance(result)){
            return Integer.toString((int)result);
        }else if (Double.class.isInstance(result)){
            return Double.toString((double)result);
        }else if (result==null) {
            return null;

        }else {

            FieldCreator fieldCreator = new FieldCreator(field.getType());
            if(fieldCreator.getNameTable()!=null){

                fieldCreator.getFieldPrimaryKey().setAccessible(true);


                ORMManager ORMManager = new ORMManager(field.getType(), DBConnectionConfig.url, DBConnectionConfig.login, DBConnectionConfig.password);
                ORMManager.insert(result);

                return ORMManager.getValue(fieldCreator.getFieldPrimaryKey(), result);

            }else {
                return "'"+getGson().toJson(result)+"'";
            }

        }

    }



    public T getElement(ResultSet resultSet){
        Field[] fields = getaClass().getDeclaredFields();

        try {
            Object object=getaClass().newInstance();

            Arrays.stream(fields).forEach(
                    e->{
                        if(e.getAnnotation(Column.class)!=null) {
                            e.setAccessible(true);
                            Object type = e.getType();

                            try {
                                try {
                                    if (type == int.class) {
                                        e.set(object, Integer.parseInt(resultSet.getString(e.getAnnotation(Column.class).name())));
                                    } else if (type == double.class) {
                                        e.set(object, Double.parseDouble(resultSet.getString(e.getAnnotation(Column.class).name())));
                                    } else {
                                        FieldCreator fieldCreator = new FieldCreator(e.getType());
                                        if (fieldCreator.getNameTable() != null) {


                                            fieldCreator.getFieldPrimaryKey().setAccessible(true);
                                            ORMManager ORMManager = new ORMManager(e.getType(), DBConnectionConfig.url, DBConnectionConfig.login, DBConnectionConfig.password);
                                            ResultSet resultSet1;

                                            resultSet1 = ORMManager.executeQuery("select * from "
                                                    + ORMManager.getNameTable()
                                                    + " where "
                                                    + ORMManager.getPrimaryKey()
                                                    + " = " + "'"+resultSet.getString(e.getAnnotation(Column.class).name())+"'"
                                            );

                                            while (resultSet1.next()){
                                                e.set(object, ORMManager.getElement(resultSet1));

                                            }



                                        } else {
                                            e.set(object, getGson().fromJson(resultSet.getString(e.getAnnotation(Column.class).name()), e.getType()));
                                        }
                                    }
                                }catch (IllegalAccessException e1){
                                    e1.printStackTrace();
                                }
                            }catch (SQLException f){
                                f.printStackTrace();
                            }



                        }

                    }
            );

            return (T) object;


        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return null;

    }


    public ResultSet getAllElements()
    {
        ResultSet res = null;
        try
        {
            res = executeQuery("select * from " + getNameTable() + ";");

        }catch (Exception ex){}
        finally {
            return res;
        }
    }



}
