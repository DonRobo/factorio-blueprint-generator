package com.donrobo.fpbg.data;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ItemStack {

    private final int count;
    private final Item item;
}
