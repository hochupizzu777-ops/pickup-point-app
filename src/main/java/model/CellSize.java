package model;

import javax.print.attribute.standard.JobKOctets;
import java.util.Objects;

public enum CellSize {

    SMALL(1),
    MEDIUM(2),
    LARGE(3);

    private final int level;

    CellSize(int level){
        this.level = level;
    }

    public int getLevel(){
        return level;
    }

    public boolean fits(CellSize requiredSize){
        Objects.requireNonNull(requiredSize, "Размер заказа не должен быть null");

        return this.level >= requiredSize.level;
    }
}
