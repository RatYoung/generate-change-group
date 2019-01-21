package edu.nju.cs.inform.core.jda.type;

/**
 * Created by niejia on 16/1/28.
 */
public class JDACallRelation {
    private String caller;
    private String callee;

    public JDACallRelation(String caller, String variableName) {
        this.setCaller(caller);
        this.setCallee(variableName);
    }


    public String toString() {
        return getCaller() + " " + getCallee();
    }

    @Override
    public boolean equals(Object y) {
        if (y == this) return true;
        if (y == null) return false;
        if (y.getClass() != this.getClass()) return false;
        JDACallRelation that = (JDACallRelation) y;
        if (!this.getCaller().equals(that.getCaller())) return false;
        if (!this.getCallee().equals(that.getCallee())) return false;
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 17;
        hash = 31 * hash + getCaller().hashCode();
        hash = 31 * hash + getCallee().hashCode();
        return hash;
    }

    public String getCaller() {
        return caller;
    }

    public void setCaller(String caller) {
        this.caller = caller;
    }

    public String getCallee() {
        return callee;
    }

    public void setCallee(String callee) {
        this.callee = callee;
    }
}
