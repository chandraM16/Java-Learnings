package com.chandan.ledger;

import java.math.BigDecimal;

public class CategoryStat {
    private final Category category;
    private int count;
    private BigDecimal total;

    public CategoryStat(Category category, int count, BigDecimal total){
        this.category = category;
        this.count = count;
        this.total = total;
    }

    public int getCount() {
        return count;
    }

    public Category getCategory() {
        return category;
    }

    public BigDecimal getTotal() {
        return total;
    }


}
