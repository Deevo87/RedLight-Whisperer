package org.redlightwhisperer.utils;

import javafx.geometry.Point2D;
import org.redlightwhisperer.enums.LaneType;

import java.util.List;
import java.util.Map;

public class LanePositions {
    public static final Map<LaneType, List<Point2D>> PATHS = Map.ofEntries(
            Map.entry(LaneType.NORTH_FORWARD, List.of(new Point2D(280, 746), new Point2D(327, 746))),
            Map.entry(LaneType.SOUTH_FORWARD, List.of(new Point2D(504,14), new Point2D(452, 14))),
            Map.entry(LaneType.WEST_FORWARD, List.of(new Point2D(22, 293))),
            Map.entry(LaneType.EAST_FORWARD, List.of(new Point2D(740, 478))),
            Map.entry(LaneType.NORTH_TURN_LEFT, List.of(new Point2D(381, 240), new Point2D(564, 466), new Point2D(740, 466))),
            Map.entry(LaneType.SOUTH_TURN_LEFT, List.of(new Point2D(403, 517), new Point2D(228, 291), new Point2D(22, 290))),
            Map.entry(LaneType.WEST_TURN_LEFT, List.of(new Point2D(543, 351), new Point2D(318, 527), new Point2D(318, 746))),
            Map.entry(LaneType.EAST_TURN_LEFT, List.of(new Point2D(215, 427), new Point2D(442, 240), new Point2D(442, 14))),
            Map.entry(LaneType.NORTH_TURN_RIGHT, List.of(new Point2D(228, 240), new Point2D(22, 240))),
            Map.entry(LaneType.SOUTH_TURN_RIGHT, List.of(new Point2D(554, 517), new Point2D(740, 517))),
            Map.entry(LaneType.EAST_TURN_RIGHT, List.of(new Point2D(215, 527), new Point2D(218, 746))),
            Map.entry(LaneType.WEST_TURN_RIGHT, List.of(new Point2D(543, 250), new Point2D(543, 14)))
    );

    public static final Map<LaneType, List<Point2D>> STARTING_POSITIONS = Map.ofEntries(
            Map.entry(LaneType.NORTH_FORWARD, List.of(new Point2D(327, 167), new Point2D(280, 167))),
            Map.entry(LaneType.NORTH_TURN_LEFT, List.of(new Point2D(381, 167))),
            Map.entry(LaneType.NORTH_TURN_RIGHT, List.of(new Point2D(229, 167))),
            Map.entry(LaneType.SOUTH_FORWARD, List.of(new Point2D(452, 589), new Point2D(504, 589))),
            Map.entry(LaneType.SOUTH_TURN_LEFT, List.of(new Point2D(404, 589))),
            Map.entry(LaneType.SOUTH_TURN_RIGHT, List.of(new Point2D(554, 589))),
            Map.entry(LaneType.EAST_FORWARD, List.of(new Point2D(144, 478))),
            Map.entry(LaneType.EAST_TURN_LEFT, List.of(new Point2D(144, 427))),
            Map.entry(LaneType.EAST_TURN_RIGHT, List.of(new Point2D(144, 527))),
            Map.entry(LaneType.WEST_FORWARD, List.of(new Point2D(621, 300))),
            Map.entry(LaneType.WEST_TURN_LEFT, List.of(new Point2D(621, 351))),
            Map.entry(LaneType.WEST_TURN_RIGHT, List.of(new Point2D(621, 250)))
    );
}
