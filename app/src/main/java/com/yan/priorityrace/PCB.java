package com.yan.priorityrace;

/**
 * Created by Yan on 2016/11/17.
 */

public class PCB implements Comparable{
    public String id;//进程id
    public int priority;//进程优先级,越小优先级越高，0最高
    public int progress;//当前执行进度

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PCB pcb = (PCB) o;

        return id != null ? id.equals(pcb.id) : pcb.id == null;

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public int compareTo(Object o) {
        int numbera = priority;
        int numberb = ((PCB)o).priority;
        if(numberb > numbera)
        {
            return -1;
        }
        else if(numberb<numbera)
        {
            return 1;
        }
        else
        {
            return 0;
        }
    }
    //假定整个运行过程占用cpu时间相同，都为100；
}
