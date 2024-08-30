package de.blazemcworld.fireflow.util;

import java.util.Arrays;
import java.util.List;

public class Levenshtein {

    public static List<String> calculateAndSmartSort(String message, String ...y) {
        return smartSort(message, calculateAndSort(message, y).toArray(String[]::new));
    }

    public static List<String> smartSort(String message, String ...y) {
        List<String> list = Arrays.stream(y).toList();
        list = list.stream()
                .sorted((a, b) -> {
                    boolean aStartsWithMsg = a.toLowerCase().startsWith(message.toLowerCase());
                    boolean bStartsWithMsg = b.toLowerCase().startsWith(message.toLowerCase());
                    boolean aContainsMsg = a.toLowerCase().contains(message.toLowerCase());
                    boolean bContainsMsg = b.toLowerCase().contains(message.toLowerCase());
                    if (aStartsWithMsg && !bStartsWithMsg) return -1;
                    if (!aStartsWithMsg && bStartsWithMsg) return 1;
                    if (aContainsMsg && !bContainsMsg) return -1;
                    if (!aContainsMsg && bContainsMsg) return 1;
                    return 0;
                })
                .toList();
        return list;
    }

    public static List<String> calculateAndSort(String x, String ...y) {
        return Arrays.stream(y).sorted((a, b) -> calculate(x, b) - calculate(x, a)).toList();
    }

    public static int calculate(String x, String y) {
        int[][] dp = new int[x.length() + 1][y.length() + 1];

        for (int i = 0; i <= x.length(); i++) {
            for (int j = 0; j <= y.length(); j++) {
                if (i == 0) {
                    dp[i][j] = j;
                }
                else if (j == 0) {
                    dp[i][j] = i;
                }
                else {
                    dp[i][j] = min(dp[i - 1][j - 1]
                                    + costOfSubstitution(x.charAt(i - 1), y.charAt(j - 1)),
                            dp[i - 1][j] + 1,
                            dp[i][j - 1] + 1);
                }
            }
        }

        return dp[x.length()][y.length()];
    }


    private static int costOfSubstitution(char a, char b) {
        return a == b ? 0 : 1;
    }

    private static int min(int... numbers) {
        return Arrays.stream(numbers)
                .min().orElse(Integer.MAX_VALUE);
    }
}
