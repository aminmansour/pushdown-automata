package view;

import javafx.scene.paint.Color;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.util.Pair;

class Arrow extends Path {
    private static final double defaultArrowHeadSize = 5.0;

    public Arrow(VisualControlState q1, VisualControlState q2) {
        super();
        strokeProperty().bind(fillProperty());
        setFill(Color.web("#607D8B"));
        double startX = q1.getXPos() + q1.getWidth();
        double startY = q1.getYPos() + q1.getHeight();

        if (q1 == q2) {
            generateRoundTransition(startX, startY);
        } else {
            generateStandardTransition(q1, q2, startX, startY);
        }
    }

    private void generateStandardTransition(VisualControlState q1, VisualControlState q2, double startX, double startY) {
        Pair<Double, Double> centerVector = new Pair<>(q2.getXPos() + q2.getWidth(), q2.getYPos() + q2.getHeight());
        Pair<Double, Double> vector1 = new Pair<>(-centerVector.getKey() + q1.getXPos(), -centerVector.getValue() + q1.getYPos());
        Pair<Double, Double> vector2 = new Pair<>(1.0, 0.0);
        double dotProduct = vector1.getKey() * vector2.getKey() + vector1.getValue() * vector2.getValue();
        double bottomProduct = Math.sqrt(Math.pow(vector2.getKey(), 2) + Math.pow(vector2.getValue(), 2)) + Math.sqrt(Math.pow(vector1.getKey(), 2) + Math.pow(vector1.getValue(), 2));
        double cosineAngle = Math.acos(dotProduct / bottomProduct);

        if (q1.getYPos() < centerVector.getValue()) {
            cosineAngle = 2 * Math.PI - cosineAngle;
        }

        double xEndPoint = centerVector.getKey() + q2.getRadius() * Math.cos(cosineAngle);
        double yEndPoint = centerVector.getValue() + q2.getRadius() * Math.cos(cosineAngle);
        getElements().add(new MoveTo(startX, startY));
        getElements().add(new LineTo(xEndPoint, yEndPoint));
        generateArrowHead(startX, startY, xEndPoint, yEndPoint);
    }

    private void generateRoundTransition(double startX, double startY) {
        double yStartPoint = startY - (26.5 / 2);
        double xStartPoint = startX + 4;
        double midPointX = startX - 100;
        double midPointY = startY;
        double yEndPoint = startY + (26.5 / 2);
        double xEndPoint = startX;
        getElements().add(new MoveTo(xStartPoint, yStartPoint));
        getElements().add(new LineTo(midPointX, midPointY));
        getElements().add(new MoveTo(midPointX, midPointY));
        getElements().add(new LineTo(xEndPoint, yEndPoint));

        generateArrowHead(midPointX, midPointY, xEndPoint, yEndPoint);
    }

    private void generateArrowHead(double startX, double startY, double xEndPoint, double yEndPoint) {
        //ArrowHead
        double angle = Math.atan2((yEndPoint - startY), (xEndPoint - startX)) - Math.PI / 2.0;
        double sin = Math.sin(angle);
        double cos = Math.cos(angle);
        //point1
        double x1 = (-1.0 / 2.0 * cos + Math.sqrt(3) / 2 * sin) * defaultArrowHeadSize + xEndPoint;
        double y1 = (-1.0 / 2.0 * sin - Math.sqrt(3) / 2 * cos) * defaultArrowHeadSize + yEndPoint;
        //point2
        double x2 = (1.0 / 2.0 * cos + Math.sqrt(3) / 2 * sin) * defaultArrowHeadSize + xEndPoint;
        double y2 = (1.0 / 2.0 * sin - Math.sqrt(3) / 2 * cos) * defaultArrowHeadSize + yEndPoint;
        getElements().add(new LineTo(x1, y1));
        getElements().add(new LineTo(x2, y2));
        getElements().add(new LineTo(xEndPoint, yEndPoint));
    }


}