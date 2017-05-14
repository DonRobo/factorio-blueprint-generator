package com.donrobo.fpbg;

import com.donrobo.fpbg.blueprint.Blueprint;
import com.donrobo.fpbg.blueprint.Direction;
import com.donrobo.fpbg.blueprint.building.Splitter;
import com.donrobo.fpbg.blueprint.building.YellowBelt;

import java.awt.*;
import java.awt.datatransfer.StringSelection;

public class SplitterMain {

    public static void main(String[] args) {
        Blueprint bp = new Blueprint();
        bp.addBuilding(new Splitter(-1, 0, Direction.RIGHT));
        bp.addBuilding(new Splitter(0, 0, Direction.UP));
        bp.addBuilding(new Splitter(2, 0, Direction.DOWN));
        bp.addBuilding(new Splitter(4, 0, Direction.LEFT));

        bp.addBuilding(new YellowBelt(-1, -1, Direction.UP));
        bp.addBuilding(new YellowBelt(0, -1, Direction.UP));
        bp.addBuilding(new YellowBelt(1, -1, Direction.UP));
        bp.addBuilding(new YellowBelt(2, -1, Direction.UP));
        bp.addBuilding(new YellowBelt(3, -1, Direction.UP));
        bp.addBuilding(new YellowBelt(4, -1, Direction.UP));

        StringSelection stringSelection = new StringSelection(bp.toBlueprintString());
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, stringSelection);

    }
}
