import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {
    public static void main(String[] args) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            String line = reader.readLine();
            Parser parser = new Parser();
            Tree t = parser.parse(line);
            Visualizer.save(t, "output.png");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            System.err.println("Parse Error: " + e.getMessage());
        }
    }
}
