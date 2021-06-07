package com.nco.utils.tables;

public class Attribute {

    private String name;
    private String current;
    private String max;
    private String id;

    public Attribute (Object name, Object current, Object max, Object id) {
        this.name = name.toString();
        this.current = current.toString();
        this.max = max.toString();
        this.id = id.toString();
    }

    @Override
    public String toString() {
        return "Name : '" + name + "'\nCurrent : '" + current + "'\nMax : '" + max + "'\nID : '" + id + "'";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCurrent() {
        return current;
    }

    public void setCurrent(String current) {
        this.current = current;
    }

    public String getMax() {
        return max;
    }

    public void setMax(String max) {
        this.max = max;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
