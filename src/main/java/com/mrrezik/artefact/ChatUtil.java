package com.mrrezik.artefact.util;

import org.bukkit.ChatColor;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatUtil {

    // Паттерн для стандартного Spigot HEX: &#RRGGBB
    private static final Pattern HEX_SPIGOT_PATTERN = Pattern.compile("&#([0-9A-Fa-f]{6})");
    // Паттерн для пользовательского HEX: <#RRGGBB>
    private static final Pattern HEX_CUSTOM_PATTERN = Pattern.compile("<#([0-9A-Fa-f]{6})>");

    // Паттерн для стандартных & codes
    private static final Pattern COLOR_CODE_PATTERN = Pattern.compile("(?i)&([0-9A-FK-OR])");

    /**
     * Конвертирует все цветовые коды: &x, &#RRGGBB, <#RRGGBB>.
     *
     * @param text Текст для окрашивания
     * @return Окрашенный текст
     */
    public static String color(String text) {
        if (text == null || text.isEmpty()) {
            return "";
        }

        // 1. Обработка пользовательского формата <#RRGGBB>
        // Использование replaceAll с лямбда-функцией для обработки групп
        text = HEX_CUSTOM_PATTERN.matcher(text).replaceAll(match -> translateHexToLegacy(match.group(1)));

        // 2. Обработка стандартного Spigot HEX &#RRGGBB
        text = HEX_SPIGOT_PATTERN.matcher(text).replaceAll(match -> translateHexToLegacy(match.group(1)));

        // 3. Обработка стандартных & codes (конвертируем & в §)
        text = COLOR_CODE_PATTERN.matcher(text).replaceAll("§$1");

        return text;
    }

    /**
     * Конвертирует RRGGBB в последовательность §x§R§R§G§G§B§B для HEX-цветов (формат Spigot 1.16+).
     * @param hexCode RRGGBB (6 символов)
     * @return Последовательность кодов цветов
     */
    private static String translateHexToLegacy(String hexCode) {
        StringBuilder magic = new StringBuilder();
        // Начинаем с §x
        magic.append(ChatColor.COLOR_CHAR).append('x');
        for (char c : hexCode.toCharArray()) {
            // Добавляем § и следующий символ цвета (R, G или B)
            magic.append(ChatColor.COLOR_CHAR).append(c);
        }
        return magic.toString();
    }
}