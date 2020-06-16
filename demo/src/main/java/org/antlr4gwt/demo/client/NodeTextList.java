package org.antlr4gwt.demo.client;

import java.util.ArrayList;
import java.util.Iterator;

public class  NodeTextList <T> extends ArrayList<T> {
    public String toString() {
        Iterator it = iterator();
        if (!it.hasNext())
            return "";
        StringBuilder sb = new StringBuilder();
        for (;;) {
            Object e = it.next();
            sb.append(e == this ? "" : e);
            if (!it.hasNext())
                return sb.toString();
        }
    }
    public NodeTextList all(T... e) {
         if(e!=null) {
             for(T t:e)add(t);
         }
          return this;
    }
};