import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

class Player {
    String name;
    String team;
    String position;
    int minutes;
    int goals;
    int assists;
    double xG;
    double xGPer90;

    public Player(String name, String team, String position, int minutes, int goals, int assists, double xG, double xGPer90) {
        this.name = name;
        this.team = team;
        this.position = position;
        this.minutes = minutes;
        this.goals = goals;
        this.assists = assists;
        this.xG = xG;
        this.xGPer90 = xGPer90;
    }

    @Override
    public String toString() {
        return "Player: " + name + ", Team: " + team +
                ", Position: " + position + ", Minutes: " + minutes +
                ", Goals: " + goals + ", Assists: " + assists +
                ", xG: " + xG + ", xG per 90: " + xGPer90;
    }
}
//Hello
public class FBrefPlayerScraper {
    public static void main(String[] args) {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        WebDriver driver = new ChromeDriver(options);
        List<Player> players = new ArrayList<>();

        try {
            driver.get("https://fbref.com/en/comps/9/stats/Premier-League-Stats#all_stats_standard");
            Thread.sleep(3000);
            String pageSource = driver.getPageSource();
            Document doc = Jsoup.parse(pageSource);
            Element statsDiv = doc.select("div#div_stats_standard").first();

            if (statsDiv != null) {
                System.out.println("Stats div found!");
                Elements playerRows = statsDiv.select("table#stats_standard tbody tr[data-row]");

                if (playerRows.isEmpty()) {
                    System.out.println("No player rows found.");
                } else {
                    for (Element row : playerRows) {
                        String playerName = row.select("td[data-stat=player] a").text();
                        String playerTeam = row.select("td[data-stat=team] a").text();
                        String playerPosition = row.select("td[data-stat=position]").text();
                        String playerMinutes = row.select("td[data-stat=minutes]").text();
                        String playerGoals = row.select("td[data-stat=goals]").text();
                        String playerAssists = row.select("td[data-stat=assists]").text();
                        String playerXG = row.select("td[data-stat=xg]").text();
                        String playerXGPer90 = row.select("td[data-stat=xg_per90]").text();

                        int minutes = parseInteger(playerMinutes);
                        int goals = parseInteger(playerGoals);
                        int assists = parseInteger(playerAssists);
                        double xG = parseDouble(playerXG);
                        double xGPer90 = parseDouble(playerXGPer90);

                        if (!playerName.isEmpty()) {
                            players.add(new Player(playerName, playerTeam, playerPosition, minutes, goals, assists, xG, xGPer90));
                        }
                    }
                }

                Scanner scanner = new Scanner(System.in);
                System.out.println("Sort players by: 1: Goals\n 2: xG\n 3: xG per 90\n 4: Assists");
                int choice = scanner.nextInt();

                switch (choice) {
                    case 1:
                        Collections.sort(players, Comparator.comparingInt((Player p) -> p.goals).reversed());
                        break;
                    case 2:
                        Collections.sort(players, Comparator.comparingDouble((Player p) -> p.xG).reversed());
                        break;
                    case 3:
                        Collections.sort(players, Comparator.comparingDouble((Player p) -> p.xGPer90).reversed());
                        break;
                    case 4:
                        Collections.sort(players, Comparator.comparingInt((Player p) -> p.assists).reversed());
                        break;
                    default:
                        System.out.println("Invalid choice. Displaying players without sorting.");
                }

                for (Player player : players) {
                    System.out.println(player);
                }

                scanner.close();
            } else {
                System.out.println("Stats div not found.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            driver.quit();
        }
    }

    private static int parseInteger(String str) {
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private static double parseDouble(String str) {
        try {
            return Double.parseDouble(str);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
}
