package itti.com.pl.arena.cm.dto;

import itti.com.pl.arena.cm.utils.helper.StringHelper;

public enum RelativePosition {

    Front("front"), LeftFront("left front"), RightFront("right front"), Back("back"), LeftBack("left back"), RightBack(
            "right back"), Left("left"), Rigth("right")

    ;

    private String position;

    private RelativePosition(String position) {
        this.position = position;
    }

    public String getPosition() {
        return position;
    }

    public static RelativePosition getPostion(String position) {

        if (position == null) {
            return null;
        }
        for (RelativePosition positionEnum : RelativePosition.values()) {
            if (StringHelper.equalsIgnoreCase(positionEnum.getPosition(), position)) {
                return positionEnum;
            }
        }
        return null;
    }
}
