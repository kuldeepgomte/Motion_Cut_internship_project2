import java.util.HashMap;
import java.util.Scanner;

public class Main {

    // HashMap to store the mapping between short URLs and long URLs
    private HashMap<String, String> urlMap;
    private static final String BASE_URL = "https://short.url/"; // Base URL for shortened links
    private static final int SHORT_URL_LENGTH = 6; // Length of the short URL identifier

    public Main() {
        urlMap = new HashMap<>();
    }

    // Method to generate a short URL
    private String generateShortUrl(String longUrl) {
        // Basic hash function: use the hash code of the long URL and convert it to a base62 string
        int hashCode = longUrl.hashCode();
        String shortUrl = base62Encode(Math.abs(hashCode));
        return BASE_URL + shortUrl.substring(0, Math.min(shortUrl.length(), SHORT_URL_LENGTH));
    }

    // Method to encode a number into base62 (0-9, a-z, A-Z)
    private String base62Encode(int value) {
        String characters = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        StringBuilder sb = new StringBuilder();
        while (value > 0) {
            sb.insert(0, characters.charAt(value % 62));
            value /= 62;
        }
        return sb.toString();
    }

    // Method to shorten a long URL
    public String shortenUrl(String longUrl) {
        // Check if the long URL is already shortened
        for (String shortUrl : urlMap.keySet()) {
            if (urlMap.get(shortUrl).equals(longUrl)) {
                return shortUrl; // Return existing short URL
            }
        }

        // Generate a new short URL
        String shortUrl = generateShortUrl(longUrl);

        // Handle collisions (if the short URL already exists)
        while (urlMap.containsKey(shortUrl)) {
            shortUrl = generateShortUrl(longUrl + System.currentTimeMillis()); // Append timestamp to make it unique
        }

        // Store the mapping
        urlMap.put(shortUrl, longUrl);
        return shortUrl;
    }

    // Method to expand a short URL to its original form
    public String expandUrl(String shortUrl) {
        if (urlMap.containsKey(shortUrl)) {
            return urlMap.get(shortUrl);
        } else {
            throw new IllegalArgumentException("Invalid short URL");
        }
    }

    // Command-line interface for user interaction
    public void startCLI() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("\n1. Shorten a URL");
            System.out.println("2. Expand a URL");
            System.out.println("3. Exit");
            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    System.out.print("Enter the long URL: ");
                    String longUrl = scanner.nextLine();
                    String shortUrl = shortenUrl(longUrl);
                    System.out.println("Shortened URL: " + shortUrl);
                    break;
                case 2:
                    System.out.print("Enter the short URL: ");
                    String inputShortUrl = scanner.nextLine();
                    try {
                        String originalUrl = expandUrl(inputShortUrl);
                        System.out.println("Original URL: " + originalUrl);
                    } catch (IllegalArgumentException e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                    break;
                case 3:
                    System.out.println("Exiting...");
                    scanner.close();
                    return;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    public static void main(String[] args) {
        Main Main = new Main();
        Main.startCLI();
    }
}
