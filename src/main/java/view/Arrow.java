package view;

import javafx.scene.paint.Color;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.util.Pair;

class Arrow extends Path {
    private static final double defaultArrowHeadSize = 5.0;
    private boolean isRecursiveArrow;
    private double startY;
    private double startX;
    private double endY;
    private double endX;
    private double midPointX;
    private double midPointY;

    public Arrow(VisualControlState q1, VisualControlState q2) {
        super();
        strokeProperty().bind(fillProperty());
        setFill(Color.web("#607D8B"));


        if (q1 == q2) {
            isRecursiveArrow = true;
            boolean isShownOnLeftSide = q1.getOrderShown() % 2 == 0;
            startX = q1.getXPos() + (isShownOnLeftSide ? (q1.isInitial() ? 11 : 0) : q1.getWRadius() + 26.5);
            startY = q1.getYPos() + q1.getHRadius();
            generateRoundTransition(startX, startY, isShownOnLeftSide);
        } else {
            double differenceInX = Math.abs(q2.getXPos() + q2.getWRadius()) - (q1.getXPos() + q1.getWRadius());
            double w = Math.max(differenceInX, -differenceInX);
            double differenceInY = (q2.getYPos() - q2.getHRadius()) - (q1.getYPos() - q1.getHRadius());
            double h = Math.max(differenceInY, -differenceInY);
            double lengthOfArrow = Math.sqrt(Math.pow(w, 2) + Math.pow(h, 2));
            int quadrant;
            if (q1.getXPos() <= q2.getXPos()) {
                quadrant = q1.getYPos() > q2.getYPos() ? 3 : 2;
            } else {
                quadrant = q1.getYPos() > q2.getYPos() ? 4 : 1;
            }

            if (quadrant == 1 || quadrant == 3) {
                double angle1 = Math.toDegrees(Math.acos(h / lengthOfArrow));
                double angle0 = 90 - angle1;
                if (angle1 <= angle0) {
                    if (quadrant == 1) {
                        endX = q2.getXPos() + q2.getWRadius();
                        endY = q2.getYPos();
                    } else {
                        endX = q2.getXPos() + q2.getWRadius();
                        endY = q2.getYPos() + 2 * q2.getHRadius();
                    }
                } else {
                    if (quadrant == 1) {
                        endX = q2.getXPos() + q2.getWRadius() + 26.5;
                        endY = q2.getYPos() + q2.getHRadius();
                    } else {
                        endX = q2.getXPos() + q2.getWRadius();
                        endY = q2.getYPos() + 2 * q2.getHRadius();
                    }
                }
            } else if (quadrant == 2 || quadrant == 4) {
                double angle1 = Math.toDegrees(Math.acos(w / lengthOfArrow));
                double angle0 = 90 - angle1;
                if (angle1 <= angle0) {
                    if (quadrant == 2) {
                        endX = q2.getXPos();
                        endY = q2.getYPos() + q2.getHRadius();
                    } else {
                        endX = q2.getXPos() + q2.getWRadius() + 26.5;
                        endY = q2.getYPos() + q2.getHRadius();
                    }
                } else {
                    if (quadrant == 2) {
                        endX = q2.getXPos() + q2.getWRadius();
                        endY = q2.getYPos();
                    } else {
                        endX = q2.getXPos() + q2.getWRadius();
                        endY = q2.getYPos() + q2.getHRadius() + 26.5;
                    }
                }
            }
            generateStandardTransition(q1);
        }
    }

    private void generateStandardTransition(VisualControlState q1) {
        startY = q1.getYPos() + q1.getHRadius();
        startX = q1.getXPos() + q1.getWRadius();
        getElements().add(new MoveTo(startX, startY));
        getElements().add(new LineTo(endX, endY));
        generateArrowHead(startX, q1.getYPos() + 23.5, endX, endY);
    }

    private void generateRoundTransition(double startXPos, double startYPos, boolean leftColumnSource) {
        double yEndPoint, xEndPoint;

        if (leftColumnSource) {
            startY = startYPos - (26.5 / 2);
            startX = startXPos + 4;
            midPointX = startXPos - 100;
            midPointY = startYPos;
            yEndPoint = startYPos + (26.5 / 2);
            xEndPoint = startXPos + 4;
        } else {
            startY = startYPos + (26.5 / 2);
            startX = startXPos - 4;
            midPointX = startXPos + 100;
            midPointY = startYPos;
            yEndPoint = startYPos - (26.5 / 2);
            xEndPoint = startXPos - 4;
        }
        getElements().add(new MoveTo(startX, startY));
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

    public Pair<Double, Double> getStartCoordinate() {
        return new Pair<>(startX, startY);
    }

    public Pair<Double, Double> getEndCoordinate() {
        return new Pair<>(endX, endY);
    }

    public Pair<Double, Double> getMidCoordinate() {
        if (isRecursiveArrow) {
            return new Pair<>(midPointX, midPointY);
        }
        return new Pair<>((endX + startX) / 2, (endY + startY) / 2);

    }




}