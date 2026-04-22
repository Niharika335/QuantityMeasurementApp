public class QuantityMeasurement {

    // Base unit = FEET
    enum LengthUnit {
        FEET(1.0),
        INCH(1.0 / 12.0),
        YARD(3.0),
        CENTIMETER(0.0328084);

        private final double toFeetFactor;

        LengthUnit(double toFeetFactor) {
            this.toFeetFactor = toFeetFactor;
        }

        public double toFeet(double value) {
            return value * toFeetFactor;
        }

        public double fromFeet(double feetValue) {
            return feetValue / toFeetFactor;
        }
    }

    static class QuantityLength {
        private final double value;
        private final LengthUnit unit;

        public QuantityLength(double value, LengthUnit unit) {
            if (!Double.isFinite(value)) {
                throw new IllegalArgumentException("Invalid numeric value");
            }
            if (unit == null) {
                throw new IllegalArgumentException("Unit cannot be null");
            }
            this.value = value;
            this.unit = unit;
        }

        public double getValue() {
            return value;
        }

        public LengthUnit getUnit() {
            return unit;
        }

        // Convert to another unit (instance method)
        public QuantityLength convertTo(LengthUnit targetUnit) {
            double convertedValue = convert(this.value, this.unit, targetUnit);
            return new QuantityLength(convertedValue, targetUnit);
        }

        // Equality based on base unit
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;

            QuantityLength other = (QuantityLength) obj;

            return Double.compare(
                    unit.toFeet(value),
                    other.unit.toFeet(other.value)
            ) == 0;
        }

        @Override
        public String toString() {
            return "Quantity(" + value + ", " + unit + ")";
        }
    }

    // ✅ Static Conversion API (CORE of UC5)
    public static double convert(double value, LengthUnit source, LengthUnit target) {

        // Validation
        if (!Double.isFinite(value)) {
            throw new IllegalArgumentException("Value must be finite");
        }
        if (source == null || target == null) {
            throw new IllegalArgumentException("Units cannot be null");
        }

        // Convert → base unit (feet)
        double inFeet = source.toFeet(value);

        // Convert → target unit
        return target.fromFeet(inFeet);
    }

    // Overloaded method (demonstration)
    public static void demonstrateLengthConversion(double value,
                                                   LengthUnit from,
                                                   LengthUnit to) {
        double result = convert(value, from, to);
        System.out.println("Input: convert(" + value + ", " + from + ", " + to + ")");
        System.out.println("Output: " + result);
    }

    public static void demonstrateLengthConversion(QuantityLength q,
                                                   LengthUnit target) {
        QuantityLength converted = q.convertTo(target);
        System.out.println(q + " -> " + converted);
    }

    public static void main(String[] args) {

        demonstrateLengthConversion(1.0, LengthUnit.FEET, LengthUnit.INCH);
        demonstrateLengthConversion(3.0, LengthUnit.YARD, LengthUnit.FEET);
        demonstrateLengthConversion(36.0, LengthUnit.INCH, LengthUnit.YARD);
        demonstrateLengthConversion(1.0, LengthUnit.CENTIMETER, LengthUnit.INCH);

        QuantityLength q = new QuantityLength(2.0, LengthUnit.YARD);
        demonstrateLengthConversion(q, LengthUnit.INCH);
    }
}