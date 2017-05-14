package com.donrobo.fpbg.planner;

import com.donrobo.fpbg.data.Item;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FractionalItemStack {

    private final double count;
    private final Item item;

}
