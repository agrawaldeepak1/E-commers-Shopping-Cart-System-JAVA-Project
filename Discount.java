package ECommerce;

public class Discount {
    private String code;
    private double percentage;
    private String applicableProduct; // Null if applicable to total

    public Discount(String code, double percentage, String applicableProduct) {
        this.code = code;
        this.percentage = percentage;
        this.applicableProduct = applicableProduct;
    }

    public String getCode() {
        return code;
    }

    public double getPercentage() {
        return percentage;
    }

    public String getApplicableProduct() {
        return applicableProduct;
    }

    public double applyDiscount(double total, Product product) {
        if (applicableProduct == null || applicableProduct.equalsIgnoreCase(product.getName())) {
            return total * (1 - percentage / 100);
        }
        return total;
    }
}
