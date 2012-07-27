package org.phw.eop.domain;

/**
 * 起始，结束范围类。
 * @author BingooHuang
 *
 */
public final class Range implements java.io.Serializable {

    private static final long serialVersionUID = 1190587321928670469L;

    /**
     * 范围值类型.
     * @author BingooHuang
     *
     */
    public static enum RangeType {
        /**
         * <ul>
         * <li>ONLY_START 只有开始值.</li>
         * <li>ONLY_END   只有结束值.</li>
         * <li>START_END  开始和结束都有值.</li>
         * <li>SAME_SAME  开始和结束都有相同值.</li>
         * </ul>
         */
        ONLY_START, ONLY_END, START_END, SINGLE_VALUE
    }

    private String expr;
    private RangeType rangeType;
    private Object start;
    private Object end;

    /**
     * 似有构造函数。
     */
    private Range() {

    }

    /**
     * 构造只有起始值的范围。
     * @param expr 范围表达式
     * @param start 起始值
     * @return 范围
     */
    public static Range createOnlyStartRange(String expr, Object start) {
        Range range = new Range();
        range.setExpr(expr);
        range.setRangeType(RangeType.ONLY_START);
        range.setStart(start);

        return range;
    }

    /**
     * 构造只有结束值的范围。
     * @param expr 范围表达式
     * @param end 结束值
     * @return 范围
     */
    public static Range createOnlyEndRange(String expr, Object end) {
        Range range = new Range();
        range.setExpr(expr);
        range.setRangeType(RangeType.ONLY_END);
        range.setEnd(end);

        return range;
    }

    /**
     * 构造有起始和结束值的范围。
     * @param expr 范围表达式
     * @param start 起始值
     * @param end 结束值
     * @return 范围
     */
    public static Range createStartEndRange(String expr, Object start, Object end) {
        if (start instanceof Comparable && ((Comparable) start).compareTo(end) > 0) {
            throw new RuntimeException(expr + "范围表达式非法, 起始值大于结束值");
        }

        Range range = new Range();
        range.setExpr(expr);
        range.setRangeType(RangeType.START_END);
        range.setStart(start);
        range.setEnd(end);

        return range;
    }

    /**
     * 构造起始值和结束值相等的范围。
     * @param expr 范围表达式
     * @param value 值
     * @return 范围
     */
    public static Range createSingleValueRange(String expr, Object value) {
        Range range = new Range();
        range.setExpr(expr);
        range.setRangeType(RangeType.SINGLE_VALUE);
        range.setStart(value);

        return range;
    }

    /**
     * 判断值是否在范围数组之内。
     * @param ranges 范围数组
     * @param value 值
     * @return true 在范围数组定义的范围之内, 其余为false。
     */
    public static boolean inRange(Range[] ranges, Object value) {
        if (value == null) {
            return false;
        }

        // 没有定义范围，返回真
        if (ranges == null || ranges.length == 0) {
            return true;
        }

        for (Range range : ranges) {
            if (range.inRange(value)) {
                return true;
            }
        }

        return false;
    }

    /**
     * 判断值是否在范围之内。
     * @param value 值
     * @return true 在当前范围之内， false不在范围之内。
     */
    public boolean inRange(Object value) {
        if (value == null) {
            return false;
        }

        boolean ret = false;
        switch (rangeType) {
        case SINGLE_VALUE:
            ret = start.equals(value);
            break;
        case ONLY_START:
            ret = start instanceof Comparable && ((Comparable) start).compareTo(value) <= 0;
            break;
        case ONLY_END:
            ret = end instanceof Comparable && ((Comparable) end).compareTo(value) >= 0;
            break;
        case START_END:
            ret = start instanceof Comparable && ((Comparable) start).compareTo(value) <= 0
                    && end instanceof Comparable && ((Comparable) end).compareTo(value) >= 0;
            break;
        default:
            break;
        }

        return ret;
    }

    public Object getStart() {
        return start;
    }

    private void setStart(Object start) {
        this.start = start;
    }

    public Object getEnd() {
        return end;
    }

    private void setEnd(Object end) {
        this.end = end;
    }

    private void setRangeType(RangeType rangeType) {
        this.rangeType = rangeType;
    }

    public RangeType getRangeType() {
        return rangeType;
    }

    public void setExpr(String expr) {
        this.expr = expr;
    }

    public String getExpr() {
        return expr;
    }

    @Override
    public String toString() {
        return "Range [expr=" + expr + ", rangeType=" + rangeType + ", start=" + start + ", end="
                + end + "]";
    }
}
