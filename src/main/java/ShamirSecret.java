import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.math.BigInteger;
import java.util.*;

public class ShamirSecret {

    public static void main(String[] args) throws Exception {
        // Read JSON file
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(new File("src/main/resources/input2.json"));

        int n = root.get("keys").get("n").asInt();
        int k = root.get("keys").get("k").asInt();

        List<Point> points = new ArrayList<>();

        Iterator<String> fieldNames = root.fieldNames();
        while (fieldNames.hasNext()) {
            String key = fieldNames.next();
            if (key.equals("keys")) continue;

            int x = Integer.parseInt(key);
            int base = Integer.parseInt(root.get(key).get("base").asText());
            String valueStr = root.get(key).get("value").asText();

            BigInteger y = new BigInteger(valueStr, base); // convert base → decimal
            points.add(new Point(x, y));
        }

        // Take first k points
        List<Point> subset = points.subList(0, k);

        BigInteger secret = lagrangeInterpolation(subset);
        System.out.println("Secret = " + secret);
    }

    static BigInteger lagrangeInterpolation(List<Point> points) {
        BigInteger result = BigInteger.ZERO;

        for (int i = 0; i < points.size(); i++) {
            BigInteger xi = BigInteger.valueOf(points.get(i).x);
            BigInteger yi = points.get(i).y;

            BigInteger numerator = BigInteger.ONE;
            BigInteger denominator = BigInteger.ONE;

            for (int j = 0; j < points.size(); j++) {
                if (i == j) continue;
                BigInteger xj = BigInteger.valueOf(points.get(j).x);

                numerator = numerator.multiply(xj.negate());   // multiply by -xj
                denominator = denominator.multiply(xi.subtract(xj));
            }

            BigInteger term = yi.multiply(numerator).divide(denominator);
            result = result.add(term);
        }
        return result;
    }

    static class Point {
        int x;
        BigInteger y;
        Point(int x, BigInteger y) {
            this.x = x;
            this.y = y;
        }
    }
}
