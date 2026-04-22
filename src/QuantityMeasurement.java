public class QuantityMeasurement{

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
                throw new IllegalArgumentException("Invalid value");
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

        // ✅ UC6 (implicit target = this.unit)
        public QuantityLength add(QuantityLength other) {
            return QuantityMeasurement.add(this, other, this.unit);
        }

        // ✅ UC7 (explicit target unit)
        public QuantityLength add(QuantityLength other, LengthUnit targetUnit) {
            return QuantityMeasurement.add(this, other, targetUnit);
        }

        @Override
        public String toString() {
            return "Quantity(" + value + ", " + unit + ")";
        }

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
    }

    // ✅ Conversion (UC5 reused)
    public static double convert(double value, LengthUnit source, LengthUnit target) {
        if (!Double.isFinite(value)) {
            throw new IllegalArgumentException("Invalid value");
        }
        if (source == null || target == null) {
            throw new IllegalArgumentException("Unit cannot be null");
        }

        double feet = source.toFeet(value);
        return target.fromFeet(feet);
    }

    // ✅ Private helper (DRY)
    private static double addInFeet(QuantityLength a, QuantityLength b) {
        double aFeet = a.getUnit().toFeet(a.getValue());
        double bFeet = b.getUnit().toFeet(b.getValue());
        return aFeet + bFeet;
    }

    // ✅ UC7 core method (explicit target)
    public static QuantityLength add(QuantityLength a,
                                     QuantityLength b,
                                     LengthUnit targetUnit) {

        if (a == null || b == null) {
            throw new IllegalArgumentException("Operands cannot be null");
        }
        if (targetUnit == null) {
            throw new IllegalArgumentException("Target unit cannot be null");
        }

        double sumFeet = addInFeet(a, b);
        double result = targetUnit.fromFeet(sumFeet);

        return new QuantityLength(result, targetUnit);
    }

    // ✅ Demo
    public static void main(String[] args) {

        QuantityLength a = new QuantityLength(1.0, LengthUnit.FEET);
        QuantityLength b = new QuantityLength(12.0, LengthUnit.INCH);

        System.out.println(add(a, b, LengthUnit.FEET));   // Quantity(2.0, FEET)
        System.out.println(add(a, b, LengthUnit.INCH));   // Quantity(24.0, INCH)
        System.out.println(add(a, b, LengthUnit.YARD));   // ~0.667 YARD

        QuantityLength yard = new QuantityLength(1.0, LengthUnit.YARD);
        QuantityLength feet = new QuantityLength(3.0, LengthUnit.FEET);

        System.out.println(add(yard, feet, LengthUnit.YARD)); // Quantity(2.0, YARD)
    }
}